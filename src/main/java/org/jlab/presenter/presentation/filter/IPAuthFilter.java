package org.jlab.presenter.presentation.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * A WebFilter that prompts users to authenticate if offsite to view content, but not if onsite or on localhost.
 *
 * @author ryans
 */
@WebFilter(filterName = "IPAuthFilter", urlPatterns = {"/*"}, dispatcherTypes = {DispatcherType.REQUEST, DispatcherType.FORWARD}, asyncSupported = false)
public class IPAuthFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(
            IPAuthFilter.class.getName());


    public static final String[] IGNORE_PATTERNS = new String[]{
            "/sso", // declarative security access point
            "/WEB-INF", // forward to jsp views
            "/error",
            "/resources",
            "/keep-alive",
            "/logout",
            "/pd-menu" // welcome page
    };

    public static final String LOCAL_PATTERN_STRING = "127\\.0\\.0\\.1";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest)request;
        HttpServletResponse httpResponse = (HttpServletResponse)response;

        String path = httpRequest.getServletPath();

        LOGGER.log(Level.FINEST, "Servlet Path: {0}", path);

        boolean ignoreMatch = false;

        // Ignore certain paths
        for(String s: IGNORE_PATTERNS) {
            if(path.startsWith(s)) {
                ignoreMatch = true;
                break;
            }
        }


        if (ignoreMatch) {
            LOGGER.log(Level.FINEST, "Ignoring path: {0}", path);
            chain.doFilter(request, response);
        } else {
            boolean redirect = false;

            String patternStr = System.getenv("ONSITE_WHITELIST_PATTERN");
            boolean whitelistlocal = "true".equals(System.getenv("ONSITE_WHITELIST_LOCAL"));
            String username = httpRequest.getRemoteUser();

            LOGGER.log(Level.FINEST, "Username: {0}, ONSITE_WHITELIST_PATTERN: {1}, ONSITE_WHITELIST_LOCAL: {2}", new Object[] {username, patternStr, whitelistlocal});

            // If not authenticated and whitelist pattern configured
            if(username == null && patternStr != null) {
                LOGGER.log(Level.FINEST, "Not authenticated and whitelist pattern is configured");
                String ip = request.getRemoteAddr();

                String xForwardedFor = httpRequest.getHeader("X-Forwarded-For");

                if(xForwardedFor != null && !xForwardedFor.isEmpty()) {
                    String[] ipArray = xForwardedFor.split(",");
                    ip = ipArray[0].trim(); // first one, if more than one
                }

                // If user is not coming from localhost or onsite whitelist IP, then redirect
                if(whitelistlocal && inWhitelist(LOCAL_PATTERN_STRING, ip)) {
                    LOGGER.log(Level.FINEST, "localhost ip, so ignoring");
                } else if (!inWhitelist(patternStr, ip)) {
                    LOGGER.log(Level.FINEST, "Need to redirect to login");
                    redirect = true;
                }
            }

            if(redirect) {
                String absHostUrl = System.getenv("FRONTEND_SERVER_URL");
                String contextPath = httpRequest.getContextPath();
                String requestUri = httpRequest.getRequestURI();
                String queryString = httpRequest.getQueryString();

                String domainRelativeReturnUrl = absHostUrl + requestUri;

                if(queryString != null) {
                    domainRelativeReturnUrl = domainRelativeReturnUrl + "?" + queryString;
                }

                LOGGER.log(Level.FINEST, "contextPath: {0}", contextPath);
                LOGGER.log(Level.FINEST, "requestUri: {0}", requestUri);
                LOGGER.log(Level.FINEST, "queryString: {0}", queryString);

                String redirectUrl = absHostUrl + contextPath + "/sso?returnUrl=" + URLEncoder.encode(domainRelativeReturnUrl, StandardCharsets.UTF_8);
                LOGGER.log(Level.FINEST, "redirectUrl: {0}", redirectUrl);
                httpResponse.sendRedirect(redirectUrl);
            } else {
                chain.doFilter(request, response);
            }
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    public boolean inWhitelist(String patternStr, String ip) {
        Pattern pattern = Pattern.compile(patternStr);

        boolean matches = pattern.matcher(ip).matches();

        LOGGER.log(Level.FINEST, "pattern = {0}, ip = {1}, match = {2}", new Object[]{patternStr, ip, matches});

        return matches;
    }
}

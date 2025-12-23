package org.jlab.presenter.presentation.filter;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.jsp.jstl.core.Config;
import java.io.IOException;
import java.util.Locale;

/**
 * @author ryans
 */
@WebFilter(
    filterName = "LocaleFilter",
    urlPatterns = {"/*"},
    dispatcherTypes = {DispatcherType.REQUEST})
public class LocaleFilter implements Filter {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    // If a request does not contain the header "Accept-Language: en-US" then we see issues with
    // date formatting.
    // For example the jstl:formatDate tag requires a locale or will simply use Object.toString().
    // Also, most of the time our apps don't care about client locale - we always assume en-US as
    // our layouts often aren't made to have dynamic date formats.
    Config.set(request, Config.FMT_LOCALE, new Locale("en", "US"));
    chain.doFilter(request, response);
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {}

  @Override
  public void destroy() {}
}

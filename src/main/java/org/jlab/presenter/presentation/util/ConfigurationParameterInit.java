package org.jlab.presenter.presentation.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ConfigurationParameterInit implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(
            ConfigurationParameterInit.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        String cdnHostname = System.getenv("CDN_SERVER");
        if (cdnHostname == null || cdnHostname.trim().isEmpty()) {
            // Let's use a sensible default
            cdnHostname = "cdn.acc.jlab.org";
        }

        context.setInitParameter("cdnContextPath", "//" + cdnHostname);
        LOGGER.log(Level.FINEST, "Setting CDN_SERVER: {0}", cdnHostname);

        String proxyServer = System.getenv("PROXY_SERVER");

        // If null or empty set to sensible (production) default
        if (proxyServer == null || proxyServer.trim().isEmpty()) {
            proxyServer = "accweb.acc.jlab.org";
        }

        context.setInitParameter("PROXY_SERVER", proxyServer);
        LOGGER.log(Level.FINEST, "Setting PROXY_SERVER: {0}", proxyServer);

        String notification = System.getenv("SERVER_MESSAGE");

        // If not null, but empty set to null
        if (notification != null && notification.trim().isEmpty()) {
            notification = null;
        }

        if (notification != null) {
            context.setInitParameter("notification", notification);
        }
        LOGGER.log(Level.FINEST, "Setting notification: {0}", notification);

        // We will just expose all environment variables!
        context.setAttribute("env", System.getenv());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Nothing to do
    }

}

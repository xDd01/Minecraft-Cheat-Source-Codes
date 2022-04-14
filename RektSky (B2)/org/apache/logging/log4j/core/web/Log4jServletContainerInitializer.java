package org.apache.logging.log4j.core.web;

import java.util.*;
import javax.servlet.*;

public class Log4jServletContainerInitializer implements ServletContainerInitializer
{
    public void onStartup(final Set<Class<?>> classes, final ServletContext servletContext) throws ServletException {
        if (servletContext.getMajorVersion() > 2) {
            servletContext.log("Log4jServletContainerInitializer starting up Log4j in Servlet 3.0+ environment.");
            final Log4jWebInitializer initializer = Log4jWebInitializerImpl.getLog4jWebInitializer(servletContext);
            initializer.initialize();
            initializer.setLoggerContext();
            servletContext.addListener((EventListener)new Log4jServletContextListener());
            final FilterRegistration.Dynamic filter = servletContext.addFilter("log4jServletFilter", (Filter)new Log4jServletFilter());
            if (filter == null) {
                throw new UnavailableException("In a Servlet 3.0+ application, you must not define a log4jServletFilter in web.xml. Log4j 2 defines this for you automatically.");
            }
            filter.addMappingForUrlPatterns((EnumSet)EnumSet.allOf(DispatcherType.class), false, new String[] { "/*" });
        }
    }
}

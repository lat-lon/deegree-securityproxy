package org.deegree.securityproxy.filter;

import static java.io.File.separator;
import static java.lang.System.getenv;
import static javax.servlet.http.HttpServletResponse.SC_OK;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.deegree.securityproxy.logger.Log4JReportLogger;
import org.deegree.securityproxy.logger.ProxyReportLogger;
import org.deegree.securityproxy.report.ProxyReport;

/**
 * Servlet Filter that logs all incoming requests and their response
 * 
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
public class LoggingFilter implements Filter {

    private final Logger log = Logger.getLogger( LoggingFilter.class );

    private static final String PROXY_CONFIG_ENV = "proxyConfigEnv";

    private static final String LOG4J_FILENAME = "log4j.properties";

    private ProxyReportLogger logger;

    @Override
    public void init( FilterConfig filterConfig )
                            throws ServletException {
        configureLogging( filterConfig );
        logger = new Log4JReportLogger();
    }

    @Override
    public void doFilter( ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain )
                            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        StatusExposingServletResponse wrappedResponse = new StatusExposingServletResponse( httpResponse );
        chain.doFilter( httpRequest, wrappedResponse );
        generateAndLogProxyReport( httpRequest, wrappedResponse );
    }

    @Override
    public void destroy() {
    }

    protected void setReportLogger( ProxyReportLogger logger ) {
        this.logger = logger;
    }

    private void configureLogging( FilterConfig filterConfig ) {
        String log4jConfigurationPath = buildLog4JConfigurationPath( filterConfig );
        if ( log4jConfigurationPath != null ) {
            PropertyConfigurator.configure( log4jConfigurationPath );
        } else {
            log.warn( "Could not retrieve log4j.properties from configuration directory. Please set the value of PROXY_CONFIG environment variable and place the log4j.properties in it." );
        }
    }

    private String buildLog4JConfigurationPath( FilterConfig filterConfig ) {
        String path = getenv( filterConfig.getInitParameter( PROXY_CONFIG_ENV ) );
        if ( path != null ) {
            StringBuilder builder = new StringBuilder( path );
            if ( !path.endsWith( separator ) )
                builder.append( separator );
            return builder.append( LOG4J_FILENAME ).toString();
        }
        return null;
    }

    private void generateAndLogProxyReport( HttpServletRequest request, StatusExposingServletResponse response ) {
        
        boolean isRequestSuccessful = SC_OK == response.getStatus() ? true : false;
        String targetURI = request.getRequestURL().toString();
        String queryString = request.getQueryString();
        String requestURL = queryString != null ? targetURI + "?" + queryString : targetURI;
        ProxyReport report = new ProxyReport( request.getRemoteAddr(), requestURL, isRequestSuccessful );
        logger.logProxyReportInfo( report );
    }

}

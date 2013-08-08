package org.deegree.securityproxy.filter;

import static javax.servlet.http.HttpServletResponse.SC_OK;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

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
    
    private ProxyReportLogger logger;

    @Override
    public void init( FilterConfig filterConfig )
                            throws ServletException {
        logger = new Log4JReportLogger();
    }

    @Override
    public void doFilter( ServletRequest request, ServletResponse res, FilterChain chain )
                            throws IOException, ServletException {
        StatusExposingServletResponse response = new StatusExposingServletResponse( (HttpServletResponse) res );
        chain.doFilter( request, response );
        generateReport( request, response );
    }

    @Override
    public void destroy() {
    }

    protected void setReportLogger (ProxyReportLogger logger) {
        this.logger = logger;
    }
    
    private void generateReport( ServletRequest request, StatusExposingServletResponse response ) {
        boolean isRequestSuccessful = SC_OK == response.getStatus() ? true : false;
        ProxyReport report = new ProxyReport( request.getRemoteAddr(), request.getServerName(), isRequestSuccessful );
        logger.logProxyReportInfo( report );
    }

}

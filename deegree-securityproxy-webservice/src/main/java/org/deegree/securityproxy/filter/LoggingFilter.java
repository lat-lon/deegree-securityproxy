package org.deegree.securityproxy.filter;

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

import org.deegree.securityproxy.logger.ProxyReportLogger;
import org.deegree.securityproxy.report.ProxyReport;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Servlet Filter that logs all incoming requests and their response
 * 
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author <a href="goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
public class LoggingFilter implements Filter {

    @Autowired
    private ProxyReportLogger proxyReportLogger;

    @Override
    public void init( FilterConfig filterConfig )
                            throws ServletException {
    }
    
    @Override
    public void doFilter( ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain )
                            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        FilterResponseWrapper wrappedResponse = new FilterResponseWrapper( httpResponse );
        chain.doFilter( httpRequest, wrappedResponse );
        generateAndLogProxyReport( httpRequest, wrappedResponse );
    }

    @Override
    public void destroy() {
    }

    private void generateAndLogProxyReport( HttpServletRequest request, FilterResponseWrapper response ) {
        boolean isRequestSuccessful = SC_OK == response.getStatus() ? true : false;
        String targetURI = request.getRequestURL().toString();
        String queryString = request.getQueryString();
        String requestURL = queryString != null ? targetURI + "?" + queryString : targetURI;
        ProxyReport report = new ProxyReport( request.getRemoteAddr(), requestURL, isRequestSuccessful );
        proxyReportLogger.logProxyReportInfo( report );
    }

}

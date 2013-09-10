package org.deegree.securityproxy.filter;

import static javax.servlet.http.HttpServletResponse.SC_OK;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.deegree.securityproxy.authorization.wcs.WcsRequestAuthorizationManager;
import org.deegree.securityproxy.logger.SecurityRequestResposeLogger;
import org.deegree.securityproxy.report.SecurityReport;
import org.deegree.securityproxy.request.UnsupportedRequestTypeException;
import org.deegree.securityproxy.request.WcsRequest;
import org.deegree.securityproxy.request.WcsRequestParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

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
    private WcsRequestAuthorizationManager requestAuthorizationManager;

    @Autowired
    private WcsRequestParser parser;

    @Autowired
    private SecurityRequestResposeLogger proxyReportLogger;

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
        createUuidHeader( wrappedResponse );
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
        SecurityReport report = new SecurityReport( request.getRemoteAddr(), requestURL, isRequestSuccessful, "" );
        proxyReportLogger.logProxyReportInfo( report );
    }

    private void createUuidHeader( FilterResponseWrapper wrappedResponse ) {
        String uuid = UUID.randomUUID().toString();
        wrappedResponse.addHeader( "serial_uuid", uuid );
    }

}

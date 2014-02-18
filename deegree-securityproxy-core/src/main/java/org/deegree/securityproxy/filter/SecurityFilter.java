package org.deegree.securityproxy.filter;

import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

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

import org.apache.log4j.Logger;
import org.deegree.securityproxy.authorization.logging.AuthorizationReport;
import org.deegree.securityproxy.authorization.wcs.RequestAuthorizationManager;
import org.deegree.securityproxy.logger.ResponseFilterReportLogger;
import org.deegree.securityproxy.logger.SecurityRequestResposeLogger;
import org.deegree.securityproxy.report.SecurityReport;
import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.request.UnsupportedRequestTypeException;
import org.deegree.securityproxy.request.WcsRequestParser;
import org.deegree.securityproxy.responsefilter.ResponseFilterManager;
import org.deegree.securityproxy.responsefilter.logging.ResponseFilterReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

/**
 * Servlet Filter that logs all incoming requests and their response and performs access decision.
 * 
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author <a href="goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
public class SecurityFilter implements Filter {

    private final static Logger LOG = Logger.getLogger( SecurityFilter.class );

    private static final String UNSUPPORTED_REQUEST_ERROR_MSG = "Could not parse request.";

    static final String REQUEST_ATTRIBUTE_SERVICE_URL = "net.sf.j2ep.serviceurl";

    @Autowired
    private RequestAuthorizationManager requestAuthorizationManager;

    @Autowired
    private WcsRequestParser parser;

    @Autowired
    private SecurityRequestResposeLogger proxyReportLogger;

    @Autowired
    private ResponseFilterReportLogger filterReportLogger;

    @Autowired
    private ResponseFilterManager filterManager;

    @Override
    public void init( FilterConfig filterConfig )
                            throws ServletException {
    }

    @Override
    public void doFilter( ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain )
                            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        StatusCodeResponseBodyWrapper wrappedResponse = new StatusCodeResponseBodyWrapper( httpResponse );
        String uuid = createUuidHeader( wrappedResponse );
        AuthorizationReport authorizationReport;
        Authentication authentication = getContext().getAuthentication();
        OwsRequest owsRequest = null;
        try {
            owsRequest = parser.parse( httpRequest );
            authorizationReport = requestAuthorizationManager.decide( authentication, owsRequest );
        } catch ( UnsupportedRequestTypeException e ) {
            authorizationReport = new AuthorizationReport( UNSUPPORTED_REQUEST_ERROR_MSG );
        } catch ( IllegalArgumentException e ) {
            authorizationReport = new AuthorizationReport( e.getMessage() );
        }
        if ( authorizationReport.isAuthorized() ) {
            attachServiceUrlAttributeToRequest( httpRequest, authorizationReport );
            chain.doFilter( httpRequest, wrappedResponse );
            if ( filterManager.supports( owsRequest.getClass() ) ) {
                ResponseFilterReport filterResponse = filterManager.filterResponse( wrappedResponse, owsRequest,
                                                                                    authentication );
                filterReportLogger.logResponseFilterReport( filterResponse, uuid );
                LOG.debug( "Filter was applied. Response: " + filterResponse.getMessage() );
            } else {
                LOG.debug( "No filter configured for " + owsRequest.getClass() );
                wrappedResponse.copyBufferedStreamToRealStream();
            }
        }
        handleAuthorizationReport( uuid, httpRequest, wrappedResponse, authorizationReport );
    }

    @Override
    public void destroy() {
    }

    /**
     * For testing purposes only. Set authorization manager manually.
     * 
     * @param requestAuthorizationManager
     */
    protected void setRequestAuthorizationManager( RequestAuthorizationManager requestAuthorizationManager ) {
        this.requestAuthorizationManager = requestAuthorizationManager;
    }

    private void handleAuthorizationReport( String uuid, HttpServletRequest httpRequest,
                                            StatusCodeResponseBodyWrapper wrappedResponse,
                                            AuthorizationReport authorizationReport ) {
        generateAndLogProxyReport( authorizationReport, uuid, httpRequest, wrappedResponse );
        if ( !authorizationReport.isAuthorized() ) {
            throw new AccessDeniedException( authorizationReport.getMessage() );
        }
    }

    private void generateAndLogProxyReport( AuthorizationReport authorizationReport, String uuid,
                                            HttpServletRequest request, StatusCodeResponseBodyWrapper response ) {

        String message = "";
        if ( authorizationReport.getMessage() != null ) {
            message = authorizationReport.getMessage();
        }
        generateAndLogProxyReport( message, uuid, request, response );
    }

    private void generateAndLogProxyReport( String message, String uuid, HttpServletRequest request,
                                            StatusCodeResponseBodyWrapper response ) {
        int statusCode = response.getStatus();
        boolean isRequestSuccessful = SC_OK == statusCode ? true : false;
        String targetURI = request.getRequestURL().toString();
        String queryString = request.getQueryString();
        String requestURL = queryString != null ? targetURI + "?" + queryString : targetURI;
        SecurityReport report = new SecurityReport( request.getRemoteAddr(), requestURL, isRequestSuccessful, message );
        proxyReportLogger.logProxyReportInfo( report, uuid );
    }

    private String createUuidHeader( StatusCodeResponseBodyWrapper wrappedResponse ) {
        String uuid = UUID.randomUUID().toString();
        wrappedResponse.addHeader( "serial_uuid", uuid );
        return uuid;
    }

    private void attachServiceUrlAttributeToRequest( HttpServletRequest httpRequest,
                                                     AuthorizationReport authorizationReport ) {
        String serviceUrl = authorizationReport.getServiceUrl();
        httpRequest.setAttribute( REQUEST_ATTRIBUTE_SERVICE_URL, serviceUrl );
    }
}
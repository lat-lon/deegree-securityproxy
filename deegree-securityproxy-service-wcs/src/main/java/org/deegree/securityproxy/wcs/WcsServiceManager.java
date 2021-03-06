package org.deegree.securityproxy.wcs;

import org.deegree.securityproxy.authorization.RequestAuthorizationManager;
import org.deegree.securityproxy.authorization.logging.AuthorizationReport;
import org.deegree.securityproxy.exception.ServiceExceptionManager;
import org.deegree.securityproxy.exception.ServiceExceptionWrapper;
import org.deegree.securityproxy.filter.ServiceManager;
import org.deegree.securityproxy.filter.StatusCodeResponseBodyWrapper;
import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.request.UnsupportedRequestTypeException;
import org.deegree.securityproxy.request.parser.OwsRequestParser;
import org.deegree.securityproxy.request.parser.RequestParsingException;
import org.deegree.securityproxy.request.parser.ServiceTypeParser;
import org.deegree.securityproxy.responsefilter.ResponseFilterException;
import org.deegree.securityproxy.responsefilter.ResponseFilterManager;
import org.deegree.securityproxy.responsefilter.logging.DefaultResponseFilterReport;
import org.deegree.securityproxy.responsefilter.logging.ResponseFilterReport;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * This is an implementation of a {@link ServiceManager} for wcs-requests. It contains wcs specific parser,
 * authorization-manager and filter-manager. It is possible to start parsing of wcs-requests, wcs-authorization,
 * wcs-response-filtering and a check whether response-filtering is enabled.
 * 
 * @author <a href="goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * @version $Revision: $, $Date: $
 */
class WcsServiceManager implements ServiceManager, ServiceExceptionManager {

    private final OwsRequestParser parser;

    private final RequestAuthorizationManager requestAuthorizationManager;

    private final List<ResponseFilterManager> filterManagers;

    private final ServiceExceptionWrapper serviceExceptionWrapper;

    public WcsServiceManager( OwsRequestParser parser, RequestAuthorizationManager requestAuthorizationManager,
                              List<ResponseFilterManager> filterManagers,
                              ServiceExceptionWrapper serviceExceptionWrapper ) {
        this.parser = parser;
        this.requestAuthorizationManager = requestAuthorizationManager;
        this.filterManagers = filterManagers;

        if ( serviceExceptionWrapper != null )
            this.serviceExceptionWrapper = serviceExceptionWrapper;
        else
            this.serviceExceptionWrapper = new ServiceExceptionWrapper();
    }

    @Override
    public OwsRequest parse( HttpServletRequest httpRequest )
                    throws UnsupportedRequestTypeException, RequestParsingException {
        return parser.parse( httpRequest );
    }

    @Override
    public AuthorizationReport authorize( Authentication authentication, OwsRequest owsRequest ) {
        return requestAuthorizationManager.decide( authentication, owsRequest );
    }

    @Override
    public boolean isResponseFilterEnabled( OwsRequest owsRequest ) {
        for ( ResponseFilterManager filterManager : filterManagers ) {
            if ( filterManager.canBeFiltered( owsRequest ) )
                return true;
        }
        return false;
    }

    @Override
    public ResponseFilterReport filterResponse( StatusCodeResponseBodyWrapper wrappedResponse,
                                                Authentication authentication, OwsRequest owsRequest )
                    throws ResponseFilterException {
        for ( ResponseFilterManager filterManager : filterManagers ) {
            if ( filterManager.canBeFiltered( owsRequest ) )
                return filterManager.filterResponse( wrappedResponse, owsRequest, authentication );
        }
        return createEmptyFilterReport();
    }

    @Override
    public boolean isServiceTypeSupported( String serviceType, HttpServletRequest request ) {
        if ( serviceType != null )
            return doesEqualWcs( serviceType );
        return doesEqualWcs( new ServiceTypeParser().determineServiceType( request ) );
    }

    private boolean doesEqualWcs( String serviceType ) {
        return "wcs".equalsIgnoreCase( serviceType );
    }

    @Override
    public ServiceExceptionWrapper retrieveServiceExceptionWrapper() {
        return serviceExceptionWrapper;
    }

    private ResponseFilterReport createEmptyFilterReport() {
        return new DefaultResponseFilterReport( "Response was not filtered! No response filter manager was found!" );
    }

}

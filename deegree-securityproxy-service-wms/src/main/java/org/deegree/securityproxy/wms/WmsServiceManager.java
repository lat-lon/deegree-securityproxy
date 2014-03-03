package org.deegree.securityproxy.wms;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.deegree.securityproxy.authorization.RequestAuthorizationManager;
import org.deegree.securityproxy.authorization.logging.AuthorizationReport;
import org.deegree.securityproxy.filter.ServiceManager;
import org.deegree.securityproxy.filter.StatusCodeResponseBodyWrapper;
import org.deegree.securityproxy.request.KvpNormalizer;
import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.request.OwsRequestParser;
import org.deegree.securityproxy.request.UnsupportedRequestTypeException;
import org.deegree.securityproxy.responsefilter.logging.ResponseFilterReport;
import org.springframework.security.core.Authentication;

/**
 * This is an implementation of a {@link ServiceManager} for wms-requests. It contains wms specific parser,
 * authorization-manager and filter-manager. It is possible to start parsing of wms-requests, wms-authorization,
 * wms-response-filtering and a check whether response-filtering is enabled.
 * 
 * @author <a href="wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author last edited by: $Author: stenger $
 * @version $Revision: $, $Date: $
 */
class WmsServiceManager implements ServiceManager {

    private final OwsRequestParser parser;

    private final RequestAuthorizationManager requestAuthorizationManager;

    public WmsServiceManager( OwsRequestParser parser, RequestAuthorizationManager requestAuthorizationManager ) {
        this.parser = parser;
        this.requestAuthorizationManager = requestAuthorizationManager;
    }

    @Override
    public OwsRequest parse( HttpServletRequest httpRequest )
                            throws UnsupportedRequestTypeException {
        return parser.parse( httpRequest );
    }

    @Override
    public AuthorizationReport authorize( Authentication authentication, OwsRequest owsRequest ) {
        return requestAuthorizationManager.decide( authentication, owsRequest );
    }

    @Override
    public boolean isResponseFilterEnabled( OwsRequest owsRequest ) {
        return false;
    }

    @Override
    public ResponseFilterReport filterResponse( StatusCodeResponseBodyWrapper wrappedResponse,
                                                Authentication authentication, OwsRequest owsRequest )
                            throws IOException {
        return null;
    }

    @Override
    public boolean isServiceTypeSupported( HttpServletRequest request ) {
        @SuppressWarnings("unchecked")
        Map<String, String[]> kvpMap = KvpNormalizer.normalizeKvpMap( request.getParameterMap() );
        return "wms".equalsIgnoreCase( kvpMap.get( "service" )[0] );
    }

}
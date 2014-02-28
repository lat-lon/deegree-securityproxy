package org.deegree.securityproxy.wms;

import org.deegree.securityproxy.authorization.RequestAuthorizationManager;
import org.deegree.securityproxy.authorization.logging.AuthorizationReport;
import org.deegree.securityproxy.filter.ServiceManager;
import org.deegree.securityproxy.filter.StatusCodeResponseBodyWrapper;
import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.request.OwsRequestParser;
import org.deegree.securityproxy.request.UnsupportedRequestTypeException;
import org.deegree.securityproxy.responsefilter.ResponseFilterManager;
import org.deegree.securityproxy.responsefilter.logging.ResponseFilterReport;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

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

    private final ResponseFilterManager filterManager;

    public WmsServiceManager( OwsRequestParser parser, RequestAuthorizationManager requestAuthorizationManager,
                              ResponseFilterManager filterManager ) {
        this.parser = parser;
        this.requestAuthorizationManager = requestAuthorizationManager;
        this.filterManager = filterManager;
    }

    @Override
    public OwsRequest parse( HttpServletRequest httpRequest ) throws UnsupportedRequestTypeException {
        return null;
    }

    @Override
    public AuthorizationReport authorize( Authentication authentication, OwsRequest owsRequest ) {
        return null;
    }

    @Override
    public boolean isResponseFilterEnabled( OwsRequest owsRequest ) {
        return false;
    }

    @Override
    public ResponseFilterReport filterResponse( StatusCodeResponseBodyWrapper wrappedResponse,
                                                Authentication authentication,
                                                OwsRequest owsRequest ) throws IOException {
        return null;
    }

    @Override
    public boolean isServiceTypeSupported( HttpServletRequest request ) {
        return false;
    }

}
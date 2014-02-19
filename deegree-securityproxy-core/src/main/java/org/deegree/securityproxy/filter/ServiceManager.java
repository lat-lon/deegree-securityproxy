package org.deegree.securityproxy.filter;

import org.deegree.securityproxy.authorization.logging.AuthorizationReport;
import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.request.UnsupportedRequestTypeException;
import org.deegree.securityproxy.responsefilter.logging.ResponseFilterReport;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author <a href="goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * @version $Revision: $, $Date: $
 */
public interface ServiceManager {

    OwsRequest parse( HttpServletRequest httpRequest ) throws UnsupportedRequestTypeException;

    AuthorizationReport authorize( Authentication authentication, OwsRequest owsRequest );

    boolean isResponseFilterEnabled( OwsRequest owsRequest );

    ResponseFilterReport filterResponse( StatusCodeResponseBodyWrapper wrappedResponse,
                                         Authentication authentication, OwsRequest owsRequest ) throws IOException;

    boolean isServiceTypeSupported( HttpServletRequest request );
}

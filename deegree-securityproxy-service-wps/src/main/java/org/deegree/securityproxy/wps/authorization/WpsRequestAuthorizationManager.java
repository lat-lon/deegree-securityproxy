package org.deegree.securityproxy.wps.authorization;

import org.deegree.securityproxy.authorization.RequestAuthorizationManager;
import org.deegree.securityproxy.authorization.logging.AuthorizationReport;
import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.wps.request.WpsRequest;
import org.springframework.security.core.Authentication;

/**
 * Checks if a authenticated User is permitted to perform an incoming {@link javax.servlet.http.HttpServletRequest}
 * against a WPS.
 * 
 * @author <a href="wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author <a href="stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * @version $Revision: $, $Date: $
 */
public class WpsRequestAuthorizationManager implements RequestAuthorizationManager {

    @Override
    public AuthorizationReport decide( Authentication authentication, OwsRequest request ) {
        return new AuthorizationReport();
    }

    @Override
    public boolean supports( Class<?> clazz ) {
        return WpsRequest.class.equals( clazz );
    }

}

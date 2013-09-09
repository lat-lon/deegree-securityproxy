package org.deegree.securityproxy.authorization.wcs;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;

/**
 * Checks if a authenticated User is permitted to perform an incoming {@link HttpServletRequest} against a WCS.
 * 
 * @author <a href="stenger@lat-lon.de">Dirk Stenger</a>
 * @author <a href="goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
public class WcsRequestAuthorizationManager implements AccessDecisionManager {

    @Override
    public void decide( Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes )
                            throws AccessDeniedException, InsufficientAuthenticationException {
        // HttpServletRequest req = (HttpServletRequest) object;
        // Shall support Authentication instances that contain WcsPermission(s) as GrantedAuthority(ies)
    }

    @Override
    public boolean supports( ConfigAttribute attribute ) {
        // Not needed in this implementation.
        return true;
    }

    @Override
    public boolean supports( Class<?> clazz ) {
        // Shall support HttpServletRequests for service type WCS
        return false;
    }

}

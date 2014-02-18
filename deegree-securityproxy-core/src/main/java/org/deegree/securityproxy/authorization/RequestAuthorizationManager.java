package org.deegree.securityproxy.authorization;

import org.deegree.securityproxy.authorization.logging.AuthorizationReport;
import org.springframework.security.core.Authentication;

/**
 * 
 * Implementations provide authorization for a request type defined in the supports(Class) method.
 * 
 * @author <a href="wilden@lat-lon.de">Johannes Wilden</a>
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * 
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
public interface RequestAuthorizationManager {

    /**
     * Perform an access decision.
     * 
     * @param authentication
     *            the user authentication to authorize. May be <code>null</code>.
     * @param object
     *            the secured object
     * @return {@link AuthorizationReport} containing message and result of the authorization
     */
    AuthorizationReport decide( Authentication authentication, Object object );

    boolean supports( Class<?> clazz );

}
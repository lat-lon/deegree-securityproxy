package org.deegree.securityproxy.authorization;

import org.deegree.securityproxy.authorization.logging.AuthorizationReport;
import org.deegree.securityproxy.request.OwsRequest;
import org.springframework.security.core.Authentication;

/**
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
     *            the user authentication to authorize, may be <code>null</code>.
     * @param request
     *            the secured request, never <code>null</code>
     * @return {@link AuthorizationReport} containing message and result of the authorization
     */
    AuthorizationReport decide( Authentication authentication, OwsRequest request );

    /**
     * @param clazz
     *            never <code>null</code>
     * @return <code>true</code> if the class is supported, <code>false</code> otherwise
     */
    boolean supports( Class<?> clazz );

}
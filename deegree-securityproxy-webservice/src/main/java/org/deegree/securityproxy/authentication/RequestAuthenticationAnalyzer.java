package org.deegree.securityproxy.authentication;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;

/**
 * A request authentication analyzer checks an incoming servlet request for authentication related information.
 * 
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
public interface RequestAuthenticationAnalyzer {

    /**
     * Checks an incoming servlet request and generates an unverified {@link Authentication} instance. No actual
     * authentication takes place - the context will later handle verification of the generated token.
     * 
     * @param request
     *            never <code>null</code>. The request body stream must not be spent in the process!
     * @return an {@link Authentication} instance that contains information about the requesting user based on the
     *         request. No actual authentication takes place at this point.
     */
    Authentication provideAuthenticationFromHttpRequest( HttpServletRequest request );
}

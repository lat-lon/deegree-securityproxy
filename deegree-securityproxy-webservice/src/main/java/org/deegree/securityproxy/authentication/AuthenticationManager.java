package org.deegree.securityproxy.authentication;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;

/**
 * Performs authentication of an incoming {@link HttpServletRequest} and stores result in the security context.
 * 
 * @author <a href="goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
public class AuthenticationManager {

    @Autowired
    private RequestAuthenticationAnalyzer analyzer;

    @Autowired
    private AuthenticationProvider provider;

    /**
     * Perform authentication of an {@link HttpServletRequest} and store result in the security context.
     * 
     * @param request
     *            never <code>null</code>
     * @throws IllegalArgumentException
     *             on <code>null</code> request parameter
     */
    public void performAuthentication( HttpServletRequest request )
                            throws IllegalArgumentException {
        if ( request == null )
            throw new IllegalArgumentException( "Passed request is null!" );
        Authentication preparedAuthenticationToken = analyzer.provideAuthenticationFromHttpRequest( request );
        provider.authenticate( preparedAuthenticationToken );
    }
}

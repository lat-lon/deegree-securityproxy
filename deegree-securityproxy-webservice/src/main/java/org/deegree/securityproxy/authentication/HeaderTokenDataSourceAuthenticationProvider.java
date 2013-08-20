package org.deegree.securityproxy.authentication;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

/**
 * Performs verification of an incoming {@link Authentication}. Authenticates the token against a
 * {@link HeaderTokenDataSource}
 * 
 * @author <a href="goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
public class HeaderTokenDataSourceAuthenticationProvider implements AuthenticationProvider {

    private static Logger log = Logger.getLogger( HeaderTokenDataSourceAuthenticationProvider.class );

    @Autowired
    private HeaderTokenDataSource source;

    @Override
    public Authentication authenticate( Authentication authentication )
                            throws AuthenticationException {
        log.info( "Authenticating incoming request " + authentication );
        if ( authentication == null )
            throw new IllegalArgumentException( "Passed token must not be null!" );
        String headerTokenValue = (String) authentication.getPrincipal();
        log.info( "Header token " + headerTokenValue );
        return createVerifiedToken( headerTokenValue );
    }

    @Override
    public boolean supports( Class<?> authenticationTokenType ) {
        return true;
    }

    private Authentication createVerifiedToken( String headerTokenValue ) {
        UserDetails userDetails = source.loadUserDetailsFromDataSource( headerTokenValue );
        boolean isAuthenticated = userDetails != null;
        if ( isAuthenticated ) {
            return new PreAuthenticatedAuthenticationToken( userDetails, headerTokenValue,
                                                            userDetails.getAuthorities() );
        } else {
            throw new BadCredentialsException( "No pre-authenticated principal found in request." );
        }
    }

}

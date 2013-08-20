package org.deegree.securityproxy.authentication;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Performs verification of an incoming {@link HeaderAuthenticationToken}. Authenticates the token against a
 * {@link HeaderTokenDataSource}
 * 
 * @author <a href="goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
public class HeaderTokenDataSourceAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private HeaderTokenDataSource source;

    @Override
    public Authentication authenticate( Authentication authentication )
                            throws AuthenticationException {
        if ( authentication == null )
            throw new IllegalArgumentException( "Passed token must not be null!" );
        if ( !( authentication instanceof HeaderAuthenticationToken ) )
            throw new IllegalArgumentException( "Passed token is not an HeaderAuthenticationToken!" );
        HeaderAuthenticationToken token = (HeaderAuthenticationToken) authentication;
        String headerTokenValue = token.getHeaderTokenValue();
        if ( headerTokenValue == null ) {
            populateTokenWithNoHeaderPresent( token );
        } else {
            populateTokenWithHeaderPresent( token, headerTokenValue );
        }
        getContext().setAuthentication( token );
        return token;
    }

    @Override
    public boolean supports( Class<?> authenticationTokenType ) {
        return HeaderAuthenticationToken.class.isAssignableFrom( authenticationTokenType );
    }

    private void populateTokenWithHeaderPresent( HeaderAuthenticationToken token, String headerTokenValue ) {
        UserDetails userDetails = source.loadUserDetailsFromDataSource( headerTokenValue );
        token.setPrincipal( userDetails );
        boolean isAuthenticated = userDetails != null;
        token.setAuthenticated( isAuthenticated );
        if ( isAuthenticated )
            token.setCredentials( headerTokenValue );
    }

    private void populateTokenWithNoHeaderPresent( Authentication token ) {
        token.setAuthenticated( false );
    }
}

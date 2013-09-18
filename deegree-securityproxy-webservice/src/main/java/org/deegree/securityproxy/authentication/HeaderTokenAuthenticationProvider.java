package org.deegree.securityproxy.authentication;

import java.util.Collections;

import org.apache.log4j.Logger;
import org.deegree.securityproxy.authentication.repository.UserDetailsDao;
import org.deegree.securityproxy.authentication.repository.UserDetailsDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

/**
 * Performs verification of an incoming {@link Authentication}. Authenticates the token against a
 * {@link UserDetailsDaoImpl}
 * 
 * @author <a href="goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
public class HeaderTokenAuthenticationProvider implements AuthenticationProvider {

    private static Logger log = Logger.getLogger( HeaderTokenAuthenticationProvider.class );

    @Autowired
    private UserDetailsDao dao;

    @Override
    public Authentication authenticate( Authentication authentication )
                            throws AuthenticationException {
        log.info( "Authenticating incoming request " + authentication );
        if ( authentication == null )
            return generateAnonymousAuthenticationToken();
        String headerTokenValue = (String) authentication.getPrincipal();
        log.info( "Header token " + headerTokenValue );
        return createVerifiedToken( headerTokenValue );
    }

    @Override
    public boolean supports( Class<?> authenticationTokenType ) {
        return true;
    }

    private Authentication createVerifiedToken( String headerTokenValue ) {
        UserDetails userDetails = dao.loadUserDetailsFromDataSource( headerTokenValue );
        boolean isAuthenticated = userDetails != null;
        if ( isAuthenticated ) {
            return new PreAuthenticatedAuthenticationToken( userDetails, headerTokenValue, userDetails.getAuthorities() );
        } else {
            return generateAnonymousAuthenticationToken();
        }
    }

    private AnonymousAuthenticationToken generateAnonymousAuthenticationToken() {
        SimpleGrantedAuthority grantedAuthorityImpl = new SimpleGrantedAuthority(
                                                              "ROLE_ANONYMOUS" );
        return new AnonymousAuthenticationToken(
                                                 "Anonymous User",
                                                 "Anonymous User",
                                                 Collections.singletonList( grantedAuthorityImpl ) );
    }
}

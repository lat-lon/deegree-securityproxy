package org.deegree.securityproxy.authentication.wass;

import java.util.Collections;

import org.apache.log4j.Logger;
import org.deegree.securityproxy.sessionid.SessionIdManager;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

/**
 * Authenticate by the session id requested from a WASS.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class WassSessionAuthenticationProvider implements AuthenticationProvider {

    static final String ANONYMOUS_USER = "Anonymous User";

    private static final Logger LOG = Logger.getLogger( WassSessionAuthenticationProvider.class );

    private final SessionIdManager sessionIdManager;

    public WassSessionAuthenticationProvider( SessionIdManager sessionIdManager ) {
        this.sessionIdManager = sessionIdManager;
    }

    @Override
    public Authentication authenticate( Authentication authentication )
                            throws AuthenticationException {
        LOG.info( "Authenticating incoming: " + authentication );
        if ( authentication == null )
            return generateAnonymousAuthenticationToken();
        return createVerifiedToken( authentication );
    }

    @Override
    public boolean supports( Class<?> authentication ) {
        // TODO: which authentications are supported?
        return true;
    }

    private Authentication createVerifiedToken( Authentication authentication ) {
        String sessionId = retrieveSessionId( authentication );
        boolean isAuthenticated = sessionId != null;
        if ( isAuthenticated ) {
            return new PreAuthenticatedAuthenticationToken( authentication.getPrincipal(), sessionId );
        } else {
            return generateAnonymousAuthenticationToken();
        }
    }

    private String retrieveSessionId( Authentication authentication ) {
        // TODO: retrieve session id by username/password
        return sessionIdManager.retrieveSessionId();
    }

    private AnonymousAuthenticationToken generateAnonymousAuthenticationToken() {
        SimpleGrantedAuthority grantedAuthorityImpl = new SimpleGrantedAuthority( "ROLE_ANONYMOUS" );
        return new AnonymousAuthenticationToken( ANONYMOUS_USER, ANONYMOUS_USER,
                                                 Collections.singletonList( grantedAuthorityImpl ) );
    }

}
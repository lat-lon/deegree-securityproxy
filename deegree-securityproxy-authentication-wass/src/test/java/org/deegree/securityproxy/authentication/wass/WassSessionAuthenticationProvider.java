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
 * Appends a session id requested from a WASS.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class WassSessionAuthenticationProvider implements AuthenticationProvider {

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
        String sessionId = sessionIdManager.retrieveSessionId();
        boolean isAuthenticated = sessionId != null;
        if ( isAuthenticated ) {
            return new PreAuthenticatedAuthenticationToken( authentication.getPrincipal(), sessionId );
        } else {
            return generateAnonymousAuthenticationToken();
        }
    }

    private AnonymousAuthenticationToken generateAnonymousAuthenticationToken() {
        SimpleGrantedAuthority grantedAuthorityImpl = new SimpleGrantedAuthority( "ROLE_ANONYMOUS" );
        return new AnonymousAuthenticationToken( "Anonymous User", "Anonymous User",
                                                 Collections.singletonList( grantedAuthorityImpl ) );
    }

}

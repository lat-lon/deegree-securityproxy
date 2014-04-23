package org.deegree.securityproxy.authentication.wass;

import static org.deegree.securityproxy.authentication.wass.WassSessionAuthenticationProvider.ANONYMOUS_USER;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.deegree.securityproxy.sessionid.SessionIdManager;
import org.junit.Test;
import org.springframework.security.core.Authentication;

/**
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class WassSessionAuthenticationProviderTest {

    private static final String TECHNICAL_SESSION_ID = "technicalSessionId";

    private static final String VALID_USER_SESSION_ID = "userSessionId";

    private static final String VALID_USER = "validUser";

    private static final String VALID_PASSWORD = "validPassword";

    @Test
    public void testAuthenticateAnonymous()
                            throws Exception {
        SessionIdManager sessionIdManager = mockSessionIdManager();
        WassSessionAuthenticationProvider provider = new WassSessionAuthenticationProvider( sessionIdManager );
        Authentication authenticate = provider.authenticate( mockAuthentication( null ) );

        assertThat( (String) authenticate.getPrincipal(), is( nullValue() ) );
        assertThat( (String) authenticate.getCredentials(), is( TECHNICAL_SESSION_ID ) );
    }

    @Test
    public void testAuthenticateNullAuthentication()
                            throws Exception {
        SessionIdManager sessionIdManager = mockSessionIdManager();
        WassSessionAuthenticationProvider provider = new WassSessionAuthenticationProvider( sessionIdManager );
        Authentication authenticate = provider.authenticate( null );

        assertThat( (String) authenticate.getPrincipal(), is( ANONYMOUS_USER ) );
        assertThat( (String) authenticate.getCredentials(), is( "" ) );
    }

    private Authentication mockAuthentication( String principal ) {
        Authentication authentication = mock( Authentication.class );
        when( authentication.getPrincipal() ).thenReturn( principal );
        return authentication;
    }

    private SessionIdManager mockSessionIdManager() {
        SessionIdManager sessionIdManager = mock( SessionIdManager.class );
        when( sessionIdManager.retrieveSessionId() ).thenReturn( TECHNICAL_SESSION_ID );
        when( sessionIdManager.retrieveSessionId( VALID_USER, VALID_PASSWORD ) ).thenReturn( VALID_USER_SESSION_ID );
        return sessionIdManager;
    }

}
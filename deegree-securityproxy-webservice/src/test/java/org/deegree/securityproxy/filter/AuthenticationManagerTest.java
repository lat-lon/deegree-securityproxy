package org.deegree.securityproxy.filter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.deegree.securityproxy.authentication.AuthenticationManager;
import org.deegree.securityproxy.authentication.RequestAuthenticationAnalyzer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/AuthenticationManagerTestContext.xml" })
public class AuthenticationManagerTest {

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private AuthenticationProvider provider;

    @Autowired
    private RequestAuthenticationAnalyzer analyzer;

    /**
     * Reset the mocked instance of logger to prevent side effects between tests
     */
    @Before
    public void resetMocks() {
        reset( provider, analyzer );
    }

    @Test
    public void testRequestShouldBeAuthenticated()
                            throws IOException, ServletException {
        HttpServletRequest request = mock( HttpServletRequest.class );
        manager.performAuthentication( request );
        Authentication provideAuthenticationFromHttpRequest = verify( analyzer ).provideAuthenticationFromHttpRequest( request );
        verify( provider ).authenticate( provideAuthenticationFromHttpRequest );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullRequestShouldCauseException() {
        manager.performAuthentication( null );
    }

}
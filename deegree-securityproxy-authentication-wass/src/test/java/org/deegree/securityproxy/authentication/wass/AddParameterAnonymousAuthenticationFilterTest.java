package org.deegree.securityproxy.authentication.wass;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.deegree.securityproxy.filter.KvpRequestWrapper;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class AddParameterAnonymousAuthenticationFilterTest {

    private static final String parameterKey = "paramKey";

    @Test
    public void testDoFilter()
                            throws Exception {
        String parameterValue = "paramValue";
        AddParameterAnonymousAuthenticationFilter filter = createFilter( parameterValue );
        FilterChain filterChain = mockChain();
        HttpServletResponse response = mockResponse();
        filter.doFilter( mockRequest(), response, filterChain );

        ArgumentCaptor<ServletRequest> argument = ArgumentCaptor.forClass( ServletRequest.class );
        verify( filterChain ).doFilter( argument.capture(), eq( response ) );
        KvpRequestWrapper passedRequest = (KvpRequestWrapper) argument.getValue();

        assertThat( passedRequest.getParameter( parameterKey ), is( parameterValue ) );
    }

    @Test
    public void testDoFilterWithNullAuthenticationCredentials()
                            throws Exception {
        String parameterValue = null;
        AddParameterAnonymousAuthenticationFilter filter = createFilter( parameterValue );
        FilterChain filterChain = mockChain();
        HttpServletResponse response = mockResponse();
        filter.doFilter( mockRequest(), response, filterChain );

        ArgumentCaptor<ServletRequest> argument = ArgumentCaptor.forClass( ServletRequest.class );
        verify( filterChain ).doFilter( argument.capture(), eq( response ) );
        KvpRequestWrapper passedRequest = (KvpRequestWrapper) argument.getValue();

        assertThat( passedRequest.getParameter( parameterKey ), is( parameterValue ) );
    }

    private HttpServletRequest mockRequest() {
        return mock( HttpServletRequest.class );
    }

    private HttpServletResponse mockResponse() {
        return mock( HttpServletResponse.class );
    }

    private FilterChain mockChain() {
        return mock( FilterChain.class );
    }

    private AddParameterAnonymousAuthenticationFilter createFilter( String credentials ) {
        AddParameterAnonymousAuthenticationFilter filter = new AddParameterAnonymousAuthenticationFilter( parameterKey );
        AuthenticationManager authenticationManager = mockAuthentication( credentials );
        filter.setAuthenticationManager( authenticationManager );
        return filter;
    }

    private AuthenticationManager mockAuthentication( String credentials ) {
        AuthenticationManager authenticationManager = mock( AuthenticationManager.class );
        Authentication resultAuthentication = mock( Authentication.class );
        when( resultAuthentication.getCredentials() ).thenReturn( credentials );
        when( authenticationManager.authenticate( any( Authentication.class ) ) ).thenReturn( resultAuthentication );
        return authenticationManager;
    }
}

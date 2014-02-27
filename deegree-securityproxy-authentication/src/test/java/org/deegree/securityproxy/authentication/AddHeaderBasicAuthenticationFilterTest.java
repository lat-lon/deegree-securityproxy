package org.deegree.securityproxy.authentication;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.security.core.Authentication;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class AddHeaderBasicAuthenticationFilterTest {

    private static final String HEADER_NAME = "headerName";

    private static final String HEADER_TOKEN = "testToken";

    @Test
    public void testOnSuccessfulAuthenticationShouldReturnHeader()
                            throws Exception {
        AddHeaderBasicAuthenticationFilter authenticationFilter = new AddHeaderBasicAuthenticationFilter( HEADER_NAME );
        HttpServletRequest request = createWrappedRequest();
        authenticationFilter.onSuccessfulAuthentication( request, mockResponse(), mockAuthResult() );

        assertThat( request.getHeader( HEADER_NAME ), is( HEADER_TOKEN ) );
    }

    @Test
    public void testDoFilter()
                            throws Exception {
        AddHeaderBasicAuthenticationFilter authenticationFilter = new AddHeaderBasicAuthenticationFilter( HEADER_NAME );
        HttpServletRequest request = mockRequest();
        FilterChain mockedChain = mockFilterChain();
        authenticationFilter.doFilter( request, mockResponse(), mockedChain );

        verify( mockedChain ).doFilter( any( ServletRequest.class ), any( ServletResponse.class ) );
    }

    private HttpServletRequest createWrappedRequest() {
        return new AddHeaderHttpServletRequestWrapper( mock( HttpServletRequest.class ) );
    }

    private HttpServletRequest mockRequest() {
        return mock( HttpServletRequest.class );
    }

    private HttpServletResponse mockResponse() {
        return mock( HttpServletResponse.class );
    }

    private FilterChain mockFilterChain() {
        return mock( FilterChain.class );
    }

    private Authentication mockAuthResult() {
        Authentication authResult = mock( Authentication.class );
        OwsUserDetails user = mock( OwsUserDetails.class );
        when( user.getAccessToken() ).thenReturn( HEADER_TOKEN );
        when( authResult.getPrincipal() ).thenReturn( user );
        return authResult;
    }

}
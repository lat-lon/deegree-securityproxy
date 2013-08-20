package org.deegree.securityproxy.filter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.deegree.securityproxy.authentication.HeaderAuthenticationToken;
import org.deegree.securityproxy.authentication.HttpHeaderRequestAuthenticationAnalyzer;
import org.junit.Before;
import org.junit.Test;

public class HttpHeaderRequestAuthenticationAnalyzerTest {

    private static final String VALID_ACCESS_TOKEN_HEADER_VALUE = "00000000001";

    private static final String ACCESS_TOKEN_HEADER_KEY = "access_token";

    private HttpHeaderRequestAuthenticationAnalyzer analyzer;
    
    @Before
    public void setupAnalyzer() {
        analyzer = new HttpHeaderRequestAuthenticationAnalyzer( ACCESS_TOKEN_HEADER_KEY );
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testConstructorShouldThrowExceptionOnNullParameter(){
        new HttpHeaderRequestAuthenticationAnalyzer( null );
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testConstructorShouldThrowExceptionOnEmptyParameter(){
        new HttpHeaderRequestAuthenticationAnalyzer( "" );
    }
    
    @Test
    public void testProvideAuthenticationFromHttpRequest(){
        HeaderAuthenticationToken auth = (HeaderAuthenticationToken) analyzer.provideAuthenticationFromHttpRequest( mockServletRequestWithHeader() );
        assertThat(auth.getHeaderTokenValue(), is( VALID_ACCESS_TOKEN_HEADER_VALUE ));
    }
    
    @Test
    public void testProvideAuthenticationFromHttpRequestHeaderNotSet(){
        HeaderAuthenticationToken auth = (HeaderAuthenticationToken) analyzer.provideAuthenticationFromHttpRequest( mockServletRequestNullHeader() );
        assertThat(auth.getHeaderTokenValue(), nullValue() );
    }

    private HttpServletRequest mockServletRequestWithHeader() {
        HttpServletRequest mockRequest = mock( HttpServletRequest.class );
        when( mockRequest.getHeader( ACCESS_TOKEN_HEADER_KEY ) ).thenReturn( VALID_ACCESS_TOKEN_HEADER_VALUE );
        return mockRequest;
    }
    
    private HttpServletRequest mockServletRequestNullHeader() {
        HttpServletRequest mockRequest = mock( HttpServletRequest.class );
        return mockRequest;
    }
}

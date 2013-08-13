package org.deegree.securityproxy.filter;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.deegree.securityproxy.logger.ProxyReportLogger;
import org.deegree.securityproxy.report.ProxyReport;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests for {@link LoggingFilter}
 *
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author <a href="goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/testApplicationContext.xml" })
public class LoggingFilterTest {

    private static final String CLIENT_IP_ADDRESS = "127.0.0.1";

    private static final String TARGET_URL = "devcloud.blackbridge.com";

    private static final String QUERY_STRING = "request=GetCapabilities";
    
    @Autowired
    private LoggingFilter loggingFilter;
    
    /**
     * Autowire a mocked instance of {@link ProxyReportLogger}
     */
    @Autowired
    private ProxyReportLogger logger;
    
    /**
     * Reset the mocked instance of logger to prevent side effects between tests
     */
    @Before
    public void resetMock() {
        reset( logger );
    }
    
    @Test
    public void testLoggingFilterShouldGenerateReportWithCorrectIpAddress()
                            throws IOException, ServletException {
        loggingFilter.doFilter( generateMockRequest(), generateMockResponse(), new FilterChainTestImpl( SC_OK ) );
        verify( logger ).logProxyReportInfo( argThat( hasCorrectIpAddress() ) );
    }

    @Test
    public void testLoggingFilterShouldGenerateReportWithCorrectTargetUrl()
                            throws IOException, ServletException {
        loggingFilter.doFilter( generateMockRequest(), generateMockResponse(), new FilterChainTestImpl( SC_OK ) );
        verify( logger ).logProxyReportInfo( argThat( hasCorrectTargetUrl() ) );
    }

    @Test
    public void testLoggingFilterShouldGenerateCorrectReportForSuccessfulReponse()
                            throws IOException, ServletException {
        loggingFilter.doFilter( generateMockRequest(), generateMockResponse(), new FilterChainTestImpl( SC_OK ) );
        verify( logger ).logProxyReportInfo( argThat( hasResponse( SC_OK ) ) );
    }

    @Test
    public void testLoggingFilterShouldGenerateCorrectReportForNotSuccessfulReponse()
                            throws IOException, ServletException {
        loggingFilter.doFilter( generateMockRequest(), generateMockResponse(), new FilterChainTestImpl( SC_BAD_REQUEST ) );
        verify( logger ).logProxyReportInfo( argThat( hasResponse( SC_BAD_REQUEST ) ) );
    }

    @Test
    public void testLoggingFilterShouldGenerateCorrectReportNullQueryString()
                            throws IOException, ServletException {
        loggingFilter.doFilter( generateMockRequestNullQueryString(), generateMockResponse(),
                                new FilterChainTestImpl( SC_BAD_REQUEST ) );
        verify( logger ).logProxyReportInfo( argThat( hasCorrectTargetUrlWithNullQueryString() ) );
    }

    private ServletRequest generateMockRequestNullQueryString() {
        HttpServletRequest mockRequest = mock( HttpServletRequest.class );
        when( mockRequest.getRemoteAddr() ).thenReturn( CLIENT_IP_ADDRESS );
        when( mockRequest.getRequestURL() ).thenReturn( new StringBuffer( TARGET_URL ) );
        when( mockRequest.getQueryString() ).thenReturn( null );
        return mockRequest;
    }

    private HttpServletRequest generateMockRequest() {
        HttpServletRequest mockRequest = mock( HttpServletRequest.class );
        when( mockRequest.getRemoteAddr() ).thenReturn( CLIENT_IP_ADDRESS );
        when( mockRequest.getRequestURL() ).thenReturn( new StringBuffer( TARGET_URL ) );
        when( mockRequest.getQueryString() ).thenReturn( QUERY_STRING );
        return mockRequest;
    }

    private HttpServletResponse generateMockResponse() {
        HttpServletResponse mockResponse = mock( HttpServletResponse.class );
        return mockResponse;
    }

    private Matcher<ProxyReport> hasCorrectIpAddress() {
        return new BaseMatcher<ProxyReport>() {

            public boolean matches( Object item ) {
                ProxyReport report = (ProxyReport) item;
                return CLIENT_IP_ADDRESS.equals( report.getIpAddressOfRequestingUser() );
            }

            public void describeTo( Description description ) {
                description.appendText( "Client IP address should be " + CLIENT_IP_ADDRESS );
            }
        };
    }

    private Matcher<ProxyReport> hasCorrectTargetUrl() {
        return new BaseMatcher<ProxyReport>() {

            private final String expected = TARGET_URL + "?" + QUERY_STRING;

            public boolean matches( Object item ) {
                ProxyReport report = (ProxyReport) item;
                return expected.equals( report.getTargetUri() );
            }

            public void describeTo( Description description ) {
                description.appendText( "Target url should be " + TARGET_URL + "?" + QUERY_STRING );
            }
        };
    }

    private Matcher<ProxyReport> hasCorrectTargetUrlWithNullQueryString() {
        return new BaseMatcher<ProxyReport>() {

            public boolean matches( Object item ) {
                ProxyReport report = (ProxyReport) item;
                return TARGET_URL.equals( report.getTargetUri() );
            }

            public void describeTo( Description description ) {
                description.appendText( "Target url should be " + TARGET_URL );
            }
        };
    }

    private Matcher<ProxyReport> hasResponse( final int expected ) {
        return new BaseMatcher<ProxyReport>() {

            public boolean matches( Object item ) {
                ProxyReport report = (ProxyReport) item;
                if ( SC_OK == expected )
                    return report.isResponseSuccessfullySent();
                else
                    return !report.isResponseSuccessfullySent();
            }

            public void describeTo( Description description ) {
                String not = SC_OK == expected ? "" : " not";
                String message = "Response report should indicate that the response has" + not
                                 + " been successfully sent!";
                description.appendText( message );
            }

        };
    }
}

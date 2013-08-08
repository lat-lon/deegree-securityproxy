package org.deegree.securityproxy.filter;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.deegree.securityproxy.logger.ProxyReportLogger;
import org.deegree.securityproxy.report.ProxyReport;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;

/**
 * Tests for {@link LoggingFilter}
 * 
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
public class LoggingFilterTest {

    private static final String CLIENT_IP_ADDRESS = "127.0.0.1";

    private static final String TARGET_URL  = "devcloud.blackbridge.com";
    
    @Test
    public void testLoggingFilterShouldGenerateReportWithCorrectIpAddress()
                            throws IOException, ServletException {
        ProxyReportLogger logger = mock( ProxyReportLogger.class );
        LoggingFilter loggingFilter = createLoggingFilter( logger );
        loggingFilter.doFilter( generateMockRequest(), generateMockResponse(), new FilterChainTestImpl( SC_OK ) );
        verify( logger ).logProxyReportInfo( argThat( hasCorrectIpAddress() ) );
    }

    @Test
    public void testLoggingFilterShouldGenerateReportWithCorrectTargetUrl()
                            throws IOException, ServletException {
        ProxyReportLogger logger = mock( ProxyReportLogger.class );
        LoggingFilter loggingFilter = createLoggingFilter( logger );
        loggingFilter.doFilter( generateMockRequest(), generateMockResponse(), new FilterChainTestImpl( SC_OK ) );
        verify( logger ).logProxyReportInfo( argThat( hasCorrectTargetHostName() ) );
    }

    @Test
    public void testLoggingFilterShouldGenerateCorrectReportForSuccessfulReponse()
                            throws IOException, ServletException {
        ProxyReportLogger logger = mock( ProxyReportLogger.class );
        LoggingFilter loggingFilter = createLoggingFilter( logger );
        loggingFilter.doFilter( generateMockRequest(), generateMockResponse(), new FilterChainTestImpl( SC_OK ) );
        verify( logger ).logProxyReportInfo( argThat( hasCorrectResponse( SC_OK ) ) );
    }
    
    @Test
    public void testLoggingFilterShouldGenerateCorrectReportForNotSuccessfulReponse()
                            throws IOException, ServletException {
        ProxyReportLogger logger = mock( ProxyReportLogger.class );
        LoggingFilter loggingFilter = createLoggingFilter( logger );
        loggingFilter.doFilter( generateMockRequest(), generateMockResponse(), new FilterChainTestImpl( SC_BAD_REQUEST ) );
        verify( logger ).logProxyReportInfo( argThat( hasCorrectResponse( SC_BAD_REQUEST ) ) );
    }

    private HttpServletRequest generateMockRequest() {
        HttpServletRequest mockRequest = mock( HttpServletRequest.class );
        when( mockRequest.getRemoteAddr() ).thenReturn( CLIENT_IP_ADDRESS );
        when( mockRequest.getRequestURL() ).thenReturn( new StringBuffer( TARGET_URL ) );
        return mockRequest;
    }

    private HttpServletResponse generateMockResponse() {
        HttpServletResponse mockResponse = mock( HttpServletResponse.class );
        return mockResponse;
    }

    private LoggingFilter createLoggingFilter( ProxyReportLogger logger ) {
        LoggingFilter loggingFilter = new LoggingFilter();
        loggingFilter.setReportLogger( logger );
        return loggingFilter;
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

    private Matcher<ProxyReport> hasCorrectTargetHostName() {
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

    private Matcher<ProxyReport> hasCorrectResponse( final int expected ) {
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

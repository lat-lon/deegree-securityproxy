package org.deegree.securityproxy.filter;

import org.deegree.securityproxy.authorization.logging.AuthorizationReport;
import org.deegree.securityproxy.exception.OwsServiceExceptionHandler;
import org.deegree.securityproxy.logger.ResponseFilterReportLogger;
import org.deegree.securityproxy.logger.SecurityRequestResponseLogger;
import org.deegree.securityproxy.report.SecurityReport;
import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.responsefilter.logging.ResponseFilterReport;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.deegree.securityproxy.authorization.TestRequestAuthorizationManager.SERVICE_URL;
import static org.deegree.securityproxy.exception.OwsCommonException.INVALID_PARAMETER;
import static org.deegree.securityproxy.exception.OwsCommonException.MISSING_PARAMETER;
import static org.deegree.securityproxy.filter.SecurityFilter.REQUEST_ATTRIBUTE_SERVICE_URL;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link SecurityFilterTest}
 * 
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author <a href="goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: erben $
 * @version $Revision: $, $Date: $
 */
public class SecurityFilterTest {

    private static final boolean IS_AUTHORIZED = true;

    private static final boolean IS_NOT_AUTHORIZED = false;

    private static final int SERIAL_UUID_LENGTH = 36;

    private static final String CLIENT_IP_ADDRESS = "127.0.0.1";

    private static final String TARGET_URL = "devcloud.blackbridge.com";

    private static final String QUERY_STRING = "service=WMS&request=GetCapabilities";

    private SecurityFilter filterAuthorized;

    private SecurityFilter filterUnauthorized;

    private SecurityRequestResponseLogger logger;

    private ResponseFilterReportLogger loggerResponseFilterReportMock;

    private OwsServiceExceptionHandler exceptionHandler;

    /**
     * Reset the mocked instance of logger to prevent side effects between tests
     */
    @Before
    public void reset()
                    throws Exception {
        logger = mockSecurityRequestResponseLogger();
        loggerResponseFilterReportMock = mockResponseFilterReportLogger();
        exceptionHandler = mock( OwsServiceExceptionHandler.class );
        List<ServiceManager> serviceManagersAuthorized = createServiceManagersWithOneServiceManager( IS_AUTHORIZED );
        filterAuthorized = new SecurityFilter( serviceManagersAuthorized, logger, loggerResponseFilterReportMock,
                        exceptionHandler );
        List<ServiceManager> serviceManagersUnauthorized = createServiceManagersWithOneServiceManager( IS_NOT_AUTHORIZED );
        filterUnauthorized = new SecurityFilter( serviceManagersUnauthorized, logger, loggerResponseFilterReportMock,
                        exceptionHandler );
    }

    @Test
    public void testLoggingFilterShouldGenerateReportWithSerialUuid()
                    throws IOException, ServletException {
        filterAuthorized.doFilter( generateMockRequest(), generateMockResponse(), new FilterChainTestImpl( SC_OK ) );
        verify( logger ).logProxyReportInfo( (SecurityReport) anyObject(), argThat( hasCorrectSerialUuid() ) );
    }

    @Test
    public void testLoggingFilterShouldGenerateReportWithCorrectIpAddress()
                    throws IOException, ServletException {
        filterAuthorized.doFilter( generateMockRequest(), generateMockResponse(), new FilterChainTestImpl( SC_OK ) );
        verify( logger ).logProxyReportInfo( argThat( hasCorrectIpAddress() ), (String) anyObject() );
    }

    @Test
    public void testLoggingShouldGenerateReportWithCorrectTargetUrl()
                    throws IOException, ServletException {
        filterAuthorized.doFilter( generateMockRequest(), generateMockResponse(), new FilterChainTestImpl( SC_OK ) );
        verify( logger ).logProxyReportInfo( argThat( hasCorrectTargetUrl() ), (String) anyObject() );
    }

    @Test
    public void testLoggingShouldGenerateCorrectReportForSuccessfulResponse()
                    throws IOException, ServletException {
        filterAuthorized.doFilter( generateMockRequest(), generateMockResponse(), new FilterChainTestImpl( SC_OK ) );
        verify( logger ).logProxyReportInfo( argThat( hasResponse( SC_OK ) ), (String) anyObject() );
    }

    @Test
    public void testLoggingOfResponseFilterReportShouldNeInvoked()
                    throws IOException, ServletException {
        filterAuthorized.doFilter( generateMockRequest(), generateMockResponse(), new FilterChainTestImpl( SC_OK ) );
        verify( loggerResponseFilterReportMock ).logResponseFilterReport( (ResponseFilterReport) anyObject(),
                                                                          (String) anyObject() );
    }

    @Test
    public void testLoggingOfResponseFilterReportShouldGenerateReportWithSerialUuid()
                    throws IOException, ServletException {
        filterAuthorized.doFilter( generateMockRequest(), generateMockResponse(), new FilterChainTestImpl( SC_OK ) );
        verify( loggerResponseFilterReportMock ).logResponseFilterReport( (ResponseFilterReport) anyObject(),
                                                                          argThat( hasCorrectSerialUuid() ) );
    }

    @Test(expected = AccessDeniedException.class)
    public void testLoggingShouldGenerateCorrectReportForUnauthenticatedResponse()
                    throws Exception {
        filterUnauthorized.doFilter( generateMockRequest(), generateMockResponse(), new FilterChainTestImpl(
                        SC_UNAUTHORIZED ) );
        verify( logger ).logProxyReportInfo( argThat( hasResponse( SC_UNAUTHORIZED ) ), (String) anyObject() );
    }

    @Test
    public void testLoggingShouldGenerateCorrectReportNullQueryString()
                    throws Exception {
        filterUnauthorized.doFilter( generateMockRequestNullQueryString(), generateMockResponse(),
                                     new FilterChainTestImpl( SC_BAD_REQUEST ) );
        verify( logger ).logProxyReportInfo( argThat( hasCorrectTargetUrlWithNullQueryString() ), (String) anyObject() );

        verify( exceptionHandler ).writeException( any( StatusCodeResponseBodyWrapper.class ), eq( MISSING_PARAMETER ),
                                                   eq( "service" ) );
    }

    @Test(expected = AccessDeniedException.class)
    public void testFilterShouldThrowExceptionOnUnauthorized()
                    throws Exception {
        filterUnauthorized.doFilter( generateMockRequest(), generateMockResponse(), new FilterChainTestImpl(
                        SC_UNAUTHORIZED ) );
    }

    @Test
    public void testResponseShouldContainSerialUuidHeader()
                    throws IOException, ServletException {
        HttpServletResponse response = generateMockResponse();
        filterAuthorized.doFilter( generateMockRequest(), response, new FilterChainTestImpl( SC_OK ) );
        filterAuthorized.doFilter( generateMockRequest(), response, new FilterChainTestImpl( SC_OK ) );

        ArgumentCaptor<String> uuidArgumentFirstInvocation = ArgumentCaptor.forClass( String.class );
        verify( response, times( 2 ) ).addHeader( argThat( is( "serial_uuid" ) ), uuidArgumentFirstInvocation.capture() );
        verify( response, times( 2 ) ).addHeader( argThat( is( "serial_uuid" ) ), uuidArgumentFirstInvocation.capture() );

        String firstUuidArgument = uuidArgumentFirstInvocation.getAllValues().get( 0 );
        String secondUuidArgument = uuidArgumentFirstInvocation.getAllValues().get( 1 );

        assertThat( firstUuidArgument, is( not( secondUuidArgument ) ) );
    }

    @Test
    public void testResponseShouldInvokeResponseFilterManager()
                    throws Exception {
        List<ServiceManager> serviceManagers = new ArrayList<ServiceManager>();
        ServiceManager serviceManager = mockSupportedServiceManager( true );
        serviceManagers.add( serviceManager );
        SecurityFilter filter = new SecurityFilter( serviceManagers, logger, loggerResponseFilterReportMock,
                        exceptionHandler );
        filter.doFilter( generateMockRequest(), generateMockResponse(), new FilterChainTestImpl( SC_OK ) );

        verify( serviceManager ).filterResponse( (StatusCodeResponseBodyWrapper) anyObject(),
                                                 (Authentication) anyObject(), (OwsRequest) anyObject() );
    }

    @Test
    public void testRequestShouldContainServiceNameAttribute()
                    throws IOException, ServletException {
        HttpServletRequest request = generateMockRequest();
        filterAuthorized.doFilter( request, generateMockResponse(), new FilterChainTestImpl( SC_OK ) );

        verify( request ).setAttribute( eq( REQUEST_ATTRIBUTE_SERVICE_URL ), eq( SERVICE_URL ) );
    }

    @Test
    public void testDoFilterWithEmptyListOfServiceManagersShouldDenyAccess()
                    throws Exception {
        SecurityFilter filter = createSecurityFilterWithEmptyListOfServiceManagers();
        filter.doFilter( generateMockRequest(), generateMockResponse(), new FilterChainTestImpl( SC_OK ) );

        verify( exceptionHandler ).writeException( any( StatusCodeResponseBodyWrapper.class ), eq( INVALID_PARAMETER ),
                                                   eq( "service" ) );
    }

    @Test
    public void testDoFilterWithNullServiceManagersShouldDenyAccess()
                    throws Exception {
        SecurityFilter filter = createSecurityFilterWithNullServiceManagers();
        filter.doFilter( generateMockRequest(), generateMockResponse(), new FilterChainTestImpl( SC_OK ) );

        verify( exceptionHandler ).writeException( any( StatusCodeResponseBodyWrapper.class ), eq( INVALID_PARAMETER ),
                                                   eq( "service" ) );
    }

    @Test
    public void testDoFilterWithThreeServiceManagersShouldTakeTheFirstManager()
                    throws Exception {
        ServiceManager serviceManager1 = mockSupportedServiceManager( true );
        ServiceManager serviceManager2 = mockSupportedServiceManager( true );
        ServiceManager serviceManager3 = mockSupportedServiceManager( true );
        SecurityFilter filter = createSecurityFilterWithServiceManagers( serviceManager1, serviceManager2,
                                                                         serviceManager3 );
        filter.doFilter( generateMockRequest(), generateMockResponse(), new FilterChainTestImpl( SC_OK ) );

        verify( serviceManager1, times( 1 ) ).isServiceTypeSupported( anyString(), any( HttpServletRequest.class ) );
        verify( serviceManager2, never() ).isServiceTypeSupported( anyString(), any( HttpServletRequest.class ) );
        verify( serviceManager3, never() ).isServiceTypeSupported( anyString(), any( HttpServletRequest.class ) );
    }

    @Test
    public void testDoFilterWithThreeServiceManagersShouldTakeTheSecondManager()
                    throws Exception {
        ServiceManager serviceManager1 = mockUnsupportedServiceManager();
        ServiceManager serviceManager2 = mockSupportedServiceManager( true );
        ServiceManager serviceManager3 = mockUnsupportedServiceManager();
        SecurityFilter filter = createSecurityFilterWithServiceManagers( serviceManager1, serviceManager2,
                                                                         serviceManager3 );
        filter.doFilter( generateMockRequest(), generateMockResponse(), new FilterChainTestImpl( SC_OK ) );

        verify( serviceManager1, times( 1 ) ).isServiceTypeSupported( anyString(), any( HttpServletRequest.class ) );
        verify( serviceManager2, times( 1 ) ).isServiceTypeSupported( anyString(), any( HttpServletRequest.class ) );
        verify( serviceManager3, never() ).isServiceTypeSupported( anyString(), any( HttpServletRequest.class ) );
    }

    @Test
    public void testDoFilterWithThreeServiceManagersShouldTakeTheThirdManager()
                    throws Exception {
        ServiceManager serviceManager1 = mockUnsupportedServiceManager();
        ServiceManager serviceManager2 = mockUnsupportedServiceManager();
        ServiceManager serviceManager3 = mockSupportedServiceManager( true );
        SecurityFilter filter = createSecurityFilterWithServiceManagers( serviceManager1, serviceManager2,
                                                                         serviceManager3 );
        filter.doFilter( generateMockRequest(), generateMockResponse(), new FilterChainTestImpl( SC_OK ) );

        verify( serviceManager1, times( 1 ) ).isServiceTypeSupported( anyString(), any( HttpServletRequest.class ) );
        verify( serviceManager2, times( 1 ) ).isServiceTypeSupported( anyString(), any( HttpServletRequest.class ) );
        verify( serviceManager3, times( 1 ) ).isServiceTypeSupported( anyString(), any( HttpServletRequest.class ) );
    }

    private ServletRequest generateMockRequestNullQueryString() {
        HttpServletRequest mockRequest = mock( HttpServletRequest.class );
        when( mockRequest.getRemoteAddr() ).thenReturn( CLIENT_IP_ADDRESS );
        when( mockRequest.getRequestURL() ).thenReturn( new StringBuffer( TARGET_URL ) );
        when( mockRequest.getQueryString() ).thenReturn( null );
        when( mockRequest.getParameterMap() ).thenReturn( new HashMap<String, String[]>() );
        return mockRequest;
    }

    private HttpServletRequest generateMockRequest() {
        HttpServletRequest mockRequest = mock( HttpServletRequest.class );
        when( mockRequest.getRemoteAddr() ).thenReturn( CLIENT_IP_ADDRESS );
        when( mockRequest.getRequestURL() ).thenReturn( new StringBuffer( TARGET_URL ) );
        when( mockRequest.getQueryString() ).thenReturn( QUERY_STRING );
        Map<String, String[]> parameterMap = new HashMap<String, String[]>();
        parameterMap.put( "service", new String[] { "WMS" } );
        parameterMap.put( "request", new String[] { "GetCapabilities" } );
        when( mockRequest.getParameterMap() ).thenReturn( parameterMap );
        doReturn( "GET" ).when( mockRequest ).getMethod();
        return mockRequest;
    }

    private HttpServletResponse generateMockResponse() {
        HttpServletResponse mock = mock( HttpServletResponse.class );
        try {
            when( mock.getWriter() ).thenReturn( mock( PrintWriter.class ) );
        } catch ( Exception e ) {
        }
        return mock;
    }

    private Matcher<String> hasCorrectSerialUuid() {
        return new BaseMatcher<String>() {

            public boolean matches( Object item ) {
                String uuid = (String) item;
                return SERIAL_UUID_LENGTH == uuid.length();
            }

            public void describeTo( Description description ) {
                description.appendText( "Length of serial uuid should be " + SERIAL_UUID_LENGTH );
            }
        };
    }

    private Matcher<SecurityReport> hasCorrectIpAddress() {
        return new BaseMatcher<SecurityReport>() {

            public boolean matches( Object item ) {
                SecurityReport report = (SecurityReport) item;
                return CLIENT_IP_ADDRESS.equals( report.getIpAddressOfRequestingUser() );
            }

            public void describeTo( Description description ) {
                description.appendText( "Client IP address should be " + CLIENT_IP_ADDRESS );
            }
        };
    }

    private Matcher<SecurityReport> hasCorrectTargetUrl() {
        return new BaseMatcher<SecurityReport>() {

            private final String expected = TARGET_URL + "?" + QUERY_STRING;

            public boolean matches( Object item ) {
                SecurityReport report = (SecurityReport) item;
                return expected.equals( report.getTargetUri() );
            }

            public void describeTo( Description description ) {
                description.appendText( "Target url should be " + TARGET_URL + "?" + QUERY_STRING );
            }
        };
    }

    private Matcher<SecurityReport> hasCorrectTargetUrlWithNullQueryString() {
        return new BaseMatcher<SecurityReport>() {

            public boolean matches( Object item ) {
                SecurityReport report = (SecurityReport) item;
                return TARGET_URL.equals( report.getTargetUri() );
            }

            public void describeTo( Description description ) {
                description.appendText( "Target url should be " + TARGET_URL );
            }
        };
    }

    private Matcher<SecurityReport> hasResponse( final int expected ) {
        return new BaseMatcher<SecurityReport>() {

            public boolean matches( Object item ) {
                SecurityReport report = (SecurityReport) item;
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

    private SecurityFilter createSecurityFilterWithEmptyListOfServiceManagers() {
        List<ServiceManager> serviceManagers = new ArrayList<ServiceManager>();
        return new SecurityFilter( serviceManagers, logger, loggerResponseFilterReportMock, exceptionHandler );
    }

    private SecurityFilter createSecurityFilterWithNullServiceManagers() {
        return new SecurityFilter( null, logger, loggerResponseFilterReportMock, exceptionHandler );
    }

    private SecurityFilter createSecurityFilterWithServiceManagers( ServiceManager... serviceManager )
                    throws Exception {
        List<ServiceManager> serviceManagers = createServiceManagersWithThreeServiceManagers( serviceManager );
        return new SecurityFilter( serviceManagers, logger, loggerResponseFilterReportMock, exceptionHandler );
    }

    private List<ServiceManager> createServiceManagersWithOneServiceManager( boolean isAuthorized )
                    throws Exception {
        List<ServiceManager> serviceManagers = new ArrayList<ServiceManager>();
        ServiceManager serviceManager = mockSupportedServiceManager( isAuthorized );
        serviceManagers.add( serviceManager );
        return serviceManagers;
    }

    private List<ServiceManager> createServiceManagersWithThreeServiceManagers( ServiceManager... serviceManagers ) {
        List<ServiceManager> serviceManagerList = new ArrayList<ServiceManager>();
        for ( ServiceManager serviceManager : serviceManagers ) {
            serviceManagerList.add( serviceManager );
        }
        return serviceManagerList;
    }

    private ResponseFilterReportLogger mockResponseFilterReportLogger() {
        return mock( ResponseFilterReportLogger.class );
    }

    private SecurityRequestResponseLogger mockSecurityRequestResponseLogger() {
        return mock( SecurityRequestResponseLogger.class );
    }

    private ServiceManager mockSupportedServiceManager( boolean isAuthorized )
                    throws Exception {
        ServiceManager serviceManager = mockServiceManager();
        createDoReturnsForServiceManager( isAuthorized, true, serviceManager );
        return serviceManager;
    }

    private ServiceManager mockUnsupportedServiceManager()
                    throws Exception {
        ServiceManager serviceManager = mockServiceManager();
        createDoReturnsForServiceManager( true, false, serviceManager );
        return serviceManager;
    }

    private void createDoReturnsForServiceManager( boolean isAuthorized, boolean isServiceSupported,
                                                   ServiceManager serviceManager )
                    throws Exception {
        ResponseFilterReport responseFilterReport = mockResponseFilterReport();
        createDoReturnsForServiceManager( isAuthorized, isServiceSupported, serviceManager, responseFilterReport );
    }

    private void createDoReturnsForServiceManager( boolean isAuthorized, boolean isServiceSupported,
                                                   ServiceManager serviceManager,
                                                   ResponseFilterReport responseFilterReport )
                    throws Exception {
        doReturn( isServiceSupported ).when( serviceManager ).isServiceTypeSupported( anyString(),
                                                                                      any( HttpServletRequest.class ) );
        Map<String, String[]> additionalKeyValuePairs = createAdditionalKeyValuePairs();
        AuthorizationReport authorizationReport = new AuthorizationReport( "message", isAuthorized, "url",
                        additionalKeyValuePairs );
        doReturn( authorizationReport ).when( serviceManager ).authorize( any( Authentication.class ),
                                                                          any( OwsRequest.class ) );
        OwsRequest owsRequest = mockOwsRequest();
        doReturn( owsRequest ).when( serviceManager ).parse( any( HttpServletRequest.class ) );
        doReturn( true ).when( serviceManager ).isResponseFilterEnabled( any( OwsRequest.class ) );
        doReturn( responseFilterReport ).when( serviceManager ).filterResponse( any( StatusCodeResponseBodyWrapper.class ),
                                                                                any( Authentication.class ),
                                                                                any( OwsRequest.class ) );
    }

    private ServiceManager mockServiceManager() {
        return mock( ServiceManager.class );
    }

    private ResponseFilterReport mockResponseFilterReport() {
        return mock( ResponseFilterReport.class );
    }

    private OwsRequest mockOwsRequest() {
        return mock( OwsRequest.class );
    }

    private Map<String, String[]> createAdditionalKeyValuePairs() {
        Map<String, String[]> additionalKeyValuePairs = new HashMap<String, String[]>();
        additionalKeyValuePairs.put( "additionalKey", new String[] { "additionalValue" } );
        return additionalKeyValuePairs;
    }

}
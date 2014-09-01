package org.deegree.securityproxy.wps.responsefilter.capabilities;

import static org.deegree.securityproxy.service.commons.responsefilter.AbstractResponseFilterManager.SERVICE_EXCEPTION_MSG;
import static org.deegree.securityproxy.service.commons.responsefilter.capabilities.AbstractCapabilitiesResponseFilterManager.FILTERING_NOT_REQUIRED_MESSAGE;
import static org.deegree.securityproxy.service.commons.responsefilter.capabilities.AbstractCapabilitiesResponseFilterManager.SUCCESSFUL_FILTERING_MESSAGE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.xmlmatchers.XmlMatchers.isEquivalentTo;
import static org.xmlmatchers.transform.XmlConverters.the;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.deegree.securityproxy.authentication.ows.domain.LimitedOwsServiceVersion;
import org.deegree.securityproxy.authentication.ows.raster.RasterPermission;
import org.deegree.securityproxy.filter.StatusCodeResponseBodyWrapper;
import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.request.OwsServiceVersion;
import org.deegree.securityproxy.responsefilter.logging.ResponseFilterReport;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.XmlFilter;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.XmlModificationManagerCreator;
import org.deegree.securityproxy.wps.request.WpsRequest;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class WpsCapabilitiesResponseFilterManagerTest {

    @Test
    public void testFilterResponseShouldReturnFilterReport()
                    throws Exception {
        StatusCodeResponseBodyWrapper response = mockStatusCodeResponseBodyWrapper();
        WpsRequest wpsRequest = createWpsGetCapabilitiesRequest();
        Authentication authentication = createAuthenticationWithKnownProcessIds();
        ResponseFilterReport filterReport = createFilterManagerWithMocks().filterResponse( response, wpsRequest,
                                                                                           authentication );

        assertThat( filterReport, notNullValue() );
        assertThat( filterReport.getMessage(), is( SUCCESSFUL_FILTERING_MESSAGE ) );
        assertThat( filterReport.isFiltered(), is( true ) );
    }

    @Test
    public void testFilterResponseWithFilteringNotRequiredShouldReturnFilterReport()
                    throws Exception {
        StatusCodeResponseBodyWrapper response = mockStatusCodeResponseBodyWrapper();
        WpsRequest wpsRequest = createWpsGetCapabilitiesRequest();
        Authentication authentication = createAuthenticationWithDescribeProcess();
        ResponseFilterReport filterReport = createFilterManagerWithMocks().filterResponse( response, wpsRequest,
                                                                                           authentication );

        assertThat( filterReport, notNullValue() );
        assertThat( filterReport.getMessage(), is( FILTERING_NOT_REQUIRED_MESSAGE ) );
        assertThat( filterReport.isFiltered(), is( false ) );
    }

    @Test
    public void testFilterResponseWithExceptionShouldReturnFilterReport()
                    throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockStatusCodeResponseBodyWrapper( filteredCapabilities,
                                                                                    "../service_exception.xml" );
        WpsRequest wpsRequest = createWpsGetCapabilitiesRequest();
        Authentication authentication = createAuthenticationWithKnownProcessIds();

        ResponseFilterReport filterReport = createFilterManager().filterResponse( response, wpsRequest, authentication );

        assertThat( filterReport, notNullValue() );
        assertThat( filterReport.getMessage(), is( SERVICE_EXCEPTION_MSG ) );
        assertThat( filterReport.isFiltered(), is( false ) );
    }

    @Test
    public void testFilterResponseWithAllPermissionShouldNotFilterResponse()
                    throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockStatusCodeResponseBodyWrapper( filteredCapabilities,
                                                                                    "wps_1_0_0.xml" );
        WpsRequest wpsRequest = createWpsGetCapabilitiesRequest();
        Authentication authentication = createAuthenticationWithAllProcessIds();
        createFilterManager().filterResponse( response, wpsRequest, authentication );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "wps_1_0_0.xml" ) ) );
    }

    @Test
    public void testFilterResponseWithApplicablePermissionShouldFilterResponse()
                    throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockStatusCodeResponseBodyWrapper( filteredCapabilities,
                                                                                    "wps_1_0_0.xml" );
        WpsRequest wpsRequest = createWpsGetCapabilitiesRequest();
        Authentication authentication = createAuthenticationWithKnownProcessIds();
        createFilterManager().filterResponse( response, wpsRequest, authentication );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "wps_1_0_0-Filtered.xml" ) ) );
    }

    @Ignore("Capabilities without ProcessOffering section of without processes are not allowed!")
    @Test
    public void testFilterResponseWithoutApplicablePermissionShouldFilterResponseCompletly()
                    throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockStatusCodeResponseBodyWrapper( filteredCapabilities,
                                                                                    "wps_1_0_0.xml" );
        WpsRequest wpsRequest = createWpsGetCapabilitiesRequest();
        Authentication authentication = createAuthenticationWithoutKnownProcessIds();
        createFilterManager().filterResponse( response, wpsRequest, authentication );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "wps_1_0_0-FilteredComplete.xml" ) ) );
    }

    @Test
    public void testFilterResponseWithoutGetCoveragePermissionShouldNotFilterResponse()
                    throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockStatusCodeResponseBodyWrapper( filteredCapabilities,
                                                                                    "wps_1_0_0.xml" );
        WpsRequest wpsRequest = createWpsGetCapabilitiesRequest();
        Authentication authentication = createAuthenticationWithDescribeProcess();
        createFilterManager().filterResponse( response, wpsRequest, authentication );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "wps_1_0_0.xml" ) ) );
    }

    @Test
    public void testFilterResponseWithExceptionShouldReturnExceptionResponse()
                    throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockStatusCodeResponseBodyWrapper( filteredCapabilities,
                                                                                    "../service_exception.xml" );
        Authentication mockAuthentication = createAuthenticationWithKnownProcessIds();
        createFilterManager().filterResponse( response, createWpsExecuteRequest(), mockAuthentication );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "../service_exception.xml" ) ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFilterResponseWithUnsuppportedVersion()
                    throws Exception {
        StatusCodeResponseBodyWrapper response = mockStatusCodeResponseBodyWrapper();
        WpsRequest wpsRequest = createWpsGetCapabilitiesRequestWithVersion130();
        Authentication authentication = createAuthenticationWithKnownProcessIds();
        createFilterManagerWithMocks().filterResponse( response, wpsRequest, authentication );
    }

    @Test
    public void testIsCorrectServiceTypeFromWpsExecuteRequest()
                    throws Exception {
        WpsCapabilitiesResponseFilterManager filterManager = createFilterManagerWithMocks();
        boolean correctServiceType = filterManager.isCorrectServiceType( createWpsExecuteRequest() );

        assertThat( correctServiceType, is( true ) );
    }

    @Test
    public void testIsCorrectServiceTypeFromMockedRequest()
                    throws Exception {
        WpsCapabilitiesResponseFilterManager filterManager = createFilterManagerWithMocks();
        boolean correctServiceType = filterManager.isCorrectServiceType( mock( OwsRequest.class ) );

        assertThat( correctServiceType, is( false ) );
    }

    @Test
    public void testIsCorrectRequestParameterFromWpsCapabilitiesRequest()
                    throws Exception {
        WpsCapabilitiesResponseFilterManager filterManager = createFilterManagerWithMocks();
        boolean correctServiceType = filterManager.isCorrectRequestParameter( createWpsGetCapabilitiesRequest() );

        assertThat( correctServiceType, is( true ) );
    }

    @Test
    public void testIsCorrectRequestParameterFromWpsExecuteRequest()
                    throws Exception {
        WpsCapabilitiesResponseFilterManager filterManager = createFilterManagerWithMocks();
        boolean correctServiceType = filterManager.isCorrectRequestParameter( createWpsExecuteRequest() );

        assertThat( correctServiceType, is( false ) );
    }

    @Test
    public void testIsCorrectRequestParameterFromMockedRequestRequest()
                    throws Exception {
        WpsCapabilitiesResponseFilterManager filterManager = createFilterManagerWithMocks();
        boolean correctServiceType = filterManager.isCorrectRequestParameter( mock( OwsRequest.class ) );

        assertThat( correctServiceType, is( false ) );
    }

    private WpsCapabilitiesResponseFilterManager createFilterManagerWithMocks() {
        XmlFilter capabilitiesFilter = mock( XmlFilter.class );
        XmlModificationManagerCreator xmlModificationManagerCreator = new WpsCapabilitiesModificationManagerCreator();
        return new WpsCapabilitiesResponseFilterManager( capabilitiesFilter, xmlModificationManagerCreator );
    }

    private WpsCapabilitiesResponseFilterManager createFilterManager() {
        XmlFilter capabilitiesFilter = new XmlFilter();
        XmlModificationManagerCreator xmlModificationManagerCreator = new WpsCapabilitiesModificationManagerCreator();
        return new WpsCapabilitiesResponseFilterManager( capabilitiesFilter, xmlModificationManagerCreator );
    }

    private WpsRequest createWpsExecuteRequest() {
        return new WpsRequest( "Execute", new OwsServiceVersion( "1.0.0" ), "WPS" );
    }

    private WpsRequest createWpsGetCapabilitiesRequest() {
        return new WpsRequest( "GetCapabilities", new OwsServiceVersion( "1.0.0" ), "WPS" );
    }

    private WpsRequest createWpsGetCapabilitiesRequestWithVersion130() {
        return new WpsRequest( "GetCapabilities", new OwsServiceVersion( "1.3.0" ), "WPS" );
    }

    private StatusCodeResponseBodyWrapper mockStatusCodeResponseBodyWrapper()
                    throws IOException {
        StatusCodeResponseBodyWrapper mockedServletResponse = mock( StatusCodeResponseBodyWrapper.class );
        when( mockedServletResponse.getStatus() ).thenReturn( 200 );
        when( mockedServletResponse.getBufferedStream() ).thenReturn( new ByteArrayInputStream( new byte[] {} ) );
        when( mockedServletResponse.getOutputStream() ).thenReturn( mock( ServletOutputStream.class ) );
        when( mockedServletResponse.getRealOutputStream() ).thenReturn( mock( ServletOutputStream.class ) );
        return mockedServletResponse;
    }

    private StatusCodeResponseBodyWrapper mockStatusCodeResponseBodyWrapper( ByteArrayOutputStream filteredStream,
                                                                             String originalXmlFileName )
                    throws IOException {
        StatusCodeResponseBodyWrapper mockedServletResponse = mock( StatusCodeResponseBodyWrapper.class );
        when( mockedServletResponse.getStatus() ).thenReturn( 200 );
        when( mockedServletResponse.getBufferedStream() ).thenReturn( retrieveResourceAsStream( originalXmlFileName ),
                                                                      retrieveResourceAsStream( originalXmlFileName ) );
        when( mockedServletResponse.getRealOutputStream() ).thenReturn( createStream( filteredStream ) );
        doCallRealMethod().when( mockedServletResponse ).copyBufferedStreamToRealStream();
        return mockedServletResponse;
    }

    private Authentication createAuthenticationWithDescribeProcess() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add( createRasterPermission( "DescribeProcess", "st_vectormean" ) );
        return mockAuthentication( authorities );
    }

    private Authentication createAuthenticationWithoutKnownProcessIds() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add( createRasterPermission( "Execute", "UNKNOWN" ) );
        return mockAuthentication( authorities );
    }

    private Authentication createAuthenticationWithKnownProcessIds() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add( createRasterPermission( "Execute", "st_vectormean" ) );
        authorities.add( createRasterPermission( "Execute", "Union" ) );
        return mockAuthentication( authorities );
    }

    private Authentication createAuthenticationWithAllProcessIds() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add( createRasterPermission( "Execute", "st_snappoints" ) );
        authorities.add( createRasterPermission( "Execute", "st_groupnearfeatures" ) );
        authorities.add( createRasterPermission( "Execute", "st_vectormean" ) );
        authorities.add( createRasterPermission( "Execute", "st_boundingbox" ) );
        authorities.add( createRasterPermission( "Execute", "Union" ) );
        return mockAuthentication( authorities );
    }

    private Authentication mockAuthentication( Collection<? extends GrantedAuthority> authorities ) {
        Authentication authenticationMock = mock( Authentication.class );
        doReturn( authorities ).when( authenticationMock ).getAuthorities();
        return authenticationMock;
    }

    private RasterPermission createRasterPermission( String operationType, String processId ) {
        return new RasterPermission( "wps", operationType, new LimitedOwsServiceVersion( "<= 1.0.0" ), processId,
                        "serviceName", "internalServiceUrl", null );
    }

    private InputStream retrieveResourceAsStream( String originalXmlFileName ) {
        return WpsCapabilitiesResponseFilterManagerTest.class.getResourceAsStream( originalXmlFileName );
    }

    private Source expectedXml( String expectedFile ) {
        return new StreamSource( retrieveResourceAsStream( expectedFile ) );
    }

    private Source asXml( ByteArrayOutputStream bufferingStream ) {
        byte[] byteArray = bufferingStream.toByteArray();
        return the( new StreamSource( new ByteArrayInputStream( byteArray ) ) );
    }

    private ServletOutputStream createStream( final ByteArrayOutputStream bufferingStream ) {
        ServletOutputStream stream = new ServletOutputStream() {
            @Override
            public void write( int b )
                            throws IOException {
                bufferingStream.write( b );
            }
        };
        return stream;
    }

}
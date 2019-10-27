package org.deegree.securityproxy.wcs.responsefilter.capabilities;

import static org.deegree.securityproxy.service.commons.responsefilter.AbstractResponseFilterManager.SERVICE_EXCEPTION_MSG;
import static org.deegree.securityproxy.service.commons.responsefilter.capabilities.AbstractCapabilitiesResponseFilterManager.FILTERING_NOT_REQUIRED_MESSAGE;
import static org.deegree.securityproxy.service.commons.responsefilter.capabilities.AbstractCapabilitiesResponseFilterManager.SUCCESSFUL_FILTERING_MESSAGE;
import static org.deegree.securityproxy.wcs.request.WcsRequestParser.GETCAPABILITIES;
import static org.deegree.securityproxy.wcs.request.WcsRequestParser.GETCOVERAGE;
import static org.deegree.securityproxy.wcs.request.WcsRequestParser.VERSION_100;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.xmlmatchers.XmlMatchers.isEquivalentTo;
import static org.xmlmatchers.transform.XmlConverters.the;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.deegree.securityproxy.authentication.ows.domain.LimitedOwsServiceVersion;
import org.deegree.securityproxy.authentication.ows.raster.OwsPermission;
import org.deegree.securityproxy.filter.StatusCodeResponseBodyWrapper;
import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.responsefilter.logging.ResponseFilterReport;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.XmlFilter;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.XmlModificationManager;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.XmlModificationManagerCreator;
import org.deegree.securityproxy.wcs.request.WcsRequest;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class WcsCapabilitiesResponseFilterManagerTest {

    private static final String SERVICE_NAME = "serviceName";

    private static final String COVERAGE_NAME = "213_9999";

    private final XmlFilter mockedCapabilitiesFilter = mock( XmlFilter.class );

    private final XmlModificationManagerCreator modificationManagerCreator = new WcsCapabilitiesModificationManagerCreator();

    private final XmlModificationManagerCreator modificationManagerCreatorWithDcpUrls = new WcsCapabilitiesModificationManagerCreator(
                    "htt://getUrl", "http://postUrl" );

    private final WcsCapabilitiesResponseFilterManager wcsCapabilitiesResponseFilterManagerWithMock = new WcsCapabilitiesResponseFilterManager(
                    mockedCapabilitiesFilter, modificationManagerCreator );

    private final XmlFilter capabilitiesFilter = new XmlFilter();

    private final WcsCapabilitiesResponseFilterManager wcsCapabilitiesResponseFilterManager = new WcsCapabilitiesResponseFilterManager(
                    capabilitiesFilter, modificationManagerCreator );

    private final WcsCapabilitiesResponseFilterManager wcsCapabilitiesResponseFilterManagerWithDcpUrls = new WcsCapabilitiesResponseFilterManager(
                    capabilitiesFilter, modificationManagerCreatorWithDcpUrls );

    @Test
    public void testFilterResponseShouldCallFilterCapabilities()
                    throws Exception {
        XmlFilter capabilitiesFilter = mockCapabilitiesFilter();
        WcsCapabilitiesResponseFilterManager wcsCapabilitiesResponseFilterManager = new WcsCapabilitiesResponseFilterManager(
                        capabilitiesFilter, modificationManagerCreator );
        StatusCodeResponseBodyWrapper response = mockStatusCodeResponseBodyWrapper();
        WcsRequest wcsRequest = createWcsGetCapabilitiesRequest();
        Authentication authentication = createAuthenticationWithKnownCoverage();
        wcsCapabilitiesResponseFilterManager.filterResponse( response, wcsRequest, authentication );

        verify( capabilitiesFilter ).filterXml( eq( response ), any( XmlModificationManager.class ) );
    }

    @Test
    public void testFilterResponseShouldReturnFilterReport()
                    throws Exception {
        StatusCodeResponseBodyWrapper response = mockStatusCodeResponseBodyWrapper();
        WcsRequest wcsRequest = createWcsGetCapabilitiesRequest();
        Authentication authentication = createAuthenticationWithKnownCoverage();
        ResponseFilterReport filterReport = wcsCapabilitiesResponseFilterManagerWithMock.filterResponse( response,
                                                                                                         wcsRequest,
                                                                                                         authentication );

        assertThat( filterReport, notNullValue() );
        assertThat( filterReport.getMessage(), is( SUCCESSFUL_FILTERING_MESSAGE ) );
        assertThat( filterReport.isFiltered(), is( true ) );
    }

    @Test
    public void testFilterResponseWithFilteringNotRequiredShouldReturnFilterReport()
                    throws Exception {
        StatusCodeResponseBodyWrapper response = mockStatusCodeResponseBodyWrapper();
        WcsRequest wcsRequest = createWcsGetCapabilitiesRequest();
        Authentication authentication = createAuthenticationWithDescribeCoverage();
        ResponseFilterReport filterReport = wcsCapabilitiesResponseFilterManagerWithMock.filterResponse( response,
                                                                                                         wcsRequest,
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
        WcsRequest wcsRequest = createWcsGetCapabilitiesRequest();
        Authentication authentication = createAuthenticationWithKnownCoverage();

        ResponseFilterReport filterReport = wcsCapabilitiesResponseFilterManager.filterResponse( response, wcsRequest,
                                                                                                 authentication );

        assertThat( filterReport, notNullValue() );
        assertThat( filterReport.getMessage(), is( SERVICE_EXCEPTION_MSG ) );
        assertThat( filterReport.isFiltered(), is( false ) );
    }

    @Test
    public void testFilterResponseWithAllPermissionShouldNotFilterResponse()
                    throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockStatusCodeResponseBodyWrapper( filteredCapabilities,
                                                                                    "wcs_1_0_0.xml" );
        WcsRequest wcsRequest = createWcsGetCapabilitiesRequest();
        Authentication authentication = createAuthenticationWithAllCoverages();
        wcsCapabilitiesResponseFilterManager.filterResponse( response, wcsRequest, authentication );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "wcs_1_0_0.xml" ) ) );
    }

    @Test
    public void testFilterResponseWithApplicablePermissionShouldFilterResponse()
                    throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockStatusCodeResponseBodyWrapper( filteredCapabilities,
                                                                                    "wcs_1_0_0.xml" );
        WcsRequest wcsRequest = createWcsGetCapabilitiesRequest();
        Authentication authentication = createAuthenticationWithKnownCoverage();
        wcsCapabilitiesResponseFilterManager.filterResponse( response, wcsRequest, authentication );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "wcs_1_0_0-Filtered.xml" ) ) );
    }

    @Test
    public void testFilterResponseWithoutApplicablePermissionShouldFilterResponseCompletly()
                    throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockStatusCodeResponseBodyWrapper( filteredCapabilities,
                                                                                    "wcs_1_0_0.xml" );
        WcsRequest wcsRequest = createWcsGetCapabilitiesRequest();
        Authentication authentication = createAuthenticationWithUnknownCoverage();
        wcsCapabilitiesResponseFilterManager.filterResponse( response, wcsRequest, authentication );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "wcs_1_0_0-FilteredComplete.xml" ) ) );
    }

    @Test
    public void testFilterResponseWithoutGetCoveragePermissionShouldNotFilterResponse()
                    throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockStatusCodeResponseBodyWrapper( filteredCapabilities,
                                                                                    "wcs_1_0_0.xml" );
        WcsRequest wcsRequest = createWcsGetCapabilitiesRequest();
        Authentication authentication = createAuthenticationWithDescribeCoverage();
        wcsCapabilitiesResponseFilterManager.filterResponse( response, wcsRequest, authentication );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "wcs_1_0_0.xml" ) ) );
    }

    @Test
    public void testFilterResponseWithDcpUrlsShouldBeFiltered()
                    throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockStatusCodeResponseBodyWrapper( filteredCapabilities,
                                                                                    "wcs_1_0_0.xml" );
        WcsRequest wcsRequest = createWcsGetCapabilitiesRequest();
        Authentication authentication = createAuthenticationWithDescribeCoverage();
        wcsCapabilitiesResponseFilterManagerWithDcpUrls.filterResponse( response, wcsRequest, authentication );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "wcs_1_0_0_ReplacedDcpUrls.xml" ) ) );
    }

    @Test
    public void testFilterResponseWithExceptionShouldReturnExceptionResponse()
                    throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockStatusCodeResponseBodyWrapper( filteredCapabilities,
                                                                                    "../service_exception.xml" );
        Authentication mockAuthentication = createAuthenticationWithKnownCoverage();
        wcsCapabilitiesResponseFilterManager.filterResponse( response, createWcsGetCoverageRequest(),
                                                             mockAuthentication );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "../service_exception.xml" ) ) );
    }

    @Test
    public void testCanBeFilteredWithWcsGetCapabilitiesRequestRequestShouldReturnTrue() {
        boolean canBeFiltered = wcsCapabilitiesResponseFilterManagerWithMock.canBeFiltered( createWcsGetCapabilitiesRequest() );

        assertThat( canBeFiltered, is( true ) );
    }

    @Test
    public void testCanBeFilteredWithWcsGetCoverageRequestShouldReturnFalse() {
        boolean canBeFiltered = wcsCapabilitiesResponseFilterManagerWithMock.canBeFiltered( createWcsGetCoverageRequest() );

        assertThat( canBeFiltered, is( false ) );
    }

    @Test
    public void testCanBeFilteredWithNotWcsRequestRequestShouldReturnFalse() {
        boolean canBeFiltered = wcsCapabilitiesResponseFilterManagerWithMock.canBeFiltered( mockOwsRequest() );

        assertThat( canBeFiltered, is( false ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCanBeFilteredWithNullRequestShouldThrowIllegalArgumentException() {
        wcsCapabilitiesResponseFilterManagerWithMock.canBeFiltered( null );
    }

    private OwsRequest mockOwsRequest() {
        return mock( OwsRequest.class );
    }

    private WcsRequest createWcsGetCapabilitiesRequest() {
        return new WcsRequest( GETCAPABILITIES, VERSION_100, SERVICE_NAME );
    }

    private WcsRequest createWcsGetCoverageRequest() {
        return new WcsRequest( GETCOVERAGE, VERSION_100, Collections.singletonList( COVERAGE_NAME ), SERVICE_NAME );
    }

    private Authentication createAuthenticationWithKnownCoverage() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add( createRasterPermission( "GetCoverage", COVERAGE_NAME ) );
        return mockAuthentication( authorities );
    }

    private Authentication createAuthenticationWithAllCoverages() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add( createRasterPermission( "GetCoverage", COVERAGE_NAME ) );
        authorities.add( createRasterPermission( "GetCoverage", "123_6788" ) );
        authorities.add( createRasterPermission( "GetCoverage", "testdata_raw" ) );
        authorities.add( createRasterPermission( "GetCoverage", "567_8765" ) );
        return mockAuthentication( authorities );
    }

    private Authentication createAuthenticationWithUnknownCoverage() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add( createRasterPermission( "GetCoverage", "notFiltered" ) );
        return mockAuthentication( authorities );
    }

    private Authentication createAuthenticationWithDescribeCoverage() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add( createRasterPermission( "DescribeCoverage", COVERAGE_NAME ) );
        return mockAuthentication( authorities );
    }

    private OwsPermission createRasterPermission( String operationType, String coverageName ) {
        return new OwsPermission( "wcs", operationType, new LimitedOwsServiceVersion( "<= 1.1.0" ), coverageName,
                        "serviceName", "internalServiceUrl", null );
    }

    private Authentication mockAuthentication( Collection<? extends GrantedAuthority> authorities ) {
        Authentication authenticationMock = mock( Authentication.class );
        doReturn( authorities ).when( authenticationMock ).getAuthorities();
        return authenticationMock;
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

    private InputStream retrieveResourceAsStream( String originalXmlFileName ) {
        return WcsCapabilitiesResponseFilterManagerTest.class.getResourceAsStream( originalXmlFileName );
    }

    private Source expectedXml( String expectedFile ) {
        return new StreamSource( retrieveResourceAsStream( expectedFile ) );
    }

    private Source asXml( ByteArrayOutputStream bufferingStream ) {
        return the( new StreamSource( new ByteArrayInputStream( bufferingStream.toByteArray() ) ) );
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

    private XmlFilter mockCapabilitiesFilter() {
        return mock( XmlFilter.class );
    }

}
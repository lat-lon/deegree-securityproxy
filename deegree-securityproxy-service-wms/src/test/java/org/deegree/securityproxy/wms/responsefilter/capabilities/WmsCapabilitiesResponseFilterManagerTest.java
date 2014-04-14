package org.deegree.securityproxy.wms.responsefilter.capabilities;

import static org.deegree.securityproxy.wms.request.WmsRequestParser.GETCAPABILITIES;
import static org.deegree.securityproxy.wms.request.WmsRequestParser.GETFEATUREINFO;
import static org.deegree.securityproxy.wms.request.WmsRequestParser.GETMAP;
import static org.deegree.securityproxy.wms.request.WmsRequestParser.VERSION_130;
import static org.deegree.securityproxy.wms.request.WmsRequestParser.WMS_SERVICE;
import static org.hamcrest.CoreMatchers.is;
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
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.CapabilitiesFilter;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.DecisionMakerCreator;
import org.deegree.securityproxy.wms.request.WmsRequest;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class WmsCapabilitiesResponseFilterManagerTest {

    private WmsCapabilitiesResponseFilterManager filterManager = createFilterManager();

    @Test
    public void testFilterResponseWithAllPermissionShouldNotFilterResponse()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockStatusCodeResponseBodyWrapper( filteredCapabilities,
                                                                                    "wms_1_3_0.xml" );
        WmsRequest wmsRequest = createWms130CapabilitiesRequest();
        Authentication authentication = createAuthenticationAllLayersGetMap();
        filterManager.filterResponse( response, wmsRequest, authentication );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "wms_1_3_0.xml" ) ) );
    }

    @Test
    public void testFilterResponseWithGetMapPermissionShouldFilterResponse()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockStatusCodeResponseBodyWrapper( filteredCapabilities,
                                                                                    "wms_1_3_0.xml" );
        WmsRequest wmsRequest = createWms130CapabilitiesRequest();
        Authentication authentication = createAuthenticationTwoLayersGetMap();
        filterManager.filterResponse( response, wmsRequest, authentication );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "wms_1_3_0-Filtered.xml" ) ) );
    }

    @Test
    public void testFilterResponseWithGetMapAndGetFeatureinfoPermissionShouldFilterResponse()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockStatusCodeResponseBodyWrapper( filteredCapabilities,
                                                                                    "wms_1_3_0.xml" );
        WmsRequest wmsRequest = createWms130CapabilitiesRequest();
        Authentication authentication = createAuthenticationOneLayerGetMapOneLayerGetFeatureInfo();
        filterManager.filterResponse( response, wmsRequest, authentication );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "wms_1_3_0-Filtered.xml" ) ) );
    }

    @Test
    public void testFilterResponseWithoutPermissionOnSubLayerShouldFilterResponse()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockStatusCodeResponseBodyWrapper( filteredCapabilities,
                                                                                    "wms_1_3_0.xml" );
        WmsRequest wmsRequest = createWms130CapabilitiesRequest();
        Authentication authentication = createAuthenticationOneSubLayerNotGrantedGetMap();
        filterManager.filterResponse( response, wmsRequest, authentication );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "wms_1_3_0-FilteredSubLayers.xml" ) ) );
    }

    @Test
    public void testFilterResponseWithUnknownGetMapLayerPermissionsShouldFilterAllLayers()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockStatusCodeResponseBodyWrapper( filteredCapabilities,
                                                                                    "wms_1_3_0.xml" );
        WmsRequest wmsRequest = createWms130CapabilitiesRequest();
        Authentication authentication = createAuthenticationUnknownLayerGetMap();
        filterManager.filterResponse( response, wmsRequest, authentication );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "wms_1_3_0-FilteredComplete.xml" ) ) );
    }

    @Test
    public void testFilterResponseWithGetCapabilitiesPermissionsShouldFilterAllLayers()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockStatusCodeResponseBodyWrapper( filteredCapabilities,
                                                                                    "wms_1_3_0.xml" );
        WmsRequest wmsRequest = createWms130CapabilitiesRequest();
        Authentication authentication = createAuthenticationGetCapabilities();
        filterManager.filterResponse( response, wmsRequest, authentication );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "wms_1_3_0-FilteredComplete.xml" ) ) );
    }

    @Test
    public void testIsCorrectRequestType()
                            throws Exception {
        boolean isCorrectRequestType = filterManager.isCorrectServiceType( createWms130CapabilitiesRequest() );

        assertThat( isCorrectRequestType, is( true ) );
    }

    @Test
    public void testIsCorrectRequestTypeWithUnknownRequestShouldReturnFalse()
                            throws Exception {
        boolean isCorrectRequestType = filterManager.isCorrectServiceType( mockUnknownRequest() );

        assertThat( isCorrectRequestType, is( false ) );
    }

    @Test
    public void testIsGetCapabilitiesRequestRequestType()
                            throws Exception {
        boolean isCorrectRequestType = filterManager.isCorrectRequestParameter( createWms130CapabilitiesRequest() );

        assertThat( isCorrectRequestType, is( true ) );
    }

    @Test
    public void testIsGetCapabilitiesRequestRequestTypeWithGetMapRequestShouldReturnFalse()
                            throws Exception {
        boolean isCorrectRequestType = filterManager.isCorrectRequestParameter( createWms130GetMapRequest() );

        assertThat( isCorrectRequestType, is( false ) );
    }

    private WmsCapabilitiesResponseFilterManager createFilterManager() {
        CapabilitiesFilter capabilitiesFilter = new CapabilitiesFilter();
        DecisionMakerCreator decisionMakerCreator = new WmsDecisionMakerCreator();
        return new WmsCapabilitiesResponseFilterManager( capabilitiesFilter, decisionMakerCreator );
    }

    private WmsRequest createWms130CapabilitiesRequest() {
        return new WmsRequest( GETCAPABILITIES, VERSION_130, "serviceName" );
    }

    private WmsRequest createWms130GetMapRequest() {
        return new WmsRequest( GETMAP, VERSION_130, "serviceName" );
    }

    private OwsRequest mockUnknownRequest() {
        OwsRequest request = mock( OwsRequest.class );
        when( request.getServiceType() ).thenReturn( "Unknown" );
        return request;
    }

    private Authentication createAuthenticationAllLayersGetMap() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add( createRasterPermission( GETMAP, "testdata_view" ) );
        authorities.add( createRasterPermission( GETMAP, "sub_testdata_view1" ) );
        authorities.add( createRasterPermission( GETMAP, "sub_testdata_view2" ) );
        authorities.add( createRasterPermission( GETMAP, "footprints" ) );
        authorities.add( createRasterPermission( GETMAP, "view" ) );
        authorities.add( createRasterPermission( GETMAP, "testdata_footprints" ) );
        authorities.add( createRasterPermission( GETMAP, "abcde_view" ) );
        authorities.add( createRasterPermission( GETMAP, "abcde_footprints" ) );
        return mockAuthentication( authorities );
    }

    private Authentication createAuthenticationTwoLayersGetMap() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add( createRasterPermission( GETMAP, "abcde_view" ) );
        authorities.add( createRasterPermission( GETMAP, "abcde_footprints" ) );
        return mockAuthentication( authorities );
    }

    private Authentication createAuthenticationOneSubLayerNotGrantedGetMap() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add( createRasterPermission( GETMAP, "testdata_view" ) );
        authorities.add( createRasterPermission( GETMAP, "sub_testdata_view1" ) );
        authorities.add( createRasterPermission( GETMAP, "footprints" ) );
        authorities.add( createRasterPermission( GETMAP, "view" ) );
        authorities.add( createRasterPermission( GETMAP, "testdata_footprints" ) );
        authorities.add( createRasterPermission( GETMAP, "abcde_view" ) );
        authorities.add( createRasterPermission( GETMAP, "abcde_footprints" ) );
        return mockAuthentication( authorities );
    }

    private Authentication createAuthenticationUnknownLayerGetMap() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add( createRasterPermission( GETMAP, "not_a_known_wms_layer" ) );
        return mockAuthentication( authorities );
    }

    private Authentication createAuthenticationGetCapabilities() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add( createRasterPermission( GETCAPABILITIES, null ) );
        return mockAuthentication( authorities );
    }

    private Authentication createAuthenticationOneLayerGetMapOneLayerGetFeatureInfo() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add( createRasterPermission( GETMAP, "abcde_view" ) );
        authorities.add( createRasterPermission( GETFEATUREINFO, "abcde_footprints" ) );
        return mockAuthentication( authorities );
    }

    private RasterPermission createRasterPermission( String operationType, String layerName ) {
        return new RasterPermission( WMS_SERVICE, operationType, new LimitedOwsServiceVersion( "<= 1.3.0" ), layerName,
                                     "serviceName", "internalServiceUrl", null );
    }

    private Authentication mockAuthentication( Collection<? extends GrantedAuthority> authorities ) {
        Authentication authenticationMock = mock( Authentication.class );
        doReturn( authorities ).when( authenticationMock ).getAuthorities();
        return authenticationMock;
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

    private Source expectedXml( String expectedFile ) {
        return new StreamSource( retrieveResourceAsStream( expectedFile ) );
    }

    private InputStream retrieveResourceAsStream( String originalXmlFileName ) {
        return WmsCapabilitiesResponseFilterManagerTest.class.getResourceAsStream( originalXmlFileName );
    }

    private Source asXml( ByteArrayOutputStream bufferingStream ) {
        System.out.println( bufferingStream );
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
}
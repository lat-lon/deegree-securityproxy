//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2014 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact information:

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/
package org.deegree.securityproxy.wms.request;

import static org.deegree.securityproxy.wms.request.WmsRequestParser.GETCAPABILITIES;
import static org.deegree.securityproxy.wms.request.WmsRequestParser.GETFEATUREINFO;
import static org.deegree.securityproxy.wms.request.WmsRequestParser.GETMAP;
import static org.deegree.securityproxy.wms.request.WmsRequestParser.VERSION_130;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.deegree.securityproxy.request.UnsupportedRequestTypeException;
import org.junit.Test;
import org.mockito.Mockito;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Tests for {@link WmsRequestParser}.
 * 
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * @version $Revision: $, $Date: $
 */
public class WmsRequestParserTest {

    private static final String WIDTH_PARAM = "WIDTH";

    private static final String HEIGHT_PARAM = "HEIGHT";

    private static final String FORMAT_PARAM = "FORMAT";

    private static final String CRS_PARAM = "CRS";

    private static final String BBOX_PARAM = "BBOX";

    private static final String LAYERS_PARAM = "LAYERS";

    private static final String STYLES_PARAM = "STYLES";

    private static final String REQUEST_PARAM = "REQUEST";

    private static final String VERSION_PARAM = "VERSION";

    private static final String SERVICE_PARAM = "SERVICE";

    private static final String QUERY_LAYERS_PARAM = "QUERY_LAYERS";

    private static final String INFO_FORMAT_PARAM = "INFO_FORMAT";

    private static final String I_PARAM = "I";

    private static final String J_PARAM = "J";

    private static final String LAYER_NAME = "layerName";

    private static final String LAYER_NAME_2 = "layerName";

    private static final String STYLES_NAME = "stylesName";

    private static final String SERVICE_NAME = "serviceName";

    private static final String SERVICE_NAME_WITH_PATH = "path/" + SERVICE_NAME;

    private static final String CRS_NAME = "EPSG:4326";

    private static final String BBOX_NAME = "-89.67,20.25,-89.67,20.44";

    private static final Envelope BBOX = new Envelope( -89.67, -89.67, 20.25, 20.44 );

    private static final String WIDTH_NAME = "50";

    private static final String HEIGHT_NAME = "50";

    private static final String FORMAT_NAME = "formatName";

    private static final String QUERY_LAYER_NAME = "queryLayerName";

    private static final String INFO_FORMAT_NAME = "infoFormatName";

    private static final String I_NAME = "50";

    private static final String J_NAME = "50";

    private final WmsRequestParser parser = new WmsRequestParser();

    /* Tests for valid requests for WMS GetCapabilities */
    @Test
    public void testParseFromGetCapabilitiesRequestShouldIgnoreLayerNames()
                            throws UnsupportedRequestTypeException {
        HttpServletRequest request = mockWmsGetCapabilitiesRequest();
        WmsRequest wmsRequest = (WmsRequest) parser.parse( request );
        assertThat( wmsRequest.getLayerNames().isEmpty(), is( true ) );
    }

    @Test
    public void testParseFromGetCapabilitiesRequestShouldParseOperationTypeAndServiceVersionAndServiceName()
                            throws UnsupportedRequestTypeException {
        HttpServletRequest request = mockWmsGetCapabilitiesRequest();
        WmsRequest wmsRequest = (WmsRequest) parser.parse( request );
        assertThat( wmsRequest.getOperationType(), is( GETCAPABILITIES ) );
        assertThat( wmsRequest.getServiceVersion(), is( VERSION_130 ) );
        assertThat( wmsRequest.getServiceName(), is( SERVICE_NAME ) );
    }

    @Test
    public void testParseFromGetCapabilitiesRequestWithExtendedPathShouldParseServiceName()
                            throws UnsupportedRequestTypeException {
        HttpServletRequest request = mockWmsGetCapabilitiesRequestWithExtendedPath();
        WmsRequest wmsRequest = (WmsRequest) parser.parse( request );
        assertThat( wmsRequest.getOperationType(), is( GETCAPABILITIES ) );
        assertThat( wmsRequest.getServiceVersion(), is( VERSION_130 ) );
        assertThat( wmsRequest.getServiceName(), is( SERVICE_NAME ) );
    }

    /* Test for valid requests for WMS GetFeatureInfo */
    @Test
    public void testParseFromGetFeatureInfoRequestShouldParseLayersAndOperationTypeAndServiceVersionAndServiceName()
                            throws UnsupportedRequestTypeException {
        HttpServletRequest request = mockWmsGetFeatureInfoRequest();
        WmsRequest wmsRequest = (WmsRequest) parser.parse( request );
        List<String> layerNames = wmsRequest.getLayerNames();
        List<String> queryLayerNames = wmsRequest.getQueryLayerNames();
        assertThat( layerNames.size(), is( 2 ) );
        assertThat( layerNames.get( 0 ), is( LAYER_NAME ) );
        assertThat( layerNames.get( 1 ), is( LAYER_NAME ) );
        assertThat( queryLayerNames.get( 0 ), is( QUERY_LAYER_NAME ) );
        assertThat( queryLayerNames.get( 1 ), is( QUERY_LAYER_NAME ) );
        assertThat( queryLayerNames.size(), is( 2 ) );
        assertThat( wmsRequest.getOperationType(), is( GETFEATUREINFO ) );
        assertThat( wmsRequest.getServiceVersion(), is( VERSION_130 ) );
        assertThat( wmsRequest.getServiceName(), is( SERVICE_NAME ) );
    }

    /* Test for valid requests for WMS GetMap */
    @Test
    public void testParseFromGetMapRequestShouldParseLayerAndOperationTypeAndServiceVersionAndServiceName()
                            throws UnsupportedRequestTypeException {
        HttpServletRequest request = mockWmsGetMapRequest();
        WmsRequest wmsRequest = (WmsRequest) parser.parse( request );
        List<String> layerNames = wmsRequest.getLayerNames();
        assertThat( layerNames.size(), is( 1 ) );
        assertThat( layerNames.get( 0 ), is( LAYER_NAME ) );
        assertThat( wmsRequest.getOperationType(), is( GETMAP ) );
        assertThat( wmsRequest.getServiceVersion(), is( VERSION_130 ) );
        assertThat( wmsRequest.getServiceName(), is( SERVICE_NAME ) );
    }

    @Test
    public void testParseFromGetMapRequestWithTwoLayers()
                            throws UnsupportedRequestTypeException {
        HttpServletRequest request = mockWmsGetMapRequestTwoLayerParameters();
        WmsRequest wmsRequest = (WmsRequest) parser.parse( request );
        List<String> layerNames = wmsRequest.getLayerNames();
        assertThat( layerNames.size(), is( 2 ) );
        assertThat( layerNames, hasItem( LAYER_NAME ) );
        assertThat( layerNames, hasItem( LAYER_NAME_2 ) );
    }

    @Test
    public void testParseFromGetMapRequestMultipleLayers()
                            throws UnsupportedRequestTypeException {
        HttpServletRequest request = mockWmsGetMapRequestMultipleLayerParameter();
        WmsRequest wmsRequest = (WmsRequest) parser.parse( request );
        List<String> layerNames = wmsRequest.getLayerNames();
        assertThat( layerNames.size(), is( 2 ) );
        assertThat( layerNames, hasItem( LAYER_NAME ) );
        assertThat( layerNames, hasItem( LAYER_NAME_2 ) );
    }

    @Test
    public void testParseFromGetMapRequestShouldParseFormat()
                            throws UnsupportedRequestTypeException {
        HttpServletRequest request = mockWmsGetMapRequest();
        WmsRequest wmsRequest = (WmsRequest) parser.parse( request );
        String format = wmsRequest.getFormat();
        assertThat( format, is( FORMAT_NAME ) );
    }

    @Test
    public void testParseFromGetMapRequestShouldParseCrs()
                            throws UnsupportedRequestTypeException {
        HttpServletRequest request = mockWmsGetMapRequest();
        WmsRequest wmsRequest = (WmsRequest) parser.parse( request );
        String crs = wmsRequest.getCrs();
        assertThat( crs, is( CRS_NAME ) );
    }

    @Test
    public void testParseFromGetMapRequestShouldParseBbox()
                            throws UnsupportedRequestTypeException {
        HttpServletRequest request = mockWmsGetMapRequest();
        WmsRequest wmsRequest = (WmsRequest) parser.parse( request );
        Envelope bbox = wmsRequest.getBbox();
        assertThat( bbox, is( BBOX ) );
    }

    @Test
    public void testParseBboxFromValidString() {
        Envelope parsedBbox = parser.parseBbox( BBOX_NAME );
        assertThat( parsedBbox, is( BBOX ) );
    }

    @Test
    public void testParseBboxFromValidStringWithWhiteSpace() {
        Envelope parsedBbox = parser.parseBbox( "1,3, 6, 8" );
        assertThat( parsedBbox, is( new Envelope( 1, 6, 3, 8 ) ) );
    }

    /* Test for invalid requests */
    @Test(expected = IllegalArgumentException.class)
    public void testParseFromGetCapabilitiesRequestMissingServiceNameShouldFail()
                            throws UnsupportedRequestTypeException {
        HttpServletRequest request = mockWmsGetCapabilitiesRequestMissingServiceName();
        parser.parse( request );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseFromGetMapRequestMissingLayersShouldFail()
                            throws UnsupportedRequestTypeException {
        HttpServletRequest request = mockWmsGetMapRequestMissingLayersParameter();
        parser.parse( request );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseFromGetMapRequestTwoCrsShouldFail()
                            throws UnsupportedRequestTypeException {
        HttpServletRequest request = mockWmsGetMapRequestTwoCrsParameters();
        parser.parse( request );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseFromGetMapRequestMissingCrsShouldFail()
                            throws UnsupportedRequestTypeException {
        HttpServletRequest request = mockWmsGetMapRequestMissingCrsParameter();
        parser.parse( request );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseFromGetMapRequestTwoBboxShouldFail()
                            throws UnsupportedRequestTypeException {
        HttpServletRequest request = mockWmsGetMapRequestTwoBboxParameters();
        parser.parse( request );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseFromGetMapRequestMissingBboxShouldFail()
                            throws UnsupportedRequestTypeException {
        HttpServletRequest request = mockWmsGetMapRequestMissingBbox();
        parser.parse( request );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseFromGetMapRequestTwoFormatShouldFail()
                            throws UnsupportedRequestTypeException {
        HttpServletRequest request = mockWmsGetMapRequestTwoFormatParameters();
        parser.parse( request );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseFromGetMapRequestMissingFormatShouldFail()
                            throws UnsupportedRequestTypeException {
        HttpServletRequest request = mockWmsGetMapRequestMissingFormatParameter();
        parser.parse( request );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseFromGetFeatureInfoRequestTwoFormatShouldFail()
                            throws UnsupportedRequestTypeException {
        HttpServletRequest request = mockWmsGetFeatureInfoRequestTwoFormatParameters();
        parser.parse( request );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseFromGetFeatureInfoRequestMissingFormatShouldFail()
                            throws UnsupportedRequestTypeException {
        HttpServletRequest request = mockWmsGetFeatureInfoRequestMissingFormatParameter();
        parser.parse( request );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseWithNullRequestShouldFail()
                            throws UnsupportedRequestTypeException {
        parser.parse( null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseWithMissingRequestParameterShouldFail()
                            throws UnsupportedRequestTypeException {
        parser.parse( mockInvalidWmsRequestMissingRequestParameter() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseWithMissingServiceParameterShouldFail()
                            throws UnsupportedRequestTypeException {
        parser.parse( mockInvalidWmsRequestMissingServiceParameter() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseWithDoubleServiceParameterShouldFail()
                            throws UnsupportedRequestTypeException {
        parser.parse( mockInvalidWmsRequestDoubleServiceParameter( "wms" ) );
    }

    @Test(expected = UnsupportedRequestTypeException.class)
    public void testParseWmsRequestShouldFail()
                            throws UnsupportedRequestTypeException {
        parser.parse( mockWcsGetRequest() );
    }

    @Test
    public void testParseWithMissingVersionParameter()
                            throws UnsupportedRequestTypeException {
        parser.parse( mockValidWmsRequestMissingVersionParameter() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseBboxWithMissingCoordinateShouldFail() {
        parser.parseBbox( "1.98,3,4" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseBboxWithInvalidCoordinateShouldFail() {
        parser.parseBbox( "1,3,4,8a" );
    }

    private HttpServletRequest mockWmsGetCapabilitiesRequestMissingServiceName() {
        Map<String, String[]> parameterMap = createValidGetCapabilitiesParameterMap();
        return mockRequestWithoutServiceName( parameterMap );
    }

    private HttpServletRequest mockWmsGetCapabilitiesRequest() {
        Map<String, String[]> parameterMap = createValidGetCapabilitiesParameterMap();
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockWmsGetCapabilitiesRequestWithExtendedPath() {
        Map<String, String[]> parameterMap = createValidGetCapabilitiesParameterMap();
        return mockRequest( parameterMap, SERVICE_NAME_WITH_PATH );
    }

    private HttpServletRequest mockWmsGetMapRequest() {
        Map<String, String[]> parameterMap = createValidGetMapParameterMap();
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockWmsGetMapRequestMultipleLayerParameter() {
        Map<String, String[]> parameterMap = createValidGetMapParameterMap();
        parameterMap.put( LAYERS_PARAM, new String[] { LAYER_NAME + "," + LAYER_NAME_2 } );
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockWmsGetMapRequestTwoLayerParameters() {
        Map<String, String[]> parameterMap = createValidGetMapParameterMap();
        parameterMap.put( LAYERS_PARAM, new String[] { LAYER_NAME, LAYER_NAME_2 } );
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockWmsGetMapRequestMissingLayersParameter() {
        Map<String, String[]> parameterMap = createValidGetMapParameterMap();
        parameterMap.remove( LAYERS_PARAM );
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockWmsGetMapRequestTwoCrsParameters() {
        Map<String, String[]> parameterMap = createValidGetMapParameterMap();
        parameterMap.put( CRS_PARAM, new String[] { CRS_NAME, CRS_NAME } );
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockWmsGetMapRequestMissingCrsParameter() {
        Map<String, String[]> parameterMap = createValidGetMapParameterMap();
        parameterMap.remove( CRS_PARAM );
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockWmsGetMapRequestTwoBboxParameters() {
        Map<String, String[]> parameterMap = createValidGetMapParameterMap();
        parameterMap.put( BBOX_PARAM, new String[] { BBOX_NAME, BBOX_NAME } );
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockWmsGetMapRequestMissingBbox() {
        Map<String, String[]> parameterMap = createValidGetMapParameterMap();
        parameterMap.remove( BBOX_PARAM );
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockWmsGetMapRequestTwoFormatParameters() {
        Map<String, String[]> parameterMap = createValidGetMapParameterMap();
        parameterMap.put( FORMAT_PARAM, new String[] { FORMAT_NAME, FORMAT_NAME } );
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockWmsGetFeatureInfoRequestTwoFormatParameters() {
        Map<String, String[]> parameterMap = createValidGetFeatureInfoParameterMap();
        parameterMap.put( FORMAT_PARAM, new String[] { FORMAT_NAME, FORMAT_NAME } );
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockWmsGetMapRequestMissingFormatParameter() {
        Map<String, String[]> parameterMap = createValidGetMapParameterMap();
        parameterMap.remove( FORMAT_PARAM );
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockWmsGetFeatureInfoRequestMissingFormatParameter() {
        Map<String, String[]> parameterMap = createValidGetFeatureInfoParameterMap();
        parameterMap.remove( FORMAT_PARAM );
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockWmsGetFeatureInfoRequest() {
        Map<String, String[]> parameterMap = createValidGetFeatureInfoParameterMap();
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockWcsGetRequest() {
        Map<String, String[]> parameterMap = createWcsParameterMap();
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockInvalidWmsRequestMissingRequestParameter() {
        Map<String, String[]> parameterMap = createValidGetCapabilitiesParameterMap();
        parameterMap.remove( REQUEST_PARAM );
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockInvalidWmsRequestMissingServiceParameter() {
        Map<String, String[]> parameterMap = createValidGetCapabilitiesParameterMap();
        parameterMap.remove( SERVICE_PARAM );
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockValidWmsRequestMissingVersionParameter() {
        Map<String, String[]> parameterMap = createValidGetCapabilitiesParameterMap();
        parameterMap.remove( VERSION_PARAM );
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockInvalidWmsRequestDoubleServiceParameter( String serviceType ) {
        Map<String, String[]> parameterMap = createValidGetCapabilitiesParameterMap();
        parameterMap.put( "service", new String[] { serviceType } );
        return mockRequest( parameterMap );
    }

    private Map<String, String[]> createWcsParameterMap() {
        Map<String, String[]> parameterMap = new HashMap<String, String[]>();
        parameterMap.put( VERSION_PARAM, new String[] { VERSION_130.getVersionString() } );
        parameterMap.put( REQUEST_PARAM, new String[] { GETCAPABILITIES } );
        parameterMap.put( SERVICE_PARAM, new String[] { "wcs" } );
        return parameterMap;
    }

    private Map<String, String[]> createValidGetFeatureInfoParameterMap() {
        Map<String, String[]> parameterMap = new HashMap<String, String[]>();
        parameterMap.put( VERSION_PARAM, new String[] { VERSION_130.getVersionString() } );
        parameterMap.put( REQUEST_PARAM, new String[] { GETFEATUREINFO } );
        parameterMap.put( QUERY_LAYERS_PARAM, new String[] { QUERY_LAYER_NAME + "," + QUERY_LAYER_NAME } );
        parameterMap.put( INFO_FORMAT_PARAM, new String[] { INFO_FORMAT_NAME } );
        parameterMap.put( I_PARAM, new String[] { I_NAME } );
        parameterMap.put( J_PARAM, new String[] { J_NAME } );
        parameterMap.put( LAYERS_PARAM, new String[] { LAYER_NAME + "," + LAYER_NAME } );
        parameterMap.put( SERVICE_PARAM, new String[] { "wms" } );
        parameterMap.put( STYLES_PARAM, new String[] { STYLES_NAME } );
        parameterMap.put( CRS_PARAM, new String[] { CRS_NAME } );
        parameterMap.put( BBOX_PARAM, new String[] { BBOX_NAME } );
        parameterMap.put( WIDTH_PARAM, new String[] { WIDTH_NAME } );
        parameterMap.put( HEIGHT_PARAM, new String[] { HEIGHT_NAME } );
        parameterMap.put( FORMAT_PARAM, new String[] { FORMAT_NAME } );
        return parameterMap;
    }

    private Map<String, String[]> createValidGetCapabilitiesParameterMap() {
        Map<String, String[]> parameterMap = new HashMap<String, String[]>();
        parameterMap.put( VERSION_PARAM, new String[] { VERSION_130.getVersionString() } );
        parameterMap.put( REQUEST_PARAM, new String[] { GETCAPABILITIES } );
        parameterMap.put( SERVICE_PARAM, new String[] { "wms" } );
        return parameterMap;
    }

    private Map<String, String[]> createValidGetMapParameterMap() {
        Map<String, String[]> parameterMap = new HashMap<String, String[]>();
        parameterMap.put( VERSION_PARAM, new String[] { VERSION_130.getVersionString() } );
        parameterMap.put( REQUEST_PARAM, new String[] { GETMAP } );
        parameterMap.put( LAYERS_PARAM, new String[] { LAYER_NAME } );
        parameterMap.put( SERVICE_PARAM, new String[] { "wms" } );
        parameterMap.put( STYLES_PARAM, new String[] { STYLES_NAME } );
        parameterMap.put( CRS_PARAM, new String[] { CRS_NAME } );
        parameterMap.put( BBOX_PARAM, new String[] { BBOX_NAME } );
        parameterMap.put( WIDTH_PARAM, new String[] { WIDTH_NAME } );
        parameterMap.put( HEIGHT_PARAM, new String[] { HEIGHT_NAME } );
        parameterMap.put( FORMAT_PARAM, new String[] { FORMAT_NAME } );
        return parameterMap;
    }

    private HttpServletRequest mockRequest( Map<String, String[]> parameterMap ) {
        return mockRequest( parameterMap, SERVICE_NAME );
    }

    private HttpServletRequest mockRequest( Map<String, String[]> parameterMap, String serviceName ) {
        HttpServletRequest servletRequest = Mockito.mock( HttpServletRequest.class );
        when( servletRequest.getParameterMap() ).thenReturn( parameterMap );
        when( servletRequest.getParameterNames() ).thenReturn( new Vector<String>( parameterMap.keySet() ).elements() );
        if ( parameterMap.get( VERSION_PARAM ) != null ) {
            when( servletRequest.getParameter( VERSION_PARAM ) ).thenReturn( parameterMap.get( VERSION_PARAM )[0] );
            when( servletRequest.getParameterValues( VERSION_PARAM ) ).thenReturn( parameterMap.get( VERSION_PARAM ) );
        }
        if ( parameterMap.get( LAYERS_PARAM ) != null ) {
            when( servletRequest.getParameter( LAYERS_PARAM ) ).thenReturn( parameterMap.get( LAYERS_PARAM )[0] );
        }
        when( servletRequest.getParameterValues( LAYER_NAME ) ).thenReturn( parameterMap.get( LAYERS_PARAM ) );
        if ( parameterMap.get( REQUEST_PARAM ) != null ) {
            when( servletRequest.getParameter( REQUEST_PARAM ) ).thenReturn( parameterMap.get( REQUEST_PARAM )[0] );
            when( servletRequest.getParameterValues( REQUEST_PARAM ) ).thenReturn( parameterMap.get( REQUEST_PARAM ) );
        }
        if ( parameterMap.get( SERVICE_PARAM ) != null ) {
            when( servletRequest.getParameter( SERVICE_PARAM ) ).thenReturn( parameterMap.get( SERVICE_PARAM )[0] );
            when( servletRequest.getParameterValues( SERVICE_PARAM ) ).thenReturn( parameterMap.get( SERVICE_PARAM ) );
        }
        when( servletRequest.getServletPath() ).thenReturn( serviceName );
        return servletRequest;
    }

    private HttpServletRequest mockRequestWithoutServiceName( Map<String, String[]> parameterMap ) {
        HttpServletRequest servletRequest = Mockito.mock( HttpServletRequest.class );
        when( servletRequest.getParameterMap() ).thenReturn( parameterMap );
        when( servletRequest.getParameterNames() ).thenReturn( new Vector<String>( parameterMap.keySet() ).elements() );
        if ( parameterMap.get( VERSION_PARAM ) != null ) {
            when( servletRequest.getParameter( VERSION_PARAM ) ).thenReturn( parameterMap.get( VERSION_PARAM )[0] );
            when( servletRequest.getParameterValues( VERSION_PARAM ) ).thenReturn( parameterMap.get( VERSION_PARAM ) );
        }
        if ( parameterMap.get( LAYERS_PARAM ) != null ) {
            when( servletRequest.getParameter( LAYERS_PARAM ) ).thenReturn( parameterMap.get( LAYERS_PARAM )[0] );
        }
        when( servletRequest.getParameterValues( LAYER_NAME ) ).thenReturn( parameterMap.get( LAYERS_PARAM ) );
        if ( parameterMap.get( REQUEST_PARAM ) != null ) {
            when( servletRequest.getParameter( REQUEST_PARAM ) ).thenReturn( parameterMap.get( REQUEST_PARAM )[0] );
            when( servletRequest.getParameterValues( REQUEST_PARAM ) ).thenReturn( parameterMap.get( REQUEST_PARAM ) );
        }
        if ( parameterMap.get( SERVICE_PARAM ) != null ) {
            when( servletRequest.getParameter( SERVICE_PARAM ) ).thenReturn( parameterMap.get( SERVICE_PARAM )[0] );
            when( servletRequest.getParameterValues( SERVICE_PARAM ) ).thenReturn( parameterMap.get( SERVICE_PARAM ) );
        }
        return servletRequest;
    }

}
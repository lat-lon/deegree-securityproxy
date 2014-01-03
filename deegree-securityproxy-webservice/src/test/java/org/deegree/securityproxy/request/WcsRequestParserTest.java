//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2011 by:
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
package org.deegree.securityproxy.request;

import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import static org.deegree.securityproxy.commons.WcsOperationType.*;
import static org.deegree.securityproxy.commons.WcsServiceVersion.VERSION_100;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * @version $Revision: $, $Date: $
 */
public class WcsRequestParserTest {

    private WcsRequestParser parser = new WcsRequestParser();

    public static final String FORMAT_PARAM = "FORMAT";

    public static final String RESX_PARAM = "RESX";

    public static final String RESY_PARAM = "RESY";

    public static final String CRS_PARAM = "CRS";

    public static final String BBOX_PARAM = "BBOX";

    private static final String COVERAGE_PARAM = "COVERAGE";

    private static final String REQUEST_PARAM = "REQUEST";

    private static final String VERSION_PARAM = "VERSION";

    private static final String SERVICE_PARAM = "SERVICE";

    private static final String COVERAGE_NAME = "layerName";

    private static final String SERVICE_NAME = "serviceName";

    /* Tests for valid requests for WCS GetCapabilities */
    @Test
    public void testParseFromGetCapabilitiesRequestShouldIgnoreCoverageName()
          throws UnsupportedRequestTypeException {
        HttpServletRequest request = mockWcsGetCapabilitiesRequest();
        WcsRequest wcsRequest = parser.parse( request );
        assertThat( wcsRequest.getCoverageNames().isEmpty(), is( true ) );
    }

    @Test
    public void testParseFromGetCapabilitiesRequestShouldParseOperationTypeAndServiceVersion()
          throws UnsupportedRequestTypeException {
        HttpServletRequest request = mockWcsGetCapabilitiesRequest();
        WcsRequest wcsRequest = parser.parse( request );
        assertThat( wcsRequest.getOperationType(), is( GETCAPABILITIES ) );
        assertThat( wcsRequest.getServiceVersion(), is( VERSION_100 ) );
    }

    /* Test for valid requests for WCS DescribeCoverage */
    @Test
    public void testParseFromDescribeCoverageRequestShouldParseCoverageOperationTypeAndServiceVersion()
          throws UnsupportedRequestTypeException {
        HttpServletRequest request = mockWcsDescribeCoverageRequest();
        WcsRequest wcsRequest = parser.parse( request );
        List<String> coverageNames = wcsRequest.getCoverageNames();
        assertThat( coverageNames.get( 0 ), is( COVERAGE_NAME ) );
        assertThat( coverageNames.get( 1 ), is( COVERAGE_NAME ) );
        assertThat( coverageNames.size(), is( 2 ) );
        assertThat( wcsRequest.getOperationType(), is( DESCRIBECOVERAGE ) );
        assertThat( wcsRequest.getServiceVersion(), is( VERSION_100 ) );
    }

    /* Test for valid requests for WCS GetCoverage */
    @Test
    public void testParseFromGetCoverageRequestShouldParseCoverageOperationTypeAndServiceVersion()
          throws UnsupportedRequestTypeException {
        HttpServletRequest request = mockWcsGetCoverageRequest();
        WcsRequest wcsRequest = parser.parse( request );
        List<String> coverageNames = wcsRequest.getCoverageNames();
        assertThat( coverageNames.get( 0 ), is( COVERAGE_NAME ) );
        assertThat( coverageNames.size(), is( 1 ) );
        assertThat( wcsRequest.getOperationType(), is( GETCOVERAGE ) );
        assertThat( wcsRequest.getServiceVersion(), is( VERSION_100 ) );
    }

    /* Test for invalid requests */
    @Test(expected = IllegalArgumentException.class)
    public void testParseFromGetCoverageRequestMultipleCoveragesShouldFail()
          throws UnsupportedRequestTypeException {
        HttpServletRequest request = mockWcsGetCoverageRequestMultipleCoverageParameter();
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
        parser.parse( mockInvalidWcsRequestMissingRequestParameter( "wcs" ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseWithMissingServiceParameterShouldFail()
          throws UnsupportedRequestTypeException {
        parser.parse( mockInvalidWcsRequestMissingServiceParameter( "wcs" ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseWithDoubleServiceParameterShouldFail()
          throws UnsupportedRequestTypeException {
        parser.parse( mockInvalidWcsRequestDoubleServiceParameter( "wcs" ) );
    }

    @Test(expected = UnsupportedRequestTypeException.class)
    public void testParseWmsRequestShouldFail()
          throws UnsupportedRequestTypeException {
        parser.parse( mockWmsGetRequest() );
    }

    @Test
    public void testParseWithMissingVersionParameter()
          throws UnsupportedRequestTypeException {
        parser.parse( mockValidWcsRequestMissingVersionParameter( "wcs" ) );
    }

    private HttpServletRequest mockWcsGetCapabilitiesRequest() {
        Map<String, String[]> parameterMap = createValidGetCapabilitiesParameterMap();
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockWcsGetCoverageRequest() {
        Map<String, String[]> parameterMap = createValidGetCoverageParameterMap();
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockWcsGetCoverageRequestMultipleCoverageParameter() {
        Map<String, String[]> parameterMap = createValidGetCoverageParameterMap();
        parameterMap.put( COVERAGE_PARAM, new String[] { COVERAGE_NAME + "," + COVERAGE_NAME } );
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockWcsDescribeCoverageRequest() {
        Map<String, String[]> parameterMap = createValidDescribeCoverageParameterMap();
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockWmsGetRequest() {
        Map<String, String[]> parameterMap = createWmsParameterMap();
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockInvalidWcsRequestMissingRequestParameter( String serviceType ) {
        Map<String, String[]> parameterMap = createValidGetCapabilitiesParameterMap();
        parameterMap.remove( REQUEST_PARAM );
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockInvalidWcsRequestMissingServiceParameter( String serviceType ) {
        Map<String, String[]> parameterMap = createValidGetCapabilitiesParameterMap();
        parameterMap.remove( SERVICE_PARAM );
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockValidWcsRequestMissingVersionParameter( String serviceType ) {
        Map<String, String[]> parameterMap = createValidGetCapabilitiesParameterMap();
        parameterMap.remove( VERSION_PARAM );
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockInvalidWcsRequestDoubleServiceParameter( String serviceType ) {
        Map<String, String[]> parameterMap = createValidGetCapabilitiesParameterMap();
        parameterMap.put( "service", new String[] { serviceType } );
        return mockRequest( parameterMap );
    }

    private Map<String, String[]> createWmsParameterMap() {
        Map<String, String[]> parameterMap = new HashMap<String, String[]>();
        parameterMap.put( VERSION_PARAM, new String[] { VERSION_100.getVersionString() } );
        parameterMap.put( REQUEST_PARAM, new String[] { GETCAPABILITIES.name() } );
        parameterMap.put( SERVICE_PARAM, new String[] { "wms" } );
        return parameterMap;
    }

    private Map<String, String[]> createValidDescribeCoverageParameterMap() {
        Map<String, String[]> parameterMap = new HashMap<String, String[]>();
        parameterMap.put( VERSION_PARAM, new String[] { VERSION_100.getVersionString() } );
        parameterMap.put( REQUEST_PARAM, new String[] { DESCRIBECOVERAGE.name() } );
        parameterMap.put( COVERAGE_PARAM, new String[] { COVERAGE_NAME + "," + COVERAGE_NAME } );
        parameterMap.put( SERVICE_PARAM, new String[] { "wcs" } );
        return parameterMap;
    }

    private Map<String, String[]> createValidGetCapabilitiesParameterMap() {
        Map<String, String[]> parameterMap = new HashMap<String, String[]>();
        parameterMap.put( VERSION_PARAM, new String[] { VERSION_100.getVersionString() } );
        parameterMap.put( REQUEST_PARAM, new String[] { GETCAPABILITIES.name() } );
        parameterMap.put( SERVICE_PARAM, new String[] { "wcs" } );
        return parameterMap;
    }

    private Map<String, String[]> createValidGetCoverageParameterMap() {
        Map<String, String[]> parameterMap = new HashMap<String, String[]>();
        parameterMap.put( VERSION_PARAM, new String[] { VERSION_100.getVersionString() } );
        parameterMap.put( REQUEST_PARAM, new String[] { GETCOVERAGE.name() } );
        parameterMap.put( COVERAGE_PARAM, new String[] { COVERAGE_NAME } );
        parameterMap.put( SERVICE_PARAM, new String[] { "wcs" } );
        parameterMap.put( CRS_PARAM, new String[] { "EPSG:4326" } );
        parameterMap.put( BBOX_PARAM, new String[] { "-89.67,20.25,-89.32,20.44" } );
        parameterMap.put( RESX_PARAM, new String[] { "50" } );
        parameterMap.put( RESY_PARAM, new String[] { "50" } );
        parameterMap.put( FORMAT_PARAM, new String[] { "GEOTIFF_INT16" } );
        return parameterMap;
    }

    private HttpServletRequest mockRequest( Map<String, String[]> parameterMap ) {
        HttpServletRequest servletRequest = Mockito.mock( HttpServletRequest.class );
        when( servletRequest.getParameterMap() ).thenReturn( parameterMap );
        when( servletRequest.getParameterNames() ).thenReturn( new Vector<String>( parameterMap.keySet() ).elements() );
        if ( parameterMap.get( VERSION_PARAM ) != null ) {
            when( servletRequest.getParameter( VERSION_PARAM ) ).thenReturn( parameterMap.get( VERSION_PARAM )[0] );
            when( servletRequest.getParameterValues( VERSION_PARAM ) ).thenReturn( parameterMap.get( VERSION_PARAM ) );
        }
        if ( parameterMap.get( COVERAGE_PARAM ) != null ) {
            when( servletRequest.getParameter( COVERAGE_PARAM ) ).thenReturn( parameterMap.get( COVERAGE_PARAM )[0] );
        }
        when( servletRequest.getParameterValues( COVERAGE_NAME ) ).thenReturn( parameterMap.get( COVERAGE_PARAM ) );
        if ( parameterMap.get( REQUEST_PARAM ) != null ) {
            when( servletRequest.getParameter( REQUEST_PARAM ) ).thenReturn( parameterMap.get( REQUEST_PARAM )[0] );
            when( servletRequest.getParameterValues( REQUEST_PARAM ) ).thenReturn( parameterMap.get( REQUEST_PARAM ) );
        }
        if ( parameterMap.get( SERVICE_PARAM ) != null ) {
            when( servletRequest.getParameter( SERVICE_PARAM ) ).thenReturn( parameterMap.get( SERVICE_PARAM )[0] );
            when( servletRequest.getParameterValues( SERVICE_PARAM ) ).thenReturn( parameterMap.get( SERVICE_PARAM ) );
        }
        when( servletRequest.getQueryString() ).thenReturn( SERVICE_NAME );
        return servletRequest;
    }

}
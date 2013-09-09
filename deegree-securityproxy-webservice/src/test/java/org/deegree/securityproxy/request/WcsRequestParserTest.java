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

import static org.deegree.securityproxy.commons.WcsOperationType.GETCAPABILITIES;
import static org.deegree.securityproxy.commons.WcsServiceVersion.VERSION_100;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.deegree.securityproxy.commons.WcsOperationType;
import org.deegree.securityproxy.commons.WcsServiceVersion;
import org.deegree.securityproxy.request.WcsRequest;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class WcsRequestParserTest {

    private WcsRequestParser parser = new WcsRequestParser();

    private static final String COVERAGE_PARAM = "COVERAGE";

    private static final String REQUEST_PARAM = "REQUEST";

    private static final String VERSION_PARAM = "VERSION";

    private static final String SERVICE_PARAM = "SERVICE";

    private static final WcsServiceVersion SERVICE_VERSION = VERSION_100;

    private static final WcsOperationType OPERATION_TYPE = GETCAPABILITIES;

    private static final String LAYER_NAME = "layerName";

    private static final String SERVICE_NAME = "serviceName";

    @Test
    public void testParseFromGetRequestShouldParseLayerName() {
        HttpServletRequest request = mockWcsGetRequest();
        WcsRequest wcsRequest = parser.parse( request );
        assertThat( wcsRequest.getLayerName(), is( LAYER_NAME ) );
    }

    @Test
    public void testParseFromGetRequestShouldParseOperationType() {
        HttpServletRequest request = mockWcsGetRequest();
        WcsRequest wcsRequest = parser.parse( request );
        assertThat( wcsRequest.getOperationType(), is( OPERATION_TYPE ) );
    }

    @Test
    public void testParseFromGetRequestShouldParseServiceName() {
        HttpServletRequest request = mockWcsGetRequest();
        WcsRequest wcsRequest = parser.parse( request );
        assertThat( wcsRequest.getServiceName(), is( SERVICE_NAME ) );
    }

    @Test
    public void testParseFromGetRequestShouldParseServiceVersion() {
        HttpServletRequest request = mockWcsGetRequest();
        WcsRequest wcsRequest = parser.parse( request );
        assertThat( wcsRequest.getServiceVersion(), is( SERVICE_VERSION ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseWithNullRequestShouldFail() {
        parser.parse( null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseWithMissingRequestParameterShouldFail() {
        parser.parse( mockInvalidWcsRequestMissingRequestParameter( "wcs" ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseWithMissingServiceParameterShouldFail() {
        parser.parse( mockInvalidWcsRequestMissingServiceParameter( "wcs" ) );
    }

    @Test(expected = UnsupportedRequestTypeException.class)
    public void testParseWmsRequestShouldFail() {
        parser.parse( mockWmsGetRequest() );
    }

    private HttpServletRequest mockWcsGetRequest() {
        Map<String, String> parameterMap = createValidParameterMap( "wcs" );
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockWmsGetRequest() {
        Map<String, String> parameterMap = createValidParameterMap( "wms" );
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockInvalidWcsRequestMissingRequestParameter( String serviceType ) {
        Map<String, String> parameterMap = createValidParameterMap( "wcs" );
        parameterMap.remove( REQUEST_PARAM );
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockInvalidWcsRequestMissingServiceParameter( String serviceType ) {
        Map<String, String> parameterMap = createValidParameterMap( "wcs" );
        parameterMap.remove( SERVICE_PARAM );
        return mockRequest( parameterMap );
    }

    private Map<String, String> createValidParameterMap( String serviceType ) {
        Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put( VERSION_PARAM, SERVICE_VERSION.getVersionString() );
        parameterMap.put( REQUEST_PARAM, OPERATION_TYPE.name() );
        parameterMap.put( COVERAGE_PARAM, LAYER_NAME );
        parameterMap.put( SERVICE_PARAM, serviceType );
        return parameterMap;
    }
    
    private HttpServletRequest mockRequest( Map<String, String> parameterMap ) {
        HttpServletRequest servletRequest = Mockito.mock( HttpServletRequest.class );
        when( servletRequest.getParameterMap() ).thenReturn( parameterMap );
        when( servletRequest.getParameterNames() ).thenReturn( new Vector<String>( parameterMap.keySet() ).elements() );
        when( servletRequest.getParameter( VERSION_PARAM ) ).thenReturn( parameterMap.get( VERSION_PARAM ) );
        when( servletRequest.getParameterValues( VERSION_PARAM ) ).thenReturn( new String[] { parameterMap.get( VERSION_PARAM ) } );
        when( servletRequest.getParameter( REQUEST_PARAM ) ).thenReturn( parameterMap.get( REQUEST_PARAM ) );
        when( servletRequest.getParameterValues( REQUEST_PARAM ) ).thenReturn( new String[] { parameterMap.get( REQUEST_PARAM ) } );
        when( servletRequest.getParameter( COVERAGE_PARAM ) ).thenReturn( parameterMap.get( COVERAGE_PARAM ) );
        when( servletRequest.getParameterValues( LAYER_NAME ) ).thenReturn( new String[] { parameterMap.get( COVERAGE_PARAM ) } );
        when( servletRequest.getPathInfo() ).thenReturn( "/" + SERVICE_NAME );
        return servletRequest;
    }

}
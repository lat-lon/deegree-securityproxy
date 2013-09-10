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
    public void testParseFromGetRequestShouldParseLayerName()
                            throws UnsupportedRequestTypeException {
        HttpServletRequest request = mockWcsGetRequest();
        WcsRequest wcsRequest = parser.parse( request );
        assertThat( wcsRequest.getCoverageName(), is( LAYER_NAME ) );
    }

    @Test
    public void testParseFromGetRequestShouldParseOperationType()
                            throws UnsupportedRequestTypeException {
        HttpServletRequest request = mockWcsGetRequest();
        WcsRequest wcsRequest = parser.parse( request );
        assertThat( wcsRequest.getOperationType(), is( OPERATION_TYPE ) );
    }

    @Test
    public void testParseFromGetRequestShouldParseServiceVersion()
                            throws UnsupportedRequestTypeException {
        HttpServletRequest request = mockWcsGetRequest();
        WcsRequest wcsRequest = parser.parse( request );
        assertThat( wcsRequest.getServiceVersion(), is( SERVICE_VERSION ) );
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

    @Test
    public void testParseWithMissingCoverageParameter()
                            throws UnsupportedRequestTypeException {
        parser.parse( mockValidWcsRequestMissingCoverageParameter( "wcs" ) );
    }

    private HttpServletRequest mockWcsGetRequest() {
        Map<String, String[]> parameterMap = createValidParameterMap( "wcs" );
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockWmsGetRequest() {
        Map<String, String[]> parameterMap = createValidParameterMap( "wms" );
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockInvalidWcsRequestMissingRequestParameter( String serviceType ) {
        Map<String, String[]> parameterMap = createValidParameterMap( "wcs" );
        parameterMap.remove( REQUEST_PARAM );
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockInvalidWcsRequestMissingServiceParameter( String serviceType ) {
        Map<String, String[]> parameterMap = createValidParameterMap( "wcs" );
        parameterMap.remove( SERVICE_PARAM );
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockValidWcsRequestMissingVersionParameter( String serviceType ) {
        Map<String, String[]> parameterMap = createValidParameterMap( "wcs" );
        parameterMap.remove( VERSION_PARAM );
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockValidWcsRequestMissingCoverageParameter( String serviceType ) {
        Map<String, String[]> parameterMap = createValidParameterMap( "wcs" );
        parameterMap.remove( COVERAGE_PARAM );
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockInvalidWcsRequestDoubleServiceParameter( String serviceType ) {
        Map<String, String[]> parameterMap = createValidParameterMap( "wcs" );
        parameterMap.put( "service", new String[] { serviceType } );
        return mockRequest( parameterMap );
    }

    private Map<String, String[]> createValidParameterMap( String serviceType ) {
        Map<String, String[]> parameterMap = new HashMap<String, String[]>();
        parameterMap.put( VERSION_PARAM, new String[] { SERVICE_VERSION.getVersionString() } );
        parameterMap.put( REQUEST_PARAM, new String[] { OPERATION_TYPE.name() } );
        parameterMap.put( COVERAGE_PARAM, new String[] { LAYER_NAME } );
        parameterMap.put( SERVICE_PARAM, new String[] { serviceType } );
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
        when( servletRequest.getParameterValues( LAYER_NAME ) ).thenReturn( parameterMap.get( COVERAGE_PARAM ) );
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
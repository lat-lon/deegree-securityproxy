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
package org.deegree.securityproxy.wps.request;

import static org.deegree.securityproxy.wps.request.WpsRequestParser.DESCRIBEPROCESS;
import static org.deegree.securityproxy.wps.request.WpsRequestParser.EXECUTE;
import static org.deegree.securityproxy.wps.request.WpsRequestParser.GETCAPABILITIES;
import static org.deegree.securityproxy.wps.request.WpsRequestParser.VERSION_100;
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

/**
 * Tests for {@link WpsRequestParser}.
 * 
 * @author <a href="mailto:wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author last edited by: $Author: stenger $
 * @version $Revision: $, $Date: $
 */
public class WpsRequestParserTest {

    private static final String REQUEST_PARAM = "REQUEST";

    private static final String VERSION_PARAM = "VERSION";

    private static final String SERVICE_PARAM = "SERVICE";

    private static final String IDENTIFIER_PARAM = "IDENTIFIER";

    private static final String SERVICE_NAME = "serviceName";

    private static final String SERVICE_NAME_WITH_PATH = "path/" + SERVICE_NAME;

    private static final String IDENTIFIER = "process1";

    private static final String IDENTIFIER_2 = "process2";

    private final WpsRequestParser parser = new WpsRequestParser();

    /* Tests for valid requests for WMS GetCapabilities */
    @Test
    public void testParseFromGetCapabilitiesRequestShouldIgnoreIdentifiers()
                            throws UnsupportedRequestTypeException {
        HttpServletRequest request = mockWpsGetCapabilitiesRequest();
        WpsRequest wpsRequest = parser.parse( request );
        assertThat( wpsRequest.getIdentifiers().isEmpty(), is( true ) );
    }

    @Test
    public void testParseFromGetCapabilitiesRequestShouldParseOperationTypeAndServiceVersionAndServiceName()
                            throws UnsupportedRequestTypeException {
        HttpServletRequest request = mockWpsGetCapabilitiesRequest();
        WpsRequest wmsRequest = parser.parse( request );
        assertThat( wmsRequest.getOperationType(), is( GETCAPABILITIES ) );
        assertThat( wmsRequest.getServiceVersion(), is( VERSION_100 ) );
        assertThat( wmsRequest.getServiceName(), is( SERVICE_NAME ) );
    }

    @Test
    public void testParseFromGetCapabilitiesRequestWithExtendedPathShouldParseServiceName()
                            throws UnsupportedRequestTypeException {
        HttpServletRequest request = mockWpsGetCapabilitiesRequestWithExtendedPath();
        WpsRequest wpsRequest = parser.parse( request );
        assertThat( wpsRequest.getOperationType(), is( GETCAPABILITIES ) );
        assertThat( wpsRequest.getServiceVersion(), is( VERSION_100 ) );
        assertThat( wpsRequest.getServiceName(), is( SERVICE_NAME ) );
    }

    /* Test for valid requests for WPS DescribeProcess */
    @Test
    public void testParseFromDescribeProcessRequestShouldParseIdentifiersAndOperationTypeAndVersionAndServiceName()
                            throws UnsupportedRequestTypeException {
        HttpServletRequest request = mockWpsDescribeProceessRequest();
        WpsRequest wpsRequest = parser.parse( request );
        List<String> identifiers = wpsRequest.getIdentifiers();
        assertThat( identifiers.size(), is( 2 ) );
        assertThat( identifiers.get( 0 ), is( IDENTIFIER ) );
        assertThat( identifiers.get( 1 ), is( IDENTIFIER_2 ) );
        assertThat( wpsRequest.getOperationType(), is( DESCRIBEPROCESS ) );
        assertThat( wpsRequest.getServiceVersion(), is( VERSION_100 ) );
        assertThat( wpsRequest.getServiceName(), is( SERVICE_NAME ) );
    }

    /* Test for valid requests for WPS Execute */
    @Test
    public void testParseFromGetMapRequestShouldParseIdentifierAndOperationTypeAndServiceVersionAndServiceName()
                            throws UnsupportedRequestTypeException {
        HttpServletRequest request = mockWpsExecuteRequest();
        WpsRequest wpsRequest = parser.parse( request );
        List<String> identifiers = wpsRequest.getIdentifiers();
        assertThat( identifiers.size(), is( 1 ) );
        assertThat( identifiers.get( 0 ), is( IDENTIFIER ) );
        assertThat( wpsRequest.getOperationType(), is( EXECUTE ) );
        assertThat( wpsRequest.getServiceVersion(), is( VERSION_100 ) );
        assertThat( wpsRequest.getServiceName(), is( SERVICE_NAME ) );
    }

    public void testParse()
                            throws Exception {

    }

    private HttpServletRequest mockWpsGetCapabilitiesRequest() {
        Map<String, String[]> parameterMap = createValidGetCapabilitiesParameterMap();
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockWpsGetCapabilitiesRequestWithExtendedPath() {
        Map<String, String[]> parameterMap = createValidGetCapabilitiesParameterMap();
        return mockRequest( parameterMap, SERVICE_NAME_WITH_PATH );
    }

    private HttpServletRequest mockWpsDescribeProceessRequest() {
        Map<String, String[]> parameterMap = createValidDescribeProcessParameterMap();
        return mockRequest( parameterMap );
    }

    private Map<String, String[]> createValidGetCapabilitiesParameterMap() {
        Map<String, String[]> parameterMap = new HashMap<String, String[]>();
        parameterMap.put( VERSION_PARAM, new String[] { VERSION_100.getVersionString() } );
        parameterMap.put( REQUEST_PARAM, new String[] { GETCAPABILITIES } );
        parameterMap.put( SERVICE_PARAM, new String[] { "wps" } );
        return parameterMap;
    }

    private Map<String, String[]> createValidDescribeProcessParameterMap() {
        Map<String, String[]> parameterMap = new HashMap<String, String[]>();
        parameterMap.put( VERSION_PARAM, new String[] { VERSION_100.getVersionString() } );
        parameterMap.put( REQUEST_PARAM, new String[] { DESCRIBEPROCESS } );
        parameterMap.put( IDENTIFIER_PARAM, new String[] { IDENTIFIER + "," + IDENTIFIER_2 } );
        parameterMap.put( SERVICE_PARAM, new String[] { "wps" } );
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
        if ( parameterMap.get( IDENTIFIER_PARAM ) != null ) {
            when( servletRequest.getParameter( IDENTIFIER_PARAM ) ).thenReturn( parameterMap.get( IDENTIFIER_PARAM )[0] );
        }
        when( servletRequest.getParameterValues( IDENTIFIER_PARAM ) ).thenReturn( parameterMap.get( IDENTIFIER_PARAM ) );
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

    private HttpServletRequest mockWpsExecuteRequest() {
        Map<String, String[]> parameterMap = createValidExecuteParameterMap();
        return mockRequest( parameterMap );
    }

    private Map<String, String[]> createValidExecuteParameterMap() {
        Map<String, String[]> parameterMap = new HashMap<String, String[]>();
        parameterMap.put( VERSION_PARAM, new String[] { VERSION_100.getVersionString() } );
        parameterMap.put( REQUEST_PARAM, new String[] { EXECUTE } );
        parameterMap.put( IDENTIFIER_PARAM, new String[] { IDENTIFIER } );
        parameterMap.put( SERVICE_PARAM, new String[] { "wps" } );
        return parameterMap;
    }
}
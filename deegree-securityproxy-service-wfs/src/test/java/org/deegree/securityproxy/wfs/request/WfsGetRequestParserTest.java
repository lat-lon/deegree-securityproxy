package org.deegree.securityproxy.wfs.request;

import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.request.UnsupportedRequestTypeException;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import static org.deegree.securityproxy.wfs.request.WfsGetRequestParser.DESCRIBEFEATURETYPE;
import static org.deegree.securityproxy.wfs.request.WfsGetRequestParser.GETCAPABILITIES;
import static org.deegree.securityproxy.wfs.request.WfsGetRequestParser.GETFEATURE;
import static org.deegree.securityproxy.wfs.request.WfsGetRequestParser.VERSION_110;
import static org.deegree.securityproxy.wfs.request.WfsGetRequestParser.WFS_SERVICE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link WfsGetRequestParser}.
 * 
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * 
 * @version $Revision: $, $Date: $
 */
public class WfsGetRequestParserTest {

    private static final String REQUEST_PARAM = "REQUEST";

    private static final String VERSION_PARAM = "VERSION";

    private static final String SERVICE_PARAM = "SERVICE";

    private final WfsGetRequestParser parser = new WfsGetRequestParser();

    @Test
    public void testParseFromGetCapabilitiesRequestShouldParseOperationTypeAndServiceVersionAndServiceType()
                            throws UnsupportedRequestTypeException {
        HttpServletRequest request = mockWfsGetCapabilitiesRequest();
        OwsRequest wfsRequest = parser.parse( request );
        assertThat( wfsRequest.getOperationType(), is( GETCAPABILITIES ) );
        assertThat( wfsRequest.getServiceVersion(), is( VERSION_110 ) );
        assertThat( wfsRequest.getServiceType(), is( WFS_SERVICE ) );
    }

    @Test
    public void testParseFromGetFeatureRequestShouldParseOperationTypeAndServiceVersionAndServiceType()
                            throws UnsupportedRequestTypeException {
        HttpServletRequest request = mockWfsGetFeatureRequest();
        OwsRequest wfsRequest = parser.parse( request );
        assertThat( wfsRequest.getOperationType(), is( GETFEATURE ) );
        assertThat( wfsRequest.getServiceVersion(), is( VERSION_110 ) );
        assertThat( wfsRequest.getServiceType(), is( WFS_SERVICE ) );
    }

    @Test
    public void testParseFromDescribeFeatureTypeRequestShouldParseOperationTypeAndServiceVersionAndServiceType()
                            throws UnsupportedRequestTypeException {
        HttpServletRequest request = mockWfsDescribeFeatureTypeRequest();
        OwsRequest wfsRequest = parser.parse( request );
        assertThat( wfsRequest.getOperationType(), is( DESCRIBEFEATURETYPE ) );
        assertThat( wfsRequest.getServiceVersion(), is( VERSION_110 ) );
        assertThat( wfsRequest.getServiceType(), is( WFS_SERVICE ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseWithNullRequestShouldFail()
                            throws UnsupportedRequestTypeException {
        parser.parse( null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseWithMissingRequestParameterShouldFail()
                            throws UnsupportedRequestTypeException {
        parser.parse( mockInvalidWfsRequestMissingRequestParameter() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseWithDoubleRequestParameterShouldFail()
                            throws UnsupportedRequestTypeException {
        parser.parse( mockInvalidWfsRequestDoubleRequestParameter( "GetCapabilities" ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseWithMissingServiceParameterShouldFail()
                            throws UnsupportedRequestTypeException {
        parser.parse( mockInvalidWfsRequestMissingServiceParameter() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseWithDoubleServiceParameterShouldFail()
                            throws UnsupportedRequestTypeException {
        parser.parse( mockInvalidWfsRequestDoubleServiceParameter( "wfs" ) );
    }

    @Test
    public void testParseWithMissingVersionParameter()
                            throws UnsupportedRequestTypeException {
        parser.parse( mockValidWfsRequestMissingVersionParameter() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseWithDoubleVersionParameterShouldFail()
                            throws UnsupportedRequestTypeException {
        parser.parse( mockInvalidWfsRequestDoubleVersionParameter( "1.1.0" ) );
    }

    @Test(expected = UnsupportedRequestTypeException.class)
    public void testParseWcsRequestShouldFail()
                            throws UnsupportedRequestTypeException {
        parser.parse( mockWcsGetRequest() );
    }

    private HttpServletRequest mockWfsGetCapabilitiesRequest() {
        Map<String, String[]> parameterMap = createValidGetCapabilitiesParameterMap();
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockWfsGetFeatureRequest() {
        Map<String, String[]> parameterMap = createValidGetFeatureParameterMap();
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockWfsDescribeFeatureTypeRequest() {
        Map<String, String[]> parameterMap = createValidDescribeFeatureTypeParameterMap();
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockRequest( Map<String, String[]> parameterMap ) {
        HttpServletRequest servletRequest = mock( HttpServletRequest.class );
        when( servletRequest.getParameterMap() ).thenReturn( parameterMap );
        when( servletRequest.getParameterNames() ).thenReturn( new Vector<String>( parameterMap.keySet() ).elements() );
        if ( parameterMap.get( VERSION_PARAM ) != null ) {
            when( servletRequest.getParameter( VERSION_PARAM ) ).thenReturn( parameterMap.get( VERSION_PARAM )[0] );
            when( servletRequest.getParameterValues( VERSION_PARAM ) ).thenReturn( parameterMap.get( VERSION_PARAM ) );
        }
        if ( parameterMap.get( REQUEST_PARAM ) != null ) {
            when( servletRequest.getParameter( REQUEST_PARAM ) ).thenReturn( parameterMap.get( REQUEST_PARAM )[0] );
            when( servletRequest.getParameterValues( REQUEST_PARAM ) ).thenReturn( parameterMap.get( REQUEST_PARAM ) );
        }
        if ( parameterMap.get( SERVICE_PARAM ) != null ) {
            when( servletRequest.getParameter( SERVICE_PARAM ) ).thenReturn( parameterMap.get( SERVICE_PARAM )[0] );
            when( servletRequest.getParameterValues( SERVICE_PARAM ) ).thenReturn( parameterMap.get( SERVICE_PARAM ) );
        }
        doReturn( "GET" ).when( servletRequest ).getMethod();
        return servletRequest;
    }

    private Map<String, String[]> createValidGetCapabilitiesParameterMap() {
        Map<String, String[]> parameterMap = new HashMap<String, String[]>();
        parameterMap.put( VERSION_PARAM, new String[] { VERSION_110.getVersionString() } );
        parameterMap.put( REQUEST_PARAM, new String[] { GETCAPABILITIES } );
        parameterMap.put( SERVICE_PARAM, new String[] { "wfs" } );
        return parameterMap;
    }

    private Map<String, String[]> createValidGetFeatureParameterMap() {
        Map<String, String[]> parameterMap = new HashMap<String, String[]>();
        parameterMap.put( VERSION_PARAM, new String[] { VERSION_110.getVersionString() } );
        parameterMap.put( REQUEST_PARAM, new String[] { GETFEATURE } );
        parameterMap.put( SERVICE_PARAM, new String[] { "wfs" } );
        return parameterMap;
    }

    private Map<String, String[]> createValidDescribeFeatureTypeParameterMap() {
        Map<String, String[]> parameterMap = new HashMap<String, String[]>();
        parameterMap.put( VERSION_PARAM, new String[] { VERSION_110.getVersionString() } );
        parameterMap.put( REQUEST_PARAM, new String[] { DESCRIBEFEATURETYPE } );
        parameterMap.put( SERVICE_PARAM, new String[] { "wfs" } );
        return parameterMap;
    }

    private HttpServletRequest mockInvalidWfsRequestMissingRequestParameter() {
        Map<String, String[]> parameterMap = createValidGetCapabilitiesParameterMap();
        parameterMap.remove( REQUEST_PARAM );
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockInvalidWfsRequestMissingServiceParameter() {
        Map<String, String[]> parameterMap = createValidGetCapabilitiesParameterMap();
        parameterMap.remove( SERVICE_PARAM );
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockInvalidWfsRequestDoubleRequestParameter( String requestType ) {
        Map<String, String[]> parameterMap = createValidGetCapabilitiesParameterMap();
        parameterMap.put( "request", new String[] { requestType } );
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockInvalidWfsRequestDoubleServiceParameter( String serviceType ) {
        Map<String, String[]> parameterMap = createValidGetCapabilitiesParameterMap();
        parameterMap.put( "service", new String[] { serviceType } );
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockInvalidWfsRequestDoubleVersionParameter( String version ) {
        Map<String, String[]> parameterMap = createValidGetCapabilitiesParameterMap();
        parameterMap.put( "version", new String[] { version } );
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockValidWfsRequestMissingVersionParameter() {
        Map<String, String[]> parameterMap = createValidGetCapabilitiesParameterMap();
        parameterMap.remove( VERSION_PARAM );
        return mockRequest( parameterMap );
    }

    private HttpServletRequest mockWcsGetRequest() {
        Map<String, String[]> parameterMap = createWcsParameterMap();
        return mockRequest( parameterMap );
    }

    private Map<String, String[]> createWcsParameterMap() {
        Map<String, String[]> parameterMap = new HashMap<String, String[]>();
        parameterMap.put( VERSION_PARAM, new String[] { "1.0.0" } );
        parameterMap.put( REQUEST_PARAM, new String[] { "GetCapabilities" } );
        parameterMap.put( SERVICE_PARAM, new String[] { "wcs" } );
        return parameterMap;
    }

}

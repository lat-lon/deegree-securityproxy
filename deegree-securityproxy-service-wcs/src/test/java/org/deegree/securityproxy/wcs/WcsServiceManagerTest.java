package org.deegree.securityproxy.wcs;

import org.deegree.securityproxy.authorization.RequestAuthorizationManager;
import org.deegree.securityproxy.filter.StatusCodeResponseBodyWrapper;
import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.request.OwsRequestParser;
import org.deegree.securityproxy.responsefilter.ResponseFilterManager;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for WcsServiceManager.
 *
 * @author <a href="stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * @version $Revision: $, $Date: $
 */
public class WcsServiceManagerTest {

    private WcsServiceManager wcsServiceManager;

    private OwsRequestParser parser;

    private RequestAuthorizationManager requestAuthorizationManager;

    private ResponseFilterManager filterManager;

    @Before
    public void reset() {
        parser = mockOwsRequestParser();
        requestAuthorizationManager = mockRequestAuthorizationManager();
        filterManager = mockResponseFilterManager();
        wcsServiceManager = new WcsServiceManager( parser, requestAuthorizationManager, filterManager );
    }

    @Test
    public void testParse() throws Exception {
        HttpServletRequest request = mockHttpServletRequest();
        wcsServiceManager.parse( request );

        verify( parser ).parse( any( HttpServletRequest.class ) );
    }

    @Test
    public void testAuthorize() throws Exception {
        Authentication authentication = mockAuthentication();
        OwsRequest owsRequest = mockOwsRequest();
        wcsServiceManager.authorize( authentication, owsRequest );

        verify( requestAuthorizationManager ).decide( any( Authentication.class ), any( OwsRequest.class ) );
    }

    @Test
    public void testIsResponseFilterEnabled() throws Exception {
        OwsRequest owsRequest = mockOwsRequest();
        wcsServiceManager.isResponseFilterEnabled( owsRequest );

        verify( filterManager ).supports( owsRequest.getClass() );
    }

    @Test
    public void testFilterResponse() throws Exception {
        StatusCodeResponseBodyWrapper wrappedResponse = mockStatusCodeResponseBodyWrapper();
        Authentication authentication = mockAuthentication();
        OwsRequest owsRequest = mockOwsRequest();
        wcsServiceManager.filterResponse( wrappedResponse, authentication, owsRequest );

        verify( filterManager ).filterResponse( any( StatusCodeResponseBodyWrapper.class ), any( OwsRequest.class ),
                                                any( Authentication.class ) );
    }

    @Test
    public void testIsServiceTypeSupportedWithWcsServiceParameterShouldReturnTrue() throws Exception {
        HttpServletRequest request = mockHttpServletRequestWithWcsServiceParameter();
        boolean isSupported = wcsServiceManager.isServiceTypeSupported( request );

        assertThat( isSupported, is( true ) );
    }

    @Test
    public void testIsServiceTypeSupportedWithWmsServiceParameterShouldReturnFalse() throws Exception {
        HttpServletRequest request = mockHttpServletRequestWithWmsServiceParameter();
        boolean isSupported = wcsServiceManager.isServiceTypeSupported( request );

        assertThat( isSupported, is( false ) );
    }

    private ResponseFilterManager mockResponseFilterManager() {
        return mock( ResponseFilterManager.class );
    }

    private RequestAuthorizationManager mockRequestAuthorizationManager() {
        return mock( RequestAuthorizationManager.class );
    }

    private OwsRequestParser mockOwsRequestParser() {
        return mock( OwsRequestParser.class );
    }

    private HttpServletRequest mockHttpServletRequest() {
        return mock( HttpServletRequest.class );
    }

    private OwsRequest mockOwsRequest() {
        return mock( OwsRequest.class );
    }

    private Authentication mockAuthentication() {
        return mock( Authentication.class );
    }

    private StatusCodeResponseBodyWrapper mockStatusCodeResponseBodyWrapper() {
        return mock( StatusCodeResponseBodyWrapper.class );
    }

    private HttpServletRequest mockHttpServletRequestWithWcsServiceParameter() {
        HttpServletRequest request = mock( HttpServletRequest.class );
        Map<String, String[]> kvpMap = createKvpMapWithServiceParameter( "wcs" );
        doReturn( kvpMap ).when( request ).getParameterMap();
        return request;
    }

    private HttpServletRequest mockHttpServletRequestWithWmsServiceParameter() {
        HttpServletRequest request = mock( HttpServletRequest.class );
        Map<String, String[]> kvpMap = createKvpMapWithServiceParameter( "wms" );
        doReturn( kvpMap ).when( request ).getParameterMap();
        return request;
    }

    private Map<String, String[]> createKvpMapWithServiceParameter( String serviceValue ) {
        Map<String, String[]> kvpMap = new HashMap<String, String[]>();
        String[] serviceTypes = { serviceValue };
        kvpMap.put( "service", serviceTypes );
        return kvpMap;
    }
}

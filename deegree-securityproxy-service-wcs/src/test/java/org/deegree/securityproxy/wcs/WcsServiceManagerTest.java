package org.deegree.securityproxy.wcs;

import org.deegree.securityproxy.authorization.RequestAuthorizationManager;
import org.deegree.securityproxy.exception.ServiceExceptionWrapper;
import org.deegree.securityproxy.filter.ServiceManager;
import org.deegree.securityproxy.filter.StatusCodeResponseBodyWrapper;
import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.request.parser.OwsRequestParser;
import org.deegree.securityproxy.responsefilter.ResponseFilterManager;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

    private List<ResponseFilterManager> filterManagers;

    private ServiceExceptionWrapper serviceExceptionWrapper;

    @Before
    public void reset() {
        parser = mockOwsRequestParser();
        requestAuthorizationManager = mockRequestAuthorizationManager();
        filterManagers = mockResponseFilterManagers();
        serviceExceptionWrapper = mockServiceExceptionWrapper();
        wcsServiceManager = new WcsServiceManager( parser, requestAuthorizationManager, filterManagers,
                        serviceExceptionWrapper );
    }

    @Test
    public void testParse()
                    throws Exception {
        HttpServletRequest request = mockHttpServletRequest();
        wcsServiceManager.parse( request );

        verify( parser ).parse( request );
    }

    @Test
    public void testAuthorize()
                    throws Exception {
        Authentication authentication = mockAuthentication();
        OwsRequest owsRequest = mockOwsRequest();
        wcsServiceManager.authorize( authentication, owsRequest );

        verify( requestAuthorizationManager ).decide( authentication, owsRequest );
    }

    @Test
    public void testIsResponseFilterEnabled()
                    throws Exception {
        ResponseFilterManager filterManager1 = mockEnabledResponseFilterManager();
        ResponseFilterManager filterManager2 = mockEnabledResponseFilterManager();
        ServiceManager wcsServiceManager = createWcsServiceManagerWithTwoFilterManager( filterManager1, filterManager2 );
        OwsRequest owsRequest = mockOwsRequest();
        boolean isResponseFilterEnabled = wcsServiceManager.isResponseFilterEnabled( owsRequest );

        assertThat( isResponseFilterEnabled, is( true ) );
        verify( filterManager1, times( 1 ) ).canBeFiltered( owsRequest );
        verify( filterManager2, never() ).canBeFiltered( owsRequest );
    }

    @Test
    public void testFilterResponse()
                    throws Exception {
        ResponseFilterManager filterManager1 = mockEnabledResponseFilterManager();
        ResponseFilterManager filterManager2 = mockEnabledResponseFilterManager();
        ServiceManager wcsServiceManager = createWcsServiceManagerWithTwoFilterManager( filterManager1, filterManager2 );
        StatusCodeResponseBodyWrapper wrappedResponse = mockStatusCodeResponseBodyWrapper();
        Authentication authentication = mockAuthentication();
        OwsRequest owsRequest = mockOwsRequest();
        wcsServiceManager.filterResponse( wrappedResponse, authentication, owsRequest );

        verify( filterManager1, times( 1 ) ).filterResponse( wrappedResponse, owsRequest, authentication );
        verify( filterManager2, never() ).filterResponse( wrappedResponse, owsRequest, authentication );
    }

    @Test
    public void testRetrieveServiceExceptionWrapper()
                    throws Exception {
        ServiceExceptionWrapper retrievedServiceExceptionWrapper = wcsServiceManager.retrieveServiceExceptionWrapper();

        assertThat( retrievedServiceExceptionWrapper, CoreMatchers.is( serviceExceptionWrapper ) );
    }

    @Test
    public void testIsServiceTypeSupportedWithWcsServiceParameterShouldReturnTrue()
                    throws Exception {
        HttpServletRequest request = mockHttpServletRequestWithWcsServiceParameter();
        boolean isSupported = wcsServiceManager.isServiceTypeSupported( request );

        assertThat( isSupported, is( true ) );
    }

    @Test
    public void testIsServiceTypeSupportedWithWmsServiceParameterShouldReturnFalse()
                    throws Exception {
        HttpServletRequest request = mockHttpServletRequestWithWmsServiceParameter();
        boolean isSupported = wcsServiceManager.isServiceTypeSupported( request );

        assertThat( isSupported, is( false ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsServiceTypeSupportedWithNoServiceParameterShouldThrowException()
                    throws Exception {
        HttpServletRequest request = mockHttpServletRequest();
        wcsServiceManager.isServiceTypeSupported( request );
    }

    @SuppressWarnings("unchecked")
    private List<ResponseFilterManager> mockResponseFilterManagers() {
        return mock( List.class );
    }

    private ResponseFilterManager mockEnabledResponseFilterManager() {
        ResponseFilterManager responseFilterManager = mock( ResponseFilterManager.class );
        doReturn( true ).when( responseFilterManager ).canBeFiltered( Matchers.any( OwsRequest.class ) );
        return responseFilterManager;
    }

    private RequestAuthorizationManager mockRequestAuthorizationManager() {
        return mock( RequestAuthorizationManager.class );
    }

    private ServiceExceptionWrapper mockServiceExceptionWrapper() {
        return mock( ServiceExceptionWrapper.class );
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
        return mockHttpServletRequestWithServiceParameter( "wcs" );
    }

    private HttpServletRequest mockHttpServletRequestWithWmsServiceParameter() {
        return mockHttpServletRequestWithServiceParameter( "wms" );
    }

    private HttpServletRequest mockHttpServletRequestWithServiceParameter( String serviceValue ) {
        HttpServletRequest request = mock( HttpServletRequest.class );
        Map<String, String[]> kvpMap = createKvpMapWithServiceParameter( serviceValue );
        doReturn( kvpMap ).when( request ).getParameterMap();
        doReturn( "GET" ).when( request ).getMethod();
        return request;
    }

    private Map<String, String[]> createKvpMapWithServiceParameter( String serviceValue ) {
        Map<String, String[]> kvpMap = new HashMap<String, String[]>();
        String[] serviceTypes = { serviceValue };
        kvpMap.put( "service", serviceTypes );
        return kvpMap;
    }

    private ServiceManager createWcsServiceManagerWithTwoFilterManager( ResponseFilterManager filterManager1,
                                                                        ResponseFilterManager filterManager2 ) {
        List<ResponseFilterManager> filterManagers = createFilterManagersWithTwoFilterManager( filterManager1,
                                                                                               filterManager2 );
        return new WcsServiceManager( parser, requestAuthorizationManager, filterManagers, serviceExceptionWrapper );
    }

    private List<ResponseFilterManager> createFilterManagersWithTwoFilterManager( ResponseFilterManager filterManager1,
                                                                                  ResponseFilterManager filterManager2 ) {
        List<ResponseFilterManager> filterManagers = new ArrayList<ResponseFilterManager>();
        filterManagers.add( filterManager1 );
        filterManagers.add( filterManager2 );
        return filterManagers;
    }

}

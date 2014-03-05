package org.deegree.securityproxy.wcs;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.deegree.securityproxy.authorization.RequestAuthorizationManager;
import org.deegree.securityproxy.exception.ServiceExceptionWrapper;
import org.deegree.securityproxy.filter.StatusCodeResponseBodyWrapper;
import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.request.OwsRequestParser;
import org.deegree.securityproxy.responsefilter.ResponseFilterManager;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;

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

    private ServiceExceptionWrapper serviceExceptionWrapper;

    @Before
    public void reset() {
        parser = mockOwsRequestParser();
        requestAuthorizationManager = mockRequestAuthorizationManager();
        filterManager = mockResponseFilterManager();
        serviceExceptionWrapper = mockServiceExceptionWrapper();
        wcsServiceManager = new WcsServiceManager( parser, requestAuthorizationManager, filterManager,
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
        OwsRequest owsRequest = mockOwsRequest();
        wcsServiceManager.isResponseFilterEnabled( owsRequest );

        verify( filterManager ).supports( owsRequest.getClass() );
    }

    @Test
    public void testFilterResponse()
                            throws Exception {
        StatusCodeResponseBodyWrapper wrappedResponse = mockStatusCodeResponseBodyWrapper();
        Authentication authentication = mockAuthentication();
        OwsRequest owsRequest = mockOwsRequest();
        wcsServiceManager.filterResponse( wrappedResponse, authentication, owsRequest );

        verify( filterManager ).filterResponse( wrappedResponse, owsRequest, authentication );
    }

    @Test
    public void testretrieveServiceExceptionWrapper()
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

    private ResponseFilterManager mockResponseFilterManager() {
        return mock( ResponseFilterManager.class );
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
        return request;
    }

    private Map<String, String[]> createKvpMapWithServiceParameter( String serviceValue ) {
        Map<String, String[]> kvpMap = new HashMap<String, String[]>();
        String[] serviceTypes = { serviceValue };
        kvpMap.put( "service", serviceTypes );
        return kvpMap;
    }

}
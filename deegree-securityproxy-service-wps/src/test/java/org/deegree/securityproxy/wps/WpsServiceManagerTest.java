package org.deegree.securityproxy.wps;

import org.deegree.securityproxy.authorization.RequestAuthorizationManager;
import org.deegree.securityproxy.exception.ServiceExceptionWrapper;
import org.deegree.securityproxy.filter.StatusCodeResponseBodyWrapper;
import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.request.OwsRequestParser;
import org.deegree.securityproxy.responsefilter.ResponseFilterException;
import org.deegree.securityproxy.responsefilter.ResponseFilterManager;
import org.deegree.securityproxy.responsefilter.logging.ResponseFilterReport;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

/**
 * @author <a href="wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author last edited by: $Author: stenger $
 * @version $Revision: $, $Date: $
 */
public class WpsServiceManagerTest {

    private WpsServiceManager wpsServiceManager;

    private OwsRequestParser parser = mock( OwsRequestParser.class );

    private ServiceExceptionWrapper serviceExceptionWrapper = mock( ServiceExceptionWrapper.class );

    private RequestAuthorizationManager requestAuthorizationManager = mock( RequestAuthorizationManager.class );

    private ResponseFilterReport response = mock( ResponseFilterReport.class );

    @Before
    public void resetMocks() {
        reset( serviceExceptionWrapper, parser, requestAuthorizationManager);
        List<ResponseFilterManager> filterManagers = emptyList();
        wpsServiceManager = new WpsServiceManager( parser, requestAuthorizationManager, filterManagers,
            serviceExceptionWrapper );
    }

    @Test
    public void testParse()
                            throws Exception {
        HttpServletRequest request = mockHttpServletRequest();
        wpsServiceManager.parse( request );

        verify( parser ).parse( request );
    }

    @Test
    public void testAuthorize()
                            throws Exception {
        Authentication authentication = mockAuthentication();
        OwsRequest owsRequest = mockOwsRequest();
        wpsServiceManager.authorize( authentication, owsRequest );

        verify( requestAuthorizationManager ).decide( authentication, owsRequest );
    }

    @Test
    public void testIsResponseFilterEnabledWithoutFilterManagers()
                            throws Exception {
        OwsRequest owsRequest = mockOwsRequest();
        boolean isEnabled = wpsServiceManager.isResponseFilterEnabled( owsRequest );

        assertThat( isEnabled, is( false ) );
    }

    @Test
    public void testIsResponseFilterEnabledWithFilterManager()
                            throws Exception {
        OwsRequest owsRequest = mockOwsRequest();
        boolean isEnabled = createWpsServiceMangerWithFilterManagers().isResponseFilterEnabled( owsRequest );

        assertThat( isEnabled, is( true ) );
    }

    @Test
    public void testFilterResponseWithoutFilterManagers()
                            throws Exception {
        StatusCodeResponseBodyWrapper wrappedResponse = mockStatusCodeResponseBodyWrapper();
        Authentication authentication = mockAuthentication();
        OwsRequest owsRequest = mockOwsRequest();
        ResponseFilterReport responseFilterReport = wpsServiceManager.filterResponse( wrappedResponse, authentication,
                                                                                      owsRequest );

        assertThat( responseFilterReport, notNullValue() );
    }

    @Test
    public void testFilterResponseWithFilterManager()
                            throws Exception {
        StatusCodeResponseBodyWrapper wrappedResponse = mockStatusCodeResponseBodyWrapper();
        Authentication authentication = mockAuthentication();
        OwsRequest owsRequest = mockOwsRequest();
        ResponseFilterReport responseFilterReport = createWpsServiceMangerWithFilterManagers().filterResponse( wrappedResponse,
                                                                                                               authentication,
                                                                                                               owsRequest );

        assertThat( responseFilterReport, is( response ) );
    }

    @Test
    public void testRetrieveServiceExceptionWrapper()
                            throws Exception {
        ServiceExceptionWrapper retrievedServiceExceptionWrapper = wpsServiceManager.retrieveServiceExceptionWrapper();

        assertThat( retrievedServiceExceptionWrapper, is( serviceExceptionWrapper ) );
    }

    @Test
    public void testIsServiceTypeSupportedWithWpsServiceParameterShouldReturnTrue()
                            throws Exception {
        HttpServletRequest request = mockHttpServletRequestWithServiceParameter( "wps" );
        boolean isSupported = wpsServiceManager.isServiceTypeSupported( request );

        assertThat( isSupported, is( true ) );
    }

    @Test
    public void testIsServiceTypeSupportedWithWcsServiceParameterShouldReturnFalse()
                            throws Exception {
        HttpServletRequest request = mockHttpServletRequestWithServiceParameter( "wms" );
        boolean isSupported = wpsServiceManager.isServiceTypeSupported( request );

        assertThat( isSupported, is( false ) );
    }

    private HttpServletRequest mockHttpServletRequest() {
        return mock( HttpServletRequest.class );
    }

    private Authentication mockAuthentication() {
        return mock( Authentication.class );
    }

    private OwsRequest mockOwsRequest() {
        return mock( OwsRequest.class );
    }

    private StatusCodeResponseBodyWrapper mockStatusCodeResponseBodyWrapper() {
        return mock( StatusCodeResponseBodyWrapper.class );
    }

    private WpsServiceManager createWpsServiceMangerWithFilterManagers()
                            throws IllegalArgumentException, ResponseFilterException {
        List<ResponseFilterManager> filterManagers = asList( mockEnabledResponseFilterManager(),
                                                             mockDisabledResponseFilterManager() );
        return new WpsServiceManager( parser, requestAuthorizationManager, filterManagers, serviceExceptionWrapper );
    }

    private ResponseFilterManager mockEnabledResponseFilterManager()
                            throws IllegalArgumentException, ResponseFilterException {
        ResponseFilterManager responseFilterManager = mock( ResponseFilterManager.class );
        doReturn( true ).when( responseFilterManager ).canBeFiltered( Matchers.any( OwsRequest.class ) );
        doReturn( response ).when( responseFilterManager ).filterResponse( Mockito.any( StatusCodeResponseBodyWrapper.class ),
                                                                           Mockito.any( OwsRequest.class ),
                                                                           Mockito.any( Authentication.class ) );
        return responseFilterManager;
    }

    private ResponseFilterManager mockDisabledResponseFilterManager() {
        ResponseFilterManager responseFilterManager = mock( ResponseFilterManager.class );
        doReturn( false ).when( responseFilterManager ).canBeFiltered( Matchers.any( OwsRequest.class ) );
        return responseFilterManager;
    }

    private HttpServletRequest mockHttpServletRequestWithServiceParameter( String serviceParameter ) {
        HttpServletRequest request = mock( HttpServletRequest.class );
        Map<String, String[]> kvpMap = createKvpMapWithServiceParameter( serviceParameter );
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
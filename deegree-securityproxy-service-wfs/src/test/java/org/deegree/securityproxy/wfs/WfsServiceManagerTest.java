package org.deegree.securityproxy.wfs;

import org.deegree.securityproxy.authorization.logging.AuthorizationReport;
import org.deegree.securityproxy.exception.ServiceExceptionWrapper;
import org.deegree.securityproxy.filter.StatusCodeResponseBodyWrapper;
import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.request.parser.OwsRequestParser;
import org.deegree.securityproxy.request.parser.ServiceTypeParser;
import org.deegree.securityproxy.responsefilter.ResponseFilterException;
import org.deegree.securityproxy.responsefilter.ResponseFilterManager;
import org.deegree.securityproxy.responsefilter.logging.ResponseFilterReport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link WfsServiceManager}.
 * 
 * @author <a href="stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * @version $Revision: $, $Date: $
 */
public class WfsServiceManagerTest {

    private final OwsRequestParser parser = mockOwsRequestParser();

    private final ServiceExceptionWrapper serviceExceptionWrapper = mockServiceExceptionWrapper();

    private final ResponseFilterReport response = mockResponseFilterReport();

    private final Map<String, String[]> additionalKeyValuePairs = createAdditionalKeyValuePairs();

    private WfsServiceManager wfsServiceManager;

    @Before
    public void resetMocks() {
        reset( serviceExceptionWrapper );
        List<ResponseFilterManager> filterManagers = emptyList();
        wfsServiceManager = new WfsServiceManager( parser, filterManagers, serviceExceptionWrapper,
                        additionalKeyValuePairs );
    }

    @Test
    public void testParse()
                    throws Exception {
        HttpServletRequest request = mockHttpServletRequest();
        wfsServiceManager.parse( request );

        verify( parser ).parse( request );
    }

    @Test
    public void testAuthorize()
                    throws Exception {
        Authentication authentication = mockAuthentication();
        OwsRequest owsRequest = mockOwsRequest();
        AuthorizationReport report = wfsServiceManager.authorize( authentication, owsRequest );

        assertThat( report.isAuthorized(), is( true ) );
        assertThat( report.getAdditionalKeyValuePairs(), is( additionalKeyValuePairs ) );
    }

    @Test
    public void testIsResponseFilterEnabledWithoutFilterManagers()
                    throws Exception {
        OwsRequest owsRequest = mockOwsRequest();
        boolean isEnabled = wfsServiceManager.isResponseFilterEnabled( owsRequest );

        assertThat( isEnabled, is( false ) );
    }

    @Test
    public void testFilterResponseWithoutFilterManagers()
                    throws Exception {
        StatusCodeResponseBodyWrapper wrappedResponse = mockStatusCodeResponseBodyWrapper();
        Authentication authentication = mockAuthentication();
        OwsRequest owsRequest = mockOwsRequest();
        ResponseFilterReport responseFilterReport = wfsServiceManager.filterResponse( wrappedResponse, authentication,
                                                                                      owsRequest );

        assertThat( responseFilterReport, notNullValue() );
    }

    @Test
    public void testIsResponseFilterEnabledWithFilterManager()
                    throws Exception {
        OwsRequest owsRequest = mockOwsRequest();
        boolean isEnabled = createWfsServiceMangerWithFilterManagers().isResponseFilterEnabled( owsRequest );

        assertThat( isEnabled, is( true ) );
    }

    @Test
    public void testFilterResponseWithFilterManager()
                    throws Exception {
        StatusCodeResponseBodyWrapper wrappedResponse = mockStatusCodeResponseBodyWrapper();
        Authentication authentication = mockAuthentication();
        OwsRequest owsRequest = mockOwsRequest();
        ResponseFilterReport responseFilterReport = createWfsServiceMangerWithFilterManagers().filterResponse( wrappedResponse,
                                                                                                               authentication,
                                                                                                               owsRequest );

        assertThat( responseFilterReport, is( response ) );
    }

    @Test
    public void testRetrieveServiceExceptionWrapper()
                    throws Exception {
        ServiceExceptionWrapper retrievedServiceExceptionWrapper = wfsServiceManager.retrieveServiceExceptionWrapper();

        assertThat( retrievedServiceExceptionWrapper, is( serviceExceptionWrapper ) );
    }

    @Test
    public void testIsServiceTypeSupportedWithWfsServiceParameterShouldReturnTrue()
                    throws Exception {
        HttpServletRequest request = mockHttpServletRequestWithWfsServiceParameter();
        String serviceType = new ServiceTypeParser().determineServiceType( request );
        boolean isSupported = wfsServiceManager.isServiceTypeSupported( serviceType, request );

        assertThat( isSupported, is( true ) );
    }

    @Test
    public void testIsServiceTypeSupportedWithWcsServiceParameterShouldReturnFalse()
                    throws Exception {
        HttpServletRequest request = mockHttpServletRequestWithWcsServiceParameter();
        String serviceType = new ServiceTypeParser().determineServiceType( request );
        boolean isSupported = wfsServiceManager.isServiceTypeSupported( serviceType, request );

        assertThat( isSupported, is( false ) );
    }

    @Test
    public void testIsServiceTypeSupportedWithNoServiceParameterShouldReturnFalse()
                    throws Exception {
        HttpServletRequest request = mockHttpServletRequest();
        String serviceType = new ServiceTypeParser().determineServiceType( request );
        boolean isSupported = wfsServiceManager.isServiceTypeSupported( serviceType, request );

        assertThat( isSupported, is( false ) );
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

    private HttpServletRequest mockHttpServletRequestWithWfsServiceParameter() {
        return mockHttpServletRequestWithServiceParameter( "wfs" );
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

    private WfsServiceManager createWfsServiceMangerWithFilterManagers()
                    throws IllegalArgumentException, ResponseFilterException {
        List<ResponseFilterManager> filterManagers = asList( mockEnabledResponseFilterManager(),
                                                             mockDisabledResponseFilterManager() );
        return new WfsServiceManager( parser, filterManagers, serviceExceptionWrapper, additionalKeyValuePairs );
    }

    private ResponseFilterManager mockEnabledResponseFilterManager()
                    throws IllegalArgumentException, ResponseFilterException {
        ResponseFilterManager responseFilterManager = mock( ResponseFilterManager.class );
        doReturn( true ).when( responseFilterManager ).canBeFiltered( any( OwsRequest.class ) );
        doReturn( response ).when( responseFilterManager ).filterResponse( any( StatusCodeResponseBodyWrapper.class ),
                                                                           any( OwsRequest.class ),
                                                                           any( Authentication.class ) );
        return responseFilterManager;
    }

    private ResponseFilterManager mockDisabledResponseFilterManager() {
        ResponseFilterManager responseFilterManager = mock( ResponseFilterManager.class );
        doReturn( false ).when( responseFilterManager ).canBeFiltered( any( OwsRequest.class ) );
        return responseFilterManager;
    }

    private Map<String, String[]> createAdditionalKeyValuePairs() {
        Map<String, String[]> additionalKeyValuePairs = new HashMap<String, String[]>();
        additionalKeyValuePairs.put( "key1", new String[] { "value1" } );
        return additionalKeyValuePairs;
    }

    private ServiceExceptionWrapper mockServiceExceptionWrapper() {
        return mock( ServiceExceptionWrapper.class );
    }

    private ResponseFilterReport mockResponseFilterReport() {
        return mock( ResponseFilterReport.class );
    }

}

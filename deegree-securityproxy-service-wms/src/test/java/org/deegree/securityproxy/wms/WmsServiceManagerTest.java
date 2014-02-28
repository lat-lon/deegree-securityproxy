package org.deegree.securityproxy.wms;

import org.deegree.securityproxy.authorization.RequestAuthorizationManager;
import org.deegree.securityproxy.filter.StatusCodeResponseBodyWrapper;
import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.request.OwsRequestParser;
import org.deegree.securityproxy.responsefilter.ResponseFilterManager;
import org.deegree.securityproxy.responsefilter.logging.ResponseFilterReport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;


/**
 * @author <a href="wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author last edited by: $Author: stenger $
 * @version $Revision: $, $Date: $
 */
public class WmsServiceManagerTest {

    private WmsServiceManager wmsServiceManager;

    private OwsRequestParser parser;

    private RequestAuthorizationManager requestAuthorizationManager;

    @Before
    public void reset() {
        parser = mockOwsRequestParser();
        requestAuthorizationManager = mockRequestAuthorizationManager();
        wmsServiceManager = new WmsServiceManager( parser, requestAuthorizationManager );
    }

    @Test
    public void testParse()
          throws Exception {
        HttpServletRequest request = mockHttpServletRequest();
        wmsServiceManager.parse( request );

        verify( parser ).parse( request );
    }

    @Test
    public void testAuthorize()
          throws Exception {
        Authentication authentication = mockAuthentication();
        OwsRequest owsRequest = mockOwsRequest();
        wmsServiceManager.authorize( authentication, owsRequest );

        verify( requestAuthorizationManager ).decide( authentication, owsRequest );
    }

    @Test
    public void testIsResponseFilterEnabled()
          throws Exception {
        OwsRequest owsRequest = mockOwsRequest();
        boolean isEnabled = wmsServiceManager.isResponseFilterEnabled( owsRequest );

        assertThat( isEnabled, is( false ) );
    }

    @Test
    public void testFilterResponse()
          throws Exception {
        StatusCodeResponseBodyWrapper wrappedResponse = mockStatusCodeResponseBodyWrapper();
        Authentication authentication = mockAuthentication();
        OwsRequest owsRequest = mockOwsRequest();
        ResponseFilterReport responseFilterReport = wmsServiceManager.filterResponse( wrappedResponse,
                                                                                      authentication,
                                                                                      owsRequest );

        assertThat( responseFilterReport, nullValue() );
    }

    @Test
    public void testIsServiceTypeSupportedWithWmsServiceParameterShouldReturnTrue()
          throws Exception {
        HttpServletRequest request = mockHttpServletRequestWithWmsServiceParameter();
        boolean isSupported = wmsServiceManager.isServiceTypeSupported( request );

        assertThat( isSupported, is( true ) );
    }

    @Test
    public void testIsServiceTypeSupportedWithWmsServiceParameterShouldReturnFalse()
          throws Exception {
        HttpServletRequest request = mockHttpServletRequestWithWcsServiceParameter();
        boolean isSupported = wmsServiceManager.isServiceTypeSupported( request );

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
package org.deegree.securityproxy.request.parser;

import org.deegree.securityproxy.request.parser.ServiceTypeParser;
import org.junit.Ignore;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link org.deegree.securityproxy.request.parser.ServiceTypeParser}
 *
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * 
 * @version $Revision: $, $Date: $
 */
public class ServiceTypeParserTest {

    @Test
    public void testDetermineServiceTypeWithGetRequest()
                    throws Exception {
        final String serviceType = "wps";
        HttpServletRequest getRequest = mockGetRequest( serviceType );
        String determinedServiceType = new ServiceTypeParser().determineServiceType( getRequest );

        assertThat( determinedServiceType, is( serviceType ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDetermineServiceTypeWithGetRequestWithMissingServiceParameterShouldThrowException()
                    throws Exception {
        HttpServletRequest getRequest = mockGetRequest( null );
        new ServiceTypeParser().determineServiceType( getRequest );
    }

    @Ignore("Has not been implemented, yet")
    @Test
    public void testDetermineServiceTypeWithPostRequest()
                    throws Exception {
        final String serviceType = "wps";
        HttpServletRequest postRequest = mockPostRequest();
        String determinedServiceType = new ServiceTypeParser().determineServiceType( postRequest );

        assertThat( determinedServiceType, is( serviceType ) );
    }

    private HttpServletRequest mockGetRequest( String serviceParameter ) {
        HttpServletRequest request = mock( HttpServletRequest.class );
        if ( serviceParameter != null ) {
            Map<String, String[]> kvpMap = createKvpMap( serviceParameter );
            doReturn( kvpMap ).when( request ).getParameterMap();
        }
        doReturn( "GET" ).when( request ).getMethod();
        return request;
    }

    private Map<String, String[]> createKvpMap( String serviceParameter ) {
        Map<String, String[]> kvpMap = new HashMap<String, String[]>();
        String[] serviceTypes = { serviceParameter };
        kvpMap.put( "service", serviceTypes );
        return kvpMap;
    }

    private HttpServletRequest mockPostRequest() {
        HttpServletRequest request = mock( HttpServletRequest.class );
        doReturn( "POST" ).when( request ).getMethod();
        return request;
    }

}

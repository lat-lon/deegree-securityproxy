package org.deegree.securityproxy.request.parser;

import org.junit.Test;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    @Test
    public void testDetermineServiceTypeWithGetRequestWithMissingServiceParameterShouldReturnNull()
                    throws Exception {
        HttpServletRequest getRequest = mockGetRequest( null );
        String determinedServiceType = new ServiceTypeParser().determineServiceType( getRequest );

        assertThat( determinedServiceType, nullValue() );
    }

    @Test
    public void testDetermineServiceTypeWithPostRequest()
                    throws Exception {
        HttpServletRequest postRequest = mockPostRequest( "GetCapabilities.xml" );
        String determinedServiceType = new ServiceTypeParser().determineServiceType( postRequest );

        assertThat( determinedServiceType, is( "WPS" ) );
    }

    @Test
    public void testDetermineServiceTypeWithPostRequestWithMissingServiceParameterShouldReturnNull()
                    throws Exception {
        HttpServletRequest postRequest = mockPostRequest( "GetCapabilities-MissingServiceParameter.xml" );
        String determinedServiceType = new ServiceTypeParser().determineServiceType( postRequest );

        assertThat( determinedServiceType, nullValue() );
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

    private HttpServletRequest mockPostRequest( String requestResource )
                    throws IOException {
        HttpServletRequest request = mock( HttpServletRequest.class );
        when( request.getServletPath() ).thenReturn( "serviceName" );
        final InputStream requestStream = ServiceTypeParserTest.class.getResourceAsStream( requestResource );
        ServletInputStream servletInputStream = new ServletInputStream() {
            @Override
            public int read()
                            throws IOException {
                return requestStream.read();
            }
        };
        when( request.getInputStream() ).thenReturn( servletInputStream );
        doReturn( "POST" ).when( request ).getMethod();
        return request;
    }

}
package org.deegree.securityproxy.authentication;

import static java.util.Collections.list;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class AddHeaderHttpServletRequestWrapperTest {

    private static final String HEADER_NAME = "headerName";

    private static final String HEADER_VALUE = "headerToken";

    private final HttpServletRequest request = mockRequest();

    @Test
    public void testAddHeaderShouldReturnHeader() {
        AddHeaderHttpServletRequestWrapper requestWrapper = new AddHeaderHttpServletRequestWrapper( request );

        requestWrapper.addHeader( HEADER_NAME, HEADER_VALUE );

        assertThat( requestWrapper.getHeader( HEADER_NAME ), is( HEADER_VALUE ) );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAddHeaderShouldReturnHeaderName() {
        AddHeaderHttpServletRequestWrapper requestWrapper = new AddHeaderHttpServletRequestWrapper( request );

        requestWrapper.addHeader( HEADER_NAME, HEADER_VALUE );

        List<String> list = list( requestWrapper.getHeaderNames() );
        assertThat( list, hasItem( HEADER_NAME ) );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAddHeaderShouldReturnHeaderValues() {
        AddHeaderHttpServletRequestWrapper requestWrapper = new AddHeaderHttpServletRequestWrapper( request );

        requestWrapper.addHeader( HEADER_NAME, HEADER_VALUE );

        List<String> list = list( requestWrapper.getHeaders( HEADER_NAME ) );
        assertThat( list, hasItem( HEADER_VALUE ) );
    }

    private HttpServletRequest mockRequest() {
        return mock( HttpServletRequest.class );
    }

}
package org.deegree.securityproxy.authentication;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static org.deegree.securityproxy.authentication.ServiceExceptionEntryPoint.DEFAULT_BODY;
import static org.deegree.securityproxy.authentication.ServiceExceptionEntryPoint.DEFAULT_STATUS_CODE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.AuthenticationException;

/**
 * @author <a href="goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
public class ServiceExceptionEntryPointTest {

    private static final String PATH_TO_EXCEPTION_FILE = "/ogc_wcs_100_serviceexception.xml";

    private final HttpServletRequest mockRequest = mock( HttpServletRequest.class );

    private final AuthenticationException mockException = mock( AuthenticationException.class );

    private HttpServletResponse mockResponse = mockResponse();

    @Before
    public void resetMocks() {
        reset( mockRequest, mockException );
        mockResponse = mockResponse();
    }

    @Test
    public void testCommenceWithDefaultException()
                            throws IOException, ServletException {
        ServiceExceptionEntryPoint entryPoint = new ServiceExceptionEntryPoint();
        entryPoint.commence( mockRequest, mockResponse, mockException );
        verify( mockResponse ).setStatus( DEFAULT_STATUS_CODE );
        verify( mockResponse.getWriter() ).write( DEFAULT_BODY );
    }

    @Test
    public void testCommenceWithValidPathToExceptionException()
                            throws IOException, ServletException {
        ServiceExceptionEntryPoint entryPoint = new ServiceExceptionEntryPoint( getPathToException(),
                                                                                DEFAULT_STATUS_CODE );
        entryPoint.commence( mockRequest, mockResponse, mockException );

        String expectedBody = readResponseBodyFromFile();
        verify( mockResponse.getWriter() ).write( expectedBody );
    }

    @Test
    public void testCommenceWithCustomStatusCode()
                            throws IOException, ServletException {
        ServiceExceptionEntryPoint entryPoint = new ServiceExceptionEntryPoint( getPathToException(), SC_BAD_REQUEST );
        entryPoint.commence( mockRequest, mockResponse, mockException );
        verify( mockResponse ).setStatus( SC_BAD_REQUEST );
    }

    @Test
    public void testCommenceWithNullExceptionPathShouldWriteDefaultBody()
                            throws IOException, ServletException {
        ServiceExceptionEntryPoint entryPoint = new ServiceExceptionEntryPoint( null, DEFAULT_STATUS_CODE );
        entryPoint.commence( mockRequest, mockResponse, mockException );

        verify( mockResponse.getWriter() ).write( DEFAULT_BODY );
    }

    @Test
    public void testCommenceWithEmptyExceptionPathShouldWriteDefaultBody()
                            throws IOException, ServletException {
        ServiceExceptionEntryPoint entryPoint = new ServiceExceptionEntryPoint( "", DEFAULT_STATUS_CODE );
        entryPoint.commence( mockRequest, mockResponse, mockException );

        verify( mockResponse.getWriter() ).write( DEFAULT_BODY );
    }

    private String getPathToException() {
        return ServiceExceptionEntryPointTest.class.getResource( PATH_TO_EXCEPTION_FILE ).getPath();
    }

    private HttpServletResponse mockResponse() {
        HttpServletResponse mock = mock( HttpServletResponse.class );
        try {
            when( mock.getWriter() ).thenReturn( mock( PrintWriter.class ) );
        } catch ( Exception e ) {
        }
        return mock;
    }

    private String readResponseBodyFromFile()
                            throws IOException {
        InputStream resourceAsStream = ServiceExceptionEntryPointTest.class.getResourceAsStream( PATH_TO_EXCEPTION_FILE );
        return IOUtils.toString( resourceAsStream );
    }

}

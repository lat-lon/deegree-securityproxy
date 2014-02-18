package org.deegree.securityproxy.authentication;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static org.deegree.securityproxy.authentication.ServiceExceptionHandler.DEFAULT_AUTHORIZATION_DENIED_STATUS_CODE;
import static org.deegree.securityproxy.authentication.ServiceExceptionHandler.DEFAULT_BODY;
import static org.deegree.securityproxy.authentication.ServiceExceptionHandler.DEFAULT_AUTHENTICATION_DENIED_STATUS_CODE;
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
public class ServiceExceptionHandlerTest {

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
        ServiceExceptionHandler entryPoint = new ServiceExceptionHandler();
        entryPoint.commence( mockRequest, mockResponse, mockException );
        verify( mockResponse ).setStatus( DEFAULT_AUTHENTICATION_DENIED_STATUS_CODE );
        verify( mockResponse.getWriter() ).write( DEFAULT_BODY );
    }

    @Test
    public void testCommenceWithValidPathToExceptionException()
                            throws IOException, ServletException {
        ServiceExceptionHandler entryPoint = new ServiceExceptionHandler( getPathToException(),
                                                                                DEFAULT_AUTHENTICATION_DENIED_STATUS_CODE, DEFAULT_AUTHORIZATION_DENIED_STATUS_CODE );
        entryPoint.commence( mockRequest, mockResponse, mockException );

        String expectedBody = readResponseBodyFromFile();
        verify( mockResponse.getWriter() ).write( expectedBody );
    }

    @Test
    public void testCommenceWithCustomStatusCode()
                            throws IOException, ServletException {
        ServiceExceptionHandler entryPoint = new ServiceExceptionHandler( getPathToException(), SC_BAD_REQUEST, DEFAULT_AUTHORIZATION_DENIED_STATUS_CODE );
        entryPoint.commence( mockRequest, mockResponse, mockException );
        verify( mockResponse ).setStatus( SC_BAD_REQUEST );
    }

    @Test
    public void testCommenceWithNullExceptionPathShouldWriteDefaultBody()
                            throws IOException, ServletException {
        ServiceExceptionHandler entryPoint = new ServiceExceptionHandler( null, DEFAULT_AUTHENTICATION_DENIED_STATUS_CODE, DEFAULT_AUTHORIZATION_DENIED_STATUS_CODE );
        entryPoint.commence( mockRequest, mockResponse, mockException );

        verify( mockResponse.getWriter() ).write( DEFAULT_BODY );
    }

    @Test
    public void testCommenceWithEmptyExceptionPathShouldWriteDefaultBody()
                            throws IOException, ServletException {
        ServiceExceptionHandler entryPoint = new ServiceExceptionHandler( "", DEFAULT_AUTHENTICATION_DENIED_STATUS_CODE, DEFAULT_AUTHORIZATION_DENIED_STATUS_CODE );
        entryPoint.commence( mockRequest, mockResponse, mockException );

        verify( mockResponse.getWriter() ).write( DEFAULT_BODY );
    }

    private String getPathToException() {
        return ServiceExceptionHandlerTest.class.getResource( PATH_TO_EXCEPTION_FILE ).getPath();
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
        InputStream resourceAsStream = ServiceExceptionHandlerTest.class.getResourceAsStream( PATH_TO_EXCEPTION_FILE );
        return IOUtils.toString( resourceAsStream );
    }

}

package org.deegree.securityproxy.exception;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static org.deegree.securityproxy.exception.ServiceExceptionWrapper.DEFAULT_AUTHENTICATION_DENIED_STATUS_CODE;
import static org.deegree.securityproxy.exception.ServiceExceptionWrapper.DEFAULT_AUTHORIZATION_DENIED_STATUS_CODE;
import static org.deegree.securityproxy.exception.ServiceExceptionWrapper.DEFAULT_BODY;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class ServiceExceptionWrapperTest {

    private static final String PATH_TO_EXCEPTION_FILE = "/ogc_wcs_100_serviceexception.xml";

    @Test
    public void testRetrieveAllWithDefaultException()
                            throws IOException, ServletException {
        ServiceExceptionWrapper exceptionWrapper = new ServiceExceptionWrapper();

        assertThat( exceptionWrapper.retrieveAuthenticationDeniedExceptionBody(), is( DEFAULT_BODY ) );
        assertThat( exceptionWrapper.retrieveAuthorizationDeniedExceptionBody(), is( DEFAULT_BODY ) );
        assertThat( exceptionWrapper.retrieveAuthenticationDeniedStatusCode(),
                    is( DEFAULT_AUTHENTICATION_DENIED_STATUS_CODE ) );
        assertThat( exceptionWrapper.retrieveAuthorizationDeniedStatusCode(),
                    is( DEFAULT_AUTHORIZATION_DENIED_STATUS_CODE ) );
    }

    @Test
    public void testRetrieveExceptionBodiesWithValidPath()
                            throws IOException, ServletException {
        ServiceExceptionWrapper exceptionWrapper = new ServiceExceptionWrapper(
                                                                                getPathToException(),
                                                                                DEFAULT_AUTHENTICATION_DENIED_STATUS_CODE,
                                                                                DEFAULT_AUTHORIZATION_DENIED_STATUS_CODE );

        String expectedBody = readResponseBodyFromFile();
        assertThat( exceptionWrapper.retrieveAuthenticationDeniedExceptionBody(), is( expectedBody ) );
        assertThat( exceptionWrapper.retrieveAuthorizationDeniedExceptionBody(), is( expectedBody ) );
    }

    @Test
    public void testRetrieveAuthenticationDeniedStatusCodeWithCustomStatusCode()
                            throws IOException, ServletException {
        ServiceExceptionWrapper exceptionWrapper = new ServiceExceptionWrapper( getPathToException(), SC_BAD_REQUEST,
                                                                                DEFAULT_AUTHORIZATION_DENIED_STATUS_CODE );
        assertThat( exceptionWrapper.retrieveAuthenticationDeniedStatusCode(), is( SC_BAD_REQUEST ) );
    }

    @Test
    public void testRetrieveAuthorizationDeniedStatusCodeWithCustomStatusCode()
                            throws IOException, ServletException {
        ServiceExceptionWrapper exceptionWrapper = new ServiceExceptionWrapper(
                                                                                getPathToException(),
                                                                                DEFAULT_AUTHORIZATION_DENIED_STATUS_CODE,
                                                                                SC_BAD_REQUEST );
        assertThat( exceptionWrapper.retrieveAuthorizationDeniedStatusCode(), is( SC_BAD_REQUEST ) );
    }

    @Test
    public void testRetrieveExceptionBodiesWithNullExceptionPathShouldWriteDefaultBody()
                            throws IOException, ServletException {
        ServiceExceptionWrapper exceptionWrapper = new ServiceExceptionWrapper(
                                                                                null,
                                                                                DEFAULT_AUTHENTICATION_DENIED_STATUS_CODE,
                                                                                DEFAULT_AUTHORIZATION_DENIED_STATUS_CODE );

        assertThat( exceptionWrapper.retrieveAuthenticationDeniedExceptionBody(), is( DEFAULT_BODY ) );
        assertThat( exceptionWrapper.retrieveAuthorizationDeniedExceptionBody(), is( DEFAULT_BODY ) );
    }

    @Test
    public void testRetrieveExceptionBodiesWithEmptyExceptionPathShouldWriteDefaultBody()
                            throws IOException, ServletException {
        ServiceExceptionWrapper exceptionWrapper = new ServiceExceptionWrapper(
                                                                                "",
                                                                                DEFAULT_AUTHENTICATION_DENIED_STATUS_CODE,
                                                                                DEFAULT_AUTHORIZATION_DENIED_STATUS_CODE );

        assertThat( exceptionWrapper.retrieveAuthenticationDeniedExceptionBody(), is( DEFAULT_BODY ) );
        assertThat( exceptionWrapper.retrieveAuthorizationDeniedExceptionBody(), is( DEFAULT_BODY ) );
    }

    private String getPathToException() {
        return ServiceExceptionHandlerTest.class.getResource( PATH_TO_EXCEPTION_FILE ).getPath();
    }

    private String readResponseBodyFromFile()
                            throws IOException {
        InputStream resourceAsStream = ServiceExceptionHandlerTest.class.getResourceAsStream( PATH_TO_EXCEPTION_FILE );
        return IOUtils.toString( resourceAsStream );
    }

}
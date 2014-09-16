package org.deegree.securityproxy.exception;

import org.deegree.securityproxy.request.parser.ServiceTypeParser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static org.deegree.securityproxy.exception.ServiceExceptionWrapper.DEFAULT_AUTHENTICATION_DENIED_STATUS_CODE;
import static org.deegree.securityproxy.exception.ServiceExceptionWrapper.DEFAULT_AUTHORIZATION_DENIED_STATUS_CODE;
import static org.deegree.securityproxy.exception.ServiceExceptionWrapper.DEFAULT_BODY;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author <a href="goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
public class ServiceExceptionHandlerTest {

    private static final int AUTHENTICATION_DENIED_STATUS_CODE = 401;

    private static final String AUTHENTICATION_DENIED_BODY = "Authentication denied";

    private static final int AUTHOTIZATION_DENIED_STATUS_CODE = 303;

    private static final String AUTHOTIZATION_DENIED_BODY = "Authorization denied";

    private final HttpServletRequest mockRequest = mock( HttpServletRequest.class );

    private final AuthenticationException mockException = mock( AuthenticationException.class );

    private final AccessDeniedException mockAccessDeniedException = mock( AccessDeniedException.class );

    private HttpServletResponse mockResponse = mockResponse();

    @Before
    public void resetMocks() {
        reset( mockRequest, mockException );
        mockResponse = mockResponse();
    }

    @Test
    public void testCommenceWithResponsibleManager()
                    throws IOException, ServletException {
        HttpServletRequest request = mockRequest;
        ServiceExceptionHandler entryPoint = createServiceExceptionHandlerWithManager( request );

        entryPoint.commence( request, mockResponse, mockException );

        verify( mockResponse.getWriter() ).write( AUTHENTICATION_DENIED_BODY );
        verify( mockResponse ).setStatus( AUTHENTICATION_DENIED_STATUS_CODE );
    }

    @Test
    public void testCommenceWithoutResponsibleManagers()
                    throws IOException, ServletException {
        ServiceExceptionHandler entryPoint = createServiceExceptionHandlerWithoutResponibleManager();

        entryPoint.commence( mockRequest, mockResponse, mockException );

        verify( mockResponse ).setStatus( DEFAULT_AUTHENTICATION_DENIED_STATUS_CODE );
        verify( mockResponse.getWriter() ).write( DEFAULT_BODY );
    }

    @Test
    public void testCommenceWithoutManagers()
                    throws IOException, ServletException {
        ServiceExceptionHandler entryPoint = createServiceExceptionHandlerWithoutManagers();

        entryPoint.commence( mockRequest, mockResponse, mockException );

        verify( mockResponse ).setStatus( DEFAULT_AUTHENTICATION_DENIED_STATUS_CODE );
        verify( mockResponse.getWriter() ).write( DEFAULT_BODY );
    }

    @Test
    public void testHandleWithResponsibleManager()
                    throws IOException, ServletException {
        HttpServletRequest request = mockRequest;
        ServiceExceptionHandler entryPoint = createServiceExceptionHandlerWithManager( request );

        entryPoint.handle( mockRequest, mockResponse, mockAccessDeniedException );

        verify( mockResponse.getWriter() ).write( AUTHOTIZATION_DENIED_BODY );
        verify( mockResponse ).setStatus( AUTHOTIZATION_DENIED_STATUS_CODE );
    }

    @Test
    public void testHandleWithoutResponsibleManagers()
                    throws IOException, ServletException {
        ServiceExceptionHandler entryPoint = createServiceExceptionHandlerWithoutResponibleManager();

        entryPoint.handle( mockRequest, mockResponse, mockAccessDeniedException );

        verify( mockResponse ).setStatus( DEFAULT_AUTHORIZATION_DENIED_STATUS_CODE );
        verify( mockResponse.getWriter() ).write( DEFAULT_BODY );
    }

    @Test
    public void testHandleWithoutManagers()
                    throws IOException, ServletException {
        ServiceExceptionHandler entryPoint = createServiceExceptionHandlerWithoutManagers();

        entryPoint.handle( mockRequest, mockResponse, mockAccessDeniedException );

        verify( mockResponse ).setStatus( DEFAULT_AUTHORIZATION_DENIED_STATUS_CODE );
        verify( mockResponse.getWriter() ).write( DEFAULT_BODY );
    }

    private ServiceExceptionHandler createServiceExceptionHandlerWithoutManagers() {
        List<ServiceExceptionManager> serviceExceptionManagers = new ArrayList<ServiceExceptionManager>();
        return new ServiceExceptionHandler( serviceExceptionManagers );
    }

    private ServiceExceptionHandler createServiceExceptionHandlerWithoutResponibleManager() {
        return createServiceExceptionHandler( mockRequest, false );
    }

    private ServiceExceptionHandler createServiceExceptionHandlerWithManager( HttpServletRequest httpServletRequest ) {
        return createServiceExceptionHandler( mockRequest, true );
    }

    private ServiceExceptionHandler createServiceExceptionHandler( HttpServletRequest httpServletRequest,
                                                                   boolean isSupported ) {
        List<ServiceExceptionManager> serviceExceptionManagers = new ArrayList<ServiceExceptionManager>();
        ServiceExceptionManager serviceExceptionManager = mock( ServiceExceptionManager.class );
        String serviceType = new ServiceTypeParser().determineServiceType( httpServletRequest );
        when( serviceExceptionManager.isServiceTypeSupported( serviceType, httpServletRequest ) ).thenReturn( isSupported );
        ServiceExceptionWrapper serviceExceptionWrapper = mockServiceExceptionWrapper();
        when( serviceExceptionManager.retrieveServiceExceptionWrapper() ).thenReturn( serviceExceptionWrapper );
        serviceExceptionManagers.add( serviceExceptionManager );
        return new ServiceExceptionHandler( serviceExceptionManagers );
    }

    private ServiceExceptionWrapper mockServiceExceptionWrapper() {
        ServiceExceptionWrapper serviceExceptionWrapper = mock( ServiceExceptionWrapper.class );
        when( serviceExceptionWrapper.retrieveAuthenticationDeniedStatusCode() ).thenReturn( AUTHENTICATION_DENIED_STATUS_CODE );
        when( serviceExceptionWrapper.retrieveAuthenticationDeniedExceptionBody() ).thenReturn( AUTHENTICATION_DENIED_BODY );
        when( serviceExceptionWrapper.retrieveAuthorizationDeniedExceptionBody() ).thenReturn( AUTHOTIZATION_DENIED_BODY );
        when( serviceExceptionWrapper.retrieveAuthorizationDeniedStatusCode() ).thenReturn( AUTHOTIZATION_DENIED_STATUS_CODE );
        return serviceExceptionWrapper;
    }

    private HttpServletResponse mockResponse() {
        HttpServletResponse mock = mock( HttpServletResponse.class );
        try {
            when( mock.getWriter() ).thenReturn( mock( PrintWriter.class ) );
        } catch ( Exception e ) {
        }
        return mock;
    }

}
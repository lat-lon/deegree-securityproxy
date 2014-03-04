package org.deegree.securityproxy;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.deegree.securityproxy.filter.ServiceManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * Custom Entry Point to put a configurable exception message and status code into the http response.
 * 
 * @author <a href="goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
public class ServiceExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final List<ServiceExceptionManager> serviceExceptionManagers;

    /**
     * @param serviceManagers
     *            list of {@link ServiceManager}
     */
    public ServiceExceptionHandler( List<ServiceExceptionManager> serviceManagers ) {
        this.serviceExceptionManagers = serviceManagers;
    }

    @Override
    public void commence( HttpServletRequest request, HttpServletResponse response,
                          AuthenticationException authenticationException )
                            throws IOException, ServletException {
        for ( ServiceExceptionManager serviceManager : serviceExceptionManagers ) {
            if ( serviceManager.isServiceTypeSupported( request ) ) {
                ServiceExceptionWrapper serviceExceptionWrapper = serviceManager.retrieveServiceExceptionWrapper();
                authenticationDenied( response, serviceExceptionWrapper );
                return;
            }
        }
        authenticationDenied( response, new ServiceExceptionWrapper() );
    }

    @Override
    public void handle( HttpServletRequest request, HttpServletResponse response,
                        AccessDeniedException accessDeniedException )
                            throws IOException, ServletException {
        for ( ServiceExceptionManager serviceManager : serviceExceptionManagers ) {
            if ( serviceManager.isServiceTypeSupported( request ) ) {
                ServiceExceptionWrapper serviceExceptionWrapper = serviceManager.retrieveServiceExceptionWrapper();
                authorizationDenied( response, serviceExceptionWrapper );
                return;
            }
        }
        authorizationDenied( response, new ServiceExceptionWrapper() );
    }

    private void authenticationDenied( HttpServletResponse response, ServiceExceptionWrapper serviceExceptionWrapper )
                            throws IOException {
        response.setStatus( serviceExceptionWrapper.retrieveAuthenticationDeniedStatusCode() );
        response.getWriter().write( serviceExceptionWrapper.retrieveAuthenticationDeniedExceptionBody() );
    }

    private void authorizationDenied( HttpServletResponse response, ServiceExceptionWrapper serviceExceptionWrapper )
                            throws IOException {
        response.setStatus( serviceExceptionWrapper.retrieveAuthorizationDeniedStatusCode() );
        response.getWriter().write( serviceExceptionWrapper.retrieveAuthorizationDeniedExceptionBody() );
    }

}
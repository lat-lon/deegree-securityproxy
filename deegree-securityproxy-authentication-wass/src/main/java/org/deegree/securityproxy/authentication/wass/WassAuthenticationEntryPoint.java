package org.deegree.securityproxy.authentication.wass;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class WassAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence( HttpServletRequest request, HttpServletResponse response,
                          AuthenticationException authException )
                            throws IOException, ServletException {
        response.setStatus( 401 );
        response.getWriter().write( "Access Denied!" );
    }

}

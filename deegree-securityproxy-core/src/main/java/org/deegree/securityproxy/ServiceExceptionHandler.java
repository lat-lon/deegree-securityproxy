package org.deegree.securityproxy;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
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

    private static final Logger LOG = Logger.getLogger( ServiceExceptionHandler.class );

    public static final String DEFAULT_BODY = "Access Denied";

    public static final int DEFAULT_AUTHENTICATION_DENIED_STATUS_CODE = SC_UNAUTHORIZED;

    public static final int DEFAULT_AUTHORIZATION_DENIED_STATUS_CODE = SC_FORBIDDEN;

    private final String exceptionBody;

    private final int authenticationDeniedStatusCode;

    private final int authorizationDeniedStatusCode;

    /**
     * ServiceExceptionEntryPoint.DEFAULT_BODY and ServiceExceptionEntryPoint.DEFAULT_STATUS_CODE are used as exception
     * body an status code
     */
    public ServiceExceptionHandler() {
        exceptionBody = DEFAULT_BODY;
        authenticationDeniedStatusCode = DEFAULT_AUTHENTICATION_DENIED_STATUS_CODE;
        authorizationDeniedStatusCode = DEFAULT_AUTHORIZATION_DENIED_STATUS_CODE;
    }

    /**
     * @param pathToExceptionFile
     *            the path to the exception file relative from this class. If <code>null</code> or empty the
     *            ServiceExceptionEntryPoint.DEFAULT_BODY is used
     * @param authenticationDeniedStatusCode
     *            the status code to set
     */
    public ServiceExceptionHandler( String pathToExceptionFile, int authenticationDeniedStatusCode,
                                    int authorizationDeniedStatusCode ) {
        this.exceptionBody = readExceptionBodyFromFile( pathToExceptionFile );
        this.authenticationDeniedStatusCode = authenticationDeniedStatusCode;
        this.authorizationDeniedStatusCode = authorizationDeniedStatusCode;
    }

    @Override
    public void commence( HttpServletRequest request, HttpServletResponse response,
                          AuthenticationException authenticationException )
                            throws IOException, ServletException {
        response.setStatus( authenticationDeniedStatusCode );
        response.getWriter().write( exceptionBody );
    }

    @Override
    public void handle( HttpServletRequest request, HttpServletResponse response,
                        AccessDeniedException accessDeniedException )
                            throws IOException, ServletException {
        response.setStatus( authorizationDeniedStatusCode );
        response.getWriter().write( exceptionBody );
    }

    private String readExceptionBodyFromFile( String pathToExceptionFile ) {
        LOG.info( "Reading exception body from " + pathToExceptionFile );
        if ( pathToExceptionFile != null && pathToExceptionFile.length() > 0 ) {
            InputStream exceptionAsStream = null;
            try {
                File exceptionFile = new File( pathToExceptionFile );
                exceptionAsStream = new FileInputStream( exceptionFile );
                return IOUtils.toString( exceptionAsStream );
            } catch ( FileNotFoundException e ) {
                LOG.warn( "Could not read exception message from file: File not found! Defaulting to " + DEFAULT_BODY );
            } catch ( IOException e ) {
                LOG.warn( "Could not read exception message from file. Defaulting to " + DEFAULT_BODY + "Reason: "
                          + e.getMessage() );
            } finally {
                closeQuietly( exceptionAsStream );
            }
        }
        return DEFAULT_BODY;
    }


}
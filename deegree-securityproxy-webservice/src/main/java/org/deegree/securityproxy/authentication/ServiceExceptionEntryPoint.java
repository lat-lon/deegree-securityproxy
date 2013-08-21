package org.deegree.securityproxy.authentication;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * Custom Entry Point to put a configurable exception message and status code into the http response.
 * 
 * @author <a href="goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
public class ServiceExceptionEntryPoint implements AuthenticationEntryPoint {

    private static Logger LOG = Logger.getLogger( HeaderTokenAuthenticationProvider.class );

    public static final String DEFAULT_BODY = "Access Denied";

    public static final int DEFAULT_STATUS_CODE = SC_FORBIDDEN;

    private String exceptionBody;

    private int statusCode;

    /**
     * ServiceExceptionEntryPoint.DEFAULT_BODY and ServiceExceptionEntryPoint.DEFAULT_STATUS_CODE are used as exception
     * body an status code
     */
    public ServiceExceptionEntryPoint() {
        exceptionBody = DEFAULT_BODY;
        statusCode = DEFAULT_STATUS_CODE;
    }

    /**
     * @param pathToExceptionFile
     *            the path to the exception file relative from this class. If <code>null</code> or empty the
     *            ServiceExceptionEntryPoint.DEFAULT_BODY is used
     * @param statusCode
     *            the status code to set
     */
    public ServiceExceptionEntryPoint( String pathToExceptionFile, int statusCode ) {
        this.exceptionBody = readExceptionBodyFromFile( pathToExceptionFile );
        this.statusCode = statusCode;
    }

    @Override
    public void commence( HttpServletRequest request, HttpServletResponse response,
                          AuthenticationException authenticationException )
                            throws IOException, ServletException {
        response.setStatus( statusCode );
        response.getWriter().write( exceptionBody );
    }

    private String readExceptionBodyFromFile( String pathToExceptionFile ) {
        if ( pathToExceptionFile != null && pathToExceptionFile.length() > 0 ) {
            InputStream exceptionAsStream = ServiceExceptionEntryPoint.class.getResourceAsStream( pathToExceptionFile );
            try {
                if ( exceptionAsStream != null )
                    return IOUtils.toString( exceptionAsStream );
            } catch ( IOException e ) {
                LOG.warn( "Could not read exception message from f. Defaulting to " + DEFAULT_BODY );
            } finally {
                closeQuietly( exceptionAsStream );
            }
        }
        return DEFAULT_BODY;
    }

}
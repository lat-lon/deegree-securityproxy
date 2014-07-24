//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2011 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact information:

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/
package org.deegree.securityproxy.exception;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static org.deegree.securityproxy.exception.ExceptionUtils.readExceptionBodyFromFile;

import java.io.IOException;

import org.deegree.securityproxy.filter.StatusCodeResponseBodyWrapper;

/**
 * Wraps common service exceptions, if an internal error occurs or a bad request (e.g. missing service type) was send.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class OwsServiceExceptionHandler {

    static final String DEFAULT_BODY = "Bad request";

    static final int DEFAULT_STATUS_CODE = SC_BAD_REQUEST;

    private final String exceptionBody;

    private final int exceptionStatusCode;

    /**
     * OwsServiceExceptionWrapper.DEFAULT_BODY and OwsServiceExceptionWrapper.DEFAULT_STATUS_CODE are used as exception
     * body an status code
     */
    public OwsServiceExceptionHandler() {
        this.exceptionBody = DEFAULT_BODY;
        this.exceptionStatusCode = DEFAULT_STATUS_CODE;
    }

    /**
     * 
     * @param pathToExceptionFile
     *            the path to the exception file relative from this class. If <code>null</code> or empty the
     *            ServiceExceptionEntryPoint.DEFAULT_BODY is used. The exception may contain a variable
     *            ${exception.message} and{exception.code} which are replaced by the OwsCommonsException code and
     *            message.
     * @param exceptionStatusCode
     *            the status code to set
     */
    public OwsServiceExceptionHandler( String pathToExceptionFile, int exceptionStatusCode ) {
        this.exceptionBody = readExceptionBodyFromFile( pathToExceptionFile, DEFAULT_BODY );
        this.exceptionStatusCode = exceptionStatusCode;
    }

    /**
     * Writes the exception in the response and sets the status code. Exception variables ${exception.message} and
     * ${exception.code} are replaced by the exception message and code.
     * 
     * @param response
     *            to write the exception in and set the response, never <code>null</code>
     * @param exception
     *            the exception containing the code to write in variable §{exception.code}, if <code>null</code> an
     *            empty string is written
     * @param message
     *            the exception message to write in variable §{exception.message}, if <code>null</code> an empty string
     *            is written
     * @throws IOException
     *             - writing the exception failed
     */
    public void writeException( StatusCodeResponseBodyWrapper response, OwsCommonException exception, String message )
                            throws IOException {
        response.setStatus( exceptionStatusCode );
        String exceptionToWrite = createExceptionBody( exception, message );
        response.getWriter().write( exceptionToWrite );
    }

    private String createExceptionBody( OwsCommonException exception, String message ) {
        String exceptionMsg = message != null ? message : "";
        String adadptedException = exceptionBody.replace( "${exception.message}", exceptionMsg );
        String exceptionCode = exception != null ? exception.getExceptionCode() : "";
        adadptedException = adadptedException.replace( "${exception.code}", exceptionCode );
        return adadptedException;
    }

}
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

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.deegree.securityproxy.exception.ExceptionUtils.readExceptionBodyFromFile;

/**
 * Wraps status code and exception bodys if the authentication and authorization failed.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class ServiceExceptionWrapper {

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
    public ServiceExceptionWrapper() {
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
    public ServiceExceptionWrapper( String pathToExceptionFile, int authenticationDeniedStatusCode,
                                    int authorizationDeniedStatusCode ) {
        this.exceptionBody = readExceptionBodyFromFile( pathToExceptionFile, DEFAULT_BODY );
        this.authenticationDeniedStatusCode = authenticationDeniedStatusCode;
        this.authorizationDeniedStatusCode = authorizationDeniedStatusCode;
    }

    /**
     * @return the status code when authentication is denied
     */
    public int retrieveAuthenticationDeniedStatusCode() {
        return authenticationDeniedStatusCode;
    }

    /**
     * @return the exception body when authentication is denied
     */
    public String retrieveAuthenticationDeniedExceptionBody() {
        return exceptionBody;
    }

    /**
     * @return the status code when authorization is denied
     */
    public int retrieveAuthorizationDeniedStatusCode() {
        return authorizationDeniedStatusCode;
    }

    /**
     * @return the exception body when authorization is denied
     */
    public String retrieveAuthorizationDeniedExceptionBody() {
        return exceptionBody;
    }

}
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
package org.deegree.securityproxy.service.commons.responsefilter;

import org.apache.log4j.Logger;
import org.deegree.securityproxy.filter.StatusCodeResponseBodyWrapper;
import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.responsefilter.ResponseFilterException;
import org.deegree.securityproxy.responsefilter.ResponseFilterManager;
import org.deegree.securityproxy.responsefilter.logging.DefaultResponseFilterReport;
import org.deegree.securityproxy.responsefilter.logging.ResponseFilterReport;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.deegree.securityproxy.service.commons.responsefilter.ResponseFilterUtils.copyBufferedStream;
import static org.deegree.securityproxy.service.commons.responsefilter.ResponseFilterUtils.isException;

/**
 * The {@link AbstractResponseFilterManager} provides filtering of the {@link StatusCodeResponseBodyWrapper} of a
 * {@link OwsRequest}s.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="mailto:wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * 
 * @version $Revision: $, $Date: $
 */
public abstract class AbstractResponseFilterManager implements ResponseFilterManager {

    public static final String NOT_A_CORRECT_REQUEST_MSG = "Request is not supported - filtering not required.";

    public static final String SERVICE_EXCEPTION_MSG = "Response is a ServiceException.";

    private static final Logger LOG = Logger.getLogger( AbstractResponseFilterManager.class );

    @Override
    public ResponseFilterReport filterResponse( StatusCodeResponseBodyWrapper servletResponse, OwsRequest request,
                                                Authentication auth )
                            throws ResponseFilterException {
        checkParameters( servletResponse, request );
        if ( canBeFiltered( request ) ) {
            LOG.info( "Apply filter for response of request " + request );
            try {
                if ( isException( servletResponse ) ) {
                    LOG.debug( "Response contains an exception!" );
                    copyBufferedStream( servletResponse );
                    return new DefaultResponseFilterReport( SERVICE_EXCEPTION_MSG );
                }
                return applyFilter( servletResponse, request, auth );
            } catch ( IOException e ) {
                return handleIOException( servletResponse, e );
            }
        }
        LOG.debug( "Requested service type and operation are not supported by this filter manager and will be ignored!" );
        copyBufferedStream( servletResponse );
        return new DefaultResponseFilterReport( NOT_A_CORRECT_REQUEST_MSG );
    }

    @Override
    public boolean canBeFiltered( OwsRequest request ) {
        checkIfRequestIsNull( request );
        return isCorrectServiceType( request ) && isCorrectRequestParameter( request );
    }

    /**
     * Invoked if service response was not an exception.
     * 
     * @param response
     *            the wrapper around the response, never <code>null</code>
     * @param request
     *            containing the request parameter, never <code>null</code>
     * @param auth
     *            the authentication of the user, never <code>null</code>
     * @return a {@link ResponseFilterReport} containing information about the filtering, never <code>null</code>
     * @throws ResponseFilterException
     * @throws IOException
     */
    protected abstract ResponseFilterReport applyFilter( StatusCodeResponseBodyWrapper response, OwsRequest request,
                                                         Authentication auth )
                            throws ResponseFilterException, IOException;

    /**
     * Invoked when an {@link IOException} is thrown during filtering.
     * 
     * @param response
     *            the wrapper around the response, never <code>null</code>
     * @param exception
     *            the {@link IOException} to handle, never <code>null</code>
     * @return a DefaultResponseFilterReport containing information about the exception.
     * @throws ResponseFilterException
     *             - handling the {@link IOException} forces throwing an exception
     */
    protected abstract ResponseFilterReport handleIOException( StatusCodeResponseBodyWrapper response,
                                                               IOException exception )
                            throws ResponseFilterException;

    /**
     * @param request
     *            containing the request parameter, never <code>null</code>
     * @return <code>true</code> if service is from the supported type, <code>false</code> otherwise
     */
    protected abstract boolean isCorrectServiceType( OwsRequest request );

    /**
     * @param request
     *            containing the request parameter, never <code>null</code>
     * @return <code>true</code> if request parameter is a supported request, <code>false</code> otherwise
     */
    protected abstract boolean isCorrectRequestParameter( OwsRequest request );

    /**
     * Checks if request is <code>null</code>, if <code>null</code> an {@link IllegalArgumentException} is thrown.
     * 
     * @param request
     * @throws IllegalArgumentException
     *             if request is <code>null</code>
     */
    protected void checkIfRequestIsNull( OwsRequest request ) {
        if ( request == null )
            throw new IllegalArgumentException( "Request must not be null!" );
    }

    /**
     * Checks if {@link HttpServletResponse} and {@link OwsRequest} are valid, an {@link IllegalArgumentException} is
     * thrown, if one of the parameter is <code>null</code> or the request is not of the supported service type.
     * 
     * @param servletResponse
     * @param request
     * @throws IllegalArgumentException
     *             - one of the parameter is <code>null</code> of service type is not supported
     */
    protected void checkParameters( HttpServletResponse servletResponse, OwsRequest request ) {
        if ( servletResponse == null )
            throw new IllegalArgumentException( "Parameter servletResponse may not be null!" );
        if ( request == null )
            throw new IllegalArgumentException( "Parameter request may not be null!" );
        if ( !isCorrectServiceType( request ) )
            throw new IllegalArgumentException( "OwsRequest of class " + request.getClass().getCanonicalName()
                                                + " is not supported!" );
    }

}

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
package org.deegree.securityproxy.responsefilter;

import javax.servlet.http.HttpServletResponse;

import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.responsefilter.logging.ResponseFilterReport;
import org.springframework.security.core.Authentication;

/**
 * Implementations provides filtering of {@link OwsRequest}s defined in the {@link #supports(Class)} method.
 * 
 * @author <a href="mailto:erben@lat-lon.de">Alexander Erben</a>
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public interface ResponseFilterManager {

    /**
     * Filters and adjusts the passed response if necessary.
     * 
     * @param servletResponse
     *            the response to filter, the response may be adjusted during filtering, never <code>null</code>
     * @param request
     *            parsed request, never <code>null</code>; If not supported (check with {@link #supports(Class)}) an
     *            {@link IllegalArgumentException} is thrown
     * @param auth
     *            may be <code>null</code>
     * @return the report containing detailed information about the filtering, never <code>null</code>
     * @throws IllegalArgumentException
     *             if one of the required arguments is <code>null</code> or the passed {@link OwsRequest} is not
     *             supported
     */
    ResponseFilterReport filterResponse( HttpServletResponse servletResponse, OwsRequest request, Authentication auth )
                            throws IllegalArgumentException;

    /**
     * Checks if the passed class can be filtered or not.
     * 
     * @param clazz
     *            to check if can handled by this {@link ResponseFilterManager}
     * @return true if the {@link ResponseFilterManager} can handle {@link OwsRequest} implementations of the passed
     *         class, false otherwise or if clazz parameter is <code>null</code>
     */
    <T extends OwsRequest> boolean supports( Class<T> clazz );

}
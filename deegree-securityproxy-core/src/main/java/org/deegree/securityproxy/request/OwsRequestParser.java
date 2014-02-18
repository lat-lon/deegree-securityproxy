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
package org.deegree.securityproxy.request;

import javax.servlet.http.HttpServletRequest;

/**
 * Parsing of {@link OwsRequest}s from a {@link HttpServletRequest}
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public interface OwsRequestParser {

    /**
     * Parses an incoming {@link HttpServletRequest} into a {@link OwsRequest}.
     * 
     * @param request
     *            never <code>null</code>. Must contain the following parameters exactly once ignoring the casing:
     *            "request" and "service". Must contain the following parameter not more than once: "coverage". May
     *            contain the following parameter not more than once: "version".
     * @return {@link WcsRequest}. Never <code>null</code>
     * @throws UnsupportedRequestTypeException
     *             when the given request does not have the correct service type
     */
    OwsRequest parse( HttpServletRequest request )
                            throws UnsupportedRequestTypeException;

}
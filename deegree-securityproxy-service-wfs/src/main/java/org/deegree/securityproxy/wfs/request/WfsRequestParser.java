//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2014 by:
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
package org.deegree.securityproxy.wfs.request;

import javax.servlet.http.HttpServletRequest;

import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.request.OwsRequestParser;
import org.deegree.securityproxy.request.PostOrGetOwsRequestParser;
import org.deegree.securityproxy.request.RequestParsingException;
import org.deegree.securityproxy.request.UnsupportedRequestTypeException;

/**
 * Parses an incoming {@link HttpServletRequest} of all supported request methods into a {@link WfsRequest}.
 * 
 * @deprecated use {@link PostOrGetOwsRequestParser} instead
 * 
 * @author <a href="goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * @version $Revision: $, $Date: $
 */
@Deprecated
public class WfsRequestParser implements OwsRequestParser {

    @Override
    public OwsRequest parse( HttpServletRequest request )
                    throws UnsupportedRequestTypeException, RequestParsingException {
        checkIfRequestIsNotNull( request );
        OwsRequestParser parser = createParser( request );
        return parser.parse( request );
    }

    private OwsRequestParser createParser( HttpServletRequest request ) {
        String method = request.getMethod();
        if ( "GET".equals( method ) )
            return new WfsGetRequestParser();
        else if ( "POST".equals( method ) )
            return new WfsPostRequestParser();
        throw new IllegalArgumentException( "Could not find a parser for request method!" );
    }

    private void checkIfRequestIsNotNull( HttpServletRequest request ) {
        if ( request == null )
            throw new IllegalArgumentException( "Request must not be null!" );
    }

}
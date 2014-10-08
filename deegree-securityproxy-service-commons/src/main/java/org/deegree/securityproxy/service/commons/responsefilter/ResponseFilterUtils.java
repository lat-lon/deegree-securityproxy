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

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.deegree.securityproxy.filter.StatusCodeResponseBodyWrapper;
import org.deegree.securityproxy.responsefilter.ResponseFilterException;

/**
 * Contains useful methods to filter a response.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public final class ResponseFilterUtils {

    private ResponseFilterUtils() {
    }

    /**
     * Checks if the response is an exception (if the status code is not 200 or the body contains the string
     * 'ServiceExceptionReport')
     * 
     * @param servletResponse
     *            to check, never <code>null</code>
     * @return <code>true</code> if the response is an exception, <code>false</code> otherwise
     * @throws IOException
     *             - access to response failed
     */
    public static boolean isException( StatusCodeResponseBodyWrapper servletResponse )
                    throws IOException {
        if ( servletResponse.getStatus() != 200 )
            return true;
        InputStream bufferedStream = servletResponse.getBufferedStream();
        try {
            String bodyAsString = IOUtils.toString( bufferedStream );
            return bodyAsString.contains( "ServiceExceptionReport" ) || bodyAsString.contains( "ExceptionReport" );
        } finally {
            closeQuietly( bufferedStream );
        }
    }

    /**
     * Copies the buffered stream into the real stream.
     * 
     * @param servletResponse
     *            used for copy, never <code>null</code>
     * @throws ResponseFilterException
     *             - copying failed
     */
    public static void copyBufferedStream( StatusCodeResponseBodyWrapper servletResponse )
                    throws ResponseFilterException {
        try {
            servletResponse.copyBufferedStreamToRealStream();
        } catch ( IOException e ) {
            throw new ResponseFilterException( "Buffered response stream could not be copied into real stream", e );
        }
    }

}
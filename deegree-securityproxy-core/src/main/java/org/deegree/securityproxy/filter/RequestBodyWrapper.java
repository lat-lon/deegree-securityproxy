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
package org.deegree.securityproxy.filter;

import static org.apache.commons.io.IOUtils.toByteArray;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Wraps a {@link HttpServletRequest} and allows reading the stream multiple times.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class RequestBodyWrapper extends HttpServletRequestWrapper {

    private byte[] byteArray;

    public RequestBodyWrapper( HttpServletRequest request ) throws IOException {
        super( request );
        byteArray = toByteArray( request.getInputStream() );
    }

    @Override
    public ServletInputStream getInputStream()
                    throws IOException {
        return new ServletInputStream() {

            private final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream( byteArray );

            @Override
            public int read()
                            throws IOException {
                return byteArrayInputStream.read();
            }

            @Override
            public void close()
                            throws IOException {
                super.close();
                byteArrayInputStream.close();
            }
        };
    }

    @Override
    public BufferedReader getReader()
                    throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream( byteArray );
        return new BufferedReader( new InputStreamReader( byteArrayInputStream ) ) {

            @Override
            public void close()
                            throws IOException {
                super.close();
                byteArrayInputStream.close();
            }
        };
    }
}
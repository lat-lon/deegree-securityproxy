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
package org.deegree.securityproxy.wps.request.parser;

import org.deegree.securityproxy.request.OwsServiceVersion;
import org.deegree.securityproxy.wps.request.WpsRequest;
import org.junit.Test;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class WpsPostRequestParserTest {

    private WpsPostRequestParser parser = new WpsPostRequestParser();

    @Test
    public void testParseExecute()
                    throws Exception {
        HttpServletRequest executeRequest = mockExecuteRequest();
        WpsRequest request = (WpsRequest) parser.parse( executeRequest );

        assertThat( request.getOperationType(), is( "Execute" ) );
        assertThat( request.getServiceType(), is( "wps" ) );
        assertThat( request.getServiceVersion(), is( new OwsServiceVersion( 1, 0, 0 ) ) );
        assertThat( request.getIdentifiers().size(), is( 1 ) );
        assertThat( request.getIdentifiers(), hasItem( "Buffer" ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseGetCapabilitiesWfs()
                    throws Exception {
        HttpServletRequest executeRequest = mockGetCapabilitiesWfsRequest();
        parser.parse( executeRequest );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseGetCapabilities()
                    throws Exception {
        HttpServletRequest executeRequest = mockGetCapabilitiesRequest();
        parser.parse( executeRequest );
    }

    private HttpServletRequest mockGetCapabilitiesRequest()
                    throws IOException {
        String requestResource = "GetCapabilities.xml";
        return mockPostRequest( requestResource );
    }

    private HttpServletRequest mockExecuteRequest()
                    throws IOException {
        String requestResource = "Execute.xml";
        return mockPostRequest( requestResource );
    }

    private HttpServletRequest mockGetCapabilitiesWfsRequest()
                    throws IOException {
        String requestResource = "GetCapabilities-WFS.xml";
        return mockPostRequest( requestResource );
    }

    private HttpServletRequest mockPostRequest( String requestResource )
                    throws IOException {
        HttpServletRequest servletRequest = mock( HttpServletRequest.class );
        when( servletRequest.getServletPath() ).thenReturn( "serviceName" );
        final InputStream requestStream = WpsGetRequestParserTest.class.getResourceAsStream( requestResource );
        ServletInputStream servletInputStream = new ServletInputStream() {
            @Override
            public int read()
                            throws IOException {
                return requestStream.read();
            }
        };
        when( servletRequest.getInputStream() ).thenReturn( servletInputStream );
        return servletRequest;
    }

}
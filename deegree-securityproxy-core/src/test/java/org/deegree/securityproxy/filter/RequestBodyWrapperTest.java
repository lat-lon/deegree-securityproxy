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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class RequestBodyWrapperTest {

    private final String TEST_CONTENT = "ABCDEF";

    @Test
    public void testGetInputStreamReadMultipleTimes()
                    throws Exception {
        RequestBodyWrapper requestBodyWrapper = new RequestBodyWrapper( mockRequest() );

        ServletInputStream inputStreamFirst = requestBodyWrapper.getInputStream();
        String contentFirst = asString( inputStreamFirst );
        assertThat( contentFirst.trim(), is( TEST_CONTENT ) );

        ServletInputStream inputStreamSecond = requestBodyWrapper.getInputStream();
        String contentSecond = asString( inputStreamSecond );
        assertThat( contentSecond.trim(), is( TEST_CONTENT ) );
    }

    @Test
    public void testGetReaderReadMultipleTimes()
                    throws Exception {
        RequestBodyWrapper requestBodyWrapper = new RequestBodyWrapper( mockRequest() );

        BufferedReader readerFirst = requestBodyWrapper.getReader();
        String contentFirst = asString( readerFirst );
        assertThat( contentFirst.trim(), is( TEST_CONTENT ) );

        BufferedReader readerSecond = requestBodyWrapper.getReader();
        String contentSecond = asString( readerSecond );
        assertThat( contentSecond.trim(), is( TEST_CONTENT ) );
    }

    private HttpServletRequest mockRequest()
                    throws IOException {
        HttpServletRequest mockRequest = mock( HttpServletRequest.class );
        when( mockRequest.getInputStream() ).thenReturn( createServletStream() );
        return mockRequest;
    }

    private ServletInputStream createServletStream() {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream( TEST_CONTENT.getBytes() );
        return new ServletInputStream() {

            @Override
            public int read()
                            throws IOException {
                return byteArrayInputStream.read();
            }
        };
    }

    private String asString( BufferedReader reader )
                    throws IOException {
        char[] buffer = new char[10];
        IOUtils.read( reader, buffer );
        return new String( buffer );
    }

    private String asString( ServletInputStream inputStream )
                    throws IOException {
        byte[] buffer = new byte[10];
        IOUtils.read( inputStream, buffer );
        return new String( buffer );
    }
}
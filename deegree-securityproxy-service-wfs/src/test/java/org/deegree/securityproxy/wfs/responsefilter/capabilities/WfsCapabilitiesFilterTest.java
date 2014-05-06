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
package org.deegree.securityproxy.wfs.responsefilter.capabilities;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.xmlmatchers.XmlMatchers.isEquivalentTo;
import static org.xmlmatchers.transform.XmlConverters.the;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletOutputStream;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.deegree.securityproxy.filter.StatusCodeResponseBodyWrapper;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.XmlFilter;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.XmlModificationManager;
import org.junit.Test;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class WfsCapabilitiesFilterTest {

    @Test
    public void testFilter()
                            throws Exception {
        XmlFilter xmlFilter = new XmlFilter();
        ByteArrayOutputStream filteredStream = new ByteArrayOutputStream();
        XmlModificationManager xmlModifier = createModificationManager();
        xmlFilter.filterXml( mockResponse( filteredStream ), xmlModifier );

        assertThat( asXml( filteredStream ), isEquivalentTo( expectedXml() ) );
    }

    private XmlModificationManager createModificationManager() {
        WfsCapabilitiesModificationManagerCreator creator = new WfsCapabilitiesModificationManagerCreator(
                                                                                                           "newGetDcpUrl?",
                                                                                                           "newPostDcpUrl" );
        return creator.createXmlModificationManager( null, null );
    }

    private StatusCodeResponseBodyWrapper mockResponse( ByteArrayOutputStream filteredStream )
                            throws IOException {
        StatusCodeResponseBodyWrapper mockedServletResponse = mock( StatusCodeResponseBodyWrapper.class );
        InputStream resourceToFilter = WfsCapabilitiesFilterTest.class.getResourceAsStream( "wfs_110_capabilities.xml" );
        when( mockedServletResponse.getBufferedStream() ).thenReturn( resourceToFilter );
        when( mockedServletResponse.getRealOutputStream() ).thenReturn( createStream( filteredStream ) );
        return mockedServletResponse;
    }

    private ServletOutputStream createStream( final ByteArrayOutputStream bufferingStream ) {
        ServletOutputStream stream = new ServletOutputStream() {
            @Override
            public void write( int b )
                                    throws IOException {
                bufferingStream.write( b );
            }
        };
        return stream;
    }

    public static Source expectedXml() {
        InputStream filteredResources = WfsCapabilitiesFilterTest.class.getResourceAsStream( "wfs_110_capabilities-filtered.xml" );
        return new StreamSource( filteredResources );
    }

    public static Source asXml( ByteArrayOutputStream bufferingStream ) {
        return the( new StreamSource( new ByteArrayInputStream( bufferingStream.toByteArray() ) ) );
    }

}
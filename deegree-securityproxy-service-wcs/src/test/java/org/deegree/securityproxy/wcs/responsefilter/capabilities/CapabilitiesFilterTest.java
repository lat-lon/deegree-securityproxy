package org.deegree.securityproxy.wcs.responsefilter.capabilities;

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
import org.junit.Test;
import org.springframework.security.core.Authentication;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class CapabilitiesFilterTest {

    @Test
    public void testFilterCapabilitiesWithoutFilter()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockResponse( "simpleResponse.xml", filteredCapabilities );

        CapabilitiesFilter capabilitiesFilter = createCapabilitiesFilter();
        capabilitiesFilter.filterCapabilities( response, mockAuth() );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "simpleResponse.xml" ) ) );
    }

    @Test
    public void testFilterCapabilitiesSimpleFilteredE()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockResponse( "simpleResponse.xml", filteredCapabilities );

        CapabilitiesFilter capabilitiesFilter = createCapabilitiesFilter( "e" );
        capabilitiesFilter.filterCapabilities( response, mockAuth() );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "simpleFilteredE.xml" ) ) );
    }

    @Test
    public void testFilterCapabilitiesSimpleFilteredF()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockResponse( "simpleResponse.xml", filteredCapabilities );

        CapabilitiesFilter capabilitiesFilter = createCapabilitiesFilter( "f" );
        capabilitiesFilter.filterCapabilities( response, mockAuth() );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "simpleFilteredF.xml" ) ) );
    }

    private CapabilitiesFilter createCapabilitiesFilter() {
        return new CapabilitiesFilter( null );
    }

    private CapabilitiesFilter createCapabilitiesFilter( String nameToFilter ) {
        ElementDecisionRule eventFilter = new ElementDecisionRule( nameToFilter, null );
        return new CapabilitiesFilter( eventFilter );
    }

    private StatusCodeResponseBodyWrapper mockResponse( String originalXmlFileName, ByteArrayOutputStream filteredStream )
                            throws IOException {
        StatusCodeResponseBodyWrapper mockedServletResponse = mock( StatusCodeResponseBodyWrapper.class );
        InputStream resourceToFilter = CapabilitiesFilterTest.class.getResourceAsStream( originalXmlFileName );
        when( mockedServletResponse.getBufferedStream() ).thenReturn( resourceToFilter );
        when( mockedServletResponse.getRealOutputStream() ).thenReturn( createStream( filteredStream ) );
        return mockedServletResponse;
    }

    private Source expectedXml( String expectedFile ) {
        return new StreamSource( CapabilitiesFilterTest.class.getResourceAsStream( expectedFile ) );
    }

    private Source asXml( ByteArrayOutputStream bufferingStream ) {
        return the( new StreamSource( new ByteArrayInputStream( bufferingStream.toByteArray() ) ) );
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

    private Authentication mockAuth() {
        return mock( Authentication.class );
    }

}
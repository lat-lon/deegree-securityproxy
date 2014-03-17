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

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class CapabilitiesFilterTest {

    private final CapabilitiesFilter capabilitiesFilter = new CapabilitiesFilter();

    @Test
    public void testFilterCapabilitiesWithoutFilter()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockResponse( "simpleResponse.xml", filteredCapabilities );

        capabilitiesFilter.filterCapabilities( response, null );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "simpleResponse.xml" ) ) );
    }

    @Test
    public void testFilterCapabilitiesSimpleFiltered()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockResponse( "simpleResponse.xml", filteredCapabilities );

        capabilitiesFilter.filterCapabilities( response, createEventFilter( "f" ) );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "simpleFiltered.xml" ) ) );
    }

    @Test
    public void testFilterCapabilitiesSimpleFilteredWithNamespace()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockResponse( "simpleResponse.xml", filteredCapabilities );

        capabilitiesFilter.filterCapabilities( response, createEventFilter( "e", "http://simple.de" ) );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "simpleFilteredByNamespace.xml" ) ) );
    }

    @Test
    public void testFilterCapabilitiesSimpleFilteredWithNamespaceAndText()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockResponse( "simpleResponse.xml", filteredCapabilities );

        capabilitiesFilter.filterCapabilities( response, createEventFilter( "d", "http://simple1.de", "dtext" ) );

        assertThat( asXml( filteredCapabilities ),
                    isEquivalentTo( expectedXml( "simpleFilteredByNamespaceAndText.xml" ) ) );
    }

    @Test
    public void testFilterCapabilitiesSimpleFilteredWithNamespaceAndTextAndttribute()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockResponse( "simpleResponse.xml", filteredCapabilities );

        capabilitiesFilter.filterCapabilities( response, createEventFilter( "d", "http://simple1.de", "2nddtext" ) );

        assertThat( asXml( filteredCapabilities ),
                    isEquivalentTo( expectedXml( "simpleFilteredByNamespaceAndTextWithAttribute.xml" ) ) );
    }

    @Test
    public void testFilterCapabilitiesExtendedFiltered()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockResponse( "extendedResponse.xml", filteredCapabilities );

        ElementRule subRule = new ElementRule( "i", "http://extended.de", "idH" );
        capabilitiesFilter.filterCapabilities( response, createEventFilter( "f", "http://extended.de", subRule ) );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "extendedResponse.xml" ) ) );
    }

    @Test
    public void testFilterCapabilitiesExtendedFilteredFromSubelement()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockResponse( "extendedResponse.xml", filteredCapabilities );

        ElementRule subRule = new ElementRule( "g", "http://extended.de", "idG" );
        capabilitiesFilter.filterCapabilities( response, createEventFilter( "f", "http://extended.de", subRule ) );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "extendedFilteredBySubelement.xml" ) ) );
    }

    @Test
    public void testFilterCapabilitiesExtendedFilteredByNestedSubelement()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockResponse( "extendedResponse.xml", filteredCapabilities );

        ElementRule subRule = new ElementRule( "l", "http://extended.de", "idL" );
        capabilitiesFilter.filterCapabilities( response, createEventFilter( "f", "http://extended.de", subRule ) );

        assertThat( asXml( filteredCapabilities ),
                    isEquivalentTo( expectedXml( "extendedFilteredByNestedSubelement.xml" ) ) );
    }

    private ElementDecisionMaker createEventFilter( String nameToFilter ) {
        return createEventFilter( nameToFilter, null );
    }

    private ElementDecisionMaker createEventFilter( String nameToFilter, String namespace ) {
        return createEventFilter( nameToFilter, namespace, (String) null );
    }

    private ElementDecisionMaker createEventFilter( String nameToFilter, String namespace, String text ) {
        ElementRule rule = new ElementRule( nameToFilter, namespace, text );
        return new ElementDecisionMaker( rule );
    }

    private ElementDecisionMaker createEventFilter( String nameToFilter, String namespace, ElementRule subRule ) {
        ElementRule rule = new ElementRule( nameToFilter, namespace, subRule );
        return new ElementDecisionMaker( rule );
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
        System.out.println( bufferingStream.toString() );
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

}
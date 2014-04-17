package org.deegree.securityproxy.service.commons.responsefilter.capabilities;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.xmlmatchers.XmlMatchers.isEquivalentTo;
import static org.xmlmatchers.transform.XmlConverters.the;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.deegree.securityproxy.filter.StatusCodeResponseBodyWrapper;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.element.ElementDecisionMaker;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.element.ElementPathStep;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.element.ElementRule;
import org.junit.Test;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class CapabilitiesFilterTest {

    private static final String EXTENDED_NS_URI = "http://extended.de";

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

        capabilitiesFilter.filterCapabilities( response, createDecisionMaker( "f" ) );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "simpleFiltered.xml" ) ) );
    }

    @Test
    public void testFilterCapabilitiesSimpleFilteredWithNamespace()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockResponse( "simpleResponse.xml", filteredCapabilities );

        capabilitiesFilter.filterCapabilities( response, createDecisionMaker( "e", "http://simple.de" ) );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "simpleFilteredByNamespace.xml" ) ) );
    }

    @Test
    public void testFilterCapabilitiesSimpleFilteredWithNamespaceAndText()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockResponse( "simpleResponse.xml", filteredCapabilities );

        capabilitiesFilter.filterCapabilities( response, createDecisionMaker( "d", "http://simple1.de", "dtext" ) );

        assertThat( asXml( filteredCapabilities ),
                    isEquivalentTo( expectedXml( "simpleFilteredByNamespaceAndText.xml" ) ) );
    }

    @Test
    public void testFilterCapabilitiesSimpleFilteredWithNamespaceAndTextAndttribute()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockResponse( "simpleResponse.xml", filteredCapabilities );

        capabilitiesFilter.filterCapabilities( response, createDecisionMaker( "d", "http://simple1.de", "2nddtext" ) );

        assertThat( asXml( filteredCapabilities ),
                    isEquivalentTo( expectedXml( "simpleFilteredByNamespaceAndTextWithAttribute.xml" ) ) );
    }

    @Test
    public void testFilterCapabilitiesExtendedFiltered()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockResponse( "extendedResponse.xml", filteredCapabilities );

        ElementRule subRule = new ElementRule( "i", EXTENDED_NS_URI, "idH" );
        capabilitiesFilter.filterCapabilities( response, createDecisionMaker( "f", EXTENDED_NS_URI, subRule ) );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "extendedResponse.xml" ) ) );
    }

    @Test
    public void testFilterCapabilitiesExtendedFilteredFromSubelement()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockResponse( "extendedResponse.xml", filteredCapabilities );

        ElementRule subRule = new ElementRule( "g", EXTENDED_NS_URI, "idG" );
        capabilitiesFilter.filterCapabilities( response, createDecisionMaker( "f", EXTENDED_NS_URI, subRule ) );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "extendedFilteredBySubelement.xml" ) ) );
    }

    @Test
    public void testFilterCapabilitiesExtendedFilteredByNestedSubelement()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockResponse( "extendedResponse.xml", filteredCapabilities );

        ElementRule subRule = new ElementRule( "l", EXTENDED_NS_URI, "idL" );
        capabilitiesFilter.filterCapabilities( response, createDecisionMaker( "f", EXTENDED_NS_URI, subRule ) );

        assertThat( asXml( filteredCapabilities ),
                    isEquivalentTo( expectedXml( "extendedFilteredByNestedSubelement.xml" ) ) );
    }

    @Test
    public void testFilterCapabilitiesExtendedFilteredByPath()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockResponse( "extendedResponse.xml", filteredCapabilities );

        List<ElementPathStep> path = createPath();
        capabilitiesFilter.filterCapabilities( response, createDecisionMaker( "k", EXTENDED_NS_URI, "idK2", path ) );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "extendedResponseByPath.xml" ) ) );
    }

    @Test
    public void testFilterCapabilitiesExtendedFilteredByPathWithAttribute()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockResponse( "extendedResponse.xml", filteredCapabilities );

        List<ElementPathStep> path = createPathWithAttribute();
        capabilitiesFilter.filterCapabilities( response, createDecisionMaker( "k", EXTENDED_NS_URI, "idK1", path ) );

        assertThat( asXml( filteredCapabilities ),
                    isEquivalentTo( expectedXml( "extendedResponseByPathWithAttributes.xml" ) ) );
    }

    @Test
    public void testFilterCapabilitiesExtendedFilteredByTwoRules()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockResponse( "extendedResponse.xml", filteredCapabilities );

        ElementRule elementRule1 = new ElementRule( "d", EXTENDED_NS_URI, "dtext" );
        ElementRule elementRule2 = new ElementRule( "k", EXTENDED_NS_URI, "idK2" );
        DecisionMaker decisionMaker = createDecisionMaker( elementRule1, elementRule2 );
        capabilitiesFilter.filterCapabilities( response, decisionMaker );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "extendedResponseByTwoRules.xml" ) ) );
    }

    private List<ElementPathStep> createPath() {
        List<ElementPathStep> path = new ArrayList<ElementPathStep>();
        path.add( new ElementPathStep( new QName( EXTENDED_NS_URI, "A" ) ) );
        path.add( new ElementPathStep( new QName( EXTENDED_NS_URI, "B" ) ) );
        path.add( new ElementPathStep( new QName( EXTENDED_NS_URI, "e" ) ) );
        path.add( new ElementPathStep( new QName( EXTENDED_NS_URI, "f" ) ) );
        path.add( new ElementPathStep( new QName( EXTENDED_NS_URI, "f" ) ) );
        path.add( new ElementPathStep( new QName( EXTENDED_NS_URI, "j" ) ) );
        return path;
    }

    private List<ElementPathStep> createPathWithAttribute() {
        List<ElementPathStep> path = new ArrayList<ElementPathStep>();
        path.add( new ElementPathStep( new QName( EXTENDED_NS_URI, "A" ) ) );
        path.add( new ElementPathStep( new QName( EXTENDED_NS_URI, "B" ) ) );
        path.add( new ElementPathStep( new QName( EXTENDED_NS_URI, "e" ) ) );
        path.add( new ElementPathStep( new QName( EXTENDED_NS_URI, "f" ) ) );
        path.add( new ElementPathStep( new QName( EXTENDED_NS_URI, "f" ), new QName( "att" ), "zwei" ) );
        path.add( new ElementPathStep( new QName( EXTENDED_NS_URI, "j" ) ) );
        return path;
    }

    private DecisionMaker createDecisionMaker( String nameToFilter ) {
        return createDecisionMaker( nameToFilter, null );
    }

    private DecisionMaker createDecisionMaker( String nameToFilter, String namespace ) {
        return createDecisionMaker( nameToFilter, namespace, (String) null );
    }

    private DecisionMaker createDecisionMaker( String nameToFilter, String namespace, String text ) {
        ElementRule rule = new ElementRule( nameToFilter, namespace, text );
        return new ElementDecisionMaker( rule );
    }

    private DecisionMaker createDecisionMaker( String nameToFilter, String namespace, ElementRule subRule ) {
        ElementRule rule = new ElementRule( nameToFilter, namespace, subRule );
        return new ElementDecisionMaker( rule );
    }

    private DecisionMaker createDecisionMaker( String nameToFilter, String namespace, String text,
                                                      List<ElementPathStep> path ) {
        ElementRule rule = new ElementRule( nameToFilter, namespace, text, path );
        return new ElementDecisionMaker( rule );
    }

    private DecisionMaker createDecisionMaker( ElementRule... elementRules ) {
        List<ElementRule> rules = asList( elementRules );
        return new ElementDecisionMaker( rules );
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
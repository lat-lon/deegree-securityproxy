package org.deegree.securityproxy.service.commons.responsefilter.capabilities;

import static java.util.Arrays.asList;
import static org.deegree.securityproxy.service.commons.responsefilter.capabilities.XmlTestUtils.asXml;
import static org.deegree.securityproxy.service.commons.responsefilter.capabilities.XmlTestUtils.expectedXml;
import static org.deegree.securityproxy.service.commons.responsefilter.capabilities.XmlTestUtils.mockResponse;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.xmlmatchers.XmlMatchers.hasXPath;
import static org.xmlmatchers.XmlMatchers.isEquivalentTo;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;

import org.deegree.securityproxy.filter.StatusCodeResponseBodyWrapper;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.element.ElementDecisionMaker;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.element.ElementPathStep;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.element.ElementRule;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.text.AttributeModifier;
import org.junit.Test;
import org.springframework.util.xml.SimpleNamespaceContext;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class XmlFilterTest {

    private static final String EXTENDED_NS_URI = "http://extended.de";

    private final XmlFilter capabilitiesFilter = new XmlFilter();

    @Test
    public void testFilterXmlWithoutFilter()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockResponse( "simpleResponse.xml", filteredCapabilities );

        capabilitiesFilter.filterXml( response, null );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "simpleResponse.xml" ) ) );
    }

    @Test
    public void testFilterXmlSimpleFiltered()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockResponse( "simpleResponse.xml", filteredCapabilities );

        capabilitiesFilter.filterXml( response, createDecisionMaker( "f" ) );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "simpleFiltered.xml" ) ) );
    }

    @Test
    public void testFilterXmlSimpleFilteredWithNamespace()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockResponse( "simpleResponse.xml", filteredCapabilities );

        capabilitiesFilter.filterXml( response, createDecisionMaker( "e", "http://simple.de" ) );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "simpleFilteredByNamespace.xml" ) ) );
    }

    @Test
    public void testFilterXmlSimpleFilteredWithNamespaceAndText()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockResponse( "simpleResponse.xml", filteredCapabilities );

        capabilitiesFilter.filterXml( response, createDecisionMaker( "d", "http://simple1.de", "dtext" ) );

        assertThat( asXml( filteredCapabilities ),
                    isEquivalentTo( expectedXml( "simpleFilteredByNamespaceAndText.xml" ) ) );
    }

    @Test
    public void testFilterXmlSimpleFilteredWithNamespaceAndTextAndttribute()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockResponse( "simpleResponse.xml", filteredCapabilities );

        capabilitiesFilter.filterXml( response, createDecisionMaker( "d", "http://simple1.de", "2nddtext" ) );

        assertThat( asXml( filteredCapabilities ),
                    isEquivalentTo( expectedXml( "simpleFilteredByNamespaceAndTextWithAttribute.xml" ) ) );
    }

    @Test
    public void testFilterXmlExtendedFiltered()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockResponse( "extendedResponse.xml", filteredCapabilities );

        ElementRule subRule = new ElementRule( "i", EXTENDED_NS_URI, "idH" );
        capabilitiesFilter.filterXml( response, createDecisionMaker( "f", EXTENDED_NS_URI, subRule ) );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "extendedResponse.xml" ) ) );
    }

    @Test
    public void testFilterXmlExtendedFilteredFromSubelement()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockResponse( "extendedResponse.xml", filteredCapabilities );

        ElementRule subRule = new ElementRule( "g", EXTENDED_NS_URI, "idG" );
        capabilitiesFilter.filterXml( response, createDecisionMaker( "f", EXTENDED_NS_URI, subRule ) );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "extendedFilteredBySubelement.xml" ) ) );
    }

    @Test
    public void testFilterXmlExtendedFilteredByNestedSubelement()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockResponse( "extendedResponse.xml", filteredCapabilities );

        ElementRule subRule = new ElementRule( "l", EXTENDED_NS_URI, "idL" );
        capabilitiesFilter.filterXml( response, createDecisionMaker( "f", EXTENDED_NS_URI, subRule ) );

        assertThat( asXml( filteredCapabilities ),
                    isEquivalentTo( expectedXml( "extendedFilteredByNestedSubelement.xml" ) ) );
    }

    @Test
    public void testFilterXmlExtendedFilteredByPath()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockResponse( "extendedResponse.xml", filteredCapabilities );

        List<ElementPathStep> path = createPath();
        capabilitiesFilter.filterXml( response, createDecisionMaker( "k", EXTENDED_NS_URI, "idK2", path ) );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "extendedResponseByPath.xml" ) ) );
    }

    @Test
    public void testFilterXmlExtendedFilteredByPathWithAttribute()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockResponse( "extendedResponse.xml", filteredCapabilities );

        List<ElementPathStep> path = createPathWithAttribute();
        capabilitiesFilter.filterXml( response, createDecisionMaker( "k", EXTENDED_NS_URI, "idK1", path ) );

        assertThat( asXml( filteredCapabilities ),
                    isEquivalentTo( expectedXml( "extendedResponseByPathWithAttributes.xml" ) ) );
    }

    @Test
    public void testFilterXmlExtendedFilteredByTwoRules()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockResponse( "extendedResponse.xml", filteredCapabilities );

        ElementRule elementRule1 = new ElementRule( "d", EXTENDED_NS_URI, "dtext" );
        ElementRule elementRule2 = new ElementRule( "k", EXTENDED_NS_URI, "idK2" );
        XmlModificationManager decisionMaker = createDecisionMaker( elementRule1, elementRule2 );
        capabilitiesFilter.filterXml( response, decisionMaker );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "extendedResponseByTwoRules.xml" ) ) );
    }

    @Test
    public void testFilterXmlWithModificationNeededShouldModifyAttribute()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockResponse( "simpleResponse.xml", filteredCapabilities );

        String newAttributeValue = "newAttValue";
        XmlModificationManager xmlModifier = mockXmlModifier( newAttributeValue );
        capabilitiesFilter.filterXml( response, xmlModifier );

        SimpleNamespaceContext nsContext = new SimpleNamespaceContext();
        nsContext.bindNamespaceUri( "sp1", "http://simple1.de" );
        assertThat( asXml( filteredCapabilities ), hasXPath( "/A/B/sp1:d/@datt", is( newAttributeValue ), nsContext ) );
    }

    @Test
    public void testFilterXmlWithModificationNotNeededShouldNotModifyAttribute()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockResponse( "simpleResponse.xml", filteredCapabilities );

        XmlModificationManager xmlModifier = mockXmlModifier( null );
        capabilitiesFilter.filterXml( response, xmlModifier );

        SimpleNamespaceContext nsContext = new SimpleNamespaceContext();
        nsContext.bindNamespaceUri( "sp1", "http://simple1.de" );
        assertThat( asXml( filteredCapabilities ), hasXPath( "/A/B/sp1:d/@datt", is( "d_att_ext" ), nsContext ) );
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

    @SuppressWarnings("unchecked")
    private XmlModificationManager mockXmlModifier( String newAttributeValue )
                            throws XMLStreamException {
        AttributeModifier attributeModifier = mock( AttributeModifier.class );
        when(
              attributeModifier.determineNewAttributeValue( any( BufferingXMLEventReader.class ),
                                                            any( StartElement.class ), any( Attribute.class ),
                                                            any( ( LinkedList.class ) ) ) ).thenReturn( newAttributeValue );
        return new XmlModificationManager( attributeModifier );
    }

    private XmlModificationManager createDecisionMaker( String nameToFilter ) {
        return createDecisionMaker( nameToFilter, null );
    }

    private XmlModificationManager createDecisionMaker( String nameToFilter, String namespace ) {
        return createDecisionMaker( nameToFilter, namespace, (String) null );
    }

    private XmlModificationManager createDecisionMaker( String nameToFilter, String namespace, String text ) {
        ElementRule rule = new ElementRule( nameToFilter, namespace, text );
        ElementDecisionMaker decisionMaker = new ElementDecisionMaker( rule );
        return new XmlModificationManager( decisionMaker );
    }

    private XmlModificationManager createDecisionMaker( String nameToFilter, String namespace, ElementRule subRule ) {
        ElementRule rule = new ElementRule( nameToFilter, namespace, subRule );
        ElementDecisionMaker decisionMaker = new ElementDecisionMaker( rule );
        return new XmlModificationManager( decisionMaker );
    }

    private XmlModificationManager createDecisionMaker( String nameToFilter, String namespace, String text,
                                             List<ElementPathStep> path ) {
        ElementRule rule = new ElementRule( nameToFilter, namespace, text, path );
        ElementDecisionMaker decisionMaker = new ElementDecisionMaker( rule );
        return new XmlModificationManager( decisionMaker );
    }

    private XmlModificationManager createDecisionMaker( ElementRule... elementRules ) {
        List<ElementRule> rules = asList( elementRules );
        ElementDecisionMaker decisionMaker = new ElementDecisionMaker( rules );
        return new XmlModificationManager( decisionMaker );
    }

}
package org.deegree.securityproxy.service.commons.responsefilter.capabilities;

import static java.util.Arrays.asList;
import static org.deegree.securityproxy.service.commons.responsefilter.capabilities.XmlTestUtils.asXml;
import static org.deegree.securityproxy.service.commons.responsefilter.capabilities.XmlTestUtils.expectedXml;
import static org.deegree.securityproxy.service.commons.responsefilter.capabilities.XmlTestUtils.mockResponse;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
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
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.text.XmlModifier;
import org.junit.Test;
import org.springframework.util.xml.SimpleNamespaceContext;

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

    @SuppressWarnings("unchecked")
    @Test
    public void testFilterCapabilitiesShouldInvokeXmlModifier()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockResponse( "simpleResponse.xml", filteredCapabilities );

        DecisionMaker decisionMaker = mockDecisionMaker();
        XmlModifier xmlModifier = mockXmlModifier( null );
        capabilitiesFilter.filterCapabilities( response, decisionMaker, xmlModifier );

        verify( xmlModifier ).determineNewAttributeValue( any( BufferingXMLEventReader.class ),
                                                          any( StartElement.class ), any( Attribute.class ),
                                                          any( ( LinkedList.class ) ) );
    }

    @Test
    public void testFilterCapabilitiesWithModificationNeededShouldModifyAttribute()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockResponse( "simpleResponse.xml", filteredCapabilities );

        DecisionMaker decisionMaker = mockDecisionMaker();
        String newAttributeValue = "newAttValue";
        XmlModifier xmlModifier = mockXmlModifier( newAttributeValue );
        capabilitiesFilter.filterCapabilities( response, decisionMaker, xmlModifier );

        SimpleNamespaceContext nsContext = new SimpleNamespaceContext();
        nsContext.bindNamespaceUri( "sp1", "http://simple1.de" );
        assertThat( asXml( filteredCapabilities ), hasXPath( "/A/B/sp1:d/@datt", is( newAttributeValue ), nsContext ) );
    }

    @Test
    public void testFilterCapabilitiesWithModificationNotNeededShouldNotModifyAttribute()
                            throws Exception {
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockResponse( "simpleResponse.xml", filteredCapabilities );

        DecisionMaker decisionMaker = mockDecisionMaker();
        XmlModifier xmlModifier = mockXmlModifier( null );
        capabilitiesFilter.filterCapabilities( response, decisionMaker, xmlModifier );

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

    private DecisionMaker mockDecisionMaker() {
        return mock( DecisionMaker.class );
    }

    @SuppressWarnings("unchecked")
    private XmlModifier mockXmlModifier( String newAttributeValue )
                            throws XMLStreamException {
        XmlModifier xmlModifier = mock( XmlModifier.class );
        when(
              xmlModifier.determineNewAttributeValue( any( BufferingXMLEventReader.class ), any( StartElement.class ),
                                                      any( Attribute.class ), any( ( LinkedList.class ) ) ) ).thenReturn( newAttributeValue );
        return xmlModifier;
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

}
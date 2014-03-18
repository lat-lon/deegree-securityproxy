package org.deegree.securityproxy.wcs.responsefilter.capabilities.element;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.deegree.securityproxy.wcs.responsefilter.capabilities.BufferingXMLEventReader;
import org.deegree.securityproxy.wcs.responsefilter.capabilities.DecisionMaker;
import org.junit.Test;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class ElementDecisionMakerTest {

    private static final String NAMESPACE_URI = "http://namespace.uri.de";

    private static final String NAME_TO_FILTER = "nameToFilter";

    private final ElementRule nameAndNamespaceRule = new ElementRule( NAME_TO_FILTER, NAMESPACE_URI );

    private final ElementRule nameRule = new ElementRule( NAME_TO_FILTER );

    private final DecisionMaker elementDecisionRule = new ElementDecisionMaker( nameAndNamespaceRule );

    private final DecisionMaker elementDecisionRuleUnsetNamespace = new ElementDecisionMaker( nameRule );

    private final DecisionMaker elementDecisionRuleTwoRules = new ElementDecisionMaker( asList( nameRule,
                                                                                                nameAndNamespaceRule ) );

    @Test(expected = IllegalArgumentException.class)
    public void testElementDecisionMakerWithNullRule()
                            throws Exception {
        new ElementDecisionMaker( (ElementRule) null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testElementDecisionMakerWithNullRules()
                            throws Exception {
        new ElementDecisionMaker( (List<ElementRule>) null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testElementDecisionMakerWithEmptyRules()
                            throws Exception {
        new ElementDecisionMaker( Collections.<ElementRule> emptyList() );
    }

    @Test
    public void testIgnoreWithStartElementAndFilteredNameShouldReturnTrue()
                            throws Exception {
        boolean ignore = elementDecisionRule.ignore( mockXmlEventReader(), mockEventToIgnore(), mockVisitedElements() );

        assertThat( ignore, is( true ) );
    }

    @Test
    public void testIgnoreWithoutNamespaceReturnTrue()
                            throws Exception {
        boolean ignore = elementDecisionRuleUnsetNamespace.ignore( mockXmlEventReader(),
                                                                   mockEventIgnoredNameWithoutNamespaceUri(),
                                                                   mockVisitedElements() );

        assertThat( ignore, is( true ) );
    }

    @Test
    public void testIgnoreWithNotStartElementAndFilteredNameShouldReturnFalse()
                            throws Exception {
        boolean ignore = elementDecisionRule.ignore( mockXmlEventReader(), mockEventNotStart(), mockVisitedElements() );

        assertThat( ignore, is( false ) );
    }

    @Test
    public void testIgnoreWithStartElementAndOtherNameShouldReturnFalse()
                            throws Exception {
        boolean ignore = elementDecisionRule.ignore( mockXmlEventReader(), mockEventNotIgnoredName(),
                                                     mockVisitedElements() );

        assertThat( ignore, is( false ) );
    }

    @Test
    public void testIgnoreWithStartElementAndNameToFilterButOtherNamespaceShouldReturnFalse()
                            throws Exception {
        boolean ignore = elementDecisionRule.ignore( mockXmlEventReader(), mockEventIgnoredNameOtherNamespaceUri(),
                                                     mockVisitedElements() );

        assertThat( ignore, is( false ) );
    }

    @Test
    public void testIgnoreWithTwoRulesShouldReturnTrue()
                            throws Exception {
        boolean ignore = elementDecisionRuleTwoRules.ignore( mockXmlEventReader(),
                                                             mockEventIgnoredNameWithoutNamespaceUri(),
                                                             mockVisitedElements() );

        assertThat( ignore, is( true ) );
    }

    private BufferingXMLEventReader mockXmlEventReader() {
        return mock( BufferingXMLEventReader.class );
    }

    @SuppressWarnings("unchecked")
    private LinkedList<StartElement> mockVisitedElements() {
        return mock( LinkedList.class );
    }

    private XMLEvent mockEventToIgnore() {
        return mockEvent( true, NAME_TO_FILTER, NAMESPACE_URI );
    }

    private XMLEvent mockEventNotStart() {
        return mockEvent( false, NAME_TO_FILTER, NAMESPACE_URI );
    }

    private XMLEvent mockEventNotIgnoredName() {
        return mockEvent( true, "notIgnored", NAMESPACE_URI );
    }

    private XMLEvent mockEventIgnoredNameOtherNamespaceUri() {
        return mockEvent( true, NAME_TO_FILTER, "http://otherNamespace.de" );
    }

    private XMLEvent mockEventIgnoredNameWithoutNamespaceUri() {
        return mockEvent( true, NAME_TO_FILTER, null );
    }

    private XMLEvent mockEvent( boolean isStart, String elementName, String namespaceUri ) {
        XMLEvent event = mock( XMLEvent.class );
        when( event.isStartElement() ).thenReturn( isStart );
        StartElement startElement = mock( StartElement.class );
        QName elementQName = new QName( namespaceUri, elementName );
        when( startElement.getName() ).thenReturn( elementQName );
        when( event.asStartElement() ).thenReturn( startElement );
        return event;
    }

}
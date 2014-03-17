package org.deegree.securityproxy.wcs.responsefilter.capabilities;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.xml.namespace.QName;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

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

    private final ElementDecisionMaker elementDecisionRule = new ElementDecisionMaker( nameAndNamespaceRule );

    private final ElementDecisionMaker elementDecisionRuleUnsetNamespace = new ElementDecisionMaker( nameRule );

    @Test(expected = IllegalArgumentException.class)
    public void testElementDecisionMakerWithNullRule()
                            throws Exception {
        new ElementDecisionMaker( null );
    }

    @Test
    public void testIgnoreWithStartElementAndFilteredNameShouldReturnTrue()
                            throws Exception {
        boolean ignore = elementDecisionRule.ignore( mockXmlEventReader(), mockEventToIgnore() );

        assertThat( ignore, is( true ) );
    }

    @Test
    public void testIgnoreWithoutNamespaceReturnTrue()
                            throws Exception {
        boolean ignore = elementDecisionRuleUnsetNamespace.ignore( mockXmlEventReader(),
                                                                   mockEventIgnoredNameWithoutNamespaceUri() );

        assertThat( ignore, is( true ) );
    }

    @Test
    public void testIgnoreWithNotStartElementAndFilteredNameShouldReturnFalse()
                            throws Exception {
        boolean ignore = elementDecisionRule.ignore( mockXmlEventReader(), mockEventNotStart() );

        assertThat( ignore, is( false ) );
    }

    @Test
    public void testIgnoreWithStartElementAndOtherNameShouldReturnFalse()
                            throws Exception {
        boolean ignore = elementDecisionRule.ignore( mockXmlEventReader(), mockEventNotIgnoredName() );

        assertThat( ignore, is( false ) );
    }

    @Test
    public void testIgnoreWithStartElementAndNameToFilterButOtherNamespaceShouldReturnFalse()
                            throws Exception {
        boolean ignore = elementDecisionRule.ignore( mockXmlEventReader(), mockEventIgnoredNameOtherNamespaceUri() );

        assertThat( ignore, is( false ) );
    }

    private BufferingXMLEventReader mockXmlEventReader() {
        return mock( BufferingXMLEventReader.class );
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
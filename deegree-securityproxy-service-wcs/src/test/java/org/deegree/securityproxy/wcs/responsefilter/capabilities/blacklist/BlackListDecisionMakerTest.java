package org.deegree.securityproxy.wcs.responsefilter.capabilities.blacklist;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import javax.xml.namespace.QName;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.deegree.securityproxy.wcs.responsefilter.capabilities.BufferingXMLEventReader;
import org.junit.Test;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class BlackListDecisionMakerTest {

    private static final String ELEMENT_TO_FILTER_NAME = "nameToFilter";

    private static final String ELEMENT_TO_FILTER_NAMESPACE_URI = "http://elementtofilter.uri.de";

    private static final String SUB_ELEMENT_NAME = "subElement";

    private static final String SUB_ELEMENT_NAMESPACE_URI = "http://subelement.uri.de";

    private static final String TEXT_VALUE = "textValue";

    @Test(expected = IllegalArgumentException.class)
    public void testBlackListDecisionMakerWithNullElementNameShouldThrowException()
                            throws Exception {
        new BlackListDecisionMaker( null, "elementToSkipNamespace", "subElementName", "subElementNamespace",
                                    singletonList( "blackListTextValues" ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBlackListDecisionMakerWithEmptyElementNameShouldThrowException()
                            throws Exception {
        new BlackListDecisionMaker( "", "elementToSkipNamespace", "subElementName", "subElementNamespace",
                                    singletonList( "blackListTextValues" ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBlackListDecisionMakerWithNullSubElementNameShouldThrowException()
                            throws Exception {
        new BlackListDecisionMaker( "elementToSkipName", "elementToSkipNamespace", null, "subElementNamespace",
                                    singletonList( "blackListTextValues" ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBlackListDecisionMakerWithEmptySubElementNameShouldThrowException()
                            throws Exception {
        new BlackListDecisionMaker( "elementToSkipName", "elementToSkipNamespace", "", "subElementNamespace",
                                    singletonList( "blackListTextValues" ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBlackListDecisionMakerWithNullBlackListTextValuesShouldThrowException()
                            throws Exception {
        new BlackListDecisionMaker( "elementToSkipName", "elementToSkipNamespace", "subElementName",
                                    "subElementNamespace", null );
    }

    @Test
    public void testIgnoreWithEmptyBlackListShouldReturnTrue()
                            throws Exception {
        BlackListDecisionMaker blackListDecisionMaker = new BlackListDecisionMaker( ELEMENT_TO_FILTER_NAME,
                                                                                    ELEMENT_TO_FILTER_NAMESPACE_URI,
                                                                                    SUB_ELEMENT_NAME,
                                                                                    SUB_ELEMENT_NAMESPACE_URI,
                                                                                    Collections.<String> emptyList() );
        XMLEvent event = mockCurrentEvent();
        boolean ignore = blackListDecisionMaker.ignore( mockXmlEventReader(), event, mockVisitedElements() );

        assertThat( ignore, is( true ) );
    }

    @Test
    public void testIgnoreWithBlackListNotMatchingTextShouldReturnTrue()
                            throws Exception {
        BlackListDecisionMaker blackListDecisionMaker = new BlackListDecisionMaker( ELEMENT_TO_FILTER_NAME,
                                                                                    ELEMENT_TO_FILTER_NAMESPACE_URI,
                                                                                    SUB_ELEMENT_NAME,
                                                                                    SUB_ELEMENT_NAMESPACE_URI,
                                                                                    singletonList( "notNextText" ) );
        XMLEvent event = mockCurrentEvent();
        boolean ignore = blackListDecisionMaker.ignore( mockXmlEventReader(), event, mockVisitedElements() );

        assertThat( ignore, is( true ) );
    }

    @Test
    public void testIgnoreWithBlackListMatchingTextShouldReturnFalse()
                            throws Exception {
        BlackListDecisionMaker blackListDecisionMaker = new BlackListDecisionMaker( ELEMENT_TO_FILTER_NAME,
                                                                                    ELEMENT_TO_FILTER_NAMESPACE_URI,
                                                                                    SUB_ELEMENT_NAME,
                                                                                    SUB_ELEMENT_NAMESPACE_URI,
                                                                                    singletonList( TEXT_VALUE ) );
        XMLEvent event = mockCurrentEvent();
        boolean ignore = blackListDecisionMaker.ignore( mockXmlEventReader(), event, mockVisitedElements() );

        assertThat( ignore, is( false ) );
    }

    @Test
    public void testIgnoreWithOtherElementShouldReturnFalse()
                            throws Exception {
        BlackListDecisionMaker blackListDecisionMaker = new BlackListDecisionMaker( "e2", "http://text.de", "b1",
                                                                                    "http://text.de",
                                                                                    singletonList( TEXT_VALUE ) );
        XMLEvent event = mockStartEvent( "zzz2", "http://text.de", "test" );
        boolean ignore = blackListDecisionMaker.ignore( mockXmlEventReader(), event, mockVisitedElements() );

        assertThat( ignore, is( false ) );
    }

    @Test
    public void testIgnoreWithOtherSubElementShouldReturnFalse()
                            throws Exception {
        BlackListDecisionMaker blackListDecisionMaker = new BlackListDecisionMaker( "e2", "http://text.de", "b1",
                                                                                    "http://text.de",
                                                                                    singletonList( TEXT_VALUE ) );
        XMLEvent event = mockStartEvent( "e2", "http://text.de", "test" );
        boolean ignore = blackListDecisionMaker.ignore( mockXmlEventReader(), event, mockVisitedElements() );

        assertThat( ignore, is( false ) );
    }

    private XMLEvent mockCurrentEvent() {
        return mockStartEvent( ELEMENT_TO_FILTER_NAME, ELEMENT_TO_FILTER_NAMESPACE_URI, "test" );
    }

    @SuppressWarnings("unchecked")
    private BufferingXMLEventReader mockXmlEventReader() {
        BufferingXMLEventReader mock = mock( BufferingXMLEventReader.class );
        Iterator<XMLEvent> mockIteratorFirst = mockIterator();
        Iterator<XMLEvent> mockIteratorSecond = mockIterator();
        when( mock.retrievePeekIterator( any( XMLEvent.class ) ) ).thenReturn( mockIteratorFirst, mockIteratorSecond );
        return mock;
    }

    @SuppressWarnings("unchecked")
    private Iterator<XMLEvent> mockIterator() {
        Iterator<XMLEvent> iterator = mock( Iterator.class );
        XMLEvent xmlEvent1 = mockStartEvent( SUB_ELEMENT_NAME, SUB_ELEMENT_NAMESPACE_URI, TEXT_VALUE );
        XMLEvent xmlEvent2 = mockCharacterEvent( TEXT_VALUE );
        XMLEvent xmlEvent3 = mockEndEvent();
        XMLEvent xmlEvent4 = mockEndEvent();
        when( iterator.next() ).thenReturn( xmlEvent1, xmlEvent2, xmlEvent3, xmlEvent4 );
        when( iterator.hasNext() ).thenReturn( true, true, true, true, false );
        return iterator;
    }

    @SuppressWarnings("unchecked")
    private LinkedList<StartElement> mockVisitedElements() {
        return mock( LinkedList.class );
    }

    private XMLEvent mockStartEvent( String elementName, String namespaceUri, String elementValue ) {
        XMLEvent event = mock( XMLEvent.class );
        when( event.isStartElement() ).thenReturn( true );
        when( event.isEndElement() ).thenReturn( false );
        StartElement startElement = mock( StartElement.class );
        QName elementQName = new QName( namespaceUri, elementName );
        when( startElement.getName() ).thenReturn( elementQName );
        if ( elementValue != null ) {
            Characters chars = mock( Characters.class );
            when( chars.getData() ).thenReturn( elementValue );
            when( startElement.asCharacters() ).thenReturn( chars );
        }
        when( event.asStartElement() ).thenReturn( startElement );
        return event;
    }

    private XMLEvent mockCharacterEvent( String textValue ) {
        XMLEvent event = mock( XMLEvent.class );
        when( event.isCharacters() ).thenReturn( true );

        Characters chars = mock( Characters.class );
        when( chars.getData() ).thenReturn( textValue );
        when( event.asCharacters() ).thenReturn( chars );

        return event;
    }

    private XMLEvent mockEndEvent() {
        XMLEvent event = mock( XMLEvent.class );
        when( event.isStartElement() ).thenReturn( false );
        when( event.isEndElement() ).thenReturn( true );
        return event;
    }

}
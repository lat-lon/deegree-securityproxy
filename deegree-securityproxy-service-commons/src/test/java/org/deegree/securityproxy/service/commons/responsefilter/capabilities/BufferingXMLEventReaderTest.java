package org.deegree.securityproxy.service.commons.responsefilter.capabilities;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Iterator;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class BufferingXMLEventReaderTest {

    private BufferingXMLEventReader bufferingReader;

    @Before
    public void createReader()
                            throws FactoryConfigurationError, XMLStreamException {
        String xml = "<A><B><c>ctext</c></B><D><e>etext</e></D><A>";
        InputStream originalCapabilities = new ByteArrayInputStream( xml.getBytes() );
        XMLInputFactory inFactory = XMLInputFactory.newInstance();
        XMLEventReader reader = inFactory.createXMLEventReader( originalCapabilities );
        bufferingReader = new BufferingXMLEventReader( reader );
    }

    @After
    public void closeReader()
                            throws XMLStreamException {
        bufferingReader.close();
    }

    @Test
    public void testPeekNextEventIsStartDocument()
                            throws Exception {
        XMLEvent peekNextEvent = bufferingReader.peekNextEvent();
        assertThat( peekNextEvent.isStartDocument(), is( true ) );

        XMLEvent nextEvent = bufferingReader.nextEvent();
        assertThat( nextEvent.isStartDocument(), is( true ) );
    }

    @Test
    public void testPeekNextEventIsStartElement()
                            throws Exception {
        XMLEvent nextEvent = bufferingReader.nextEvent();
        nextEvent = bufferingReader.nextEvent();
        assertThat( nextEvent.isStartElement(), is( true ) );
        assertThat( nextEvent.asStartElement().getName().getLocalPart(), is( "A" ) );

        XMLEvent peekNextEvent = bufferingReader.peekNextEvent();
        assertThat( peekNextEvent.isStartElement(), is( true ) );
        assertThat( peekNextEvent.asStartElement().getName().getLocalPart(), is( "B" ) );

        nextEvent = bufferingReader.nextEvent();
        assertThat( nextEvent.isStartElement(), is( true ) );
        assertThat( nextEvent.asStartElement().getName().getLocalPart(), is( "B" ) );
    }

    @Test
    public void testPeekNextEventIsEndElement()
                            throws Exception {
        XMLEvent nextEvent = bufferingReader.nextEvent();
        nextEvent = bufferingReader.nextEvent();
        nextEvent = bufferingReader.nextEvent();
        nextEvent = bufferingReader.nextEvent();
        nextEvent = bufferingReader.nextEvent();

        XMLEvent peekNextEvent = bufferingReader.peekNextEvent();
        assertThat( peekNextEvent.isEndElement(), is( true ) );
        assertThat( peekNextEvent.asEndElement().getName().getLocalPart(), is( "c" ) );

        nextEvent = bufferingReader.nextEvent();
        assertThat( nextEvent.isEndElement(), is( true ) );
        assertThat( nextEvent.asEndElement().getName().getLocalPart(), is( "c" ) );
    }

    @Test
    public void testPeekNextEventIsText()
                            throws Exception {
        XMLEvent nextEvent = bufferingReader.nextEvent();
        nextEvent = bufferingReader.nextEvent();
        nextEvent = bufferingReader.nextEvent();
        nextEvent = bufferingReader.nextEvent();

        XMLEvent peekNextEvent = bufferingReader.peekNextEvent();
        assertThat( peekNextEvent.isCharacters(), is( true ) );
        assertThat( peekNextEvent.asCharacters().getData(), is( "ctext" ) );

        nextEvent = bufferingReader.nextEvent();
        assertThat( nextEvent.isCharacters(), is( true ) );
        assertThat( nextEvent.asCharacters().getData(), is( "ctext" ) );
    }

    @Test
    public void testPeekNextEventMultiplePeekIsText()
                            throws Exception {
        XMLEvent nextEvent = bufferingReader.nextEvent();
        nextEvent = bufferingReader.nextEvent();

        XMLEvent peekNextEvent = bufferingReader.peekNextEvent();
        peekNextEvent = bufferingReader.peekNextEvent();
        peekNextEvent = bufferingReader.peekNextEvent();
        assertThat( peekNextEvent.isCharacters(), is( true ) );
        assertThat( peekNextEvent.asCharacters().getData(), is( "ctext" ) );

        nextEvent = bufferingReader.nextEvent();
        nextEvent = bufferingReader.nextEvent();
        nextEvent = bufferingReader.nextEvent();
        assertThat( nextEvent.isCharacters(), is( true ) );
        assertThat( nextEvent.asCharacters().getData(), is( "ctext" ) );
    }

    @Test
    public void testRetrievePeekIteratorFromStartDocument()
                            throws Exception {
        Iterator<XMLEvent> peekIterator = bufferingReader.retrievePeekIterator( null );
        XMLEvent next = peekIterator.next();

        assertThat( next.isStartDocument(), is( true ) );
    }

    @Test
    public void testRetrievePeekIteratorShouldStartAtNext()
                            throws Exception {
        bufferingReader.nextEvent();
        bufferingReader.nextEvent();
        bufferingReader.nextEvent();

        Iterator<XMLEvent> peekIterator = bufferingReader.retrievePeekIterator( null );
        XMLEvent next = peekIterator.next();

        assertThat( next.isStartElement(), is( true ) );
        assertThat( next.asStartElement().getName().getLocalPart(), is( "c" ) );
    }

    @Test
    public void testRetrievePeekIteratorShouldStartAtPeeked()
                            throws Exception {
        bufferingReader.nextEvent();
        bufferingReader.nextEvent();
        bufferingReader.nextEvent();
        bufferingReader.peekNextEvent();
        bufferingReader.peekNextEvent();

        Iterator<XMLEvent> peekIterator = bufferingReader.retrievePeekIterator( null );
        XMLEvent next = peekIterator.next();

        assertThat( next.isStartElement(), is( true ) );
        assertThat( next.asStartElement().getName().getLocalPart(), is( "c" ) );
    }

    @Test
    public void testRetrievePeekIteratorShouldStartAtStartEvent()
                            throws Exception {
        bufferingReader.nextEvent();
        bufferingReader.nextEvent();
        bufferingReader.nextEvent();
        bufferingReader.peekNextEvent();
        XMLEvent startEvent = bufferingReader.peekNextEvent();
        bufferingReader.peekNextEvent();
        bufferingReader.peekNextEvent();

        Iterator<XMLEvent> peekIterator = bufferingReader.retrievePeekIterator( startEvent );
        XMLEvent next = peekIterator.next();

        assertThat( next.isCharacters(), is( true ) );
        assertThat( next.asCharacters().getData(), is( "ctext" ) );
    }

}
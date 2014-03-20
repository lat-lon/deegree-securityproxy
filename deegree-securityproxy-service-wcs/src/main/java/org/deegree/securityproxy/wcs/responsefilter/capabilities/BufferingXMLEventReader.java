//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2011 by:
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
package org.deegree.securityproxy.wcs.responsefilter.capabilities;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/**
 * Encapsulates a {@link XMLEventReader} and allows to look ahead the coming {@link XMLEvent}.
 * 
 * This class is not thread safe!
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class BufferingXMLEventReader implements XMLEventReader {

    private final XMLEventReader reader;

    private final LinkedList<XMLEvent> events = new LinkedList<XMLEvent>();

    /**
     * @param reader
     *            wrapped reader to buffer, never <code>null</code>
     */
    public BufferingXMLEventReader( XMLEventReader reader ) {
        this.reader = reader;
    }

    @Override
    public Object next() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public XMLEvent nextEvent()
                            throws XMLStreamException {
        if ( !events.isEmpty() )
            return events.pollFirst();
        return reader.nextEvent();
    }

    @Override
    public boolean hasNext() {
        return !events.isEmpty() || reader.hasNext();
    }

    @Override
    public XMLEvent peek()
                            throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getElementText()
                            throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    @Override
    public XMLEvent nextTag()
                            throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getProperty( String name )
                            throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close()
                            throws XMLStreamException {
        reader.close();
    }

    /**
     * Peek to the next {@link XMLEvent}. May be called multiple times to retrieve the next one... Use
     * #retrievePeekIterator() to control the start element. This method starts at the position of the underlying
     * {@link XMLEventReader}.
     * 
     * @return the next {@link XMLEvent}, never <code>null</code>
     * @throws XMLStreamException
     *             -if there is an error with the underlying XML
     * @throws NoSuchElementException
     *             - iteration has no more elements.
     */
    public XMLEvent peekNextEvent()
                            throws XMLStreamException {
        XMLEvent nextEvent = reader.nextEvent();
        events.add( nextEvent );
        return nextEvent;
    }

    /**
     * @param event
     *            used as start element if this event is already read from underlying {@link XMLEventReader}
     * @return the {@link Iterator} over all {@link XMLEvent}s beginning at the passed position or at the already
     *         retrieved {@link XMLEvent} if the passed event is null or not yet read. Iterates until the end of the
     *         xml.
     */
    public Iterator<XMLEvent> retrievePeekIterator( XMLEvent event ) {
        return new PeekIterator( event );
    }

    class PeekIterator implements Iterator<XMLEvent> {

        private int indexOfNextEvents = 0;

        /**
         * @param event
         *            current event, may be <code>null</code>
         */
        public PeekIterator( XMLEvent event ) {
            if ( event != null && events.contains( event ) )
                indexOfNextEvents = events.indexOf( event );
        }

        @Override
        public boolean hasNext() {
            if ( !events.isEmpty() )
                return true;
            return reader.hasNext();
        }

        @Override
        public XMLEvent next() {
            if ( indexOfNextEvents < events.size() )
                return events.get( indexOfNextEvents++ );
            try {
                return peekNextEvent();
            } catch ( XMLStreamException e ) {
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new IllegalArgumentException();
        }
    }

}
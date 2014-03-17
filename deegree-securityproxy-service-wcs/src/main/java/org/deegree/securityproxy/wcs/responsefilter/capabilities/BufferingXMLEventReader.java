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

import java.util.LinkedList;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/**
 * TODO add class documentation here
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

    public XMLEvent peekNextEvent()
                            throws XMLStreamException {
        XMLEvent nextEvent = reader.nextEvent();
        events.add( nextEvent );
        return nextEvent;
    }

}

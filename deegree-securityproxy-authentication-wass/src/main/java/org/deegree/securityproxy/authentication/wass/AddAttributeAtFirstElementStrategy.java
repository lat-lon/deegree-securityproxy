//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2014 by:
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
package org.deegree.securityproxy.authentication.wass;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

/**
 * Appends a new attribute in the root element of the xml.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class AddAttributeAtFirstElementStrategy implements PostStrategy {

    private final XMLEventFactory newFactory = XMLEventFactory.newFactory();

    private final XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();

    private final XMLInputFactory inputFactory = XMLInputFactory.newFactory();

    private final String attributeName;

    /**
     * @param attributeName
     *            the name of the attribute, never <code>null</code> or empty
     * @throws IllegalArgumentException
     *             if the attributeName is <code>null</code> or empty
     */
    public AddAttributeAtFirstElementStrategy( String attributeName ) {
        if ( attributeName == null || attributeName.isEmpty() )
            throw new IllegalArgumentException( "attributeName must not be null or empty!" );
        this.attributeName = attributeName;
    }

    @Override
    public void modifyPostRequest( InputStream originalStream, OutputStream modifiedStream, String parameterValue )
                            throws XMLStreamException {
        XMLEventWriter xmlWriter = outputFactory.createXMLEventWriter( modifiedStream );
        XMLEventReader xmlReader = inputFactory.createXMLEventReader( originalStream );

        copyXmlAndAppendValue( parameterValue, xmlWriter, xmlReader );
    }

    private void copyXmlAndAppendValue( String parameterValue, XMLEventWriter xmlWriter, XMLEventReader xmlReader )
                            throws XMLStreamException {
        boolean isFirstStartElement = true;
        while ( xmlReader.hasNext() ) {
            XMLEvent event = xmlReader.nextEvent();
            xmlWriter.add( event );
            if ( event.isStartElement() ) {
                if ( isFirstStartElement ) {
                    appendAttribute( parameterValue, xmlWriter );
                }
                isFirstStartElement = false;
            }
        }
    }

    private void appendAttribute( String parameterValue, XMLEventWriter xmlWriter )
                            throws XMLStreamException {
        if ( parameterValue != null ) {
            Attribute newAttribute = newFactory.createAttribute( attributeName, parameterValue );
            xmlWriter.add( newAttribute );
        }
    }

}
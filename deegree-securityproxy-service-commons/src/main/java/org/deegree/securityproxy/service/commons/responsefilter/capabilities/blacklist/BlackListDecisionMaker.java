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
package org.deegree.securityproxy.service.commons.responsefilter.capabilities.blacklist;

import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.deegree.securityproxy.service.commons.responsefilter.capabilities.BufferingXMLEventReader;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.DecisionMaker;

/**
 * An implementation of the {@link DecisionMaker} using a list of blacklist text values which are not ignored, when
 * elements with the same name/namspace and sub element are ignored.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class BlackListDecisionMaker implements DecisionMaker {

    private final String elementToSkipName;

    private final String elementToSkipNamespace;

    private final String subElementName;

    private final String subElementNamespace;

    private final List<String> blackListTextValues;

    /**
     * @param elementToSkipName
     *            name of the ignore candidate, never <code>null</code>
     * @param elementToSkipNamespace
     *            namespace of the ignore candidate, may be <code>null</code> if not set
     * @param subElementName
     *            name of the sub element deciding if ignored, never <code>null</code>
     * @param subElementNamespace
     *            namespace of the sub element deciding if ignored, may be <code>null</code> if not set
     * @param blackListTextValues
     *            values of the sub element, which should not be ignored. if the list is empty, #ignore will return true
     *            at all time, never <code>null</code>
     * @throws IllegalArgumentExceptionXmlModifierCreator
     *             - a required parameter is <code>null</code>
     */
    public BlackListDecisionMaker( String elementToSkipName, String elementToSkipNamespace, String subElementName,
                                   String subElementNamespace, List<String> blackListTextValues ) {
        checkParameters( elementToSkipName, subElementName, blackListTextValues );
        this.elementToSkipName = elementToSkipName;
        this.elementToSkipNamespace = elementToSkipNamespace;
        this.subElementName = subElementName;
        this.subElementNamespace = subElementNamespace;
        this.blackListTextValues = blackListTextValues;
    }

    @Override
    public boolean ignore( BufferingXMLEventReader reader, XMLEvent event, List<StartElement> visitedElements )
                    throws XMLStreamException {
        if ( event.isStartElement() ) {
            StartElement startElement = event.asStartElement();

            boolean isNameMatching = isNameMatching( startElement, elementToSkipName, elementToSkipNamespace );
            if ( !isNameMatching )
                return false;

            StartElement matchedSubElement = retrieveMatchingSubElement( reader, event );
            if ( matchedSubElement == null )
                return false;

            if ( elementTextIsNotBlacklisted( reader, matchedSubElement ) )
                return true;

        }
        return false;
    }

    /**
     * @return the blackListTextValues
     */
    public List<String> getBlackListTextValues() {
        return blackListTextValues;
    }

    private boolean elementTextIsNotBlacklisted( BufferingXMLEventReader reader, StartElement matchedSubElement )
                    throws XMLStreamException {
        String elementText = retrieveElementText( reader, matchedSubElement );
        return !blackListTextValues.contains( elementText );
    }

    private String retrieveElementText( BufferingXMLEventReader reader, XMLEvent event )
                    throws XMLStreamException {
        Iterator<XMLEvent> peekIterator = reader.retrievePeekIterator( event );
        XMLEvent peeked = skipCurrentEvent( event, peekIterator );
        while ( endElementIsNotReached( peeked ) ) {
            if ( peeked.isCharacters() ) {
                return peeked.asCharacters().getData();
            }
            peeked = peekIterator.next();
        }
        return null;
    }

    private StartElement retrieveMatchingSubElement( BufferingXMLEventReader reader, XMLEvent event ) {
        Iterator<XMLEvent> peekIterator = reader.retrievePeekIterator( event );
        int depth = 0;
        while ( peekIterator.hasNext() && depth >= 0 ) {
            XMLEvent peeked = peekIterator.next();
            if ( peeked.isStartElement() ) {
                if ( isPrimaryDescendantOfCurrentElement( depth ) ) {
                    StartElement matchedStartElement = peeked.asStartElement();
                    boolean matchesSubElement = isNameMatching( matchedStartElement, subElementName,
                                                                subElementNamespace );
                    if ( matchesSubElement )
                        return matchedStartElement;
                }
                depth++;
            } else if ( peeked.isEndElement() ) {
                depth--;
            }
        }
        return null;
    }

    private boolean isNameMatching( StartElement startElement, String name, String namespace ) {
        QName elementToSkipQName = new QName( namespace, name );
        return elementToSkipQName.equals( startElement.getName() );
    }

    private boolean isPrimaryDescendantOfCurrentElement( int depth ) {
        return depth == 0;
    }

    private boolean endElementIsNotReached( XMLEvent peeked ) {
        return !peeked.isEndElement();
    }

    private XMLEvent skipCurrentEvent( XMLEvent event, Iterator<XMLEvent> peekIterator ) {
        XMLEvent peeked = peekIterator.next();
        if ( peeked == event )
            peeked = peekIterator.next();
        return peeked;
    }

    private void checkParameters( String elementToSkipName, String subElementName, List<String> blackListTextValues ) {
        if ( elementToSkipName == null || elementToSkipName.isEmpty() )
            throw new IllegalArgumentException( "elementToSkipName must not be null or empty!" );
        if ( subElementName == null || subElementName.isEmpty() )
            throw new IllegalArgumentException( "subElementName must not be null or empty!" );
        if ( blackListTextValues == null )
            throw new IllegalArgumentException( "blackListTextValues must not be null or empty!" );
    }

}
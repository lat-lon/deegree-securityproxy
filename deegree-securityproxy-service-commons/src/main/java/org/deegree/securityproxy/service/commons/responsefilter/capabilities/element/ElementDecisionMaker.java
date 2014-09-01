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
package org.deegree.securityproxy.service.commons.responsefilter.capabilities.element;

import static java.util.Collections.singletonList;
import static org.deegree.securityproxy.service.commons.responsefilter.capabilities.element.PathUtils.isPathMatching;

import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.deegree.securityproxy.service.commons.responsefilter.capabilities.BufferingXMLEventReader;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.DecisionMaker;

/**
 * Decides if an element should be writer or not.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class ElementDecisionMaker implements DecisionMaker {

    private final List<ElementRule> elementRules;

    /**
     * Instantiates a {@link ElementDecisionMaker} with exactly one rule.
     * 
     * @param elementRule
     *            never <code>null</code>
     * @throws IllegalArgumentException
     *             if the elementRule is <code>null</code>
     */
    public ElementDecisionMaker( ElementRule elementRule ) {
        checkElementRule( elementRule );
        this.elementRules = singletonList( elementRule );
    }

    /**
     * Instantiates a {@link ElementDecisionMaker} with multiple rules.
     * 
     * @param elementRules
     *            never <code>null</code>
     * @throws IllegalArgumentException
     *             if the elementRule is <code>null</code>
     */
    public ElementDecisionMaker( List<ElementRule> elementRules ) {
        checkElementRules( elementRules );
        this.elementRules = elementRules;
    }

    @Override
    public boolean ignore( BufferingXMLEventReader reader, XMLEvent event, List<StartElement> visitedElements )
                    throws XMLStreamException {
        return event.isStartElement() && atLeastOneElementRuleIsMatching( reader, event, visitedElements );
    }

    private boolean atLeastOneElementRuleIsMatching( BufferingXMLEventReader reader, XMLEvent event,
                                                     List<StartElement> visitedElements )
                    throws XMLStreamException {
        for ( ElementRule elementRule : elementRules ) {
            boolean matchesElementRule = matchesElementRule( reader, event, elementRule, visitedElements );
            if ( matchesElementRule )
                return true;
        }
        return false;
    }

    private boolean matchesElementRule( BufferingXMLEventReader reader, XMLEvent event, ElementRule elementRule,
                                        List<StartElement> visitedElements )
                    throws XMLStreamException {
        StartElement startElement = event.asStartElement();
        QName ignoredQName = new QName( elementRule.getNamespace(), elementRule.getName() );
        final boolean isNameMatching = ignoredQName.equals( startElement.getName() );

        if ( !isNameMatching )
            return false;

        boolean isTextMatching = true;
        if ( elementRule.getText() != null ) {
            isTextMatching = isTextMatching( reader, event, elementRule ) == elementRule.isTextShouldMatch();
        }
        if ( !isTextMatching )
            return false;

        boolean isPathMatching = true;
        if ( elementRule.getPath() != null )
            isPathMatching = isPathMatching( elementRule.getPath(), visitedElements );

        if ( !isPathMatching )
            return false;

        if ( elementRule.getSubRule() != null )
            return isSubRuleMatching( reader, event, elementRule );

        return isPathMatching;
    }

    private boolean isTextMatching( BufferingXMLEventReader reader, XMLEvent event, ElementRule rule )
                    throws XMLStreamException {
        Iterator<XMLEvent> peekIterator = reader.retrievePeekIterator( event );
        XMLEvent peeked = skipCurrentEvent( event, peekIterator );
        while ( endElementIsNotReached( peeked ) ) {
            if ( peeked.isCharacters() ) {
                if ( elementTextIsAsExpected( rule, peeked ) )
                    return true;
            }
            peeked = peekIterator.next();
        }
        return false;
    }

    private boolean isSubRuleMatching( BufferingXMLEventReader reader, XMLEvent event, ElementRule elementRule )
                    throws XMLStreamException {
        ElementRule subRule = elementRule.getSubRule();
        if ( subRule != null ) {
            Iterator<XMLEvent> peekIterator = reader.retrievePeekIterator( event );
            int depth = 0;
            while ( peekIterator.hasNext() && depth >= 0 ) {
                XMLEvent peeked = peekIterator.next();
                if ( peeked.isStartElement() ) {
                    if ( isPrimaryDescendantOfCurrentElement( depth ) ) {
                        boolean matchesElementRule = matchesElementRule( reader, peeked, subRule, null );
                        if ( matchesElementRule )
                            return true;
                    }
                    depth++;
                } else if ( peeked.isEndElement() ) {
                    depth--;
                }
            }
        }
        return false;
    }

    private boolean elementTextIsAsExpected( ElementRule rule, XMLEvent peeked ) {
        return rule.getText().contains( peeked.asCharacters().getData() );
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

    private boolean isPrimaryDescendantOfCurrentElement( int depth ) {
        return depth == 0;
    }

    private void checkElementRule( ElementRule elementRuleToCheck ) {
        if ( elementRuleToCheck == null )
            throw new IllegalArgumentException( "elementRule must not be null!" );
    }

    private void checkElementRules( List<ElementRule> elementRulesToCheck ) {
        if ( elementRulesToCheck == null || elementRulesToCheck.isEmpty() )
            throw new IllegalArgumentException( "elementRules must not be null or empty!" );
    }

}
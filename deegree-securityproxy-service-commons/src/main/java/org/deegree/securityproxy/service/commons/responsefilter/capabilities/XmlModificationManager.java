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
package org.deegree.securityproxy.service.commons.responsefilter.capabilities;

import java.util.LinkedList;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.deegree.securityproxy.service.commons.responsefilter.capabilities.text.AttributeModifier;

/**
 * Encapsulates the different xml modification handlers.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class XmlModificationManager {

    private final DecisionMaker decisionMaker;

    private final AttributeModifier attributeModifier;

    /**
     * Instantiates a {@link XmlModificationManager} without attributeModifier.
     * 
     * @param decisionMaker
     *            may be <code>null</code>
     */
    public XmlModificationManager( DecisionMaker decisionMaker ) {
        this( decisionMaker, null );
    }

    /**
     * Instantiates a {@link XmlModificationManager} without decisionMaker.
     * 
     * @param attributeModifier
     *            may be <code>null</code>
     */
    public XmlModificationManager( AttributeModifier attributeModifier ) {
        this( null, attributeModifier );
    }

    /**
     * Instantiates a {@link XmlModificationManager} with decisionMaker and attributeModifier.
     * 
     * @param decisionMaker
     *            may be <code>null</code>
     * @param attributeModifier
     *            may be <code>null</code>
     */
    public XmlModificationManager( DecisionMaker decisionMaker, AttributeModifier attributeModifier ) {
        this.decisionMaker = decisionMaker;
        this.attributeModifier = attributeModifier;
    }

    /**
     * if an attributeModifier is available
     * {@link AttributeModifier#determineNewAttributeValue(BufferingXMLEventReader, StartElement, Attribute, LinkedList)}
     * is invoked, if the attributeModifier is <code>null</code>, <code>null</code> is returned
     */
    public String determineNewAttributeValue( BufferingXMLEventReader reader, StartElement startElement,
                                              Attribute attribute, LinkedList<StartElement> visitedElements )
                    throws XMLStreamException {
        if ( attributeModifier != null )
            return attributeModifier.determineNewAttributeValue( reader, startElement, attribute, visitedElements );
        return null;
    }

    /**
     * if an decisionMaker is available {@link DecisionMaker#ignore(BufferingXMLEventReader, XMLEvent, java.util.List)}
     * is invoked, if the decisionMaker <code>null</code>, <code>null</code> is returned
     **/
    public boolean ignore( BufferingXMLEventReader reader, XMLEvent currentEvent,
                           LinkedList<StartElement> visitedElements )
                    throws XMLStreamException {
        if ( decisionMaker != null )
            return decisionMaker.ignore( reader, currentEvent, visitedElements );
        return false;
    }

    /**
     * @return <code>true</code> if one of the decisionMaker or atttributeModifier is not <code>null</code>,
     *         <code>false</code> if one of both is available
     */
    public boolean isModificationRequired() {
        return decisionMaker != null || attributeModifier != null;
    }

    /**
     * @return the decisionMaker may be <code>null</code>
     */
    public DecisionMaker getDecisionMaker() {
        return decisionMaker;
    }

    /**
     * @return the attributeModifier may be <code>null</code>
     */
    public AttributeModifier getAttributeModifier() {
        return attributeModifier;
    }

}
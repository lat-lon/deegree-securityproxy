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
package org.deegree.securityproxy.service.commons.responsefilter.capabilities.text;

import static org.deegree.securityproxy.service.commons.responsefilter.capabilities.element.PathUtils.isPathMatching;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;

import org.deegree.securityproxy.service.commons.responsefilter.capabilities.BufferingXMLEventReader;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.element.ElementPathStep;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.element.PathUtils;

/**
 * returns a static text if one of the {@link ElementPathStep} matches
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class StaticAttributeModifier implements AttributeModifier {

    private final List<AttributeModificationRule> attributeModificationRules;

    private final String attributeName;

    private final String attributeNamespace;

    /**
     * Instantiates a new {@link StaticAttributeModifier} which does not check the attribute.
     * 
     * @param attributeModificationRules
     *            never <code>null</code>
     */
    public StaticAttributeModifier( List<AttributeModificationRule> attributeModificationRules ) {
        this( attributeModificationRules, null, null );
    }

    /**
     * @param attributeModificationRules
     *            never <code>null</code>
     * @param attributeName
     *            , must not be <code>null</code> (if <code>null</code> the attribute is not checked)
     * @param attributeNamespace
     *            may be <code>null</code> for attributes without namespace
     */
    public StaticAttributeModifier( List<AttributeModificationRule> attributeModificationRules, String attributeName,
                                    String attributeNamespace ) {
        this.attributeModificationRules = attributeModificationRules;
        this.attributeName = attributeName;
        this.attributeNamespace = attributeNamespace;
    }

    @Override
    public String determineNewAttributeValue( BufferingXMLEventReader reader, StartElement currentStartElement,
                                              Attribute attribute, LinkedList<StartElement> visitedElements )
                    throws XMLStreamException {
        LinkedList<StartElement> extendedPath = extendPathByCurrentStartElement( currentStartElement, visitedElements );
        for ( AttributeModificationRule rule : attributeModificationRules ) {
            boolean isPathMatching = isPathMatching( rule.getPath(), extendedPath );
            if ( isPathMatching && isAttributeMatching( attribute ) )
                return rule.getValue();
        }
        return null;
    }

    private LinkedList<StartElement> extendPathByCurrentStartElement( StartElement currentStartElement,
                                                                      LinkedList<StartElement> visitedElements ) {
        LinkedList<StartElement> extendedPath = new LinkedList<StartElement>( visitedElements );
        extendedPath.add( currentStartElement );
        return extendedPath;
    }

    private boolean isAttributeMatching( Attribute attribute ) {
        return attributeName == null || PathUtils.isAttributeMatching( attribute, attributeName, attributeNamespace );
    }

    /**
     * @return the attributeModificationRules, never <code>null</code>
     */
    public List<AttributeModificationRule> getAttributeModificationRules() {
        return Collections.unmodifiableList( attributeModificationRules );
    }

}
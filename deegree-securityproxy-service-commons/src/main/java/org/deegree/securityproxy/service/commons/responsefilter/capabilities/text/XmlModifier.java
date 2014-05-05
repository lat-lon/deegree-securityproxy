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
package org.deegree.securityproxy.service.commons.responsefilter.capabilities.text;

import java.util.LinkedList;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;

import org.deegree.securityproxy.service.commons.responsefilter.capabilities.BufferingXMLEventReader;

/**
 * Encapsulates modifications of the XML (e. g. attribute or element text changes).
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public interface XmlModifier {

    /**
     * Checks if the current attribute value must be modified or not and returns the new value
     * 
     * @param reader
     *            the event reader currently read, never <code>null</code>
     * @param event
     *            the current start element containing the attribute, never <code>null</code>
     * @param attribute
     *            to be filtered or not, never <code>null</code>
     * @param visitedElements
     *            a list of already visited start elements, never <code>null</code>
     * @return the new value if modification is required, <code>null</code> if modification of the attribute value is
     *         not required
     * @throws XMLStreamException
     *             -if there is an error with the underlying XML
     */
    String determineNewAttributeValue( BufferingXMLEventReader reader, StartElement event, Attribute attribute,
                                       LinkedList<StartElement> visitedElements )
                            throws XMLStreamException;

}
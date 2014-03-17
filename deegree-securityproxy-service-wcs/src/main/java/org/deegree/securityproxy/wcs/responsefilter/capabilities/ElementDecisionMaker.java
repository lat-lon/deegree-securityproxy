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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * Decides if an element should be writer or not.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class ElementDecisionMaker {

    private final ElementRule elementRule;

    /**
     * @param elementRule
     *            never <code>null</code>
     * @throws IllegalArgumentException
     *             if the elementRule is <code>null</code>
     */
    public ElementDecisionMaker( ElementRule elementRule ) {
        this.elementRule = elementRule;
        checkNameToFilter( elementRule );
    }

    /**
     * @param reader
     *            the event reader currently read, never <code>null</code>
     * @param event
     *            the current event to filter, never <code>null</code>
     * @return <code>true</code> if the passed event should be skipped, <code>false</code> otherwise
     * @throws XMLStreamException
     */
    public boolean ignore( XMLEventReader reader, XMLEvent event )
                            throws XMLStreamException {
        if ( event.isStartElement() && hasStartElementNameToFilter( event ) && hasExpectedElementText( reader ) ) {
            return true;
        }
        return false;
    }

    private boolean hasStartElementNameToFilter( XMLEvent event ) {
        StartElement startElement = event.asStartElement();
        QName ignoredQName = new QName( elementRule.getNamespace(), elementRule.getName() );
        return ignoredQName.equals( startElement.getName() );
    }

    private boolean hasExpectedElementText( XMLEventReader reader )
                            throws XMLStreamException {
        // String text = elementRule.getText();
        // if ( text != null ) {
        // XMLEvent peek = reader.peek();
        // if ( peek.isCharacters() )
        // return text.equals( peek.asCharacters().getData() );
        // }
        // return false;
        return true;
    }

    private void checkNameToFilter( ElementRule elementRule ) {
        if ( elementRule == null )
            throw new IllegalArgumentException( "nameToFilter must not be null or empty!" );
    }

}
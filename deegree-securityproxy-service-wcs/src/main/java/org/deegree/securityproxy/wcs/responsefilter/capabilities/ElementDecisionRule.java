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
import javax.xml.stream.events.XMLEvent;

/**
 * Decides if an element should be writer or not.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class ElementDecisionRule {

    private final String nameToFilter;

    private final String namespace;

    /**
     * @param nameToFilter
     *            used to compare with the element name, never <code>null</code> or empty
     * @param namespace
     *            namespace uri may be <code>null</code> if the namespace uri is not set
     * @throws IllegalArgumentException
     *             if the parameter is <code>null</code>
     */
    public ElementDecisionRule( String nameToFilter, String namespace ) {
        checkNameToFilter( nameToFilter );
        this.nameToFilter = nameToFilter;
        this.namespace = namespace;
    }

    /**
     * @param event
     *            the current event to filter, never <code>null</code>
     * @return <code>true</code> if the passed event should be skipped, <code>false</code> otherwise
     */
    public boolean ignore( XMLEvent event ) {
        if ( event.isStartElement() && hasStartElementNameToFilter( event ) ) {
            return true;
        }
        return false;
    }

    private boolean hasStartElementNameToFilter( XMLEvent event ) {
        return new QName( namespace, nameToFilter ).equals( event.asStartElement().getName() );
    }

    private void checkNameToFilter( String nameToFilter ) {
        if ( nameToFilter == null || nameToFilter.isEmpty() )
            throw new IllegalArgumentException( "nameToFilter must not be null or empty!" );
    }
}
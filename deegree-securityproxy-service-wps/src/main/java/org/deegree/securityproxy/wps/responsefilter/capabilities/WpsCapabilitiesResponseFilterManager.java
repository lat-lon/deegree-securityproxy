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
package org.deegree.securityproxy.wps.responsefilter.capabilities;

import static org.deegree.securityproxy.wps.request.parser.WpsGetRequestParser.GETCAPABILITIES;

import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.responsefilter.ResponseFilterManager;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.AbstractCapabilitiesResponseFilterManager;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.XmlFilter;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.XmlModificationManager;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.XmlModificationManagerCreator;
import org.deegree.securityproxy.wps.request.WpsRequest;

/**
 * {@link ResponseFilterManager} filtering wps capabilities documents by user permissions.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class WpsCapabilitiesResponseFilterManager extends AbstractCapabilitiesResponseFilterManager {

    /**
     * @param capabilitiesFilter
     *            used to filter the capabilities, never <code>null</code>
     * @param xmlModificationManagerCreator
     *            used to create the {@link XmlModificationManager}, never <code>null</code>
     */
    public WpsCapabilitiesResponseFilterManager( XmlFilter capabilitiesFilter,
                                                 XmlModificationManagerCreator xmlModificationManagerCreator ) {
        super( capabilitiesFilter, xmlModificationManagerCreator );
    }

    @Override
    protected boolean isCorrectServiceType( OwsRequest request ) {
        return WpsRequest.class.equals( request.getClass() );
    }

    @Override
    protected boolean isCorrectRequestParameter( OwsRequest request ) {
        return GETCAPABILITIES.equals( request.getOperationType() );
    }

}
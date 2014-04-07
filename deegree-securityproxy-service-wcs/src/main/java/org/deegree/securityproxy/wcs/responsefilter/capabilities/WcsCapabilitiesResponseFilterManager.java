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

import static org.deegree.securityproxy.wcs.request.WcsRequestParser.GETCAPABILITIES;

import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.responsefilter.ResponseFilterManager;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.AbstractCapabilitiesResponseFilterManager;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.CapabilitiesFilter;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.DecisionMaker;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.DecisionMakerCreator;
import org.deegree.securityproxy.wcs.request.WcsRequest;

/**
 * {@link ResponseFilterManager} filtering capabilities documents by user permissions.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="mailto:goltz@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * 
 * @version $Revision: $, $Date: $
 */
public class WcsCapabilitiesResponseFilterManager extends AbstractCapabilitiesResponseFilterManager {

    /**
     * @param capabilitiesFilter
     *            used to filter the capabilities, never <code>null</code>
     * @param decisionMakerCreator
     *            used to create the {@link DecisionMaker}, never <code>null</code>
     */
    public WcsCapabilitiesResponseFilterManager( CapabilitiesFilter capabilitiesFilter,
                                                 DecisionMakerCreator decisionMakerCreator ) {
        super( capabilitiesFilter, decisionMakerCreator );
    }

    @Override
    protected boolean isCorrectRequestType( OwsRequest owsRequest ) {
        return WcsRequest.class.equals( owsRequest.getClass() );
    }

    @Override
    protected boolean isGetCapabilitiesRequest( OwsRequest owsRequest ) {
        return GETCAPABILITIES.equals( owsRequest.getOperationType() );
    }

}
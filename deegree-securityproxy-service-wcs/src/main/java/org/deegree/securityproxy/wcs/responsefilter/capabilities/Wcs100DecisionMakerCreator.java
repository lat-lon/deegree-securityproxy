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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.deegree.securityproxy.authentication.ows.raster.RasterPermission;
import org.deegree.securityproxy.request.OwsServiceVersion;
import org.deegree.securityproxy.wcs.responsefilter.capabilities.blacklist.BlackListDecisionMaker;
import org.deegree.securityproxy.wcs.responsefilter.capabilities.element.ElementRule;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * Creates {@link ElementRule} instances.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class Wcs100DecisionMakerCreator {

    private static final OwsServiceVersion VERSION_1_0_0 = new OwsServiceVersion( 1, 0, 0 );

    static final String WCS_1_0_0_NS_URI = "http://www.opengis.net/wcs";

    private static final String ELEMENT_TO_FILTER = "CoverageOfferingBrief";

    private static final String SUB_ELEMENT_NAME = "name";

    /**
     * Creates a {@link DecisionMaker} for WCS 1.0.0 capabilities
     * 
     * @param authentication
     *            containing the user rules to use as filters, never <code>null</code>
     * @return the {@link DecisionMaker} or <code>null</code>, if filtering is not required
     */
    public DecisionMaker createDecisionMakerForWcs100( Authentication authentication ) {
        List<String> blackListTextValues = new ArrayList<String>();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for ( GrantedAuthority grantedAuthority : authorities ) {
            addBlackListValuesFromAuthorities( blackListTextValues, grantedAuthority );
        }
        if ( !blackListTextValues.isEmpty() )
            return new BlackListDecisionMaker( ELEMENT_TO_FILTER, WCS_1_0_0_NS_URI, SUB_ELEMENT_NAME, WCS_1_0_0_NS_URI,
                                               blackListTextValues );
        return null;
    }

    private void addBlackListValuesFromAuthorities( List<String> blackListTextValues, GrantedAuthority grantedAuthority ) {
        if ( grantedAuthority instanceof RasterPermission ) {
            RasterPermission permission = (RasterPermission) grantedAuthority;
            if ( isWcs100GetCoveragePermission( permission ) ) {
                blackListTextValues.add( permission.getLayerName() );
            }
        }
    }

    private boolean isWcs100GetCoveragePermission( RasterPermission permission ) {
        return "wcs".equalsIgnoreCase( permission.getServiceType() )
               && permission.getServiceVersion().contains( VERSION_1_0_0 )
               && "getcoverage".equalsIgnoreCase( permission.getOperationType() );
    }

}
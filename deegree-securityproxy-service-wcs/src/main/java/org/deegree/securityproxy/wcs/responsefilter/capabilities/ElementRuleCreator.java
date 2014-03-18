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
public class ElementRuleCreator {

    private static final OwsServiceVersion VERSION_1_0_0 = new OwsServiceVersion( 1, 0, 0 );

    static final String WCS_1_0_0_NS_URI = "http://www.opengis.net/wcs";

    /**
     * Creates a {@link List} of {@link ElementRule}s for WCS 1.0.0 specifying the elements to remove from the
     * capabilities document.
     * 
     * @param authentication
     *            containing the user rules to use as filters, never <code>null</code>
     * @return a list of {@link ElementRule}s to use as specifying the elements to remove from the capabilities
     *         document, never <code>null</code>
     */
    public List<ElementRule> createElementRulesForWcs100( Authentication authentication ) {
        List<ElementRule> elementRules = new ArrayList<ElementRule>();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for ( GrantedAuthority grantedAuthority : authorities ) {
            ElementRule elementRule = createElementRule( grantedAuthority );
            if ( elementRule != null )
                elementRules.add( elementRule );
        }
        return elementRules;
    }

    private ElementRule createElementRule( GrantedAuthority grantedAuthority ) {
        if ( grantedAuthority instanceof RasterPermission ) {
            RasterPermission permission = (RasterPermission) grantedAuthority;
            if ( isWcs100Permission( permission ) ) {
                return createWcsRule( permission );
            }
        }
        return null;
    }

    private ElementRule createWcsRule( RasterPermission permission ) {
        if ( "getcoverage".equalsIgnoreCase( permission.getOperationType() ) ) {
            return createGetCoverageRule( permission );
        }
        return null;
    }

    private ElementRule createGetCoverageRule( RasterPermission rasterPermission ) {
        ElementRule subRule = new ElementRule( "name", WCS_1_0_0_NS_URI, rasterPermission.getLayerName() );
        return new ElementRule( "CoverageOfferingBrief", WCS_1_0_0_NS_URI, subRule );
    }

    private boolean isWcs100Permission( RasterPermission permission ) {
        return "wcs".equalsIgnoreCase( permission.getServiceType() )
               && permission.getServiceVersion().contains( VERSION_1_0_0 );
    }

}
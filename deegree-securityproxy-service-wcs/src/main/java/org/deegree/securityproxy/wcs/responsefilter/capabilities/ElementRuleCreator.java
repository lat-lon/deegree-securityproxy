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

import javax.xml.namespace.QName;

import org.deegree.securityproxy.authentication.ows.raster.RasterPermission;
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

    private static final String WCS_NS_URI = "http://www.opengis.net/wcs/1.1";

    private static final String OWCS_NS_URI = "http://www.opengis.net/wcs/1.1/ows";

    public List<ElementRule> createElementRules( Authentication authentication ) {
        List<ElementRule> elementRules = new ArrayList<ElementRule>();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for ( GrantedAuthority grantedAuthority : authorities ) {
            ElementRule elementRule = createElementRule( grantedAuthority );
            if ( elementRule != null )
                elementRules.add( elementRule );
        }
        return elementRules;
    }

    ElementRule createElementRule( GrantedAuthority grantedAuthority ) {
        if ( grantedAuthority instanceof RasterPermission ) {
            RasterPermission permission = (RasterPermission) grantedAuthority;
            if ( "WCS".equalsIgnoreCase( permission.getServiceType() ) ) {
                return createWcsRule( permission );
            }
        }
        return null;
    }

    private ElementRule createWcsRule( RasterPermission permission ) {
        if ( "describecoverage".equalsIgnoreCase( permission.getOperationType() ) ) {
            return createDescribeCoverageRule( permission );
        } else if ( "getcoverage".equalsIgnoreCase( permission.getOperationType() ) ) {
            return createGetCoverageRule( permission );
        }
        return null;
    }

    // TODO: consider version in namespace uri and path
    private ElementRule createGetCoverageRule( RasterPermission rasterPermission ) {
        ElementRule subRule = new ElementRule( "Identifier", WCS_NS_URI, rasterPermission.getLayerName() );
        return new ElementRule( "CoverageSummary", WCS_NS_URI, subRule );
    }

    // TODO: consider version in namespace uri and path
    private ElementRule createDescribeCoverageRule( RasterPermission rasterPermission ) {
        List<ElementPathStep> path = new ArrayList<ElementPathStep>();
        path.add( new ElementPathStep( new QName( WCS_NS_URI, "Capabilities" ) ) );
        path.add( new ElementPathStep( new QName( OWCS_NS_URI, "OperationsMetadata" ) ) );
        path.add( new ElementPathStep( new QName( OWCS_NS_URI, "Operation" ), new QName( "name" ), "DescribeCoverage" ) );
        path.add( new ElementPathStep( new QName( OWCS_NS_URI, "Parameter" ), new QName( "name" ), "Identifier" ) );
        path.add( new ElementPathStep( new QName( OWCS_NS_URI, "AllowedValues" ) ) );
        path.add( new ElementPathStep( new QName( OWCS_NS_URI, "Parameter" ) ) );
        return new ElementRule( "value", OWCS_NS_URI, rasterPermission.getLayerName(), path );
    }

}
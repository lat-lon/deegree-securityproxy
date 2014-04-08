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
package org.deegree.securityproxy.wms.responsefilter.capabilities;

import static org.deegree.securityproxy.wms.request.WmsRequestParser.GETMAP;
import static org.deegree.securityproxy.wms.request.WmsRequestParser.WMS_SERVICE;
import static org.deegree.securityproxy.wms.request.WmsRequestParser.VERSION_130;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.deegree.securityproxy.authentication.ows.raster.RasterPermission;
import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.request.OwsServiceVersion;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.DecisionMaker;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.DecisionMakerCreator;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.blacklist.BlackListDecisionMaker;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * Creates {@link DecisionMaker}s for WMS capabilities requests.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class WmsDecisionMakerCreator implements DecisionMakerCreator {

    private static final OwsServiceVersion VERSION_1_3_0 = new OwsServiceVersion( 1, 3, 0 );

    static final String WMS_1_3_0_NS_URI = "http://www.opengis.net/wms";

    private static final String ELEMENT_TO_FILTER = "Layer";

    private static final String SUB_ELEMENT_NAME = "Name";

    @Override
    public DecisionMaker createDecisionMaker( OwsRequest owsRequest, Authentication authentication ) {
        checkVersion( owsRequest );
        List<String> blackListLayerNames = new ArrayList<String>();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for ( GrantedAuthority grantedAuthority : authorities ) {
            addBlackListValuesFromAuthorities( blackListLayerNames, grantedAuthority );
        }
        if ( !blackListLayerNames.isEmpty() )
            return new BlackListDecisionMaker( ELEMENT_TO_FILTER, WMS_1_3_0_NS_URI, SUB_ELEMENT_NAME, WMS_1_3_0_NS_URI,
                                               blackListLayerNames );
        return null;
    }

    private void checkVersion( OwsRequest owsRequest ) {
        if ( !VERSION_1_3_0.equals( owsRequest.getServiceVersion() ) )
            throw new IllegalArgumentException( "Capabilities request for version " + owsRequest.getServiceVersion()
                                                + " is not supported yet!" );
    }

    private void addBlackListValuesFromAuthorities( List<String> blackListTextValues, GrantedAuthority authority ) {
        if ( authority instanceof RasterPermission ) {
            RasterPermission permission = (RasterPermission) authority;
            if ( isWms130GetMapPermission( permission ) ) {
                blackListTextValues.add( permission.getLayerName() );
            }
        }
    }

    private boolean isWms130GetMapPermission( RasterPermission permission ) {
        return WMS_SERVICE.equalsIgnoreCase( permission.getServiceType() )
               && permission.getServiceVersion().contains( VERSION_130 )
               && GETMAP.equalsIgnoreCase( permission.getOperationType() );
    }

}
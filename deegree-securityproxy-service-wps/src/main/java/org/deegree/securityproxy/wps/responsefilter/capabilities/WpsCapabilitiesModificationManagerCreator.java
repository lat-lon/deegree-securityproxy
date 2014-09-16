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

import static org.deegree.securityproxy.wps.request.parser.WpsRequestParser.DESCRIBEPROCESS;
import static org.deegree.securityproxy.wps.request.parser.WpsRequestParser.EXECUTE;
import static org.deegree.securityproxy.wps.request.parser.WpsRequestParser.GETCAPABILITIES;
import static org.deegree.securityproxy.wps.request.parser.WpsRequestParser.WPS_SERVICE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.deegree.securityproxy.authentication.ows.raster.RasterPermission;
import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.request.OwsServiceVersion;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.DecisionMaker;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.XmlModificationManager;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.XmlModificationManagerCreator;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.blacklist.BlackListDecisionMaker;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * Provides an {@link XmlModificationManager} instance to filter WPS capabilities.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class WpsCapabilitiesModificationManagerCreator implements XmlModificationManagerCreator {

    private static final OwsServiceVersion VERSION_1_0_0 = new OwsServiceVersion( 1, 0, 0 );

    private static final String ELEMENT_TO_SKIP = "Process";

    private static final String WPS_1_0_0_NS_URI = "http://www.opengis.net/wps/1.0.0";

    private static final String OWS_1_1_NS_URI = "http://www.opengis.net/ows/1.1";

    private static final String SUB_ELEMENT_NAME = "Identifier";

    @Override
    public XmlModificationManager createXmlModificationManager( OwsRequest owsRequest, Authentication authentication ) {
        checkVersion( owsRequest );
        return new XmlModificationManager( createDecisionMaker( authentication ) );
    }

    DecisionMaker createDecisionMaker( Authentication authentication ) {
        List<String> authenticatedProcessIds = collectAuthenticatedProcessIds( authentication );
        return new BlackListDecisionMaker( ELEMENT_TO_SKIP, WPS_1_0_0_NS_URI, SUB_ELEMENT_NAME, OWS_1_1_NS_URI,
                        authenticatedProcessIds );
    }

    private List<String> collectAuthenticatedProcessIds( Authentication authentication ) {
        Set<String> layerNamesToPreserve = new HashSet<String>();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for ( GrantedAuthority grantedAuthority : authorities ) {
            addLayerNameRule( layerNamesToPreserve, grantedAuthority );
        }
        return new ArrayList<String>( layerNamesToPreserve );
    }

    private void addLayerNameRule( Set<String> layerNameRulesToPreserve, GrantedAuthority authority ) {
        if ( authority instanceof RasterPermission ) {
            RasterPermission permission = (RasterPermission) authority;
            if ( isWps100Permission( permission ) ) {
                layerNameRulesToPreserve.add( permission.getLayerName() );
            }
        }
    }

    private boolean isWps100Permission( RasterPermission permission ) {
        return WPS_SERVICE.equalsIgnoreCase( permission.getServiceType() )
               && permission.getServiceVersion().contains( VERSION_1_0_0 ) && isValidOperation( permission );
    }

    private boolean isValidOperation( RasterPermission permission ) {
        return EXECUTE.equalsIgnoreCase( permission.getOperationType() )
               || DESCRIBEPROCESS.equalsIgnoreCase( permission.getOperationType() )
               || GETCAPABILITIES.equalsIgnoreCase( permission.getOperationType() );
    }

    private void checkVersion( OwsRequest owsRequest ) {
        if ( !VERSION_1_0_0.equals( owsRequest.getServiceVersion() ) )
            throw new IllegalArgumentException( "Capabilities request for version " + owsRequest.getServiceVersion()
                                                + " is not supported yet!" );
    }

}
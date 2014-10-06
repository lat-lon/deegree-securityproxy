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

import org.deegree.securityproxy.authentication.ows.raster.RasterPermission;
import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.request.OwsServiceVersion;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.DecisionMaker;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.XmlModificationManager;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.XmlModificationManagerCreator;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.blacklist.BlackListDecisionMaker;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.element.ElementPathStep;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.text.AttributeModificationRule;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.text.AttributeModifier;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.text.StaticAttributeModifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.deegree.securityproxy.wms.request.WmsRequestParser.GETFEATUREINFO;
import static org.deegree.securityproxy.wms.request.WmsRequestParser.GETMAP;
import static org.deegree.securityproxy.wms.request.WmsRequestParser.VERSION_130;
import static org.deegree.securityproxy.wms.request.WmsRequestParser.WMS_SERVICE;

/**
 * Creates {@link DecisionMaker}s for WMS capabilities requests.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class WmsCapabilitiesModificationManagerCreator implements XmlModificationManagerCreator {

    private String getDcpUrl;

    private String postDcpUrl;

    private static final OwsServiceVersion VERSION_1_3_0 = new OwsServiceVersion( 1, 3, 0 );

    static final String WMS_1_3_0_NS_URI = "http://www.opengis.net/wms";

    private static final String ELEMENT_TO_FILTER = "Layer";

    private static final String SUB_ELEMENT_NAME = "Name";

    /**
     * Instantiates a new {@link WmsCapabilitiesModificationManagerCreator}. DCP url is not replaced!
     */
    public WmsCapabilitiesModificationManagerCreator() {
        this( null, null );
    }

    /**
     *
     * @param getDcpUrl
     *            the new HTTP GET DCP URL replacing all DCP urls in the capabilities document, if <code>null</code>
     *            nothing is replaced
     * @param postDcpUrl
     *            the new HTTP POST DCP URL replacing all DCP urls in the capabilities document, if <code>null</code>
     *            nothing is replaced
     */
    public WmsCapabilitiesModificationManagerCreator( String getDcpUrl, String postDcpUrl ) {
        this.getDcpUrl = getDcpUrl;
        this.postDcpUrl = postDcpUrl;
    }

    @Override
    public XmlModificationManager createXmlModificationManager( OwsRequest owsRequest, Authentication authentication ) {
        return new XmlModificationManager( createDecisionMaker( owsRequest, authentication ), createAttributeModifier() );
    }

    private DecisionMaker createDecisionMaker( OwsRequest owsRequest, Authentication authentication ) {
        checkVersion( owsRequest );
        List<String> blackListLayerNames = new ArrayList<String>();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for ( GrantedAuthority grantedAuthority : authorities ) {
            addBlackListValuesFromAuthorities( blackListLayerNames, grantedAuthority );
        }
        return new BlackListDecisionMaker( ELEMENT_TO_FILTER, WMS_1_3_0_NS_URI, SUB_ELEMENT_NAME, WMS_1_3_0_NS_URI,
                        blackListLayerNames );
    }

    private AttributeModifier createAttributeModifier() {
        List<AttributeModificationRule> getRules = createGetRules();
        List<AttributeModificationRule> postRules = createPostRules();
        if ( !getRules.isEmpty() || !postRules.isEmpty() ) {
            List<AttributeModificationRule> rules = new LinkedList<AttributeModificationRule>();
            rules.addAll( getRules );
            rules.addAll( postRules );
            return new StaticAttributeModifier( rules, "href", "http://www.w3.org/1999/xlink" );
        }
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
            if ( isWms130GetMapPermission( permission ) && !blackListTextValues.contains( permission.getLayerName() ) ) {
                blackListTextValues.add( permission.getLayerName() );
            }
        }
    }

    private boolean isWms130GetMapPermission( RasterPermission permission ) {
        return WMS_SERVICE.equalsIgnoreCase( permission.getServiceType() )
               && permission.getServiceVersion().contains( VERSION_130 ) && isGetMapOrFeatureInfo( permission );
    }

    private boolean isGetMapOrFeatureInfo( RasterPermission permission ) {
        return GETMAP.equalsIgnoreCase( permission.getOperationType() )
               || GETFEATUREINFO.equalsIgnoreCase( permission.getOperationType() );
    }

    private List<AttributeModificationRule> createGetRules() {
        return createRules( getDcpUrl, "Get" );
    }

    private List<AttributeModificationRule> createPostRules() {
        return createRules( postDcpUrl, "Post" );
    }

    private List<AttributeModificationRule> createRules( String url, String getOrPost ) {
        List<AttributeModificationRule> rules = new ArrayList<AttributeModificationRule>();
        if ( url != null ) {
            rules.add( new AttributeModificationRule( url, createPath( "GetCapabilities", getOrPost ) ) );
            rules.add( new AttributeModificationRule( url, createPath( "GetMap", getOrPost ) ) );
            rules.add( new AttributeModificationRule( url, createPath( "GetFeatureInfo", getOrPost ) ) );
        }
        return rules;
    }

    private LinkedList<ElementPathStep> createPath( String operationType, String getOrPost ) {
        LinkedList<ElementPathStep> path = new LinkedList<ElementPathStep>();
        path.add( new ElementPathStep( new QName( "http://www.opengis.net/wms", "WMS_Capabilities" ) ) );
        path.add( new ElementPathStep( new QName( "http://www.opengis.net/wms", "Capability" ) ) );
        path.add( new ElementPathStep( new QName( "http://www.opengis.net/wms", "Request" ) ) );
        path.add( new ElementPathStep( new QName( "http://www.opengis.net/wms", operationType ) ) );
        path.add( new ElementPathStep( new QName( "http://www.opengis.net/wms", "DCPType" ) ) );
        path.add( new ElementPathStep( new QName( "http://www.opengis.net/wms", "HTTP" ) ) );
        path.add( new ElementPathStep( new QName( "http://www.opengis.net/wms", getOrPost ) ) );
        path.add( new ElementPathStep( new QName( "http://www.opengis.net/wms", "OnlineResource" ) ) );
        return path;
    }

}
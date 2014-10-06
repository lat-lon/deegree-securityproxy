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
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.deegree.securityproxy.authentication.ows.raster.RasterPermission;
import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.request.OwsServiceVersion;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.DecisionMaker;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.XmlModificationManager;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.XmlModificationManagerCreator;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.blacklist.BlackListDecisionMaker;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.element.ElementPathStep;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.element.ElementRule;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.text.AttributeModificationRule;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.text.AttributeModifier;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.text.StaticAttributeModifier;
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
public class WcsCapabilitiesModificationManagerCreator implements XmlModificationManagerCreator {

    private static final OwsServiceVersion VERSION_1_0_0 = new OwsServiceVersion( 1, 0, 0 );

    static final String WCS_1_0_0_NS_URI = "http://www.opengis.net/wcs";

    private static final String ELEMENT_TO_FILTER = "CoverageOfferingBrief";

    private static final String SUB_ELEMENT_NAME = "name";

    private final String getDcpUrl;

    private final String postDcpUrl;

    /**
     * Instantiates a new {@link WcsCapabilitiesModificationManagerCreator}. DCP url is not replaced!
     */
    public WcsCapabilitiesModificationManagerCreator() {
        this( null, null );
    }

    /**
     * @param getDcpUrl
     *            the new HTTP GET DCP URL replacing all DCP urls in the capabilities document, if <code>null</code>
     *            nothing is replaced
     * @param postDcpUrl
     *            the new HTTP POST DCP URL replacing all DCP urls in the capabilities document, if <code>null</code>
     *            nothing is replaced
     */
    public WcsCapabilitiesModificationManagerCreator( String getDcpUrl, String postDcpUrl ) {
        this.getDcpUrl = getDcpUrl;
        this.postDcpUrl = postDcpUrl;
    }

    @Override
    public XmlModificationManager createXmlModificationManager( OwsRequest owsRequest, Authentication authentication ) {
        return new XmlModificationManager( createDecisionMaker( owsRequest, authentication ), createAttributeModifier() );
    }

    public DecisionMaker createDecisionMaker( OwsRequest owsRequest, Authentication authentication ) {
        checkVersion( owsRequest );
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

    AttributeModifier createAttributeModifier() {
        List<AttributeModificationRule> getRules = createGetRules();
        List<AttributeModificationRule> postRules = createPostRules();
        if ( !getRules.isEmpty() || !postRules.isEmpty() ) {
            List<AttributeModificationRule> rules = new LinkedList<AttributeModificationRule>();
            if ( getRules != null )
                rules.addAll( getRules );
            if ( postRules != null )
                rules.addAll( postRules );
            return new StaticAttributeModifier( rules, "href", "http://www.w3.org/1999/xlink" );
        }
        return null;
    }

    private void checkVersion( OwsRequest owsRequest ) {
        if ( !VERSION_1_0_0.equals( owsRequest.getServiceVersion() ) )
            throw new IllegalArgumentException( "Capabilities request for version " + owsRequest.getServiceVersion()
                                                + " is not supported yet!" );
    }

    private void addBlackListValuesFromAuthorities( List<String> blackListTextValues, GrantedAuthority authority ) {
        if ( authority instanceof RasterPermission ) {
            RasterPermission permission = (RasterPermission) authority;
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

    private List<AttributeModificationRule> createGetRules() {
        return createRules( getDcpUrl, "Get" );
    }

    private List<AttributeModificationRule> createPostRules() {
        return createRules( postDcpUrl, "Post" );
    }

    private List<AttributeModificationRule> createRules( String url, String method ) {
        List<AttributeModificationRule> rules = new ArrayList<AttributeModificationRule>();
        if ( url != null ) {
            rules.add( new AttributeModificationRule( url, createPath( "GetCapabilities", method ) ) );
            rules.add( new AttributeModificationRule( url, createPath( "DescribeCoverage", method ) ) );
            rules.add( new AttributeModificationRule( url, createPath( "GetCoverage", method ) ) );
        }
        return rules;
    }

    private LinkedList<ElementPathStep> createPath( String operation, String method ) {
        LinkedList<ElementPathStep> path = new LinkedList<ElementPathStep>();
        path.add( new ElementPathStep( new QName( WCS_1_0_0_NS_URI, "WCS_Capabilities" ) ) );
        path.add( new ElementPathStep( new QName( WCS_1_0_0_NS_URI, "Capability" ) ) );
        path.add( new ElementPathStep( new QName( WCS_1_0_0_NS_URI, "Request" ) ) );
        path.add( new ElementPathStep( new QName( WCS_1_0_0_NS_URI, operation ) ) );
        path.add( new ElementPathStep( new QName( WCS_1_0_0_NS_URI, "DCPType" ) ) );
        path.add( new ElementPathStep( new QName( WCS_1_0_0_NS_URI, "HTTP" ) ) );
        path.add( new ElementPathStep( new QName( WCS_1_0_0_NS_URI, method ) ) );
        path.add( new ElementPathStep( new QName( WCS_1_0_0_NS_URI, "OnlineResource" ) ) );
        return path;
    }

}
//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2014 by:
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
package org.deegree.securityproxy.wfs.responsefilter.capabilities;

import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.XmlModificationManager;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.XmlModificationManagerCreator;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.element.ElementPathStep;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.text.AttributeModificationRule;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.text.AttributeModifier;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.text.StaticAttributeModifier;
import org.springframework.security.core.Authentication;

/**
 * Creates {@link XmlModificationManagerCreator}s for WFS capabilities requests.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class WfsCapabilitiesModificationManagerCreator implements XmlModificationManagerCreator {

    private String getDcpUrl;

    private String postDcpUrl;

    /**
     * 
     * @param dcpUrl
     *            the new DCP URL replacing all DCP urls in the capabilities document, never <code>null</code>
     */
    public WfsCapabilitiesModificationManagerCreator( String getDcpUrl, String postDcpUrl ) {
        this.getDcpUrl = getDcpUrl;
        this.postDcpUrl = postDcpUrl;
    }

    @Override
    public XmlModificationManager createXmlModificationManager( OwsRequest owsRequest, Authentication authentication ) {
        return new XmlModificationManager( createAttributeModifier() );
    }

    private AttributeModifier createAttributeModifier() {
        List<AttributeModificationRule> rules = new LinkedList<AttributeModificationRule>();
        rules.add( createGetRule() );
        rules.add( createPostRule() );
        return new StaticAttributeModifier( rules );
    }

    private AttributeModificationRule createGetRule() {
        LinkedList<ElementPathStep> path = createBasePath();
        path.add( new ElementPathStep( new QName( "http://www.opengis.net/ows", "Get" ) ) );
        return new AttributeModificationRule( getDcpUrl, path );
    }

    private AttributeModificationRule createPostRule() {
        LinkedList<ElementPathStep> path = createBasePath();
        path.add( new ElementPathStep( new QName( "http://www.opengis.net/ows", "Post" ) ) );
        return new AttributeModificationRule( postDcpUrl, path );
    }

    private LinkedList<ElementPathStep> createBasePath() {
        LinkedList<ElementPathStep> path = new LinkedList<ElementPathStep>();
        path.add( new ElementPathStep( new QName( "http://www.opengis.net/wfs", "WFS_Capabilities" ) ) );
        path.add( new ElementPathStep( new QName( "http://www.opengis.net/ows", "OperationsMetadata" ) ) );
        path.add( new ElementPathStep( new QName( "http://www.opengis.net/ows", "Operation" ) ) );
        path.add( new ElementPathStep( new QName( "http://www.opengis.net/ows", "DCP" ) ) );
        path.add( new ElementPathStep( new QName( "http://www.opengis.net/ows", "HTTP" ) ) );
        return path;
    }

}
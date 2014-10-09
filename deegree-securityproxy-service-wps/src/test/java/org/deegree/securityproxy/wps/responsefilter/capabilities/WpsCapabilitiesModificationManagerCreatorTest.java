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

import static org.deegree.securityproxy.wps.request.parser.WpsGetRequestParser.DESCRIBEPROCESS;
import static org.deegree.securityproxy.wps.request.parser.WpsGetRequestParser.EXECUTE;
import static org.deegree.securityproxy.wps.request.parser.WpsGetRequestParser.WPS_SERVICE;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.deegree.securityproxy.authentication.ows.domain.LimitedOwsServiceVersion;
import org.deegree.securityproxy.authentication.ows.raster.RasterPermission;
import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.request.OwsServiceVersion;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.DecisionMaker;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.XmlModificationManager;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.blacklist.BlackListDecisionMaker;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.text.AttributeModificationRule;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.text.AttributeModifier;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.text.StaticAttributeModifier;
import org.deegree.securityproxy.wps.request.WpsRequest;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class WpsCapabilitiesModificationManagerCreatorTest {

    private static final String PROCESS_ID_1 = "process1";

    private static final String PROCESS_ID_2 = "process2";

    private final WpsCapabilitiesModificationManagerCreator decisionMakerCreator = new WpsCapabilitiesModificationManagerCreator();

    @Test
    public void testCreateDecisionMakerForWpsOneExecute()
                    throws Exception {
        DecisionMaker decisionMaker = retrieveDecisionMaker( createAuthenticationWithOneExecute() );

        List<String> blackListTextValues = ( (BlackListDecisionMaker) decisionMaker ).getBlackListTextValues();
        assertThat( blackListTextValues.size(), is( 1 ) );
        assertThat( blackListTextValues, hasItems( PROCESS_ID_1 ) );
    }

    @Test
    public void testCreateDecisionMakerForWpsTwoExecute()
                    throws Exception {
        DecisionMaker decisionMaker = retrieveDecisionMaker( createAuthenticationWithTwoExecuteOneDescribeProcess() );

        List<String> blackListTextValues = ( (BlackListDecisionMaker) decisionMaker ).getBlackListTextValues();
        assertThat( blackListTextValues.size(), is( 2 ) );
        assertThat( blackListTextValues, hasItems( PROCESS_ID_1, PROCESS_ID_2 ) );
    }

    @Test
    public void testCreateDecisionMakerForWpsNoExecute()
                    throws Exception {
        DecisionMaker decisionMaker = retrieveDecisionMaker( createAuthenticationWithOneUnknownRequest() );

        List<String> blackListTextValues = ( (BlackListDecisionMaker) decisionMaker ).getBlackListTextValues();
        assertThat( blackListTextValues.size(), is( 0 ) );
    }

    @Test
    public void testCreateXmlModificationManagerForWpsNoExecute()
                    throws Exception {
        XmlModificationManager xmlModificationManager = decisionMakerCreator.createXmlModificationManager( createWps100Request(),
                                                                                                           createAuthenticationWithOneExecute() );

        assertThat( xmlModificationManager, is( notNullValue() ) );
    }

    @Test
    public void testCreateAttributeModifier()
                    throws Exception {
        WpsCapabilitiesModificationManagerCreator decisionMakerCreator = new WpsCapabilitiesModificationManagerCreator();
        AttributeModifier attributeModifier = retrieveAttributeModifier( decisionMakerCreator );

        assertThat( attributeModifier, is( nullValue() ) );
    }

    @Test
    public void testCreateAttributeModifierWithGetDcpUrl()
                    throws Exception {
        WpsCapabilitiesModificationManagerCreator decisionMakerCreator = new WpsCapabilitiesModificationManagerCreator(
                        "http://getDcpUrl", null );
        StaticAttributeModifier attributeModifier = (StaticAttributeModifier) retrieveAttributeModifier( decisionMakerCreator );

        List<AttributeModificationRule> attributeModificationRules = attributeModifier.getAttributeModificationRules();
        assertThat( attributeModificationRules.size(), is( 1 ) );
    }

    @Test
    public void testCreateAttributeModifierWithPostDcpUrl()
                    throws Exception {
        WpsCapabilitiesModificationManagerCreator decisionMakerCreator = new WpsCapabilitiesModificationManagerCreator(
                        null, "http://postDcpUrl" );
        StaticAttributeModifier attributeModifier = (StaticAttributeModifier) retrieveAttributeModifier( decisionMakerCreator );

        List<AttributeModificationRule> attributeModificationRules = attributeModifier.getAttributeModificationRules();
        assertThat( attributeModificationRules.size(), is( 1 ) );
    }

    @Test
    public void testCreateAttributeModifierWithDcpUrls()
                    throws Exception {
        WpsCapabilitiesModificationManagerCreator decisionMakerCreator = new WpsCapabilitiesModificationManagerCreator(
                        "http://getDcpUrl", "http://postDcpUrl" );
        StaticAttributeModifier attributeModifier = (StaticAttributeModifier) retrieveAttributeModifier( decisionMakerCreator );

        List<AttributeModificationRule> attributeModificationRules = attributeModifier.getAttributeModificationRules();
        assertThat( attributeModificationRules.size(), is( 2 ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateXmlModificationManagerForWps130ShouldFail()
                    throws Exception {
        decisionMakerCreator.createXmlModificationManager( createWps130Request(), createAuthenticationWithOneExecute() );
    }

    private Authentication createAuthenticationWithOneExecute() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add( createRasterPermission( EXECUTE, PROCESS_ID_1 ) );
        return mockAuthentication( authorities );
    }

    private Authentication createAuthenticationWithOneUnknownRequest() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add( createRasterPermission( "NotKnownOperation", "NotKnownLayer" ) );
        return mockAuthentication( authorities );
    }

    private Authentication createAuthenticationWithTwoExecuteOneDescribeProcess() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add( createRasterPermission( EXECUTE, PROCESS_ID_1 ) );
        authorities.add( createRasterPermission( EXECUTE, PROCESS_ID_2 ) );
        authorities.add( createRasterPermission( DESCRIBEPROCESS, PROCESS_ID_1 ) );
        authorities.add( createRasterPermission( "NotKnownOperation", "NotKnownLayer" ) );
        return mockAuthentication( authorities );
    }

    private RasterPermission createRasterPermission( String operationType, String layerName ) {
        return new RasterPermission( WPS_SERVICE, operationType, new LimitedOwsServiceVersion( "<= 1.3.0" ), layerName,
                        "serviceName", "internalServiceUrl", null );
    }

    private Authentication mockAuthentication( Collection<? extends GrantedAuthority> authorities ) {
        Authentication authenticationMock = mock( Authentication.class );
        doReturn( authorities ).when( authenticationMock ).getAuthorities();
        return authenticationMock;
    }

    private OwsRequest createWps130Request() {
        return createWpsRequest( "1.3.0" );
    }

    private OwsRequest createWps100Request() {
        return createWpsRequest( "1.0.0" );
    }

    private OwsRequest createWpsRequest( String version ) {
        return new WpsRequest( EXECUTE, new OwsServiceVersion( version ), "serviceName" );
    }

    private DecisionMaker retrieveDecisionMaker( Authentication authentication ) {
        return decisionMakerCreator.createXmlModificationManager( createWps100Request(), authentication ).getDecisionMaker();
    }

    private AttributeModifier
                    retrieveAttributeModifier( WpsCapabilitiesModificationManagerCreator decisionMakerCreator ) {
        return decisionMakerCreator.createXmlModificationManager( createWps100Request(),
                                                                  createAuthenticationWithOneExecute() ).getAttributeModifier();
    }

}

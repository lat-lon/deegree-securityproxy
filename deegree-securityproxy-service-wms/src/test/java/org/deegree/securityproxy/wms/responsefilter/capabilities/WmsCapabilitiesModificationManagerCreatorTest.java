package org.deegree.securityproxy.wms.responsefilter.capabilities;

import org.deegree.securityproxy.authentication.ows.domain.LimitedOwsServiceVersion;
import org.deegree.securityproxy.authentication.ows.raster.RasterPermission;
import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.request.OwsServiceVersion;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.DecisionMaker;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.XmlModificationManager;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.blacklist.BlackListDecisionMaker;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.text.AttributeModifier;
import org.deegree.securityproxy.wms.request.WmsRequest;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.deegree.securityproxy.wms.request.WmsRequestParser.GETFEATUREINFO;
import static org.deegree.securityproxy.wms.request.WmsRequestParser.GETMAP;
import static org.deegree.securityproxy.wms.request.WmsRequestParser.WMS_SERVICE;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class WmsCapabilitiesModificationManagerCreatorTest {

    private static final String LAYER_NAME_1 = "layer1";

    private static final String LAYER_NAME_2 = "layer2";

    private static final String getDcpUrl = "http://getDcpUrl.org";

    private static final String postDcpUrl = "http://postDcpUrl.org";

    private final WmsCapabilitiesModificationManagerCreator decisionMakerCreator = new WmsCapabilitiesModificationManagerCreator();

    @Test
    public void testCreateXmlModificationManagerForWmsOneGetMap()
                    throws Exception {
        XmlModificationManager xmlModificationManager = decisionMakerCreator.createXmlModificationManager( createWms130Request(),
                                                                                                           createAuthenticationWithOneGetMap() );
        DecisionMaker decisionMaker = xmlModificationManager.getDecisionMaker();

        assertThat( decisionMaker, is( notNullValue() ) );
        List<String> blackListLayers = ( (BlackListDecisionMaker) decisionMaker ).getBlackListTextValues();
        assertThat( blackListLayers.size(), is( 1 ) );
        assertThat( blackListLayers, hasItem( LAYER_NAME_1 ) );
    }

    @Test
    public void testCreateXmlModificationManagerForWmsTwoGetMap()
                    throws Exception {
        XmlModificationManager xmlModificationManager = decisionMakerCreator.createXmlModificationManager( createWms130Request(),
                                                                                                           createAuthenticationWithTwoGetMapOneGetFeatureInfo() );
        DecisionMaker decisionMaker = xmlModificationManager.getDecisionMaker();

        assertThat( decisionMaker, is( notNullValue() ) );
        List<String> blackListLayers = ( (BlackListDecisionMaker) decisionMaker ).getBlackListTextValues();
        assertThat( blackListLayers.size(), is( 2 ) );
        assertThat( blackListLayers, hasItem( LAYER_NAME_1 ) );
        assertThat( blackListLayers, hasItem( LAYER_NAME_2 ) );
    }

    @Test
    public void testCreateXmlModificationManagerForWmsNoGetMap()
                    throws Exception {
        XmlModificationManager xmlModificationManager = decisionMakerCreator.createXmlModificationManager( createWms130Request(),
                                                                                                           createAuthenticationWithOneUnknownRequest() );
        DecisionMaker decisionMaker = xmlModificationManager.getDecisionMaker();

        assertThat( decisionMaker, is( notNullValue() ) );
        List<String> blackListLayers = ( (BlackListDecisionMaker) decisionMaker ).getBlackListTextValues();
        assertThat( blackListLayers.size(), is( 0 ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateXmlModificationManagerForWms110ShouldFail()
                    throws Exception {
        decisionMakerCreator.createXmlModificationManager( createWms110Request(), createAuthenticationWithOneGetMap() );
    }

    @Test
    public void testCreateXmlModificationManagerWithDcpUrls() {
        WmsCapabilitiesModificationManagerCreator modificationManagerCreator = new WmsCapabilitiesModificationManagerCreator(
                        getDcpUrl, postDcpUrl );
        XmlModificationManager xmlModificationManager = modificationManagerCreator.createXmlModificationManager( createWms130Request(),
                                                                                                                 mock( Authentication.class ) );
        AttributeModifier attributeModifier = xmlModificationManager.getAttributeModifier();

        assertThat( attributeModifier, notNullValue() );
    }

    @Test
    public void testCreateXmlModificationManagerWithoutDcpUrls() {
        WmsCapabilitiesModificationManagerCreator modificationManagerCreator = new WmsCapabilitiesModificationManagerCreator(
                        null, null );
        XmlModificationManager xmlModificationManager = modificationManagerCreator.createXmlModificationManager( createWms130Request(),
                                                                                                                 mock( Authentication.class ) );
        AttributeModifier attributeModifier = xmlModificationManager.getAttributeModifier();

        assertThat( attributeModifier, nullValue() );
    }

    @Test
    public void testCreateXmlModificationManagerWithGetDcpUrl() {
        WmsCapabilitiesModificationManagerCreator modificationManagerCreator = new WmsCapabilitiesModificationManagerCreator(
                        getDcpUrl, null );
        XmlModificationManager xmlModificationManager = modificationManagerCreator.createXmlModificationManager( createWms130Request(),
                                                                                                                 mock( Authentication.class ) );
        AttributeModifier attributeModifier = xmlModificationManager.getAttributeModifier();

        assertThat( attributeModifier, notNullValue() );
    }

    @Test
    public void testCreateXmlModificationManagerWithPostDcpUrl() {
        WmsCapabilitiesModificationManagerCreator modificationManagerCreator = new WmsCapabilitiesModificationManagerCreator(
                        null, postDcpUrl );
        XmlModificationManager xmlModificationManager = modificationManagerCreator.createXmlModificationManager( createWms130Request(),
                                                                                                                 mock( Authentication.class ) );
        AttributeModifier attributeModifier = xmlModificationManager.getAttributeModifier();

        assertThat( attributeModifier, notNullValue() );
    }

    private Authentication createAuthenticationWithOneGetMap() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add( createRasterPermission( GETMAP, LAYER_NAME_1 ) );
        return mockAuthentication( authorities );
    }

    private Authentication createAuthenticationWithOneUnknownRequest() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add( createRasterPermission( "NotKnownOperation", "NotKnownLayer" ) );
        return mockAuthentication( authorities );
    }

    private Authentication createAuthenticationWithTwoGetMapOneGetFeatureInfo() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add( createRasterPermission( GETMAP, LAYER_NAME_1 ) );
        authorities.add( createRasterPermission( GETMAP, LAYER_NAME_2 ) );
        authorities.add( createRasterPermission( GETFEATUREINFO, LAYER_NAME_1 ) );
        authorities.add( createRasterPermission( "NotKnownOperation", "NotKnownLayer" ) );
        return mockAuthentication( authorities );
    }

    private RasterPermission createRasterPermission( String operationType, String layerName ) {
        return new RasterPermission( WMS_SERVICE, operationType, new LimitedOwsServiceVersion( "<= 1.3.0" ), layerName,
                        "serviceName", "internalServiceUrl", null );
    }

    private Authentication mockAuthentication( Collection<? extends GrantedAuthority> authorities ) {
        Authentication authenticationMock = mock( Authentication.class );
        doReturn( authorities ).when( authenticationMock ).getAuthorities();
        return authenticationMock;
    }

    private OwsRequest createWms130Request() {
        return createWmsRequest( "1.3.0" );
    }

    private OwsRequest createWms110Request() {
        return createWmsRequest( "1.1.0" );
    }

    private OwsRequest createWmsRequest( String version ) {
        return new WmsRequest( GETMAP, new OwsServiceVersion( version ), "serviceName" );
    }

}
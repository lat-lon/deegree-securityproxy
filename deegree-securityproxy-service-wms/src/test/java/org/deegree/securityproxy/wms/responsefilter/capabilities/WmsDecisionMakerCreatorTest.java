package org.deegree.securityproxy.wms.responsefilter.capabilities;

import static org.deegree.securityproxy.wms.request.WmsRequestParser.GETFEATUREINFO;
import static org.deegree.securityproxy.wms.request.WmsRequestParser.GETMAP;
import static org.deegree.securityproxy.wms.request.WmsRequestParser.WMS_SERVICE;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
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
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.DecisionMakerCreator;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.blacklist.BlackListDecisionMaker;
import org.deegree.securityproxy.wms.request.WmsRequest;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class WmsDecisionMakerCreatorTest {

    private static final String LAYER_NAME_1 = "layer1";

    private static final String LAYER_NAME_2 = "layer2";

    private final DecisionMakerCreator decisionMakerCreator = new WmsDecisionMakerCreator();

    @Test
    public void testCreateDecisionMakerForWmsOneGetMap()
                            throws Exception {
        DecisionMaker decisionMaker = decisionMakerCreator.createDecisionMaker( createWms130Request(),
                                                                                createAuthenticationWithOneGetMap() );

        assertThat( decisionMaker, is( notNullValue() ) );
        List<String> blackListLayers = ( (BlackListDecisionMaker) decisionMaker ).getBlackListTextValues();
        assertThat( blackListLayers.size(), is( 1 ) );
        assertThat( blackListLayers, hasItem( LAYER_NAME_1 ) );
    }

    @Test
    public void testCreateDecisionMakerForWmsTwoGetMap()
                            throws Exception {
        DecisionMaker decisionMaker = decisionMakerCreator.createDecisionMaker( createWms130Request(),
                                                                                createAuthenticationWithTwoGetMapOneGetFeatureInfo() );

        assertThat( decisionMaker, is( notNullValue() ) );
        List<String> blackListLayers = ( (BlackListDecisionMaker) decisionMaker ).getBlackListTextValues();
        assertThat( blackListLayers.size(), is( 2 ) );
        assertThat( blackListLayers, hasItem( LAYER_NAME_1 ) );
        assertThat( blackListLayers, hasItem( LAYER_NAME_2 ) );
    }

    @Test
    public void testCreateDecisionMakerForWmsNoGetMap()
                            throws Exception {
        DecisionMaker decisionMaker = decisionMakerCreator.createDecisionMaker( createWms130Request(),
                                                                                createAuthenticationWithOneUnknownRequest() );

        assertThat( decisionMaker, is( notNullValue() ) );
        List<String> blackListLayers = ( (BlackListDecisionMaker) decisionMaker ).getBlackListTextValues();
        assertThat( blackListLayers.size(), is( 0 ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateDecisionMakerForWms110ShouldFail()
                            throws Exception {
        decisionMakerCreator.createDecisionMaker( createWms110Request(), createAuthenticationWithOneGetMap() );
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
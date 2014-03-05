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
package org.deegree.securityproxy.wms.authorization;

import static org.deegree.securityproxy.wms.authorization.WmsRequestAuthorizationManager.AUTHORIZED;
import static org.deegree.securityproxy.wms.request.WmsRequestParser.GETCAPABILITIES;
import static org.deegree.securityproxy.wms.request.WmsRequestParser.GETFEATUREINFO;
import static org.deegree.securityproxy.wms.request.WmsRequestParser.GETMAP;
import static org.deegree.securityproxy.wms.request.WmsRequestParser.VERSION_130;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.deegree.securityproxy.authentication.ows.domain.LimitedOwsServiceVersion;
import org.deegree.securityproxy.authentication.ows.raster.RasterPermission;
import org.deegree.securityproxy.authorization.RequestAuthorizationManager;
import org.deegree.securityproxy.authorization.logging.AuthorizationReport;
import org.deegree.securityproxy.request.OwsServiceVersion;
import org.deegree.securityproxy.wms.request.WmsRequest;
import org.junit.Test;
import org.springframework.security.core.Authentication;

/**
 * Tests for {@link WmsRequestAuthorizationManager}.
 * 
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * @version $Revision: $, $Date: $
 */
public class WmsRequestAuthorizationManagerTest {

    private static final boolean NOT_AUTHORIZED = false;

    private static final LimitedOwsServiceVersion VERSION_LESS_EQUAL_130 = new LimitedOwsServiceVersion( "<= 1.3.0" );

    private static final String WMS_TYPE = "wms";

    private static final String WCS_TYPE = "wcs";

    private static final String SERVICE_NAME = "serviceName";

    private static final String LAYER_NAME = "layerName";

    private static final String LAYER_NAME_2 = "layerName2";

    private static final String INTERNAL_SERVICE_URL = "serviceUrl";

    private final Map<String, String[]> ADDITIONAL_KEY_VALUE_PAIRS = createAdditionalKeyValuePairs();

    private final RequestAuthorizationManager authorizationManager = new WmsRequestAuthorizationManager();

    @Test
    public void testSupportsWcsRequestShouldBeSupported()
                            throws Exception {
        boolean isSupported = authorizationManager.supports( WmsRequest.class );
        assertThat( isSupported, is( true ) );
    }

    @Test
    public void testSupportsHttpServletRequestShouldBeUnsupported()
                            throws Exception {
        boolean isSupported = authorizationManager.supports( HttpServletRequest.class );
        assertThat( isSupported, is( false ) );
    }

    @Test
    public void testDecideWithSingleAuthorization()
                            throws Exception {
        Authentication authentication = mockGetMapAuthentication();
        WmsRequest request = mockGetMapRequest();
        AuthorizationReport report = authorizationManager.decide( authentication, request );
        assertThat( report.isAuthorized(), is( AUTHORIZED ) );
    }

    @Test
    public void testDecideWithMultipleAuthorizations()
                            throws Exception {
        Authentication authentication = mockDefaultAuthenticationWithMultiplePermissions();
        WmsRequest request = mockGetCapabilitiesRequest();
        AuthorizationReport report = authorizationManager.decide( authentication, request );
        assertThat( report.isAuthorized(), is( AUTHORIZED ) );
    }

    @Test
    public void testDecideGetMapWithNonWmsAuthorizations()
                            throws Exception {
        Authentication authentication = mockAllAuthenticationWithNonWmsPermissions();
        WmsRequest request = mockGetMapRequest();
        AuthorizationReport report = authorizationManager.decide( authentication, request );
        assertThat( report.isAuthorized(), is( NOT_AUTHORIZED ) );
    }

    @Test
    public void testDecideGetMapWithOneAuthorizedAndOneUnauthorizedLayer()
          throws Exception {
        Authentication authentication = mockGetMapAuthentication();
        WmsRequest request = mockGetMapRequestWithOneAuthorizedAndOneUnauthorizedLayer();
        AuthorizationReport report = authorizationManager.decide( authentication, request );
        assertThat( report.isAuthorized(), is( NOT_AUTHORIZED ) );
    }

    @Test
    public void testDecideGetCapabilitiesWithNonWmsAuthorizations()
                            throws Exception {
        Authentication authentication = mockAllAuthenticationWithNonWmsPermissions();
        WmsRequest request = mockGetCapabilitiesRequest();
        AuthorizationReport report = authorizationManager.decide( authentication, request );
        assertThat( report.isAuthorized(), is( NOT_AUTHORIZED ) );
    }

    @Test
    public void testDecideGetFeatureInfoRequest()
          throws Exception {
        Authentication authentication = mockDefaultAuthenticationWithMultiplePermissions();
        WmsRequest request = mockGetFeatureInfoRequest();
        AuthorizationReport report = authorizationManager.decide( authentication, request );
        assertThat( report.isAuthorized(), is( AUTHORIZED ) );
    }

    @Test
    public void testDecideGetFeatureInfoWithUnauthorizedQueryLayers()
          throws Exception {
        Authentication authentication = mockDefaultAuthenticationWithMultiplePermissions();
        WmsRequest request = mockGetFeatureInfoRequestWithUnauthorizedQueryLayers();
        AuthorizationReport report = authorizationManager.decide( authentication, request );
        assertThat( report.isAuthorized(), is( NOT_AUTHORIZED ) );
    }

    @Test
    public void testDecideGetFeatureInfoWithUnauthorizedLayers()
          throws Exception {
        Authentication authentication = mockDefaultAuthenticationWithMultiplePermissions();
        WmsRequest request = mockGetFeatureInfoRequestWithUnauthorizedLayers();
        AuthorizationReport report = authorizationManager.decide( authentication, request );
        assertThat( report.isAuthorized(), is( NOT_AUTHORIZED ) );
    }

    @Test
    public void testDecideGetFeatureInfoWithNonWmsAuthorizations()
                            throws Exception {
        Authentication authentication = mockAllAuthenticationWithNonWmsPermissions();
        WmsRequest request = mockGetFeatureInfoRequest();
        AuthorizationReport report = authorizationManager.decide( authentication, request );
        assertThat( report.isAuthorized(), is( NOT_AUTHORIZED ) );
    }

    @Test
    public void testDecideMultipleAuthorizationsShouldBeRefusedCauseOfVersion()
                            throws Exception {
        Authentication authentication = mockDefaultAuthenticationWithMultiplePermissions();
        WmsRequest request = mockGetCapabilitiesRequestWithUnsupportedVersion();
        AuthorizationReport report = authorizationManager.decide( authentication, request );
        assertThat( report.isAuthorized(), is( NOT_AUTHORIZED ) );
        assertThat( report.getMessage(), is( WmsRequestAuthorizationManager.GETCAPABILITIES_UNAUTHORIZED_MSG ) );
    }

    @Test
    public void testDecideSingleAuthorizationShouldBeRefusedCauseOfVersion()
                            throws Exception {
        Authentication authentication = mockGetMapAuthentication();
        WmsRequest request = mockRequestWithUnsupportedVersion();
        AuthorizationReport report = authorizationManager.decide( authentication, request );
        assertThat( report.isAuthorized(), is( NOT_AUTHORIZED ) );
        assertThat( report.getMessage(), is( WmsRequestAuthorizationManager.GETMAP_UNAUTHORIZED_MSG ) );

    }

    @Test
    public void testDecideSingleAuthorizationShouldBeRefusedCauseOfOperationType()
                            throws Exception {
        Authentication authentication = mockGetMapAuthentication();
        WmsRequest request = mockRequestWithUnsupportedOperationType();
        AuthorizationReport report = authorizationManager.decide( authentication, request );
        assertThat( report.isAuthorized(), is( NOT_AUTHORIZED ) );
    }

    @Test
    public void testDecideSingleAuthorizationShouldBeRefusedBecauseOfCovName()
                            throws Exception {
        Authentication authentication = mockGetMapAuthentication();
        WmsRequest request = mockRequestWithUnsupportedLayerName();
        AuthorizationReport report = authorizationManager.decide( authentication, request );
        assertThat( report.isAuthorized(), is( NOT_AUTHORIZED ) );
        assertThat( report.getMessage(), is( WmsRequestAuthorizationManager.GETMAP_UNAUTHORIZED_MSG ) );
    }

    @Test
    public void testDecideSingleAuthorizationShouldBeRefusedBecauseOfServiceName()
                            throws Exception {
        Authentication authentication = mockGetMapAuthentication();
        WmsRequest request = mockRequestWithUnsupportedServiceName();
        AuthorizationReport report = authorizationManager.decide( authentication, request );
        assertThat( report.isAuthorized(), is( NOT_AUTHORIZED ) );
        assertThat( report.getMessage(), is( WmsRequestAuthorizationManager.GETMAP_UNAUTHORIZED_MSG ) );
    }

    @Test
    public void testDecideSingleAuthorizationShouldIdentifyAdditionalKeyValuePairs()
                            throws Exception {
        Authentication authentication = mockGetMapAuthentication();
        WmsRequest request = mockGetMapRequest();
        AuthorizationReport report = authorizationManager.decide( authentication, request );

        String expectedAdditionalKey = "additionalKey";
        String[] expectedAdditionalValue = { "additionalValue" };
        Set<String> actualKeySet = report.getAdditionalKeyValuePairs().keySet();
        String[] actualValue = report.getAdditionalKeyValuePairs().get( expectedAdditionalKey );

        assertThat( actualKeySet, hasItem( expectedAdditionalKey ) );
        assertThat( actualValue, is( expectedAdditionalValue ) );
    }

    private WmsRequest mockGetMapRequest() {
        List<String> layerNames = createListWithLayerNames();
        return mockRequest( layerNames, GETMAP, SERVICE_NAME, VERSION_130, null );
    }

    private WmsRequest mockGetMapRequestWithOneAuthorizedAndOneUnauthorizedLayer() {
        List<String> layerNames = createListWithLayerNamesWithOneAuthorizedAndOneUnauthorizedLayer();
        return mockRequest( layerNames, GETMAP, SERVICE_NAME, VERSION_130, null );
    }

    private WmsRequest mockGetCapabilitiesRequest() {
        return mockRequest( null, GETCAPABILITIES, SERVICE_NAME, VERSION_130, null );
    }

    private WmsRequest mockGetFeatureInfoRequest() {
        List<String> layerNames = createListWithLayerNames();
        return mockRequest( layerNames, GETFEATUREINFO, SERVICE_NAME, VERSION_130, layerNames );
    }

    private WmsRequest mockGetFeatureInfoRequestWithUnauthorizedLayers() {
        List<String> layerNames = createListWithLayerNames();
        List<String> unauthorizedLayerName = createListWithUnauthorizedLayerName();
        return mockRequest( unauthorizedLayerName, GETFEATUREINFO, SERVICE_NAME, VERSION_130, layerNames );
    }

    private WmsRequest mockGetFeatureInfoRequestWithUnauthorizedQueryLayers() {
        List<String> layerNames = createListWithLayerNames();
        List<String> unauthorizedLayerName = createListWithUnauthorizedLayerName();
        return mockRequest( layerNames, GETFEATUREINFO, SERVICE_NAME, VERSION_130, unauthorizedLayerName );
    }

    private WmsRequest mockGetCapabilitiesRequestWithUnsupportedVersion() {
        return mockRequest( null, GETCAPABILITIES, SERVICE_NAME, new OwsServiceVersion( 2, 0, 0 ), null );
    }

    private WmsRequest mockRequestWithUnsupportedVersion() {
        List<String> layerNames = createListWithLayerNames();
        return mockRequest( layerNames, GETMAP, SERVICE_NAME, new OwsServiceVersion( 2, 0, 0 ), null );
    }

    private WmsRequest mockRequestWithUnsupportedOperationType() {
        List<String> layerNames = createListWithLayerNames();
        return mockRequest( layerNames, GETFEATUREINFO, SERVICE_NAME, VERSION_130, layerNames );
    }

    private WmsRequest mockRequestWithUnsupportedLayerName() {
        List<String> unauthorizedLayerName = createListWithUnauthorizedLayerName();
        return mockRequest( unauthorizedLayerName, GETMAP, SERVICE_NAME, VERSION_130, null );
    }

    private WmsRequest mockRequestWithUnsupportedServiceName() {
        List<String> layerNames = createListWithLayerNames();
        return mockRequest( layerNames, GETMAP, "unknown", VERSION_130, null );
    }

    private WmsRequest mockRequest( List<String> layerNames, String operationType, String serviceName,
                                    OwsServiceVersion version, List<String> queryLayerNames ) {
        WmsRequest mock = mock( WmsRequest.class );
        when( mock.getLayerNames() ).thenReturn( layerNames );
        when( mock.getOperationType() ).thenReturn( operationType );
        when( mock.getServiceVersion() ).thenReturn( version );
        when( mock.getServiceName() ).thenReturn( serviceName );
        when( mock.getServiceType() ).thenReturn( WMS_TYPE );
        when( mock.getQueryLayerNames() ).thenReturn( queryLayerNames );
        return mock;
    }

    private Authentication mockGetMapAuthentication() {
        Authentication authentication = mock( Authentication.class );
        Collection<RasterPermission> authorities = new ArrayList<RasterPermission>();
        authorities.add( new RasterPermission( WMS_TYPE, GETMAP, VERSION_LESS_EQUAL_130, LAYER_NAME, SERVICE_NAME,
                                               INTERNAL_SERVICE_URL, ADDITIONAL_KEY_VALUE_PAIRS ) );
        authorities.add( new RasterPermission( WMS_TYPE, GETMAP, VERSION_LESS_EQUAL_130, LAYER_NAME_2, SERVICE_NAME,
                                               INTERNAL_SERVICE_URL, ADDITIONAL_KEY_VALUE_PAIRS ) );
        doReturn( authorities ).when( authentication ).getAuthorities();
        return authentication;
    }

    private Authentication mockDefaultAuthenticationWithMultiplePermissions() {
        Authentication authentication = mock( Authentication.class );
        Collection<RasterPermission> authorities = new ArrayList<RasterPermission>();
        authorities.add( new RasterPermission( WMS_TYPE, GETMAP, VERSION_LESS_EQUAL_130, LAYER_NAME, SERVICE_NAME,
                                               INTERNAL_SERVICE_URL, ADDITIONAL_KEY_VALUE_PAIRS ) );
        authorities.add( new RasterPermission( WMS_TYPE, GETMAP, VERSION_LESS_EQUAL_130, LAYER_NAME_2, SERVICE_NAME,
                                               INTERNAL_SERVICE_URL, ADDITIONAL_KEY_VALUE_PAIRS ) );
        authorities.add( new RasterPermission( WMS_TYPE, GETCAPABILITIES, VERSION_LESS_EQUAL_130, null, SERVICE_NAME,
                                               INTERNAL_SERVICE_URL, ADDITIONAL_KEY_VALUE_PAIRS ) );
        authorities
              .add( new RasterPermission( WMS_TYPE, GETFEATUREINFO, VERSION_LESS_EQUAL_130, LAYER_NAME, SERVICE_NAME,
                                          INTERNAL_SERVICE_URL, ADDITIONAL_KEY_VALUE_PAIRS ) );
        authorities
              .add( new RasterPermission( WMS_TYPE, GETFEATUREINFO, VERSION_LESS_EQUAL_130, LAYER_NAME_2, SERVICE_NAME,
                                          INTERNAL_SERVICE_URL, ADDITIONAL_KEY_VALUE_PAIRS ) );
        doReturn( authorities ).when( authentication ).getAuthorities();
        return authentication;
    }

    private Authentication mockAllAuthenticationWithNonWmsPermissions() {
        Authentication authentication = mock( Authentication.class );
        Collection<RasterPermission> authorities = new ArrayList<RasterPermission>();
        authorities.add( new RasterPermission( WCS_TYPE, GETMAP, VERSION_LESS_EQUAL_130, LAYER_NAME, SERVICE_NAME,
                                               INTERNAL_SERVICE_URL, ADDITIONAL_KEY_VALUE_PAIRS ) );
        authorities.add( new RasterPermission( WCS_TYPE, GETCAPABILITIES, VERSION_LESS_EQUAL_130, null, SERVICE_NAME,
                                               INTERNAL_SERVICE_URL, ADDITIONAL_KEY_VALUE_PAIRS ) );
        authorities.add( new RasterPermission( WCS_TYPE, GETFEATUREINFO, VERSION_LESS_EQUAL_130, null, SERVICE_NAME,
                                               INTERNAL_SERVICE_URL, ADDITIONAL_KEY_VALUE_PAIRS ) );
        doReturn( authorities ).when( authentication ).getAuthorities();
        return authentication;
    }

    private Map<String, String[]> createAdditionalKeyValuePairs() {
        Map<String, String[]> additionalKeyValuePairs = new HashMap<String, String[]>();
        additionalKeyValuePairs.put( "additionalKey", new String[] { "additionalValue" } );
        return additionalKeyValuePairs;
    }

    private List<String> createListWithLayerNames() {
        List<String> layerNames = new ArrayList<String>();
        layerNames.add( LAYER_NAME );
        layerNames.add( LAYER_NAME_2 );
        return layerNames;
    }

    private List<String> createListWithLayerNamesWithOneAuthorizedAndOneUnauthorizedLayer() {
        List<String> layerNames = new ArrayList<String>();
        layerNames.add( LAYER_NAME );
        layerNames.add( "unauthorized-layer" );
        return layerNames;
    }

    private List<String> createListWithUnauthorizedLayerName() {
        List<String> unauthorizedLayerName = new ArrayList<String>();
        unauthorizedLayerName.add( "unauthorized-layer" );
        return unauthorizedLayerName;
    }

}
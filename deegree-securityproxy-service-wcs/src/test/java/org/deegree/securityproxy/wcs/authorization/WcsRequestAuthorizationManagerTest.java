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
package org.deegree.securityproxy.wcs.authorization;

import org.deegree.securityproxy.authentication.ows.WcsPermission;
import org.deegree.securityproxy.authentication.ows.domain.WcsOperationType;
import org.deegree.securityproxy.authentication.ows.domain.WcsServiceVersion;
import org.deegree.securityproxy.authorization.RequestAuthorizationManager;
import org.deegree.securityproxy.authorization.logging.AuthorizationReport;
import org.deegree.securityproxy.wcs.request.WcsRequest;
import org.junit.Test;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

import java.lang.reflect.Array;
import java.util.*;

import static org.deegree.securityproxy.authentication.ows.domain.WcsOperationType.*;
import static org.deegree.securityproxy.authentication.ows.domain.WcsServiceVersion.VERSION_100;
import static org.deegree.securityproxy.authentication.ows.domain.WcsServiceVersion.VERSION_200;
import static org.deegree.securityproxy.wcs.authorization.WcsRequestAuthorizationManager.AUTHORIZED;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * @version $Revision: $, $Date: $
 */
public class WcsRequestAuthorizationManagerTest {

    private static final boolean NOT_AUTHORIZED = false;

    private static final WcsServiceVersion VERSION = VERSION_100;

    private static final WcsOperationType OPERATION_TYPE = GETCOVERAGE;

    private static final String SERVICE_NAME = "serviceName";

    private static final String COVERAGE_NAME = "layerName";

    private static final String INTERNAL_SERVICE_URL = "serviceUrl";

    private final Map<String, String[]> ADDITIONAL_KEY_VALUE_PAIRS = createAdditionalKeyValuePairs();

    private final RequestAuthorizationManager authorizationManager = new WcsRequestAuthorizationManager();

    @Test
    public void testSupportsWcsRequestShouldBeSupported()
          throws Exception {
        boolean isSupported = authorizationManager.supports( WcsRequest.class );
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
        Authentication authentication = mockDefaultAuthentication();
        WcsRequest request = mockDefaultRequest();
        AuthorizationReport report = authorizationManager.decide( authentication, request );
        assertThat( report.isAuthorized(), is( AUTHORIZED ) );
    }

    @Test
    public void testDecideWithMultipleAuthorizations()
          throws Exception {
        Authentication authentication = mockDefaultAuthenticationWithMultiplePermissions();
        WcsRequest request = mockGetCapabilitiesRequest();
        AuthorizationReport report = authorizationManager.decide( authentication, request );
        assertThat( report.isAuthorized(), is( AUTHORIZED ) );
    }

    @Test
    public void testDecideMultipleAuthorizationsShouldBeRefusedCauseOfVersion()
          throws Exception {
        Authentication authentication = mockDefaultAuthenticationWithMultiplePermissions();
        WcsRequest request = mockGetCapabilitiesRequestWithUnsupportedVersion();
        AuthorizationReport report = authorizationManager.decide( authentication, request );
        assertThat( report.isAuthorized(), is( NOT_AUTHORIZED ) );
        assertThat( report.getMessage(), is( WcsRequestAuthorizationManager.GETCAPABILITIES_UNAUTHORIZED_MSG ) );
    }

    @Test
    public void testDecideSingleAuthorizationShouldBeRefusedCauseOfVersion()
          throws Exception {
        Authentication authentication = mockDefaultAuthentication();
        WcsRequest request = mockRequestWithUnsupportedVersion();
        AuthorizationReport report = authorizationManager.decide( authentication, request );
        assertThat( report.isAuthorized(), is( NOT_AUTHORIZED ) );
        assertThat( report.getMessage(), is( WcsRequestAuthorizationManager.GETCOVERAGE_UNAUTHORIZED_MSG ) );

    }

    @Test
    public void testDecideSingleAuthorizationShouldBeRefusedCauseOfOperationType()
          throws Exception {
        Authentication authentication = mockDefaultAuthentication();
        WcsRequest request = mockRequestWithUnsupportedOperationType();
        AuthorizationReport report = authorizationManager.decide( authentication, request );
        assertThat( report.isAuthorized(), is( NOT_AUTHORIZED ) );
    }

    @Test
    public void testDecideSingleAuthorizationShouldBeRefusedBecauseOfCovName()
          throws Exception {
        Authentication authentication = mockDefaultAuthentication();
        WcsRequest request = mockRequestWithUnsupportedLayerName();
        AuthorizationReport report = authorizationManager.decide( authentication, request );
        assertThat( report.isAuthorized(), is( NOT_AUTHORIZED ) );
        assertThat( report.getMessage(), is( WcsRequestAuthorizationManager.GETCOVERAGE_UNAUTHORIZED_MSG ) );
    }

    @Test
    public void testDecideSingleAuthorizationShouldBeRefusedBecauseOfServiceName()
          throws Exception {
        Authentication authentication = mockDefaultAuthentication();
        WcsRequest request = mockRequestWithUnsupportedServiceName();
        AuthorizationReport report = authorizationManager.decide( authentication, request );
        assertThat( report.isAuthorized(), is( NOT_AUTHORIZED ) );
        assertThat( report.getMessage(), is( WcsRequestAuthorizationManager.GETCOVERAGE_UNAUTHORIZED_MSG ) );
    }

    @Test
    public void testDecideSingleAuthorizationShouldIdentifyAdditionalKeyValuePairs()
          throws Exception {
        Authentication authentication = mockDefaultAuthentication();
        WcsRequest request = mockDefaultRequest();
        AuthorizationReport report = authorizationManager.decide( authentication, request );

        String expectedAdditionalKey = "additionalKey";
        String[] expectedAdditionalValue = { "additionalValue" };
        Set<String> actualKeySet = report.getAdditionalKeyValuePairs().keySet();
        String[] actualValue = report.getAdditionalKeyValuePairs().get( expectedAdditionalKey );

        assertTrue( actualKeySet.contains( expectedAdditionalKey ) );
        assertTrue( Arrays.equals( actualValue, expectedAdditionalValue ) );
    }

    private WcsRequest mockDefaultRequest() {
        return mockRequest( COVERAGE_NAME, OPERATION_TYPE, SERVICE_NAME, VERSION );
    }

    private WcsRequest mockGetCapabilitiesRequest() {
        return mockRequest( null, GETCAPABILITIES, SERVICE_NAME, VERSION );
    }

    private WcsRequest mockGetCapabilitiesRequestWithUnsupportedVersion() {
        return mockRequest( null, GETCAPABILITIES, SERVICE_NAME, VERSION_200 );
    }

    private WcsRequest mockRequestWithUnsupportedVersion() {
        return mockRequest( COVERAGE_NAME, OPERATION_TYPE, SERVICE_NAME, VERSION_200 );
    }

    private WcsRequest mockRequestWithUnsupportedOperationType() {
        return mockRequest( COVERAGE_NAME, DESCRIBECOVERAGE, SERVICE_NAME, VERSION );
    }

    private WcsRequest mockRequestWithUnsupportedLayerName() {
        return mockRequest( "unknown", OPERATION_TYPE, SERVICE_NAME, VERSION );
    }

    private WcsRequest mockRequestWithUnsupportedServiceName() {
        return mockRequest( COVERAGE_NAME, OPERATION_TYPE, "unknown", VERSION );
    }

    private WcsRequest mockRequest( String layerName, WcsOperationType operationType, String serviceName,
                                    WcsServiceVersion version ) {
        WcsRequest mock = mock( WcsRequest.class );
        when( mock.getCoverageNames() ).thenReturn( Collections.singletonList( layerName ) );
        when( mock.getOperationType() ).thenReturn( operationType );
        when( mock.getServiceVersion() ).thenReturn( version );
        when( mock.getServiceName() ).thenReturn( serviceName );
        return mock;
    }

    private Authentication mockDefaultAuthentication() {
        Authentication authentication = mock( Authentication.class );
        Collection<WcsPermission> authorities = new ArrayList<WcsPermission>();
        authorities.add( new WcsPermission( OPERATION_TYPE, VERSION, COVERAGE_NAME, SERVICE_NAME, INTERNAL_SERVICE_URL,
                                            ADDITIONAL_KEY_VALUE_PAIRS ) );
        doReturn( authorities ).when( authentication ).getAuthorities();
        return authentication;
    }

    private Authentication mockDefaultAuthenticationWithMultiplePermissions() {
        Authentication authentication = mock( Authentication.class );
        Collection<WcsPermission> authorities = new ArrayList<WcsPermission>();
        authorities.add( new WcsPermission( OPERATION_TYPE, VERSION, COVERAGE_NAME, SERVICE_NAME, INTERNAL_SERVICE_URL,
                                            ADDITIONAL_KEY_VALUE_PAIRS ) );
        authorities.add( new WcsPermission( GETCAPABILITIES, VERSION, null, SERVICE_NAME, INTERNAL_SERVICE_URL,
                                            ADDITIONAL_KEY_VALUE_PAIRS ) );
        doReturn( authorities ).when( authentication ).getAuthorities();
        return authentication;
    }

    private Map<String, String[]> createAdditionalKeyValuePairs() {
        Map<String, String[]> additionalKeyValuePairs = new HashMap<String, String[]>();
        additionalKeyValuePairs.put( "additionalKey", new String[] { "additionalValue" } );
        return additionalKeyValuePairs;
    }
}
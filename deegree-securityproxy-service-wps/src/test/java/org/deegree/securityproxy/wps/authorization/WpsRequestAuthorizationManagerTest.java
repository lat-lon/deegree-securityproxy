package org.deegree.securityproxy.wps.authorization;

import static org.deegree.securityproxy.wps.request.WpsRequestParser.DESCRIBEPROCESS;
import static org.deegree.securityproxy.wps.request.WpsRequestParser.EXECUTE;
import static org.deegree.securityproxy.wps.request.WpsRequestParser.GETCAPABILITIES;
import static org.deegree.securityproxy.wps.request.WpsRequestParser.VERSION_100;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.deegree.securityproxy.authentication.ows.domain.LimitedOwsServiceVersion;
import org.deegree.securityproxy.authentication.ows.raster.RasterPermission;
import org.deegree.securityproxy.authorization.RequestAuthorizationManager;
import org.deegree.securityproxy.authorization.logging.AuthorizationReport;
import org.deegree.securityproxy.request.OwsServiceVersion;
import org.deegree.securityproxy.wps.request.WpsRequest;
import org.junit.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class WpsRequestAuthorizationManagerTest {

    private static final String SERVICE_TYPE = "wps";

    private static final OwsServiceVersion VERSION_200 = new OwsServiceVersion( 2, 0, 0 );

    private static final LimitedOwsServiceVersion VERSION_LESS_EQUAL_100 = new LimitedOwsServiceVersion( "<= 1.0.0" );

    private static final String SERVICE_NAME = "serviceName";

    private static final String PROCESS_ID = "processId";

    private final RequestAuthorizationManager authorizationManager = new WpsRequestAuthorizationManager();

    @Test
    public void testSupportsWpsRequestShouldBeSupported()
                    throws Exception {
        boolean isSupported = authorizationManager.supports( WpsRequest.class );
        assertThat( isSupported, is( true ) );
    }

    @Test
    public void testSupportsHttpServletRequestShouldBeUnsupported()
                    throws Exception {
        boolean isSupported = authorizationManager.supports( HttpServletRequest.class );
        assertThat( isSupported, is( false ) );
    }

    @Test
    public void testDecideAnonymous()
                    throws Exception {
        Authentication authentication = mock( AnonymousAuthenticationToken.class );
        WpsRequest request = mockGetCapabilitiesRequest();
        AuthorizationReport report = authorizationManager.decide( authentication, request );
        assertThat( report.isAuthorized(), is( false ) );
    }

    @Test
    public void testDecideValidGetCapabilities()
                    throws Exception {
        Authentication authentication = mockDefaultAuthenticationWithAllPermissions();
        WpsRequest request = mockGetCapabilitiesRequest();
        AuthorizationReport report = authorizationManager.decide( authentication, request );
        assertThat( report.isAuthorized(), is( true ) );
    }

    @Test
    public void testDecideValidGetCapabilitiesWithUnsupportedVersion()
                    throws Exception {
        Authentication authentication = mockDefaultAuthenticationWithAllPermissions();
        WpsRequest request = mockGetCapabilitiesRequestWithUnsupportedVersion();
        AuthorizationReport report = authorizationManager.decide( authentication, request );
        assertThat( report.isAuthorized(), is( false ) );
    }

    @Test
    public void testDecideValidExecute()
                    throws Exception {
        Authentication authentication = mockDefaultAuthenticationWithAllPermissions();
        WpsRequest request = mockExecuteRequest();
        AuthorizationReport report = authorizationManager.decide( authentication, request );
        assertThat( report.isAuthorized(), is( true ) );
    }

    @Test
    public void testDecideValidExecuteNotGrantedUser()
                    throws Exception {
        Authentication authentication = mockDefaultAuthenticationWithCapabilitiesPermissions();
        WpsRequest request = mockExecuteRequest();
        AuthorizationReport report = authorizationManager.decide( authentication, request );
        assertThat( report.isAuthorized(), is( false ) );
    }

    @Test
    public void testDecideValidExecuteInvalidProcessIdShouldBeUnauthorized()
                    throws Exception {
        Authentication authentication = mockDefaultAuthenticationWithAllPermissionsButInvalidProcessId();
        WpsRequest request = mockExecuteRequest();
        AuthorizationReport report = authorizationManager.decide( authentication, request );
        assertThat( report.isAuthorized(), is( false ) );
    }

    @Test
    public void testDecideValidDescribeProcessProcessIdInvalidExecuteAndValidDescribeProcess()
                    throws Exception {
        Authentication authentication = mockDefaultAuthentication( DESCRIBEPROCESS, EXECUTE );
        WpsRequest request = mockExecuteRequest();
        AuthorizationReport report = authorizationManager.decide( authentication, request );
        assertThat( report.isAuthorized(), is( false ) );
    }

    @Test
    public void testDecideValidDescribeProcess()
                    throws Exception {
        Authentication authentication = mockDefaultAuthenticationWithAllPermissions();
        WpsRequest request = mockDescribeProcessRequest();
        AuthorizationReport report = authorizationManager.decide( authentication, request );
        assertThat( report.isAuthorized(), is( true ) );
    }

    @Test
    public void testDecideValidDescribeProcessNotGrantedUser()
                    throws Exception {
        Authentication authentication = mockDefaultAuthenticationWithCapabilitiesPermissions();
        WpsRequest request = mockDescribeProcessRequest();
        AuthorizationReport report = authorizationManager.decide( authentication, request );
        assertThat( report.isAuthorized(), is( false ) );
    }

    @Test
    public void testDecideValidDescribeProcessInvalidProcessIdShouldBeUnauthorized()
                    throws Exception {
        Authentication authentication = mockDefaultAuthenticationWithAllPermissionsButInvalidProcessId();
        WpsRequest request = mockDescribeProcessRequest();
        AuthorizationReport report = authorizationManager.decide( authentication, request );
        assertThat( report.isAuthorized(), is( false ) );
    }

    @Test
    public void testDecideValidDescribeProcessProcessIdInvalidDescribeProcessAndValidExecuteShouldBeUnauthorized()
                    throws Exception {
        Authentication authentication = mockDefaultAuthentication( EXECUTE, DESCRIBEPROCESS );
        WpsRequest request = mockDescribeProcessRequest();
        AuthorizationReport report = authorizationManager.decide( authentication, request );
        assertThat( report.isAuthorized(), is( false ) );
    }

    private WpsRequest mockGetCapabilitiesRequest() {
        return mockRequest( null, GETCAPABILITIES, SERVICE_NAME, VERSION_100 );
    }

    private WpsRequest mockExecuteRequest() {
        return mockRequest( PROCESS_ID, EXECUTE, SERVICE_NAME, VERSION_100 );
    }

    private WpsRequest mockDescribeProcessRequest() {
        return mockRequest( PROCESS_ID, DESCRIBEPROCESS, SERVICE_NAME, VERSION_100 );
    }

    private WpsRequest mockGetCapabilitiesRequestWithUnsupportedVersion() {
        return mockRequest( null, GETCAPABILITIES, SERVICE_NAME, VERSION_200 );
    }

    private WpsRequest mockRequest( String layerName, String operationType, String serviceName,
                                    OwsServiceVersion version ) {
        WpsRequest mock = mock( WpsRequest.class );
        when( mock.getOperationType() ).thenReturn( operationType );
        when( mock.getServiceVersion() ).thenReturn( version );
        when( mock.getServiceName() ).thenReturn( serviceName );
        when( mock.getServiceType() ).thenReturn( SERVICE_TYPE );
        if ( layerName == null )
            when( mock.getIdentifiers() ).thenReturn( Collections.<String>emptyList() );
        else
            when( mock.getIdentifiers() ).thenReturn( Collections.singletonList( layerName ) );
        return mock;
    }

    private Authentication mockDefaultAuthenticationWithAllPermissions() {
        Authentication authentication = mock( Authentication.class );
        Collection<RasterPermission> authorities = new ArrayList<RasterPermission>();
        authorities.add( new RasterPermission( SERVICE_TYPE, GETCAPABILITIES, VERSION_LESS_EQUAL_100, null, null, null,
                        null ) );
        authorities.add( new RasterPermission( SERVICE_TYPE, EXECUTE, VERSION_LESS_EQUAL_100, PROCESS_ID, null, null,
                        null ) );
        authorities.add( new RasterPermission( SERVICE_TYPE, DESCRIBEPROCESS, VERSION_LESS_EQUAL_100, PROCESS_ID, null,
                        null, null ) );
        doReturn( authorities ).when( authentication ).getAuthorities();
        return authentication;
    }

    private Authentication mockDefaultAuthenticationWithAllPermissionsButInvalidProcessId() {
        Authentication authentication = mock( Authentication.class );
        Collection<RasterPermission> authorities = new ArrayList<RasterPermission>();
        authorities.add( new RasterPermission( SERVICE_TYPE, GETCAPABILITIES, VERSION_LESS_EQUAL_100, null, null, null,
                        null ) );
        authorities.add( new RasterPermission( SERVICE_TYPE, EXECUTE, VERSION_LESS_EQUAL_100, "invalidProcessId", null,
                        null, null ) );
        authorities.add( new RasterPermission( SERVICE_TYPE, DESCRIBEPROCESS, VERSION_LESS_EQUAL_100,
                        "invalidProcessId", null, null, null ) );
        doReturn( authorities ).when( authentication ).getAuthorities();
        return authentication;
    }

    private Authentication mockDefaultAuthentication( String operationNameWithValidProcessId,
                                                      String operationNameWithInvalidProcessId ) {
        Authentication authentication = mock( Authentication.class );
        Collection<RasterPermission> authorities = new ArrayList<RasterPermission>();
        authorities.add( new RasterPermission( SERVICE_TYPE, operationNameWithValidProcessId, VERSION_LESS_EQUAL_100,
                        PROCESS_ID, null, null, null ) );
        authorities.add( new RasterPermission( SERVICE_TYPE, operationNameWithInvalidProcessId, VERSION_LESS_EQUAL_100,
                        "invalidProcessId", null, null, null ) );
        doReturn( authorities ).when( authentication ).getAuthorities();
        return authentication;
    }

    private Authentication mockDefaultAuthenticationWithCapabilitiesPermissions() {
        Authentication authentication = mock( Authentication.class );
        Collection<RasterPermission> authorities = new ArrayList<RasterPermission>();
        authorities.add( new RasterPermission( SERVICE_TYPE, GETCAPABILITIES, VERSION_LESS_EQUAL_100, null, null, null,
                        null ) );
        doReturn( authorities ).when( authentication ).getAuthorities();
        return authentication;
    }
}
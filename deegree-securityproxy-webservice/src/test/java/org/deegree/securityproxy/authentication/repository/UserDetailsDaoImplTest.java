package org.deegree.securityproxy.authentication.repository;

import static org.deegree.securityproxy.commons.WcsOperationType.GETCAPABILITIES;
import static org.deegree.securityproxy.commons.WcsOperationType.GETCOVERAGE;
import static org.deegree.securityproxy.commons.WcsServiceVersion.VERSION_100;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.deegree.securityproxy.authentication.wcs.WcsPermission;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author <a href="goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:org/deegree/securityproxy/authentication/repository/UserDaoTestContext.xml" })
public class UserDetailsDaoImplTest {

    private EmbeddedDatabase db;

    @Autowired
    private UserDetailsDao source;

    @Before
    public void setUp() {
        db = new EmbeddedDatabaseBuilder().build();
    }

    @Test
    public void testLoadUserDetailsFromDataSourceValidHeaderShouldReturnUserDetails() {
        UserDetails details = source.loadUserDetailsFromDataSource( "VALID_HEADER" );
        assertThat( details.getUsername(), is( "USER" ) );
        assertThat( details.getPassword(), is( "PASSWORD" ) );
    }

    @Test
    public void testLoadUserDetailsFromDataSourceValidHeaderShouldReturnUserDetailWithGetCapabilitiesPermission() {
        UserDetails details = source.loadUserDetailsFromDataSource( "VALID_HEADER_GETCAPABILITIES" );
        Collection<? extends GrantedAuthority> authorities = details.getAuthorities();
        assertThat( authorities.size(), is( 1 ) );
        WcsPermission firstAuthority = (WcsPermission) authorities.iterator().next();
        assertThat( firstAuthority.getOperationType(), is( GETCAPABILITIES ) );
        assertThat( firstAuthority.getServiceVersion(), is( VERSION_100 ) );
        assertThat( firstAuthority.getServiceName(), is( "serviceName" ) );
        assertThat( firstAuthority.getCoverageName(), is( nullValue() ) );
    }

    @Test
    public void testLoadUserDetailsFromDataSourceValidHeaderShouldReturnUserDetailWithGetCoveragePermission() {
        UserDetails details = source.loadUserDetailsFromDataSource( "VALID_HEADER_GETCOVERAGE" );
        Collection<? extends GrantedAuthority> authorities = details.getAuthorities();
        assertThat( authorities.size(), is( 1 ) );
        WcsPermission firstAuthority = (WcsPermission) authorities.iterator().next();
        assertThat( firstAuthority.getOperationType(), is( GETCOVERAGE ) );
        assertThat( firstAuthority.getServiceVersion(), is( VERSION_100 ) );
        assertThat( firstAuthority.getServiceName(), is( "serviceName" ) );
        assertThat( firstAuthority.getCoverageName(), is( "layerName" ) );
    }

    @Test
    public void testLoadUserDetailsFromDataSourceValidHeaderShouldReturnUserDetailWithMultiplePermissions() {
        UserDetails details = source.loadUserDetailsFromDataSource( "VALID_HEADER_MULTIPLE" );
        Collection<? extends GrantedAuthority> authorities = details.getAuthorities();
        assertThat( authorities.size(), is( 2 ) );
    }

    @Test
    public void testLoadUserDetailsFromDataSourceValidHeaderShouldReturnUserDetailWithOnePermissionsButMultipleVersions() {
        UserDetails details = source.loadUserDetailsFromDataSource( "VALID_HEADER_MULTIPLE_VERSIONS" );
        Collection<? extends GrantedAuthority> authorities = details.getAuthorities();
        assertThat( authorities.size(), is( 3 ) );
    }

    @Test
    public void testLoadUserDetailsFromDataSourceInvalidHeaderShouldReturnNull() {
        UserDetails details = source.loadUserDetailsFromDataSource( "INVALID_HEADER" );
        assertThat( details, nullValue() );
    }

    @Test
    public void testLoadUserDetailsForUserWithNotWcsPermissionFails() {
        UserDetails details = source.loadUserDetailsFromDataSource( "WMS_VALID_HEADER" );
        assertThat( details, nullValue() );
    }

    @Test
    public void testLoadUserDetailsForUserSubscriptionOk() {
        UserDetails details = source.loadUserDetailsFromDataSource( "VALID_HEADER_SUBSCRIPTION_OK" );
        assertThat( details, notNullValue() );
    }

    @Test
    public void testLoadUserDetailsForUserSubscriptionExpired() {
        UserDetails details = source.loadUserDetailsFromDataSource( "VALID_HEADER_SUBSCRIPTION_EXPIRED" );
        assertThat( details, nullValue() );
    }

    @Test
    public void testLoadUserDetailsFromDataSourceShouldThrowExceptionOnEmptyHeader() {
        UserDetails details = source.loadUserDetailsFromDataSource( "" );
        assertThat( details, nullValue() );
    }

    @Test
    public void testLoadUserDetailsFromDataSourceShouldThrowExceptionOnNullArgument() {
        UserDetails details = source.loadUserDetailsFromDataSource( null );
        assertThat( details, nullValue() );
    }

    @After
    public void tearDown() {
        db.shutdown();
    }
}

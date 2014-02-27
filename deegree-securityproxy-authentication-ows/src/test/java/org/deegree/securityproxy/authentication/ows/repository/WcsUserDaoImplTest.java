package org.deegree.securityproxy.authentication.ows.repository;

import static org.deegree.securityproxy.authentication.ows.domain.WcsOperationType.GETCAPABILITIES;
import static org.deegree.securityproxy.authentication.ows.domain.WcsOperationType.GETCOVERAGE;
import static org.deegree.securityproxy.authentication.ows.domain.WcsServiceVersion.VERSION_100;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.deegree.securityproxy.authentication.ows.WcsGeometryFilterInfo;
import org.deegree.securityproxy.authentication.ows.WcsPermission;
import org.deegree.securityproxy.authentication.ows.WcsUser;
import org.deegree.securityproxy.authentication.repository.UserDao;
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
 * @version $Revision: $, $Date: $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:org/deegree/securityproxy/authentication/ows/repository/UserDaoTestContext.xml" })
public class WcsUserDaoImplTest {

    private EmbeddedDatabase db;

    @Autowired
    private UserDao source;

    @Before
    public void setUp() {
        db = new EmbeddedDatabaseBuilder().build();
    }

    @Test
    public void testRetrieveUserByIdValidHeaderShouldReturnUserDetails() {
        UserDetails details = source.retrieveUserById( "VALID_HEADER" );
        assertThat( details.getUsername(), is( "USER" ) );
        assertThat( details.getPassword(), is( "PASSWORD" ) );
    }

    @Test
    public void testRetrieveUserByIdValidHeaderShouldReturnUserDetailWithGetCapabilitiesPermission() {
        UserDetails details = source.retrieveUserById( "VALID_HEADER_GETCAPABILITIES" );
        Collection<? extends GrantedAuthority> authorities = details.getAuthorities();
        assertThat( authorities.size(), is( 1 ) );
        WcsPermission firstAuthority = (WcsPermission) authorities.iterator().next();
        assertThat( firstAuthority.getOperationType(), is( GETCAPABILITIES ) );
        assertThat( firstAuthority.getServiceVersion(), is( VERSION_100 ) );
        assertThat( firstAuthority.getServiceName(), is( "serviceName" ) );
        assertThat( firstAuthority.getCoverageName(), is( nullValue() ) );
    }

    @Test
    public void testRetrieveUserByIdValidHeaderShouldReturnUserDetailWithGetCoveragePermission() {
        UserDetails details = source.retrieveUserById( "VALID_HEADER_GETCOVERAGE" );
        Collection<? extends GrantedAuthority> authorities = details.getAuthorities();
        assertThat( authorities.size(), is( 1 ) );
        WcsPermission firstAuthority = (WcsPermission) authorities.iterator().next();
        assertThat( firstAuthority.getOperationType(), is( GETCOVERAGE ) );
        assertThat( firstAuthority.getServiceVersion(), is( VERSION_100 ) );
        assertThat( firstAuthority.getServiceName(), is( "serviceName" ) );
        assertThat( firstAuthority.getCoverageName(), is( "layerName" ) );
    }

    @Test
    public void testRetrieveUserByIdValidHeaderShouldReturnUserDetailWithMultiplePermissions() {
        UserDetails details = source.retrieveUserById( "VALID_HEADER_MULTIPLE" );
        Collection<? extends GrantedAuthority> authorities = details.getAuthorities();
        assertThat( authorities.size(), is( 2 ) );
    }

    @Test
    public void testRetrieveUserByIdValidHeaderShouldReturnUserDetailWithOnePermissionsButMultipleVersions() {
        UserDetails details = source.retrieveUserById( "VALID_HEADER_MULTIPLE_VERSIONS" );
        Collection<? extends GrantedAuthority> authorities = details.getAuthorities();
        assertThat( authorities.size(), is( 3 ) );
    }

    @Test
    public void testRetrieveUserByIdInvalidHeaderShouldReturnNull() {
        UserDetails details = source.retrieveUserById( "INVALID_HEADER" );
        assertThat( details, nullValue() );
    }

    @Test
    public void testLoadUserDetailsForUserWithNotWcsPermissionFails() {
        UserDetails details = source.retrieveUserById( "WMS_VALID_HEADER" );
        assertThat( details, nullValue() );
    }

    @Test
    public void testLoadUserDetailsForUserSubscriptionOk() {
        UserDetails details = source.retrieveUserById( "VALID_HEADER_SUBSCRIPTION_OK" );
        assertThat( details, notNullValue() );
    }

    @Test
    public void testLoadUserDetailsForUserSubscriptionExpired() {
        UserDetails details = source.retrieveUserById( "VALID_HEADER_SUBSCRIPTION_EXPIRED" );
        assertThat( details, nullValue() );
    }

    @Test
    public void testRetrieveUserByIdShouldThrowExceptionOnEmptyHeader() {
        WcsUser wcsUser = (WcsUser) source.retrieveUserById( "" );
        assertThat( wcsUser, nullValue() );
    }

    @Test
    public void testRetrieveUserByIdShouldThrowExceptionOnNullArgument() {
        WcsUser wcsUser = (WcsUser) source.retrieveUserById( null );
        assertThat( wcsUser, nullValue() );
    }

    @Test
    public void testRetrieveUserByIdValidHeaderWithGeometryLimitShouldReturnEmptyCollection() {
        WcsUser wcsUser = (WcsUser) source.retrieveUserById( "VALID_HEADER_WITH_NULL_GEOMETRY_LIMIT" );
        List<WcsGeometryFilterInfo> emptyList = Collections.emptyList();
        assertThat( wcsUser.getWcsGeometryFilterInfos(), is( emptyList ) );
    }

    @Test
    public void testRetrieveUserByIdValidHeaderWithGeometryLimitShouldReturnCollectionWithOneRecord() {
        int expectedSize = 1;
        String expectedCoverageName = "layer1";
        String expectedGeometryLimit = "SRID=4326;MULTIPOLYGON(((-89.739 20.864,-89.758 20.876,-89.765 20.894,-89.748 20.897,-89.73 20.91,-89.708 20.928,-89.704 20.948,-89.716 20.964,-89.729 20.99,-89.73 21.017,-89.712 21.021,-89.685 21.031,-89.667 21.025,-89.641 21.017,-89.62 21.019,-89.599 21.018,-89.575 20.995,-89.568 20.97,-89.562 20.934,-89.562 20.91,-89.577 20.89,-89.609 20.878,-89.636 20.877,-89.664 20.881,-89.683 20.904,-89.683 20.917,-89.664 20.941,-89.662 20.954,-89.674 20.965,-89.687 20.983,-89.705 20.989,-89.703 20.974,-89.696 20.961,-89.686 20.949,-89.683 20.935,-89.694 20.919,-89.705 20.901,-89.722 20.875,-89.727 20.869,-89.739 20.864),(-89.627 20.985,-89.603 20.962,-89.62 20.936,-89.634 20.943,-89.639 20.961,-89.649 20.975,-89.627 20.985)))";

        WcsUser wcsUser = (WcsUser) source.retrieveUserById( "VALID_HEADER_WITH_GEOMETRY_LIMIT_ONE_RECORD" );
        List<WcsGeometryFilterInfo> responseFilters = wcsUser.getWcsGeometryFilterInfos();
        WcsGeometryFilterInfo responseFilter = responseFilters.get( 0 );
        String coverageName = responseFilter.getCoverageName();
        String geometryLimit = responseFilter.getGeometryString();

        assertThat( responseFilters.size(), is( expectedSize ) );
        assertThat( coverageName, is( expectedCoverageName ) );
        assertThat( geometryLimit, is( expectedGeometryLimit ) );
    }

    @Test
    public void testRetrieveUserByIdValidHeaderWithGeometryLimitShouldReturnCollectionWithTwoRecords() {
        int expectedSize = 2;
        String expectedFirstCoverageName = "layer1";
        String expectedFirstGeometryLimit = "SRID=4326;MULTIPOLYGON(((-89.739 20.864,-89.758 20.876,-89.765 20.894,-89.748 20.897,-89.73 20.91,-89.708 20.928,-89.704 20.948,-89.716 20.964,-89.729 20.99,-89.73 21.017,-89.712 21.021,-89.685 21.031,-89.667 21.025,-89.641 21.017,-89.62 21.019,-89.599 21.018,-89.575 20.995,-89.568 20.97,-89.562 20.934,-89.562 20.91,-89.577 20.89,-89.609 20.878,-89.636 20.877,-89.664 20.881,-89.683 20.904,-89.683 20.917,-89.664 20.941,-89.662 20.954,-89.674 20.965,-89.687 20.983,-89.705 20.989,-89.703 20.974,-89.696 20.961,-89.686 20.949,-89.683 20.935,-89.694 20.919,-89.705 20.901,-89.722 20.875,-89.727 20.869,-89.739 20.864),(-89.627 20.985,-89.603 20.962,-89.62 20.936,-89.634 20.943,-89.639 20.961,-89.649 20.975,-89.627 20.985)))";
        String expectedSecondCoverageName = "layer2";
        String expectedSecondGeometryLimit = "POLYGON";

        WcsUser wcsUser = (WcsUser) source.retrieveUserById( "VALID_HEADER_WITH_GEOMETRY_LIMIT_TWO_RECORDS" );
        List<WcsGeometryFilterInfo> responseFilters = wcsUser.getWcsGeometryFilterInfos();
        WcsGeometryFilterInfo firstResponseFilter = responseFilters.get( 0 );
        WcsGeometryFilterInfo secondResponseFilter = responseFilters.get( 1 );
        String firstCoverageName = firstResponseFilter.getCoverageName();
        String firstGeometryLimit = firstResponseFilter.getGeometryString();
        String secondCoverageName = secondResponseFilter.getCoverageName();
        String secondGeometryLimit = secondResponseFilter.getGeometryString();

        assertThat( responseFilters.size(), is( expectedSize ) );
        assertThat( firstCoverageName, is( expectedFirstCoverageName ) );
        assertThat( firstGeometryLimit, is( expectedFirstGeometryLimit ) );
        assertThat( secondCoverageName, is( expectedSecondCoverageName ) );
        assertThat( secondGeometryLimit, is( expectedSecondGeometryLimit ) );
    }

    @Test
    public void testRetrieveUserByIdValidHeaderShouldReturnInternalServiceUrl() {
        UserDetails wcsUser = source.retrieveUserById( "VALID_HEADER_INTERNAL_SERVICE_URL" );
        List<String> internalServiceUrls = new ArrayList<String>();
        for ( GrantedAuthority authority : wcsUser.getAuthorities() ) {
            String internalServiceUrl = ( (WcsPermission) authority ).getInternalServiceUrl();
            internalServiceUrls.add( internalServiceUrl );
        }
        assertThat( internalServiceUrls, hasItem( "serviceUrl" ) );
    }

    @Test
    public void testRetrieveUserByIdWithoutAdditionalRequestParameters() {
        WcsUser wcsUser = (WcsUser) source.retrieveUserById( "VALID_HEADER_INTERNAL_SERVICE_URL" );
        Map<String, String[]> additionalKeyValuePair = retrieveFirstAdditionalKeyValuePair( wcsUser );

        assertThat( additionalKeyValuePair.size(), is( 0 ) );
    }

    @Test
    public void testRetrieveUserByIdWithOneAdditionalRequestParameters() {
        WcsUser wcsUser = (WcsUser) source.retrieveUserById( "VALID_HEADER_WITH_ONE_EMPTY_REQUEST_PARAM" );
        Map<String, String[]> additionalKeyValuePair = retrieveFirstAdditionalKeyValuePair( wcsUser );

        assertThat( additionalKeyValuePair.size(), is( 1 ) );
        assertThat( additionalKeyValuePair.containsKey( "requestParam1" ), is( true ) );
        assertThat( additionalKeyValuePair.get( "requestParam1" ), is( new String[] { "addParam1" } ) );
    }

    @Test
    public void testRetrieveUserByIdWithAdditionalRequestParameters() {
        WcsUser wcsUser = (WcsUser) source.retrieveUserById( "VALID_HEADER_WITH_REQUEST_PARAMS" );
        Map<String, String[]> additionalKeyValuePair = retrieveFirstAdditionalKeyValuePair( wcsUser );

        assertThat( additionalKeyValuePair.size(), is( 2 ) );
        assertThat( additionalKeyValuePair.containsKey( "requestParam1" ), is( true ) );
        assertThat( additionalKeyValuePair.containsKey( "requestParam2" ), is( true ) );
        assertThat( additionalKeyValuePair.get( "requestParam1" ), is( new String[] { "addParam1" } ) );
        assertThat( additionalKeyValuePair.get( "requestParam2" ), is( new String[] { "addParam2" } ) );
    }

    @Test
    public void testRetrieveUserByIdWithMultiplePermissionsWithAdditionalRequestParameters() {
        WcsUser wcsUser = (WcsUser) source.retrieveUserById( "VALID_HEADER_WITH_REQUEST_PARAMS" );
        List<Map<String, String[]>> additionalKeyValuePairsList = new ArrayList<Map<String, String[]>>();
        for ( GrantedAuthority authority : wcsUser.getAuthorities() ) {
            Map<String, String[]> additionalKeyValuePairs = ( (WcsPermission) authority ).getAdditionalKeyValuePairs();
            additionalKeyValuePairsList.add( additionalKeyValuePairs );
        }
        Map<String, String[]> firstAdditionalKeyValuePairs = additionalKeyValuePairsList.get( 0 );
        Map<String, String[]> secondAdditionalKeyValuePairs = additionalKeyValuePairsList.get( 1 );

        assertThat( additionalKeyValuePairsList.size(), is( 2 ) );
        assertThat( firstAdditionalKeyValuePairs.containsKey( "requestParam1" ), is( true ) );
        assertThat( firstAdditionalKeyValuePairs.containsKey( "requestParam2" ), is( true ) );
        assertThat( firstAdditionalKeyValuePairs.get( "requestParam1" ), is( new String[] { "addParam1" } ) );
        assertThat( firstAdditionalKeyValuePairs.get( "requestParam2" ), is( new String[] { "addParam2" } ) );
        assertThat( secondAdditionalKeyValuePairs.containsKey( "requestParam1" ), is( true ) );
        assertThat( secondAdditionalKeyValuePairs.containsKey( "requestParam2" ), is( true ) );
        assertThat( secondAdditionalKeyValuePairs.get( "requestParam1" ), is( new String[] { "addParam1" } ) );
        assertThat( secondAdditionalKeyValuePairs.get( "requestParam2" ), is( new String[] { "addParam2" } ) );
    }

    @Test
    public void testRetrieveUserByIdShouldHabeCorrectAccessToken() {
        WcsUser wcsUser = (WcsUser) source.retrieveUserById( "VALID_HEADER_WITH_REQUEST_PARAMS" );

        assertThat( wcsUser.getAccessToken(), is( "VALID_HEADER_WITH_REQUEST_PARAMS" ) );
    }

    /* retrieveUserByName */

    @Test
    public void testRetrieveUserByNameValidNameShouldReturnUserDetailWithPermissions() {
        UserDetails details = source.retrieveUserByName( "VALID_USER_MULTIPLE_VERSIONS" );
        Collection<? extends GrantedAuthority> authorities = details.getAuthorities();
        assertThat( authorities.size(), is( 3 ) );
    }

    @Test
    public void testRetrieveUserByNameShouldHabeCorrectAccessToken() {
        WcsUser wcsUser = (WcsUser) source.retrieveUserByName( "VALID_USER_GETCAPABILITIES" );
        assertThat( wcsUser.getAccessToken(), is( "HEADER_GC" ) );
    }

    @Test
    public void testRetrieveUserByNameValidSubscription() {
        UserDetails details = source.retrieveUserByName( "VALID_USER_SUBSCRIPTION_OK" );
        assertThat( details, notNullValue() );
    }

    @Test
    public void testRetrieveUserByNameInvalidSubscription() {
        UserDetails details = source.retrieveUserByName( "VALID_USER_SUBSCRIPTION_EXPIRED" );
        assertThat( details, nullValue() );
    }

    @Test
    public void testRetrieveUserByNameInvalidNameShouldReturnNull() {
        UserDetails details = source.retrieveUserByName( "INVALID_USER" );
        assertThat( details, nullValue() );
    }

    @Test
    public void testRetrieveUserByNameEmptyNameShouldReturnNull() {
        WcsUser wcsUser = (WcsUser) source.retrieveUserByName( "" );
        assertThat( wcsUser, nullValue() );
    }

    @Test
    public void testRetrieveUserByNameNullNameShouldReturnNull() {
        WcsUser wcsUser = (WcsUser) source.retrieveUserByName( null );
        assertThat( wcsUser, nullValue() );
    }

    @After
    public void tearDown() {
        db.shutdown();
    }

    private Map<String, String[]> retrieveFirstAdditionalKeyValuePair( WcsUser wcsUser ) {
        GrantedAuthority authority = wcsUser.getAuthorities().get( 0 );
        return ( (WcsPermission) authority ).getAdditionalKeyValuePairs();
    }

}
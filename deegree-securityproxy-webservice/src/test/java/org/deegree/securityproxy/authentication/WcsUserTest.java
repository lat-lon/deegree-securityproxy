package org.deegree.securityproxy.authentication;

import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.List;

import org.deegree.securityproxy.authentication.wcs.WcsPermission;
import org.junit.Test;

public class WcsUserTest {

    private static final int EMPTY = 0;

    private static final String USERNAME = null;

    private static final String PASSWORD = null;

    private static final String INTERNAL_SERVICE_URL = "internal-service-url";

    @Test
    public void testConstructorWithNullAuthoritiesShouldReturnEmptyLists()
                            throws Exception {
        WcsUser wcsUser = new WcsUser( USERNAME, PASSWORD, null, createEmptyFilterList(), null );
        assertThat( wcsUser.getAuthorities().size(), is( EMPTY ) );
        assertThat( wcsUser.getWcsPermissions().size(), is( EMPTY ) );
    }

    @Test
    public void testGetPermissionsShouldReturnInsertedPermissions()
                            throws Exception {
        List<WcsPermission> insertedPermissionsList = createPermissionsList();
        WcsUser wcsUser = new WcsUser( USERNAME, PASSWORD, insertedPermissionsList, createEmptyFilterList(), null );
        List<WcsPermission> authorities = wcsUser.getWcsPermissions();
        assertThat( authorities, is( insertedPermissionsList ) );
    }

    @Test
    public void testGetPermissionsShouldReturnInsertedFilters()
                            throws Exception {
        List<WcsGeometryFilterInfo> insertedFilterList = createFilterList();
        WcsUser wcsUser = new WcsUser( USERNAME, PASSWORD, createEmptyPermissionsList(), insertedFilterList, null );
        List<WcsGeometryFilterInfo> filters = wcsUser.getWcsGeometryFilterInfos();
        assertThat( filters, is( insertedFilterList ) );
    }

    @Test
    public void testGetPermissionsShouldReturnInsertedInternalServiceUrl()
          throws Exception {
        String insertedServiceUrl = INTERNAL_SERVICE_URL;
        WcsUser wcsUser = new WcsUser( USERNAME, PASSWORD, createEmptyPermissionsList(), createEmptyFilterList(),
                                       insertedServiceUrl );
        String serviceUrl = wcsUser.getInternalServiceUrl();
        assertThat( serviceUrl, is( insertedServiceUrl ) );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetPermissionsShouldReturnUnmodifiableWcsPermissionList()
                            throws Exception {
        WcsUser wcsUser = new WcsUser( USERNAME, PASSWORD, createPermissionsList(), createEmptyFilterList(), null );
        List<WcsPermission> authorities = wcsUser.getWcsPermissions();
        authorities.add( mockWcsPermission() );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetPermissionsShouldReturnUnmodifiableFilterList()
                            throws Exception {
        WcsUser wcsUser = new WcsUser( USERNAME, PASSWORD, createEmptyPermissionsList(), createFilterList(), null );
        List<WcsGeometryFilterInfo> filters = wcsUser.getWcsGeometryFilterInfos();
        filters.add( mockFilter() );
    }

    private List<WcsPermission> createEmptyPermissionsList() {
        return emptyList();
    }

    private List<WcsGeometryFilterInfo> createEmptyFilterList() {
        return emptyList();
    }

    private List<WcsPermission> createPermissionsList() {
        return Collections.singletonList( mockWcsPermission() );
    }

    private List<WcsGeometryFilterInfo> createFilterList() {
        return Collections.singletonList( mockFilter() );
    }

    private WcsPermission mockWcsPermission() {
        return mock( WcsPermission.class );
    }

    private WcsGeometryFilterInfo mockFilter() {
        return mock( WcsGeometryFilterInfo.class );
    }

}

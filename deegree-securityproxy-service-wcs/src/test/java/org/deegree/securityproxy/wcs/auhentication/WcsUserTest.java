package org.deegree.securityproxy.wcs.auhentication;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.deegree.securityproxy.wcs.authentication.WcsGeometryFilterInfo;
import org.deegree.securityproxy.wcs.authentication.WcsPermission;
import org.deegree.securityproxy.wcs.authentication.WcsUser;
import org.junit.Test;

/***
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class WcsUserTest {

    private static final int EMPTY = 0;

    private static final String USERNAME = null;

    private static final String PASSWORD = null;

    @Test
    public void testConstructorWithNullAuthoritiesShouldReturnEmptyLists()
                            throws Exception {
        WcsUser wcsUser = new WcsUser( USERNAME, PASSWORD, null, createEmptyFilterList() );
        assertThat( wcsUser.getAuthorities().size(), is( EMPTY ) );
        assertThat( wcsUser.getWcsPermissions().size(), is( EMPTY ) );
    }

    @Test
    public void testGetPermissionsShouldReturnInsertedPermissions()
                            throws Exception {
        List<WcsPermission> insertedPermissionsList = createPermissionsList();
        WcsUser wcsUser = new WcsUser( USERNAME, PASSWORD, insertedPermissionsList, createEmptyFilterList() );
        List<WcsPermission> authorities = wcsUser.getWcsPermissions();
        assertThat( authorities, is( insertedPermissionsList ) );
    }

    @Test
    public void testGetPermissionsShouldReturnInsertedFilters()
                            throws Exception {
        List<WcsGeometryFilterInfo> insertedFilterList = createFilterList();
        WcsUser wcsUser = new WcsUser( USERNAME, PASSWORD, createEmptyPermissionsList(), insertedFilterList );
        List<WcsGeometryFilterInfo> filters = wcsUser.getWcsGeometryFilterInfos();
        assertThat( filters, is( insertedFilterList ) );
    }



    @Test(expected = UnsupportedOperationException.class)
    public void testGetPermissionsShouldReturnUnmodifiableWcsPermissionList()
                            throws Exception {
        WcsUser wcsUser = new WcsUser( USERNAME, PASSWORD, createPermissionsList(), createEmptyFilterList() );
        List<WcsPermission> authorities = wcsUser.getWcsPermissions();
        authorities.add( mockWcsPermission() );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetPermissionsShouldReturnUnmodifiableFilterList()
                            throws Exception {
        WcsUser wcsUser = new WcsUser( USERNAME, PASSWORD, createEmptyPermissionsList(), createFilterList() );
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
        return singletonList( mockFilter() );
    }

    private WcsPermission mockWcsPermission() {
        return mock( WcsPermission.class );
    }

    private WcsGeometryFilterInfo mockFilter() {
        return mock( WcsGeometryFilterInfo.class );
    }

}
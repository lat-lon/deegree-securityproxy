package org.deegree.securityproxy.authentication.ows;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.List;

import org.deegree.securityproxy.authentication.ows.GeometryFilterInfo;
import org.deegree.securityproxy.authentication.ows.RasterPermission;
import org.deegree.securityproxy.authentication.ows.WcsUser;
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

    private static final String ACCESSTOKEN = "accessToken";

    @Test
    public void testConstructorWithNullAuthoritiesShouldReturnEmptyLists()
                            throws Exception {
        WcsUser wcsUser = new WcsUser( USERNAME, PASSWORD, ACCESSTOKEN, null, createEmptyFilterList() );
        assertThat( wcsUser.getAuthorities().size(), is( EMPTY ) );
        assertThat( wcsUser.getWcsPermissions().size(), is( EMPTY ) );
    }

    @Test
    public void testGetPermissionsShouldReturnInsertedPermissions()
                            throws Exception {
        List<RasterPermission> insertedPermissionsList = createPermissionsList();
        WcsUser wcsUser = new WcsUser( USERNAME, PASSWORD, ACCESSTOKEN, insertedPermissionsList,
                                       createEmptyFilterList() );
        List<RasterPermission> authorities = wcsUser.getWcsPermissions();
        assertThat( authorities, is( insertedPermissionsList ) );
    }

    @Test
    public void testGetPermissionsShouldReturnInsertedFilters()
                            throws Exception {
        List<GeometryFilterInfo> insertedFilterList = createFilterList();
        WcsUser wcsUser = new WcsUser( USERNAME, PASSWORD, ACCESSTOKEN, createEmptyPermissionsList(),
                                       insertedFilterList );
        List<GeometryFilterInfo> filters = wcsUser.getWcsGeometryFilterInfos();
        assertThat( filters, is( insertedFilterList ) );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetPermissionsShouldReturnUnmodifiableWcsPermissionList()
                            throws Exception {
        WcsUser wcsUser = new WcsUser( USERNAME, PASSWORD, ACCESSTOKEN, createPermissionsList(),
                                       createEmptyFilterList() );
        List<RasterPermission> authorities = wcsUser.getWcsPermissions();
        authorities.add( mockWcsPermission() );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetPermissionsShouldReturnUnmodifiableFilterList()
                            throws Exception {
        WcsUser wcsUser = new WcsUser( USERNAME, PASSWORD, ACCESSTOKEN, createEmptyPermissionsList(),
                                       createFilterList() );
        List<GeometryFilterInfo> filters = wcsUser.getWcsGeometryFilterInfos();
        filters.add( mockFilter() );
    }

    private List<RasterPermission> createEmptyPermissionsList() {
        return emptyList();
    }

    private List<GeometryFilterInfo> createEmptyFilterList() {
        return emptyList();
    }

    private List<RasterPermission> createPermissionsList() {
        return Collections.singletonList( mockWcsPermission() );
    }

    private List<GeometryFilterInfo> createFilterList() {
        return singletonList( mockFilter() );
    }

    private RasterPermission mockWcsPermission() {
        return mock( RasterPermission.class );
    }

    private GeometryFilterInfo mockFilter() {
        return mock( GeometryFilterInfo.class );
    }

}
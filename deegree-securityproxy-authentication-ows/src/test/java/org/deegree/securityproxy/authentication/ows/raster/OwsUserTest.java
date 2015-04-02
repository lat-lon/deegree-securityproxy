package org.deegree.securityproxy.authentication.ows.raster;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

/***
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class OwsUserTest {

    private static final int EMPTY = 0;

    private static final String USERNAME = null;

    private static final String PASSWORD = null;

    private static final String ACCESSTOKEN = "accessToken";

    @Test
    public void testConstructorWithNullAuthoritiesShouldReturnEmptyLists()
                            throws Exception {
        OwsUser wcsUser = new OwsUser( USERNAME, PASSWORD, ACCESSTOKEN, null, createEmptyFilterList() );
        assertThat( wcsUser.getAuthorities().size(), is( EMPTY ) );
    }

    @Test
    public void testGetPermissionsShouldReturnInsertedPermissions()
                            throws Exception {
        List<OwsPermission> insertedPermissionsList = createPermissionsList();
        OwsUser wcsUser = new OwsUser( USERNAME, PASSWORD, ACCESSTOKEN, insertedPermissionsList,
                                             createEmptyFilterList() );
        List<OwsPermission> authorities = wcsUser.getAuthorities();
        assertThat( authorities, is( insertedPermissionsList ) );
    }

    @Test
    public void testGetPermissionsShouldReturnInsertedFilters()
                            throws Exception {
        List<GeometryFilterInfo> insertedFilterList = createFilterList();
        OwsUser wcsUser = new OwsUser( USERNAME, PASSWORD, ACCESSTOKEN, createEmptyPermissionsList(),
                                             insertedFilterList );
        List<GeometryFilterInfo> filters = wcsUser.getRasterGeometryFilterInfos();
        assertThat( filters, is( insertedFilterList ) );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetPermissionsShouldReturnUnmodifiableWcsPermissionList()
                            throws Exception {
        OwsUser wcsUser = new OwsUser( USERNAME, PASSWORD, ACCESSTOKEN, createPermissionsList(),
                                             createEmptyFilterList() );
        List<OwsPermission> authorities = wcsUser.getAuthorities();
        authorities.add( mockWcsPermission() );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetPermissionsShouldReturnUnmodifiableFilterList()
                            throws Exception {
        OwsUser wcsUser = new OwsUser( USERNAME, PASSWORD, ACCESSTOKEN, createEmptyPermissionsList(),
                                             createFilterList() );
        List<GeometryFilterInfo> filters = wcsUser.getRasterGeometryFilterInfos();
        filters.add( mockFilter() );
    }

    private List<OwsPermission> createEmptyPermissionsList() {
        return emptyList();
    }

    private List<GeometryFilterInfo> createEmptyFilterList() {
        return emptyList();
    }

    private List<OwsPermission> createPermissionsList() {
        return Collections.singletonList( mockWcsPermission() );
    }

    private List<GeometryFilterInfo> createFilterList() {
        return singletonList( mockFilter() );
    }

    private OwsPermission mockWcsPermission() {
        return mock( OwsPermission.class );
    }

    private GeometryFilterInfo mockFilter() {
        return mock( GeometryFilterInfo.class );
    }

}
package org.deegree.securityproxy.authentication;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import org.deegree.securityproxy.authentication.wcs.WcsPermission;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * {@link UserDetails} implementation encapsulating username, password, authorities ({@link WcsPermission}s) and
 * {@link WcsGeometryFilterInfo}s.
 * 
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author <a href="stenger@lat-lon.de">Dirk Stenger</a>
 * @author <a href="goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class WcsUser implements UserDetails {

    private static final long serialVersionUID = 1264359266739783359L;

    private final String username;

    private final String password;

    private final List<WcsPermission> authorities;

    private final List<WcsGeometryFilterInfo> filters;

    /**
     * @param username
     *            may be <code>null</code>
     * @param password
     *            may be <code>null</code>
     * @param authorities
     *            may be <code>null</code> or empty
     * @param filters
     *            may be <code>null</code> or empty
     */
    public WcsUser( String username, String password, List<WcsPermission> authorities,
                    List<WcsGeometryFilterInfo> filters ) {
        this.username = username;
        this.password = password;
        if ( authorities == null )
            authorities = new ArrayList<WcsPermission>();
        this.authorities = unmodifiableList( authorities );
        if ( filters == null )
            filters = new ArrayList<WcsGeometryFilterInfo>();
        this.filters = unmodifiableList( filters );
    }

    @Override
    public List<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * @return authorities as list of {@link WcsPermission}s in a unmodifiable list, never <code>null</code>
     */
    public List<WcsPermission> getWcsPermissions() {
        return authorities;
    }

    /**
     * @return all {@link WcsGeometryFilterInfo}s in a unmodifiable list, never <code>null</code>
     */
    public List<WcsGeometryFilterInfo> getWcsGeometryFilterInfos() {
        return filters;
    }

}

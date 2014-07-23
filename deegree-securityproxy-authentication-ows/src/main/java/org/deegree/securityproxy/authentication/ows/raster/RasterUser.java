package org.deegree.securityproxy.authentication.ows.raster;

import static java.util.Collections.unmodifiableList;

import java.util.Collections;
import java.util.List;

import org.deegree.securityproxy.authentication.OwsUserDetails;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * {@link UserDetails} implementation encapsulating username, password, authorities ({@link RasterPermission}s) and
 * {@link GeometryFilterInfo}s.
 * 
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author <a href="stenger@lat-lon.de">Dirk Stenger</a>
 * @author <a href="goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class RasterUser extends OwsUserDetails {

    private static final long serialVersionUID = 1264359266739783359L;

    private final String username;

    private final String password;

    private final List<RasterPermission> authorities;

    private final List<GeometryFilterInfo> filters;

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
    public RasterUser( String username, String password, String accessToken, List<RasterPermission> authorities,
                       List<GeometryFilterInfo> filters ) {
        super( accessToken );
        this.username = username;
        this.password = password;

        if ( authorities != null )
            this.authorities = unmodifiableList( authorities );
        else
            this.authorities = unmodifiableList( Collections.<RasterPermission> emptyList() );

        if ( filters != null )
            this.filters = unmodifiableList( filters );
        else
            this.filters = unmodifiableList( Collections.<GeometryFilterInfo> emptyList() );
    }

    @Override
    public List<RasterPermission> getAuthorities() {
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
     * @return all {@link GeometryFilterInfo}s in a unmodifiable list, never <code>null</code>
     */
    public List<GeometryFilterInfo> getRasterGeometryFilterInfos() {
        return filters;
    }

}

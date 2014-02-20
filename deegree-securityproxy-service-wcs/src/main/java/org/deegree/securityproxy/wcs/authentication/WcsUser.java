package org.deegree.securityproxy.wcs.authentication;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    private final Map<String, String> additionalKeyValuePairs;

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
                    List<WcsGeometryFilterInfo> filters, Map<String, String> additionalKeyValuePairs ) {
        this.username = username;
        this.password = password;

        if ( authorities != null )
            this.authorities = unmodifiableList( authorities );
        else
            this.authorities = unmodifiableList( Collections.<WcsPermission> emptyList() );

        if ( filters != null )
            this.filters = unmodifiableList( filters );
        else
            this.filters = unmodifiableList( Collections.<WcsGeometryFilterInfo> emptyList() );

        if ( additionalKeyValuePairs != null )
            this.additionalKeyValuePairs = unmodifiableMap( additionalKeyValuePairs );
        else
            this.additionalKeyValuePairs = unmodifiableMap( Collections.<String, String> emptyMap() );
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

    /**
     * @return the additionalKeyValuePairs in a unmodifiable map, may be empty but never <code>null</code>
     */
    public Map<String, String> getAdditionalKeyValuePairs() {
        return additionalKeyValuePairs;
    }

}
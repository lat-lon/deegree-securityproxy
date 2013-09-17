package org.deegree.securityproxy.authentication;

import java.util.Collections;
import java.util.List;

import org.deegree.securityproxy.authentication.wcs.WcsPermission;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class WcsUser implements UserDetails {

    private static final long serialVersionUID = 1264359266739783359L;

    private final List<WcsGeometryFilterInfo> filters;

    private final String username;

    private final String password;

    private final List<WcsPermission> authorities;

    public WcsUser( String username, String password, List<WcsPermission> authorities,
                    List<WcsGeometryFilterInfo> filters ) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.filters = filters;
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

    public List<WcsPermission> getWcsPermissions() {
        return authorities;
    }

    public List<WcsGeometryFilterInfo> getResponseFilters() {
        return Collections.unmodifiableList( filters );
    }

}

package org.deegree.securityproxy.authentication;

import java.util.Collection;
import java.util.Collections;

import org.deegree.securityproxy.authentication.wcs.WcsPermission;
import org.deegree.securityproxy.responsefilter.ResponseFilter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class WcsUser implements UserDetails {

    private static final long serialVersionUID = 1264359266739783359L;

    private final Collection<ResponseFilter> filters;

    private final String username;

    private final String password;

    private final Collection<WcsPermission> authorities;

    public WcsUser( String username, String password, Collection<WcsPermission> authorities,
                    Collection<ResponseFilter> filters ) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.filters = filters;
    }

    public Collection<ResponseFilter> getFilters() {
        return Collections.unmodifiableCollection( filters );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
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

    public Collection<WcsPermission> getWcsPermissions() {
        return authorities;
    }

    public Collection<ResponseFilter> getResponseFilters() {
        return Collections.unmodifiableCollection( filters );
    }

}

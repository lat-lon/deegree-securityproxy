package org.deegree.securityproxy.authorization.wcs;

import org.deegree.securityproxy.authorization.logging.AuthorizationReport;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;

public class TestWcsRequestAuthorizationManager implements RequestAuthorizationManager {

    private boolean isAuthorized = true;

    public TestWcsRequestAuthorizationManager() {
    }

    public TestWcsRequestAuthorizationManager( boolean isAuthorized ) {
        this.isAuthorized = isAuthorized;
    }

    @Override
    public AuthorizationReport decide( Authentication authentication, Object object)
                            throws AccessDeniedException, InsufficientAuthenticationException {
        return new AuthorizationReport( "", isAuthorized, "" );
    }

    @Override
    public boolean supports( Class<?> clazz ) {
        return true;
    }

}

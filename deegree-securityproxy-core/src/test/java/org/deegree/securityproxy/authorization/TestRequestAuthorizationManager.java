package org.deegree.securityproxy.authorization;

import org.deegree.securityproxy.authorization.logging.AuthorizationReport;
import org.deegree.securityproxy.request.OwsRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;

public class TestRequestAuthorizationManager implements RequestAuthorizationManager {

    public static final String SERVICE_URL = "url";

    private boolean isAuthorized = true;

    private final Map<String, String[]> ADDITIONAL_KEY_VALUE_PAIRS = createAdditionalKeyValuePairs();

    public TestRequestAuthorizationManager() {
    }

    public TestRequestAuthorizationManager( boolean isAuthorized ) {
        this.isAuthorized = isAuthorized;
    }

    @Override
    public AuthorizationReport decide( Authentication authentication, OwsRequest request )
          throws AccessDeniedException, InsufficientAuthenticationException {
        return new AuthorizationReport( "", isAuthorized, SERVICE_URL, ADDITIONAL_KEY_VALUE_PAIRS );
    }

    @Override
    public boolean supports( Class<?> clazz ) {
        return true;
    }

    private Map<String, String[]> createAdditionalKeyValuePairs() {
        Map<String, String[]> additionalKeyValuePairs = new HashMap<String, String[]>();
        additionalKeyValuePairs.put( "additionalKey", new String[] { "additionalValue" } );
        return additionalKeyValuePairs;
    }

}
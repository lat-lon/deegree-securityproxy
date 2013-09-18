package org.deegree.securityproxy.authorization.wcs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.deegree.securityproxy.authentication.wcs.WcsPermission;
import org.deegree.securityproxy.authorization.logging.AuthorizationReport;
import org.deegree.securityproxy.commons.WcsOperationType;
import org.deegree.securityproxy.request.WcsRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * Checks if a authenticated User is permitted to perform an incoming {@link HttpServletRequest} against a WCS.
 * 
 * @author <a href="stenger@lat-lon.de">Dirk Stenger</a>
 * @author <a href="goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
public class WcsRequestAuthorizationManager implements RequestAuthorizationManager {

    public static final boolean NOT_AUTHORIZED = false;

    public static final boolean AUTHORIZED = true;

    private static final String NOT_AUTHENTICATED_ERROR_MSG = "Error while retrieving authentication! User could not be authenticated.";

    public static final String VERSION_UNAUTHORIZED_MSG = "User not permitted to access the service with the requested version.";

    public static final String OPTYPE_UNAUTHORIZED_MSG = "User not permitted to access the service with the requested operation type.";

    public static final String COVERAGENAME_UNAUTHORIZED_MSG = "User not permitted to access the service with the requested coverage.";

    public static final String ACCESS_GRANTED_MSG = "Access granted.";

    private static final String UNKNOWN_ERROR = "Unknown error. See application log for details.";

    @Override
    public AuthorizationReport decide( Authentication authentication, Object securedObject ) {
        if ( !checkAuthentication( authentication ) ) {
            return new AuthorizationReport( NOT_AUTHENTICATED_ERROR_MSG, NOT_AUTHORIZED );
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        WcsRequest wcsRequest = (WcsRequest) securedObject;
        if ( isGetCoverageRequest( wcsRequest ) ) {
            return authorizeGetCoverage( wcsRequest, authorities );
        } else if ( isDescribeCoverageRequest( wcsRequest ) ) {
            return authorizeDescribeCoverage( wcsRequest, authorities );
        } else if ( isGetCapabilitiesRequest( wcsRequest ) ) {
            return authorizeGetCapabilities( wcsRequest, authorities );
        }
        return new AuthorizationReport( UNKNOWN_ERROR, NOT_AUTHORIZED );
    }

    private boolean checkAuthentication( Authentication authentication ) {
        return !(authentication instanceof AnonymousAuthenticationToken);
    }

    @Override
    public boolean supports( Class<?> clazz ) {
        return WcsRequest.class.equals( clazz );
    }

    private AuthorizationReport authorizeDescribeCoverage( WcsRequest wcsRequest,
                                                           Collection<? extends GrantedAuthority> authorities ) {
        List<String> grantedCoverages = new ArrayList<String>();
        for ( GrantedAuthority authority : authorities ) {
            if ( authority instanceof WcsPermission ) {
                WcsPermission wcsPermission = (WcsPermission) authority;
                if ( isOperationTypeAuthorized( wcsRequest, wcsPermission )
                     && isServiceVersionAuthorized( wcsRequest, wcsPermission ) ) {
                    grantedCoverages.add( wcsPermission.getCoverageName() );
                }
            }
        }
        for ( String coverageName : wcsRequest.getCoverageNames() ) {
            if ( !grantedCoverages.contains( coverageName ) )
                return new AuthorizationReport( COVERAGENAME_UNAUTHORIZED_MSG, NOT_AUTHORIZED );

        }
        return new AuthorizationReport( ACCESS_GRANTED_MSG, AUTHORIZED );
    }

    private AuthorizationReport authorizeGetCoverage( WcsRequest wcsRequest,
                                                      Collection<? extends GrantedAuthority> authorities ) {
        String message = UNKNOWN_ERROR;
        for ( GrantedAuthority authority : authorities ) {
            if ( authority instanceof WcsPermission ) {
                WcsPermission wcsPermission = (WcsPermission) authority;
                if ( !isFirstCoverageNameAuthorized( wcsRequest, wcsPermission ) ) {
                    message = COVERAGENAME_UNAUTHORIZED_MSG;
                } else if ( !isOperationTypeAuthorized( wcsRequest, wcsPermission ) ) {
                    message = OPTYPE_UNAUTHORIZED_MSG;
                } else if ( !isServiceVersionAuthorized( wcsRequest, wcsPermission ) ) {
                    message = VERSION_UNAUTHORIZED_MSG;
                } else {
                    return new AuthorizationReport( ACCESS_GRANTED_MSG, AUTHORIZED );
                }
            }
        }
        return new AuthorizationReport( message, NOT_AUTHORIZED );
    }

    private AuthorizationReport authorizeGetCapabilities( WcsRequest wcsRequest,
                                                          Collection<? extends GrantedAuthority> authorities ) {
        String message = UNKNOWN_ERROR;
        for ( GrantedAuthority authority : authorities ) {
            if ( authority instanceof WcsPermission ) {
                WcsPermission wcsPermission = (WcsPermission) authority;
                if ( !isOperationTypeAuthorized( wcsRequest, wcsPermission ) ) {
                    message = OPTYPE_UNAUTHORIZED_MSG;
                } else if ( !isServiceVersionAuthorized( wcsRequest, wcsPermission ) ) {
                    message = VERSION_UNAUTHORIZED_MSG;
                } else {
                    return new AuthorizationReport( ACCESS_GRANTED_MSG, AUTHORIZED );
                }
            }
        }
        return new AuthorizationReport( message, NOT_AUTHORIZED );
    }

    private boolean isDescribeCoverageRequest( WcsRequest wcsRequest ) {
        return WcsOperationType.DESCRIBECOVERAGE.equals( wcsRequest.getOperationType() );
    }

    private boolean isGetCoverageRequest( WcsRequest wcsRequest ) {
        return WcsOperationType.GETCOVERAGE.equals( wcsRequest.getOperationType() );
    }

    private boolean isGetCapabilitiesRequest( WcsRequest wcsRequest ) {
        return WcsOperationType.GETCAPABILITIES.equals( wcsRequest.getOperationType() );

    }

    private boolean isOperationTypeAuthorized( WcsRequest wcsRequest, WcsPermission wcsPermission ) {
        if ( wcsRequest.getOperationType() != null )
            return wcsRequest.getOperationType().equals( wcsPermission.getOperationType() );
        return false;
    }

    private boolean isServiceVersionAuthorized( WcsRequest wcsRequest, WcsPermission wcsPermission ) {
        if ( wcsRequest.getServiceVersion() != null )
            return wcsRequest.getServiceVersion().equals( wcsPermission.getServiceVersion() );
        return false;
    }

    private boolean isFirstCoverageNameAuthorized( WcsRequest wcsRequest, WcsPermission wcsPermission ) {
        if ( !wcsRequest.getCoverageNames().isEmpty() ) {
            String firstCoverage = wcsRequest.getCoverageNames().get( 0 );
            return firstCoverage.equals( wcsPermission.getCoverageName() );
        }
        return false;

    }

}
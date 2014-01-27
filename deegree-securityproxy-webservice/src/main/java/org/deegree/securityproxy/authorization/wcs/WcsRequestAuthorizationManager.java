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

    public static final boolean AUTHORIZED = true;

    public static final String NOT_AUTHENTICATED_ERROR_MSG = "Error while retrieving authentication! User could not be authenticated.";

    public static final String ACCESS_GRANTED_MSG = "Access granted.";

    public static final String UNKNOWN_ERROR_MSG = "Unknown error. See application log for details.";

    public static final String DESCRIBECOVERAGE_UNAUTHORIZED_MSG = "User not permitted to perform operation DescribeCoverage with the given parameters";

    public static final String GETCOVERAGE_UNAUTHORIZED_MSG = "User not permitted to perform operation GetCoverage with the given parameters";

    public static final String GETCAPABILITIES_UNAUTHORIZED_MSG = "User not permitted to perform operation GetCapabilities with the given parameters";

    @Override
    public AuthorizationReport decide( Authentication authentication, Object securedObject ) {
        if ( !checkAuthentication( authentication ) ) {
            return new AuthorizationReport( NOT_AUTHENTICATED_ERROR_MSG );
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
        return new AuthorizationReport( UNKNOWN_ERROR_MSG );
    }

    private boolean checkAuthentication( Authentication authentication ) {
        return !( authentication instanceof AnonymousAuthenticationToken );
    }

    @Override
    public boolean supports( Class<?> clazz ) {
        return WcsRequest.class.equals( clazz );
    }

    private AuthorizationReport authorizeDescribeCoverage( WcsRequest wcsRequest,
                                                           Collection<? extends GrantedAuthority> authorities ) {
        List<String> grantedCoverages = new ArrayList<String>();
        String internalServiceUrl = null;
        for ( GrantedAuthority authority : authorities ) {
            if ( authority instanceof WcsPermission ) {
                WcsPermission wcsPermission = (WcsPermission) authority;
                if ( isOperationTypeAuthorized( wcsRequest, wcsPermission )
                     && isServiceVersionAuthorized( wcsRequest, wcsPermission )
                     && isServiceNameAuthorized( wcsRequest, wcsPermission ) ) {
                    grantedCoverages.add( wcsPermission.getCoverageName() );
                    // If there are data inconsistencies and a service-name is mapped to different internal-urls, the
                    // DSP always chooses the url of the last permission.
                    internalServiceUrl = wcsPermission.getInternalServiceUrl();
                }
            }
        }
        for ( String coverageName : wcsRequest.getCoverageNames() ) {
            if ( !grantedCoverages.contains( coverageName ) )
                return new AuthorizationReport( DESCRIBECOVERAGE_UNAUTHORIZED_MSG );

        }
        return new AuthorizationReport( ACCESS_GRANTED_MSG, AUTHORIZED, internalServiceUrl );
    }

    private AuthorizationReport authorizeGetCoverage( WcsRequest wcsRequest,
                                                      Collection<? extends GrantedAuthority> authorities ) {
        for ( GrantedAuthority authority : authorities ) {
            if ( authority instanceof WcsPermission ) {
                WcsPermission wcsPermission = (WcsPermission) authority;
                if ( isFirstCoverageNameAuthorized( wcsRequest, wcsPermission )
                     && isOperationTypeAuthorized( wcsRequest, wcsPermission )
                     && isServiceVersionAuthorized( wcsRequest, wcsPermission )
                     && isServiceNameAuthorized( wcsRequest, wcsPermission ) ) {
                    return new AuthorizationReport( ACCESS_GRANTED_MSG, AUTHORIZED,
                                                    wcsPermission.getInternalServiceUrl() );
                }
            }
        }
        return new AuthorizationReport( GETCOVERAGE_UNAUTHORIZED_MSG );
    }

    private AuthorizationReport authorizeGetCapabilities( WcsRequest wcsRequest,
                                                          Collection<? extends GrantedAuthority> authorities ) {
        for ( GrantedAuthority authority : authorities ) {
            if ( authority instanceof WcsPermission ) {
                WcsPermission wcsPermission = (WcsPermission) authority;
                if ( isOperationTypeAuthorized( wcsRequest, wcsPermission )
                     && isServiceVersionAuthorized( wcsRequest, wcsPermission )
                     && isServiceNameAuthorized( wcsRequest, wcsPermission ) ) {
                    return new AuthorizationReport( ACCESS_GRANTED_MSG, AUTHORIZED,
                                                    wcsPermission.getInternalServiceUrl() );
                }
            }
        }
        return new AuthorizationReport( GETCAPABILITIES_UNAUTHORIZED_MSG );
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

    private boolean isServiceNameAuthorized( WcsRequest wcsRequest, WcsPermission wcsPermission ) {
        if ( wcsRequest.getServiceName() != null )
            return wcsRequest.getServiceName().equals( wcsPermission.getServiceName() );
        return false;
    }
}

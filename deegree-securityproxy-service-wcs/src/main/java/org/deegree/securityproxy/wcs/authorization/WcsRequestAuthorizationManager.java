package org.deegree.securityproxy.wcs.authorization;

import static org.deegree.securityproxy.wcs.request.WcsRequestParser.DESCRIBECOVERAGE;
import static org.deegree.securityproxy.wcs.request.WcsRequestParser.GETCAPABILITIES;
import static org.deegree.securityproxy.wcs.request.WcsRequestParser.GETCOVERAGE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.deegree.securityproxy.authentication.ows.WcsPermission;
import org.deegree.securityproxy.authentication.ows.domain.LimitedOwsServiceVersion;
import org.deegree.securityproxy.authentication.ows.domain.OwsServiceVersion;
import org.deegree.securityproxy.authorization.RequestAuthorizationManager;
import org.deegree.securityproxy.authorization.logging.AuthorizationReport;
import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.wcs.request.WcsRequest;
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

    private static final String NOT_AUTHENTICATED_ERROR_MSG = "Error while retrieving authentication! "
                                                              + "User could not be authenticated.";

    private static final String ACCESS_GRANTED_MSG = "Access granted.";

    private static final String UNKNOWN_ERROR_MSG = "Unknown error. See application log for details.";

    private static final String DESCRIBECOVERAGE_UNAUTHORIZED_MSG = "User not permitted to perform operation "
                                                                    + "DescribeCoverage with the given parameters";

    public static final String GETCOVERAGE_UNAUTHORIZED_MSG = "User not permitted to perform operation GetCoverage "
                                                              + "with the given parameters";

    public static final String GETCAPABILITIES_UNAUTHORIZED_MSG = "User not permitted to perform operation "
                                                                  + "GetCapabilities with the given parameters";

    @Override
    public AuthorizationReport decide( Authentication authentication, OwsRequest request ) {
        if ( !checkAuthentication( authentication ) ) {
            return new AuthorizationReport( NOT_AUTHENTICATED_ERROR_MSG );
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        WcsRequest wcsRequest = (WcsRequest) request;
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
        Map<String, String[]> additionalKeyValuePairs = null;
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
                    additionalKeyValuePairs = wcsPermission.getAdditionalKeyValuePairs();
                }
            }
        }
        for ( String coverageName : wcsRequest.getCoverageNames() ) {
            if ( !grantedCoverages.contains( coverageName ) )
                return new AuthorizationReport( DESCRIBECOVERAGE_UNAUTHORIZED_MSG );

        }
        return new AuthorizationReport( ACCESS_GRANTED_MSG, AUTHORIZED, internalServiceUrl, additionalKeyValuePairs );
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
                                                    wcsPermission.getInternalServiceUrl(),
                                                    wcsPermission.getAdditionalKeyValuePairs() );
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
                                                    wcsPermission.getInternalServiceUrl(),
                                                    wcsPermission.getAdditionalKeyValuePairs() );
                }
            }
        }
        return new AuthorizationReport( GETCAPABILITIES_UNAUTHORIZED_MSG );
    }

    private boolean isDescribeCoverageRequest( WcsRequest wcsRequest ) {
        return DESCRIBECOVERAGE.equals( wcsRequest.getOperationType() );
    }

    private boolean isGetCoverageRequest( WcsRequest wcsRequest ) {
        return GETCOVERAGE.equals( wcsRequest.getOperationType() );
    }

    private boolean isGetCapabilitiesRequest( WcsRequest wcsRequest ) {
        return GETCAPABILITIES.equals( wcsRequest.getOperationType() );
    }

    private boolean isOperationTypeAuthorized( WcsRequest wcsRequest, WcsPermission wcsPermission ) {
        return wcsRequest.getOperationType() != null
               && wcsRequest.getOperationType().equalsIgnoreCase( wcsPermission.getOperationType() );
    }

    private boolean isServiceVersionAuthorized( WcsRequest wcsRequest, WcsPermission wcsPermission ) {
        OwsServiceVersion requestedServiceVersion = wcsRequest.getServiceVersion();
        if(requestedServiceVersion == null)
            return false;
        LimitedOwsServiceVersion serviceVersionLimit = wcsPermission.getServiceVersion();
        return serviceVersionLimit.contains( requestedServiceVersion );
    }

    private boolean isFirstCoverageNameAuthorized( WcsRequest wcsRequest, WcsPermission wcsPermission ) {
        if ( !wcsRequest.getCoverageNames().isEmpty() ) {
            String firstCoverage = wcsRequest.getCoverageNames().get( 0 );
            return firstCoverage.equals( wcsPermission.getCoverageName() );
        }
        return false;
    }

    private boolean isServiceNameAuthorized( WcsRequest wcsRequest, WcsPermission wcsPermission ) {
        return wcsRequest.getServiceName() != null
               && wcsRequest.getServiceName().equals( wcsPermission.getServiceName() );
    }
}

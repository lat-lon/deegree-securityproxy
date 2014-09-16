package org.deegree.securityproxy.wps.authorization;

import static org.deegree.securityproxy.wps.request.parser.WpsGetRequestParser.DESCRIBEPROCESS;
import static org.deegree.securityproxy.wps.request.parser.WpsGetRequestParser.EXECUTE;
import static org.deegree.securityproxy.wps.request.parser.WpsGetRequestParser.GETCAPABILITIES;

import java.util.Collection;

import org.deegree.securityproxy.authentication.ows.domain.LimitedOwsServiceVersion;
import org.deegree.securityproxy.authentication.ows.raster.RasterPermission;
import org.deegree.securityproxy.authorization.RequestAuthorizationManager;
import org.deegree.securityproxy.authorization.logging.AuthorizationReport;
import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.request.OwsServiceVersion;
import org.deegree.securityproxy.wps.request.WpsRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * Checks if a authenticated User is permitted to perform an incoming {@link javax.servlet.http.HttpServletRequest}
 * against a WPS.
 * 
 * @author <a href="wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author <a href="stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * @version $Revision: $, $Date: $
 */
public class WpsRequestAuthorizationManager implements RequestAuthorizationManager {

    public static final boolean AUTHORIZED = true;

    private static final String NOT_AUTHENTICATED_ERROR_MSG = "Error while retrieving authentication! "
                                                              + "User could not be authenticated.";

    private static final String UNKNOWN_ERROR_MSG = "Unknown error. See application log for details.";

    public static final String GETCAPABILITIES_UNAUTHORIZED_MSG = "User not permitted to perform operation "
                                                                  + "GetCapabilities with the given parameters";

    public static final String EXECUTE_UNAUTHORIZED_MSG = "User not permitted to perform operation "
                                                          + "Execute with the given parameters";

    public static final String DESCRIBEPROCESS_UNAUTHORIZED_MSG = "User not permitted to perform operation "
                                                                  + "DescribeProcess with the given parameters";

    private static final String ACCESS_GRANTED_MSG = "Access granted.";

    @Override
    public AuthorizationReport decide( Authentication authentication, OwsRequest request ) {
        if ( !checkAuthentication( authentication ) ) {
            return new AuthorizationReport( NOT_AUTHENTICATED_ERROR_MSG );
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        WpsRequest wpsRequest = (WpsRequest) request;
        if ( isExecuteRequest( wpsRequest ) ) {
            return authorizeExecute( wpsRequest, authorities );
        } else if ( isDescribeProcessRequest( wpsRequest ) ) {
            return authorizeDescribeProcess( wpsRequest, authorities );
        } else if ( isGetCapabilitiesRequest( wpsRequest ) ) {
            return authorizeGetCapabilities( wpsRequest, authorities );
        }
        return new AuthorizationReport( UNKNOWN_ERROR_MSG );
    }

    @Override
    public boolean supports( Class<?> clazz ) {
        return WpsRequest.class.equals( clazz );
    }

    private boolean checkAuthentication( Authentication authentication ) {
        return !( authentication instanceof AnonymousAuthenticationToken );
    }

    private boolean isGetCapabilitiesRequest( WpsRequest wpsRequest ) {
        return GETCAPABILITIES.equals( wpsRequest.getOperationType() );
    }

    private boolean isExecuteRequest( WpsRequest wpsRequest ) {
        return EXECUTE.equals( wpsRequest.getOperationType() );
    }

    private boolean isDescribeProcessRequest( WpsRequest wpsRequest ) {
        return DESCRIBEPROCESS.equals( wpsRequest.getOperationType() );
    }

    private AuthorizationReport authorizeGetCapabilities( WpsRequest wpsRequest,
                                                          Collection<? extends GrantedAuthority> authorities ) {
        return authorizeBaseParams( wpsRequest, authorities, GETCAPABILITIES_UNAUTHORIZED_MSG );
    }

    private AuthorizationReport authorizeExecute( WpsRequest wpsRequest,
                                                  Collection<? extends GrantedAuthority> authorities ) {
        AuthorizationReport authorizationReport = authorizeBaseParams( wpsRequest, authorities,
                                                                       EXECUTE_UNAUTHORIZED_MSG );
        return authorizeProcessIds( wpsRequest, authorities, authorizationReport, EXECUTE_UNAUTHORIZED_MSG );
    }

    private AuthorizationReport authorizeDescribeProcess( WpsRequest wpsRequest,
                                                          Collection<? extends GrantedAuthority> authorities ) {
        AuthorizationReport authorizationReport = authorizeBaseParams( wpsRequest, authorities,
                                                                       DESCRIBEPROCESS_UNAUTHORIZED_MSG );
        return authorizeProcessIds( wpsRequest, authorities, authorizationReport, DESCRIBEPROCESS_UNAUTHORIZED_MSG );
    }

    private AuthorizationReport authorizeBaseParams( WpsRequest wpsRequest,
                                                     Collection<? extends GrantedAuthority> authorities,
                                                     String unauthorisedMsg ) {
        for ( GrantedAuthority authority : authorities ) {
            if ( authority instanceof RasterPermission ) {
                RasterPermission wpsPermission = (RasterPermission) authority;
                if ( areBaseParamsAuthorized( wpsRequest, wpsPermission ) ) {
                    return new AuthorizationReport( ACCESS_GRANTED_MSG, AUTHORIZED,
                                    wpsPermission.getInternalServiceUrl(), wpsPermission.getAdditionalKeyValuePairs() );
                }
            }
        }
        return new AuthorizationReport( unauthorisedMsg );
    }

    private AuthorizationReport authorizeProcessIds( WpsRequest wpsRequest,
                                                     Collection<? extends GrantedAuthority> authorities,
                                                     AuthorizationReport authorizationReport, String unauthorisedMsg ) {
        if ( authorizationReport.isAuthorized() ) {
            boolean isProcessIdAuthorized = isProcessIdAuthorized( wpsRequest, authorities );
            if ( !isProcessIdAuthorized )
                return new AuthorizationReport( unauthorisedMsg );
        }
        return authorizationReport;
    }

    private boolean areBaseParamsAuthorized( WpsRequest wpsRequest, RasterPermission wpsPermission ) {
        return isServiceTypeAuthorized( wpsRequest, wpsPermission )
               && isOperationTypeAuthorized( wpsRequest, wpsPermission )
               && isServiceVersionAuthorized( wpsRequest, wpsPermission );
    }

    private boolean isServiceTypeAuthorized( WpsRequest wpsRequest, RasterPermission wpsPermission ) {
        return wpsRequest.getServiceType() != null
               && wpsRequest.getServiceType().equalsIgnoreCase( wpsPermission.getServiceType() );
    }

    private boolean isOperationTypeAuthorized( WpsRequest wpsRequest, RasterPermission wpsPermission ) {
        return wpsRequest.getOperationType() != null
               && wpsRequest.getOperationType().equalsIgnoreCase( wpsPermission.getOperationType() );
    }

    private boolean isServiceVersionAuthorized( WpsRequest wpsRequest, RasterPermission wpsPermission ) {
        OwsServiceVersion requestedServiceVersion = wpsRequest.getServiceVersion();
        if ( requestedServiceVersion == null )
            return false;
        LimitedOwsServiceVersion serviceVersionLimit = wpsPermission.getServiceVersion();
        return serviceVersionLimit.contains( requestedServiceVersion );
    }

    private boolean isProcessIdAuthorized( WpsRequest wpsRequest, Collection<? extends GrantedAuthority> authorities ) {
        for ( GrantedAuthority authority : authorities ) {
            if ( authority instanceof RasterPermission ) {
                RasterPermission wpsPermission = (RasterPermission) authority;
                if ( wpsRequest.getOperationType().equalsIgnoreCase( wpsPermission.getOperationType() )
                     && wpsRequest.getIdentifiers().contains( wpsPermission.getLayerName() ) ) {
                    return true;
                }
            }
        }
        return false;
    }

}
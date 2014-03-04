package org.deegree.securityproxy.wms.authorization;

import static org.deegree.securityproxy.wms.request.WmsRequestParser.GETCAPABILITIES;
import static org.deegree.securityproxy.wms.request.WmsRequestParser.GETFEATUREINFO;
import static org.deegree.securityproxy.wms.request.WmsRequestParser.GETMAP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.deegree.securityproxy.authentication.ows.domain.LimitedOwsServiceVersion;
import org.deegree.securityproxy.authentication.ows.raster.RasterPermission;
import org.deegree.securityproxy.authorization.RequestAuthorizationManager;
import org.deegree.securityproxy.authorization.logging.AuthorizationReport;
import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.request.OwsServiceVersion;
import org.deegree.securityproxy.wms.request.WmsRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * Checks if a authenticated User is permitted to perform an incoming {@link javax.servlet.http.HttpServletRequest}
 * against a WMS.
 * 
 * @author <a href="stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * @version $Revision: $, $Date: $
 */
public class WmsRequestAuthorizationManager implements RequestAuthorizationManager {

    public static final boolean AUTHORIZED = true;

    private static final String NOT_AUTHENTICATED_ERROR_MSG = "Error while retrieving authentication! "
                                                              + "User could not be authenticated.";

    private static final String ACCESS_GRANTED_MSG = "Access granted.";

    private static final String UNKNOWN_ERROR_MSG = "Unknown error. See application log for details.";

    private static final String GETFEATUREINFO_UNAUTHORIZED_MSG = "User not permitted to perform operation "
                                                                  + "GetFeatureInfo with the given parameters";

    public static final String GETMAP_UNAUTHORIZED_MSG = "User not permitted to perform operation GetMap "
                                                         + "with the given parameters";

    public static final String GETCAPABILITIES_UNAUTHORIZED_MSG = "User not permitted to perform operation "
                                                                  + "GetCapabilities with the given parameters";

    @Override
    public AuthorizationReport decide( Authentication authentication, OwsRequest request ) {
        if ( !checkAuthentication( authentication ) ) {
            return new AuthorizationReport( NOT_AUTHENTICATED_ERROR_MSG );
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        WmsRequest wmsRequest = (WmsRequest) request;
        if ( isGetMapRequest( wmsRequest ) ) {
            return authorizeGetMap( wmsRequest, authorities );
        } else if ( isGetFeatureInfoRequest( wmsRequest ) ) {
            return authorizeGetFeatureInfo( wmsRequest, authorities );
        } else if ( isGetCapabilitiesRequest( wmsRequest ) ) {
            return authorizeGetCapabilities( wmsRequest, authorities );
        }
        return new AuthorizationReport( UNKNOWN_ERROR_MSG );
    }

    private boolean checkAuthentication( Authentication authentication ) {
        return !( authentication instanceof AnonymousAuthenticationToken );
    }

    @Override
    public boolean supports( Class<?> clazz ) {
        return WmsRequest.class.equals( clazz );
    }

    private AuthorizationReport authorizeGetFeatureInfo( WmsRequest wmsRequest,
                                                         Collection<? extends GrantedAuthority> authorities ) {
        List<String> grantedLayers = new ArrayList<String>();
        String internalServiceUrl = null;
        Map<String, String[]> additionalKeyValuePairs = null;
        for ( GrantedAuthority authority : authorities ) {
            if ( authority instanceof RasterPermission ) {
                RasterPermission wmsPermission = (RasterPermission) authority;
                if ( areCommonParamsAuthorized( wmsRequest, wmsPermission ) ) {
                    grantedLayers.add( wmsPermission.getLayerName() );
                    // If there are data inconsistencies and a service-name is mapped to different internal-urls, the
                    // DSP always chooses the url of the last permission.
                    internalServiceUrl = wmsPermission.getInternalServiceUrl();
                    additionalKeyValuePairs = wmsPermission.getAdditionalKeyValuePairs();
                }
            }
        }
        for ( String layerName : wmsRequest.getLayerNames() ) {
            if ( !grantedLayers.contains( layerName ) )
                return new AuthorizationReport( GETFEATUREINFO_UNAUTHORIZED_MSG );
        }
        for ( String queryLayerName : wmsRequest.getQueryLayerNames() ) {
            if ( !grantedLayers.contains( queryLayerName ) )
                return new AuthorizationReport( GETFEATUREINFO_UNAUTHORIZED_MSG );
        }
        return new AuthorizationReport( ACCESS_GRANTED_MSG, AUTHORIZED, internalServiceUrl, additionalKeyValuePairs );
    }

    private AuthorizationReport authorizeGetMap( WmsRequest wmsRequest,
                                                 Collection<? extends GrantedAuthority> authorities ) {
        for ( GrantedAuthority authority : authorities ) {
            if ( authority instanceof RasterPermission ) {
                RasterPermission wmsPermission = (RasterPermission) authority;
                if ( isFirstLayerNameAuthorized( wmsRequest, wmsPermission )
                     && areCommonParamsAuthorized( wmsRequest, wmsPermission ) ) {
                    return createAuthorizedReport( wmsPermission );
                }
            }
        }
        return new AuthorizationReport( GETMAP_UNAUTHORIZED_MSG );
    }

    private AuthorizationReport authorizeGetCapabilities( WmsRequest wmsRequest,
                                                          Collection<? extends GrantedAuthority> authorities ) {
        for ( GrantedAuthority authority : authorities ) {
            if ( authority instanceof RasterPermission ) {
                RasterPermission wmsPermission = (RasterPermission) authority;
                if ( areCommonParamsAuthorized( wmsRequest, wmsPermission ) ) {
                    return createAuthorizedReport( wmsPermission );
                }
            }
        }
        return new AuthorizationReport( GETCAPABILITIES_UNAUTHORIZED_MSG );
    }

    private AuthorizationReport createAuthorizedReport( RasterPermission wmsPermission ) {
        String internalServiceUrl = wmsPermission.getInternalServiceUrl();
        Map<String, String[]> additionalKVPs = wmsPermission.getAdditionalKeyValuePairs();
        return new AuthorizationReport( ACCESS_GRANTED_MSG, AUTHORIZED, internalServiceUrl, additionalKVPs );
    }

    private boolean areCommonParamsAuthorized( WmsRequest wmsRequest, RasterPermission wmsPermission ) {
        return isServiceTypeAuthorized( wmsRequest, wmsPermission )
               && isOperationTypeAuthorized( wmsRequest, wmsPermission )
               && isServiceVersionAuthorized( wmsRequest, wmsPermission )
               && isServiceNameAuthorized( wmsRequest, wmsPermission );
    }

    private boolean isGetFeatureInfoRequest( WmsRequest wmsRequest ) {
        return GETFEATUREINFO.equals( wmsRequest.getOperationType() );
    }

    private boolean isGetMapRequest( WmsRequest wmsRequest ) {
        return GETMAP.equals( wmsRequest.getOperationType() );
    }

    private boolean isGetCapabilitiesRequest( WmsRequest wmsRequest ) {
        return GETCAPABILITIES.equals( wmsRequest.getOperationType() );
    }

    private boolean isServiceTypeAuthorized( WmsRequest wmsRequest, RasterPermission wmsPermission ) {
        return wmsRequest.getServiceType() != null
               && wmsRequest.getServiceType().equalsIgnoreCase( wmsPermission.getServiceType() );
    }

    private boolean isOperationTypeAuthorized( WmsRequest wmsRequest, RasterPermission wmsPermission ) {
        return wmsRequest.getOperationType() != null
               && wmsRequest.getOperationType().equalsIgnoreCase( wmsPermission.getOperationType() );
    }

    private boolean isServiceVersionAuthorized( WmsRequest wmsRequest, RasterPermission wmsPermission ) {
        OwsServiceVersion requestedServiceVersion = wmsRequest.getServiceVersion();
        if ( requestedServiceVersion == null )
            return false;
        LimitedOwsServiceVersion serviceVersionLimit = wmsPermission.getServiceVersion();
        return serviceVersionLimit.contains( requestedServiceVersion );
    }

    private boolean isFirstLayerNameAuthorized( WmsRequest wmsRequest, RasterPermission wmsPermission ) {
        if ( !wmsRequest.getLayerNames().isEmpty() ) {
            String firstLayer = wmsRequest.getLayerNames().get( 0 );
            return firstLayer.equals( wmsPermission.getLayerName() );
        }
        return false;
    }

    private boolean isServiceNameAuthorized( WmsRequest wmsRequest, RasterPermission wmsPermission ) {
        return wmsRequest.getServiceName() != null
               && wmsRequest.getServiceName().equals( wmsPermission.getServiceName() );
    }

}
package org.deegree.securityproxy.authorization.wcs;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.deegree.securityproxy.authentication.wcs.WcsPermission;
import org.deegree.securityproxy.commons.WcsOperationType;
import org.deegree.securityproxy.request.WcsRequest;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
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
public class WcsRequestAuthorizationManager implements AccessDecisionManager {

    @Override
    public void decide( Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes )
                            throws AccessDeniedException, InsufficientAuthenticationException {
        WcsRequest wcsRequest = (WcsRequest) object;
        if ( authentication == null )
            throw new AccessDeniedException( "Not authenticated!" );
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for ( GrantedAuthority authority : authorities ) {
            if ( isAuthorized( authority, wcsRequest ) ) {
                return;
            }
        }
        // Shall support Authentication instances that contain WcsPermission(s) as GrantedAuthority(ies)
        throw new AccessDeniedException( "Unauthorized" );
    }

    private boolean isAuthorized( GrantedAuthority authority, WcsRequest wcsRequest ) {
        if ( authority instanceof WcsPermission ) {
            WcsPermission wcsPermission = (WcsPermission) authority;

            if ( isGetCoverageRequest( wcsRequest ) || isDescribeCoverageRequest( wcsRequest ) ) {
                if ( !isCoverageNameAuthorized( wcsRequest, wcsPermission ) )
                    return false;
            }
            if ( isOperationTypeAuthorized( wcsRequest, wcsPermission )
                 && isServiceVersionAuthorized( wcsRequest, wcsPermission ) ) {
                return true;
            }

        }
        return false;
    }

    private boolean isDescribeCoverageRequest( WcsRequest wcsRequest ) {
        return WcsOperationType.DESCRIBECOVERAGE.equals( wcsRequest.getOperationType() );

    }

    private boolean isGetCoverageRequest( WcsRequest wcsRequest ) {
        return WcsOperationType.GETCOVERAGE.equals( wcsRequest.getOperationType() );
    }

    private boolean isOperationTypeAuthorized( WcsRequest wcsRequest, WcsPermission wcsPermission ) {
        if ( wcsRequest.getOperationType() != null )
            return wcsRequest.getOperationType().equals( wcsPermission.getOperationType() );
        else
            return false;
    }

    private boolean isServiceVersionAuthorized( WcsRequest wcsRequest, WcsPermission wcsPermission ) {
        if ( wcsRequest.getServiceVersion() != null )
            return wcsRequest.getServiceVersion().equals( wcsPermission.getServiceVersion() );
        else
            return false;
    }

    private boolean isCoverageNameAuthorized( WcsRequest wcsRequest, WcsPermission wcsPermission ) {
        if ( wcsRequest.getCoverageName() != null )
            return wcsRequest.getCoverageName().equals( wcsPermission.getLayerName() );
        else
            return false;
    }

    @Override
    public boolean supports( ConfigAttribute attribute ) {
        // Not needed in this implementation.
        return true;
    }

    @Override
    public boolean supports( Class<?> clazz ) {
        return WcsRequest.class.equals( clazz );
    }

}
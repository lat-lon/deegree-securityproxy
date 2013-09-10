package org.deegree.securityproxy.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.deegree.securityproxy.authorization.wcs.WcsRequestAuthorizationManager;
import org.deegree.securityproxy.request.UnsupportedRequestTypeException;
import org.deegree.securityproxy.request.WcsRequest;
import org.deegree.securityproxy.request.WcsRequestParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthorizationFilter implements Filter {

    @Autowired
    private WcsRequestAuthorizationManager requestAuthorizationManager;

    @Autowired
    private WcsRequestParser parser;

    @Override
    public void init( FilterConfig filterConfig )
                            throws ServletException {
    }

    @Override
    public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain )
                            throws IOException, ServletException {
        try {
            WcsRequest wcsRequest = parser.parse( (HttpServletRequest) request );
            requestAuthorizationManager.decide( SecurityContextHolder.getContext().getAuthentication(), wcsRequest,
                                                null );
        } catch ( UnsupportedRequestTypeException e ) {
            throw new AccessDeniedException( "Unsupported service type!" );
        } catch ( IllegalArgumentException e ) {
            throw new AccessDeniedException( "Unauthorized!" );
        }
    }

    @Override
    public void destroy() {
    }

}

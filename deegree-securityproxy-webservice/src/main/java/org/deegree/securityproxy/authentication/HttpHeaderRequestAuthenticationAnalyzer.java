package org.deegree.securityproxy.authentication;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;

public class HttpHeaderRequestAuthenticationAnalyzer implements RequestAuthenticationAnalyzer {

    private final String headerKey;

    public HttpHeaderRequestAuthenticationAnalyzer( String headerKey ) {
        if ( headerKey == null )
            throw new IllegalArgumentException( "Header key must not be null!" );
        if ( "".equals( headerKey ) )
            throw new IllegalArgumentException( "Header key must not be empty!" );
        this.headerKey = headerKey;
    }

    @Override
    public Authentication provideAuthenticationFromHttpRequest( final HttpServletRequest request ) {
        String headerValue = parseRequestHeaderForAccessToken( request );
        return generateAuthentication( headerValue );
    }

    private String parseRequestHeaderForAccessToken( final HttpServletRequest request ) {
        return request.getHeader( headerKey );
    }

    private Authentication generateAuthentication( String headerValue ) {
        return new HeaderAuthenticationToken( headerValue );
    }

}

package org.deegree.securityproxy.authentication;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;

/**
 * Analyzes an incoming {@link HttpRequests} and extracts the header for the provided key to store it in an
 * {@link Authentication} instance.
 * 
 * @author <a href="goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
public class HttpHeaderRequestAuthenticationAnalyzer implements RequestAuthenticationAnalyzer {

    private final String headerKey;

    /**
     * Set the HTTP header key that is used to retrieve the token value.
     * 
     * @param headerKey
     *            never <code>null</code> and never empty.
     * @throws IllegalArgumentException
     *             if header key is <code>null</code> or empty.
     */
    public HttpHeaderRequestAuthenticationAnalyzer( String headerKey ) throws IllegalArgumentException {
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

//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2014 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact information:

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/
package org.deegree.securityproxy.authentication.wass;

import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Logger;
import org.deegree.securityproxy.filter.KvpRequestWrapper;
import org.deegree.securityproxy.request.HttpServletRequestBodyWrapper;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

/**
 * {@link AbstractAuthenticationProcessingFilter} implementation using a {@link AuthenticationManager} to authenticate a
 * anonymous user. All requests are authenticated. After authentication the credentials are added as parameter to the
 * request.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class AddParameterAnonymousAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final Logger LOG = Logger.getLogger( AddParameterAnonymousAuthenticationFilter.class );

    private static final String ANONYMOUS_USER = "anonymousUser";

    private final String parameterKey;

    private final PostStrategy postStrategy;

    /**
     * @param parameterKey
     *            the name of the parameter added to GET requests, if <code>null</code> GET requests are not modified
     * @param postStrategy
     *            a strategy encapsulating how to modify post requests, if <code>null</code> POST requests are not
     *            modified
     */
    public AddParameterAnonymousAuthenticationFilter( String parameterKey, PostStrategy postStrategy ) {
        // path is not used, cause method #requiresAuthentication() authentication is overwritten!
        super( "/unusedPath" );
        this.parameterKey = parameterKey;
        this.postStrategy = postStrategy;
    }

    @Override
    public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain )
                            throws IOException, ServletException {
        HttpServletRequest wrappedRequest = wrapRequest( (HttpServletRequest) request );
        super.doFilter( wrappedRequest, response, chain );
    }

    @Override
    protected boolean requiresAuthentication( HttpServletRequest request, HttpServletResponse response ) {
        return true;
    }

    @Override
    public Authentication attemptAuthentication( HttpServletRequest request, HttpServletResponse response )
                            throws AuthenticationException, IOException, ServletException {
        Authentication authRequest = new AnonymousAuthenticationToken( ANONYMOUS_USER, ANONYMOUS_USER,
                                                                       createAuthorityList( "ROLE_ANONYMOUS" ) );
        return this.getAuthenticationManager().authenticate( authRequest );
    }

    @Override
    protected void successfulAuthentication( HttpServletRequest request, HttpServletResponse response,
                                             FilterChain chain, Authentication authResult )
                            throws IOException, ServletException {
        LOG.debug( "Authentication was successful, request will be modified if required!" );
        String method = request.getMethod();
        if ( isGetRequestedAndSupported( method ) )
            addParameter( request, authResult );
        else if ( isPostRequestedAndSupported( method ) ) {
            modifyPostBody( request, authResult );
        }
        chain.doFilter( request, response );
    }

    private HttpServletRequest wrapRequest( HttpServletRequest request )
                            throws ServletException {
        String method = request.getMethod();
        System.out.println( method );
        if ( isGetRequestedAndSupported( method ) ) {
            LOG.debug( "Retrieved GET request with query string " + request.getQueryString() );
            return new KvpRequestWrapper( request );
        } else if ( isPostRequestedAndSupported( method ) ) {
            LOG.debug( "Retrieved POST request with query string " + request.getQueryString() );
            try {
                return new HttpServletRequestBodyWrapper( request );
            } catch ( IOException e ) {
                throw new ServletException( e );
            }
        }
        return request;
    }

    private void addParameter( HttpServletRequest request, Authentication authResult ) {
        String parameterValue = extractParameterValue( authResult );
        ( (KvpRequestWrapper) request ).addParameter( parameterKey, parameterValue );
    }

    private void modifyPostBody( HttpServletRequest request, Authentication authResult )
                            throws IOException, ServletException {
        String parameterValue = extractParameterValue( authResult );
        if ( parameterValue != null && postStrategy != null ) {
            applyPostStrategy( (HttpServletRequestBodyWrapper) request, parameterValue );
        }
    }

    private void applyPostStrategy( HttpServletRequestBodyWrapper requestWrapper, String parameterValue )
                            throws ServletException, IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ServletInputStream requestAsStream = requestWrapper.getInputStream();
        try {
            postStrategy.modifyPostRequest( requestAsStream, outputStream, parameterValue );
            outputStream.flush();
            ByteArrayInputStream modifiedRequest = new ByteArrayInputStream( outputStream.toByteArray() );
            requestWrapper.renewInputStream( modifiedRequest );
        } catch ( XMLStreamException e ) {
            throw new ServletException( e );
        } finally {
            closeQuietly( outputStream );
            closeQuietly( requestAsStream );
        }
    }

    private String extractParameterValue( Authentication authResult ) {
        Object credentials = authResult.getCredentials();
        if ( credentials != null && credentials instanceof String ) {
            LOG.debug( "authentication parameter " + parameterKey + "=" + credentials );
            return (String) credentials;
        }
        return null;
    }

    private boolean isGetRequestedAndSupported( String method ) {
        return "GET".equals( method ) && parameterKey != null && !parameterKey.isEmpty();
    }

    private boolean isPostRequestedAndSupported( String method ) {
        return "POST".equals( method ) && postStrategy != null;
    }

}
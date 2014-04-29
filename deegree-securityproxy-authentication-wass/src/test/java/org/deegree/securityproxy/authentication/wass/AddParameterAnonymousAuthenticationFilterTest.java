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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.FilterChain;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.IOUtils;
import org.deegree.securityproxy.filter.KvpRequestWrapper;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class AddParameterAnonymousAuthenticationFilterTest {

    private static final String PARAMETER_KEY = "paramKey";

    private static final String PARAMETER_VALUE = "paramValue";

    @Test
    public void testDoFilterWithGetRequest()
                            throws Exception {
        AddParameterAnonymousAuthenticationFilter filter = createFilter( PARAMETER_VALUE );
        FilterChain filterChain = mockChain();
        HttpServletResponse response = mockResponse();
        filter.doFilter( mockGetRequest(), response, filterChain );

        ArgumentCaptor<ServletRequest> argument = ArgumentCaptor.forClass( ServletRequest.class );
        verify( filterChain ).doFilter( argument.capture(), eq( response ) );
        KvpRequestWrapper passedRequest = (KvpRequestWrapper) argument.getValue();

        assertThat( passedRequest.getParameter( PARAMETER_KEY ), is( PARAMETER_VALUE ) );
    }

    @Test
    public void testDoFilterWithGetRequestWithoutGetSupport()
                            throws Exception {
        AddParameterAnonymousAuthenticationFilter filter = createFilterWithPostSupport( PARAMETER_VALUE );
        FilterChain filterChain = mockChain();
        HttpServletResponse response = mockResponse();
        HttpServletRequest request = mockGetRequest();
        filter.doFilter( request, response, filterChain );

        verify( filterChain ).doFilter( request, response );
    }

    @Test
    public void testDoFilterWithGetRequestAndNullAuthenticationCredentials()
                            throws Exception {
        AddParameterAnonymousAuthenticationFilter filter = createFilter( null );
        FilterChain filterChain = mockChain();
        HttpServletResponse response = mockResponse();
        filter.doFilter( mockGetRequest(), response, filterChain );

        ArgumentCaptor<ServletRequest> argument = ArgumentCaptor.forClass( ServletRequest.class );
        verify( filterChain ).doFilter( argument.capture(), eq( response ) );
        KvpRequestWrapper passedRequest = (KvpRequestWrapper) argument.getValue();

        assertThat( passedRequest.getParameter( PARAMETER_KEY ), is( nullValue() ) );
    }

    @Test
    public void testDoFilterWithPostRequest()
                            throws Exception {
        AddParameterAnonymousAuthenticationFilter filter = createFilter( PARAMETER_VALUE );
        FilterChain filterChain = mockChain();
        HttpServletResponse response = mockResponse();

        filter.doFilter( mockPostRequest(), response, filterChain );

        ArgumentCaptor<ServletRequest> argument = ArgumentCaptor.forClass( ServletRequest.class );
        verify( filterChain ).doFilter( argument.capture(), eq( response ) );
        HttpServletRequest passedRequest = (HttpServletRequest) argument.getValue();

        String requestAsString = IOUtils.toString( passedRequest.getInputStream() );

        assertThat( requestAsString, is( PARAMETER_VALUE ) );
    }

    @Test
    public void testDoFilterWithPostRequestWithoutPostSupport()
                            throws Exception {
        AddParameterAnonymousAuthenticationFilter filter = createFilterWithGetSupport( PARAMETER_VALUE );
        FilterChain filterChain = mockChain();
        HttpServletResponse response = mockResponse();

        HttpServletRequest request = mockPostRequest();
        filter.doFilter( request, response, filterChain );

        verify( filterChain ).doFilter( request, response );
    }

    @Test
    public void testDoFilterWithUnsupportedMethod()
                            throws Exception {
        String parameterValue = "paramValue";
        AddParameterAnonymousAuthenticationFilter filter = createFilterWithGetSupport( parameterValue );
        FilterChain filterChain = mockChain();
        HttpServletResponse response = mockResponse();
        HttpServletRequest request = mockRequest( "PUT" );
        filter.doFilter( request, response, filterChain );

        verify( filterChain ).doFilter( request, response );
    }

    private HttpServletRequest mockGetRequest() {
        return mockRequest( "GET" );
    }

    private HttpServletRequest mockPostRequest()
                            throws IOException {
        HttpServletRequest postRequest = mockRequest( "POST" );
        final ByteArrayInputStream inputStream = new ByteArrayInputStream( "oldValue".getBytes() );
        ServletInputStream servletInputStream = new ServletInputStream() {
            @Override
            public int read()
                                    throws IOException {
                return inputStream.read();
            }
        };
        when( postRequest.getInputStream() ).thenReturn( servletInputStream );
        return postRequest;
    }

    private HttpServletRequest mockRequest( String method ) {
        HttpServletRequest mock = mock( HttpServletRequest.class );
        when( mock.getMethod() ).thenReturn( method );
        return mock;
    }

    private HttpServletResponse mockResponse() {
        return mock( HttpServletResponse.class );
    }

    private FilterChain mockChain() {
        return mock( FilterChain.class );
    }

    private AddParameterAnonymousAuthenticationFilter createFilter( String credentials ) {
        PostStrategy postStrategy = createPostStrategy( credentials );
        return createFilter( PARAMETER_KEY, postStrategy, credentials );
    }

    private AddParameterAnonymousAuthenticationFilter createFilterWithGetSupport( String credentials ) {
        return createFilter( PARAMETER_KEY, null, credentials );
    }

    private AddParameterAnonymousAuthenticationFilter createFilterWithPostSupport( String credentials ) {
        PostStrategy postStrategy = createPostStrategy( credentials );
        return createFilter( null, postStrategy, credentials );
    }

    private AddParameterAnonymousAuthenticationFilter createFilter( String parameterKey, PostStrategy postStrategy,
                                                                    String credentials ) {
        AddParameterAnonymousAuthenticationFilter filter = new AddParameterAnonymousAuthenticationFilter( parameterKey,
                                                                                                          postStrategy );
        AuthenticationManager authenticationManager = mockAuthentication( credentials );
        filter.setAuthenticationManager( authenticationManager );
        return filter;
    }

    private PostStrategy createPostStrategy( final String credentials ) {
        PostStrategy postStrategy = new PostStrategy() {
            @Override
            public void modifyPostRequest( InputStream originalStream, OutputStream modifiedStream,
                                           String parameterValue )
                                    throws XMLStreamException {
                try {
                    modifiedStream.write( credentials.getBytes() );
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        };
        return postStrategy;
    }

    private AuthenticationManager mockAuthentication( String credentials ) {
        AuthenticationManager authenticationManager = mock( AuthenticationManager.class );
        Authentication resultAuthentication = mock( Authentication.class );
        when( resultAuthentication.getCredentials() ).thenReturn( credentials );
        when( authenticationManager.authenticate( any( Authentication.class ) ) ).thenReturn( resultAuthentication );
        return authenticationManager;
    }

}
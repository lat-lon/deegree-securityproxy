//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2011 by:
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
package org.deegree.securityproxy.sessionid;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Requests a sessionId from a WASS.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class WassSessionIdRetriever implements SessionIdRetriever {

    private static final Logger LOG = Logger.getLogger( WassSessionIdRetriever.class );

    private final String baseUrl;

    public WassSessionIdRetriever( String baseUrl ) {
        this.baseUrl = baseUrl;
    }

    @Override
    public String retrieveSessionId( String userName, String password ) {
        checkParameters( userName, password );
        try {
            URI requestUri = createRequest( userName, password );
            return requestSessionId( requestUri );
        } catch ( ClientProtocolException e ) {
            LOG.error( "An error occured during requesting the session id.", e );
        } catch ( IOException e ) {
            LOG.error( "An error occured during requesting the session id.", e );
        } catch ( URISyntaxException e ) {
            LOG.error( "An error occured during requesting the session id.", e );
        }
        return null;
    }

    CloseableHttpClient createHttpClient() {
        return HttpClientBuilder.create().build();
    }

    private String requestSessionId( URI requestUri )
                            throws ClientProtocolException, IOException {
        LOG.info( "Request URI is " + requestUri );
        CloseableHttpResponse response = exceuteRequest( requestUri );
        try {
            StatusLine statusLine = response.getStatusLine();
            if ( statusLine.getStatusCode() == 200 ) {
                return parseResponse( response );
            }
            LOG.info( "Service does not response with status code 200: " + statusLine );
            return null;
        } finally {
            IOUtils.closeQuietly( response );
        }
    }

    private CloseableHttpResponse exceuteRequest( URI requestUri )
                            throws IOException, ClientProtocolException {
        HttpGet httpGet = createConfiguredHttpGet( requestUri );
        CloseableHttpClient httpClient = createHttpClient();
        return httpClient.execute( httpGet );
    }

    private HttpGet createConfiguredHttpGet( URI requestUri ) {
        HttpGet httpGet = new HttpGet( requestUri );
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout( 1000 ).setConnectTimeout( 1000 ).build();
        httpGet.setConfig( requestConfig );
        return httpGet;
    }

    private URI createRequest( String userName, String password )
                            throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder( baseUrl );
        uriBuilder.addParameter( "SERVICE", "WAS" );
        uriBuilder.addParameter( "REQUEST", "GetSession" );
        uriBuilder.addParameter( "VERSION", "1.0.0" );
        uriBuilder.addParameter( "AUTHMETHOD", "urn:x-gdi-nrw:authnMethod:1.0:password" );
        uriBuilder.addParameter( "CREDENTIALS", userName + "," + password );
        return uriBuilder.build();
    }

    private String parseResponse( CloseableHttpResponse httpResponse )
                            throws IOException {
        HttpEntity entity = httpResponse.getEntity();
        if ( entity != null ) {
            return parseEntity( entity );
        }
        LOG.info( "Response entity is null!" );
        return null;
    }

    private String parseEntity( HttpEntity entity )
                            throws IOException {
        String response = readResponse( entity );
        if ( isServiceException( response ) ) {
            LOG.info( "Response is a service exception: " + response );
            return null;
        }
        return response;
    }

    private String readResponse( HttpEntity entity )
                            throws IOException {
        InputStream content = entity.getContent();
        try {
            return IOUtils.toString( content, "UTF-8" );
        } finally {
            IOUtils.closeQuietly( content );
        }
    }

    private boolean isServiceException( String response ) {
        return response.contains( "ServiceExceptionReport" );
    }

    private void checkParameters( String userName, String password ) {
        if ( userName == null )
            throw new IllegalArgumentException( "User name must not be null!" );
        if ( password == null )
            throw new IllegalArgumentException( "Password must not be null!" );
    }

}

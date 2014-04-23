package org.deegree.securityproxy.sessionid;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class WassSessionIdRetrieverTest {

    private static final String BASE_URL = "http://www.WasSessionIdRetrieverTest.de/services";

    @Test
    public void testRetrieveSessionId()
                            throws Exception {
        SessionIdRetriever sessionIdRetriever = spyWassSessionIdRetriever();
        String sessionId = sessionIdRetriever.retrieveSessionId( "validName", "validPassword" );
        assertThat( sessionId, is( notNullValue() ) );
    }

    @Test
    public void testRetrieveSessionIdWithInvalidCredentials()
                            throws Exception {
        SessionIdRetriever sessionIdRetriever = spyWassSessionIdRetriever();
        String sessionId = sessionIdRetriever.retrieveSessionId( "invalidName", "invalidPassword" );
        assertThat( sessionId, is( nullValue() ) );
    }

    private WassSessionIdRetriever spyWassSessionIdRetriever()
                            throws ClientProtocolException, IOException {
        WassSessionIdRetriever wasSessionIdRetriever = new WassSessionIdRetriever( BASE_URL );
        WassSessionIdRetriever spiedWasSessionIdRetriever = spy( wasSessionIdRetriever );
        Mockito.doReturn( mockHttpClient() ).when( spiedWasSessionIdRetriever ).createHttpClient();
        return spiedWasSessionIdRetriever;
    }

    private CloseableHttpClient mockHttpClient()
                            throws ClientProtocolException, IOException {
        CloseableHttpClient httpClient = mock( CloseableHttpClient.class );
        CloseableHttpResponse response = mockResponse();
        when( httpClient.execute( Mockito.any( HttpUriRequest.class ) ) ).thenReturn( response );
        return httpClient;
    }

    private CloseableHttpResponse mockResponse()
                            throws IllegalStateException, IOException {
        CloseableHttpResponse response = mock( CloseableHttpResponse.class );
        StatusLine statusLine = new BasicStatusLine( HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK" );
        when( response.getStatusLine() ).thenReturn( statusLine );
        HttpEntity entity = mockEntity();
        when( response.getEntity() ).thenReturn( entity );
        return response;
    }

    private HttpEntity mockEntity()
                            throws IllegalStateException, IOException {
        HttpEntity entity = mock( HttpEntity.class );
        InputStream content = new ByteArrayInputStream( "test".getBytes() );
        when( entity.getContent() ).thenReturn( content );
        return entity;
    }

}
package org.deegree.securityproxy.sessionid;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class WasSessionIdRetrieverTest {

    private static final String BASE_URL = "http://www.WasSessionIdRetrieverTest.de/services";

    @Test
    public void testRetrieveSessionId()
                            throws Exception {
        SessionIdRetriever sessionIdRetriever = spyWasSessionIdRetriever();
        String sessionId = sessionIdRetriever.retrieveSessionId( "validName", "validPassword" );
        assertThat( sessionId, is( notNullValue() ) );
    }

    @Test
    public void testRetrieveSessionIdWithInvalidCredentials()
                            throws Exception {
        SessionIdRetriever sessionIdRetriever = spyWasSessionIdRetriever();
        String sessionId = sessionIdRetriever.retrieveSessionId( "invalidName", "invalidPassword" );
        assertThat( sessionId, is( nullValue() ) );
    }

    private WasSessionIdRetriever spyWasSessionIdRetriever()
                            throws ClientProtocolException, IOException {
        WasSessionIdRetriever wasSessionIdRetriever = new WasSessionIdRetriever( BASE_URL );
        WasSessionIdRetriever spiedWasSessionIdreIdRetriever = Mockito.spy( wasSessionIdRetriever );

        when( spiedWasSessionIdreIdRetriever.createHttpClient() ).thenReturn( mockHttpClient() );

        return spiedWasSessionIdreIdRetriever;
    }

    private CloseableHttpClient mockHttpClient()
                            throws ClientProtocolException, IOException {
        CloseableHttpClient httpClient = mock( CloseableHttpClient.class );
        CloseableHttpResponse response = mock( CloseableHttpResponse.class );

//         StatusLine statusLine = new BasicStatusLine( HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK" );
//         when( response.getStatusLine() ).thenReturn( statusLine );
        //
         when( response.getEntity() ).thenReturn( mockEntity() );
//        when( httpClient.execute( any( HttpUriRequest.class ) ) ).thenReturn( response, response );
        return httpClient;
    }

    private HttpEntity mockEntity() {
        HttpEntity entity = mock( HttpEntity.class );
        return entity;
    }

}
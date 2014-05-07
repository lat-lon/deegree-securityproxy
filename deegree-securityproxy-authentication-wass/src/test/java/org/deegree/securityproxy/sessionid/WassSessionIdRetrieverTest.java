package org.deegree.securityproxy.sessionid;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class WassSessionIdRetrieverTest {

    private static final String BASE_URL = "http://www.WasSessionIdRetrieverTest.de/services";

    private static final String VALID_CREDENTIALS = "validCredentials";

    private static final String INVALID_CREDENTIALS = "invalidCredentials";

    private static final String SESSION_ID = "sessionId";

    private static final String EXCEPTION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ServiceExceptionReport><ServiceException locator=\"unknown+location\">Lookup of user '[UP-ext]test' failed! A user with this name does not exist.</ServiceException></ServiceExceptionReport>";

    @Test
    public void testRetrieveSessionIdWithValidCredentialsShouldReturnSessionId()
                            throws Exception {
        SessionIdRetriever sessionIdRetriever = spyWassSessionIdRetriever( VALID_CREDENTIALS );
        String sessionId = sessionIdRetriever.retrieveSessionId( "validName", "validPassword" );
        assertThat( sessionId, is( notNullValue() ) );
        assertThat( sessionId, is( SESSION_ID ) );
    }

    @Test
    public void testRetrieveSessionIdWithInvalidCredentialsShouldReturnNull()
                            throws Exception {
        SessionIdRetriever sessionIdRetriever = spyWassSessionIdRetriever( INVALID_CREDENTIALS );
        String sessionId = sessionIdRetriever.retrieveSessionId( "invalidName", "invalidPassword" );
        assertThat( sessionId, is( nullValue() ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRetrieveSessionIdWithNullCredentialsShouldThrowIllegalArgumentException()
                            throws Exception {
        SessionIdRetriever sessionIdRetriever = spyWassSessionIdRetriever( INVALID_CREDENTIALS );
        sessionIdRetriever.retrieveSessionId( null, null );
    }

    private WassSessionIdRetriever spyWassSessionIdRetriever( String credentials )
                            throws IOException {
        WassSessionIdRetriever wasSessionIdRetriever = new WassSessionIdRetriever( BASE_URL );
        WassSessionIdRetriever spiedWasSessionIdRetriever = spy( wasSessionIdRetriever );
        doReturn( mockHttpClient( credentials ) ).when( spiedWasSessionIdRetriever ).createHttpClient();
        return spiedWasSessionIdRetriever;
    }

    private CloseableHttpClient mockHttpClient( String credentials )
                            throws IOException {
        CloseableHttpClient httpClient = mock( CloseableHttpClient.class );
        CloseableHttpResponse response = mockResponse( credentials );
        when( httpClient.execute( any( HttpUriRequest.class ) ) ).thenReturn( response );
        return httpClient;
    }

    private CloseableHttpResponse mockResponse( String credentials )
                            throws IllegalStateException, IOException {
        CloseableHttpResponse response = mock( CloseableHttpResponse.class );
        StatusLine statusLine = new BasicStatusLine( HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK" );
        when( response.getStatusLine() ).thenReturn( statusLine );
        HttpEntity entity = mockEntity( credentials );
        when( response.getEntity() ).thenReturn( entity );
        return response;
    }

    private HttpEntity mockEntity( String credentials )
                            throws IllegalStateException, IOException {
        HttpEntity entity = mock( HttpEntity.class );
        InputStream content;
        if ( VALID_CREDENTIALS.equals( credentials ) )
            content = new ByteArrayInputStream( SESSION_ID.getBytes() );
        else
            content = new ByteArrayInputStream( EXCEPTION.getBytes() );
        when( entity.getContent() ).thenReturn( content );
        return entity;
    }

}

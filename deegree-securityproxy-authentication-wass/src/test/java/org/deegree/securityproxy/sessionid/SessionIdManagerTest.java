package org.deegree.securityproxy.sessionid;

import org.junit.Test;

import static java.lang.Thread.sleep;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Tests for SessionIdManager.
 * 
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * 
 * @version $Revision: $, $Date: $
 */
public class SessionIdManagerTest {

    private static final String USER = "user";

    private static final String PASSWORD = "password";

    private static final int SAVE_TIME_OF_SESSION_ID = 999999;

    @Test
    public void testRetrieveSessionIdWithNegativeSaveTimeOfSessionIdShouldRetrieveNewSessionId()
                            throws Exception {
        SessionIdRetriever sessionIdRetriever = mockSessionIdRetriever();
        SessionIdManager sessionIdManager = new SessionIdManager( sessionIdRetriever, USER, PASSWORD, -1 );

        sessionIdManager.retrieveSessionId();

        verify( sessionIdRetriever ).retrieveSessionId( USER, PASSWORD );
    }

    @Test
    public void testRetrieveSessionIdForFirstTimeShouldRetrieveNewSessionId()
                            throws Exception {
        SessionIdRetriever sessionIdRetriever = mockSessionIdRetriever();
        SessionIdManager sessionIdManager = new SessionIdManager( sessionIdRetriever, USER, PASSWORD,
                                                                  SAVE_TIME_OF_SESSION_ID );

        sessionIdManager.retrieveSessionId();

        verify( sessionIdRetriever ).retrieveSessionId( USER, PASSWORD );
    }

    @Test
    public void testRetrieveSessionIdForThreeTimesShouldJustRetrieveNewSessionIdOnce()
                            throws Exception {
        SessionIdRetriever sessionIdRetriever = mockSessionIdRetriever();
        SessionIdManager sessionIdManager = new SessionIdManager( sessionIdRetriever, USER, PASSWORD,
                                                                  SAVE_TIME_OF_SESSION_ID );

        sessionIdManager.retrieveSessionId();
        sessionIdManager.retrieveSessionId();
        sessionIdManager.retrieveSessionId();

        verify( sessionIdRetriever, times( 1 ) ).retrieveSessionId( USER, PASSWORD );
    }

    @Test
    public void testRetrieveSessionIdForThreeTimesWithShortSaveTimeOfSessionIdShouldRetrieveNewSessionIdThreeTimes()
                            throws Exception {
        SessionIdRetriever sessionIdRetriever = mockSessionIdRetriever();
        SessionIdManager sessionIdManager = new SessionIdManager( sessionIdRetriever, USER, PASSWORD, 0 );

        sessionIdManager.retrieveSessionId();
        sleep( 1 );
        sessionIdManager.retrieveSessionId();
        sleep( 1 );
        sessionIdManager.retrieveSessionId();

        verify( sessionIdRetriever, times( 3 ) ).retrieveSessionId( USER, PASSWORD );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRetrieveSessionIdWithNullSessionIdRetrieverShouldThrowException()
                            throws Exception {
        SessionIdManager sessionIdManager = new SessionIdManager( null, USER, PASSWORD, SAVE_TIME_OF_SESSION_ID );
        sessionIdManager.retrieveSessionId();
    }

    private SessionIdRetriever mockSessionIdRetriever() {
        return mock( SessionIdRetriever.class );
    }

}

package org.deegree.securityproxy.sessionid;

import static java.lang.Thread.sleep;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;

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
        sessionIdManager.retrieveSessionId();

        verify( sessionIdRetriever, times( 2 ) ).retrieveSessionId( USER, PASSWORD );
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

    @Test
    public void testRetrieveSessionIdForThreeTimesWithSleepShorterThanSaveTimeShouldRetrieveNewSessionIdOneTime()
                            throws Exception {
        SessionIdRetriever sessionIdRetriever = mockSessionIdRetriever();
        SessionIdManager sessionIdManager = new SessionIdManager( sessionIdRetriever, USER, PASSWORD, 1 );

        sessionIdManager.retrieveSessionId();
        sleep( 1001 );
        sessionIdManager.retrieveSessionId();
        sleep( 1001 );
        sessionIdManager.retrieveSessionId();

        verify( sessionIdRetriever, times( 1 ) ).retrieveSessionId( USER, PASSWORD );
    }

    @Test
    public void testRetrieveSessionIdThreeTimesForTwoDifferentUsersShouldRetrieveNewSessionIdThreeTimes()
                            throws Exception {
        SessionIdRetriever sessionIdRetriever = mockSessionIdRetriever();
        SessionIdManager sessionIdManager = new SessionIdManager( sessionIdRetriever, USER, PASSWORD,
                                                                  SAVE_TIME_OF_SESSION_ID );

        sessionIdManager.retrieveSessionId( "user1", "password1" );
        sessionIdManager.retrieveSessionId( "user2", "password2" );
        sessionIdManager.retrieveSessionId( "user3", "password3" );

        verify( sessionIdRetriever, times( 3 ) ).retrieveSessionId( anyString(), anyString() );
        verify( sessionIdRetriever ).retrieveSessionId( "user1", "password1" );
        verify( sessionIdRetriever ).retrieveSessionId( "user2", "password2" );
        verify( sessionIdRetriever ).retrieveSessionId( "user3", "password3" );
    }

    @Test
    public void testRetrieveSessionIdThreeTimesForTwoDifferentUsersShouldRetrieveNewSessionIdTwoTimes()
                            throws Exception {
        SessionIdRetriever sessionIdRetriever = mockSessionIdRetriever();
        SessionIdManager sessionIdManager = new SessionIdManager( sessionIdRetriever, USER, PASSWORD,
                                                                  SAVE_TIME_OF_SESSION_ID );

        sessionIdManager.retrieveSessionId( "user1", "password1" );
        sessionIdManager.retrieveSessionId( "user2", "password2" );
        sessionIdManager.retrieveSessionId( "user1", "password1" );

        verify( sessionIdRetriever, times( 2 ) ).retrieveSessionId( anyString(), anyString() );
        verify( sessionIdRetriever ).retrieveSessionId( "user1", "password1" );
        verify( sessionIdRetriever ).retrieveSessionId( "user2", "password2" );
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

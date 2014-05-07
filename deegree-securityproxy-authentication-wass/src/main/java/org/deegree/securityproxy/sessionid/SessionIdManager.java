package org.deegree.securityproxy.sessionid;

import static java.lang.System.currentTimeMillis;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages sessionIds
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class SessionIdManager {

    private final SessionIdRetriever sessionIdRetriever;

    private final String technicalUserName;

    private final String technicalPassword;

    private final long saveTimeOfSessionIdInMin;

    private Map<String, UserSession> userSessions = new HashMap<String, UserSession>();

    /**
     * 
     * @param sessionIdRetriever
     *            used to request a session, never <code>null</code>
     * @param technicalUserName
     *            name of the technical user, if requested user is <code>null</code>, may be <code>null</code> if a
     *            technical user is not supported
     * @param technicalPassword
     *            password of the technical user, if requested user is <code>null</code>, may be <code>null</code> if a
     *            technical user is not supported
     * @param saveTimeOfSessionIdInMin
     *            time in minutes how long a retrieved session id is saved. When time is exceeded a new session id is
     *            retrieved, can be -1 if a new session id should be retrieved every time
     */
    public SessionIdManager( SessionIdRetriever sessionIdRetriever, String technicalUserName, String technicalPassword,
                             long saveTimeOfSessionIdInMin ) {
        checkParameters( sessionIdRetriever );
        this.sessionIdRetriever = sessionIdRetriever;
        this.technicalUserName = technicalUserName;
        this.technicalPassword = technicalPassword;
        this.saveTimeOfSessionIdInMin = saveTimeOfSessionIdInMin;
    }

    /**
     * @return the session id of the pre configured technical user (if there is one), <code>null</code> if no session id
     *         could be requested or no technical user is known
     */
    public String retrieveSessionId() {
        return retrieveSessionId( technicalUserName, technicalPassword );
    }

    /**
     * 
     * @param userName
     *            name of the user to request a session id for, if <code>null</code> the session id of the technical
     *            user is requested (if there is one)
     * @param password
     *            password of the user to request a session id for, if <code>null</code> the session id of the technical
     *            user is requested (if there is one)
     * @return the session id of the passed user or of the pre configured technical user (if userName and password is
     *         <code>null</code> and a technical user is configured), <code>null</code> if no session id could be
     *         requested or no technical user is known
     */
    public String retrieveSessionId( String userName, String password ) {
        if ( saveTimeOfSessionIdInMin >= 0 ) {
            return considerSaveTimeAndRetrieveSessionId( userName, password );
        } else {
            return sessionIdRetriever.retrieveSessionId( userName, password );
        }
    }

    private String considerSaveTimeAndRetrieveSessionId( String userName, String password ) {
        UserSession existingUserDetail = retrieveExistingUserDetail( userName, password );
        if ( existingUserDetail == null )
            return retrieveAndUpdateSessionId( userName, password );
        return considerExistingUserDetailAndRetrieveSessionId( existingUserDetail, userName, password );
    }

    private UserSession retrieveExistingUserDetail( String userName, String password ) {
        String userDetailIdentifier = createUserDetailIdentifier( userName, password );
        return userSessions.get( userDetailIdentifier );
    }

    private String considerExistingUserDetailAndRetrieveSessionId( UserSession existingUserDetail, String userName,
                                                                   String password ) {
        long lastUpdateTime = existingUserDetail.getLastUpdateTime();
        if ( isSessionOutdated( lastUpdateTime ) ) {
            return retrieveAndUpdateSessionId( userName, password );
        }
        return existingUserDetail.getCurrentSessionId();
    }

    private String retrieveAndUpdateSessionId( String userName, String password ) {
        String newSessionId = sessionIdRetriever.retrieveSessionId( userName, password );
        putUserDetailToMap( userName, password, newSessionId );
        return newSessionId;
    }

    private void putUserDetailToMap( String userName, String password, String currentSessionId ) {
        String userDetailIdentifier = createUserDetailIdentifier( userName, password );
        UserSession newUserDetail = new UserSession( currentSessionId, currentTimeMillis() );
        userSessions.put( userDetailIdentifier, newUserDetail );
    }

    private String createUserDetailIdentifier( String userName, String password ) {
        return userName + password;
    }

    private boolean isSessionOutdated( long lastUpdateTime ) {
        long saveTimeInMillis = saveTimeOfSessionIdInMin * 60 * 1000;
        return currentTimeMillis() - lastUpdateTime > saveTimeInMillis;
    }

    private void checkParameters( SessionIdRetriever sessionIdRetriever ) {
        if ( sessionIdRetriever == null )
            throw new IllegalArgumentException( "sessionIdRetriever must not be null!" );
    }

}

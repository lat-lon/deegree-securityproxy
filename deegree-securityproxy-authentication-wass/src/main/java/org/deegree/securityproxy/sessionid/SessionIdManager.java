package org.deegree.securityproxy.sessionid;

import java.util.HashMap;
import java.util.Map;

import static java.lang.System.currentTimeMillis;

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

    private final int saveTimeOfSessionIdInMin;

    private Map<String, SessionIdManagerUserDetail> userDetails = new HashMap<String, SessionIdManagerUserDetail>();

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
                             int saveTimeOfSessionIdInMin ) {
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
        SessionIdManagerUserDetail existingUserDetail = retrieveExistingUserDetail( userName, password );
        String currentSessionId = retrieveCurrentSessionId( existingUserDetail );
        long lastUpdateTime = retrieveLastUpdateTime( existingUserDetail );
        return considerExistingUserDetailAndRetrieveSessionId( userName, password, currentSessionId, lastUpdateTime );
    }

    private SessionIdManagerUserDetail retrieveExistingUserDetail( String userName, String password ) {
        String userDetailIdentifier = createUserDetailIdentifier( userName, password );
        return userDetails.get( userDetailIdentifier );
    }

    private String retrieveCurrentSessionId( SessionIdManagerUserDetail existingUserDetail ) {
        if ( existingUserDetail != null )
            return existingUserDetail.getCurrentSessionId();
        return null;
    }

    private long retrieveLastUpdateTime( SessionIdManagerUserDetail existingUserDetail ) {
        if ( existingUserDetail != null )
            return existingUserDetail.getLastUpdateTime();
        return -1;
    }

    private String considerExistingUserDetailAndRetrieveSessionId( String userName, String password,
                                                                   String currentSessionId, long lastUpdateTime ) {
        if ( lastUpdateTime == -1 ) {
            return retrieveAndUpdateSessionId( userName, password );
        } else {
            return considerLastUpdateTimeAndRetrieveSessionId( userName, password, currentSessionId, lastUpdateTime );
        }
    }

    private String considerLastUpdateTimeAndRetrieveSessionId( String userName, String password,
                                                               String currentSessionId, long lastUpdateTime ) {
        long currentTime = currentTimeMillis();
        if ( currentTime - lastUpdateTime > saveTimeOfSessionIdInMin * 1000 ) {
            return retrieveAndUpdateSessionId( userName, password );
        }
        return currentSessionId;
    }

    private String retrieveAndUpdateSessionId( String userName, String password ) {
        String newSessionId = sessionIdRetriever.retrieveSessionId( userName, password );
        putUserDetailToMap( userName, password, newSessionId );
        return newSessionId;
    }

    private void putUserDetailToMap( String userName, String password, String currentSessionId ) {
        String userDetailIdentifier = createUserDetailIdentifier( userName, password );
        SessionIdManagerUserDetail newUserDetail = new SessionIdManagerUserDetail( currentSessionId,
                                                                                   currentTimeMillis() );
        userDetails.put( userDetailIdentifier, newUserDetail );
    }

    private String createUserDetailIdentifier( String userName, String password ) {
        return userName + password;
    }

    private void checkParameters( SessionIdRetriever sessionIdRetriever ) {
        if ( sessionIdRetriever == null )
            throw new IllegalArgumentException( "sessionIdRetriever must not be null!" );
    }

}

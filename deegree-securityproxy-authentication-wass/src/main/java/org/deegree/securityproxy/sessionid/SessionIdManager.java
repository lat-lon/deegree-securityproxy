package org.deegree.securityproxy.sessionid;

import java.util.Date;

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

    private final Integer saveTimeOfSessionIdInMin;

    private Long lastUpdateTime;

    private String currentSessionId;

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
     *            retrieved, may be -1 or <code>null</code> if a new session id should be retrieved every time
     */
    public SessionIdManager( SessionIdRetriever sessionIdRetriever, String technicalUserName, String technicalPassword,
                             Integer saveTimeOfSessionIdInMin ) {
        checkParameters( sessionIdRetriever );
        this.sessionIdRetriever = sessionIdRetriever;
        this.technicalUserName = technicalUserName;
        this.technicalPassword = technicalPassword;
        if ( saveTimeOfSessionIdInMin != null )
            this.saveTimeOfSessionIdInMin = saveTimeOfSessionIdInMin;
        else
            this.saveTimeOfSessionIdInMin = -1;
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
            considerSaveTimeAndUpdateSessionId( userName, password );
            return currentSessionId;
        } else {
            return sessionIdRetriever.retrieveSessionId( userName, password );
        }
    }

    private void considerSaveTimeAndUpdateSessionId( String userName, String password ) {
        if ( lastUpdateTime == null ) {
            updateSessionId( userName, password );
        } else {
            considerLastUpdateTimeAndUpdateSessionId( userName, password );
        }
    }

    private void considerLastUpdateTimeAndUpdateSessionId( String userName, String password ) {
        Long currentTime = new Date().getTime();
        if ( currentTime - lastUpdateTime > saveTimeOfSessionIdInMin * 1000 ) {
            updateSessionId( userName, password );
        }
    }

    private void updateSessionId( String userName, String password ) {
        currentSessionId = sessionIdRetriever.retrieveSessionId( userName, password );
        lastUpdateTime = new Date().getTime();
    }

    private void checkParameters( SessionIdRetriever sessionIdRetriever ) {
        if ( sessionIdRetriever == null )
            throw new IllegalArgumentException( "sessionIdRetriever must not be null!" );
    }

}

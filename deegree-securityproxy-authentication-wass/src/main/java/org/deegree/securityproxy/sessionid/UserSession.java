package org.deegree.securityproxy.sessionid;

/**
 * Encapsulates the sessionId and last update time of a session.
 * 
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * 
 * @version $Revision: $, $Date: $
 */
public class UserSession {

    private String currentSessionId;

    private long lastUpdateTime;

    public UserSession( String currentSessionId, long lastUpdateTime ) {
        this.currentSessionId = currentSessionId;
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getCurrentSessionId() {
        return currentSessionId;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

}

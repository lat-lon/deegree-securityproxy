package org.deegree.securityproxy.sessionid;

/**
 * Encapsulates user details of {@link SessionIdManagerUserDetail}.
 * 
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * 
 * @version $Revision: $, $Date: $
 */
public class SessionIdManagerUserDetail {

    private String currentSessionId;

    private long lastUpdateTime;

    public SessionIdManagerUserDetail( String currentSessionId, long lastUpdateTime ) {
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

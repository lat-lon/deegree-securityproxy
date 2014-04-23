package org.deegree.securityproxy.sessionid;

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

    private String userName;

    private String password;

    public SessionIdManager( SessionIdRetriever sessionIdRetriever, String userName, String password ) {
        this.sessionIdRetriever = sessionIdRetriever;
        this.userName = userName;
        this.password = password;
    }

}
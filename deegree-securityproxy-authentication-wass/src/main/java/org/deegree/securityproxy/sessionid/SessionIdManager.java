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

    private final String technicalUserName;

    private final String technicalPassword;

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
     */
    public SessionIdManager( SessionIdRetriever sessionIdRetriever, String technicalUserName, String technicalPassword ) {
        this.sessionIdRetriever = sessionIdRetriever;
        this.technicalUserName = technicalUserName;
        this.technicalPassword = technicalPassword;
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
        // TODO: manage validity of a session
        return sessionIdRetriever.retrieveSessionId( userName, password );
    }

}
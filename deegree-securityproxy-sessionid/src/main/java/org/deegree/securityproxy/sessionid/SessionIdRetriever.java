package org.deegree.securityproxy.sessionid;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public interface SessionIdRetriever {

    /**
     * @param userName
     *            name of the user to retrieve the session for, never <code>null</code>
     * @param password
     *            password of the user to retrieve the session for, never <code>null</code>
     * @return the session id of the user or <code>null</code> if the session id could not be requested due to an
     *         internal error, external server error or invalid credentials.
     */
    String retrieveSessionId( String userName, String password );

}
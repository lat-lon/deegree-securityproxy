package org.deegree.securityproxy.authorization.logging;

/**
 * 
 * Encapsulates the result of an authorization.
 * 
 * @author <a href="wilden@lat-lon.de">Johannes Wilden</a>
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
public class AuthorizationReport {

    private final String message;

    private final boolean isAuthorized;

    public AuthorizationReport( String message, boolean isAuthorized ) {
        this.message = message;
        this.isAuthorized = isAuthorized;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the isAuthorized
     */
    public boolean isAuthorized() {
        return isAuthorized;
    }

}

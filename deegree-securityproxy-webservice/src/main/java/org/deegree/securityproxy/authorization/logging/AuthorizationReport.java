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

    private final String serviceUrl;

    /**
     * Instantiates a new {@link AuthorizationReport} for a failed authorization (isAuthhozied is <code>false</code>).
     * 
     * @param message
     *            containing the reason why the authorization failed, never <code>null</code>
     */
    public AuthorizationReport( String message ) {
        this( message, false, null );
    }

    /**
     * Instantiates a new {@link AuthorizationReport}.
     * 
     * @param message
     *            containing the reason why the authorization failed, never <code>null</code>
     * @param isAuthorized
     *            <code>true</code> if authorized, <code>false</code> otherwise
     * @param serviceUrl
     *            endpoint url of the requested service, may be <code>null</code> if authorization failed
     */
    public AuthorizationReport( String message, boolean isAuthorized, String serviceUrl ) {
        this.message = message;
        this.isAuthorized = isAuthorized;
        this.serviceUrl = serviceUrl;
    }

    /**
     * @return the message containing informations why the user is not authorized or that the user is authorized, never
     *         <code>null</code>
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the <code>true</code> if authorized, <code>false</code> otherwise
     */
    public boolean isAuthorized() {
        return isAuthorized;
    }

    /**
     * @return the endpoint url of the requested service, may be <code>null</code> if authorization failed
     */
    public String getServiceUrl() {
        return serviceUrl;
    }

}
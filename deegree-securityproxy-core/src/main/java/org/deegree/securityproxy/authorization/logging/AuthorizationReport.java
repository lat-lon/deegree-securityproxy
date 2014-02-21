package org.deegree.securityproxy.authorization.logging;

import java.util.Collections;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;

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

    private final Map<String, String> additionalKeyValuePairs;

    /**
     * Instantiates a new {@link AuthorizationReport} for a failed authorization (isAuthhozied is <code>false</code>).
     *
     * @param message containing the reason why the authorization failed, never <code>null</code>
     */
    public AuthorizationReport( String message ) {
        this( message, false, null, unmodifiableMap( Collections.<String, String>emptyMap() ) );
    }

    /**
     * Instantiates a new {@link AuthorizationReport}.
     *
     * @param message                 containing the reason why the authorization failed, never <code>null</code>
     * @param isAuthorized            <code>true</code> if authorized, <code>false</code> otherwise
     * @param serviceUrl              endpoint url of the requested service, may be <code>null</code> if authorization
     *                                failed
     * @param additionalKeyValuePairs additional key value pairs, may be empty but never <code>null</code>
     */
    public AuthorizationReport( String message, boolean isAuthorized, String serviceUrl,
                                Map<String, String> additionalKeyValuePairs ) {
        this.message = message;
        this.isAuthorized = isAuthorized;
        this.serviceUrl = serviceUrl;

        if ( additionalKeyValuePairs != null )
            this.additionalKeyValuePairs = unmodifiableMap( additionalKeyValuePairs );
        else
            this.additionalKeyValuePairs = unmodifiableMap( Collections.<String, String>emptyMap() );
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

    /**
     * @return additional key value pairs, may be empty but never <code>null</code>
     */
    public Map<String, String> getAdditionalKeyValuePairs() {
        return additionalKeyValuePairs;
    }

}
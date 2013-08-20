package org.deegree.securityproxy.authentication;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Loads {@link UserDetails} by header value.
 * 
 * @author <a href="goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
public class HeaderTokenDataSource {

    /**
     * Verify an header value against the encapsulated data source.
     * 
     * @param headerValue
     *            may be <code>null</code>
     * @return the user details that match the given header value
     */
    public UserDetails loadUserDetailsFromDataSource( String headerValue ) {
        return null;
    }
}

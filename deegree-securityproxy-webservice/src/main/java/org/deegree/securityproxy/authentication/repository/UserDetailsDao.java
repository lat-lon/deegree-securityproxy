package org.deegree.securityproxy.authentication.repository;

import javax.sql.DataSource;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Loads {@link UserDetails} from a {@link DataSource}.
 * 
 * @author <a href="goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
public interface UserDetailsDao {

    /**
     * Verify an header value against the encapsulated data source.
     * 
     * @param headerValue
     *            never <code>null</code> or empty
     * @return the user details that match the given header value
     * @throws IllegalArgumentException
     *             on <code>null</code> or empty argument
     */
    UserDetails retrieveUserDetailsById( String headerValue )
                            throws IllegalArgumentException;

}
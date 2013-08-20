package org.deegree.securityproxy.authentication;

import java.util.Collections;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Authentication token that encapsulates an header value that the client passed to the server.
 * 
 * @author <a href="goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * 
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
public class HeaderAuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = -7556696904089860387L;

    private String credentials;

    private UserDetails principal;

    /**
     * Set the details of the new instance to the passed value. Grants no authorities.
     * 
     * @param value
     *            the header value to set the details. May be <code>null</code>.
     */
    public HeaderAuthenticationToken( String value ) {
        super( Collections.<GrantedAuthority> emptyList() );
        setHeaderTokenValue( value );
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public void setDetails( Object details ) {
        if ( details instanceof String || details == null )
            super.setDetails( details );
        else
            throw new IllegalArgumentException( "HeaderAuthenticationToken only supports string details!" );
    }

    /**
     * @param credentials
     *            may be <code>null</code>
     */
    public void setCredentials( String credentials ) {
        this.credentials = credentials;
    }

    /**
     * @param principal
     *            may be <code>null</code>
     */
    public void setPrincipal( UserDetails principal ) {
        this.principal = principal;
    }

    /**
     * Set the details of the token to the passed {@link String} value.
     * 
     * @param value
     *            the string value to pass. May be <code>null</code>.
     */
    public void setHeaderTokenValue( String value ) {
        setDetails( value );
    }

    /**
     * Retrieve the details of the token as {@link String}.
     * 
     * @return the token value, may be <code>null</code>.
     */
    public String getHeaderTokenValue() {
        return (String) getDetails();
    }

}

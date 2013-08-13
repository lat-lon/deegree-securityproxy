package org.deegree.securityproxy.report;

/**
 * Encapsulates a proxy request report
 * 
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author <a href="goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
public class ProxyReport {

    private final String ipAddressOfRequestingUser;

    private final String targetUri;

    private final boolean isResponseSuccessfullySent;

    public ProxyReport( String ipAddressOfRequestingUser, String targetUri, boolean isResponseSuccesfullySent ) {
        this.ipAddressOfRequestingUser = ipAddressOfRequestingUser;
        this.targetUri = targetUri;
        this.isResponseSuccessfullySent = isResponseSuccesfullySent;
    }

    public String getIpAddressOfRequestingUser() {
        return ipAddressOfRequestingUser;
    }

    public String getTargetUri() {
        return targetUri;
    }

    public boolean isResponseSuccessfullySent() {
        return isResponseSuccessfullySent;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( ipAddressOfRequestingUser == null ) ? 0 : ipAddressOfRequestingUser.hashCode() );
        result = prime * result + ( isResponseSuccessfullySent ? 1231 : 1237 );
        result = prime * result + ( ( targetUri == null ) ? 0 : targetUri.hashCode() );
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        ProxyReport other = (ProxyReport) obj;
        if ( ipAddressOfRequestingUser == null ) {
            if ( other.ipAddressOfRequestingUser != null )
                return false;
        } else if ( !ipAddressOfRequestingUser.equals( other.ipAddressOfRequestingUser ) )
            return false;
        if ( isResponseSuccessfullySent != other.isResponseSuccessfullySent )
            return false;
        if ( targetUri == null ) {
            if ( other.targetUri != null )
                return false;
        } else if ( !targetUri.equals( other.targetUri ) )
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "Request sent from IP address: " );
        builder.append( ipAddressOfRequestingUser );
        builder.append( ", target URL: " );
        builder.append( targetUri );
        builder.append( ", reponse was: " );
        String sent = isResponseSuccessfullySent ? "successful." : "not successful";
        builder.append( sent );
        return builder.toString();
    }

}

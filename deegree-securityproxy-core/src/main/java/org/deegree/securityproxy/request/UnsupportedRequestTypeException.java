package org.deegree.securityproxy.request;

/**
 * Indicates that the request type is not supported.
 * 
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
public class UnsupportedRequestTypeException extends Exception {

    private static final long serialVersionUID = 7896661662041498922L;

    public UnsupportedRequestTypeException() {
        super();
    }

    public UnsupportedRequestTypeException( String string ) {
        super( string );
    }
}
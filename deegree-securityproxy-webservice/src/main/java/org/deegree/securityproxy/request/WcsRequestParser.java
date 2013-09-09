package org.deegree.securityproxy.request;

import javax.servlet.http.HttpServletRequest;


/**
 * TODO add class documentation here
 * 
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
public class WcsRequestParser {

    public WcsRequest parse( HttpServletRequest request ) {
        /*
         * TODO check if incoming request is a wcs request
         */
        return new WcsRequest( request );
    }
}

package org.deegree.securityproxy.filter;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Custom Response wrapper that allows access to the response code
 * Deletes the "Transfer Encoding" HTTP Header
 * 
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author <a href="goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
public class FilterResponseWrapper extends HttpServletResponseWrapper {

    private int httpStatus;
    
    public FilterResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public void setHeader( String name, String value ) {
        if (!"Transfer-Encoding".equals(name)) {
            super.setHeader( name, value );
        }
    }
    
    @Override
    public void addHeader( String name, String value ) {
        if (!"Transfer-Encoding".equals(name)) {
            super.setHeader( name, value );
        }
    }
    
    @Override
    public void sendError( int sc )
                            throws IOException {
        httpStatus = sc;
        super.sendError(sc);
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        httpStatus = sc;
        super.sendError(sc, msg);
    }

    @Override
    public void setStatus(int sc) {
        httpStatus = sc;
        super.setStatus(sc);
    }

    public int getStatus() {
        return httpStatus;
    }
    
}
package org.deegree.securityproxy.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * Test implementation for {@link FilterChain}
 * 
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
public class FilterChainTestImpl implements FilterChain {
    
    private int status;

    public FilterChainTestImpl(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus( int status ) {
        this.status = status;
    }

    public void doFilter( ServletRequest request, ServletResponse response )
                            throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse) response;
        res.setStatus( status );
    }

}

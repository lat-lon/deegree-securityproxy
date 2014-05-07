package org.deegree.securityproxy.wfs.request;

import javax.servlet.http.HttpServletRequest;

import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.request.OwsRequestParser;
import org.deegree.securityproxy.request.UnsupportedRequestTypeException;

/**
 * Parses an incoming HTTP POST request into a {@link WfsRequest}.
 * 
 * @author <a href="stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * @version $Revision: $, $Date: $
 */
public class WfsPostRequestParser implements OwsRequestParser {

    @Override
    public OwsRequest parse( HttpServletRequest request )
                            throws UnsupportedRequestTypeException {
        checkIfRequestIsNotNull( request );
        checkIfRequestMethodIsPost( request );
        // TODO:Not implemented yet.
        throw new UnsupportedOperationException();
    }

    private void checkIfRequestIsNotNull( HttpServletRequest request ) {
        if ( request == null )
            throw new IllegalArgumentException( "Request must not be null!" );
    }

    private void checkIfRequestMethodIsPost( HttpServletRequest request ) {
        if ( !"POST".equals( request.getMethod() ) )
            throw new IllegalArgumentException( "Request method must be POST!" );
    }

}
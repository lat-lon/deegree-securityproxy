package org.deegree.securityproxy.wfs.request;

import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.request.OwsRequestParser;
import org.deegree.securityproxy.request.UnsupportedRequestTypeException;

import javax.servlet.http.HttpServletRequest;

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
        // TODO: Not implemented yet.
        return null;
    }

}

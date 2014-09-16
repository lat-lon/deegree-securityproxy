package org.deegree.securityproxy.request.parser;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.deegree.securityproxy.request.KvpNormalizer.normalizeKvpMap;

/**
 * Simple parser to determine the service type of a request. Currently this parser supports GET and POST requests.
 *
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * 
 * @version $Revision: $, $Date: $
 */
public class ServiceTypeParser {

    /**
     * Determines service type of given {@link javax.servlet.http.HttpServletRequest}. GET and POST requests are
     * supported.
     * 
     * @param request
     *            never <code>null</code>
     * @return service type, <code>null</code> if no service type was found
     */
    public String determineServiceType( HttpServletRequest request ) {
        String method = request.getMethod();
        if ( "GET".equals( method ) ) {
            return handleGetRequest( request );
        } else if ( "POST".equals( method ) )
            // TODO: Implement parsing of POST requests.
            return null;
        return null;
    }

    private String handleGetRequest( HttpServletRequest request ) {
        @SuppressWarnings("unchecked")
        Map<String, String[]> kvpMap = normalizeKvpMap( request.getParameterMap() );
        String[] serviceTypes = kvpMap.get( "service" );
        if ( serviceTypes == null || serviceTypes.length < 1 )
            return null;
        return serviceTypes[0];
    }

}
package org.deegree.securityproxy.request;

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
     * Determines service type of given {@link javax.servlet.http.HttpServletRequest}.
     * 
     * @param request
     *            never <code>null</code>
     * @return service type
     */
    public String determineServiceType( HttpServletRequest request ) {
        String method = request.getMethod();
        if ( "GET".equals( method ) ) {
            return handleGetRequest( request );
        } else if ( "POST".equals( method ) )
            // TODO: Implement parsing of POST requests.
            return null;
        // TODO: Improve exception handling.
        throw new IllegalArgumentException( "Cannot parse other request methods than GET or POST!" );
    }

    private String handleGetRequest( HttpServletRequest request ) {
        @SuppressWarnings("unchecked")
        Map<String, String[]> kvpMap = normalizeKvpMap( request.getParameterMap() );
        String[] serviceTypes = kvpMap.get( "service" );
        if ( serviceTypes == null || serviceTypes.length < 1 )
            throw new IllegalArgumentException( "GET request does not contain a service parameter!" );
        return serviceTypes[0];
    }

}
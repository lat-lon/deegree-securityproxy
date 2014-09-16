package org.deegree.securityproxy.request.parser;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.util.Map;

import static org.deegree.securityproxy.request.KvpNormalizer.normalizeKvpMap;
import static org.deegree.securityproxy.request.parser.OwsRequestParserUtils.createReader;

/**
 * Simple parser to determine the service type of a request. Currently this parser supports GET and POST requests.
 *
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * 
 * @version $Revision: $, $Date: $
 */
public class ServiceTypeParser {

    private static final Logger LOG = Logger.getLogger( ServiceTypeParser.class );

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
        } else if ( "POST".equals( method ) ) {
            return handlePostRequest( request );
        }
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

    private String handlePostRequest( HttpServletRequest request ) {
        try {
            XMLStreamReader reader = createReader( request );
            return reader.getAttributeValue( null, "service" );
        } catch ( IOException e ) {
            return handleExceptionOfReader( e );
        } catch ( XMLStreamException e ) {
            return handleExceptionOfReader( e );
        }
    }

    private String handleExceptionOfReader( Exception e ) {
        LOG.warn( "XMLStreamReader could not be created from request:" + e.getMessage() );
        return null;
    }

}
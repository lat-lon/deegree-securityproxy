package org.deegree.securityproxy.wps.request;

import static java.util.Arrays.asList;
import static org.deegree.securityproxy.request.parser.OwsRequestParserUtils.checkRequiredParameter;
import static org.deegree.securityproxy.request.parser.OwsRequestParserUtils.checkSingleRequiredParameter;
import static org.deegree.securityproxy.request.parser.OwsRequestParserUtils.evaluateServiceName;
import static org.deegree.securityproxy.request.parser.OwsRequestParserUtils.evaluateVersion;
import static org.deegree.securityproxy.request.KvpNormalizer.normalizeKvpMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.deegree.securityproxy.request.OwsServiceVersion;
import org.deegree.securityproxy.request.UnsupportedRequestTypeException;
import org.deegree.securityproxy.request.parser.OwsRequestParser;

/**
 * Parses an incoming {@link javax.servlet.http.HttpServletRequest} into a {@link WpsRequest}.
 * 
 * @author <a href="wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author last edited by: $Author: stenger $
 * @version $Revision: $, $Date: $
 */
public class WpsRequestParser implements OwsRequestParser {

    public static final OwsServiceVersion VERSION_100 = new OwsServiceVersion( 1, 0, 0 );

    public static final String WPS_SERVICE = "WPS";

    public static final String GETCAPABILITIES = "GetCapabilities";

    public static final String DESCRIBEPROCESS = "DescribeProcess";

    public static final String EXECUTE = "Execute";

    private static final String REQUEST = "request";

    private static final String SERVICE = "service";

    private static final String VERSION = "version";

    private static final String IDENTIFIER = "identifier";

    private final List<OwsServiceVersion> supportedVersion = asList( VERSION_100 );

    @Override
    @SuppressWarnings("unchecked")
    public WpsRequest parse( HttpServletRequest request )
                    throws UnsupportedRequestTypeException {
        if ( request == null )
            throw new IllegalArgumentException( "Request must not be null!" );
        String serviceName = evaluateServiceName( request );
        Map<String, String[]> normalizedParameterMap = normalizeKvpMap( request.getParameterMap() );
        checkParameters( normalizedParameterMap );
        return parseRequest( serviceName, normalizedParameterMap );
    }

    private WpsRequest parseRequest( String serviceName, Map<String, String[]> normalizedParameterMap )
                    throws UnsupportedRequestTypeException {
        String type = normalizedParameterMap.get( REQUEST )[0];
        if ( GETCAPABILITIES.equalsIgnoreCase( type ) )
            return parseGetCapabilitiesRequest( serviceName, normalizedParameterMap );
        if ( DESCRIBEPROCESS.equalsIgnoreCase( type ) )
            return parseDescribeProcessRequest( serviceName, normalizedParameterMap );
        if ( EXECUTE.equalsIgnoreCase( type ) )
            return parseExecuteRequest( serviceName, normalizedParameterMap );
        throw new IllegalArgumentException( "Unrecognized operation type: " + type );
    }

    private WpsRequest parseGetCapabilitiesRequest( String serviceName, Map<String, String[]> normalizedParameterMap )
                    throws UnsupportedRequestTypeException {
        OwsServiceVersion version = evaluateVersion( VERSION, normalizedParameterMap, supportedVersion );
        return new WpsRequest( GETCAPABILITIES, version, serviceName );
    }

    private WpsRequest parseDescribeProcessRequest( String serviceName, Map<String, String[]> normalizedParameterMap ) {
        checkDescribeProcessParameters( normalizedParameterMap );
        OwsServiceVersion version = evaluateVersion( VERSION, normalizedParameterMap, supportedVersion );
        List<String> extractedIdentifiers = extractIdentifier( normalizedParameterMap.get( IDENTIFIER ) );
        return new WpsRequest( DESCRIBEPROCESS, version, serviceName, extractedIdentifiers );
    }

    private WpsRequest parseExecuteRequest( String serviceName, Map<String, String[]> normalizedParameterMap ) {
        checkExecuteParameters( normalizedParameterMap );
        OwsServiceVersion version = evaluateVersion( VERSION, normalizedParameterMap, supportedVersion );
        List<String> extractedIdentifier = extractIdentifier( normalizedParameterMap.get( IDENTIFIER ) );
        return new WpsRequest( EXECUTE, version, serviceName, extractedIdentifier );
    }

    private void checkParameters( Map<String, String[]> normalizedParameterMap )
                    throws UnsupportedRequestTypeException {
        checkServiceParameter( normalizedParameterMap );
        checkRequestParameter( normalizedParameterMap );
    }

    private void checkServiceParameter( Map<String, String[]> normalizedParameterMap )
                    throws UnsupportedRequestTypeException {
        String serviceType = checkSingleRequiredParameter( normalizedParameterMap, SERVICE );
        if ( !"wps".equalsIgnoreCase( serviceType ) ) {
            String msg = "Request must contain a \"service\" parameter with value \"wps\"";
            throw new UnsupportedRequestTypeException( msg );
        }
    }

    private void checkRequestParameter( Map<String, String[]> normalizedParameterMap ) {
        checkSingleRequiredParameter( normalizedParameterMap, REQUEST );
    }

    private void checkDescribeProcessParameters( Map<String, String[]> normalizedParameterMap ) {
        checkIdentifiersParameter( normalizedParameterMap );
    }

    private void checkExecuteParameters( Map<String, String[]> normalizedParameterMap ) {
        checkIdentifierParameter( normalizedParameterMap );
    }

    private void checkIdentifiersParameter( Map<String, String[]> normalizedParameterMap ) {
        checkRequiredParameter( normalizedParameterMap, IDENTIFIER );
    }

    private void checkIdentifierParameter( Map<String, String[]> normalizedParameterMap ) {
        checkSingleRequiredParameter( normalizedParameterMap, IDENTIFIER );
    }

    private List<String> extractIdentifier( String[] identifierParameters ) {
        List<String> separatedIdentifiers = new ArrayList<String>();
        if ( identifierParameters != null ) {
            for ( String identifierParameter : identifierParameters ) {
                Collections.addAll( separatedIdentifiers, identifierParameter.split( "," ) );
            }
        }
        return separatedIdentifiers;
    }

}

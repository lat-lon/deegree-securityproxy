package org.deegree.securityproxy.wfs.request;

import org.deegree.securityproxy.request.OwsRequestParser;
import org.deegree.securityproxy.request.OwsServiceVersion;
import org.deegree.securityproxy.request.UnsupportedRequestTypeException;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.deegree.securityproxy.request.GetOwsRequestParserUtils.checkSingleRequiredParameter;
import static org.deegree.securityproxy.request.GetOwsRequestParserUtils.evaluateVersion;
import static org.deegree.securityproxy.request.KvpNormalizer.normalizeKvpMap;

/**
 * Parses an incoming HTTP GET request into a {@link WfsRequest}.
 * 
 * @author <a href="stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * @version $Revision: $, $Date: $
 */
public class WfsGetRequestParser implements OwsRequestParser {

    public static final OwsServiceVersion VERSION_110 = new OwsServiceVersion( 1, 1, 0 );

    public static final String WFS_SERVICE = "WFS";

    public static final String GETCAPABILITIES = "GetCapabilities";

    public static final String DESCRIBEFEATURETYPE = "DescribeFeatureType";

    public static final String GETFEATURE = "GetFeature";

    private static final String REQUEST = "request";

    private static final String SERVICE = "service";

    private static final String VERSION = "version";

    private List<OwsServiceVersion> supportedVersion = Collections.singletonList( VERSION_110 );

    public WfsRequest parse( HttpServletRequest request )
                            throws UnsupportedRequestTypeException {
        checkIfRequestIsNotNull( request );
        checkIfRequestMethodIsGet( request );
        Map<String, String[]> normalizedParameterMap = normalizeKvpMap( request.getParameterMap() );
        checkParameters( normalizedParameterMap );
        String type = normalizedParameterMap.get( REQUEST )[0];
        if ( GETCAPABILITIES.equalsIgnoreCase( type ) )
            return parseGetCapabilitiesRequest( normalizedParameterMap );
        if ( GETFEATURE.equalsIgnoreCase( type ) )
            return parseGetFeatureRequest( normalizedParameterMap );
        if ( DESCRIBEFEATURETYPE.equalsIgnoreCase( type ) )
            return parseDescribeFeatureRequest( normalizedParameterMap );
        throw new IllegalArgumentException( "Unrecognized operation type: " + type );
    }

    private WfsRequest parseGetCapabilitiesRequest( Map<String, String[]> normalizedParameterMap )
                            throws UnsupportedRequestTypeException {
        checkServiceParameter( normalizedParameterMap );
        OwsServiceVersion version = evaluateVersion( VERSION, normalizedParameterMap, supportedVersion );
        return new WfsRequest( WFS_SERVICE, GETCAPABILITIES, version );
    }

    private WfsRequest parseGetFeatureRequest( Map<String, String[]> normalizedParameterMap )
                            throws UnsupportedRequestTypeException {
        checkServiceParameter( normalizedParameterMap );
        OwsServiceVersion version = evaluateVersion( VERSION, normalizedParameterMap, supportedVersion );
        return new WfsRequest( WFS_SERVICE, GETFEATURE, version );
    }

    private WfsRequest parseDescribeFeatureRequest( Map<String, String[]> normalizedParameterMap )
                            throws UnsupportedRequestTypeException {
        checkServiceParameter( normalizedParameterMap );
        OwsServiceVersion version = evaluateVersion( VERSION, normalizedParameterMap, supportedVersion );
        return new WfsRequest( WFS_SERVICE, DESCRIBEFEATURETYPE, version );
    }

    private void checkServiceParameter( Map<String, String[]> normalizedParameterMap )
                            throws UnsupportedRequestTypeException {
        String serviceType = checkSingleRequiredParameter( normalizedParameterMap, SERVICE );
        if ( !WFS_SERVICE.equalsIgnoreCase( serviceType ) ) {
            String msg = "Request must contain a 'service' parameter with value 'wfs'";
            throw new UnsupportedRequestTypeException( msg );
        }
    }

    private void checkParameters( Map<String, String[]> normalizedParameterMap ) {
        checkSingleRequiredParameter( normalizedParameterMap, REQUEST );
    }

    private void checkIfRequestIsNotNull( HttpServletRequest request ) {
        if ( request == null )
            throw new IllegalArgumentException( "Request must not be null!" );
    }

    private void checkIfRequestMethodIsGet( HttpServletRequest request ) {
        if ( !"GET".equals( request.getMethod() ) )
            throw new IllegalArgumentException( "Request method must be GET!" );
    }

}

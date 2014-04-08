package org.deegree.securityproxy.wms.request;

import static java.util.Arrays.asList;
import static org.deegree.securityproxy.request.GetOwsRequestParserUtils.checkRequiredParameter;
import static org.deegree.securityproxy.request.GetOwsRequestParserUtils.checkSingleRequiredParameter;
import static org.deegree.securityproxy.request.KvpNormalizer.normalizeKvpMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.deegree.securityproxy.request.OwsRequestParser;
import org.deegree.securityproxy.request.OwsServiceVersion;
import org.deegree.securityproxy.request.UnsupportedRequestTypeException;

/**
 * Parses an incoming {@link javax.servlet.http.HttpServletRequest} into a
 * {@link org.deegree.securityproxy.wms.request.WmsRequest}.
 * 
 * @author <a href="stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * @version $Revision: $, $Date: $
 */
public class WmsRequestParser implements OwsRequestParser {

    public static final OwsServiceVersion VERSION_130 = new OwsServiceVersion( 1, 3, 0 );

    public static final String WMS_SERVICE = "WMS";

    public static final String GETCAPABILITIES = "GetCapabilities";

    public static final String GETFEATUREINFO = "GetFeatureInfo";

    public static final String GETMAP = "GetMap";

    private static final String REQUEST = "request";

    private static final String SERVICE = "service";

    private static final String VERSION = "version";

    private static final String LAYERS = "layers";

    private static final String CRS = "crs";

    private static final String BBOX = "bbox";

    private static final String WIDTH = "width";

    private static final String HEIGHT = "height";

    private static final String FORMAT = "format";

    private static final String STYLES = "styles";

    private static final String QUERY_LAYERS = "query_layers";

    private static final String INFO_FORMAT = "info_format";

    private static final String I = "i";

    private static final String J = "j";

    private final List<OwsServiceVersion> supportedVersion = asList( VERSION_130 );

    @Override
    @SuppressWarnings("unchecked")
    public WmsRequest parse( HttpServletRequest request )
                            throws UnsupportedRequestTypeException {
        if ( request == null )
            throw new IllegalArgumentException( "Request must not be null!" );
        String serviceName = evaluateServiceName( request );
        Map<String, String[]> normalizedParameterMap = normalizeKvpMap( request.getParameterMap() );
        checkParameters( normalizedParameterMap );
        return parseRequest( serviceName, normalizedParameterMap );
    }

    private WmsRequest parseRequest( String serviceName, Map<String, String[]> normalizedParameterMap )
                            throws UnsupportedRequestTypeException {
        String type = normalizedParameterMap.get( REQUEST )[0];
        if ( GETCAPABILITIES.equalsIgnoreCase( type ) )
            return parseGetCapabilitiesRequest( serviceName, normalizedParameterMap );
        if ( GETFEATUREINFO.equalsIgnoreCase( type ) )
            return parseGetFeatureInfoRequest( serviceName, normalizedParameterMap );
        if ( GETMAP.equalsIgnoreCase( type ) )
            return parseGetMapRequest( serviceName, normalizedParameterMap );
        throw new IllegalArgumentException( "Unrecognized operation type: " + type );
    }

    private WmsRequest parseGetMapRequest( String serviceName, Map<String, String[]> normalizedParameterMap ) {
        checkGetMapParameters( normalizedParameterMap );

        OwsServiceVersion version = evaluateVersion( normalizedParameterMap );
        List<String> separatedLayers = extractLayers( normalizedParameterMap.get( LAYERS ) );
        return new WmsRequest( GETMAP, version, separatedLayers, serviceName );
    }

    private WmsRequest parseGetFeatureInfoRequest( String serviceName, Map<String, String[]> normalizedParameterMap ) {
        checkGetFeatureInfoParameters( normalizedParameterMap );

        OwsServiceVersion version = evaluateVersion( normalizedParameterMap );
        List<String> separatedLayers = extractLayers( normalizedParameterMap.get( LAYERS ) );
        List<String> separatedQueryLayers = extractLayers( normalizedParameterMap.get( QUERY_LAYERS ) );
        return new WmsRequest( GETFEATUREINFO, version, separatedLayers, separatedQueryLayers, serviceName );
    }

    private WmsRequest parseGetCapabilitiesRequest( String serviceName, Map<String, String[]> normalizedParameterMap )
                            throws UnsupportedRequestTypeException {
        checkGetCapabilitiesParameters( normalizedParameterMap );
        OwsServiceVersion version = evaluateVersion( normalizedParameterMap );
        return new WmsRequest( GETCAPABILITIES, version, serviceName );
    }

    private OwsServiceVersion evaluateVersion( Map<String, String[]> normalizedParameterMap ) {
        String[] versionParameters = normalizedParameterMap.get( VERSION );
        if ( versionParameters == null || versionParameters.length == 0 )
            return null;
        String versionParam = versionParameters[0];
        if ( versionParam != null && !versionParam.isEmpty() ) {
            OwsServiceVersion version = new OwsServiceVersion( versionParam );
            if ( supportedVersion.contains( version ) )
                return version;
        }
        throw new IllegalArgumentException( "Unrecognized version " + versionParam );
    }

    private String evaluateServiceName( HttpServletRequest request ) {
        String servletPath = request.getServletPath();
        if ( servletPath == null )
            throw new IllegalArgumentException( "Service name must not be null!" );
        if ( servletPath.contains( "/" ) ) {
            String[] splittedServletPath = servletPath.split( "/" );
            return splittedServletPath[splittedServletPath.length - 1];
        }
        return servletPath;
    }

    private List<String> extractLayers( String[] layerParameters ) {
        List<String> separatedLayers = new ArrayList<String>();
        if ( layerParameters != null ) {
            for ( String layerParameter : layerParameters ) {
                Collections.addAll( separatedLayers, layerParameter.split( "," ) );
            }
        }
        return separatedLayers;
    }

    private void checkParameters( Map<String, String[]> normalizedParameterMap ) {
        checkRequestParameter( normalizedParameterMap );
    }

    private void checkServiceParameter( Map<String, String[]> normalizedParameterMap )
                            throws UnsupportedRequestTypeException {
        String serviceType = checkSingleRequiredParameter( normalizedParameterMap, SERVICE );
        if ( !"wms".equalsIgnoreCase( serviceType ) ) {
            String msg = "Request must contain a \"service\" parameter with value \"wms\"";
            throw new UnsupportedRequestTypeException( msg );
        }
    }

    private void checkRequestParameter( Map<String, String[]> normalizedParameterMap ) {
        checkSingleRequiredParameter( normalizedParameterMap, REQUEST );
    }

    private void checkGetMapParameters( Map<String, String[]> normalizedParameterMap ) {
        checkLayersParameter( normalizedParameterMap );
        checkStylesParameter( normalizedParameterMap );
        checkCrsParameter( normalizedParameterMap );
        checkBboxParameter( normalizedParameterMap );
        checkWidthParameter( normalizedParameterMap );
        checkHeightParameter( normalizedParameterMap );
        checkFormatParameter( normalizedParameterMap );
    }

    private void checkGetFeatureInfoParameters( Map<String, String[]> normalizedParameterMap ) {
        checkQueryLayersParameter( normalizedParameterMap );
        checkInfoFormatParameter( normalizedParameterMap );
        checkIParameter( normalizedParameterMap );
        checkJParameter( normalizedParameterMap );
        checkLayersParameter( normalizedParameterMap );
        checkStylesParameter( normalizedParameterMap );
        checkCrsParameter( normalizedParameterMap );
        checkBboxParameter( normalizedParameterMap );
        checkWidthParameter( normalizedParameterMap );
        checkHeightParameter( normalizedParameterMap );
        checkFormatParameter( normalizedParameterMap );
    }

    private void checkGetCapabilitiesParameters( Map<String, String[]> normalizedParameterMap )
                            throws UnsupportedRequestTypeException {
        checkServiceParameter( normalizedParameterMap );
    }

    private void checkLayersParameter( Map<String, String[]> normalizedParameterMap ) {
        checkRequiredParameter( normalizedParameterMap, LAYERS );
    }

    private void checkStylesParameter( Map<String, String[]> normalizedParameterMap ) {
        checkSingleRequiredParameter( normalizedParameterMap, STYLES );
    }

    private void checkCrsParameter( Map<String, String[]> normalizedParameterMap ) {
        checkSingleRequiredParameter( normalizedParameterMap, CRS );
    }

    private void checkBboxParameter( Map<String, String[]> normalizedParameterMap ) {
        checkSingleRequiredParameter( normalizedParameterMap, BBOX );
    }

    private void checkWidthParameter( Map<String, String[]> normalizedParameterMap ) {
        checkSingleRequiredParameter( normalizedParameterMap, WIDTH );
    }

    private void checkHeightParameter( Map<String, String[]> normalizedParameterMap ) {
        checkSingleRequiredParameter( normalizedParameterMap, HEIGHT );
    }

    private void checkQueryLayersParameter( Map<String, String[]> normalizedParameterMap ) {
        checkRequiredParameter( normalizedParameterMap, QUERY_LAYERS );
    }

    private void checkInfoFormatParameter( Map<String, String[]> normalizedParameterMap ) {
        checkSingleRequiredParameter( normalizedParameterMap, INFO_FORMAT );
    }

    private void checkIParameter( Map<String, String[]> normalizedParameterMap ) {
        checkSingleRequiredParameter( normalizedParameterMap, I );
    }

    private void checkJParameter( Map<String, String[]> normalizedParameterMap ) {
        checkSingleRequiredParameter( normalizedParameterMap, J );
    }

    private void checkFormatParameter( Map<String, String[]> normalizedParameterMap ) {
        checkSingleRequiredParameter( normalizedParameterMap, FORMAT );
    }

}
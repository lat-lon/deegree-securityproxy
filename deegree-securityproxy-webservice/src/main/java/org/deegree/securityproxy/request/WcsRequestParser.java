package org.deegree.securityproxy.request;

import org.deegree.securityproxy.commons.WcsOperationType;
import org.deegree.securityproxy.commons.WcsServiceVersion;

import javax.servlet.http.HttpServletRequest;

import java.util.*;

import static org.deegree.securityproxy.commons.WcsOperationType.*;
import static org.deegree.securityproxy.request.KvpNormalizer.normalizeKvpMap;

/**
 * Parses an incoming {@link HttpServletRequest} into a {@link WcsRequest}.
 *
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author last edited by: $Author: erben $
 * @version $Revision: $, $Date: $
 */
public class WcsRequestParser {

    private static final String REQUEST = "request";

    private static final String SERVICE = "service";

    private static final String VERSION = "version";

    private static final Object COVERAGE = "coverage";

    public static final String CRS = "crs";

    public static final String BBOX = "bbox";

    public static final String TIME = "time";

    public static final String WIDTH = "width";

    public static final String HEIGHT = "height";

    public static final String RESX = "resx";

    public static final String RESY = "resy";

    public static final String FORMAT = "format";

    /**
     * Parses an incoming {@link HttpServletRequest} into a {@link WcsRequest}.
     *
     * @param request never <code>null</code>. Must contain the following parameters exactly once ignoring the casing:
     *                "request" and "service". Must contain the following parameter not more than once: "coverage". May
     *                contain the following parameter not more than once: "version".
     * @return {@link WcsRequest}. Never <code>null</code>
     * @throws UnsupportedRequestTypeException when the given request does not have the service type "wcs"
     */
    @SuppressWarnings("unchecked")
    public WcsRequest parse( HttpServletRequest request )
          throws UnsupportedRequestTypeException {
        if ( request == null )
            throw new IllegalArgumentException( "Request must not be null!" );
        String serviceName = evaluateServiceName( request );
        Map<String, String[]> normalizedParameterMap = normalizeKvpMap( request.getParameterMap() );
        checkParameters( normalizedParameterMap );
        WcsOperationType type = evaluateOperationType( normalizedParameterMap );
        switch ( type ) {
        case GETCAPABILITIES:
            return parseGetCapabilitiesRequest( serviceName, normalizedParameterMap );
        case DESCRIBECOVERAGE:
            return parseDescribeCoverageRequest( serviceName, normalizedParameterMap );
        case GETCOVERAGE:
            return parseGetCoverageRequest( serviceName, normalizedParameterMap );
        }
        throw new IllegalArgumentException( "Unrecognized operation type: " + type );
    }

    private WcsRequest parseGetCoverageRequest( String serviceName, Map<String, String[]> normalizedParameterMap ) {
        checkGetCoverageParameters( normalizedParameterMap );
        WcsServiceVersion version = evaluateVersion( normalizedParameterMap );
        String[] coverageParameter = normalizedParameterMap.get( COVERAGE );
        if ( coverageParameter == null || coverageParameter.length == 0 )
            return new WcsRequest( GETCOVERAGE, version, serviceName );
        else {
            List<String> separatedCoverages = extractCoverages( coverageParameter );
            if ( separatedCoverages.size() != 1 )
                throw new IllegalArgumentException( "GetCoverage requires exactly one coverage parameter!" );
            return new WcsRequest( GETCOVERAGE, version, separatedCoverages.get( 0 ), serviceName );
        }
    }

    private WcsRequest parseDescribeCoverageRequest( String serviceName, Map<String, String[]> normalizedParameterMap ) {
        WcsServiceVersion version = evaluateVersion( normalizedParameterMap );
        String[] coverageParameter = normalizedParameterMap.get( COVERAGE );
        if ( coverageParameter == null || coverageParameter.length == 0 )
            return new WcsRequest( DESCRIBECOVERAGE, version, serviceName );
        else {
            List<String> separatedCoverages = extractCoverages( coverageParameter );
            return new WcsRequest( DESCRIBECOVERAGE, version, separatedCoverages, serviceName );
        }
    }

    private List<String> extractCoverages( String[] coverageParameter ) {
        String firstCoverageParameter = coverageParameter[0];
        List<String> separatedCoverages = new ArrayList<String>();
        Collections.addAll( separatedCoverages, firstCoverageParameter.split( "," ) );
        return separatedCoverages;
    }

    private WcsRequest parseGetCapabilitiesRequest( String serviceName, Map<String, String[]> normalizedParameterMap ) {
        WcsServiceVersion version = evaluateVersion( normalizedParameterMap );
        return new WcsRequest( GETCAPABILITIES, version, serviceName );
    }

    private WcsServiceVersion evaluateVersion( Map<String, String[]> normalizedParameterMap ) {
        String[] versionParameters = normalizedParameterMap.get( VERSION );
        if ( versionParameters == null )
            return null;
        else {
            String value = versionParameters[0];
            if ( "1.0.0".equalsIgnoreCase( value ) )
                return WcsServiceVersion.VERSION_100;
            if ( "1.1.0".equalsIgnoreCase( value ) )
                return WcsServiceVersion.VERSION_110;
            if ( "2.0.0".equalsIgnoreCase( value ) )
                return WcsServiceVersion.VERSION_200;
            throw new IllegalArgumentException( "Unrecognized version " + value );
        }
    }

    private WcsOperationType evaluateOperationType( Map<String, String[]> normalizedParameterMap ) {
        String value = normalizedParameterMap.get( REQUEST )[0];
        if ( "GetCapabilities".equalsIgnoreCase( value ) )
            return WcsOperationType.GETCAPABILITIES;
        if ( "DescribeCoverage".equalsIgnoreCase( value ) )
            return WcsOperationType.DESCRIBECOVERAGE;
        if ( "GetCoverage".equalsIgnoreCase( value ) )
            return WcsOperationType.GETCOVERAGE;
        throw new IllegalArgumentException( "Unrecognized operation " + value );
    }

    private String evaluateServiceName( HttpServletRequest request ) {
        String serviceName = request.getServletPath();
        if ( serviceName == null )
            throw new IllegalArgumentException( "Service name must not be null!" );
        return serviceName;
    }

    private void checkParameters( Map<String, String[]> normalizedParameterMap )
          throws UnsupportedRequestTypeException {
        checkServiceParameter( normalizedParameterMap );
        checkRequestParameter( normalizedParameterMap );
    }

    private void checkServiceParameter( Map<String, String[]> normalizedParameterMap )
          throws UnsupportedRequestTypeException {
        String[] serviceParameter = normalizedParameterMap.get( SERVICE );
        if ( serviceParameter == null || serviceParameter.length == 0 ) {
            throw new IllegalArgumentException(
                  "Request must contain exactly one \"service\" parameter, ignoring the casing. None Given." );
        }
        if ( serviceParameter.length > 1 ) {
            throw new IllegalArgumentException(
                  "Request must contain exactly one \"service\" parameter, ignoring the casing. Given parameters:"
                  + Arrays.toString( serviceParameter ) );
        }
        String serviceType = serviceParameter[0];
        if ( !"wcs".equalsIgnoreCase( serviceType ) ) {
            throw new UnsupportedRequestTypeException(
                  "Request must contain a \"service\" parameter with value \"wcs\"" );
        }
    }

    private void checkRequestParameter( Map<String, String[]> normalizedParameterMap ) {
        String[] requestParameter = normalizedParameterMap.get( REQUEST );
        if ( requestParameter == null || requestParameter.length == 0 ) {
            throw new IllegalArgumentException(
                  "Request must contain exactly one \"request\" parameter, ignoring the casing. None Given." );
        }
        if ( requestParameter.length > 1 ) {
            throw new IllegalArgumentException(
                  "Request must contain exactly one \"request\" parameter, ignoring the casing. Given parameters:"
                  + Arrays.toString( requestParameter ) );
        }
    }

    private void checkGetCoverageParameters( Map<String, String[]> normalizedParameterMap ) {
        checkCoverageParameter( normalizedParameterMap );
        checkCrsParameter( normalizedParameterMap );
        checkBboxOrTimeParameter( normalizedParameterMap );
        checkWidthAndHeightOrResXAndResYParameter( normalizedParameterMap );
        checkFormatParameter( normalizedParameterMap );
    }

    private void checkCoverageParameter( Map<String, String[]> normalizedParameterMap ) {
        String[] coverageParameter = normalizedParameterMap.get( COVERAGE );
        if ( coverageParameter == null )
            throw new IllegalArgumentException( "Request must contain a \"coverage\" parameter, ignoring the casing. None given." );
        if ( coverageParameter.length == 0 || coverageParameter.length > 1 ) {
            throw new IllegalArgumentException(
                  "Request must contain exactly one \"coverage\" parameter, ignoring the casing. Given parameters:"
                  + Arrays.toString( coverageParameter ) );
        }
    }

    private void checkCrsParameter( Map<String, String[]> normalizedParameterMap ) {
        String[] crsParameter = normalizedParameterMap.get( CRS );
        if ( crsParameter == null || crsParameter.length == 0 ) {
            throw new IllegalArgumentException(
                  "Request must contain exactly one \"crs\" parameter, ignoring the casing. None Given." );
        }
        if ( crsParameter.length > 1 ) {
            throw new IllegalArgumentException(
                  "Request must contain exactly one \"crs\" parameter, ignoring the casing. Given parameters:"
                  + Arrays.toString( crsParameter ) );
        }
    }

    private void checkBboxOrTimeParameter( Map<String, String[]> normalizedParameterMap ) {
        String[] bboxParameter = normalizedParameterMap.get( BBOX );
        String[] timeParameter = normalizedParameterMap.get( TIME );
        if ( ( bboxParameter == null || bboxParameter.length == 0 ) && ( timeParameter == null || timeParameter.length == 0 ) ) {
            throw new IllegalArgumentException(
                  "Request must contain a \"bbox\" or \"time\" parameter, ignoring the casing. None Given." );
        }
        if ( !( bboxParameter == null || bboxParameter.length == 0 ) && bboxParameter.length > 1 ) {
            throw new IllegalArgumentException(
                  "Request must not contain more than one \"bbox\" parameter, ignoring the casing. Given parameters:"
                  + Arrays.toString( bboxParameter ) );
        }
        if ( !( timeParameter == null || timeParameter.length == 0 ) && timeParameter.length > 1 ) {
            throw new IllegalArgumentException(
                  "Request must not contain more than one \"time\" parameter, ignoring the casing. Given parameters:"
                  + Arrays.toString( timeParameter ) );
        }
    }

    private void checkWidthAndHeightOrResXAndResYParameter( Map<String, String[]> normalizedParameterMap ) {
        String[] widthParameter = normalizedParameterMap.get( WIDTH );
        String[] heightParameter = normalizedParameterMap.get( HEIGHT );
        String[] resxParameter = normalizedParameterMap.get( RESX );
        String[] resyParameter = normalizedParameterMap.get( RESY );
        if ( ( ( widthParameter == null || widthParameter.length == 0 ) || ( heightParameter == null || heightParameter.length == 0 ) ) && ( ( resxParameter == null || resxParameter.length == 0 ) || ( resyParameter == null || resyParameter.length == 0 ) ) ) {
            throw new IllegalArgumentException(
                  "Request must contain a \"width\" and \"height\" or \"resx\" and \"resy\" parameter, ignoring the casing." );
        }
        if ( !( widthParameter == null || widthParameter.length == 0 ) && widthParameter.length > 1 ) {
            throw new IllegalArgumentException(
                  "Request must not contain more than one \"width\" parameter, ignoring the casing. Given parameters:"
                  + Arrays.toString( widthParameter ) );
        }
        if ( !( heightParameter == null || heightParameter.length == 0 ) && heightParameter.length > 1 ) {
            throw new IllegalArgumentException(
                  "Request must not contain more than one \"height\" parameter, ignoring the casing. Given parameters:"
                  + Arrays.toString( heightParameter ) );
        }
        if ( !( resxParameter == null || resxParameter.length == 0 ) && resxParameter.length > 1 ) {
            throw new IllegalArgumentException(
                  "Request must not contain more than one \"resx\" parameter, ignoring the casing. Given parameters:"
                  + Arrays.toString( resxParameter ) );
        }
        if ( !( resyParameter == null || resyParameter.length == 0 ) && resyParameter.length > 1 ) {
            throw new IllegalArgumentException(
                  "Request must not contain more than one \"resy\" parameter, ignoring the casing. Given parameters:"
                  + Arrays.toString( resyParameter ) );
        }
    }

    private void checkFormatParameter( Map<String, String[]> normalizedParameterMap ) {
        String[] formatParameter = normalizedParameterMap.get( FORMAT );
        if ( formatParameter == null || formatParameter.length == 0 ) {
            throw new IllegalArgumentException(
                  "Request must contain exactly one \"format\" parameter, ignoring the casing. None Given." );
        }
        if ( formatParameter.length > 1 ) {
            throw new IllegalArgumentException(
                  "Request must contain exactly one \"format\" parameter, ignoring the casing. Given parameters:"
                  + Arrays.toString( formatParameter ) );
        }
    }
}

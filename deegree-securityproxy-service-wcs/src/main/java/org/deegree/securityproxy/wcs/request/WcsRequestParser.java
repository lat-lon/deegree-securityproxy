package org.deegree.securityproxy.wcs.request;

import static java.lang.String.format;
import static org.deegree.securityproxy.request.KvpNormalizer.normalizeKvpMap;
import static org.deegree.securityproxy.wcs.domain.WcsOperationType.DESCRIBECOVERAGE;
import static org.deegree.securityproxy.wcs.domain.WcsOperationType.GETCAPABILITIES;
import static org.deegree.securityproxy.wcs.domain.WcsOperationType.GETCOVERAGE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.deegree.securityproxy.request.OwsRequestParser;
import org.deegree.securityproxy.request.UnsupportedRequestTypeException;
import org.deegree.securityproxy.wcs.domain.WcsOperationType;
import org.deegree.securityproxy.wcs.domain.WcsServiceVersion;

/**
 * Parses an incoming {@link HttpServletRequest} into a {@link WcsRequest}.
 * 
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author last edited by: $Author: erben $
 * @version $Revision: $, $Date: $
 */
public class WcsRequestParser implements OwsRequestParser {

    private static final String REQUEST = "request";

    private static final String SERVICE = "service";

    private static final String VERSION = "version";

    private static final String COVERAGE = "coverage";

    private static final String CRS = "crs";

    private static final String BBOX = "bbox";

    private static final String TIME = "time";

    private static final String WIDTH = "width";

    private static final String HEIGHT = "height";

    private static final String RESX = "resx";

    private static final String RESY = "resy";

    private static final String FORMAT = "format";

    @Override
    @SuppressWarnings("unchecked")
    public WcsRequest parse( HttpServletRequest request )
                            throws UnsupportedRequestTypeException {
        if ( request == null )
            throw new IllegalArgumentException( "Request must not be null!" );
        String serviceName = evaluateServiceName( request );
        Map<String, String[]> normalizedParameterMap = normalizeKvpMap( request.getParameterMap() );
        checkParameters( normalizedParameterMap );
        return parseRequest( serviceName, normalizedParameterMap );
    }

    private WcsRequest parseRequest( String serviceName, Map<String, String[]> normalizedParameterMap ) {
        WcsOperationType type = evaluateOperationType( normalizedParameterMap );
        switch ( type ) {
        case GETCAPABILITIES:
            return parseGetCapabilitiesRequest( serviceName, normalizedParameterMap );
        case DESCRIBECOVERAGE:
            return parseDescribeCoverageRequest( serviceName, normalizedParameterMap );
        case GETCOVERAGE:
            return parseGetCoverageRequest( serviceName, normalizedParameterMap );
        default:
            throw new IllegalArgumentException( "Unrecognized operation type: " + type );
        }
    }

    private WcsRequest parseGetCoverageRequest( String serviceName, Map<String, String[]> normalizedParameterMap ) {
        checkGetCoverageParameters( normalizedParameterMap );
        WcsServiceVersion version = evaluateVersion( normalizedParameterMap );
        String[] coverageParameter = normalizedParameterMap.get( COVERAGE );
        if ( isNotSet( coverageParameter ) )
            return new WcsRequest( GETCOVERAGE, version, serviceName );

        List<String> separatedCoverages = extractCoverages( coverageParameter );
        if ( separatedCoverages.size() != 1 )
            throw new IllegalArgumentException( "GetCoverage requires exactly one coverage parameter!" );
        return new WcsRequest( GETCOVERAGE, version, separatedCoverages.get( 0 ), serviceName );
    }

    private WcsRequest parseDescribeCoverageRequest( String serviceName, Map<String, String[]> normalizedParameterMap ) {
        WcsServiceVersion version = evaluateVersion( normalizedParameterMap );
        String[] coverageParameter = normalizedParameterMap.get( COVERAGE );
        if ( isNotSet( coverageParameter ) )
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
        String servletPath = request.getServletPath();
        if ( servletPath == null )
            throw new IllegalArgumentException( "Service name must not be null!" );
        if ( servletPath.contains( "/" ) ) {
            String[] splittedServletPath = servletPath.split( "/" );
            return splittedServletPath[splittedServletPath.length - 1];
        }
        return servletPath;
    }

    private void checkParameters( Map<String, String[]> normalizedParameterMap )
                            throws UnsupportedRequestTypeException {
        checkServiceParameter( normalizedParameterMap );
        checkRequestParameter( normalizedParameterMap );
    }

    private void checkServiceParameter( Map<String, String[]> normalizedParameterMap )
                            throws UnsupportedRequestTypeException {
        String serviceType = checkSingleRequiredParameter( normalizedParameterMap, SERVICE );
        if ( !"wcs".equalsIgnoreCase( serviceType ) ) {
            String msg = "Request must contain a \"service\" parameter with value \"wcs\"";
            throw new UnsupportedRequestTypeException( msg );
        }
    }

    private void checkRequestParameter( Map<String, String[]> normalizedParameterMap ) {
        checkSingleRequiredParameter( normalizedParameterMap, REQUEST );
    }

    private void checkGetCoverageParameters( Map<String, String[]> normalizedParameterMap ) {
        checkCoverageParameter( normalizedParameterMap );
        checkCrsParameter( normalizedParameterMap );
        checkBboxOrTimeParameter( normalizedParameterMap );
        checkWidthAndHeightOrResXAndResYParameter( normalizedParameterMap );
        checkFormatParameter( normalizedParameterMap );
    }

    private void checkCoverageParameter( Map<String, String[]> normalizedParameterMap ) {
        checkSingleRequiredParameter( normalizedParameterMap, COVERAGE );
    }

    private void checkCrsParameter( Map<String, String[]> normalizedParameterMap ) {
        checkSingleRequiredParameter( normalizedParameterMap, CRS );
    }

    private void checkBboxOrTimeParameter( Map<String, String[]> normalizedParameterMap ) {
        String[] bboxParameter = normalizedParameterMap.get( BBOX );
        String[] timeParameter = normalizedParameterMap.get( TIME );
        if ( isNotSet( bboxParameter ) && isNotSet( timeParameter ) ) {
            String msg = "Request must contain a \"bbox\" or \"time\" parameter, ignoring the casing. None Given.";
            throw new IllegalArgumentException( msg );
        }
        if ( !isNotSet( bboxParameter ) && isNotSingle( bboxParameter ) ) {
            throwException( BBOX, bboxParameter );
        }
        if ( !isNotSet( timeParameter ) && isNotSingle( timeParameter ) ) {
            throwException( TIME, timeParameter );
        }
    }

    private void checkWidthAndHeightOrResXAndResYParameter( Map<String, String[]> normalizedParameterMap ) {
        String[] widthParameter = normalizedParameterMap.get( WIDTH );
        String[] heightParameter = normalizedParameterMap.get( HEIGHT );
        String[] resxParameter = normalizedParameterMap.get( RESX );
        String[] resyParameter = normalizedParameterMap.get( RESY );
        boolean isNotWidthAndHeight = isNotSet( widthParameter ) || isNotSet( heightParameter );
        boolean isNotResXAndResY = isNotSet( resxParameter ) || isNotSet( resyParameter );
        if ( isNotWidthAndHeight && isNotResXAndResY ) {
            String msg = "Request must contain a \"width\" and \"height\" or \"resx\" and \"resy\" "
                         + "parameter, ignoring the casing.";
            throw new IllegalArgumentException( msg );
        }
        if ( !isNotSet( widthParameter ) && isNotSingle( widthParameter ) ) {
            throwException( WIDTH, widthParameter );
        }
        if ( !isNotSet( heightParameter ) && isNotSingle( heightParameter ) ) {
            throwException( HEIGHT, heightParameter );
        }
        if ( !isNotSet( resxParameter ) && isNotSingle( resxParameter ) ) {
            throwException( RESX, resxParameter );
        }
        if ( !isNotSet( resyParameter ) && isNotSingle( resyParameter ) ) {
            throwException( RESY, resyParameter );
        }
    }

    private void checkFormatParameter( Map<String, String[]> normalizedParameterMap ) {
        checkSingleRequiredParameter( normalizedParameterMap, FORMAT );
    }

    private String checkSingleRequiredParameter( Map<String, String[]> normalizedParameterMap, String parameterName ) {
        String[] parameterValue = checkRequiredParameter( normalizedParameterMap, parameterName );
        if ( isNotSingle( parameterValue ) ) {
            throwException( parameterName, parameterValue );
        }
        return parameterValue[0];
    }

    private String[] checkRequiredParameter( Map<String, String[]> normalizedParameterMap, String parameterName ) {
        String[] parameterValue = normalizedParameterMap.get( parameterName );
        if ( isNotSet( parameterValue ) ) {
            throwException( parameterName );
        }
        return parameterValue;
    }

    private void throwException( String parameterName ) {
        String msg = "Request must contain exactly one %s parameter, ignoring the casing. None Given.";
        throw new IllegalArgumentException( format( msg, parameterName ) );
    }

    private void throwException( String parameterName, String[] parameterValue ) {
        String msg = "Request must contain exactly one '%s' parameter, ignoring the casing. Given parameters: %s";
        throw new IllegalArgumentException( format( msg, parameterName, asString( parameterValue ) ) );
    }

    private boolean isNotSet( String[] parameterValue ) {
        return parameterValue == null || parameterValue.length == 0;
    }

    private boolean isNotSingle( String[] parameterValue ) {
        return parameterValue.length > 1;
    }

    private String asString( String[] arrayParameter ) {
        return Arrays.toString( arrayParameter );
    }
}

package org.deegree.securityproxy.request;

import static org.deegree.securityproxy.commons.WcsOperationType.DESCRIBECOVERAGE;
import static org.deegree.securityproxy.commons.WcsOperationType.GETCAPABILITIES;
import static org.deegree.securityproxy.commons.WcsOperationType.GETCOVERAGE;
import static org.deegree.securityproxy.request.KvpNormalizer.normalizeKvpMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.deegree.securityproxy.commons.WcsOperationType;
import org.deegree.securityproxy.commons.WcsServiceVersion;

/**
 * Parses an incoming {@link HttpServletRequest} into a {@link WcsRequest}.
 * 
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
public class WcsRequestParser {

    private static final String REQUEST = "request";

    private static final String SERVICE = "service";

    private static final String VERSION = "version";

    private static final Object COVERAGE = "coverage";

    /**
     * Parses an incoming {@link HttpServletRequest} into a {@link WcsRequest}.
     * 
     * @param request
     *            never <code>null</code>. Must contain the following parameters exactly once ignoring the casing:
     *            "request" and "service". Must contain the following parameter not more than once: "coverage". May
     *            contain the following parameter not more than once: "version".
     * @return {@link WcsRequest}. Never <code>null</code>
     * @throws UnsupportedRequestTypeException
     *             when the given request does not have the service type "wcs"
     */
    @SuppressWarnings("unchecked")
    public WcsRequest parse( HttpServletRequest request )
                            throws UnsupportedRequestTypeException {
        if ( request == null )
            throw new IllegalArgumentException( "Request must not be null!" );
        Map<String, String[]> normalizedParameterMap = normalizeKvpMap( request.getParameterMap() );
        checkParameters( normalizedParameterMap );
        WcsOperationType type = evaluateOperationType( normalizedParameterMap );
        switch ( type ) {
        case GETCAPABILITIES:
            return parseGetCapabilitiesRequest( normalizedParameterMap );
        case DESCRIBECOVERAGE:
            return parseDescribeCoverageRequest( normalizedParameterMap );
        case GETCOVERAGE:
            return parseGetCoverageRequest( normalizedParameterMap );
        }
        throw new IllegalArgumentException( "Unrecognized operation type: "+type);
    }

    private WcsRequest parseGetCoverageRequest( Map<String, String[]> normalizedParameterMap ) {
        WcsServiceVersion version = evaluateVersion( normalizedParameterMap );
        String[] coverageParameter = normalizedParameterMap.get( COVERAGE );
        if ( coverageParameter == null || coverageParameter.length == 0 )
            return new WcsRequest( GETCOVERAGE, version );
        else {
            List<String> separatedCoverages = extractCoverages( coverageParameter );
            if ( ( separatedCoverages ).size() != 1 )
                throw new IllegalArgumentException( "GetCoverage requires exactly one coverage parameter!" );
            return new WcsRequest( GETCOVERAGE, version, separatedCoverages.get( 0 ) );
        }
    }

    private WcsRequest parseDescribeCoverageRequest( Map<String, String[]> normalizedParameterMap ) {
        WcsServiceVersion version = evaluateVersion( normalizedParameterMap );
        String[] coverageParameter = normalizedParameterMap.get( COVERAGE );
        if ( coverageParameter == null || coverageParameter.length == 0 )
            return new WcsRequest( DESCRIBECOVERAGE, version );
        else {
            List<String> separatedCoverages = extractCoverages( coverageParameter );
            return new WcsRequest( DESCRIBECOVERAGE, version, separatedCoverages );
        }
    }

    private List<String> extractCoverages( String[] coverageParameter ) {
        String firstCoverageParameter = coverageParameter[0];
        List<String> separatedCoverages = new ArrayList<String>();
        Collections.addAll( separatedCoverages, firstCoverageParameter.split( "," ) );
        return separatedCoverages;
    }

    private WcsRequest parseGetCapabilitiesRequest( Map<String, String[]> normalizedParameterMap ) {
        WcsServiceVersion version = evaluateVersion( normalizedParameterMap );
        return new WcsRequest( GETCAPABILITIES, version );
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

    private void checkParameters( Map<String, String[]> normalizedParameterMap )
                            throws UnsupportedRequestTypeException {
        checkServiceParameter( normalizedParameterMap );
        checkRequestParameter( normalizedParameterMap );
        checkCoverageParameter( normalizedParameterMap );
    }

    private void checkCoverageParameter( Map<String, String[]> normalizedParameterMap ) {
        String[] coverageParameter = normalizedParameterMap.get( COVERAGE );
        if ( coverageParameter == null )
            return;
        if ( coverageParameter.length > 1 ) {
            throw new IllegalArgumentException(
                                                "Request must contain exactly one \"coverage\" parameter, ignoring the casing. Given parameters:"
                                                                        + Arrays.toString( coverageParameter ) );
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
}

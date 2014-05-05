//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2011 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact information:

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/
package org.deegree.securityproxy.request;

import static java.lang.String.format;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Contains some useful methods to parse {@link OwsRequest} send with method GET.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class GetOwsRequestParserUtils {

    public static String checkSingleRequiredParameter( Map<String, String[]> normalizedParameterMap,
                                                       String parameterName ) {
        String[] parameterValue = checkRequiredParameter( normalizedParameterMap, parameterName );
        if ( isNotSingle( parameterValue ) )
            throwException( parameterName, parameterValue );
        return parameterValue[0];
    }

    public static String[] checkRequiredParameter( Map<String, String[]> normalizedParameterMap, String parameterName ) {
        String[] parameterValue = normalizedParameterMap.get( parameterName );
        if ( isNotSet( parameterValue ) ) {
            throwException( parameterName );
        }
        return parameterValue;
    }

    public static void throwException( String parameterName ) {
        String msg = "Request must contain exactly one %s parameter, ignoring the casing. None Given.";
        throw new IllegalArgumentException( format( msg, parameterName ) );
    }

    public static void throwException( String parameterName, String[] parameterValue ) {
        String msg = "Request must contain exactly one '%s' parameter, ignoring the casing. Given parameters: %s";
        throw new IllegalArgumentException( format( msg, parameterName, asString( parameterValue ) ) );
    }

    public static boolean isNotSet( String[] parameterValue ) {
        return parameterValue == null || parameterValue.length == 0;
    }

    public static boolean isNotSingle( String[] parameterValue ) {
        if ( isNotSet( parameterValue ) )
            throw new IllegalArgumentException( "parameter value must not be null or empty! Is: "
                                                + asString( parameterValue ) );
        return parameterValue.length > 1;
    }

    /**
     * Evaluates optional version parameter, returns the default version if the version parameter is missing or empty.
     * 
     * @param parameterName
     *            name of the version parameter, never <code>null</code>
     * @param normalizedParameterMap
     *            list of KVP parameters, never <code>null</code>
     * @param defaultVersion
     *            version to return if the version parameter is missing or empty, may be <code>null</code>
     * @return the requested version (if supported) or the default version if the version parameter is missing (
     *         <code>null</code> if the default version is null)
     * @throws IllegalArgumentException
     *             - version could not be parsed
     */
    public static OwsServiceVersion evaluateVersion( String parameterName,
                                                     Map<String, String[]> normalizedParameterMap,
                                                     OwsServiceVersion defaultVersion ) {
        String versionParam = parseVersion( normalizedParameterMap, parameterName );
        if ( versionParam != null && !versionParam.isEmpty() ) {
            return new OwsServiceVersion( versionParam );
        }
        return defaultVersion;
    }

    /**
     * Evaluates optional version parameter, throws an {@link IllegalArgumentException} if the parameter VERSION is
     * missing or empty.
     * 
     * @param parameterName
     *            name of the VERSION parameter, never <code>null</code>
     * @param normalizedParameterMap
     * @return the requested version (if supported) or <code>null</code> if the version parameter is missing
     * @param supportedVersion
     * @return the
     * @throws IllegalArgumentException
     *             - version parameter is missing or version could not be parsed
     */
    public static OwsServiceVersion evaluateVersion( String parameterName,
                                                     Map<String, String[]> normalizedParameterMap,
                                                     List<OwsServiceVersion> supportedVersion ) {
        String versionParam = parseVersion( normalizedParameterMap, parameterName );
        if ( versionParam != null && !versionParam.isEmpty() ) {
            OwsServiceVersion version = new OwsServiceVersion( versionParam );
            if ( supportedVersion.contains( version ) )
                return version;
        }
        throw new IllegalArgumentException( "Unrecognized version " + versionParam );
    }

    private static String parseVersion( Map<String, String[]> normalizedParameterMap, String parameterName ) {
        String[] versionParameters = normalizedParameterMap.get( parameterName );
        if ( versionParameters == null || versionParameters.length == 0 )
            return null;
        return versionParameters[0];
    }

    private static String asString( String[] arrayParameter ) {
        return Arrays.toString( arrayParameter );
    }

}
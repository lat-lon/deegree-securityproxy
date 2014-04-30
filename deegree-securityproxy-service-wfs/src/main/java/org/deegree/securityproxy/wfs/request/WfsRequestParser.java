//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2014 by:
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
package org.deegree.securityproxy.wfs.request;

import static org.deegree.securityproxy.request.GetOwsRequestParserUtils.checkSingleRequiredParameter;
import static org.deegree.securityproxy.request.GetOwsRequestParserUtils.evaluateVersion;
import static org.deegree.securityproxy.request.KvpNormalizer.normalizeKvpMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.request.OwsRequestParser;
import org.deegree.securityproxy.request.OwsServiceVersion;
import org.deegree.securityproxy.request.UnsupportedRequestTypeException;

public class WfsRequestParser implements OwsRequestParser {

    public static final OwsServiceVersion VERSION_110 = new OwsServiceVersion( 1, 1, 0 );

    public static final String WFS_SERVICE = "WFS";

    public static final String GETCAPABILITIES = "GetCapabilities";

    public static final String DESCRIBEFEATURETYPE = "DescribeFeatureType";

    public static final String GETFEATURE = "GetFeature";

    private static final String REQUEST = "request";

    private static final String SERVICE = "service";

    private static final String VERSION = "version";

    private List<OwsServiceVersion> supportedVersion = Collections.singletonList( VERSION_110 );

    @Override
    @SuppressWarnings("unchecked")
    public OwsRequest parse( HttpServletRequest request )
                            throws UnsupportedRequestTypeException {
        if ( request == null )
            throw new IllegalArgumentException( "Request must not be null!" );
        String method = request.getMethod();
        if ( "GET".equals( method ) ) {
            Map<String, String[]> normalizedParameterMap = normalizeKvpMap( request.getParameterMap() );
            checkParameters( normalizedParameterMap );
            return parseGetRequest( normalizedParameterMap );
        }
        throw new IllegalArgumentException( "Only GET requests are supported yet!" );

    }

    private WfsRequest parseGetRequest( Map<String, String[]> normalizedParameterMap )
                            throws UnsupportedRequestTypeException {
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

    private WfsRequest parseGetFeatureRequest( Map<String, String[]> normalizedParameterMap ) {
        // TODO Auto-generated method stub
        return null;
    }

    private WfsRequest parseDescribeFeatureRequest( Map<String, String[]> normalizedParameterMap ) {
        // TODO Auto-generated method stub
        return null;
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

}
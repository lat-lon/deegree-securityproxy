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
package org.deegree.securityproxy.wcs.responsefilter;

import java.util.List;

import org.deegree.securityproxy.wcs.authentication.WcsGeometryFilterInfo;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * Retrieve the clipping geometry from a list of {@link WcsGeometryFilterInfo}s. If multiple
 * {@link WcsGeometryFilterInfo} for one coverage exist the first one is parsed as geometry. There is no mechanism to
 * detect inconsistent data.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class GeometryRetrieverImpl implements GeometryRetriever {

    @Override
    public Geometry retrieveGeometry( String coverageName, List<WcsGeometryFilterInfo> geometryFilterInfos )
                            throws IllegalArgumentException, ParseException {
        checkParameters( coverageName, geometryFilterInfos );

        String geometryAsString = retrieveGeometryFromList( coverageName, geometryFilterInfos );
        if ( geometryAsString != null )
            return parseGeometry( geometryAsString );
        return null;
    }

    private void checkParameters( String coverageName, List<WcsGeometryFilterInfo> geometryFilterInfos ) {
        if ( coverageName == null )
            throw new IllegalArgumentException( "Coverage name must not be null!" );
        if ( geometryFilterInfos == null )
            throw new IllegalArgumentException( "GeometryFilterInfos name must not be null!" );
    }

    private String retrieveGeometryFromList( String coverageName, List<WcsGeometryFilterInfo> geometryFilterInfos ) {
        for ( WcsGeometryFilterInfo wcsGeometryFilterInfo : geometryFilterInfos ) {
            if ( coverageName.equals( wcsGeometryFilterInfo.getCoverageName() ) ) {
                return wcsGeometryFilterInfo.getGeometryString();
            }
        }
        return null;
    }

    private Geometry parseGeometry( String geometryFromDb )
                            throws ParseException {
        String wkt = normaliseWkt( geometryFromDb );
        WKTReader wktReader = new WKTReader();
        return wktReader.read( wkt );
    }

    String normaliseWkt( String geometryAsString ) {
        if ( geometryAsString.startsWith( "SRID" ) )
            geometryAsString = geometryAsString.substring( geometryAsString.indexOf( ";" ) + 1 );
        return geometryAsString;
    }

}
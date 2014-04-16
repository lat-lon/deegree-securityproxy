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
package org.deegree.securityproxy.service.commons.responsefilter.clipping.geometry;

import java.util.List;

import org.deegree.securityproxy.authentication.ows.raster.GeometryFilterInfo;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * The union of all layers with clipping area is calculated. If multiple {@link GeometryFilterInfo} for one layer exist
 * the first one is parsed as geometry. There is no mechanism to detect inconsistent data.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class GeometryRetrieverImpl implements GeometryRetriever {

    @Override
    public Geometry retrieveGeometry( List<String> layerNames, List<GeometryFilterInfo> geometryFilterInfos )
                            throws IllegalArgumentException, ParseException {
        checkParameters( layerNames, geometryFilterInfos );
        return calculateGeometry( layerNames, geometryFilterInfos );
    }

    String normaliseWkt( String geometryAsString ) {
        if ( geometryAsString.startsWith( "SRID" ) )
            return geometryAsString.substring( geometryAsString.indexOf( ";" ) + 1 );
        return geometryAsString;
    }

    private Geometry calculateGeometry( List<String> layerNames, List<GeometryFilterInfo> geometryFilterInfos )
                            throws ParseException {
        Geometry union = null;
        for ( String layerName : layerNames ) {
            Geometry geometryOfLayer = retrieveGeometryForLayer( geometryFilterInfos, layerName );
            union = addToUnion( union, geometryOfLayer );
        }
        return union;
    }

    private Geometry retrieveGeometryForLayer( List<GeometryFilterInfo> geometryFilterInfos, String layerName )
                            throws ParseException {
        for ( GeometryFilterInfo wcsGeometryFilterInfo : geometryFilterInfos ) {
            if ( layerName.equals( wcsGeometryFilterInfo.getLayerName() ) ) {
                String geometryString = wcsGeometryFilterInfo.getGeometryString();
                if ( geometryString != null )
                    return parseGeometry( geometryString );
            }
        }
        return null;
    }

    private Geometry addToUnion( Geometry union, Geometry layerGeometry ) {
        if ( union == null )
            return layerGeometry;
        if ( layerGeometry == null )
            return union;
        return union.union( layerGeometry );
    }

    private Geometry parseGeometry( String geometryFromDb )
                            throws ParseException {
        String wkt = normaliseWkt( geometryFromDb );
        WKTReader wktReader = new WKTReader();
        return wktReader.read( wkt );
    }

    private void checkParameters( List<String> layerNames, List<GeometryFilterInfo> geometryFilterInfos ) {
        if ( layerNames == null || layerNames.isEmpty() )
            throw new IllegalArgumentException( "List of layer names must not be null or empty!" );
        if ( geometryFilterInfos == null )
            throw new IllegalArgumentException( "GeometryFilterInfos name must not be null!" );
    }

}
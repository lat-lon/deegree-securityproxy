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
package org.deegree.securityproxy.authentication.ows.raster;

/**
 * Encapulates a geometry filter, containing the layer name and geometry
 * 
 * @author <a href="mailto:erben@lat-lon.de">Alexander Erben</a>
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class GeometryFilterInfo {

    private final String layerName;

    private final String geometry;

    /**
     * Instantiates a new {@link GeometryFilterInfo} with full extend (null geometry)
     * 
     * @param layerName
     *            neither <code>null</code> nor empty
     * @throws IllegalArgumentException
     *             if coverageName is <code>null</code>
     */
    public GeometryFilterInfo( String layerName ) throws IllegalArgumentException {
        this( layerName, null );
    }

    /**
     * @param layerName
     *            neither <code>null</code> nor empty
     * @param geometry
     *            the geometry limiting the visibility of the coverage, if <code>null</code> the extend is not limited
     * @throws IllegalArgumentException
     *             if coverageName is <code>null</code>
     */
    public GeometryFilterInfo( String layerName, String geometry ) throws IllegalArgumentException {
        checkRequiredParameter( layerName );
        this.layerName = layerName;
        this.geometry = geometry;

    }

    /**
     * @return the name of the coverage, never <code>null</code>
     */
    public String getLayerName() {
        return layerName;
    }

    /**
     * @return the geometry limiting the coverage, may be <code>null</code> if the whole extend is visible
     */
    public String getGeometryString() {
        return geometry;
    }

    private void checkRequiredParameter( String coverageName ) {
        if ( coverageName == null )
            throw new IllegalArgumentException( "Required parameter layer name is null" );
        if ( coverageName.isEmpty() )
            throw new IllegalArgumentException( "Required parameter layer name is empty" );
    }

}
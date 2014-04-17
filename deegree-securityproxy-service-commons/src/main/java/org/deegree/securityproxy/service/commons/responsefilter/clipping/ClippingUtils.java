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
package org.deegree.securityproxy.service.commons.responsefilter.clipping;

import static org.geotools.geometry.jts.JTS.transform;
import static org.geotools.referencing.CRS.findMathTransform;

import org.apache.log4j.Logger;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * Contains some convenience methods for clipping.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public final class ClippingUtils {

    private static final Logger LOG = Logger.getLogger( ClippingUtils.class );

    private ClippingUtils() {
    }

    /**
     * Transforms the passed geometry into the CRS of the visible area
     * 
     * @param visibleAreaCrs
     *            in coordinate system of the geometry to transform (sourceCrs), never <code>null</code>
     * @param visibleArea
     *            the geometry to transform to the targetCrs, never <code>null</code>
     * @param targetCrs
     *            the targetCrs to transform the geometry to never <code>null</code>
     * @return the transformed geometry in targetCrs, never <code>null</code>
     * @throws FactoryException
     * @throws TransformException
     */
    public static Geometry transformGeometry( CoordinateReferenceSystem visibleAreaCrs, Geometry visibleArea,
                                              CoordinateReferenceSystem targetCrs )
                            throws FactoryException, TransformException {
        if ( visibleArea != null ) {
            MathTransform transformVisibleAreaToTargetCrs = findMathTransform( visibleAreaCrs, targetCrs );
            Geometry visibleAreaInImageCrs = transform( visibleArea, transformVisibleAreaToTargetCrs );
            LOG.debug( "Transformed visible geometry: " + visibleAreaInImageCrs );
            return visibleAreaInImageCrs;
        }
        LOG.debug( "Visible area is null: transformation is not required!" );
        return null;
    }

    /**
     * Calculates the geometry visible after clipping. Both geometries must be in the same crs!
     * 
     * @param imageGeometry
     *            the geometry of the image, never <code>null</code>
     * @param visibleArea
     *            the visible area, never <code>null</code>
     * @return the geometry visible after clipping
     * @throws TransformException
     * @throws FactoryException
     */
    public static Geometry calculateGeometryVisibleAfterClipping( Geometry imageGeometry, Geometry visibleArea )
                            throws FactoryException, TransformException {
        if ( visibleArea.contains( imageGeometry ) ) {
            return imageGeometry;
        } else if ( visibleArea.intersects( imageGeometry ) ) {
            return visibleArea.intersection( imageGeometry );
        }
        return new GeometryFactory().toGeometry( new Envelope() );
    }

    /**
     * Clipping is required, when the visibleAreaGeometry is not null and the imageGeometry is not complete located into
     * the visibleAreaGeometry
     * 
     * @param imageGeometry
     *            the geometry of the image, never <code>null</code>
     * @param visibleAreaGeometry
     *            the geometry of the visible area, never <code>null</code>
     * @return <code>true</code> if clipping is required, <code>false</code> otherwise
     */
    public static boolean isClippingRequired( Geometry imageGeometry, Geometry visibleAreaGeometry ) {
        return visibleAreaGeometry != null && !visibleAreaGeometry.contains( imageGeometry );
    }

}

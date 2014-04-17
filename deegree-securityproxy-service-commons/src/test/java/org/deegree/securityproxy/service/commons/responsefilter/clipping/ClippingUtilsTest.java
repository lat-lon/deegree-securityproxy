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

import static org.deegree.securityproxy.service.commons.responsefilter.clipping.ClippingUtils.calculateGeometryVisibleAfterClipping;
import static org.deegree.securityproxy.service.commons.responsefilter.clipping.ClippingUtils.isClippingRequired;
import static org.deegree.securityproxy.service.commons.responsefilter.clipping.ClippingUtils.transformGeometry;
import static org.geotools.referencing.CRS.decode;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class ClippingUtilsTest {

    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

    /*
     * #calculateGeometryVisibleAfterClipping()
     */
    @Test
    public void testCalculateGeometryVisibleAfterClippingCompleteOutsideVisibleArea()
                            throws Exception {
        Geometry geometry = createTestGeometryInEpsg26912();
        Geometry geometryVisibleAfterClipping = calculateGeometryVisibleAfterClipping( geometry,
                                                                                       createWholeImageInvisibleEnvelopeInImageCrs() );
        double area = geometryVisibleAfterClipping.getArea();
        assertThat( area, is( 0d ) );
    }

    @Test
    public void testCalculateGeometryVisibleAfterClippingCompleteInsideVisibleArea()
                            throws Exception {
        Geometry geometry = createTestGeometryInEpsg26912();
        Geometry geometryVisibleAfterClipping = calculateGeometryVisibleAfterClipping( geometry,
                                                                                       createWholeImageVisibleEnvelopeInImageCrs() );
        double area = geometryVisibleAfterClipping.getArea();
        assertThat( area, is( 1.501452E8 ) );
    }

    @Test
    public void testCalculateGeometryVisibleAfterClippingIntersectingVisbleArea()
                            throws Exception {
        Geometry geometry = createTestGeometryInEpsg26912();
        Geometry geometryVisibleAfterClipping = calculateGeometryVisibleAfterClipping( geometry,
                                                                                       createImageInsersectsEnvelopeInImageCrs() );
        double area = geometryVisibleAfterClipping.getArea();

        // Expected 4.30674E7
        double expectedArea = new Envelope( 446591.945, 457331.945, 4437805.000, 4441815.000 ).getArea();
        assertThat( area, is( expectedArea ) );
    }

    /*
     * #transformGeometry()
     */
    @Test
    public void testTransformGeometry()
                            throws Exception {
        Geometry geometry = createTestGeometryInEpsg26912();

        Geometry transformedGeometry = transformGeometry( decode( "EPSG:26912" ), geometry, decode( "EPSG:4326", true ) );

        Geometry expectedGeometry = asGeometry( -111.625671116814, -111.500779260647, 39.9990119740282,
                                                40.1255737525128 );
        Geometry expectedGeometryBuffered = expectedGeometry.buffer( 0.01 );
        assertTrue( expectedGeometryBuffered.contains( transformedGeometry ) );
    }

    /*
     * #isClippingRequired()
     */
    @Test
    public void testIsClippingRequiredWithNullClippingGeometryShouldReturnFalse()
                            throws Exception {

        Geometry geometry = createTestGeometryInEpsg26912();
        boolean isClippingRequired = isClippingRequired( geometry, null );
        assertThat( isClippingRequired, is( false ) );
    }

    @Test
    public void testIsClippingRequiredWithImageGeometryIntoClippingGeometryShouldReturnFalse()
                            throws Exception {

        Geometry geometry = createTestGeometryInEpsg26912();
        Geometry clippingGeometry = geometry.buffer( 1 );
        boolean isClippingRequired = isClippingRequired( geometry, clippingGeometry );
        assertThat( isClippingRequired, is( false ) );
    }

    @Test
    public void testIsClippingRequiredWithClippingGeometryIntoImageGeometryShouldReturnTrue()
                            throws Exception {

        Geometry clippingGeometry = createTestGeometryInEpsg26912();
        Geometry geometry = clippingGeometry.buffer( 1 );
        boolean isClippingRequired = isClippingRequired( geometry, clippingGeometry );
        assertThat( isClippingRequired, is( true ) );
    }

    private Geometry createTestGeometryInEpsg26912() {
        return asGeometry( 446591.945, 457331.945, 4427835.000, 4441815.000 );
    }

    private Geometry createWholeImageVisibleEnvelopeInImageCrs() {
        return asGeometry( 446580.945, 457531.945, 4427805.000, 4441915.000 );
    }

    private Geometry createWholeImageInvisibleEnvelopeInImageCrs() {
        return asGeometry( 446580.945, 446581.945, 4427805.000, 4427806.000 );
    }

    private Geometry createImageInsersectsEnvelopeInImageCrs() {
        return asGeometry( 446580.945, 457351.945, 4437805.000, 4441915.000 );
    }

    private Geometry asGeometry( double x1, double x2, double y1, double y2 ) {
        Envelope envelope = new Envelope( x1, x2, y1, y2 );
        return GEOMETRY_FACTORY.toGeometry( envelope );
    }
}
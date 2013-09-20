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
package org.deegree.securityproxy.responsefilter.wcs;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.junit.Ignore;
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
public class GeotiffClipperTest {

    private GeotiffClipper geotiffClipper = new GeotiffClipper();

    @Test(expected = IllegalArgumentException.class)
    public void testCalculateClippedImageWithNullImageStreamShouldFail()
                            throws Exception {
        geotiffClipper.calculateClippedImage( null, mockClippingGeometry(), mockOutputStream() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCalculateClippedImageWithNullClippingGeometryShouldFail()
                            throws Exception {
        geotiffClipper.calculateClippedImage( mockInputStream(), null, mockOutputStream() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCalculateClippedImageWithNullOutputStreamShouldFail()
                            throws Exception {
        geotiffClipper.calculateClippedImage( mockInputStream(), mockClippingGeometry(), null );
    }

    @Test
    public void testCalculateClippedImageInsideVisibleArea()
                            throws Exception {
        File originalFile = createNewFile( "dem30_geotiff_tiled.tiff" );
        File newFile = createNewTempFile();

        geotiffClipper.calculateClippedImage( createInputStreamFrom( originalFile ), createWholeWorldVisibleGeometry(),
                                              createOutputStreamFrom( newFile ) );

        int heightOriginalImage = ImageIO.read( originalFile ).getHeight();
        int widthOriginalImage = ImageIO.read( originalFile ).getWidth();
        int heightNewImage = ImageIO.read( newFile ).getHeight();
        int widthNewImage = ImageIO.read( newFile ).getWidth();

        assertThat( heightNewImage, is( heightOriginalImage ) );
        assertThat( widthNewImage, is( widthOriginalImage ) );
    }

    @Ignore
    @Test
    public void testCalculateClippedImageInsideVisibleAreaAndOutsideVisibleArea()
                            throws Exception {
        File originalFile = createNewFile( "dem30_geotiff_tiled.tiff" );
        File newFile = createNewTempFile();

        geotiffClipper.calculateClippedImage( createInputStreamFrom( originalFile ),
                                              createGeometryWithImageInsideAndOutside(),
                                              createOutputStreamFrom( newFile ) );

        int heightOriginalImage = ImageIO.read( originalFile ).getHeight();
        int widthOriginalImage = ImageIO.read( originalFile ).getWidth();
        int heightNewImage = ImageIO.read( newFile ).getHeight();
        int widthNewImage = ImageIO.read( newFile ).getWidth();

        assertThat( heightNewImage, not( heightOriginalImage ) );
        assertThat( widthNewImage, not( widthOriginalImage ) );
    }

    private File createNewFile( String resourceName ) {
        return new File( GeotiffClipperTest.class.getResource( resourceName ).getPath() );
    }

    private File createNewTempFile()
                            throws IOException {
        return File.createTempFile( GeotiffClipperTest.class.getSimpleName(), "tif" );
    }

    private InputStream createInputStreamFrom( File file )
                            throws Exception {
        return new FileInputStream( file );
    }

    private OutputStream createOutputStreamFrom( File file )
                            throws Exception {
        return new FileOutputStream( file );
    }

    private InputStream mockInputStream() {
        return mock( InputStream.class );
    }

    private OutputStream mockOutputStream() {
        return mock( OutputStream.class );
    }

    private Geometry mockClippingGeometry() {
        return mock( Geometry.class );
    }

    private Geometry createWholeWorldVisibleGeometry() {
        Envelope wholeWorld = new Envelope( -180, 180, -90, 90 );
        return new GeometryFactory().toGeometry( wholeWorld );
    }

    private Geometry createGeometryWithImageInsideAndOutside() {
        Envelope smallEnvelope = new Envelope( 446592, 4427836, 457330, 4441814 );
        return new GeometryFactory().toGeometry( smallEnvelope );
    }
}
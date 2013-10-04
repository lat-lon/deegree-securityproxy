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

import static javax.imageio.ImageIO.read;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.FileImageInputStream;

import org.deegree.securityproxy.responsefilter.logging.ResponseClippingReport;
import org.geotools.data.DataSourceException;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.geometry.GeneralEnvelope;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.image.io.ImageIOExt;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Ignore;
import org.junit.Test;

import com.sun.media.imageio.plugins.tiff.BaselineTIFFTagSet;
import com.sun.media.imageio.plugins.tiff.TIFFDirectory;
import com.sun.media.imageio.plugins.tiff.TIFFField;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class GeotiffClipperTest {

    private GeotiffClipper geotiffClipper = new GeotiffClipper();

    /*
     * #calculateClippedImage() - Exceptions
     */

    @Test(expected = IllegalArgumentException.class)
    public void testCalculateClippedImageWithNullImageStreamShouldFail()
                            throws Exception {
        geotiffClipper.calculateClippedImage( null, mockClippingGeometry(), mockOutputStream() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCalculateClippedImageWithNullOutputStreamShouldFail()
                            throws Exception {
        geotiffClipper.calculateClippedImage( mockInputStream(), mockClippingGeometry(), null );
    }

    /*
     * #calculateClippedImage() - Dimension
     */

    @Test
    public void testCalculateClippedImageWithNullClippingGeometryShouldReturnWholeImage()
                            throws Exception {
        File sourceFile = createNewFile( "dem90_geotiff_tiled.tiff" );
        File destinationFile = createNewTempFile();

        InputStream inputStream = createInputStreamFrom( sourceFile );
        OutputStream outputStream = createOutputStreamFrom( destinationFile );

        geotiffClipper.calculateClippedImage( inputStream, null, outputStream );

        inputStream.close();
        outputStream.close();

        assertThat( destinationFile, hasSameDimension( sourceFile ) );
        assertThat( destinationFile, hasSamePixels( sourceFile ) );
    }

    @Test
    public void testCalculateClippedImageInsideVisibleArea()
                            throws Exception {
        File sourceFile = createNewFile( "dem90_geotiff_tiled.tiff" );
        File destinationFile = createNewTempFile();

        OutputStream outputStream = createOutputStreamFrom( destinationFile );
        InputStream inputStream = createInputStreamFrom( sourceFile );

        geotiffClipper.calculateClippedImage( inputStream, createEnvelopeWithImageInsideInWgs84(), outputStream );

        inputStream.close();
        outputStream.close();

        assertThat( destinationFile, hasSameDimension( sourceFile ) );
        assertThat( destinationFile, hasSamePixels( sourceFile ) );
    }

    @Test
    public void testCalculateClippedImageInsideAndOutsideVisibleArea()
                            throws Exception {
        File sourceFile = createNewFile( "dem90_geotiff_tiled.tiff" );
        File destinationFile = createNewTempFile();

        InputStream inputStream = createInputStreamFrom( sourceFile );
        OutputStream outputStream = createOutputStreamFrom( destinationFile );

        geotiffClipper.calculateClippedImage( inputStream, createGeometryWithImageInsideAndOutsideInWgs84(),
                                              outputStream );

        inputStream.close();
        outputStream.close();

        assertThat( destinationFile, hasSameDimension( sourceFile ) );
        assertThat( destinationFile, hasNotSamePixels( sourceFile ) );
    }

    @Test
    public void testCalculateClippedImageOutsideVisibleArea()
                            throws Exception {
        File sourceFile = createNewFile( "dem90_geotiff_tiled.tiff" );
        File destinationFile = createNewTempFile();

        InputStream inputStream = createInputStreamFrom( sourceFile );
        OutputStream outputStream = createOutputStreamFrom( destinationFile );

        geotiffClipper.calculateClippedImage( inputStream, createGeometryWithImageOutsideInWgs84(), outputStream );

        inputStream.close();
        outputStream.close();

        assertThat( destinationFile, hasSameDimension( sourceFile ) );
        assertThat( destinationFile, hasNotSamePixels( sourceFile ) );
    }

    @Test
    public void testCalculateClippedImageInsideAndOutsideVisiblePolygon()
                            throws Exception {
        File sourceFile = createNewFile( "dem90_geotiff_tiled.tiff" );
        File destinationFile = createNewTempFile();

        OutputStream outputStream = createOutputStreamFrom( destinationFile );
        InputStream inputStream = createInputStreamFrom( sourceFile );

        geotiffClipper.calculateClippedImage( inputStream, createPolygonGeometryWithImageInsideAndOutsideInWgs84(),
                                              outputStream );

        inputStream.close();
        outputStream.close();

        assertThat( destinationFile, hasSameDimension( sourceFile ) );
        assertThat( destinationFile, hasNotSamePixels( sourceFile ) );
    }

    @Test
    public void testCalculateClippedImageInsideAndOutsideVisiblePolygonWithHole()
                            throws Exception {
        File sourceFile = createNewFile( "dem90_geotiff_tiled.tiff" );
        File destinationFile = createNewTempFile();

        OutputStream outputStream = createOutputStreamFrom( destinationFile );
        InputStream inputStream = createInputStreamFrom( sourceFile );

        geotiffClipper.calculateClippedImage( inputStream,
                                              createPolygonWithHoleGeometryWithImageInsideAndOutsideInWgs84(),
                                              outputStream );

        inputStream.close();
        outputStream.close();

        assertThat( destinationFile, hasSameDimension( sourceFile ) );
        assertThat( destinationFile, hasNotSamePixels( sourceFile ) );
    }

    /*
     * #calculateClippedImage() - ReponseClippingReport
     */
    @Test
    public void testCalculateClippedImageInsideVisibleShouldReturnReport()
                            throws Exception {

        File sourceFile = createNewFile( "dem30_geotiff_tiled.tiff" );
        File destinationFile = createNewTempFile();

        OutputStream outputStream = createOutputStreamFrom( destinationFile );
        InputStream inputStream = createInputStreamFrom( sourceFile );

        ResponseClippingReport report = geotiffClipper.calculateClippedImage( inputStream,
                                                                              createEnvelopeWithImageInsideInWgs84(),
                                                                              outputStream );

        inputStream.close();
        outputStream.close();

        assertThat( report.getFailure(), is( nullValue() ) );
        assertThat( report.isFiltered(), is( false ) );
        assertThat( report.getReturnedVisibleArea(), is( notNullValue() ) );
    }

    @Test
    public void testCalculateClippedImageOutsideVisibleShouldReturnReport()
                            throws Exception {

        File sourceFile = createNewFile( "dem30_geotiff_tiled.tiff" );
        File destinationFile = createNewTempFile();

        OutputStream outputStream = createOutputStreamFrom( destinationFile );
        InputStream inputStream = createInputStreamFrom( sourceFile );

        ResponseClippingReport report = geotiffClipper.calculateClippedImage( inputStream,
                                                                              createGeometryWithImageOutsideInWgs84(),
                                                                              outputStream );

        inputStream.close();
        outputStream.close();

        assertThat( report.getFailure(), is( nullValue() ) );
        assertThat( report.isFiltered(), is( true ) );
        assertThat( report.getReturnedVisibleArea(), is( notNullValue() ) );
    }

    @Test
    public void testCalculateClippedImageIntersectingVisibleShouldReturnReport()
                            throws Exception {

        File sourceFile = createNewFile( "dem30_geotiff_tiled.tiff" );
        File destinationFile = createNewTempFile();

        OutputStream outputStream = createOutputStreamFrom( destinationFile );
        InputStream inputStream = createInputStreamFrom( sourceFile );

        ResponseClippingReport report = geotiffClipper.calculateClippedImage( inputStream,
                                                                              createGeometryWithImageInsideAndOutsideInWgs84(),
                                                                              outputStream );

        inputStream.close();
        outputStream.close();

        assertThat( report.getFailure(), is( nullValue() ) );
        assertThat( report.isFiltered(), is( true ) );
        assertThat( report.getReturnedVisibleArea(), is( notNullValue() ) );
    }

    /*
     * #calculateClippedImage() - ExceptionHandling
     */
    @Test(expected = ClippingException.class)
    public void testCalculateClippedImageThrowingExceptionShouldReturnExceptionReport()
                            throws Exception {

        File sourceFile = createExceptionFile();
        File destinationFile = createNewTempFile();

        OutputStream outputStream = createOutputStreamFrom( destinationFile );
        InputStream inputStream = createInputStreamFrom( sourceFile );

        geotiffClipper.calculateClippedImage( inputStream, createGeometryWithImageInsideAndOutsideInWgs84(),
                                              outputStream );

    }

    /*
     * #calculateClippedImage() - Compare metadata
     */
    @Test
    public void testCalculateClippedImageCompareMetadata()
                            throws Exception {
        File sourceFile = createNewFile( "dem30_geotiff_tiled.tiff" );
        File destinationFile = createNewTempFile();

        InputStream inputStream = createInputStreamFrom( sourceFile );
        OutputStream outputStream = createOutputStreamFrom( destinationFile );

        geotiffClipper.calculateClippedImage( inputStream, null, outputStream );

        inputStream.close();
        outputStream.close();

        TIFFDirectory tiffDirectorySource = retrieveTiffDirectory( sourceFile );
        TIFFDirectory tiffDirectoryDestination = retrieveTiffDirectory( destinationFile );

        String compressionSource = retrieveValueOfTiffField( BaselineTIFFTagSet.TAG_COMPRESSION, tiffDirectorySource );
        String compressionDestination = retrieveValueOfTiffField( BaselineTIFFTagSet.TAG_COMPRESSION,
                                                                  tiffDirectoryDestination );

        String resolutionUnitSource = retrieveValueOfTiffField( BaselineTIFFTagSet.TAG_RESOLUTION_UNIT,
                                                                tiffDirectorySource );
        String resolutionUnitDestination = retrieveValueOfTiffField( BaselineTIFFTagSet.TAG_RESOLUTION_UNIT,
                                                                     tiffDirectoryDestination );

        assertThat( compressionDestination, is( compressionSource ) );
        assertThat( resolutionUnitDestination, is( resolutionUnitSource ) );
    }

    /*
     * #isClippingRequired()
     */

    @Test
    public void testIsClippingRequiredWhenImageIsInsideVisibleArea()
                            throws Exception {
        File sourceFile = createNewFile( "dem30_geotiff_tiled.tiff" );
        GeoTiffReader geoTiffReader = createGeoTiffReader( sourceFile );
        boolean isClippingRequired = geotiffClipper.isClippingRequired( geoTiffReader,
                                                                        createWholeImageVisibleEnvelopeInImageCrs() );

        assertThat( isClippingRequired, is( false ) );
    }

    @Test
    public void testIsClippingRequiredWhenImageIsOutsideVisibleArea()
                            throws Exception {
        File sourceFile = createNewFile( "dem30_geotiff_tiled.tiff" );
        GeoTiffReader geoTiffReader = createGeoTiffReader( sourceFile );
        boolean isClippingRequired = geotiffClipper.isClippingRequired( geoTiffReader,
                                                                        createWholeImageInvisibleEnvelopeInImageCrs() );

        assertThat( isClippingRequired, is( true ) );
    }

    @Test
    public void testIsClippingRequiredWhenImageIntersectsVisibleArea()
                            throws Exception {
        File sourceFile = createNewFile( "dem30_geotiff_tiled.tiff" );
        GeoTiffReader geoTiffReader = createGeoTiffReader( sourceFile );
        boolean isClippingRequired = geotiffClipper.isClippingRequired( geoTiffReader,
                                                                        createImageInsersectsEnvelopeInImageCrs() );

        assertThat( isClippingRequired, is( true ) );
    }

    @Test
    public void testIsClippingRequiredWhenImageIntersectsVisibleAreaOfMultipolygon()
                            throws Exception {
        File sourceFile = createNewFile( "dem30_geotiff_tiled.tiff" );
        GeoTiffReader geoTiffReader = createGeoTiffReader( sourceFile );
        boolean isClippingRequired = geotiffClipper.isClippingRequired( geoTiffReader,
                                                                        createImageInHoleMultipolygonInImageCrs() );

        assertThat( isClippingRequired, is( true ) );
    }

    @Test
    public void testIsClippingRequiredWhenImageInVisibleAreaOfMultipolygon()
                            throws Exception {
        File sourceFile = createNewFile( "dem30_geotiff_tiled.tiff" );
        GeoTiffReader geoTiffReader = createGeoTiffReader( sourceFile );
        boolean isClippingRequired = geotiffClipper.isClippingRequired( geoTiffReader,
                                                                        createImageIntersectsMultipolygonInImageCrs() );

        assertThat( isClippingRequired, is( false ) );
    }

    /*
     * #calculateGeometryVisibleAfterClipping()
     */
    @Test
    public void testCalculateGeometryVisibleAfterClippingCompleteOutsideVisibleArea()
                            throws Exception {
        File sourceFile = createNewFile( "dem30_geotiff_tiled.tiff" );
        GeoTiffReader geoTiffReader = createGeoTiffReader( sourceFile );
        Geometry geometryVisibleAfterClipping = geotiffClipper.calculateAreaVisibleAfterClipping( geoTiffReader,
                                                                                                  createWholeImageInvisibleEnvelopeInImageCrs() );
        double area = geometryVisibleAfterClipping.getArea();
        assertThat( area, is( 0d ) );
    }

    @Test
    public void testCalculateGeometryVisibleAfterClippingCompleteInsideVisibleArea()
                            throws Exception {
        File sourceFile = createNewFile( "dem30_geotiff_tiled.tiff" );
        GeoTiffReader geoTiffReader = createGeoTiffReader( sourceFile );
        Geometry geometryVisibleAfterClipping = geotiffClipper.calculateAreaVisibleAfterClipping( geoTiffReader,
                                                                                                  createWholeImageVisibleEnvelopeInImageCrs() );
        double area = geometryVisibleAfterClipping.getArea();
        assertThat( area, is( 1.501452E8 ) );
    }

    @Test
    public void testCalculateGeometryVisibleAfterClippingIntersectingVisbleArea()
                            throws Exception {
        File sourceFile = createNewFile( "dem30_geotiff_tiled.tiff" );
        GeoTiffReader geoTiffReader = createGeoTiffReader( sourceFile );
        Geometry geometryVisibleAfterClipping = geotiffClipper.calculateAreaVisibleAfterClipping( geoTiffReader,
                                                                                                  createImageInsersectsEnvelopeInImageCrs() );
        double area = geometryVisibleAfterClipping.getArea();

        // Expected 4.30674E7
        double expectedArea = new Envelope( 446591.945, 457331.945, 4437805.000, 4441815.000 ).getArea();
        assertThat( area, is( expectedArea ) );
    }

    /*
     * #transformVisibleAreaToImageCrs()
     */

    @Ignore("why does this not work???")
    @Test
    public void testTransformVisibleAreaToImageCrs()
                            throws Exception {
        File sourceFile = createNewFile( "dem30_geotiff_tiled.tiff" );
        GeoTiffReader geoTiffReader = createGeoTiffReader( sourceFile );

        Geometry geometryToTransform = createEnvelopeWithImageInsideInWgs84();
        Geometry transformedGeometry = geotiffClipper.transformVisibleAreaToImageCrs( geometryToTransform,
                                                                                      geoTiffReader );
        Geometry expectedGeometry = new GeometryFactory().toGeometry( new Envelope( 214532.475581639, 3470063.34743009,
                                                                                    534994.655061707, 9329005.18235732 ) );

        assertThat( transformedGeometry, is( expectedGeometry ) );
    }

    /*
     * #transformToVisibleAreaCrs()
     */
    @Test
    public void testTransformToVisibleAreaCrs()
                            throws Exception {
        File sourceFile = createNewFile( "dem30_geotiff_tiled.tiff" );
        GeoTiffReader geoTiffReader = createGeoTiffReader( sourceFile );

        Geometry geoTiffEnvelopeAsGeometry = convertImageEnvelopeToGeometry( geoTiffReader );
        Geometry transformedGeometry = geotiffClipper.transformToVisibleAreaCrs( geoTiffEnvelopeAsGeometry,
                                                                                 geoTiffReader );
        Geometry expectedGeometry = new GeometryFactory().toGeometry( new Envelope( -111.625671116814,
                                                                                    -111.500779260647,
                                                                                    39.9990119740282, 40.1255737525128 ) );
        Geometry expectedGeometryBuffered = expectedGeometry.buffer( 0.01 );

        assertTrue( expectedGeometryBuffered.contains( transformedGeometry ) );
    }

    private GeoTiffReader createGeoTiffReader( File tiff )
                            throws DataSourceException {
        return new GeoTiffReader( tiff );
    }

    private File createNewFile( String resourceName ) {
        return new File( GeotiffClipperTest.class.getResource( resourceName ).getPath() );
    }

    private File createNewTempFile()
                            throws IOException {
        return File.createTempFile( GeotiffClipperTest.class.getSimpleName(), ".tif" );
    }

    private File createExceptionFile() {
        return new File( GeotiffClipperTest.class.getResource( "service_exception.xml" ).getPath() );
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

    private Geometry createWholeImageVisibleEnvelopeInImageCrs() {
        Envelope wholeWorld = new Envelope( 446580.945, 457531.945, 4427805.000, 4441915.000 );
        return new GeometryFactory().toGeometry( wholeWorld );
    }

    private Geometry createWholeImageInvisibleEnvelopeInImageCrs() {
        Envelope wholeWorld = new Envelope( 446580.945, 446581.945, 4427805.000, 4427806.000 );
        return new GeometryFactory().toGeometry( wholeWorld );
    }

    private Geometry createImageInsersectsEnvelopeInImageCrs() {
        Envelope wholeWorld = new Envelope( 446580.945, 457351.945, 4437805.000, 4441915.000 );
        return new GeometryFactory().toGeometry( wholeWorld );
    }

    private Geometry createImageInHoleMultipolygonInImageCrs()
                            throws ParseException {
        String wktPolygon = "MULTIPOLYGON (((446580.945 4427805, 446580.945 4427806, 446581.945 4427806, 446581.945 4427805, 446580.945 4427805)),"
                            + "((436491.94453 4327825, 436491.94453 4541825, 467431.94453 4541825, 467431.94453 4327825, 436491.94453 4327825),"
                            + "(446491.94453 4427825, 446491.94453 4441825, 457431.94453 4441825, 457431.94453 4427825, 446491.94453 4427825)))";
        WKTReader reader = new WKTReader();
        return reader.read( wktPolygon );
    }

    private Geometry createImageIntersectsMultipolygonInImageCrs()
                            throws ParseException {
        String wktPolygon = "MULTIPOLYGON (((446580.945 4427805, 446580.945 4427806, 446581.945 4427806, 446581.945 4427805, 446580.945 4427805)),"
                            + "((446491.94453 4427825, 446491.94453 4441825, 457431.94453 4441825, 457431.94453 4427825, 446491.94453 4427825)))";
        WKTReader reader = new WKTReader();
        return reader.read( wktPolygon );
    }

    private Geometry createEnvelopeWithImageInsideInWgs84() {
        Envelope envelope = new Envelope( -114, -108, 31.33, 84 );
        return new GeometryFactory().toGeometry( envelope );
    }

    private Geometry createGeometryWithImageInsideAndOutsideInWgs84() {
        Envelope smallEnvelope = new Envelope( -111.57, -111.53, 40, 40.1 );
        return new GeometryFactory().toGeometry( smallEnvelope );
    }

    private Geometry createGeometryWithImageOutsideInWgs84() {
        Envelope envelope = new Envelope( -111.57, -111.53, 43.57, 43.93 );
        return new GeometryFactory().toGeometry( envelope );
    }

    private Geometry createPolygonGeometryWithImageInsideAndOutsideInWgs84() {
        Coordinate coord1 = new Coordinate( -111.57, 40 );
        Coordinate coord2 = new Coordinate( -111.53, 40 );
        Coordinate coord3 = new Coordinate( -111.53, 40.1 );
        Coordinate[] coordArray = { coord1, coord2, coord3, coord1 };
        return new GeometryFactory().createPolygon( coordArray );
    }

    private Geometry createPolygonWithHoleGeometryWithImageInsideAndOutsideInWgs84() {
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate coordShell1 = new Coordinate( -111.57, 40.0 );
        Coordinate coordShell2 = new Coordinate( -111.53, 40.0 );
        Coordinate coordShell3 = new Coordinate( -111.53, 40.3 );
        Coordinate[] coordShellArray = { coordShell1, coordShell2, coordShell3, coordShell1 };
        LinearRing shell = geometryFactory.createLinearRing( coordShellArray );

        Coordinate coordHole1 = new Coordinate( -111.55, 40.04 );
        Coordinate coordHole2 = new Coordinate( -111.54, 40.04 );
        Coordinate coordHole3 = new Coordinate( -111.54, 40.06 );
        Coordinate[] coordHoleArray = { coordHole1, coordHole2, coordHole3, coordHole1 };
        LinearRing hole = geometryFactory.createLinearRing( coordHoleArray );
        LinearRing holes[] = { hole };

        return geometryFactory.createPolygon( shell, holes );
    }

    private Matcher<File> hasSameDimension( File sourceFile )
                            throws IOException {

        BufferedImage sourceImage = read( sourceFile );
        final int heightSource = sourceImage.getHeight();
        final int widthSource = sourceImage.getWidth();

        return new BaseMatcher<File>() {

            @Override
            public boolean matches( Object item ) {
                BufferedImage destinationImage;
                try {
                    destinationImage = read( (File) item );
                    int heightDestination = destinationImage.getHeight();
                    int widthDestination = destinationImage.getWidth();

                    return heightDestination == heightSource && widthDestination == widthSource;
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public void describeTo( Description description ) {
                description.appendText( "Should have the same width and heigth as the source ( " + widthSource + " * "
                                        + heightSource + ")!" );
            }
        };
    }

    private Matcher<File> hasSamePixels( File sourceFile )
                            throws Exception {

        BufferedImage sourceImage = ImageIO.read( sourceFile );
        PixelGrabber sourceGrabber = new PixelGrabber( sourceImage, 0, 0, -1, -1, false );

        int[] sourcePixels = null;
        if ( sourceGrabber.grabPixels() ) {
            int width = sourceGrabber.getWidth();
            int height = sourceGrabber.getHeight();
            sourcePixels = new int[width * height];
            sourcePixels = (int[]) sourceGrabber.getPixels();
        }
        final int[] sourcePixelsToCompare = sourcePixels;

        return new BaseMatcher<File>() {

            @Override
            public boolean matches( Object item ) {
                BufferedImage destinationImage;
                try {
                    destinationImage = read( (File) item );
                    PixelGrabber destinationGrabber = new PixelGrabber( destinationImage, 0, 0, -1, -1, false );

                    int[] destinationPixels = null;
                    if ( destinationGrabber.grabPixels() ) {
                        int width = destinationGrabber.getWidth();
                        int height = destinationGrabber.getHeight();
                        destinationPixels = new int[width * height];
                        destinationPixels = (int[]) destinationGrabber.getPixels();
                    }

                    return Arrays.equals( destinationPixels, sourcePixelsToCompare );
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public void describeTo( Description description ) {
                description.appendText( "Should contain the same pixels as the source!" );
            }
        };
    }

    private Matcher<File> hasNotSamePixels( File sourceFile )
                            throws Exception {

        BufferedImage sourceImage = ImageIO.read( sourceFile );
        PixelGrabber sourceGrabber = new PixelGrabber( sourceImage, 0, 0, -1, -1, false );

        int[] sourcePixels = null;
        if ( sourceGrabber.grabPixels() ) {
            int width = sourceGrabber.getWidth();
            int height = sourceGrabber.getHeight();
            sourcePixels = new int[width * height];
            sourcePixels = (int[]) sourceGrabber.getPixels();
        }
        final int[] sourcePixelsToCompare = sourcePixels;

        return new BaseMatcher<File>() {

            @Override
            public boolean matches( Object item ) {
                BufferedImage destinationImage;
                try {
                    destinationImage = read( (File) item );
                    PixelGrabber destinationGrabber = new PixelGrabber( destinationImage, 0, 0, -1, -1, false );

                    int[] destinationPixels = null;
                    if ( destinationGrabber.grabPixels() ) {
                        int width = destinationGrabber.getWidth();
                        int height = destinationGrabber.getHeight();
                        destinationPixels = new int[width * height];
                        destinationPixels = (int[]) destinationGrabber.getPixels();
                    }

                    return !Arrays.equals( destinationPixels, sourcePixelsToCompare );
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public void describeTo( Description description ) {
                description.appendText( "Should not contain the same pixels as the source!" );
            }
        };
    }

    private Geometry convertImageEnvelopeToGeometry( GeoTiffReader reader ) {
        GeometryFactory geometryFactory = new GeometryFactory();
        GeneralEnvelope imageEnvelope = reader.getOriginalEnvelope();
        ReferencedEnvelope envelope = new ReferencedEnvelope( imageEnvelope );
        return geometryFactory.toGeometry( envelope );
    }

    private TIFFDirectory retrieveTiffDirectory( File file )
                            throws FileNotFoundException, IOException, IIOInvalidTreeException {
        ImageReader reader = ImageIOExt.getImageioReader( new FileImageInputStream( file ) );
        reader.setInput( new FileImageInputStream( file ) );
        IIOMetadata metadata = reader.getImageMetadata( 0 );
        return TIFFDirectory.createFromMetadata( metadata );
    }

    private String retrieveValueOfTiffField( int tiffId, TIFFDirectory tiffDirectorySource ) {
        TIFFField tiffField = tiffDirectorySource.getTIFFField( tiffId );
        return tiffField.getValueAsString( 0 );
    }

}
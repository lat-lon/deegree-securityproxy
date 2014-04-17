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
package org.deegree.securityproxy.wcs.responsefilter.clipping;

import static com.sun.media.imageio.plugins.tiff.BaselineTIFFTagSet.TAG_COMPRESSION;
import static com.sun.media.imageio.plugins.tiff.BaselineTIFFTagSet.TAG_RESOLUTION_UNIT;
import static com.sun.media.imageio.plugins.tiff.BaselineTIFFTagSet.TAG_X_RESOLUTION;
import static com.sun.media.imageio.plugins.tiff.BaselineTIFFTagSet.TAG_Y_RESOLUTION;
import static org.deegree.matcher.image.ImageMatcher.hasNotSamePixels;
import static org.deegree.matcher.image.ImageMatcher.hasSameDimension;
import static org.deegree.matcher.image.ImageMatcher.hasSamePixels;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.metadata.IIOMetadataNode;

import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.responsefilter.logging.ResponseClippingReport;
import org.deegree.securityproxy.service.commons.responsefilter.clipping.exception.ClippingException;
import org.geotools.coverage.grid.io.imageio.geotiff.GeoTiffIIOMetadataDecoder;
import org.geotools.data.DataSourceException;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class GeotiffClipperTest {

    /*
     * #calculateClippedImage() - Exceptions
     */

    @Test(expected = IllegalArgumentException.class)
    public void testCalculateClippedImageWithNullImageStreamShouldFail()
                            throws Exception {
        GeotiffClipper geotiffClipper = new GeotiffClipper();
        geotiffClipper.calculateClippedImage( null, mockClippingGeometry(), mockOutputStream(), mockOwsRequest() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCalculateClippedImageWithNullOutputStreamShouldFail()
                            throws Exception {
        GeotiffClipper geotiffClipper = new GeotiffClipper();
        geotiffClipper.calculateClippedImage( mockInputStream(), mockClippingGeometry(), null, mockOwsRequest() );
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
        GeotiffClipper geotiffClipper = new GeotiffClipper();

        geotiffClipper.calculateClippedImage( inputStream, null, outputStream, mockOwsRequest() );

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
        GeotiffClipper geotiffClipper = new GeotiffClipper();
        geotiffClipper.calculateClippedImage( inputStream, createEnvelopeWithImageInsideInWgs84(), outputStream,
                                              mockOwsRequest() );

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
        GeotiffClipper geotiffClipper = new GeotiffClipper();
        geotiffClipper.calculateClippedImage( inputStream, createGeometryWithImageInsideAndOutsideInWgs84(),
                                              outputStream, mockOwsRequest() );

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
        GeotiffClipper geotiffClipper = new GeotiffClipper();
        geotiffClipper.calculateClippedImage( inputStream, createGeometryWithImageOutsideInWgs84(), outputStream,
                                              mockOwsRequest() );

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
        GeotiffClipper geotiffClipper = new GeotiffClipper();
        geotiffClipper.calculateClippedImage( inputStream, createPolygonGeometryWithImageInsideAndOutsideInWgs84(),
                                              outputStream, mockOwsRequest() );

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
        GeotiffClipper geotiffClipper = new GeotiffClipper();
        geotiffClipper.calculateClippedImage( inputStream,
                                              createPolygonWithHoleGeometryWithImageInsideAndOutsideInWgs84(),
                                              outputStream, mockOwsRequest() );

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
        GeotiffClipper geotiffClipper = new GeotiffClipper();
        ResponseClippingReport report = geotiffClipper.calculateClippedImage( inputStream,
                                                                              createEnvelopeWithImageInsideInWgs84(),
                                                                              outputStream, mockOwsRequest() );

        inputStream.close();
        outputStream.close();

        assertThat( report.isFailed(), is( false ) );
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
        GeotiffClipper geotiffClipper = new GeotiffClipper();
        ResponseClippingReport report = geotiffClipper.calculateClippedImage( inputStream,
                                                                              createGeometryWithImageOutsideInWgs84(),
                                                                              outputStream, mockOwsRequest() );

        inputStream.close();
        outputStream.close();

        assertThat( report.isFailed(), is( false ) );
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
        GeotiffClipper geotiffClipper = new GeotiffClipper();
        ResponseClippingReport report = geotiffClipper.calculateClippedImage( inputStream,
                                                                              createGeometryWithImageInsideAndOutsideInWgs84(),
                                                                              outputStream, mockOwsRequest() );

        inputStream.close();
        outputStream.close();

        assertThat( report.isFailed(), is( false ) );
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
        GeotiffClipper geotiffClipper = new GeotiffClipper();
        geotiffClipper.calculateClippedImage( inputStream, createGeometryWithImageInsideAndOutsideInWgs84(),
                                              outputStream, mockOwsRequest() );

    }

    /*
     * #calculateClippedImage() - Compare metadata
     */
    @Test
    public void testCalculateClippedImageShouldHaveSameCompression()
                            throws Exception {
        File sourceFile = createNewFile( "dem30_geotiff_tiled.tiff" );
        File destinationFile = createNewTempFile();

        InputStream inputStream = createInputStreamFrom( sourceFile );
        OutputStream outputStream = createOutputStreamFrom( destinationFile );
        GeotiffClipper geotiffClipper = new GeotiffClipper();
        geotiffClipper.calculateClippedImage( inputStream, null, outputStream, mockOwsRequest() );

        inputStream.close();
        outputStream.close();

        NodeList nodesSource = retrieveNodeWithTags( sourceFile );
        NodeList nodesDestination = retrieveNodeWithTags( destinationFile );

        String compressionSource = retrieveTagValue( TAG_COMPRESSION, nodesSource );
        String compressionDestination = retrieveTagValue( TAG_COMPRESSION, nodesDestination );

        assertThat( compressionDestination, is( compressionSource ) );
    }

    @Ignore("A test tiff with resulution tags has to be added!")
    @Test
    public void testCalculateClippedImageShouldHaveCorrectResolutionMetadata()
                            throws Exception {
        File sourceFile = createNewFile( "TODO.tif" );
        File destinationFile = createNewTempFile();

        InputStream inputStream = createInputStreamFrom( sourceFile );
        OutputStream outputStream = createOutputStreamFrom( destinationFile );
        GeotiffClipper geotiffClipper = new GeotiffClipper();
        geotiffClipper.calculateClippedImage( inputStream, null, outputStream, mockOwsRequest() );

        inputStream.close();
        outputStream.close();

        NodeList nodesSource = retrieveNodeWithTags( sourceFile );
        NodeList nodesDestination = retrieveNodeWithTags( destinationFile );

        String yResolutionSource = retrieveTagValue( TAG_Y_RESOLUTION, nodesSource );
        String xResolutionSource = retrieveTagValue( TAG_X_RESOLUTION, nodesSource );
        String resolutionUnitSource = retrieveTagValue( TAG_RESOLUTION_UNIT, nodesSource );

        String yResolutionDestination = retrieveTagValue( TAG_Y_RESOLUTION, nodesDestination );
        String xResolutionDestination = retrieveTagValue( TAG_X_RESOLUTION, nodesDestination );
        String resolutionUnitDestination = retrieveTagValue( TAG_RESOLUTION_UNIT, nodesDestination );

        assertThat( xResolutionDestination, is( xResolutionSource ) );
        assertThat( yResolutionDestination, is( yResolutionSource ) );
        assertThat( resolutionUnitDestination, is( resolutionUnitSource ) );
    }

    private File createNewFile( String resourceName ) {
        return new File( GeotiffClipperTest.class.getResource( resourceName ).getPath() );
    }

    private File createNewTempFile()
                            throws IOException {
        return File.createTempFile( GeotiffClipperTest.class.getSimpleName(), ".tif" );
    }

    private File createExceptionFile() {
        return new File( GeotiffClipperTest.class.getResource( "../service_exception.xml" ).getPath() );
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

    private OwsRequest mockOwsRequest() {
        return mock( OwsRequest.class );
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

    private NodeList retrieveNodeWithTags( File file )
                            throws DataSourceException {
        GeoTiffReader reader = new GeoTiffReader( file );
        GeoTiffIIOMetadataDecoder metadata = reader.getMetadata();
        IIOMetadataNode rootNode = metadata.getRootNode();
        Node firstChildNode = rootNode.getChildNodes().item( 0 );
        return firstChildNode.getChildNodes();
    }

    private String retrieveTagValue( int tiffId, NodeList nodeWithTags ) {
        for ( int indexTagNumber = 0; indexTagNumber < nodeWithTags.getLength(); indexTagNumber++ ) {
            Node nodeWithTag = nodeWithTags.item( indexTagNumber );
            String tagNumber = retrieveTagNumber( nodeWithTag );
            if ( Integer.toString( tiffId ).equals( tagNumber ) ) {
                return retrieveFirstValue( nodeWithTag );
            }
        }
        return null;
    }

    private String retrieveTagNumber( Node nodeWithTag ) {
        Node firstAttribute = nodeWithTag.getAttributes().item( 0 );
        return firstAttribute.getNodeValue();
    }

    private String retrieveFirstValue( Node nodeWithTag ) {
        Node nodeWithValues = nodeWithTag.getChildNodes().item( 0 );
        Node firstChildNode = nodeWithValues.getChildNodes().item( 0 );
        Node firstAttribute = firstChildNode.getAttributes().item( 0 );
        return firstAttribute.getNodeValue();
    }

}

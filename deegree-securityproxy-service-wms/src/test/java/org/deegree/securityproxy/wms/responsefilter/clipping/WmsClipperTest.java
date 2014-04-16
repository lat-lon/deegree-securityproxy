package org.deegree.securityproxy.wms.responsefilter.clipping;

import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.deegree.matcher.image.ImageMatcher.hasNotSamePixels;
import static org.deegree.matcher.image.ImageMatcher.hasSameDimension;
import static org.deegree.matcher.image.ImageMatcher.hasSamePixels;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.service.commons.responsefilter.clipping.ImageClipper;
import org.deegree.securityproxy.wms.request.WmsRequest;
import org.junit.Test;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * Tests for {@link WmsClipper}.
 * 
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * 
 * @version $Revision: $, $Date: $
 */
public class WmsClipperTest {

    private final ImageClipper wmsClipper = new WmsClipper();

    @Test
    public void testCalculateClippedImageWithPngImage()
                            throws Exception {
        File sourceFile = createNewFile( "overview.png" );
        File destinationFile = createNewTempFileInPngFormat();

        InputStream inputStream = createInputStreamFrom( sourceFile );
        OutputStream outputStream = createOutputStreamFrom( destinationFile );

        wmsClipper.calculateClippedImage( inputStream, createVisibleArea(), outputStream,
                                          createWmsRequestWithPngFormat() );

        inputStream.close();
        outputStream.close();

        assertThat( destinationFile, hasNotSamePixels( sourceFile ) );
        assertThat( destinationFile, hasSameDimension( sourceFile ) );
        assertThat( retrieveFileExtension( destinationFile ), is( retrieveFileExtension( sourceFile ) ) );
    }

    @Test
    public void testCalculateClippedImageWithJpgImage()
                            throws Exception {
        File sourceFile = createNewFile( "saltlakecity_0_0.jpg" );
        File destinationFile = createNewTempFileInJpgFormat();

        InputStream inputStream = createInputStreamFrom( sourceFile );
        OutputStream outputStream = createOutputStreamFrom( destinationFile );

        wmsClipper.calculateClippedImage( inputStream, createVisibleArea(), outputStream,
                                          createWmsRequestWithJpgFormat() );

        inputStream.close();
        outputStream.close();

        assertThat( destinationFile, hasNotSamePixels( sourceFile ) );
        assertThat( destinationFile, hasSameDimension( sourceFile ) );
        assertThat( retrieveFileExtension( destinationFile ), is( retrieveFileExtension( sourceFile ) ) );
    }

    @Test
    public void testCalculateClippedImageWithPngImageInsideClippingArea()
                            throws Exception {
        File sourceFile = createNewFile( "overview.png" );
        File destinationFile = createNewTempFileInPngFormat();

        InputStream inputStream = createInputStreamFrom( sourceFile );
        OutputStream outputStream = createOutputStreamFrom( destinationFile );

        wmsClipper.calculateClippedImage( inputStream, createVisibleArea(), outputStream,
                                          createWmsRequestWithPngFormatAndLargeBbox() );

        inputStream.close();
        outputStream.close();

        assertThat( destinationFile, hasSamePixels( sourceFile ) );
        assertThat( destinationFile, hasSameDimension( sourceFile ) );
        assertThat( retrieveFileExtension( destinationFile ), is( retrieveFileExtension( sourceFile ) ) );
    }

    @Test
    public void testCalculateClippedImageWithJpgImageInsideClippingArea()
                            throws Exception {
        File sourceFile = createNewFile( "saltlakecity_0_0.jpg" );
        File destinationFile = createNewTempFileInPngFormat();

        InputStream inputStream = createInputStreamFrom( sourceFile );
        OutputStream outputStream = createOutputStreamFrom( destinationFile );

        wmsClipper.calculateClippedImage( inputStream, createVisibleArea(), outputStream,
                                          createWmsRequestWithJpgFormatAndLargeBbox() );

        inputStream.close();
        outputStream.close();

        assertThat( destinationFile, hasSamePixels( sourceFile ) );
        assertThat( destinationFile, hasSameDimension( sourceFile ) );
        assertThat( retrieveFileExtension( destinationFile ), is( retrieveFileExtension( sourceFile ) ) );
    }

    private File createNewFile( String resourceName ) {
        return new File( WmsClipperTest.class.getResource( resourceName ).getPath() );
    }

    private File createNewTempFileInPngFormat()
                            throws IOException {
        return File.createTempFile( WmsClipperTest.class.getSimpleName(), ".png" );
    }

    private File createNewTempFileInJpgFormat()
                            throws IOException {
        return File.createTempFile( WmsClipperTest.class.getSimpleName(), ".jpg" );
    }

    private InputStream createInputStreamFrom( File file )
                            throws Exception {
        return new FileInputStream( file );
    }

    private OutputStream createOutputStreamFrom( File file )
                            throws Exception {
        return new FileOutputStream( file );
    }

    private Geometry createVisibleArea() {
        Envelope envelope = new Envelope( 8, 10, 51, 53 );
        return new GeometryFactory().toGeometry( envelope );
    }

    private OwsRequest createWmsRequestWithPngFormat() {
        String format = "image/png";
        return createWmsRequest( format );
    }

    private OwsRequest createWmsRequestWithJpgFormat() {
        String format = "image/jpg";
        return createWmsRequest( format );
    }

    private OwsRequest createWmsRequestWithPngFormatAndLargeBbox() {
        String format = "image/png";
        Envelope bbox = new Envelope( 8, 10, 51, 53 );
        return createWmsRequest( format, bbox );
    }

    private OwsRequest createWmsRequestWithJpgFormatAndLargeBbox() {
        String format = "image/jpg";
        Envelope bbox = new Envelope( 8, 10, 51, 53 );
        return createWmsRequest( format, bbox );
    }

    private OwsRequest createWmsRequest( String format ) {
        Envelope bbox = new Envelope( 7.3345265546875, 11.454399601562, 50.526648257812, 54.646521304687 );
        String crs = "EPSG:4326";
        return new WmsRequest( null, null, null, null, bbox, crs, format );
    }

    private OwsRequest createWmsRequest( String format, Envelope bbox ) {
        String crs = "EPSG:4326";
        return new WmsRequest( null, null, null, null, bbox, crs, format );
    }

    private String retrieveFileExtension( File file ) {
        return getExtension( file.getPath() );
    }

}

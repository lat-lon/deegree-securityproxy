package org.deegree.securityproxy.wms.responsefilter.clipping;

import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.deegree.matcher.image.ImageMatcher.hasNotSamePixels;
import static org.deegree.matcher.image.ImageMatcher.hasSameDimension;
import static org.deegree.matcher.image.ImageMatcher.hasSamePixels;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.deegree.securityproxy.responsefilter.logging.ResponseClippingReport;
import org.deegree.securityproxy.service.commons.responsefilter.clipping.ImageClipper;
import org.deegree.securityproxy.service.commons.responsefilter.clipping.exception.ClippingException;
import org.deegree.securityproxy.wms.request.WmsRequest;
import org.junit.Test;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * Tests for {@link SimpleRasterClipper}.
 * 
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * 
 * @version $Revision: $, $Date: $
 */
public class SimpleRasterClipperTest {

    @Test
    public void testCalculateClippedImageWithPngImage()
                            throws Exception {
        File sourceFile = createNewFile( "overview.png" );
        File destinationFile = createNewTempFileInPngFormat();

        InputStream inputStream = createInputStreamFrom( sourceFile );
        OutputStream outputStream = createOutputStreamFrom( destinationFile );

        ImageClipper wmsClipper = new SimpleRasterClipper();
        wmsClipper.calculateClippedImage( inputStream, createVisibleArea(), outputStream,
                                          createWmsRequestWithPngFormat() );

        inputStream.close();
        outputStream.close();

        assertThat( destinationFile, hasSamePixels( createNewFile( "overview_clipped.png" ) ) );
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

        ImageClipper wmsClipper = new SimpleRasterClipper();
        wmsClipper.calculateClippedImage( inputStream, createVisibleArea(), outputStream,
                                          createWmsRequestWithJpgFormat() );

        inputStream.close();
        outputStream.close();

        assertThat( destinationFile, hasNotSamePixels( sourceFile ) );
        assertThat( destinationFile, hasSameDimension( sourceFile ) );
        assertThat( retrieveFileExtension( destinationFile ), is( retrieveFileExtension( sourceFile ) ) );
    }

    @Test
    public void testCalculateClippedImageWithJpgImageShouldReturnCorrectReport()
                            throws Exception {
        File sourceFile = createNewFile( "saltlakecity_0_0.jpg" );
        File destinationFile = createNewTempFileInJpgFormat();

        InputStream inputStream = createInputStreamFrom( sourceFile );
        OutputStream outputStream = createOutputStreamFrom( destinationFile );

        ImageClipper wmsClipper = new SimpleRasterClipper();
        ResponseClippingReport report = wmsClipper.calculateClippedImage( inputStream, createVisibleArea(),
                                                                          outputStream, createWmsRequestWithJpgFormat() );

        inputStream.close();
        outputStream.close();

        assertThat( report.isFailed(), is( false ) );
        assertThat( report.isFiltered(), is( true ) );
        // TODO
        // assertThat( report.getReturnedVisibleArea(), is( ) );
    }

    @Test
    public void testCalculateClippedImageWithPngImageInsideClippingArea()
                            throws Exception {
        File sourceFile = createNewFile( "overview.png" );
        File destinationFile = createNewTempFileInPngFormat();

        InputStream inputStream = createInputStreamFrom( sourceFile );
        OutputStream outputStream = createOutputStreamFrom( destinationFile );

        ImageClipper wmsClipper = new SimpleRasterClipper();
        wmsClipper.calculateClippedImage( inputStream, createVisibleArea(), outputStream,
                                          createWmsRequestWithPngFormatAndSmallBbox() );

        inputStream.close();
        outputStream.close();

        assertThat( destinationFile, hasSamePixels( sourceFile ) );
        assertThat( destinationFile, hasSameDimension( sourceFile ) );
        assertThat( retrieveFileExtension( destinationFile ), is( retrieveFileExtension( sourceFile ) ) );
    }

    @Test
    public void testCalculateClippedImageWithPngImageInsideClippingAreaShouldReturnReportWithoutFiltering()
                            throws Exception {
        File sourceFile = createNewFile( "overview.png" );
        File destinationFile = createNewTempFileInPngFormat();

        InputStream inputStream = createInputStreamFrom( sourceFile );
        OutputStream outputStream = createOutputStreamFrom( destinationFile );

        ImageClipper wmsClipper = new SimpleRasterClipper();
        WmsRequest wmsRequest = createWmsRequestWithPngFormatAndSmallBbox();
        ResponseClippingReport report = wmsClipper.calculateClippedImage( inputStream, createVisibleArea(),
                                                                          outputStream, wmsRequest );

        inputStream.close();
        outputStream.close();

        assertThat( report.isFiltered(), is( false ) );
        assertThat( report.isFailed(), is( false ) );
        assertThat( report.getReturnedVisibleArea(), is( asGeometry( wmsRequest ) ) );
    }

    @Test
    public void testCalculateClippedImageWithJpgImageInsideClippingArea()
                            throws Exception {
        File sourceFile = createNewFile( "saltlakecity_0_0.jpg" );
        File destinationFile = createNewTempFileInJpgFormat();

        InputStream inputStream = createInputStreamFrom( sourceFile );
        OutputStream outputStream = createOutputStreamFrom( destinationFile );

        ImageClipper wmsClipper = new SimpleRasterClipper();
        wmsClipper.calculateClippedImage( inputStream, createVisibleArea(), outputStream,
                                          createWmsRequestWithJpgFormatAndSmallBbox() );

        inputStream.close();
        outputStream.close();

        assertThat( destinationFile, hasSamePixels( sourceFile ) );
        assertThat( destinationFile, hasSameDimension( sourceFile ) );
        assertThat( retrieveFileExtension( destinationFile ), is( retrieveFileExtension( sourceFile ) ) );
    }

    @Test
    public void testCalculateClippedImageWithPngImageRequestInOtherCrsThanVisibleArea()
                            throws Exception {
        File sourceFile = createNewFile( "overview.png" );
        File destinationFile = createNewTempFileInPngFormat();

        InputStream inputStream = createInputStreamFrom( sourceFile );
        OutputStream outputStream = createOutputStreamFrom( destinationFile );

        ImageClipper wmsClipper = new SimpleRasterClipper();
        wmsClipper.calculateClippedImage( inputStream, createVisibleArea(), outputStream,
                                          createWmsRequestWithPngFormatAndEpsg31467() );

        inputStream.close();
        outputStream.close();

        assertThat( destinationFile, hasNotSamePixels( sourceFile ) );
        assertThat( destinationFile, hasSamePixels( createNewFile( "overview_clipped_epsg31467.png" ) ) );
        assertThat( destinationFile, hasSameDimension( sourceFile ) );
        assertThat( retrieveFileExtension( destinationFile ), is( retrieveFileExtension( sourceFile ) ) );
    }

    @Test
    public void testCalculateClippedImageWithPngImageNullClippingArea()
                            throws Exception {
        File sourceFile = createNewFile( "overview.png" );
        File destinationFile = createNewTempFileInPngFormat();

        InputStream inputStream = createInputStreamFrom( sourceFile );
        OutputStream outputStream = createOutputStreamFrom( destinationFile );

        ImageClipper wmsClipper = new SimpleRasterClipper();
        wmsClipper.calculateClippedImage( inputStream, null, outputStream, createWmsRequestWithPngFormatAndEpsg31467() );

        inputStream.close();
        outputStream.close();

        assertThat( destinationFile, hasSamePixels( sourceFile ) );
        assertThat( destinationFile, hasSameDimension( sourceFile ) );
        assertThat( retrieveFileExtension( destinationFile ), is( retrieveFileExtension( sourceFile ) ) );
    }

    @Test
    public void testCalculateClippedImageWithPngImageNullClippingAreaShouldReturnReportWithoutFiltering()
                            throws Exception {
        File sourceFile = createNewFile( "overview.png" );
        File destinationFile = createNewTempFileInPngFormat();

        InputStream inputStream = createInputStreamFrom( sourceFile );
        OutputStream outputStream = createOutputStreamFrom( destinationFile );

        ImageClipper wmsClipper = new SimpleRasterClipper();
        WmsRequest wmsRequest = createWmsRequestWithPngFormatAndEpsg31467();
        ResponseClippingReport report = wmsClipper.calculateClippedImage( inputStream, null, outputStream, wmsRequest );

        inputStream.close();
        outputStream.close();

        assertThat( report.isFiltered(), is( false ) );
        assertThat( report.isFailed(), is( false ) );
        // TODO: same in other CRS!
        assertThat( report.getReturnedVisibleArea(), is( not( asGeometry( wmsRequest ) ) ) );
    }

    @Test(expected = ClippingException.class)
    public void testCalculateClippedImageWithTiffImageShouldThrowClippingReport()
                            throws Exception {
        File sourceFile = createNewFile( "overview.png" );
        File destinationFile = createNewTempFileInPngFormat();

        InputStream inputStream = createInputStreamFrom( sourceFile );
        OutputStream outputStream = createOutputStreamFrom( destinationFile );

        ImageClipper wmsClipper = new SimpleRasterClipper();
        try {
            wmsClipper.calculateClippedImage( inputStream, null, outputStream, createWmsRequestWithTifFormat() );
        } finally {
            inputStream.close();
            outputStream.close();
        }
    }

    private File createNewFile( String resourceName ) {
        return new File( SimpleRasterClipperTest.class.getResource( resourceName ).getPath() );
    }

    private File createNewTempFileInPngFormat()
                            throws IOException {
        return File.createTempFile( SimpleRasterClipperTest.class.getSimpleName(), ".png" );
    }

    private File createNewTempFileInJpgFormat()
                            throws IOException {
        return File.createTempFile( SimpleRasterClipperTest.class.getSimpleName(), ".jpg" );
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

    private WmsRequest createWmsRequestWithPngFormat() {
        String format = "image/png";
        return createWmsRequest( format );
    }

    private WmsRequest createWmsRequestWithJpgFormat() {
        String format = "image/jpg";
        return createWmsRequest( format );
    }

    private WmsRequest createWmsRequestWithTifFormat() {
        String format = "image/tif";
        return createWmsRequest( format );
    }

    private WmsRequest createWmsRequestWithPngFormatAndSmallBbox() {
        String format = "image/png";
        Envelope bbox = new Envelope( 8, 10, 51, 53 );
        return createWmsRequest( format, bbox );
    }

    private WmsRequest createWmsRequestWithPngFormatAndEpsg31467() {
        String format = "image/png";
        Envelope bbox = new Envelope( 3381979.18929695, 3658498.77343167, 5600312.52345328, 6060210.89246363 );
        return createWmsRequest( format, bbox, "EPSG:31467" );
    }

    private WmsRequest createWmsRequestWithJpgFormatAndSmallBbox() {
        String format = "image/jpg";
        Envelope bbox = new Envelope( 8, 10, 51, 53 );
        return createWmsRequest( format, bbox );
    }

    private WmsRequest createWmsRequest( String format ) {
        Envelope bbox = new Envelope( 7.3345265546875, 11.454399601562, 50.526648257812, 54.646521304687 );
        return createWmsRequest( format, bbox );
    }

    private WmsRequest createWmsRequest( String format, Envelope bbox ) {
        String crs = "EPSG:4326";
        return createWmsRequest( format, bbox, crs );
    }

    private WmsRequest createWmsRequest( String format, Envelope bbox, String crs ) {
        return new WmsRequest( null, null, null, null, bbox, crs, format );
    }

    private String retrieveFileExtension( File file ) {
        return getExtension( file.getPath() );
    }

    private Geometry asGeometry( WmsRequest wmsRequest ) {
        return new GeometryFactory().toGeometry( wmsRequest.getBbox() );
    }

}

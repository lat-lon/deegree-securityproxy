package org.deegree.securityproxy.wms.responsefilter.clipping;

import static javax.imageio.ImageIO.read;
import static org.junit.Assert.assertThat;

import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.deegree.securityproxy.service.commons.responsefilter.clipping.ImageClipper;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
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

        wmsClipper.calculateClippedImage( inputStream, createVisibleArea(), outputStream );

        inputStream.close();
        outputStream.close();

        assertThat( destinationFile, hasNotSamePixels( sourceFile ) );
    }

    @Test
    public void testCalculateClippedImageWithJpgImage()
                            throws Exception {
        File sourceFile = createNewFile( "saltlakecity_0_0.jpg" );
        File destinationFile = createNewTempFileInJpgFormat();

        InputStream inputStream = createInputStreamFrom( sourceFile );
        OutputStream outputStream = createOutputStreamFrom( destinationFile );

        wmsClipper.calculateClippedImage( inputStream, createVisibleArea(), outputStream );

        inputStream.close();
        outputStream.close();

        assertThat( destinationFile, hasNotSamePixels( sourceFile ) );
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
        Envelope envelope = new Envelope( 10, 20, 10, 20 );
        return new GeometryFactory().toGeometry( envelope );
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

}

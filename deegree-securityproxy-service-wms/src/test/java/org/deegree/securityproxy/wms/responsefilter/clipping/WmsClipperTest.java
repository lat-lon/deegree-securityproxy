package org.deegree.securityproxy.wms.responsefilter.clipping;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.deegree.securityproxy.service.commons.responsefilter.clipping.ImageClipper;
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

}

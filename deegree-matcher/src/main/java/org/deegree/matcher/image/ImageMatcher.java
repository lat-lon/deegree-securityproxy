package org.deegree.matcher.image;

import static javax.imageio.ImageIO.read;

import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * Matcher for images.
 * 
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * 
 * @version $Revision: $, $Date: $
 */
public class ImageMatcher {

    private static boolean INVERTED = true;

    private static boolean NOT_INVERTED = false;

    /**
     * Matcher to check whether image has same dimension as source file.
     * 
     * @param sourceFile
     *            source file to compare with
     * @return a {@link Matcher}
     * @throws IOException
     */
    public static Matcher<File> hasSameDimension( File sourceFile )
                            throws IOException {

        BufferedImage sourceImage = read( sourceFile );
        final int heightSource = sourceImage.getHeight();
        final int widthSource = sourceImage.getWidth();

        return new DimensionMatcher<File>( heightSource, widthSource );
    }

    /**
     * Matcher to check whether image has same pixels as source file.
     * 
     * @param sourceFile
     *            source file to compare with
     * @return a {@link Matcher}
     * @throws Exception
     */
    public static Matcher<File> hasSamePixels( File sourceFile )
                            throws Exception {
        BufferedImage sourceImage = ImageIO.read( sourceFile );
        PixelGrabber sourceGrabber = new PixelGrabber( sourceImage, 0, 0, -1, -1, false );

        final int[] sourcePixelsToCompare = calculatePixels( sourceGrabber );

        return new PixelMatcher<File>( sourcePixelsToCompare, NOT_INVERTED );
    }

    /**
     * Matcher to check whether image has not same pixels as source file.
     * 
     * @param sourceFile
     *            to compare with
     * @return a {@link Matcher}
     * @throws Exception
     */
    public static Matcher<File> hasNotSamePixels( File sourceFile )
                            throws Exception {

        BufferedImage sourceImage = ImageIO.read( sourceFile );
        PixelGrabber sourceGrabber = new PixelGrabber( sourceImage, 0, 0, -1, -1, false );

        final int[] sourcePixelsToCompare = calculatePixels( sourceGrabber );

        return new PixelMatcher<File>( sourcePixelsToCompare, INVERTED );
    }

    private static int[] calculatePixels( PixelGrabber destinationGrabber )
                            throws InterruptedException {
        int[] pixels = null;
        if ( destinationGrabber.grabPixels() ) {
            pixels = (int[]) destinationGrabber.getPixels();
        }
        return pixels;
    }

    /**
     * Matcher to compare dimensions
     *
     * @param <T>
     */
    private static class DimensionMatcher<T> extends BaseMatcher<T> {

        private final int heightSource;

        private final int widthSource;

        DimensionMatcher( int heightSource, int widthSource ) {
            this.heightSource = heightSource;
            this.widthSource = widthSource;
        }

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
            description.appendText( "Should have the same width and height as the source ( " + widthSource + " * "
                                    + heightSource + ")!" );
        }
    };

    private static class PixelMatcher<T> extends BaseMatcher<T> {

        private int[] sourcePixelsToCompare;

        private boolean inverse;

        PixelMatcher( int[] sourcePixelsToCompare, boolean inverse ) {
            this.sourcePixelsToCompare = sourcePixelsToCompare;
            this.inverse = inverse;
        }

        @Override
        public boolean matches( Object item ) {
            BufferedImage destinationImage;
            try {
                destinationImage = read( (File) item );
                PixelGrabber destinationGrabber = new PixelGrabber( destinationImage, 0, 0, -1, -1, false );
                int[] destinationPixels = calculatePixels( destinationGrabber );
                boolean matching = Arrays.equals( destinationPixels, sourcePixelsToCompare );

                return inverse ? !matching : matching;
            } catch ( Exception e ) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public void describeTo( Description description ) {
            String inverseString = inverse ? "not " : "";
            description.appendText( "Should "+ inverseString + "contain the same pixels as the source!" );
        }
    };
}

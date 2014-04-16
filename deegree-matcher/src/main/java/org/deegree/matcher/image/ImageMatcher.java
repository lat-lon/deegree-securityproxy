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

    /**
     * Matcher to check whether image has same dimension as source file.
     * 
     * @param sourceFile
     * @return a {@link Matcher}
     * @throws IOException
     */
    public static Matcher<File> hasSameDimension( File sourceFile )
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

    /**
     * Matcher to check whether image has same pixels as source file.
     * 
     * @param sourceFile
     * @return a {@link Matcher}
     * @throws Exception
     */
    public static Matcher<File> hasSamePixels( File sourceFile )
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

    /**
     * Matcher to check whether image has not same pixels as source file.
     * 
     * @param sourceFile
     * @return a {@link Matcher}
     * @throws Exception
     */
    public static Matcher<File> hasNotSamePixels( File sourceFile )
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

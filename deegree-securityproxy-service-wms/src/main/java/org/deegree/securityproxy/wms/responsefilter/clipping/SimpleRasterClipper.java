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
package org.deegree.securityproxy.wms.responsefilter.clipping;

import static java.lang.String.format;
import static org.apache.commons.io.IOUtils.copy;
import static org.deegree.securityproxy.service.commons.responsefilter.clipping.ClippingUtils.calculateGeometryVisibleAfterClipping;
import static org.deegree.securityproxy.service.commons.responsefilter.clipping.ClippingUtils.isClippingRequired;
import static org.deegree.securityproxy.service.commons.responsefilter.clipping.ClippingUtils.transformGeometry;
import static org.geotools.referencing.CRS.decode;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;

import org.apache.log4j.Logger;
import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.responsefilter.logging.ResponseClippingReport;
import org.deegree.securityproxy.service.commons.responsefilter.clipping.ImageClipper;
import org.deegree.securityproxy.service.commons.responsefilter.clipping.exception.ClippingException;
import org.deegree.securityproxy.wms.request.WmsRequest;
import org.geotools.geometry.jts.LiteShape;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapViewport;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * Concrete implementation to clip images in png and jpeg format.
 * 
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * 
 * @version $Revision: $, $Date: $
 */
public class SimpleRasterClipper implements ImageClipper {

    private static final Logger LOG = Logger.getLogger( SimpleRasterClipper.class );

    private static final String PNG_FORMAT = "PNG";

    private static final String JPG_FORMAT = "JPG";

    private Map<String, String> mimetypeToFileExtensions;

    private final CoordinateReferenceSystem visibleAreaCrs;

    public SimpleRasterClipper() throws NoSuchAuthorityCodeException, FactoryException {
        visibleAreaCrs = decode( "EPSG:4326", true );
    }

    @Override
    public ResponseClippingReport calculateClippedImage( InputStream imageToClip, Geometry visibleArea,
                                                         OutputStream destination, OwsRequest request )
                            throws IllegalArgumentException, ClippingException {
        checkRequiredParameters( imageToClip, destination, request );
        WmsRequest wmsRequest = (WmsRequest) request;
        String format = parseImageFormat( wmsRequest );
        if ( format != null ) {
            try {

                CoordinateReferenceSystem imgCrs = decode( wmsRequest.getCrs(), true );
                Geometry imgBbox = new GeometryFactory().toGeometry( wmsRequest.getBbox() );
                Geometry imgBboxInVisibleAreaCrs = transformGeometry( imgCrs, imgBbox, visibleAreaCrs );
                if ( isClippingRequired( imgBboxInVisibleAreaCrs, visibleArea ) ) {

                    BufferedImage inputImage = ImageIO.read( imageToClip );
                    BufferedImage outputImage = createOutputImage( inputImage, format );

                    executeClipping( visibleArea, imgBboxInVisibleAreaCrs, inputImage, outputImage );

                    writeImage( destination, format, outputImage );
                    Geometry visibleAreaAfterClipping = calculateGeometryVisibleAfterClipping( imgBboxInVisibleAreaCrs,
                                                                                           visibleArea );
                    LOG.debug( "Visible area after clipping: " + visibleAreaAfterClipping );
                    return new ResponseClippingReport( visibleAreaAfterClipping, true );
                } else {
                    LOG.info( "Clipping is not required." );
                    copy( imageToClip, destination );
                    return new ResponseClippingReport( imgBboxInVisibleAreaCrs, false );
                }
            } catch ( Exception e ) {
                LOG.trace( "An error occured during clipping!", e );
                throw new ClippingException( e );
            }
        }
        String msg = format( "Clipping of images with format %s is not supported!", wmsRequest.getFormat() );
        throw new ClippingException( msg );
    }

    private void writeImage( OutputStream destination, String format, BufferedImage outputImage )
                            throws ClippingException, IOException {
        ImageWriter imageWriter = createImageWriter( format );
        ImageWriteParam writerParam = configureWriterParameters( format, imageWriter );
        imageWriter.setOutput( ImageIO.createImageOutputStream( destination ) );
        imageWriter.write( null, new IIOImage( outputImage, null, null ), writerParam );
        imageWriter.dispose();
    }

    private ImageWriteParam configureWriterParameters( String format, ImageWriter imageWriter ) {
        ImageWriteParam writerParam = imageWriter.getDefaultWriteParam();
        if ( JPG_FORMAT.equals( format ) ) {
            writerParam.setCompressionMode( ImageWriteParam.MODE_EXPLICIT );
            writerParam.setCompressionQuality( 1f );
        }
        return writerParam;
    }

    private ImageWriter createImageWriter( String format )
                            throws ClippingException {
        Iterator<ImageWriter> iterator = ImageIO.getImageWritersByFormatName( format );
        ImageWriter imageWriter;
        if ( iterator.hasNext() )
            imageWriter = iterator.next();
        else {
            Exception e = new Exception( "No image writer was found!" );
            throw new ClippingException( e );
        }
        return imageWriter;
    }

    private String parseImageFormat( WmsRequest wmsRequest ) {
        Map<String, String> imageFormats = getAndFillMimetypeToFileExtensions();
        String requestFormat = wmsRequest.getFormat();
        return imageFormats.get( requestFormat );

    }

    private synchronized Map<String, String> getAndFillMimetypeToFileExtensions() {
        if ( mimetypeToFileExtensions == null ) {
            mimetypeToFileExtensions = new HashMap<String, String>();
            mimetypeToFileExtensions.put( "image/png", PNG_FORMAT );
            mimetypeToFileExtensions.put( "image/jpg", JPG_FORMAT );
            mimetypeToFileExtensions.put( "image/jpeg", JPG_FORMAT );
        }
        return mimetypeToFileExtensions;
    }

    private void executeClipping( Geometry visibleArea, Geometry imgBboxInVisibleAreaCrs, BufferedImage inputImage,
                                  BufferedImage outputImage )
                            throws FactoryException, TransformException {
        Graphics2D graphics = (Graphics2D) outputImage.getGraphics();
        LiteShape clippingArea = retrieveWorldToScreenClippingArea( visibleArea, imgBboxInVisibleAreaCrs, inputImage );
        graphics.clip( clippingArea );
        graphics.drawImage( inputImage, null, 0, 0 );
    }

    private LiteShape retrieveWorldToScreenClippingArea( Geometry visibleArea, Geometry imgBboxInVisibleAreaCrs,
                                                         BufferedImage inputImage )
                            throws FactoryException, TransformException {
        AffineTransform transformation = createWorldToScreenTransformation( inputImage, imgBboxInVisibleAreaCrs );
        return new LiteShape( visibleArea, transformation, false );
    }

    private BufferedImage createOutputImage( BufferedImage inputImage, String format ) {
        int inputImageWidth = inputImage.getWidth();
        int inputImageHeight = inputImage.getHeight();
        int inputImageType = inputImage.getType();
        if ( PNG_FORMAT.equals( format ) )
            inputImageType = BufferedImage.TYPE_INT_ARGB;
        return new BufferedImage( inputImageWidth, inputImageHeight, inputImageType );
    }

    private AffineTransform createWorldToScreenTransformation( BufferedImage inputImage,
                                                               Geometry imgBboxInVisibleAreaCrs )
                            throws FactoryException {
        Envelope envelopeInternal = imgBboxInVisibleAreaCrs.getEnvelopeInternal();
        ReferencedEnvelope referencedBbox = new ReferencedEnvelope( envelopeInternal, visibleAreaCrs );
        MapViewport viewPort = createViewPort( inputImage, referencedBbox );
        return viewPort.getWorldToScreen();
    }

    private MapViewport createViewPort( BufferedImage inputImage, ReferencedEnvelope referencedBbox ) {
        MapViewport viewPort = new MapViewport( referencedBbox );
        int screenX = inputImage.getMinX();
        int screenY = inputImage.getMinY();
        int screenWidth = inputImage.getWidth();
        int screenHeight = inputImage.getHeight();
        viewPort.setScreenArea( new Rectangle( screenX, screenY, screenWidth, screenHeight ) );
        return viewPort;
    }

    private void checkRequiredParameters( InputStream imageToClip, OutputStream toWriteImage, OwsRequest request )
                            throws IllegalArgumentException {
        if ( imageToClip == null )
            throw new IllegalArgumentException( "Image to clip must not be null!" );
        if ( toWriteImage == null )
            throw new IllegalArgumentException( "Output stream to write image to must not be null!" );
        if ( request == null )
            throw new IllegalArgumentException( "Request must not be null!" );
    }

}

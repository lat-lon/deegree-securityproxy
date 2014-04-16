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

import static org.geotools.referencing.CRS.decode;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

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
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Concrete implementation to clip images in png and jpeg format.
 * 
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * 
 * @version $Revision: $, $Date: $
 */
public class WmsClipper implements ImageClipper {

    private static final Logger LOG = Logger.getLogger( WmsClipper.class );

    private Map<String, String> mimetypeToFileExtensions;

    @Override
    public ResponseClippingReport calculateClippedImage( InputStream imageToClip, Geometry visibleArea,
                                                         OutputStream destination, OwsRequest request )
                            throws IllegalArgumentException, ClippingException {
        checkRequiredParameters( imageToClip, destination );
        WmsRequest wmsRequest = (WmsRequest) request;
        String format = parseImageFormat( wmsRequest );
        if ( format != null ) {
            try {
                BufferedImage inputImage = ImageIO.read( imageToClip );
                BufferedImage outputImage = createOutputImage( inputImage );
                executeClipping( visibleArea, wmsRequest, inputImage, outputImage );
                ImageIO.write( outputImage, format, destination );
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private String parseImageFormat( WmsRequest wmsRequest ) {
        Map<String, String> imageFormats = getAndFillMimetypeToFileExtensions();
        String requestFormat = wmsRequest.getFormat();
        return imageFormats.get( requestFormat );

    }

    private synchronized Map<String, String> getAndFillMimetypeToFileExtensions() {
        if ( mimetypeToFileExtensions == null ) {
            mimetypeToFileExtensions = new HashMap<String, String>();
            mimetypeToFileExtensions.put( "image/png", "PNG" );
            mimetypeToFileExtensions.put( "image/jpg", "JPG" );
            mimetypeToFileExtensions.put( "image/jpeg", "JPEG" );
        }
        return mimetypeToFileExtensions;
    }

    private void executeClipping( Geometry visibleArea, WmsRequest wmsRequest, BufferedImage inputImage,
                                  BufferedImage outputImage )
                            throws FactoryException {
        Graphics2D graphics = (Graphics2D) outputImage.getGraphics();
        LiteShape clippingArea = retrieveWorldToScreenClippingArea( visibleArea, wmsRequest, inputImage );
        graphics.clip( clippingArea );
        graphics.drawImage( inputImage, null, 0, 0 );
    }

    private LiteShape retrieveWorldToScreenClippingArea( Geometry visibleArea, WmsRequest wmsRequest,
                                                         BufferedImage inputImage )
                            throws FactoryException {
        AffineTransform transformation = createWorldToScreenTransformation( inputImage, wmsRequest );
        return new LiteShape( visibleArea, transformation, false );
    }

    private BufferedImage createOutputImage( BufferedImage inputImage ) {
        int inputImageWidth = inputImage.getWidth();
        int inputImageHeight = inputImage.getHeight();
        return new BufferedImage( inputImageWidth, inputImageHeight, BufferedImage.TYPE_INT_ARGB );
    }

    private AffineTransform createWorldToScreenTransformation( BufferedImage inputImage, WmsRequest wmsRequest )
                            throws FactoryException {
        ReferencedEnvelope referencedBbox = createReferencedBbox( wmsRequest );
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

    private ReferencedEnvelope createReferencedBbox( WmsRequest wmsRequest )
                            throws FactoryException {
        CoordinateReferenceSystem crs = decode( wmsRequest.getCrs(), true );
        Envelope bbox = wmsRequest.getBbox();
        return new ReferencedEnvelope( bbox, crs );
    }

    private void checkRequiredParameters( InputStream imageToClip, OutputStream toWriteImage )
                            throws IllegalArgumentException {
        if ( imageToClip == null )
            throw new IllegalArgumentException( "Image to clip must not be null!" );
        if ( toWriteImage == null )
            throw new IllegalArgumentException( "Output stream to write image to must not be null!" );
    }

}

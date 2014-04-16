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

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.responsefilter.logging.ResponseClippingReport;
import org.deegree.securityproxy.service.commons.responsefilter.clipping.ImageClipper;
import org.deegree.securityproxy.service.commons.responsefilter.clipping.exception.ClippingException;
import org.geotools.geometry.jts.LiteShape;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapViewport;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

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

    @Override
    public ResponseClippingReport calculateClippedImage( InputStream imageToClip, Geometry visibleArea,
                                                         OutputStream destination, OwsRequest request )
                            throws IllegalArgumentException, ClippingException {
        checkRequiredParameters( imageToClip, destination );

        try {
            BufferedImage image = ImageIO.read( imageToClip );

            BufferedImage outputImage = new BufferedImage( image.getWidth(), image.getHeight(), image.getType() );
            Graphics2D graphics = (Graphics2D) outputImage.getGraphics();
            CoordinateReferenceSystem crs = decode( "EPSG:4326", true );
            MapViewport viewPort = new MapViewport( new ReferencedEnvelope( 7.3345265546875, 11.454399601562,
                                                                            50.526648257812, 54.646521304687, crs ) );
            viewPort.setScreenArea( new Rectangle( image.getMinX(), image.getMinY(), image.getWidth(),
                                                   image.getHeight() ) );
            AffineTransform trans = viewPort.getWorldToScreen();
            LiteShape clippingArea = new LiteShape( visibleArea, trans, false );
            graphics.clip( clippingArea );
            graphics.drawImage( image, null, 0, 0 );

            // ImageIO.write( outputImage, "PNG", createTempFile( "imageToClip", ".png" ) );
            ImageIO.write( outputImage, "PNG", destination );
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        return null;
    }

    private void checkRequiredParameters( InputStream imageToClip, OutputStream toWriteImage )
                            throws IllegalArgumentException {
        if ( imageToClip == null )
            throw new IllegalArgumentException( "Image to clip must not be null!" );
        if ( toWriteImage == null )
            throw new IllegalArgumentException( "Output stream to write image to must not be null!" );
    }

}

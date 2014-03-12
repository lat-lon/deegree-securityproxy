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

import static java.io.File.createTempFile;
import static org.apache.commons.io.IOUtils.copy;
import static org.geotools.geometry.jts.JTS.transform;
import static org.geotools.referencing.CRS.decode;
import static org.geotools.referencing.CRS.findMathTransform;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.metadata.IIOMetadataNode;

import org.apache.log4j.Logger;
import org.deegree.securityproxy.responsefilter.logging.ResponseClippingReport;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.processing.operation.Crop;
import org.geotools.coverage.processing.operation.Resample;
import org.geotools.coverage.processing.operation.Rescale;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.geometry.GeneralEnvelope;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * Concrete implementation to clip geotiffs. The image dimension is kept as in the original image as well as the geotiff
 * header values. Invisible areas are cropped and in the image stored as no data values.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="mailto:goltz@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class GeotiffClipper implements ImageClipper {

    private static final Logger LOG = Logger.getLogger( GeotiffClipper.class );

    private static final String VISIBLE_AREA_CRS = "EPSG:4326";

    @Override
    public ResponseClippingReport calculateClippedImage( InputStream imageToClip, Geometry visibleArea,
                                                         OutputStream destination )
                            throws IllegalArgumentException, ClippingException {
        checkRequiredParameters( imageToClip, destination );

        try {
            File imageToClipAsFile = writeToTempFile( imageToClip );
            GeoTiffReader reader = new GeoTiffReader( imageToClipAsFile );

            Geometry visibleAreaInImageCrs = null;
            if ( visibleArea != null ) {
                visibleAreaInImageCrs = transformVisibleAreaToImageCrs( visibleArea, reader );
                LOG.debug( "Transformed visible geometry: " + visibleAreaInImageCrs );
            } else {
                LOG.debug( "Clipping geometry is full extend as no clipping area is defined!" );
            }

            GridCoverage2D geotiff = reader.read( null );
            IIOMetadataNode metadataRootNode = reader.getMetadata().getRootNode();

            GeoTiffWriterModified writer = new GeoTiffWriterModified( destination );
            writer.setIIOMetadata( metadataRootNode );

            Geometry imgEnvelope = convertImageEnvelopeToGeometry( reader );
            if ( isClippingRequired( imgEnvelope, visibleAreaInImageCrs ) ) {
                LOG.debug( "Clipping is required!" );
                GridCoverage2D clippedGeotiff = calculateClippedGeotiff( visibleAreaInImageCrs, geotiff, imgEnvelope );
                writer.write( clippedGeotiff, null );
                Geometry visibleAreaAfterClipping = calculateAreaVisibleAfterClipping( reader, visibleAreaInImageCrs );
                LOG.debug( "Visible area after clipping: " + visibleAreaAfterClipping );
                Geometry visibleAreaAfterClippingInOriginalCrs = transformToVisibleAreaCrs( visibleAreaAfterClipping,
                                                                                            reader );
                return new ResponseClippingReport( visibleAreaAfterClippingInOriginalCrs, true );
            } else {
                LOG.debug( "Clipping is not required!" );
                writer.write( geotiff, null );
                Geometry imageEnvelopeInOriginalCrs = transformToVisibleAreaCrs( imgEnvelope, reader );
                return new ResponseClippingReport( imageEnvelopeInOriginalCrs, false );
            }
        } catch ( Exception e ) {
            LOG.error( "An error occured during clipping the image!", e );
            throw new ClippingException( e );
        }
    }

    /**
     * Checks if clipping is required for the passed reader
     * 
     * @param reader
     *            never <code>null</code>
     * @param clippingGeometry
     *            in image crs, never <code>null</code>
     * @return <code>true</code> if the image boundary is not completely inside the clippingGeometry, <code>false</code>
     *         otherwise
     */
    boolean isClippingRequired( GeoTiffReader reader, Geometry clippingGeometry ) {
        Geometry imageGeometry = convertImageEnvelopeToGeometry( reader );
        return isClippingRequired( imageGeometry, clippingGeometry );
    }

    /**
     * Calculates the geometry visible after clipping
     * 
     * @param reader
     *            never <code>null</code>
     * @param visibleArea
     *            in image crs, never <code>null</code>
     * @return the geometry visible after clipping
     */
    Geometry calculateAreaVisibleAfterClipping( GeoTiffReader reader, Geometry visibleArea ) {
        Geometry imageGeometry = convertImageEnvelopeToGeometry( reader );
        if ( visibleArea.contains( imageGeometry ) ) {
            return imageGeometry;
        } else if ( visibleArea.intersects( imageGeometry ) ) {
            return visibleArea.intersection( imageGeometry );
        }
        return new GeometryFactory().toGeometry( new Envelope() );
    }

    /**
     * Transforms the passed geometry into the CRS of the reader
     * 
     * @param geometryToTransform
     *            in WGS84 (axis order: longitude/latitude), never <code>null</code>
     * @param reader
     *            never <code>null</code>
     * @return the transformed geometry, never <code>null</code>
     * @throws FactoryException
     * @throws TransformException
     */
    Geometry transformVisibleAreaToImageCrs( Geometry geometryToTransform, GeoTiffReader reader )
                            throws FactoryException, TransformException {
        CoordinateReferenceSystem visibleAreaCRS = decode( VISIBLE_AREA_CRS, true );
        CoordinateReferenceSystem imageCRS = reader.getCrs();

        MathTransform transformVisibleAreaToImageCrs = findMathTransform( visibleAreaCRS, imageCRS );
        return transform( geometryToTransform, transformVisibleAreaToImageCrs );
    }

    /**
     * Transforms the passed geometry into the CRS of the visible area
     * 
     * @param geometryToTransform
     *            in coordinate system of the image to clip, never <code>null</code>
     * @param reader
     *            never <code>null</code>
     * @return the transformed geometry, never <code>null</code>
     * @throws FactoryException
     * @throws TransformException
     */
    Geometry transformToVisibleAreaCrs( Geometry geometryToTransform, GeoTiffReader reader )
                            throws FactoryException, TransformException {
        CoordinateReferenceSystem imageCRS = reader.getCrs();
        CoordinateReferenceSystem visibleAreaCRS = decode( VISIBLE_AREA_CRS, true );

        MathTransform transformToVisibleAreaCrs = findMathTransform( imageCRS, visibleAreaCRS );
        return transform( geometryToTransform, transformToVisibleAreaCrs );
    }

    // TODO: dirty hack! coverage should be read from input stream directly
    private File writeToTempFile( InputStream coverageToClip )
                            throws IOException {
        File tempFile = createTempFile( "imageToClip", ".tif" );
        LOG.trace( "Response image was written into file: " + tempFile.getAbsolutePath() );
        FileOutputStream output = new FileOutputStream( tempFile );
        copy( coverageToClip, output );
        output.close();
        return tempFile;
    }

    private void checkRequiredParameters( InputStream imageToClip, OutputStream toWriteImage )
                            throws IllegalArgumentException {
        if ( imageToClip == null )
            throw new IllegalArgumentException( "Image to clip must not be null!" );
        if ( toWriteImage == null )
            throw new IllegalArgumentException( "Output stream to write image to must not be null!" );
    }

    private GridCoverage2D calculateClippedGeotiff( Geometry visibleAreaInImageCrs, GridCoverage2D geotiffToWrite,
                                                    Geometry imageEnvelope ) {
        GridCoverage2D modifiedCoverageToWrite;
        if ( visibleAreaInImageCrs.intersects( imageEnvelope ) ) {
            modifiedCoverageToWrite = executeCropping( geotiffToWrite, visibleAreaInImageCrs );
        } else {
            modifiedCoverageToWrite = executeRescalingToNull( geotiffToWrite );
        }
        return executeResampling( modifiedCoverageToWrite, geotiffToWrite );
    }

    private GridCoverage2D executeCropping( GridCoverage2D coverageToWrite, Geometry croppingArea ) {
        Crop crop = new Crop();
        ParameterValueGroup cropParameters = crop.getParameters();
        cropParameters.parameter( "Source" ).setValue( coverageToWrite );
        cropParameters.parameter( "ROI" ).setValue( croppingArea );
        return (GridCoverage2D) crop.doOperation( cropParameters, null );
    }

    private GridCoverage2D executeResampling( GridCoverage2D coverageToWrite, GridCoverage2D coverageToGetGeometry ) {
        Resample resample = new Resample();
        ParameterValueGroup resampleParameters = resample.getParameters();
        resampleParameters.parameter( "Source" ).setValue( coverageToWrite );
        resampleParameters.parameter( "GridGeometry" ).setValue( coverageToGetGeometry.getGridGeometry() );
        return (GridCoverage2D) resample.doOperation( resampleParameters, null );
    }

    private GridCoverage2D executeRescalingToNull( GridCoverage2D coverageToWrite ) {
        Rescale rescale = new Rescale();
        ParameterValueGroup rescaleParameters = rescale.getParameters();
        rescaleParameters.parameter( "Source" ).setValue( coverageToWrite );
        rescaleParameters.parameter( "constants" ).setValue( new double[] { 0 } );
        return (GridCoverage2D) rescale.doOperation( rescaleParameters, null );
    }

    private Geometry convertImageEnvelopeToGeometry( GeoTiffReader reader ) {
        GeometryFactory geometryFactory = new GeometryFactory();
        GeneralEnvelope imageEnvelope = reader.getOriginalEnvelope();
        ReferencedEnvelope envelope = new ReferencedEnvelope( imageEnvelope );
        return geometryFactory.toGeometry( envelope );
    }

    private boolean isClippingRequired( Geometry imageGeometry, Geometry clippingGeometry ) {
        return clippingGeometry != null && !clippingGeometry.contains( imageGeometry );
    }

}
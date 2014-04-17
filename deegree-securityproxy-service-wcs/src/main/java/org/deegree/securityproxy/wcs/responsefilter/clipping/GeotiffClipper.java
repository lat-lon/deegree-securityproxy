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
import static org.deegree.securityproxy.service.commons.responsefilter.clipping.ClippingUtils.calculateGeometryVisibleAfterClipping;
import static org.deegree.securityproxy.service.commons.responsefilter.clipping.ClippingUtils.isClippingRequired;
import static org.deegree.securityproxy.service.commons.responsefilter.clipping.ClippingUtils.transformGeometry;
import static org.geotools.referencing.CRS.decode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.metadata.IIOMetadataNode;

import org.apache.log4j.Logger;
import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.responsefilter.logging.ResponseClippingReport;
import org.deegree.securityproxy.service.commons.responsefilter.clipping.ImageClipper;
import org.deegree.securityproxy.service.commons.responsefilter.clipping.exception.ClippingException;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.processing.operation.Crop;
import org.geotools.coverage.processing.operation.Resample;
import org.geotools.coverage.processing.operation.Rescale;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.geometry.GeneralEnvelope;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * Concrete implementation to clip geotiffs. The image dimension is kept as in the original image as well as the geotiff
 * header values. Invisible areas are cropped and in the image stored as no data values.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class GeotiffClipper implements ImageClipper {

    private static final Logger LOG = Logger.getLogger( GeotiffClipper.class );

    private final CoordinateReferenceSystem visibleAreaCrs;

    public GeotiffClipper() throws NoSuchAuthorityCodeException, FactoryException {
        visibleAreaCrs = decode( "EPSG:4326", true );
    }

    @Override
    public ResponseClippingReport calculateClippedImage( InputStream imageToClip, Geometry visibleArea,
                                                         OutputStream destination, OwsRequest request )
                            throws IllegalArgumentException, ClippingException {
        checkRequiredParameters( imageToClip, destination );

        try {
            File imageToClipAsFile = writeToTempFile( imageToClip );
            GeoTiffReader reader = new GeoTiffReader( imageToClipAsFile );

            CoordinateReferenceSystem imgCrs = reader.getCrs();
            Geometry visibleAreaInImageCrs = transformGeometry( visibleAreaCrs, visibleArea, imgCrs );

            GridCoverage2D geotiff = reader.read( null );
            IIOMetadataNode metadataRootNode = reader.getMetadata().getRootNode();

            GeoTiffWriterModified writer = new GeoTiffWriterModified( destination );
            writer.setIIOMetadata( metadataRootNode );

            Geometry imgEnvelope = convertImageEnvelopeToGeometry( reader );
            if ( isClippingRequired( imgEnvelope, visibleAreaInImageCrs ) ) {
                LOG.debug( "Clipping is required!" );
                GridCoverage2D clippedGeotiff = calculateClippedGeotiff( visibleAreaInImageCrs, geotiff, imgEnvelope );
                writer.write( clippedGeotiff, null );
                Geometry imageGeometry = convertImageEnvelopeToGeometry( reader );
                Geometry visibleAreaAfterClipping = calculateGeometryVisibleAfterClipping( imageGeometry,
                                                                                       visibleAreaInImageCrs );
                LOG.debug( "Visible area after clipping: " + visibleAreaAfterClipping );
                Geometry visibleAreaAfterClippingInOriginalCrs = transformGeometry( visibleAreaCrs,
                                                                                            visibleAreaAfterClipping,
                                                                                            imgCrs );
                return new ResponseClippingReport( visibleAreaAfterClippingInOriginalCrs, true );
            } else {
                LOG.debug( "Clipping is not required!" );
                writer.write( geotiff, null );
                Geometry imageEnvelopeInOriginalCrs = transformGeometry( visibleAreaCrs, imgEnvelope, imgCrs );
                return new ResponseClippingReport( imageEnvelopeInOriginalCrs, false );
            }
        } catch ( Exception e ) {
            LOG.error( "An error occured during clipping the image!", e );
            throw new ClippingException( e );
        }
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

}
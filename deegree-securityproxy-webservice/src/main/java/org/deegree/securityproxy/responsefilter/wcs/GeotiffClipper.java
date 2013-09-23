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
package org.deegree.securityproxy.responsefilter.wcs;

import static org.geotools.geometry.jts.JTS.transform;
import static org.geotools.referencing.CRS.decode;
import static org.geotools.referencing.CRS.findMathTransform;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.processing.operation.Crop;
import org.geotools.data.DataSourceException;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.gce.geotiff.GeoTiffWriter;
import org.geotools.geometry.GeneralEnvelope;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * Concrete implementation to clip geotiffs.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="mailto:goltz@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class GeotiffClipper implements ImageClipper {

    private static final String VISIBLE_AREA_CRS = "EPSG:4326";

    @Override
    public void calculateClippedImage( InputStream coverageToClip, Geometry visibleArea, OutputStream destination )
                            throws IllegalArgumentException {
        checkRequiredParameters( coverageToClip, visibleArea, destination );

        // / TODO: check if exception was thrown!
        try {
            File coverageToClipAsFile = writeToTempFile( coverageToClip );
            GeoTiffReader reader = new GeoTiffReader( coverageToClipAsFile );

            Geometry transformedVisibleArea = transformVisibleAreaToImageCrs( visibleArea, reader );

            GeoTiffWriter writer = new GeoTiffWriter( destination );
            GridCoverage2D coverageToWrite = (GridCoverage2D) reader.read( null );

            if ( isClippingRequired( reader, transformedVisibleArea ) ) {
                GridCoverage2D croppedCoverageToWrite = executeCropping( coverageToWrite, transformedVisibleArea );
                writer.write( croppedCoverageToWrite, null );
            } else {
                writer.write( coverageToWrite, null );
            }
        } catch ( DataSourceException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch ( IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch ( NoSuchAuthorityCodeException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch ( FactoryException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch ( TransformException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // TODO: dirty hack! coverage should be read from input stream directly
    private File writeToTempFile( InputStream coverageToClip )
                            throws IOException, FileNotFoundException {
        File tempFile = File.createTempFile( "imageToClip", ".tif" );
        FileOutputStream output = new FileOutputStream( tempFile );
        IOUtils.copy( coverageToClip, output );
        output.close();
        return tempFile;
    }

    private void checkRequiredParameters( InputStream imageToClip, Geometry visibleArea, OutputStream toWriteImage )
                            throws IllegalArgumentException {
        if ( imageToClip == null )
            throw new IllegalArgumentException( "Image to clip must not be null!" );
        if ( visibleArea == null )
            throw new IllegalArgumentException( "Wcs request must not be null!" );
        if ( toWriteImage == null )
            throw new IllegalArgumentException( "Output stream to write image to must not be null!" );
    }

    private Geometry transformVisibleAreaToImageCrs( Geometry visibleArea, GeoTiffReader reader )
                            throws NoSuchAuthorityCodeException, FactoryException, TransformException {
        CoordinateReferenceSystem visibleAreaCRS = decode( VISIBLE_AREA_CRS );
        CoordinateReferenceSystem imageCRS = reader.getCrs();

        MathTransform transformVisibleAreaToImageCrs = findMathTransform( visibleAreaCRS, imageCRS );
        return transform( visibleArea, transformVisibleAreaToImageCrs );
    }

    boolean isClippingRequired( GeoTiffReader reader, Geometry clippingGeometry ) {
        GeneralEnvelope imageEnvelope = reader.getOriginalEnvelope();
        GeometryFactory geometryFactory = new GeometryFactory();
        Geometry imageGeometry = geometryFactory.toGeometry( new ReferencedEnvelope( imageEnvelope ) );
        return !clippingGeometry.contains( imageGeometry );
    }

    private GridCoverage2D executeCropping( GridCoverage2D coverageToWrite, Geometry transformedVisibleArea ) {
        Crop crop = new Crop();
        ParameterValueGroup cropParameters = crop.getParameters();
        cropParameters.parameter( "Source" ).setValue( coverageToWrite );
        cropParameters.parameter( "ROI" ).setValue( transformedVisibleArea );
        GridCoverage2D croppedCoverageToWrite = (GridCoverage2D) crop.doOperation( cropParameters, null );
        return croppedCoverageToWrite;
    }
}
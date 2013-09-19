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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.DataSourceException;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.gce.geotiff.GeoTiffWriter;
import org.geotools.geometry.GeneralEnvelope;
import org.opengis.parameter.GeneralParameterValue;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Concrete implementation to clip geotiffs.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class GeotiffClipper implements ImageClipper {

    @Override
    public void calculateClippedImage( InputStream coverageToClip, Geometry visibleArea, OutputStream destination )
                            throws IllegalArgumentException {
        checkRequiredParameters( coverageToClip, visibleArea, destination );

        // / TODO: check if exception was thrown!
        try {
            File coverageToClipAsFile = writeToTempFile( coverageToClip );

            GeoTiffReader reader = new GeoTiffReader( coverageToClipAsFile );
            GeneralEnvelope imageEnvelope = reader.getOriginalEnvelope();

            if ( isClippingRequired( imageEnvelope, visibleArea ) ) {
                GeoTiffWriter writer = new GeoTiffWriter( destination );

                GridCoverage2D coverageToWrite = (GridCoverage2D) reader.read( null );
                GeneralParameterValue[] params = null;

                writer.write( coverageToWrite, params );
            }
        } catch ( DataSourceException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch ( IOException e ) {
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

    private boolean isClippingRequired( GeneralEnvelope originalEnvelope, Geometry clippingGeometry ) {
        System.out.println( originalEnvelope );
        // TODO Auto-generated method stub
        return true;
    }
}
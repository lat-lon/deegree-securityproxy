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
package org.deegree.securityproxy.service.commons.responsefilter.clipping;

import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.io.IOUtils.write;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.deegree.securityproxy.authentication.ows.raster.GeometryFilterInfo;
import org.deegree.securityproxy.authentication.ows.raster.OwsUser;
import org.deegree.securityproxy.filter.StatusCodeResponseBodyWrapper;
import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.responsefilter.logging.DefaultResponseFilterReport;
import org.deegree.securityproxy.responsefilter.logging.ResponseClippingReport;
import org.deegree.securityproxy.responsefilter.logging.ResponseFilterReport;
import org.deegree.securityproxy.service.commons.responsefilter.AbstractResponseFilterManager;
import org.deegree.securityproxy.service.commons.responsefilter.clipping.exception.ClippingException;
import org.deegree.securityproxy.service.commons.responsefilter.clipping.geometry.GeometryRetriever;
import org.springframework.security.core.Authentication;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTWriter;

/**
 * Provides filtering of {@link OwsRequest}s.
 * 
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * 
 * @version $Revision: $, $Date: $
 */
public abstract class AbstractClippingResponseFilterManager extends AbstractResponseFilterManager {

    public static final String REQUEST_AREA_HEADER_KEY = "request_area";

    public static final int DEFAULT_STATUS_CODE = 500;

    public static final String DEFAULT_BODY = "Clipping failed!";

    private static final Logger LOG = Logger.getLogger( AbstractClippingResponseFilterManager.class );

    protected final String exceptionBody;

    protected final int exceptionStatusCode;

    private final ImageClipper imageClipper;

    private final GeometryRetriever geometryRetriever;

    /**
     * Instantiates a new {@link AbstractClippingResponseFilterManager} with default exception body (DEFAULT_BODY) and
     * status code (DEFAULT_STATUS_CODE).
     */
    public AbstractClippingResponseFilterManager( ImageClipper imageClipper, GeometryRetriever geometryRetriever ) {
        this( DEFAULT_BODY, DEFAULT_STATUS_CODE, imageClipper, geometryRetriever );

    }

    /**
     * Instantiates a new {@link AbstractClippingResponseFilterManager} with the passed exception body and status code.
     * 
     * @param pathToExceptionFile
     *            if null or not available, the default exception body (DEFAULT_BODY) is used
     * @param exceptionStatusCode
     *            the exception status code
     */
    public AbstractClippingResponseFilterManager( String pathToExceptionFile, int exceptionStatusCode,
                                                  ImageClipper imageClipper, GeometryRetriever geometryRetriever ) {
        this.exceptionBody = readExceptionBodyFromFile( pathToExceptionFile );
        this.exceptionStatusCode = exceptionStatusCode;
        this.imageClipper = imageClipper;
        this.geometryRetriever = geometryRetriever;
    }

    @Override
    protected ResponseFilterReport applyFilter( StatusCodeResponseBodyWrapper servletResponse, OwsRequest request,
                                                Authentication auth )
                    throws IOException {
        try {
            Geometry clippingGeometry = retrieveGeometryUsedForClipping( auth, request );
            return processClippingAndAddHeaderInfo( servletResponse, clippingGeometry, request );
        } catch ( ParseException e ) {
            return handleException( servletResponse, e );
        } catch ( ClippingException e ) {
            return handleException( servletResponse, e );
        }
    }

    @Override
    protected ResponseFilterReport handleIOException( StatusCodeResponseBodyWrapper servletResponse, IOException e ) {
        return handleException( servletResponse, e );
    }

    /**
     * Retrieves all requested layer names.
     * 
     * @param request
     *            containing the request parameter, never <code>null</code>
     * @return List of requested layer names, never <code>null</code> or empty
     * @throws IllegalArgumentException
     *             - request does not contain any layer names
     */
    protected abstract List<String> retrieveLayerNames( OwsRequest request );

    private DefaultResponseFilterReport processClippingAndAddHeaderInfo( StatusCodeResponseBodyWrapper servletResponse,
                                                                         Geometry clippingGeometry, OwsRequest request )
                    throws IOException, ClippingException {
        InputStream imageAsStream = servletResponse.getBufferedStream();
        ByteArrayOutputStream destination = new ByteArrayOutputStream();
        ResponseClippingReport clippedImageReport = imageClipper.calculateClippedImage( imageAsStream,
                                                                                        clippingGeometry, destination,
                                                                                        request );

        addHeaderInfoIfNoFailureOccurred( servletResponse, clippedImageReport );
        // required to set the header (must be set BEFORE any data is written to the response)
        destination.writeTo( servletResponse.getRealOutputStream() );
        return clippedImageReport;
    }

    private void writeExceptionBodyAndSetExceptionStatusCode( StatusCodeResponseBodyWrapper servletResponse ) {
        try {
            OutputStream destination = servletResponse.getRealOutputStream();
            writeExceptionBodyAndSetExceptionStatusCode( servletResponse, destination );
        } catch ( IOException e ) {
            LOG.error( "An error occurred during writing the exception response!", e );
        }
    }

    private void writeExceptionBodyAndSetExceptionStatusCode( StatusCodeResponseBodyWrapper servletResponse,
                                                              OutputStream destination )
                    throws IOException {
        servletResponse.setStatus( exceptionStatusCode );
        write( exceptionBody, destination );
    }

    private Geometry retrieveGeometryUsedForClipping( Authentication auth, OwsRequest request )
                    throws IllegalArgumentException, ParseException {
        OwsUser rasterUser = retrieveRasterUser( auth );
        List<GeometryFilterInfo> geometryFilterInfos = rasterUser.getRasterGeometryFilterInfos();
        List<String> layerNames = retrieveLayerNames( request );
        return geometryRetriever.retrieveGeometry( layerNames, geometryFilterInfos );
    }

    private OwsUser retrieveRasterUser( Authentication auth ) {
        Object principal = auth.getPrincipal();
        if ( !( principal instanceof OwsUser ) ) {
            throw new IllegalArgumentException( "Principal is not a RasterUser!" );
        }
        return (OwsUser) principal;
    }

    private void addHeaderInfoIfNoFailureOccurred( StatusCodeResponseBodyWrapper servletResponse,
                                                   ResponseClippingReport clippedImageReport ) {
        if ( noFailureOccured( clippedImageReport ) ) {
            WKTWriter writer = new WKTWriter();
            String requestAreaWkt = writer.write( clippedImageReport.getReturnedVisibleArea() );
            LOG.debug( "Add header '" + REQUEST_AREA_HEADER_KEY + "': " + requestAreaWkt );
            servletResponse.addHeader( REQUEST_AREA_HEADER_KEY, requestAreaWkt );
        }
    }

    private boolean noFailureOccured( ResponseClippingReport clippedImageReport ) {
        return clippedImageReport.getReturnedVisibleArea() != null;
    }

    private String readExceptionBodyFromFile( String pathToExceptionFile ) {
        LOG.info( "Reading exception body from " + pathToExceptionFile );
        if ( pathToExceptionFile != null && pathToExceptionFile.length() > 0 ) {
            InputStream exceptionAsStream = null;
            try {
                File exceptionFile = new File( pathToExceptionFile );
                exceptionAsStream = new FileInputStream( exceptionFile );
                return IOUtils.toString( exceptionAsStream );
            } catch ( FileNotFoundException e ) {
                LOG.warn( "Could not read exception message from file: File not found! Defaulting to " + DEFAULT_BODY );
            } catch ( IOException e ) {
                LOG.warn( "Could not read exception message from file. Defaulting to " + DEFAULT_BODY + "Reason: "
                          + e.getMessage() );
            } finally {
                closeQuietly( exceptionAsStream );
            }
        }
        return DEFAULT_BODY;
    }

    private DefaultResponseFilterReport handleException( StatusCodeResponseBodyWrapper servletResponse, Exception e ) {
        LOG.error( "Calculating clipped result image failed!", e );
        writeExceptionBodyAndSetExceptionStatusCode( servletResponse );
        return new DefaultResponseFilterReport( "" + e.getMessage() );
    }

}

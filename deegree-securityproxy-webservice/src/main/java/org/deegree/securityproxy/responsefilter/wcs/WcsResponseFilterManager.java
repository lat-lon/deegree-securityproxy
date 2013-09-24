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

import static org.deegree.securityproxy.commons.WcsOperationType.GETCOVERAGE;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.deegree.securityproxy.authentication.WcsGeometryFilterInfo;
import org.deegree.securityproxy.authentication.WcsUser;
import org.deegree.securityproxy.filter.StatusCodeResponseBodyWrapper;
import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.request.WcsRequest;
import org.deegree.securityproxy.responsefilter.ResponseFilterManager;
import org.deegree.securityproxy.responsefilter.logging.ResponseClippingReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTWriter;

/**
 * Provides filtering of {@link WcsRequest}s.
 * 
 * @author <a href="mailto:erben@lat-lon.de">Alexander Erben</a>
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class WcsResponseFilterManager implements ResponseFilterManager {

    static final String REQUEST_AREA_HEADER_KEY = "request_area";

    static final String NOT_A_COVERAGE_REQUEST_MSG = "Request was not a GetCoverage-Request - clipping not required.";

    static final String NO_LIMITING_GEOMETRY_MSG = "No limiting geometry defined!";

    private static Logger LOG = Logger.getLogger( WcsResponseFilterManager.class );

    @Autowired
    private GeometryRetriever geometryRetriever;

    @Autowired
    private ImageClipper imageClipper;

    @Override
    public ResponseClippingReport filterResponse( StatusCodeResponseBodyWrapper servletResponse, OwsRequest request,
                                                  Authentication auth ) {
        checkParameters( servletResponse, request );
        WcsRequest wcsRequest = (WcsRequest) request;
        if ( isGetCoverageRequest( wcsRequest ) ) {
            try {
                Geometry clippingGeometry = retrieveGeometryUseForClipping( auth, wcsRequest );
                if ( clippingGeometry == null ) {
                    return new ResponseClippingReport( NO_LIMITING_GEOMETRY_MSG );
                }
                InputStream imageAsStream = servletResponse.getBufferedStream();
                OutputStream destination = servletResponse.getRealOutputStream();
                ResponseClippingReport clippedImageReport = imageClipper.calculateClippedImage( imageAsStream,
                                                                                                clippingGeometry,
                                                                                                destination );

                addHeaderInfoIfNoFailureOccurred( servletResponse, clippedImageReport );
                return clippedImageReport;
            } catch ( ParseException e ) {
                LOG.error( "Calculating clipped result image failed!", e );
                return new ResponseClippingReport( e.getMessage() );
            } catch ( IOException e ) {
                LOG.error( "Calculating clipped result image failed!", e );
                return new ResponseClippingReport( e.getMessage() );
            }

        }
        return new ResponseClippingReport( NOT_A_COVERAGE_REQUEST_MSG );
    }

    @Override
    public <T extends OwsRequest> boolean supports( Class<T> clazz ) {
        if ( WcsRequest.class.equals( clazz ) )
            return true;
        return false;
    }

    private void checkParameters( HttpServletResponse servletResponse, OwsRequest request ) {
        if ( servletResponse == null )
            throw new IllegalArgumentException( "Parameter servletResponse may not be null!" );
        if ( request == null )
            throw new IllegalArgumentException( "Parameter request may not be null!" );
        if ( !supports( request.getClass() ) )
            throw new IllegalArgumentException( "OwsRequest of class " + request.getClass().getCanonicalName()
                                                + " is not supported!" );
    }

    private Geometry retrieveGeometryUseForClipping( Authentication auth, WcsRequest wcsRequest )
                            throws IllegalArgumentException, ParseException {
        WcsUser wcsUser = retrieveWcsUser( auth );
        List<WcsGeometryFilterInfo> geometryFilterInfos = wcsUser.getWcsGeometryFilterInfos();
        String coverageName = retrieveCoverageName( wcsRequest );
        return geometryRetriever.retrieveGeometry( coverageName, geometryFilterInfos );
    }

    private WcsUser retrieveWcsUser( Authentication auth ) {
        Object principal = auth.getPrincipal();
        if ( !( principal instanceof WcsUser ) ) {
            throw new IllegalArgumentException( "Principal is not a WcsUser!" );
        }
        return (WcsUser) principal;
    }

    private String retrieveCoverageName( WcsRequest wcsRequest ) {
        List<String> coverageNames = wcsRequest.getCoverageNames();
        if ( coverageNames == null || coverageNames.isEmpty() )
            throw new IllegalArgumentException( "GetCoverage request does not contain a coverage name!" );
        String coverageName = coverageNames.get( 0 );
        if ( coverageName == null )
            throw new IllegalArgumentException( "coverage is null!" );
        return coverageName;
    }

    private void addHeaderInfoIfNoFailureOccurred( StatusCodeResponseBodyWrapper servletResponse,
                                                   ResponseClippingReport clippedImageReport ) {
        if ( noFailureOccured( clippedImageReport ) ) {
            WKTWriter writer = new WKTWriter();
            String requestAreaWkt = writer.write( clippedImageReport.getReturnedVisibleArea() );
            servletResponse.addHeader( REQUEST_AREA_HEADER_KEY, requestAreaWkt );
        }
    }

    private boolean noFailureOccured( ResponseClippingReport clippedImageReport ) {
        return clippedImageReport.getFailure() == null && clippedImageReport.getReturnedVisibleArea() != null;
    }

    private boolean isGetCoverageRequest( WcsRequest wcsRequest ) {
        return GETCOVERAGE.equals( wcsRequest.getOperationType() );
    }

}
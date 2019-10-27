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

import static org.deegree.securityproxy.wms.request.WmsRequestParser.GETMAP;

import java.util.List;

import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.service.commons.responsefilter.clipping.AbstractClippingResponseFilterManager;
import org.deegree.securityproxy.service.commons.responsefilter.clipping.ImageClipper;
import org.deegree.securityproxy.service.commons.responsefilter.clipping.geometry.GeometryRetriever;
import org.deegree.securityproxy.wms.request.WmsRequest;

/**
 * Provides filtering of {@link WmsRequest}s.
 * 
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * 
 * @version $Revision: $, $Date: $
 */
public class WmsClippingResponseFilterManager extends AbstractClippingResponseFilterManager {

    /**
     * Instantiates a new {@link WmsClippingResponseFilterManager} with default exception body (DEFAULT_BODY) and status
     * code (DEFAULT_STATUS_CODE).
     */
    public WmsClippingResponseFilterManager( ImageClipper imageClipper, GeometryRetriever geometryRetriever ) {
        super( imageClipper, geometryRetriever );
    }

    /**
     * Instantiates a new {@link WmsClippingResponseFilterManager} with the passed exception body and status code.
     * 
     * @param pathToExceptionFile
     *            if null or not available, the default exception body (DEFAULT_BODY) is used
     * @param exceptionStatusCode
     *            the exception status code
     */
    public WmsClippingResponseFilterManager( String pathToExceptionFile, int exceptionStatusCode,
                                             ImageClipper imageClipper, GeometryRetriever geometryRetriever ) {
        super( pathToExceptionFile, exceptionStatusCode, imageClipper, geometryRetriever );
    }

    @Override
    protected boolean isCorrectServiceType( OwsRequest request ) {
        return WmsRequest.class.equals( request.getClass() );
    }

    @Override
    protected boolean isCorrectRequestParameter( OwsRequest request ) {
        return GETMAP.equals( request.getOperationType() );
    }

    @Override
    protected List<String> retrieveLayerNames( OwsRequest request ) {
        List<String> layerNames = ( (WmsRequest) request ).getLayerNames();
        if ( layerNames == null || layerNames.isEmpty() )
            throw new IllegalArgumentException( "GetMap request does not contain a layer name!" );
        return layerNames;
    }

}

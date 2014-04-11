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

import static org.deegree.securityproxy.wcs.request.WcsRequestParser.GETCOVERAGE;

import java.util.List;

import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.service.commons.responsefilter.clipping.AbstractClippingResponseFilterManager;
import org.deegree.securityproxy.wcs.request.WcsRequest;

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
public class WcsClippingResponseFilterManager extends AbstractClippingResponseFilterManager {

    /**
     * Instantiates a new {@link WcsClippingResponseFilterManager} with default exception body (DEFAULT_BODY) and status
     * code (DEFAULT_STATUS_CODE).
     */
    public WcsClippingResponseFilterManager() {
        super();
    }

    /**
     * Instantiates a new {@link WcsClippingResponseFilterManager} with the passed exception body and status code.
     * 
     * @param pathToExceptionFile
     *            if null or not available, the default exception body (DEFAULT_BODY) is used
     * @param exceptionStatusCode
     *            the exception status code
     */
    public WcsClippingResponseFilterManager( String pathToExceptionFile, int exceptionStatusCode ) {
        super( pathToExceptionFile, exceptionStatusCode );
    }

    @Override
    protected boolean isCorrectRequestType( OwsRequest request ) {
        return WcsRequest.class.equals( request.getClass() );
    }

    @Override
    protected boolean isCorrectRequest( OwsRequest owsRequest ) {
        return GETCOVERAGE.equals( owsRequest.getOperationType() );
    }

    @Override
    protected String retrieveLayerName( OwsRequest request ) {
        List<String> coverageNames = ( (WcsRequest) request ).getCoverageNames();
        if ( coverageNames == null || coverageNames.isEmpty() )
            throw new IllegalArgumentException( "GetCoverage request does not contain a coverage name!" );
        String coverageName = coverageNames.get( 0 );
        if ( coverageName == null )
            throw new IllegalArgumentException( "coverage is null!" );
        return coverageName;
    }

}

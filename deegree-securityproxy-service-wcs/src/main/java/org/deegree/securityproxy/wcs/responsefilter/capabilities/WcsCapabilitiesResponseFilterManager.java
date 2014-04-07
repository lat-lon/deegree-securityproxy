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
package org.deegree.securityproxy.wcs.responsefilter.capabilities;

import static org.deegree.securityproxy.wcs.request.WcsRequestParser.GETCAPABILITIES;
import static org.deegree.securityproxy.wcs.responsefilter.ResponseFilterUtils.copyBufferedStream;
import static org.deegree.securityproxy.wcs.responsefilter.ResponseFilterUtils.isException;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Logger;
import org.deegree.securityproxy.filter.StatusCodeResponseBodyWrapper;
import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.responsefilter.ResponseFilterException;
import org.deegree.securityproxy.responsefilter.ResponseFilterManager;
import org.deegree.securityproxy.responsefilter.logging.ResponseCapabilitiesReport;
import org.deegree.securityproxy.responsefilter.logging.ResponseClippingReport;
import org.deegree.securityproxy.responsefilter.logging.ResponseFilterReport;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.CapabilitiesFilter;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.DecisionMaker;
import org.deegree.securityproxy.wcs.request.WcsRequest;
import org.springframework.security.core.Authentication;

/**
 * {@link ResponseFilterManager} filtering capabilities documents by user permissions.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="mailto:goltz@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * 
 * @version $Revision: $, $Date: $
 */
public class WcsCapabilitiesResponseFilterManager implements ResponseFilterManager {

    private static final Logger LOG = Logger.getLogger( WcsCapabilitiesResponseFilterManager.class );

    static final String SUCCESSFUL_FILTERING_MESSAGE = "Capabilities of request were filtered successfully.";

    static final String FILTERING_NOT_REQUIRED_MESSAGE = "Capabilities of request must not be filtered.";

    static final String NOT_A_CAPABILITIES_REQUEST_MSG = "Request was not a GetCapabilities-Request - filtering not required.";

    static final String SERVICE_EXCEPTION_MSG = "Response is a ServiceException.";

    private final CapabilitiesFilter capabilitiesFilter;

    public WcsCapabilitiesResponseFilterManager( CapabilitiesFilter capabilitiesFilter ) {
        this.capabilitiesFilter = capabilitiesFilter;
    }

    @Override
    public ResponseFilterReport filterResponse( StatusCodeResponseBodyWrapper servletResponse, OwsRequest request,
                                                Authentication auth )
                            throws IllegalArgumentException, ResponseFilterException {
        checkParameters( servletResponse, request );
        if ( canBeFiltered( request ) ) {
            LOG.info( "Apply wcs capabilities filter for response of request " + request );
            try {
                if ( isException( servletResponse ) ) {
                    LOG.debug( "Response contains an exception!" );
                    copyBufferedStream( servletResponse );
                    return new ResponseClippingReport( SERVICE_EXCEPTION_MSG );
                }
                Wcs100DecisionMakerCreator decisionMakerCreator = new Wcs100DecisionMakerCreator();
                DecisionMaker decisionMaker = decisionMakerCreator.createDecisionMakerForWcs100( auth );
                if ( decisionMaker == null ) {
                    copyBufferedStream( servletResponse );
                    return new ResponseCapabilitiesReport( FILTERING_NOT_REQUIRED_MESSAGE );
                } else {
                    capabilitiesFilter.filterCapabilities( servletResponse, decisionMaker );
                    return new ResponseCapabilitiesReport( SUCCESSFUL_FILTERING_MESSAGE, true );
                }
            } catch ( IOException e ) {
                throw new ResponseFilterException( e );
            } catch ( XMLStreamException e ) {
                throw new ResponseFilterException( e );
            }
        }
        copyBufferedStream( servletResponse );
        return new ResponseCapabilitiesReport( NOT_A_CAPABILITIES_REQUEST_MSG );
    }

    @Override
    public boolean canBeFiltered( OwsRequest request ) {
        checkIfRequestEqualsNull( request );
        return WcsRequest.class.equals( request.getClass() ) && isGetCapabilitiesRequest( (WcsRequest) request );
    }

    private void checkIfRequestEqualsNull( OwsRequest request ) {
        if ( request == null )
            throw new IllegalArgumentException( "Request must not be null!" );
    }

    private boolean isGetCapabilitiesRequest( WcsRequest wcsRequest ) {
        return GETCAPABILITIES.equals( wcsRequest.getOperationType() );
    }

    private void checkParameters( HttpServletResponse servletResponse, OwsRequest request ) {
        if ( servletResponse == null )
            throw new IllegalArgumentException( "Parameter servletResponse may not be null!" );
        if ( request == null )
            throw new IllegalArgumentException( "Parameter request may not be null!" );
        if ( !WcsRequest.class.equals( request.getClass() ) )
            throw new IllegalArgumentException( "OwsRequest of class " + request.getClass().getCanonicalName()
                                                + " is not supported!" );
    }

}

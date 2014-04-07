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
package org.deegree.securityproxy.service.commons.responsefilter.capabilities;

import static org.deegree.securityproxy.service.commons.responsefilter.ResponseFilterUtils.copyBufferedStream;
import static org.deegree.securityproxy.service.commons.responsefilter.ResponseFilterUtils.isException;

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
import org.springframework.security.core.Authentication;

/**
 * Abstract {@link ResponseFilterManager} using a {@link DecisionMakerCreator} to create the {@link DecisionMaker}s
 * containing the rules for filtering.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public abstract class AbstractCapabilitiesResponseFilterManager implements ResponseFilterManager {

    private static final Logger LOG = Logger.getLogger( AbstractCapabilitiesResponseFilterManager.class );

    public static final String SUCCESSFUL_FILTERING_MESSAGE = "Capabilities of request were filtered successfully.";

    public static final String FILTERING_NOT_REQUIRED_MESSAGE = "Capabilities of request must not be filtered.";

    public static final String NOT_A_CAPABILITIES_REQUEST_MSG = "Request was not a GetCapabilities-Request - filtering not required.";

    public static final String SERVICE_EXCEPTION_MSG = "Response is a ServiceException.";

    private final CapabilitiesFilter capabilitiesFilter;

    private final DecisionMakerCreator decisionMakerCreator;

    public AbstractCapabilitiesResponseFilterManager( CapabilitiesFilter capabilitiesFilter,
                                                      DecisionMakerCreator decisionMakerCreator ) {
        this.capabilitiesFilter = capabilitiesFilter;
        this.decisionMakerCreator = decisionMakerCreator;
    }

    @Override
    public ResponseFilterReport filterResponse( StatusCodeResponseBodyWrapper servletResponse, OwsRequest owsRequest,
                                                Authentication auth )
                            throws IllegalArgumentException, ResponseFilterException {
        checkParameters( servletResponse, owsRequest );
        if ( canBeFiltered( owsRequest ) ) {
            LOG.info( "Apply wcs capabilities filter for response of request " + owsRequest );
            try {
                if ( isException( servletResponse ) ) {
                    LOG.debug( "Response contains an exception!" );
                    copyBufferedStream( servletResponse );
                    return new ResponseClippingReport( SERVICE_EXCEPTION_MSG );
                }
                DecisionMaker decisionMaker = decisionMakerCreator.createDecisionMaker( owsRequest, auth );
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
    public boolean canBeFiltered( OwsRequest owsRequest )
                            throws IllegalArgumentException {
        checkIfRequestIsNull( owsRequest );
        return isCorrectRequestType( owsRequest ) && isGetCapabilitiesRequest( owsRequest );
    }

    /**
     * @param owsRequest
     *            never <code>null</code>
     * @return true if request is from the supported type
     */
    protected abstract boolean isCorrectRequestType( OwsRequest owsRequest );

    /**
     * @param owsRequest
     *            never <code>null</code>
     * @return true if request is a capabilities request
     */
    protected abstract boolean isGetCapabilitiesRequest( OwsRequest owsRequest );

    private void checkIfRequestIsNull( OwsRequest request ) {
        if ( request == null )
            throw new IllegalArgumentException( "Request must not be null!" );
    }

    private void checkParameters( HttpServletResponse servletResponse, OwsRequest request ) {
        if ( servletResponse == null )
            throw new IllegalArgumentException( "Parameter servletResponse may not be null!" );
        if ( request == null )
            throw new IllegalArgumentException( "Parameter request may not be null!" );
        if ( !isCorrectRequestType( request ) )
            throw new IllegalArgumentException( "OwsRequest of class " + request.getClass().getCanonicalName()
                                                + " is not supported!" );

    }

}
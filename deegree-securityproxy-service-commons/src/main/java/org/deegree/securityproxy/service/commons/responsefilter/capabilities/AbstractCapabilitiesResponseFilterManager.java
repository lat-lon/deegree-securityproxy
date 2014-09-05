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

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.deegree.securityproxy.filter.StatusCodeResponseBodyWrapper;
import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.responsefilter.ResponseFilterException;
import org.deegree.securityproxy.responsefilter.ResponseFilterManager;
import org.deegree.securityproxy.responsefilter.logging.DefaultResponseFilterReport;
import org.deegree.securityproxy.responsefilter.logging.ResponseFilterReport;
import org.deegree.securityproxy.service.commons.responsefilter.AbstractResponseFilterManager;
import org.springframework.security.core.Authentication;

/**
 * Abstract {@link ResponseFilterManager} using a {@link XmlModificationManagerCreator} to create the
 * {@link XmlModificationManager}s containing the rules for filtering.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public abstract class AbstractCapabilitiesResponseFilterManager extends AbstractResponseFilterManager {

    public static final String SUCCESSFUL_FILTERING_MESSAGE = "Capabilities of request were filtered successfully.";

    public static final String FILTERING_NOT_REQUIRED_MESSAGE = "Capabilities of request must not be filtered.";

    private final XmlFilter capabilitiesFilter;

    private final XmlModificationManagerCreator xmlModificationManagerCreator;

    /**
     * @param capabilitiesFilter
     *            never <code>null</code>
     * @param xmlModificationManagerCreator
     *            never <code>null</code>
     */
    public AbstractCapabilitiesResponseFilterManager( XmlFilter capabilitiesFilter,
                                                      XmlModificationManagerCreator xmlModificationManagerCreator ) {
        this.capabilitiesFilter = capabilitiesFilter;
        this.xmlModificationManagerCreator = xmlModificationManagerCreator;
    }

    @Override
    protected ResponseFilterReport applyFilter( StatusCodeResponseBodyWrapper servletResponse, OwsRequest owsRequest,
                                                Authentication auth )
                    throws ResponseFilterException, IOException {
        XmlModificationManager xmlModificationManager = xmlModificationManagerCreator.createXmlModificationManager( owsRequest,
                                                                                                                    auth );
        if ( xmlModificationManager.isModificationRequired() ) {
            try {
                capabilitiesFilter.filterXml( servletResponse, xmlModificationManager );
                return createResponseAfterModification( xmlModificationManager );
            } catch ( XMLStreamException e ) {
                throw new ResponseFilterException( e );
            }
        } else {
            copyBufferedStream( servletResponse );
            return new DefaultResponseFilterReport( FILTERING_NOT_REQUIRED_MESSAGE );
        }
    }

    @Override
    protected ResponseFilterReport handleIOException( StatusCodeResponseBodyWrapper response, IOException e )
                    throws ResponseFilterException {
        throw new ResponseFilterException( e );
    }

    /**
     * @return the xmlModificationManagerCreator, never <code>null</code>
     */
    public XmlModificationManagerCreator getXmlModificationManagerCreator() {
        return xmlModificationManagerCreator;
    }

    /**
     * @param xmlModificationManager
     *            used for modification, never <code>null</code>
     * @return a {@link DefaultResponseFilterReport}, may be overwritten by subclasses, never return <code>null</code>
     */
    protected ResponseFilterReport createResponseAfterModification( XmlModificationManager xmlModificationManager ) {
        return new DefaultResponseFilterReport( SUCCESSFUL_FILTERING_MESSAGE, true );
    }

}
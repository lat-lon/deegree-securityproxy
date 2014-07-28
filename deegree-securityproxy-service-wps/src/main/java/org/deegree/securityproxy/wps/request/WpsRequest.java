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
package org.deegree.securityproxy.wps.request;

import java.util.Collections;
import java.util.List;

import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.request.OwsServiceVersion;

/**
 * Encapsulates a WPS request.
 * 
 * @author <a href="wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author last edited by: $Author: stenger $
 * @version $Revision: $, $Date: $
 */
public class WpsRequest extends OwsRequest {

    private static final String WPS_TYPE = "wps";

    private final String serviceName;

    private final List<String> identifiers;

    /**
     * Instantiates a new {@link WpsRequest} with an empty {@link List} of identifiers.
     * 
     * @param operationType
     *            the type of the operation, never <code>null</code>
     * @param serviceVersion
     *            the version of the service, never <code>null</code>
     * @param serviceName
     *            the name of the service, never <code>null</code>
     */
    public WpsRequest( String operationType, OwsServiceVersion serviceVersion, String serviceName ) {
        this( operationType, serviceVersion, serviceName, Collections.<String> emptyList() );
    }

    /**
     * Instantiates a new {@link WpsRequest}.
     * 
     * @param operationType
     *            the type of the operation, never <code>null</code>
     * @param serviceVersion
     *            the version of the service, never <code>null</code>
     * @param serviceName
     *            the name of the service, never <code>null</code>
     * @param identifiers
     *            the identifiers of the process, never <code>null</code>
     */
    public WpsRequest( String operationType, OwsServiceVersion serviceVersion, String serviceName,
                       List<String> identifiers ) {
        super( WPS_TYPE, operationType, serviceVersion );
        this.serviceName = serviceName;
        this.identifiers = identifiers;
    }

    /**
     * @return the serviceName, never <code>null</code>
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @return the list of identifiers, never <code>null</code>
     */
    public List<String> getIdentifiers() {
        return identifiers;
    }

    @Override
    public String toString() {
        return "WpsRequest [operationType=" + getOperationType() + ", serviceVersion=" + getServiceVersion()
               + ", serviceName=" + serviceName + "]";
    }

}

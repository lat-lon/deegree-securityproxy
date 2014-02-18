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
package org.deegree.securityproxy.wcs.request;

import static java.util.Collections.singletonList;

import java.util.Collections;
import java.util.List;

import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.wcs.domain.WcsOperationType;
import org.deegree.securityproxy.wcs.domain.WcsServiceVersion;

/**
 * Encapulates a WCS request.
 * 
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
public class WcsRequest implements OwsRequest {

    private final WcsOperationType operationType;

    private final WcsServiceVersion serviceVersion;

    private final List<String> coverageNames;

    private final String serviceName;

    /**
     * Instantiates a new {@link WcsRequest} with an empty {@link List} of coverage names.
     * 
     * @param operationType
     *            the type of the operation, never <code>null</code>
     * @param serviceVersion
     *            the version of the service, never <code>null</code>
     * @param serviceName
     *            the name of the service, never <code>null</code>
     */
    public WcsRequest( WcsOperationType operationType, WcsServiceVersion serviceVersion, String serviceName ) {
        this( operationType, serviceVersion, Collections.<String> emptyList(), serviceName );
    }

    /**
     * Instantiates a new {@link WcsRequest} with an single coverage name.
     * 
     * @param operationType
     *            the type of the operation, never <code>null</code>
     * @param serviceVersion
     *            the version of the service, never <code>null</code>
     * @param coverageName
     *            the name of the coverage, never <code>null</code>
     * @param serviceName
     *            the name of the service, never <code>null</code>
     */
    public WcsRequest( WcsOperationType operationType, WcsServiceVersion serviceVersion, String coverageName,
                       String serviceName ) {
        this( operationType, serviceVersion, singletonList( coverageName ), serviceName );
    }

    /**
     * Instantiates a new {@link WcsRequest}.
     * 
     * @param operationType
     *            the type of the operation, never <code>null</code>
     * @param serviceVersion
     *            the version of the service, never <code>null</code>
     * @param coverageNames
     *            a {@link List} of coverage names, may be empty but never <code>null</code>
     * @param serviceName
     *            the name of the service, never <code>null</code>
     */
    public WcsRequest( WcsOperationType operationType, WcsServiceVersion serviceVersion, List<String> coverageNames,
                       String serviceName ) {
        this.operationType = operationType;
        this.serviceVersion = serviceVersion;
        this.coverageNames = coverageNames;
        this.serviceName = serviceName;
    }

    /**
     * @return the operationType, never <code>null</code>
     */
    public WcsOperationType getOperationType() {
        return operationType;
    }

    /**
     * @return the serviceVersion, never <code>null</code>
     */
    public WcsServiceVersion getServiceVersion() {
        return serviceVersion;
    }

    /**
     * @return the coverageNames as unmodifiable list, may be empty but never <code>null</code>
     */
    public List<String> getCoverageNames() {
        return Collections.unmodifiableList( coverageNames );
    }

    /**
     * @return the serviceName, never <code>null</code>
     */
    public String getServiceName() {
        return serviceName;
    }

    @Override
    public String toString() {
        return "WcsRequest [operationType=" + operationType + ", serviceVersion=" + serviceVersion + ", coverageNames="
               + coverageNames + ", serviceName=" + serviceName + "]";
    }

}
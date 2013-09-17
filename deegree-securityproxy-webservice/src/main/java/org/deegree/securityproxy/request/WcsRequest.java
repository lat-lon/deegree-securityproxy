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
package org.deegree.securityproxy.request;

import java.util.Collections;
import java.util.List;

import org.deegree.securityproxy.commons.WcsOperationType;
import org.deegree.securityproxy.commons.WcsServiceVersion;

/**
 * Encapulates a WCS request.
 * 
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
public class WcsRequest implements OwsRequest {

    private WcsOperationType operationType;

    private WcsServiceVersion serviceVersion;

    private List<String> coverageNames;

    public WcsRequest( WcsOperationType operationType, WcsServiceVersion serviceVersion, List<String> coverageNames ) {
        this.operationType = operationType;
        this.serviceVersion = serviceVersion;
        this.coverageNames = coverageNames;
    }

    public WcsRequest( WcsOperationType operationType, WcsServiceVersion serviceVersion, String coverageName ) {
        this.operationType = operationType;
        this.serviceVersion = serviceVersion;
        this.coverageNames = Collections.singletonList( coverageName );
    }

    public WcsRequest( WcsOperationType operationType, WcsServiceVersion serviceVersion ) {
        this.operationType = operationType;
        this.serviceVersion = serviceVersion;
        this.coverageNames = Collections.emptyList();
    }

    /**
     * @return the operationType
     */
    public WcsOperationType getOperationType() {
        return operationType;
    }

    /**
     * @return the serviceVersion
     */
    public WcsServiceVersion getServiceVersion() {
        return serviceVersion;
    }

    /**
     * @return the layerName
     */
    public List<String> getCoverageNames() {
        return Collections.unmodifiableList( coverageNames );
    }

}

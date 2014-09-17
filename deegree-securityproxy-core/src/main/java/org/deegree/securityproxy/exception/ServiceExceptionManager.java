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
package org.deegree.securityproxy.exception;

import org.deegree.securityproxy.filter.ServiceManager;

import javax.servlet.http.HttpServletRequest;

/**
 * A {@link ServiceExceptionManager} encapsulates the specific {@link ServiceExceptionWrapper} of a service..
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public interface ServiceExceptionManager {

    /**
     * @return the responsible {@link ServiceExceptionWrapper}, never <code>null</code>
     */
    ServiceExceptionWrapper retrieveServiceExceptionWrapper();

    /**
     * Check if a given request is supported by the {@link ServiceManager}.
     *
     * @param serviceType
     *            may be <code>null</code>. If <code>null</code>, request parameter is used to determine if service type
     *            is supported.
     * @param request
     *            never <code>null</code>. If serviceType parameter is <code>null</code>, this parameter is used to
     *            determine if service type is supported.
     * @return <code>true</code> if this {@link ServiceManager} can handle the requested {@link HttpServletRequest},
     *         <code>false</code> otherwise.
     */
    boolean isServiceTypeSupported( String serviceType, HttpServletRequest request );

}
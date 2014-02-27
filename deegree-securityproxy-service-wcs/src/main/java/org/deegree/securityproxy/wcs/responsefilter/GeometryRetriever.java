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
package org.deegree.securityproxy.wcs.responsefilter;

import java.util.List;

import org.deegree.securityproxy.authentication.ows.WcsGeometryFilterInfo;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;

/**
 * Contains method to retrieve the clipping geometry from a list of {@link WcsGeometryFilterInfo}s
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
interface GeometryRetriever {

    /**
     * Retrieves or calculates the parsed geometry to use from the list of {@link WcsGeometryFilterInfo}s identified by
     * the given coverage name. The algorithm to detect or calculate the geometry is left to the implementations.
     * 
     * @param coverageName
     *            the name of the coverage the geometries should be retrieved for, never <code>null</code>
     * @param geometryFilterInfos
     *            the list of {@link WcsGeometryFilterInfo}s containing the geometries, never <code>null</code> may be
     *            empty
     * @return the parsed geometry, <code>null</code> if no geometry can be found for the requested coverage name
     * @throws IllegalArgumentException
     *             if one of the parameters <code>null</code>
     * @throws ParseException
     *             if the geometry could not be parsed or calculated
     */
    Geometry retrieveGeometry( String coverageName, List<WcsGeometryFilterInfo> geometryFilterInfos )
                            throws IllegalArgumentException, ParseException;

}
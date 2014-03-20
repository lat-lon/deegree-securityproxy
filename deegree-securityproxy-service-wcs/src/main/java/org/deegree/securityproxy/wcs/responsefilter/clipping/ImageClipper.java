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

import java.io.InputStream;
import java.io.OutputStream;

import org.deegree.securityproxy.responsefilter.logging.ResponseClippingReport;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Contains method to clip images.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public interface ImageClipper {

    /**
     * Clips the passed image as defined in the clipping area.
     * 
     * @param coverageToClip
     *            contains the coverage to clip - must contain an image! never <code>null</code>
     * @param visibleArea
     *            the geometry covering the area visible for the user, if <code>null</code> no clipping required
     * @param destination
     *            {@link OutputStream} to write the image, never <code>null</code>
     * @throws IllegalArgumentException
     *             if one one the parameter is <code>null</code>
     * @throws ClippingException
     *             if an error occurred during clipping
     * @return a {@link ResponseClippingReport} containing the information if clipping was required and the visible
     *         geometry
     */
    ResponseClippingReport calculateClippedImage( InputStream coverageToClip, Geometry visibleArea,
                                                  OutputStream destination )
                            throws IllegalArgumentException, ClippingException;

}
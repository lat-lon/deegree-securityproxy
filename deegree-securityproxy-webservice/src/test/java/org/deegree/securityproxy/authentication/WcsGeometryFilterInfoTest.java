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
package org.deegree.securityproxy.authentication;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class WcsGeometryFilterInfoTest {

    private String COVERAGE_NAME = "coverageName";

    private String GEOMETRY = "geometryAsString";

    @Test
    public void testConstructorWcsGeometryFilterInfo()
                            throws Exception {
        WcsGeometryFilterInfo wcsGeometryFilterInfo = new WcsGeometryFilterInfo( COVERAGE_NAME, GEOMETRY );
        assertThat( wcsGeometryFilterInfo.getCoverageName(), is( COVERAGE_NAME ) );
        assertThat( wcsGeometryFilterInfo.getGeometryString(), is( GEOMETRY ) );
    }

    @Test
    public void testConstructorWcsGeometryFilterInfoWithNullGeometry()
                            throws Exception {
        WcsGeometryFilterInfo wcsGeometryFilterInfo = new WcsGeometryFilterInfo( COVERAGE_NAME, null );
        assertThat( wcsGeometryFilterInfo.getCoverageName(), is( COVERAGE_NAME ) );
        assertThat( wcsGeometryFilterInfo.getGeometryString(), is( nullValue() ) );
    }

    @Test
    public void testConstructorWcsGeometryFilterInfoWithoutGeometry()
                            throws Exception {
        WcsGeometryFilterInfo wcsGeometryFilterInfo = new WcsGeometryFilterInfo( COVERAGE_NAME );
        assertThat( wcsGeometryFilterInfo.getCoverageName(), is( COVERAGE_NAME ) );
        assertThat( wcsGeometryFilterInfo.getGeometryString(), is( nullValue() ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWcsGeometryFilterInfoWithNullCoverageNameShouldFail()
                            throws Exception {
        new WcsGeometryFilterInfo( null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWcsGeometryFilterInfoWithNullCoverageNameAndGeoemtryShouldFail()
                            throws Exception {
        new WcsGeometryFilterInfo( null, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWcsGeometryFilterInfoWithEmptyCoverageNameShouldFail()
                            throws Exception {
        new WcsGeometryFilterInfo( "" );
    }

}
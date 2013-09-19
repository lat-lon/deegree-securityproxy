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
package org.deegree.securityproxy.responsefilter.wcs;

import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.deegree.securityproxy.authentication.WcsGeometryFilterInfo;
import org.junit.Test;
import org.opengis.geometry.Geometry;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class GeometryRetrieverImplTest {

    private static final String COVERAGE_NAME = "coverageName";

    private static final String GEOMETRY = "SRID=4326;MULTIPOLYGON(((-89.739 20.864,-89.758 20.876,-89.765 20.894,-89.748 20.897,-89.73 20.91,-89.708 20.928,-89.704 20.948,-89.716 20.964,-89.729 20.99,-89.73 21.017,-89.712 21.021,-89.685 21.031,-89.667 21.025,-89.641 21.017,-89.62 21.019,-89.599 21.018,-89.575 20.995,-89.568 20.97,-89.562 20.934,-89.562 20.91,-89.577 20.89,-89.609 20.878,-89.636 20.877,-89.664 20.881,-89.683 20.904,-89.683 20.917,-89.664 20.941,-89.662 20.954,-89.674 20.965,-89.687 20.983,-89.705 20.989,-89.703 20.974,-89.696 20.961,-89.686 20.949,-89.683 20.935,-89.694 20.919,-89.705 20.901,-89.722 20.875,-89.727 20.869,-89.739 20.864),(-89.627 20.985,-89.603 20.962,-89.62 20.936,-89.634 20.943,-89.639 20.961,-89.649 20.975,-89.627 20.985)))";

    private GeometryRetrieverImpl geometryRetriever = new GeometryRetrieverImpl();

    @Test
    public void testRetrieveGeometryFromEmptyListShouldReturnNull()
                            throws Exception {
        Geometry retrievedGeometry = geometryRetriever.retrieveGeometry( COVERAGE_NAME,
                                                                         createEmptyWcsGeometryFilterInfoList() );
        assertThat( retrievedGeometry, is( nullValue() ) );
    }

    @Test
    public void testRetrieveGeometryWithCoverageWithoutFilterShouldReturnNull()
                            throws Exception {
        Geometry retrievedGeometry = geometryRetriever.retrieveGeometry( "coverageWithoutFilter",
                                                                         createWcsGeometryFilterInfoList() );
        assertThat( retrievedGeometry, is( nullValue() ) );
    }

    @Test
    public void testRetrieveGeometryWithCoverageWithoutGeometryShouldReturnNull()
                            throws Exception {
        Geometry retrievedGeometry = geometryRetriever.retrieveGeometry( COVERAGE_NAME,
                                                                         createWcsGeometryFilterInfoListWithoutGeometry() );
        assertThat( retrievedGeometry, is( nullValue() ) );
    }

    @Test
    public void testRetrieveGeometryWithCoverageWithOneGeometryShouldReturnParsedGeometry()
                            throws Exception {
        Geometry retrievedGeometry = geometryRetriever.retrieveGeometry( COVERAGE_NAME,
                                                                         createWcsGeometryFilterInfoList() );
        assertThat( retrievedGeometry, is( notNullValue() ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRetrieveGeometryWithNullCoverageNameChouldFail()
                            throws Exception {
        geometryRetriever.retrieveGeometry( null, createWcsGeometryFilterInfoList() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRetrieveGeometryWithNullGeoemtryFilterListShouldFail()
                            throws Exception {
        geometryRetriever.retrieveGeometry( COVERAGE_NAME, null );
    }

    private List<WcsGeometryFilterInfo> createEmptyWcsGeometryFilterInfoList() {
        return emptyList();
    }

    private List<WcsGeometryFilterInfo> createWcsGeometryFilterInfoListWithoutGeometry() {
        List<WcsGeometryFilterInfo> wcsGeometryFilterInfos = new ArrayList<WcsGeometryFilterInfo>();
        wcsGeometryFilterInfos.add( new WcsGeometryFilterInfo( COVERAGE_NAME ) );
        return wcsGeometryFilterInfos;
    }

    private List<WcsGeometryFilterInfo> createWcsGeometryFilterInfoList() {
        List<WcsGeometryFilterInfo> wcsGeometryFilterInfos = new ArrayList<WcsGeometryFilterInfo>();
        wcsGeometryFilterInfos.add( new WcsGeometryFilterInfo( COVERAGE_NAME, GEOMETRY ) );
        return wcsGeometryFilterInfos;
    }

}
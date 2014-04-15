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
package org.deegree.securityproxy.service.commons.responsefilter.clipping.geometry;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.deegree.securityproxy.authentication.ows.raster.GeometryFilterInfo;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class GeometryRetrieverImplTest {

    private static final String COVERAGE_NAME_SIMPLE_1 = "simpleCoverageName1";

    private static final String COVERAGE_NAME_SIMPLE_2 = "simpleCoverageName2";

    private static final String COVERAGE_NAME = "coverageName";

    private static final String GEOMETRY_SIMPLE_1 = "SRID=4326;POLYGON ((30 10, 10 20, 20 40, 40 40, 30 10))";

    private static final String GEOMETRY_SIMPLE_2 = "SRID=4326;POLYGON ((30 10, 10 20, 20 60, 60 60, 30 10))";

    private static final String GEOMETRY = "SRID=4326;MULTIPOLYGON(((-89.739 20.864,-89.758 20.876,-89.765 20.894,-89.748 20.897,-89.73 20.91,-89.708 20.928,-89.704 20.948,-89.716 20.964,-89.729 20.99,-89.73 21.017,-89.712 21.021,-89.685 21.031,-89.667 21.025,-89.641 21.017,-89.62 21.019,-89.599 21.018,-89.575 20.995,-89.568 20.97,-89.562 20.934,-89.562 20.91,-89.577 20.89,-89.609 20.878,-89.636 20.877,-89.664 20.881,-89.683 20.904,-89.683 20.917,-89.664 20.941,-89.662 20.954,-89.674 20.965,-89.687 20.983,-89.705 20.989,-89.703 20.974,-89.696 20.961,-89.686 20.949,-89.683 20.935,-89.694 20.919,-89.705 20.901,-89.722 20.875,-89.727 20.869,-89.739 20.864),(-89.627 20.985,-89.603 20.962,-89.62 20.936,-89.634 20.943,-89.639 20.961,-89.649 20.975,-89.627 20.985)))";

    private final GeometryRetrieverImpl geometryRetriever = new GeometryRetrieverImpl();

    @Test
    public void testRetrieveGeometryFromEmptyListShouldReturnNull()
                            throws Exception {
        Geometry retrievedGeometry = geometryRetriever.retrieveGeometry( asList( COVERAGE_NAME ),
                                                                         createEmptyWcsGeometryFilterInfoList() );
        assertThat( retrievedGeometry, is( nullValue() ) );
    }

    @Test
    public void testRetrieveGeometryWithCoverageWithoutFilterShouldReturnNull()
                            throws Exception {
        Geometry retrievedGeometry = geometryRetriever.retrieveGeometry( asList( "coverageWithoutFilter" ),
                                                                         createWcsGeometryFilterInfoList() );
        assertThat( retrievedGeometry, is( nullValue() ) );
    }

    @Test
    public void testRetrieveGeometryWithCoverageWithoutGeometryShouldReturnNull()
                            throws Exception {
        Geometry retrievedGeometry = geometryRetriever.retrieveGeometry( asList( COVERAGE_NAME ),
                                                                         createWcsGeometryFilterInfoListWithoutGeometry() );
        assertThat( retrievedGeometry, is( nullValue() ) );
    }

    @Test
    public void testRetrieveGeometryWithCoverageWithOneGeometryShouldReturnNotNull()
                            throws Exception {
        Geometry retrievedGeometry = geometryRetriever.retrieveGeometry( asList( COVERAGE_NAME ),
                                                                         createWcsGeometryFilterInfoList() );
        assertThat( retrievedGeometry, is( notNullValue() ) );
    }

    @Test
    public void testRetrieveGeometryWithCoverageWithOneGeometryShouldReturnCorrectParsedGeometry()
                            throws Exception {
        Geometry retrievedGeometry = geometryRetriever.retrieveGeometry( asList( COVERAGE_NAME_SIMPLE_1 ),
                                                                         createWcsGeometryFilterInfoListWithSimpleGeometry() );
        Geometry expectedPolygon = createExpectedGeometry();
        assertThat( retrievedGeometry, is( expectedPolygon ) );
    }

    @Test
    public void testRetrieveGeometryWithCoverageFromMultipleGeometriesShouldReturnCorrectParsedGeometry()
                            throws Exception {
        Geometry retrievedGeometry = geometryRetriever.retrieveGeometry( asList( COVERAGE_NAME_SIMPLE_1 ),
                                                                         createWcsGeometryFilterInfoListWithMultipleGeometries() );
        Geometry expectedPolygon = createExpectedGeometry();
        assertThat( retrievedGeometry, is( expectedPolygon ) );
    }

    @Test
    public void testRetrieveGeometryWithCoverageFromMultipleLayerNamesShouldReturnUnion()
                            throws Exception {
        Geometry retrievedGeometry = geometryRetriever.retrieveGeometry( asList( COVERAGE_NAME_SIMPLE_1,
                                                                                 COVERAGE_NAME_SIMPLE_2 ),
                                                                         createWcsGeometryFilterInfoListWithSimpleGeometry() );
        Geometry expectedPolygon = createExpectedGeometryUnion();
        assertThat( retrievedGeometry, is( expectedPolygon ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRetrieveGeometryWithNullCoverageNameShouldFail()
                            throws Exception {
        geometryRetriever.retrieveGeometry( null, createWcsGeometryFilterInfoList() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRetrieveGeometryWithEmptyLayerNamesListShouldFail()
                            throws Exception {
        geometryRetriever.retrieveGeometry( Collections.<String> emptyList(), createWcsGeometryFilterInfoList() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRetrieveGeometryWithNullGeoemtryFilterListShouldFail()
                            throws Exception {
        geometryRetriever.retrieveGeometry( asList( COVERAGE_NAME ), null );
    }

    /*
     * #normaliseWkt()
     */

    @Test
    public void testNormaliseWkt()
                            throws Exception {
        String normalisedWkt = geometryRetriever.normaliseWkt( GEOMETRY );
        assertThat( normalisedWkt, startsWith( "MULTIPOLYGON" ) );
    }

    @Test
    public void testNormaliseWktWithoutSrid()
                            throws Exception {
        String withoutSrid = "POLYGON ((30 10, 10 20, 20 40, 40 40, 30 10))";
        String normalisedWkt = geometryRetriever.normaliseWkt( withoutSrid );
        assertThat( normalisedWkt, startsWith( "POLYGON" ) );
    }

    private List<GeometryFilterInfo> createEmptyWcsGeometryFilterInfoList() {
        return emptyList();
    }

    private List<GeometryFilterInfo> createWcsGeometryFilterInfoListWithoutGeometry() {
        List<GeometryFilterInfo> wcsGeometryFilterInfos = new ArrayList<GeometryFilterInfo>();
        wcsGeometryFilterInfos.add( new GeometryFilterInfo( COVERAGE_NAME ) );
        return wcsGeometryFilterInfos;
    }

    private List<GeometryFilterInfo> createWcsGeometryFilterInfoList() {
        List<GeometryFilterInfo> wcsGeometryFilterInfos = new ArrayList<GeometryFilterInfo>();
        wcsGeometryFilterInfos.add( new GeometryFilterInfo( COVERAGE_NAME, GEOMETRY ) );
        return wcsGeometryFilterInfos;
    }

    private List<GeometryFilterInfo> createWcsGeometryFilterInfoListWithSimpleGeometry() {
        List<GeometryFilterInfo> wcsGeometryFilterInfos = new ArrayList<GeometryFilterInfo>();
        wcsGeometryFilterInfos.add( new GeometryFilterInfo( COVERAGE_NAME_SIMPLE_1, GEOMETRY_SIMPLE_1 ) );
        wcsGeometryFilterInfos.add( new GeometryFilterInfo( COVERAGE_NAME_SIMPLE_2, GEOMETRY_SIMPLE_2 ) );
        return wcsGeometryFilterInfos;
    }

    private List<GeometryFilterInfo> createWcsGeometryFilterInfoListWithMultipleGeometries() {
        List<GeometryFilterInfo> wcsGeometryFilterInfos = new ArrayList<GeometryFilterInfo>();
        wcsGeometryFilterInfos.add( new GeometryFilterInfo( COVERAGE_NAME_SIMPLE_1, GEOMETRY_SIMPLE_1 ) );
        wcsGeometryFilterInfos.add( new GeometryFilterInfo( COVERAGE_NAME, GEOMETRY ) );
        wcsGeometryFilterInfos.add( new GeometryFilterInfo( COVERAGE_NAME_SIMPLE_1, GEOMETRY_SIMPLE_1 ) );
        return wcsGeometryFilterInfos;
    }

    private Geometry createExpectedGeometry() {
        // POLYGON ((30 10, 10 20, 20 40, 40 40, 30 10))
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate[] coordinates = new Coordinate[] { new Coordinate( 30, 10 ), new Coordinate( 10, 20 ),
                                                     new Coordinate( 20, 40 ), new Coordinate( 40, 40 ),
                                                     new Coordinate( 30, 10 ) };
        return geometryFactory.createPolygon( coordinates );
    }

    private Geometry createExpectedGeometryUnion() {
        // POLYGON ((30 10, 10 20, 20 40, 40 40, 30 10))
        // POLYGON ((30 10, 10 20, 20 60, 60 60, 30 10))
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate[] coordinates = new Coordinate[] { new Coordinate( 30, 10 ), new Coordinate( 10, 20 ),
                                                     new Coordinate( 20, 60 ), new Coordinate( 60, 60 ),
                                                     new Coordinate( 30, 10 ) };
        return geometryFactory.createPolygon( coordinates );
    }

    private Matcher<String> startsWith( final String startString ) {
        return new BaseMatcher<String>() {

            @Override
            public boolean matches( Object item ) {
                String valueToCheck = (String) item;
                return valueToCheck.startsWith( startString );
            }

            @Override
            public void describeTo( Description description ) {
                description.appendText( "Item should start with " + startString );
            }
        };
    }

}
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
package org.deegree.securityproxy.responsefilter.logging;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class ResponseClippingReportTest {

    private static final boolean IS_FILTERED = true;

    private static final boolean IS_NOT_FILTERED = false;

    @Test
    public void testGetMessageWithFailure()
                            throws Exception {
        String failure = "An error occurred";
        ResponseClippingReport clippingReport = new ResponseClippingReport( failure );

        assertThat( clippingReport.getFailure(), is( failure ) );
        assertThat( clippingReport.getReturnedVisibleArea(), is( nullValue() ) );
        assertThat( clippingReport.isFiltered(), is( IS_NOT_FILTERED ) );
        assertThat( clippingReport.getFailure(), contains( failure ) );
    }

    @Test
    public void testGetMessageWithGeometryAndClipped()
                            throws Exception {
        Geometry geometry = createGeometry();
        ResponseClippingReport clippingReport = new ResponseClippingReport( geometry, IS_FILTERED );

        assertThat( clippingReport.getReturnedVisibleArea(), is( geometry ) );
        assertThat( clippingReport.isFiltered(), is( IS_FILTERED ) );
    }

    @Test
    public void testGetMessageWithGeometryAndNotClipped()
                            throws Exception {
        Geometry geometry = createGeometry();
        ResponseClippingReport clippingReport = new ResponseClippingReport( geometry, IS_NOT_FILTERED );

        assertThat( clippingReport.getReturnedVisibleArea(), is( geometry ) );
        assertThat( clippingReport.isFiltered(), is( IS_NOT_FILTERED ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullGeometryShouldFail()
                            throws Exception {
        new ResponseClippingReport( null, true );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithFailureShouldFail()
                            throws Exception {
        new ResponseClippingReport( null );
    }

    private Geometry createGeometry() {
        Envelope smallEnvelope = new Envelope( 5, 5.1, 48.57, 48.93 );
        return new GeometryFactory().toGeometry( smallEnvelope );
    }

    private Matcher<String> contains( final String toCheck ) {
        return new BaseMatcher<String>() {

            @Override
            public boolean matches( Object item ) {
                return ( (String) item ).contains( toCheck );
            }

            @Override
            public void describeTo( Description description ) {
                description.appendText( "Should contain " + toCheck );

            }
        };
    }

}
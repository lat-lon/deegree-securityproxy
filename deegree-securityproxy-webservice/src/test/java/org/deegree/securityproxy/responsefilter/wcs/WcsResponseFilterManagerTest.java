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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import javax.servlet.http.HttpServletResponse;

import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.request.WcsRequest;
import org.junit.Test;
import org.springframework.security.core.Authentication;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class WcsResponseFilterManagerTest {

    private WcsResponseFilterManager wcsResponseFilterManager = new WcsResponseFilterManager();

    @Test
    public void testSupportsShouldSupportWcsRequests()
                            throws Exception {
        boolean isSupported = wcsResponseFilterManager.supports( WcsRequest.class );
        assertThat( isSupported, is( true ) );
    }

    @Test
    public void testSupportsShouldNotSupportOwsRequests()
                            throws Exception {
        boolean isSupported = wcsResponseFilterManager.supports( OwsRequest.class );
        assertThat( isSupported, is( false ) );
    }

    @Test
    public void testSupportsShouldNotSupportNull()
                            throws Exception {
        boolean isSupported = wcsResponseFilterManager.supports( null );
        assertThat( isSupported, is( false ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFilterResponseWithNullResponseShouldFail()
                            throws Exception {
        wcsResponseFilterManager.filterResponse( null, mockWcsRequest(), mockAuthentication() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFilterResponseWithNullRequestShouldFail()
                            throws Exception {
        wcsResponseFilterManager.filterResponse( mockServletResponse(), null, mockAuthentication() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFilterResponseWithUnsupportedRequestShouldFail()
                            throws Exception {
        wcsResponseFilterManager.filterResponse( mockServletResponse(), mockOwsRequest(), mockAuthentication() );
    }

    private OwsRequest mockOwsRequest() {
        return mock( OwsRequest.class );
    }

    private Authentication mockAuthentication() {
        return mock( Authentication.class );
    }

    private WcsRequest mockWcsRequest() {
        return mock( WcsRequest.class );
    }

    private HttpServletResponse mockServletResponse() {
        return mock( HttpServletResponse.class );
    }

}
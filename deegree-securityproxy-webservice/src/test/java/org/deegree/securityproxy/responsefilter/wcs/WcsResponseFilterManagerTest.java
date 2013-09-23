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

import static org.deegree.securityproxy.commons.WcsOperationType.GETCAPABILITIES;
import static org.deegree.securityproxy.commons.WcsOperationType.GETCOVERAGE;
import static org.deegree.securityproxy.commons.WcsServiceVersion.VERSION_110;
import static org.deegree.securityproxy.responsefilter.wcs.WcsResponseFilterManager.NOT_A_COVERAGE_REQUEST_MSG;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletOutputStream;

import org.deegree.securityproxy.authentication.WcsGeometryFilterInfo;
import org.deegree.securityproxy.authentication.WcsUser;
import org.deegree.securityproxy.authentication.wcs.WcsPermission;
import org.deegree.securityproxy.filter.StatusCodeResponseBodyWrapper;
import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.request.WcsRequest;
import org.deegree.securityproxy.responsefilter.logging.ResponseClippingReport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.vividsolutions.jts.geom.Geometry;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:org/deegree/securityproxy/responsefilter/wcs/WcsResponseFilterManagerTestContext.xml" })
public class WcsResponseFilterManagerTest {

    private static final String MOCKED_REPORT = "mockedReport";

    private static final String COVERAGE_NAME = "coverageName";

    private static final String GEOMETRY_SIMPLE = "SRID=4326;POLYGON ((30 10, 10 20, 20 40, 40 40, 30 10))";

    @Autowired
    private WcsResponseFilterManager wcsResponseFilterManager;

    @Autowired
    private GeometryRetriever geometryRetriever;

    @Autowired
    private ImageClipper imageClipper;

    @Before
    public void resetMocks() {
        reset( imageClipper );
        when(
              imageClipper.calculateClippedImage( (InputStream) anyObject(), (Geometry) anyObject(),
                                                  (OutputStream) anyObject() ) ).thenReturn( new ResponseClippingReport(
                                                                                                                         MOCKED_REPORT ) );
    }

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

    /*
     * #filterResponse()
     */

    @Test
    public void testFilterResponseWithCapabilitiesResponseShouldDoNothing()
                            throws Exception {
        StatusCodeResponseBodyWrapper mockedServletResponse = mockResponseWrapper();
        Authentication mockAuthentication = mockAuthentication();
        wcsResponseFilterManager.filterResponse( mockedServletResponse, createWcsGetCapabilitiesRequest(),
                                                 mockAuthentication );
        verifyZeroInteractions( mockedServletResponse );
    }

    @Test
    public void testFilterResponseWithCapabilitiesResponseShouldReturnCorrectReport()
                            throws Exception {
        StatusCodeResponseBodyWrapper mockedServletResponse = mockResponseWrapper();
        Authentication mockAuthentication = mockAuthentication();
        ResponseClippingReport filterResponse = wcsResponseFilterManager.filterResponse( mockedServletResponse,
                                                                                         createWcsGetCapabilitiesRequest(),
                                                                                         mockAuthentication );
        assertThat( filterResponse.isFiltered(), is( false ) );
        assertThat( filterResponse.getFailure(), is( NOT_A_COVERAGE_REQUEST_MSG ) );
        assertThat( filterResponse.getReturnedVisibleArea(), is( nullValue() ) );
    }

    @Test
    public void testFilterResponseWithCoverageResponseShouldReturnCorrectReport()
                            throws Exception {
        StatusCodeResponseBodyWrapper mockedServletResponse = mockResponseWrapper();
        Authentication mockAuthentication = mockAuthentication();
        ResponseClippingReport filterResponse = wcsResponseFilterManager.filterResponse( mockedServletResponse,
                                                                                         createWcsGetCoverageRequest(),
                                                                                         mockAuthentication );
        assertThat( filterResponse.getFailure(), is( MOCKED_REPORT ) );
    }

    /*
     * #filterResponse() - Exceptions
     */

    @Test(expected = IllegalArgumentException.class)
    public void testFilterResponseWithNullResponseShouldFail()
                            throws Exception {
        wcsResponseFilterManager.filterResponse( null, createWcsGetCoverageRequest(), mockAuthentication() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFilterResponseWithNullRequestShouldFail()
                            throws Exception {
        wcsResponseFilterManager.filterResponse( mockResponseWrapper(), null, mockAuthentication() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFilterResponseWithUnsupportedRequestShouldFail()
                            throws Exception {
        wcsResponseFilterManager.filterResponse( mockResponseWrapper(), mockOwsRequest(), mockAuthentication() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFilterResponseWithAuthenticationWithoutWcsUserShouldFail()
                            throws Exception {
        wcsResponseFilterManager.filterResponse( mockResponseWrapper(), createWcsGetCoverageRequest(),
                                                 mockAuthenticationWithoutWcsUserPrincipal() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFilterResponseWithWcsRequestWithoutCoverageShouldFail()
                            throws Exception {
        wcsResponseFilterManager.filterResponse( mockResponseWrapper(),
                                                 createWcsGetCoverageRequestWithoutCoverageName(), mockAuthentication() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFilterResponseWithWcsRequestWithNullCoverageShouldFail()
                            throws Exception {
        wcsResponseFilterManager.filterResponse( mockResponseWrapper(),
                                                 createWcsGetCoverageRequestWithNullCoverageName(),
                                                 mockAuthentication() );
    }

    private OwsRequest mockOwsRequest() {
        return mock( OwsRequest.class );
    }

    private WcsRequest createWcsGetCoverageRequest() {
        return new WcsRequest( GETCOVERAGE, VERSION_110, Collections.singletonList( COVERAGE_NAME ) );
    }

    private WcsRequest createWcsGetCoverageRequestWithoutCoverageName() {
        return new WcsRequest( GETCOVERAGE, VERSION_110, Collections.<String> emptyList() );
    }

    private WcsRequest createWcsGetCoverageRequestWithNullCoverageName() {
        return new WcsRequest( GETCOVERAGE, VERSION_110, Collections.<String> singletonList( null ) );
    }

    private WcsRequest createWcsGetCapabilitiesRequest() {
        return new WcsRequest( GETCAPABILITIES, VERSION_110 );
    }

    private StatusCodeResponseBodyWrapper mockResponseWrapper()
                            throws IOException {
        StatusCodeResponseBodyWrapper mockedServletResponse = mock( StatusCodeResponseBodyWrapper.class );
        when( mockedServletResponse.getOutputStream() ).thenReturn( mock( ServletOutputStream.class ) );
        return mockedServletResponse;
    }

    private Authentication mockAuthentication() {
        Authentication mockedAuthentication = mock( Authentication.class );
        List<WcsGeometryFilterInfo> filters = new ArrayList<WcsGeometryFilterInfo>();
        filters.add( new WcsGeometryFilterInfo( COVERAGE_NAME, GEOMETRY_SIMPLE ) );
        WcsUser wcsUser = new WcsUser( "user", "password", Collections.<WcsPermission> emptyList(), filters );
        when( mockedAuthentication.getPrincipal() ).thenReturn( wcsUser );
        return mockedAuthentication;
    }

    private Authentication mockAuthenticationWithoutWcsUserPrincipal() {
        Authentication mockedAuthentication = mock( Authentication.class );
        when( mockedAuthentication.getPrincipal() ).thenReturn( mock( UserDetails.class ) );
        return mockedAuthentication;
    }

}
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

import static org.deegree.securityproxy.service.commons.responsefilter.clipping.AbstractClippingResponseFilterManager.DEFAULT_BODY;
import static org.deegree.securityproxy.service.commons.responsefilter.clipping.AbstractClippingResponseFilterManager.DEFAULT_STATUS_CODE;
import static org.deegree.securityproxy.service.commons.responsefilter.clipping.AbstractClippingResponseFilterManager.NOT_A_CORRECT_REQUEST_MSG;
import static org.deegree.securityproxy.service.commons.responsefilter.clipping.AbstractClippingResponseFilterManager.REQUEST_AREA_HEADER_KEY;
import static org.deegree.securityproxy.service.commons.responsefilter.clipping.AbstractClippingResponseFilterManager.SERVICE_EXCEPTION_MSG;
import static org.deegree.securityproxy.wcs.request.WcsRequestParser.GETCAPABILITIES;
import static org.deegree.securityproxy.wcs.request.WcsRequestParser.GETCOVERAGE;
import static org.deegree.securityproxy.wcs.request.WcsRequestParser.VERSION_110;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletOutputStream;

import org.deegree.securityproxy.authentication.ows.raster.GeometryFilterInfo;
import org.deegree.securityproxy.authentication.ows.raster.RasterPermission;
import org.deegree.securityproxy.authentication.ows.raster.RasterUser;
import org.deegree.securityproxy.filter.StatusCodeResponseBodyWrapper;
import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.responsefilter.logging.ResponseClippingReport;
import org.deegree.securityproxy.responsefilter.logging.ResponseFilterReport;
import org.deegree.securityproxy.service.commons.responsefilter.AbstractResponseFilterManager;
import org.deegree.securityproxy.service.commons.responsefilter.clipping.ImageClipper;
import org.deegree.securityproxy.service.commons.responsefilter.clipping.exception.ClippingException;
import org.deegree.securityproxy.service.commons.responsefilter.clipping.geometry.GeometryRetriever;
import org.deegree.securityproxy.wcs.request.WcsRequest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.WKTReader;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:org/deegree/securityproxy/wcs/responsefilter/clipping/WcsResponseFilterManagerTestContext.xml" })
public class WcsClippingResponseFilterManagerTest {

    private static final String COVERAGE_NAME = "coverageName";

    private static final String COVERAGE_NAME_FAILURE = "failureCoverageName";

    private static final String COVERAGE_NAME_EMPTY = "emptyCoverageName";

    private static final String COVERAGE_NAME_NO_GEOM = "noGeomCoverageName";

    private static final String GEOMETRY_SIMPLE = "POLYGON ((30 10, 10 20, 20 40, 40 40, 30 10))";

    private static final String GEOMETRY_FAILURE = "POLYGON ((33 10, 10 20, 20 40, 40 40, 33 10))";

    private static final String GEOMETRY_EMPTY = "POINT EMPTY";

    private static final String REPORT_FAILURE = "failure";

    private static final String SERVICE_NAME = "serviceName";

    private static Geometry geometrySimple;

    private static Geometry geometryFailure;

    private static Geometry geometryEmpty;

    private static ResponseClippingReport mockReport;

    private static ResponseClippingReport mockEmptyReport;

    @Autowired
    private AbstractResponseFilterManager wcsClippingResponseFilterManager;

    @Autowired
    private GeometryRetriever geometryRetriever;

    @Autowired
    private ImageClipper imageClipper;

    @BeforeClass
    public static void initMockReport()
                            throws Exception {
        WKTReader wktReader = new WKTReader();
        geometrySimple = wktReader.read( GEOMETRY_SIMPLE );
        mockReport = new ResponseClippingReport( geometrySimple, true );

        geometryFailure = wktReader.read( GEOMETRY_FAILURE );

        geometryEmpty = new GeometryFactory().toGeometry( new Envelope() );
        mockEmptyReport = new ResponseClippingReport( geometryEmpty, true );
    }

    @Before
    public void resetMocks()
                            throws ClippingException {
        reset( imageClipper );
        when(
              imageClipper.calculateClippedImage( (InputStream) anyObject(), eq( geometrySimple ),
                                                  (OutputStream) anyObject() ) ).thenReturn( mockReport );
        ClippingException exception = mock( ClippingException.class );
        when( exception.getMessage() ).thenReturn( REPORT_FAILURE );
        when(
              imageClipper.calculateClippedImage( (InputStream) anyObject(), eq( geometryFailure ),
                                                  (OutputStream) anyObject() ) ).thenThrow( exception );
        when(
              imageClipper.calculateClippedImage( (InputStream) anyObject(), eq( geometryEmpty ),
                                                  (OutputStream) anyObject() ) ).thenReturn( mockEmptyReport );
        when(
              imageClipper.calculateClippedImage( (InputStream) anyObject(), (Geometry) isNull(),
                                                  (OutputStream) anyObject() ) ).thenReturn( mockReport );
    }

    /*
     * #filterResponse()
     */

    @Test
    public void testFilterResponseWithCapabilitiesResponseShouldReturnNotACoverageReport()
                            throws Exception {
        StatusCodeResponseBodyWrapper mockedServletResponse = mockResponseWrapper();
        Authentication mockAuthentication = mockAuthentication();
        ResponseFilterReport filterResponse = wcsClippingResponseFilterManager.filterResponse( mockedServletResponse,
                                                                                               createWcsGetCapabilitiesRequest(),
                                                                                               mockAuthentication );
        assertThat( filterResponse.isFiltered(), is( false ) );
        assertThat( filterResponse.isFailed(), is( true ) );
        assertThat( filterResponse.getMessage(), is( NOT_A_CORRECT_REQUEST_MSG ) );
    }

    @Test
    public void testFilterResponseWithCapabilitiesResponseShouldCopyBufferedStream()
                            throws Exception {
        StatusCodeResponseBodyWrapper mockedServletResponse = mockResponseWrapper();
        Authentication mockAuthentication = mockAuthentication();
        wcsClippingResponseFilterManager.filterResponse( mockedServletResponse, createWcsGetCapabilitiesRequest(),
                                                         mockAuthentication );
        verify( mockedServletResponse ).copyBufferedStreamToRealStream();
    }

    @Test
    public void testFilterResponseWithCoverageResponseShouldReturnCorrectReport()
                            throws Exception {
        StatusCodeResponseBodyWrapper mockedServletResponse = mockResponseWrapper();
        Authentication mockAuthentication = mockAuthentication();
        ResponseClippingReport filterResponse = (ResponseClippingReport) wcsClippingResponseFilterManager.filterResponse( mockedServletResponse,
                                                                                                                          createWcsGetCoverageRequest(),
                                                                                                                          mockAuthentication );
        assertThat( filterResponse, is( mockReport ) );
    }

    @Test
    public void testFilterResponseWithCoverageResponseShouldAddHeader()
                            throws Exception {
        StatusCodeResponseBodyWrapper mockedServletResponse = mockResponseWrapper();
        Authentication mockAuthentication = mockAuthentication();
        wcsClippingResponseFilterManager.filterResponse( mockedServletResponse, createWcsGetCoverageRequest(),
                                                         mockAuthentication );
        verify( mockedServletResponse ).addHeader( REQUEST_AREA_HEADER_KEY, GEOMETRY_SIMPLE );
    }

    @Test
    public void testFilterResponseWithCoverageResponseOutsideVisbleAreaShouldAddHeader()
                            throws Exception {
        StatusCodeResponseBodyWrapper mockedServletResponse = mockResponseWrapper();
        Authentication mockAuthentication = mockAuthentication();
        wcsClippingResponseFilterManager.filterResponse( mockedServletResponse,
                                                         createWcsGetCoverageRequestOutsideVisbleArea(),
                                                         mockAuthentication );
        verify( mockedServletResponse ).addHeader( REQUEST_AREA_HEADER_KEY, GEOMETRY_EMPTY );
    }

    @Test
    public void testFilterResponseWithFailureResponseShouldNotAddHeader()
                            throws Exception {
        StatusCodeResponseBodyWrapper mockedServletResponse = mockResponseWrapper();
        Authentication mockAuthentication = mockAuthentication();
        wcsClippingResponseFilterManager.filterResponse( mockedServletResponse,
                                                         createWcsGetCoverageRequestInvokeFailureResponse(),
                                                         mockAuthentication );
        verify( mockedServletResponse, times( 0 ) ).addHeader( eq( REQUEST_AREA_HEADER_KEY ), anyString() );
    }

    @Test
    public void testFilterResponseWithFailureResponseShouldWriteExceptionResponse()
                            throws Exception {
        final ByteArrayOutputStream bufferingStream = new ByteArrayOutputStream();
        ServletOutputStream stream = createStream( bufferingStream );
        StatusCodeResponseBodyWrapper mockedServletResponse = mockResponseWrapperWithOutputStream( stream );
        Authentication mockAuthentication = mockAuthentication();
        wcsClippingResponseFilterManager.filterResponse( mockedServletResponse,
                                                         createWcsGetCoverageRequestInvokeFailureResponse(),
                                                         mockAuthentication );

        assertThat( bufferingStream.toString(), is( DEFAULT_BODY ) );
        verify( mockedServletResponse, times( 1 ) ).setStatus( DEFAULT_STATUS_CODE );
    }

    @Test
    public void testFilterResponseWithExceptionShouldReturnExceptiontResponse()
                            throws Exception {
        StatusCodeResponseBodyWrapper mockedServletResponse = mockResponseWrapperWithExceptionAndStatusCode200();
        Authentication mockAuthentication = mockAuthentication();
        ResponseFilterReport filterResponse = wcsClippingResponseFilterManager.filterResponse( mockedServletResponse,
                                                                                               createWcsGetCoverageRequest(),
                                                                                               mockAuthentication );
        assertThat( filterResponse.isFiltered(), is( false ) );
        assertThat( filterResponse.isFailed(), is( true ) );
        assertThat( filterResponse.getMessage(), is( SERVICE_EXCEPTION_MSG ) );
    }

    @Test
    public void testFilterResponseWithExceptionShouldNotAddHeader()
                            throws Exception {
        StatusCodeResponseBodyWrapper mockedServletResponse = mockResponseWrapperWithExceptionAndStatusCode200();
        Authentication mockAuthentication = mockAuthentication();
        wcsClippingResponseFilterManager.filterResponse( mockedServletResponse, createWcsGetCoverageRequest(),
                                                         mockAuthentication );
        verify( mockedServletResponse, times( 0 ) ).addHeader( eq( REQUEST_AREA_HEADER_KEY ), anyString() );
    }

    @Test
    public void testFilterResponseWithExceptionShouldCopyBufferedStream()
                            throws Exception {
        StatusCodeResponseBodyWrapper mockedServletResponse = mockResponseWrapperWithExceptionAndStatusCode200();
        Authentication mockAuthentication = mockAuthentication();
        wcsClippingResponseFilterManager.filterResponse( mockedServletResponse, createWcsGetCoverageRequest(),
                                                         mockAuthentication );
        verify( mockedServletResponse ).copyBufferedStreamToRealStream();
    }

    @Test
    public void testFilterResponseWithExceptionStatusCodeShouldReturnExceptionResponse()
                            throws Exception {
        StatusCodeResponseBodyWrapper mockedServletResponse = mockResponseWrapperWithoutExceptionAndStatusCode400();
        Authentication mockAuthentication = mockAuthentication();
        ResponseFilterReport filterResponse = wcsClippingResponseFilterManager.filterResponse( mockedServletResponse,
                                                                                               createWcsGetCoverageRequest(),
                                                                                               mockAuthentication );
        assertThat( filterResponse.isFiltered(), is( false ) );
        assertThat( filterResponse.isFailed(), is( true ) );
        assertThat( filterResponse.getMessage(), is( SERVICE_EXCEPTION_MSG ) );
    }

    @Test
    public void testFilterResponseWithExceptionStatusCodeShouldNotAddHeader()
                            throws Exception {
        StatusCodeResponseBodyWrapper mockedServletResponse = mockResponseWrapperWithoutExceptionAndStatusCode400();
        Authentication mockAuthentication = mockAuthentication();
        wcsClippingResponseFilterManager.filterResponse( mockedServletResponse, createWcsGetCoverageRequest(),
                                                         mockAuthentication );
        verify( mockedServletResponse, times( 0 ) ).addHeader( eq( REQUEST_AREA_HEADER_KEY ), anyString() );
    }

    @Test
    public void testFilterResponseWithExceptionStatusCodeShouldCopyBufferedStream()
                            throws Exception {
        StatusCodeResponseBodyWrapper mockedServletResponse = mockResponseWrapperWithoutExceptionAndStatusCode400();
        Authentication mockAuthentication = mockAuthentication();
        wcsClippingResponseFilterManager.filterResponse( mockedServletResponse, createWcsGetCoverageRequest(),
                                                         mockAuthentication );
        verify( mockedServletResponse ).copyBufferedStreamToRealStream();
    }

    @Test
    public void testFilterResponseWithoutGeometryShouldReturnCorrectReport()
                            throws Exception {
        StatusCodeResponseBodyWrapper mockedServletResponse = mockResponseWrapper();
        Authentication mockAuthentication = mockAuthentication();
        ResponseClippingReport filterResponse = (ResponseClippingReport) wcsClippingResponseFilterManager.filterResponse( mockedServletResponse,
                                                                                                                          createWcsGetCoverageRequestWithoutGeom(),
                                                                                                                          mockAuthentication );
        assertThat( filterResponse, is( mockReport ) );
    }

    /*
     * #filterResponse() - Exceptions
     */

    @Test(expected = IllegalArgumentException.class)
    public void testFilterResponseWithNullResponseShouldFail()
                            throws Exception {
        wcsClippingResponseFilterManager.filterResponse( null, createWcsGetCoverageRequest(), mockAuthentication() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFilterResponseWithNullRequestShouldFail()
                            throws Exception {
        wcsClippingResponseFilterManager.filterResponse( mockResponseWrapper(), null, mockAuthentication() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFilterResponseWithUnsupportedRequestShouldFail()
                            throws Exception {
        wcsClippingResponseFilterManager.filterResponse( mockResponseWrapper(), mockOwsRequest(), mockAuthentication() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFilterResponseWithAuthenticationWithoutWcsUserShouldFail()
                            throws Exception {
        wcsClippingResponseFilterManager.filterResponse( mockResponseWrapper(), createWcsGetCoverageRequest(),
                                                         mockAuthenticationWithoutWcsUserPrincipal() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFilterResponseWithWcsRequestWithoutCoverageShouldFail()
                            throws Exception {
        wcsClippingResponseFilterManager.filterResponse( mockResponseWrapper(),
                                                         createWcsGetCoverageRequestWithoutCoverageName(),
                                                         mockAuthentication() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFilterResponseWithWcsRequestWithNullCoverageShouldFail()
                            throws Exception {
        wcsClippingResponseFilterManager.filterResponse( mockResponseWrapper(),
                                                         createWcsGetCoverageRequestWithNullCoverageName(),
                                                         mockAuthentication() );
    }

    @Test
    public void testCanBeFilteredWithWcsGetCoverageRequestShouldReturnTrue() {
        boolean canBeFiltered = wcsClippingResponseFilterManager.canBeFiltered( createWcsGetCoverageRequest() );

        assertThat( canBeFiltered, is( true ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCanBeFilteredWithNullRequestShouldThrowIllegalArgumentException() {
        wcsClippingResponseFilterManager.canBeFiltered( null );
    }

    @Test
    public void testCanBeFilteredWithWcsGetCapabilitiesRequestRequestShouldReturnFalse() {
        boolean canBeFiltered = wcsClippingResponseFilterManager.canBeFiltered( createWcsGetCapabilitiesRequest() );

        assertThat( canBeFiltered, is( false ) );
    }

    @Test
    public void testCanBeFilteredWithNotWcsRequestRequestShouldReturnFalse() {
        boolean canBeFiltered = wcsClippingResponseFilterManager.canBeFiltered( mockOwsRequest() );

        assertThat( canBeFiltered, is( false ) );
    }

    private OwsRequest mockOwsRequest() {
        return mock( OwsRequest.class );
    }

    private WcsRequest createWcsGetCoverageRequest() {
        return new WcsRequest( GETCOVERAGE, VERSION_110, Collections.singletonList( COVERAGE_NAME ), SERVICE_NAME );
    }

    private WcsRequest createWcsGetCoverageRequestOutsideVisbleArea() {
        return new WcsRequest( GETCOVERAGE, VERSION_110, Collections.singletonList( COVERAGE_NAME_EMPTY ), SERVICE_NAME );
    }

    private WcsRequest createWcsGetCoverageRequestWithoutCoverageName() {
        return new WcsRequest( GETCOVERAGE, VERSION_110, Collections.<String> emptyList(), SERVICE_NAME );
    }

    private WcsRequest createWcsGetCoverageRequestInvokeFailureResponse() {
        return new WcsRequest( GETCOVERAGE, VERSION_110, Collections.singletonList( COVERAGE_NAME_FAILURE ),
                               SERVICE_NAME );
    }

    private WcsRequest createWcsGetCoverageRequestWithoutGeom() {
        return new WcsRequest( GETCOVERAGE, VERSION_110, Collections.singletonList( COVERAGE_NAME_NO_GEOM ),
                               SERVICE_NAME );
    }

    private WcsRequest createWcsGetCoverageRequestWithNullCoverageName() {
        return new WcsRequest( GETCOVERAGE, VERSION_110, Collections.<String> singletonList( null ), SERVICE_NAME );
    }

    private WcsRequest createWcsGetCapabilitiesRequest() {
        return new WcsRequest( GETCAPABILITIES, VERSION_110, SERVICE_NAME );
    }

    private StatusCodeResponseBodyWrapper mockResponseWrapper()
                            throws IOException {
        StatusCodeResponseBodyWrapper mockedServletResponse = mock( StatusCodeResponseBodyWrapper.class );
        when( mockedServletResponse.getStatus() ).thenReturn( 200 );
        when( mockedServletResponse.getBufferedStream() ).thenReturn( new ByteArrayInputStream( new byte[] {} ) );
        when( mockedServletResponse.getOutputStream() ).thenReturn( mock( ServletOutputStream.class ) );
        when( mockedServletResponse.getRealOutputStream() ).thenReturn( mock( ServletOutputStream.class ) );
        return mockedServletResponse;
    }

    private StatusCodeResponseBodyWrapper mockResponseWrapperWithOutputStream( ServletOutputStream stream )
                            throws IOException {
        StatusCodeResponseBodyWrapper mockedServletResponse = mock( StatusCodeResponseBodyWrapper.class );
        when( mockedServletResponse.getStatus() ).thenReturn( 200 );
        when( mockedServletResponse.getBufferedStream() ).thenReturn( new ByteArrayInputStream( new byte[] {} ) );
        when( mockedServletResponse.getOutputStream() ).thenReturn( mock( ServletOutputStream.class ) );
        when( mockedServletResponse.getRealOutputStream() ).thenReturn( stream );
        return mockedServletResponse;
    }

    private ServletOutputStream createStream( final ByteArrayOutputStream bufferingStream ) {
        ServletOutputStream stream = new ServletOutputStream() {
            @Override
            public void write( int b )
                                    throws IOException {
                bufferingStream.write( b );
            }
        };
        return stream;
    }

    private StatusCodeResponseBodyWrapper mockResponseWrapperWithExceptionAndStatusCode200()
                            throws IOException {
        StatusCodeResponseBodyWrapper mockedServletResponse = mock( StatusCodeResponseBodyWrapper.class );
        when( mockedServletResponse.getStatus() ).thenReturn( 200 );
        when( mockedServletResponse.getBufferedStream() ).thenReturn( parseServiceException(), parseServiceException() );
        when( mockedServletResponse.getOutputStream() ).thenReturn( mock( ServletOutputStream.class ) );
        when( mockedServletResponse.getRealOutputStream() ).thenReturn( mock( ServletOutputStream.class ) );
        return mockedServletResponse;
    }

    private StatusCodeResponseBodyWrapper mockResponseWrapperWithoutExceptionAndStatusCode400() {
        StatusCodeResponseBodyWrapper mockedServletResponse = mock( StatusCodeResponseBodyWrapper.class );
        when( mockedServletResponse.getStatus() ).thenReturn( 400 );
        when( mockedServletResponse.getBufferedStream() ).thenReturn( new ByteArrayInputStream( new byte[] {} ) );
        when( mockedServletResponse.getOutputStream() ).thenReturn( mock( ServletOutputStream.class ) );
        return mockedServletResponse;
    }

    private Authentication mockAuthentication() {
        Authentication mockedAuthentication = mock( Authentication.class );
        List<GeometryFilterInfo> filters = new ArrayList<GeometryFilterInfo>();
        filters.add( new GeometryFilterInfo( COVERAGE_NAME, GEOMETRY_SIMPLE ) );
        filters.add( new GeometryFilterInfo( COVERAGE_NAME_FAILURE, GEOMETRY_FAILURE ) );
        filters.add( new GeometryFilterInfo( COVERAGE_NAME_EMPTY, GEOMETRY_EMPTY ) );
        filters.add( new GeometryFilterInfo( COVERAGE_NAME_NO_GEOM ) );
        RasterUser wcsUser = new RasterUser( "user", "password", "accessToken",
                                             Collections.<RasterPermission> emptyList(), filters );
        when( mockedAuthentication.getPrincipal() ).thenReturn( wcsUser );
        return mockedAuthentication;
    }

    private Authentication mockAuthenticationWithoutWcsUserPrincipal() {
        Authentication mockedAuthentication = mock( Authentication.class );
        when( mockedAuthentication.getPrincipal() ).thenReturn( mock( UserDetails.class ) );
        return mockedAuthentication;
    }

    private InputStream parseServiceException() {
        return WcsClippingResponseFilterManagerTest.class.getResourceAsStream( "../service_exception.xml" );
    }

}

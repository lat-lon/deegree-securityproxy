package org.deegree.securityproxy.wcs.responsefilter.capabilities;

import static org.deegree.securityproxy.wcs.request.WcsRequestParser.GETCAPABILITIES;
import static org.deegree.securityproxy.wcs.request.WcsRequestParser.GETCOVERAGE;
import static org.deegree.securityproxy.wcs.request.WcsRequestParser.VERSION_110;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Collections;

import org.deegree.securityproxy.filter.StatusCodeResponseBodyWrapper;
import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.responsefilter.logging.ResponseFilterReport;
import org.deegree.securityproxy.wcs.request.WcsRequest;
import org.junit.Test;
import org.springframework.security.core.Authentication;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class WcsCapabilitiesResponseFilterManagerTest {

    private static final String SERVICE_NAME = "serviceName";

    private static final String COVERAGE_NAME = "coverageName";

    private static final String SUCCESSFUL_FILTERING_MESSAGE = "Capabilities of request were filtered successfully.";

    private final CapabilitiesFilter capabilitiesFilter = mock( CapabilitiesFilter.class );

    private final WcsCapabilitiesResponseFilterManager wcsCapabilitiesResponseFilterManager = new WcsCapabilitiesResponseFilterManager(
                                                                                                                                        capabilitiesFilter );

    @Test
    public void testFilterResponseShouldCallFilterCapabilities()
                            throws Exception {
        CapabilitiesFilter capabilitiesFilter = mockCapabilitiesFilter();
        WcsCapabilitiesResponseFilterManager wcsCapabilitiesResponseFilterManager = new WcsCapabilitiesResponseFilterManager(
                                                                                                                              capabilitiesFilter );
        StatusCodeResponseBodyWrapper response = mockStatusCodeResponseBodyWrapper();
        WcsRequest wcsRequest = createWcsGetCapabilitiesRequest();
        Authentication authentication = mockAuthentication();
        wcsCapabilitiesResponseFilterManager.filterResponse( response, wcsRequest, authentication );

        verify( capabilitiesFilter ).filterCapabilities( eq( response ), any( ElementDecisionMaker.class ) );
    }

    @Test
    public void testFilterResponseShouldReturnFilterReport()
                            throws Exception {
        StatusCodeResponseBodyWrapper response = mockStatusCodeResponseBodyWrapper();
        WcsRequest wcsRequest = createWcsGetCapabilitiesRequest();
        Authentication authentication = mockAuthentication();
        ResponseFilterReport filterReport = wcsCapabilitiesResponseFilterManager.filterResponse( response, wcsRequest,
                                                                                                 authentication );

        assertThat( filterReport, notNullValue() );
        assertThat( filterReport.getMessage(), is( SUCCESSFUL_FILTERING_MESSAGE ) );
        assertThat( filterReport.isFiltered(), is( true ) );
    }

    @Test
    public void testCanBeFilteredWithWcsGetCapabilitiesRequestRequestShouldReturnTrue() {
        boolean canBeFiltered = wcsCapabilitiesResponseFilterManager.canBeFiltered( createWcsGetCapabilitiesRequest() );

        assertThat( canBeFiltered, is( true ) );
    }

    @Test
    public void testCanBeFilteredWithWcsGetCoverageRequestShouldReturnFalse() {
        boolean canBeFiltered = wcsCapabilitiesResponseFilterManager.canBeFiltered( createWcsGetCoverageRequest() );

        assertThat( canBeFiltered, is( false ) );
    }

    @Test
    public void testCanBeFilteredWithNotWcsRequestRequestShouldReturnFalse() {
        boolean canBeFiltered = wcsCapabilitiesResponseFilterManager.canBeFiltered( mockOwsRequest() );

        assertThat( canBeFiltered, is( false ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCanBeFilteredWithNullRequestShouldThrowIllegalArgumentException() {
        wcsCapabilitiesResponseFilterManager.canBeFiltered( null );
    }

    private OwsRequest mockOwsRequest() {
        return mock( OwsRequest.class );
    }

    private WcsRequest createWcsGetCapabilitiesRequest() {
        return new WcsRequest( GETCAPABILITIES, VERSION_110, SERVICE_NAME );
    }

    private WcsRequest createWcsGetCoverageRequest() {
        return new WcsRequest( GETCOVERAGE, VERSION_110, Collections.singletonList( COVERAGE_NAME ), SERVICE_NAME );
    }

    private Authentication mockAuthentication() {
        return mock( Authentication.class );
    }

    private StatusCodeResponseBodyWrapper mockStatusCodeResponseBodyWrapper() {
        return mock( StatusCodeResponseBodyWrapper.class );
    }

    private CapabilitiesFilter mockCapabilitiesFilter() {
        return mock( CapabilitiesFilter.class );
    }

}

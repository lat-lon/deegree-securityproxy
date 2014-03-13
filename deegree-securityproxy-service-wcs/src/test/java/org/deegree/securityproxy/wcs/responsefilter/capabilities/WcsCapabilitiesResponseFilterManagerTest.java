package org.deegree.securityproxy.wcs.responsefilter.capabilities;

import static org.deegree.securityproxy.wcs.request.WcsRequestParser.GETCAPABILITIES;
import static org.deegree.securityproxy.wcs.request.WcsRequestParser.GETCOVERAGE;
import static org.deegree.securityproxy.wcs.request.WcsRequestParser.VERSION_110;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Collections;

import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.wcs.request.WcsRequest;
import org.junit.Test;

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

    private final WcsCapabilitiesResponseFilterManager wcsCapabilitiesResponseFilterManager = new WcsCapabilitiesResponseFilterManager();

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

}

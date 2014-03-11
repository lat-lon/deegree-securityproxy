package org.deegree.securityproxy.wcs.responsefilter.capabilities;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.wcs.request.WcsRequest;
import org.junit.Test;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class WcsCapabilitiesResponseFilterManagerTest {

    @Test
    public void testSupportsWcsRequestShouldReturnTrue()
                            throws Exception {
        WcsCapabilitiesResponseFilterManager wcsCapabilitiesResponseFilterManager = new WcsCapabilitiesResponseFilterManager();
        boolean supportsWcsRequest = wcsCapabilitiesResponseFilterManager.supports( WcsRequest.class );
        assertThat( supportsWcsRequest, is( true ) );
    }

    @Test
    public void testSupportsOwsRequestShouldReturnFalse()
                            throws Exception {
        WcsCapabilitiesResponseFilterManager wcsCapabilitiesResponseFilterManager = new WcsCapabilitiesResponseFilterManager();
        boolean supportsOwsRequest = wcsCapabilitiesResponseFilterManager.supports( OwsRequest.class );
        assertThat( supportsOwsRequest, is( false ) );
    }

}
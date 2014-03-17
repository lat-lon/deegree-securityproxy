package org.deegree.securityproxy.responsefilter.logging;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Test for {@link ResponseCapabilitiesReport}.
 * 
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * 
 * @version $Revision: $, $Date: $
 */
public class ResponseCapabilitiesReportTest {
    @Test
    public void testGetMessage()
                            throws Exception {
        String message = "Message";
        ResponseCapabilitiesReport report = new ResponseCapabilitiesReport( message, true );

        assertThat( report.getMessage(), is( message ) );
        assertThat( report.isFiltered(), is( true ) );
    }

    @Test
    public void testGetMessageWithFailure()
                            throws Exception {
        String failureMessage = "An error occurred";
        ResponseCapabilitiesReport report = new ResponseCapabilitiesReport( failureMessage );

        assertThat( report.getMessage(), is( failureMessage ) );
        assertThat( report.isFiltered(), is( false ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetMessageWithNullShouldFail()
                            throws Exception {
        new ResponseCapabilitiesReport( null, false );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetMessageWithNullFailureShouldFail()
                            throws Exception {
        new ResponseCapabilitiesReport( null );
    }

}

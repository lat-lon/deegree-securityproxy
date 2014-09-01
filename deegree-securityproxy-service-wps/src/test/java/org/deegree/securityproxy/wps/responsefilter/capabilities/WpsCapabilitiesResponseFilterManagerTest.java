package org.deegree.securityproxy.wps.responsefilter.capabilities;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.request.OwsServiceVersion;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.XmlFilter;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.XmlModificationManagerCreator;
import org.deegree.securityproxy.wps.request.WpsRequest;
import org.junit.Test;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class WpsCapabilitiesResponseFilterManagerTest {

    @Test
    public void testIsCorrectServiceTypeFromWpsExecuteRequest()
                    throws Exception {
        WpsCapabilitiesResponseFilterManager filterManager = createFilterManagerWithMocks();
        boolean correctServiceType = filterManager.isCorrectServiceType( createWpsExecuteRequest() );

        assertThat( correctServiceType, is( true ) );
    }

    @Test
    public void testIsCorrectServiceTypeFromMockedRequest()
                    throws Exception {
        WpsCapabilitiesResponseFilterManager filterManager = createFilterManagerWithMocks();
        boolean correctServiceType = filterManager.isCorrectServiceType( mock( OwsRequest.class ) );

        assertThat( correctServiceType, is( false ) );
    }

    @Test
    public void testIsCorrectRequestParameterFromWpsCapabilitiesRequest()
                    throws Exception {
        WpsCapabilitiesResponseFilterManager filterManager = createFilterManagerWithMocks();
        boolean correctServiceType = filterManager.isCorrectRequestParameter( createWpsGetCapabilitiesRequest() );

        assertThat( correctServiceType, is( true ) );
    }

    @Test
    public void testIsCorrectRequestParameterFromWpsExecuteRequest()
                    throws Exception {
        WpsCapabilitiesResponseFilterManager filterManager = createFilterManagerWithMocks();
        boolean correctServiceType = filterManager.isCorrectRequestParameter( createWpsExecuteRequest() );

        assertThat( correctServiceType, is( false ) );
    }

    @Test
    public void testIsCorrectRequestParameterFromMockedRequestRequest()
                    throws Exception {
        WpsCapabilitiesResponseFilterManager filterManager = createFilterManagerWithMocks();
        boolean correctServiceType = filterManager.isCorrectRequestParameter( mock( OwsRequest.class ) );

        assertThat( correctServiceType, is( false ) );
    }

    private WpsCapabilitiesResponseFilterManager createFilterManagerWithMocks() {
        XmlFilter capabilitiesFilter = mock( XmlFilter.class );
        XmlModificationManagerCreator xmlModificationManagerCreator = mock( XmlModificationManagerCreator.class );
        return new WpsCapabilitiesResponseFilterManager( capabilitiesFilter, xmlModificationManagerCreator );
    }

    private WpsRequest createWpsExecuteRequest() {
        return new WpsRequest( "Execute", new OwsServiceVersion( "1.0.0" ), "WPS" );
    }

    private WpsRequest createWpsGetCapabilitiesRequest() {
        return new WpsRequest( "GetCapabilities", new OwsServiceVersion( "1.0.0" ), "WPS" );
    }

}
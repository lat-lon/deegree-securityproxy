package org.deegree.securityproxy.wms.responsefilter.capabilities;

import static org.deegree.securityproxy.wms.request.WmsRequestParser.GETCAPABILITIES;
import static org.deegree.securityproxy.wms.request.WmsRequestParser.GETMAP;
import static org.deegree.securityproxy.wms.request.WmsRequestParser.VERSION_130;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.CapabilitiesFilter;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.DecisionMakerCreator;
import org.deegree.securityproxy.wms.request.WmsRequest;
import org.junit.Test;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class WmsCapabilitiesResponseFilterManagerTest {

    @Test
    public void testIsCorrectRequestType()
                            throws Exception {
        WmsCapabilitiesResponseFilterManager filterManager = createFilterManager();
        boolean isCorrectRequestType = filterManager.isCorrectRequestType( createWms130CapabilitiesRequest() );

        assertThat( isCorrectRequestType, is( true ) );
    }

    @Test
    public void testIsCorrectRequestTypeWithWcsRequestShouldReturnFalse()
                            throws Exception {
        WmsCapabilitiesResponseFilterManager filterManager = createFilterManager();
        boolean isCorrectRequestType = filterManager.isCorrectRequestType( mockWcsRequest() );

        assertThat( isCorrectRequestType, is( false ) );
    }

    @Test
    public void testIsGetCapabilitiesRequestRequestType()
                            throws Exception {
        WmsCapabilitiesResponseFilterManager filterManager = createFilterManager();
        boolean isCorrectRequestType = filterManager.isGetCapabilitiesRequest( createWms130CapabilitiesRequest() );

        assertThat( isCorrectRequestType, is( true ) );
    }

    @Test
    public void testIsGetCapabilitiesRequestRequestTypeWithGetMapRequestShouldReturnFalse()
                            throws Exception {
        WmsCapabilitiesResponseFilterManager filterManager = createFilterManager();
        boolean isCorrectRequestType = filterManager.isGetCapabilitiesRequest( createWms130GetMapRequest() );

        assertThat( isCorrectRequestType, is( false ) );
    }

    private WmsCapabilitiesResponseFilterManager createFilterManager() {
        CapabilitiesFilter capabilitiesFilter = mock( CapabilitiesFilter.class );
        DecisionMakerCreator decisionMakerCreator = mock( DecisionMakerCreator.class );
        return new WmsCapabilitiesResponseFilterManager( capabilitiesFilter, decisionMakerCreator );
    }

    private OwsRequest createWms130CapabilitiesRequest() {
        return new WmsRequest( GETCAPABILITIES, VERSION_130, "serviceName" );
    }

    private OwsRequest createWms130GetMapRequest() {
        return new WmsRequest( GETMAP, VERSION_130, "serviceName" );
    }

    private OwsRequest mockWcsRequest() {
        OwsRequest request = mock( OwsRequest.class );
        when( request.getServiceType() ).thenReturn( "NotKnown" );
        return request;
    }

}
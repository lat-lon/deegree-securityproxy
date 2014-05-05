package org.deegree.securityproxy.wfs.responsefilter.capabilities;

import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.request.OwsServiceVersion;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.CapabilitiesFilter;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.DecisionMakerCreator;
import org.deegree.securityproxy.wfs.request.WfsRequest;
import org.junit.Test;

import static org.deegree.securityproxy.wfs.request.WfsGetRequestParser.GETCAPABILITIES;
import static org.deegree.securityproxy.wfs.request.WfsGetRequestParser.GETFEATURE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link WfsCapabilitiesResponseFilterManager}.
 * 
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * 
 * @version $Revision: $, $Date: $
 */
public class WfsCapabilitiesResponseFilterManagerTest {

    private final WfsCapabilitiesResponseFilterManager filterManager = new WfsCapabilitiesResponseFilterManager(
                                                                                                                 mock( CapabilitiesFilter.class ),
                                                                                                                 mock( DecisionMakerCreator.class ) );

    @Test
    public void testIsCorrectServiceTypeWithWfsRequestShouldReturnTrue()
                            throws Exception {
        OwsRequest request = createWfsRequest();
        boolean isCorrect = filterManager.isCorrectServiceType( request );

        assertThat( isCorrect, is( true ) );
    }

    @Test
    public void testIsCorrectServiceTypeWithNonWfsRequestShouldReturnFalse()
                            throws Exception {
        OwsRequest request = createNonWfsRequest();
        boolean isCorrect = filterManager.isCorrectServiceType( request );

        assertThat( isCorrect, is( false ) );
    }

    @Test
    public void testIsCorrectRequestParameterWithGetCapabilitiesRequestShouldReturnTrue()
                            throws Exception {
        OwsRequest request = createGetCapabilitiesRequest();
        boolean isCorrect = filterManager.isCorrectRequestParameter( request );

        assertThat( isCorrect, is( true ) );
    }

    @Test
    public void testIsCorrectRequestParameterWithGetFeatureRequestShouldReturnFalse()
                            throws Exception {
        OwsRequest request = createGetFeatureRequest();
        boolean isCorrect = filterManager.isCorrectRequestParameter( request );

        assertThat( isCorrect, is( false ) );
    }

    private OwsRequest createWfsRequest() {
        return new WfsRequest( GETCAPABILITIES, new OwsServiceVersion( 1, 1, 0 ) );
    }

    private OwsRequest createNonWfsRequest() {
        return mock( OwsRequest.class );
    }

    private OwsRequest createGetCapabilitiesRequest() {
        return new WfsRequest( GETCAPABILITIES, new OwsServiceVersion( 1, 1, 0 ) );
    }

    private OwsRequest createGetFeatureRequest() {
        return new WfsRequest( GETFEATURE, new OwsServiceVersion( 1, 1, 0 ) );
    }

}

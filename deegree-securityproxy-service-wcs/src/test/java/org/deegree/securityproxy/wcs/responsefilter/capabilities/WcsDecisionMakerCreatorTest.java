package org.deegree.securityproxy.wcs.responsefilter.capabilities;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.deegree.securityproxy.authentication.ows.domain.LimitedOwsServiceVersion;
import org.deegree.securityproxy.authentication.ows.raster.RasterPermission;
import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.request.OwsServiceVersion;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.DecisionMaker;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class WcsDecisionMakerCreatorTest {

    private static final String COVERAGE_NAME_1 = "123_6788";

    private static final String COVERAGE_NAME_2 = "567_8765";

    private final WcsDecisionMakerCreator elementRuleCreator = new WcsDecisionMakerCreator();

    @Test
    public void testCreateDecisionMakerForWcs100OneGetCoverage()
                            throws Exception {
        DecisionMaker decisionMaker = elementRuleCreator.createDecisionMaker( mockOwsRequest(),
                                                                              createAuthenticationWithOneGetCoverage() );

        assertThat( decisionMaker, is( notNullValue() ) );
    }

    @Test
    public void testCreateDecisionMakerForWcs100TwoGetCoverageOneDescribeCoverage()
                            throws Exception {
        DecisionMaker decisionMaker = elementRuleCreator.createDecisionMaker( mockOwsRequest(),
                                                                              createAuthenticationWithTwoGetCoverageOneDescribeCoverage() );

        assertThat( decisionMaker, is( notNullValue() ) );
    }

    @Test
    public void testCreateDecisionMakerForWcs100OneDescribeCoverage()
                            throws Exception {
        DecisionMaker decisionMaker = elementRuleCreator.createDecisionMaker( mockOwsRequest(),
                                                                              createAuthenticationWithOneDescribeCoverage() );

        assertThat( decisionMaker, is( nullValue() ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateDecisionMakerForWcs130ShouldFail()
                            throws Exception {
        elementRuleCreator.createDecisionMaker( mockOwsRequest130(), createAuthenticationWithOneDescribeCoverage() );
    }

    private Authentication createAuthenticationWithOneDescribeCoverage() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add( createRasterPermission( "DescribeCoverage", COVERAGE_NAME_1 ) );
        return mockAuthentication( authorities );
    }

    private Authentication createAuthenticationWithOneGetCoverage() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add( createRasterPermission( "GetCoverage", COVERAGE_NAME_1 ) );
        return mockAuthentication( authorities );
    }

    private Authentication createAuthenticationWithTwoGetCoverageOneDescribeCoverage() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add( createRasterPermission( "GetCoverage", COVERAGE_NAME_1 ) );
        authorities.add( createRasterPermission( "GetCoverage", COVERAGE_NAME_2 ) );
        authorities.add( createRasterPermission( "DescribeCoverage", COVERAGE_NAME_1 ) );
        return mockAuthentication( authorities );
    }

    private RasterPermission createRasterPermission( String operationType, String coverageName ) {
        return new RasterPermission( "wcs", operationType, new LimitedOwsServiceVersion( "<= 1.1.0" ), coverageName,
                                     "serviceName", "internalServiceUrl", null );
    }

    private Authentication mockAuthentication( Collection<? extends GrantedAuthority> authorities ) {
        Authentication authenticationMock = mock( Authentication.class );
        doReturn( authorities ).when( authenticationMock ).getAuthorities();
        return authenticationMock;
    }

    private OwsRequest mockOwsRequest() {
        return mockOwsRequest( "1.0.0" );
    }

    private OwsRequest mockOwsRequest130() {
        return mockOwsRequest( "1.3.0" );
    }

    private OwsRequest mockOwsRequest( String version ) {
        OwsRequest request = mock( OwsRequest.class );
        when( request.getServiceVersion() ).thenReturn( new OwsServiceVersion( version ) );
        return request;
    }
}
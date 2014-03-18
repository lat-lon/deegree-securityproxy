package org.deegree.securityproxy.wcs.responsefilter.capabilities;

import static org.deegree.securityproxy.wcs.responsefilter.capabilities.ElementRuleCreator.WCS_1_0_0_NS_URI;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.deegree.securityproxy.authentication.ows.domain.LimitedOwsServiceVersion;
import org.deegree.securityproxy.authentication.ows.raster.RasterPermission;
import org.deegree.securityproxy.wcs.responsefilter.capabilities.element.ElementRule;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class ElementRuleCreatorTest {

    private static final String COVERAGE_NAME_1 = "123_6788";

    private static final String COVERAGE_NAME_2 = "567_8765";

    private final ElementRuleCreator elementRuleCreator = new ElementRuleCreator();

    @Test
    public void testCreateElementRulesForWcs100OneGetCoverage()
                            throws Exception {
        List<ElementRule> elementRules = elementRuleCreator.createElementRulesForWcs100( createAuthenticationWithOneGetCoverage() );

        assertThat( elementRules.size(), is( 1 ) );
        assertThat( elementRules, hasItem( expectedRule( COVERAGE_NAME_1 ) ) );
    }

    @Test
    public void testCreateElementRulesForWcs100TwoGetCoverageOneDescribeCoverage()
                            throws Exception {
        List<ElementRule> elementRules = elementRuleCreator.createElementRulesForWcs100( createAuthenticationWithTwoGetCoverageOneDescribeCoverage() );

        assertThat( elementRules.size(), is( 2 ) );
        assertThat( elementRules, hasItem( expectedRule( COVERAGE_NAME_1 ) ) );
        assertThat( elementRules, hasItem( expectedRule( COVERAGE_NAME_2 ) ) );
    }

    @Test
    public void testCreateElementRulesForWcs100OneDescribeCoverage()
                            throws Exception {
        List<ElementRule> elementRules = elementRuleCreator.createElementRulesForWcs100( createAuthenticationWithOneDescribeCoverage() );

        assertThat( elementRules.size(), is( 0 ) );
    }

    private ElementRule expectedRule( String subRuleText ) {
        ElementRule subRule = new ElementRule( "name", WCS_1_0_0_NS_URI, subRuleText );
        return new ElementRule( "CoverageOfferingBrief", WCS_1_0_0_NS_URI, subRule );
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

}
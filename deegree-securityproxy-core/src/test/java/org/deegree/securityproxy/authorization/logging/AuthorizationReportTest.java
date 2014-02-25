package org.deegree.securityproxy.authorization.logging;

import org.junit.Test;

import java.util.Collections;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests for {@link AuthorizationReport}.
 *
 * @author <a href="stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * @version $Revision: $, $Date: $
 */
public class AuthorizationReportTest {

    @Test
    public void testGetAdditionalKeyValuePairs() throws Exception {
        Map<String, String[]> requestParametersMap = createRequestParametersMap();
        AuthorizationReport authorizationReport = new AuthorizationReport( "message", true, "serviceUrl",
                                                                           requestParametersMap );
        Map<String, String[]> requestParameters = authorizationReport.getAdditionalKeyValuePairs();

        assertThat( requestParameters, is( requestParametersMap ) );
    }

    @Test
    public void testGetAdditionalKeyValuePairsWithNull() throws Exception {
        AuthorizationReport authorizationReport = new AuthorizationReport( "message", true, "serviceUrl", null );
        Map<String, String[]> emptyRequestParametersMap = createEmptyRequestParametersMap();
        Map<String, String[]> requestParameters = authorizationReport.getAdditionalKeyValuePairs();

        assertThat( requestParameters, is( emptyRequestParametersMap ) );
    }

    @Test
    public void testGetAdditionalKeyValuePairsWithEmptyMap() throws Exception {
        Map<String, String[]> emptyRequestParametersMap = createEmptyRequestParametersMap();
        AuthorizationReport authorizationReport = new AuthorizationReport( "message", true, "serviceUrl",
                                                                           emptyRequestParametersMap );
        Map<String, String[]> requestParameters = authorizationReport.getAdditionalKeyValuePairs();

        assertThat( requestParameters, is( emptyRequestParametersMap ) );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetAdditionalKeyValuePairsShouldReturnUnmodifiableFilterList() throws Exception {
        AuthorizationReport authorizationReport = new AuthorizationReport( "message", true, "serviceUrl",
                                                                           createRequestParametersMap() );
        Map<String, String[]> requestParameters = authorizationReport.getAdditionalKeyValuePairs();
        requestParameters.put( "key", new String[] { "value" } );
    }

    private Map<String, String[]> createRequestParametersMap() {
        return Collections.singletonMap( "key", new String[] { "value" } );
    }

    private Map<String, String[]> createEmptyRequestParametersMap() {
        return emptyMap();
    }

}
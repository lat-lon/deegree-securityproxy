package org.deegree.securityproxy.wcs.authentication;

import org.deegree.securityproxy.wcs.domain.WcsOperationType;
import org.deegree.securityproxy.wcs.domain.WcsServiceVersion;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.deegree.securityproxy.wcs.domain.WcsOperationType.GETCOVERAGE;
import static org.deegree.securityproxy.wcs.domain.WcsServiceVersion.VERSION_100;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests for {@link WcsPermission}.
 *
 * @author <a href="stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * @version $Revision: $, $Date: $
 */
public class WcsPermissionTest {

    private static final WcsOperationType OPERATION_TYPE = GETCOVERAGE;

    private static final WcsServiceVersion VERSION = VERSION_100;

    private static final String COVERAGE_NAME = "layerName";

    private static final String SERVICE_NAME = "serviceName";

    private static final String INTERNAL_SERVICE_URL = "serviceUrl";

    @Test
    public void testGetAdditionalKeyValuePairs() throws Exception {
        Map<String, String[]> requestParametersMap = createRequestParametersMap();
        WcsPermission wcsPermission = new WcsPermission( OPERATION_TYPE, VERSION, COVERAGE_NAME, SERVICE_NAME,
                                                         INTERNAL_SERVICE_URL, requestParametersMap );
        Map<String, String[]> requestParameters = wcsPermission.getAdditionalKeyValuePairs();

        assertThat( requestParameters, is( requestParametersMap ) );
    }

    @Test
    public void testGetAdditionalKeyValuePairsWithNull() throws Exception {
        WcsPermission wcsPermission = new WcsPermission( OPERATION_TYPE, VERSION, COVERAGE_NAME, SERVICE_NAME,
                                                         INTERNAL_SERVICE_URL, null );
        Map<String, String[]> emptyRequestParametersMap = createEmptyRequestParametersMap();
        Map<String, String[]> requestParameters = wcsPermission.getAdditionalKeyValuePairs();

        assertThat( requestParameters, is( emptyRequestParametersMap ) );
    }

    @Test
    public void testGetAdditionalKeyValuePairsWithEmptyMap() throws Exception {
        Map<String, String[]> emptyRequestParametersMap = createEmptyRequestParametersMap();
        WcsPermission wcsPermission = new WcsPermission( OPERATION_TYPE, VERSION, COVERAGE_NAME, SERVICE_NAME,
                                                         INTERNAL_SERVICE_URL, emptyRequestParametersMap );
        Map<String, String[]> requestParameters = wcsPermission.getAdditionalKeyValuePairs();

        assertThat( requestParameters, is( emptyRequestParametersMap ) );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetAdditionalKeyValuePairsShouldReturnUnmodifiableFilterList() throws Exception {
        WcsPermission wcsPermission = new WcsPermission( OPERATION_TYPE, VERSION, COVERAGE_NAME, SERVICE_NAME,
                                                         INTERNAL_SERVICE_URL, createRequestParametersMap() );
        Map<String, String[]> requestParameters = wcsPermission.getAdditionalKeyValuePairs();
        requestParameters.put( "key", new String[] { "value" } );
    }

    private Map<String, String[]> createRequestParametersMap() {
        return Collections.singletonMap( "key", new String[] { "value" } );
    }

    private Map<String, String[]> createEmptyRequestParametersMap() {
        return emptyMap();
    }

}
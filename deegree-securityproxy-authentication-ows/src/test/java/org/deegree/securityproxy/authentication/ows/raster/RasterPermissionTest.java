package org.deegree.securityproxy.authentication.ows.raster;

import static java.util.Collections.emptyMap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collections;
import java.util.Map;

import org.deegree.securityproxy.authentication.ows.domain.LimitedOwsServiceVersion;
import org.deegree.securityproxy.authentication.ows.raster.RasterPermission;
import org.junit.Test;

/**
 * Tests for {@link RasterPermission}.
 * 
 * @author <a href="stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * @version $Revision: $, $Date: $
 */
public class RasterPermissionTest {

    private static final String OPERATION_TYPE = "GetCoverage";

    private static final LimitedOwsServiceVersion VERSION = new LimitedOwsServiceVersion( "<= 1.2.0" );

    private static final String COVERAGE_NAME = "layerName";

    private static final String SERVICE_NAME = "serviceName";

    private static final String INTERNAL_SERVICE_URL = "serviceUrl";

    @Test
    public void testGetAdditionalKeyValuePairs()
                            throws Exception {
        Map<String, String[]> requestParametersMap = createRequestParametersMap();
        RasterPermission wcsPermission = new RasterPermission( OPERATION_TYPE, VERSION, COVERAGE_NAME, SERVICE_NAME,
                                                         INTERNAL_SERVICE_URL, requestParametersMap );
        Map<String, String[]> requestParameters = wcsPermission.getAdditionalKeyValuePairs();

        assertThat( requestParameters, is( requestParametersMap ) );
    }

    @Test
    public void testGetAdditionalKeyValuePairsWithNull()
                            throws Exception {
        RasterPermission wcsPermission = new RasterPermission( OPERATION_TYPE, VERSION, COVERAGE_NAME, SERVICE_NAME,
                                                         INTERNAL_SERVICE_URL, null );
        Map<String, String[]> emptyRequestParametersMap = createEmptyRequestParametersMap();
        Map<String, String[]> requestParameters = wcsPermission.getAdditionalKeyValuePairs();

        assertThat( requestParameters, is( emptyRequestParametersMap ) );
    }

    @Test
    public void testGetAdditionalKeyValuePairsWithEmptyMap()
                            throws Exception {
        Map<String, String[]> emptyRequestParametersMap = createEmptyRequestParametersMap();
        RasterPermission wcsPermission = new RasterPermission( OPERATION_TYPE, VERSION, COVERAGE_NAME, SERVICE_NAME,
                                                         INTERNAL_SERVICE_URL, emptyRequestParametersMap );
        Map<String, String[]> requestParameters = wcsPermission.getAdditionalKeyValuePairs();

        assertThat( requestParameters, is( emptyRequestParametersMap ) );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetAdditionalKeyValuePairsShouldReturnUnmodifiableFilterList()
                            throws Exception {
        RasterPermission wcsPermission = new RasterPermission( OPERATION_TYPE, VERSION, COVERAGE_NAME, SERVICE_NAME,
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
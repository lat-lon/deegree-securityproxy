package org.deegree.securityproxy.filter;

import static java.util.Collections.emptyMap;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;

/**
 * Tests for {@link KvpRequestWrapper}.
 * 
 * @author <a href="stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * @version $Revision: $, $Date: $
 */
public class KvpRequestWrapperTest {

    private final HttpServletRequest request = mockHttpServletRequest();

    private final HttpServletRequest requestWithoutParams = mockHttpServletRequestWithoutParams();

    private final Map<String, String[]> additionalKeyValuePair = createAdditionalKeyValuePair();

    private final Map<String, String[]> additionalKeyValuePairWithoutEntries = new HashMap<String, String[]>();

    private final Map<String, String[]> additionalKeyValuePairWithTwoValues = createAdditionalKeyValuePairsWithTwoValues();

    private final Map<String, String[]> additionalKeyValuePairsWithThreeEntries = createAdditionalKeyValuePairsWithThreeEntries();

    @Test
    public void testGetQueryString()
                            throws Exception {
        KvpRequestWrapper kvpRequestWrapper = new KvpRequestWrapper( request, additionalKeyValuePair );
        String queryString = kvpRequestWrapper.getQueryString();

        String exptectedQueryString = "?existingKey=existingValue&additionalKey=additionalValue";
        assertThat( queryString, is( exptectedQueryString ) );
    }

    @Test
    public void testGetQueryStringWithoutAdditional()
                            throws Exception {
        KvpRequestWrapper kvpRequestWrapper = new KvpRequestWrapper( request, additionalKeyValuePairWithoutEntries );
        String queryString = kvpRequestWrapper.getQueryString();

        String exptectedQueryString = "?existingKey=existingValue";
        assertThat( queryString, is( exptectedQueryString ) );
    }

    @Test
    public void testGetQueryStringWithThreeAdditionalEntries()
                            throws Exception {
        KvpRequestWrapper kvpRequestWrapper = new KvpRequestWrapper( request, additionalKeyValuePairsWithThreeEntries );
        String queryString = kvpRequestWrapper.getQueryString();

        String exptectedQueryString = "?existingKey=existingValue&additionalKey1=additionalValue1&additionalKey2=additionalValue2&additionalKey3=additionalValue3";
        assertThat( queryString, is( exptectedQueryString ) );
    }

    @Test
    public void testGetQueryStringWithoutOriginalParamsAndThreeAdditional()
                            throws Exception {
        KvpRequestWrapper kvpRequestWrapper = new KvpRequestWrapper( requestWithoutParams,
                                                                     additionalKeyValuePairsWithThreeEntries );
        String queryString = kvpRequestWrapper.getQueryString();

        String exptectedQueryString = "?additionalKey1=additionalValue1&additionalKey2=additionalValue2&additionalKey3=additionalValue3";
        assertThat( queryString, is( exptectedQueryString ) );
    }

    @Test
    public void testGetQueryStringWithTwoValues()
                            throws Exception {
        KvpRequestWrapper kvpRequestWrapper = new KvpRequestWrapper( request, additionalKeyValuePairWithTwoValues );
        String queryString = kvpRequestWrapper.getQueryString();

        String exptectedQueryString = "?existingKey=existingValue&additionalKey=additionalValue1,additionalValue2";
        assertThat( queryString, is( exptectedQueryString ) );
    }

    @Test
    public void testGetParameterMap()
                            throws Exception {
        KvpRequestWrapper kvpRequestWrapper = new KvpRequestWrapper( request, additionalKeyValuePair );
        Map<String, String[]> parameterMap = kvpRequestWrapper.getParameterMap();

        String expectedExistingKey = "existingKey";
        String expectedAdditionalKey = "additionalKey";
        String[] expectedExistingValue = new String[] { "existingValue" };
        String[] expectedAdditionalValue = new String[] { "additionalValue" };
        Set<String> actualKeySet = parameterMap.keySet();
        String[] actualExistingValue = parameterMap.get( expectedExistingKey );
        String[] actualAdditionalValue = parameterMap.get( expectedAdditionalKey );

        assertThat( parameterMap.size(), is( 2 ) );
        assertThat( actualKeySet, hasItem( expectedExistingKey ) );
        assertThat( actualKeySet, hasItem( expectedAdditionalKey ) );
        assertThat( actualExistingValue, is( expectedExistingValue ) );
        assertThat( actualAdditionalValue, is( expectedAdditionalValue ) );
    }

    @Test
    public void testGetParameterMapWithThreeAdditionalEntries()
                            throws Exception {
        KvpRequestWrapper kvpRequestWrapper = new KvpRequestWrapper( request, additionalKeyValuePairsWithThreeEntries );
        Map<String, String[]> parameterMap = kvpRequestWrapper.getParameterMap();

        String expectedExistingKey = "existingKey";
        String expectedAdditionalKey1 = "additionalKey1";
        String expectedAdditionalKey2 = "additionalKey2";
        String expectedAdditionalKey3 = "additionalKey3";
        String[] expectedExistingValue = new String[] { "existingValue" };
        String[] expectedAdditionalValue1 = new String[] { "additionalValue1" };
        String[] expectedAdditionalValue2 = new String[] { "additionalValue2" };
        String[] expectedAdditionalValue3 = new String[] { "additionalValue3" };
        Set<String> actualKeySet = parameterMap.keySet();
        String[] actualExistingValue = parameterMap.get( expectedExistingKey );
        String[] actualAdditionalValue1 = parameterMap.get( expectedAdditionalKey1 );
        String[] actualAdditionalValue2 = parameterMap.get( expectedAdditionalKey2 );
        String[] actualAdditionalValue3 = parameterMap.get( expectedAdditionalKey3 );

        assertThat( parameterMap.size(), is( 4 ) );

        assertThat( actualKeySet, hasItem( expectedExistingKey ) );
        assertThat( actualKeySet, hasItem( expectedAdditionalKey1 ) );
        assertThat( actualKeySet, hasItem( expectedAdditionalKey2 ) );
        assertThat( actualKeySet, hasItem( expectedAdditionalKey3 ) );
        assertThat( actualExistingValue, is( expectedExistingValue ) );
        assertThat( actualAdditionalValue1, is( expectedAdditionalValue1 ) );
        assertThat( actualAdditionalValue2, is( expectedAdditionalValue2 ) );
        assertThat( actualAdditionalValue3, is( expectedAdditionalValue3 ) );
    }

    @Test
    public void testGetParameter()
                            throws Exception {
        KvpRequestWrapper kvpRequestWrapper = new KvpRequestWrapper( request, additionalKeyValuePair );
        String existingParameter = kvpRequestWrapper.getParameter( "existingKey" );
        String additionalParameter = kvpRequestWrapper.getParameter( "additionalKey" );

        assertThat( existingParameter, is( "existingValue" ) );
        assertThat( additionalParameter, is( "additionalValue" ) );
    }

    @Test
    public void testGetParameterWithNotExistingParameterShouldReturnNull()
                            throws Exception {
        KvpRequestWrapper kvpRequestWrapper = new KvpRequestWrapper( request, additionalKeyValuePair );
        String notExistingParameter = kvpRequestWrapper.getParameter( "notExisting" );

        assertThat( notExistingParameter, is( nullValue() ) );
    }

    @Test
    public void testGetParameterValues()
                            throws Exception {
        KvpRequestWrapper kvpRequestWrapper = new KvpRequestWrapper( request, additionalKeyValuePair );
        String[] existingParameterValues = kvpRequestWrapper.getParameterValues( "existingKey" );
        String[] additionalParameterValues = kvpRequestWrapper.getParameterValues( "additionalKey" );

        String[] expectedExistingParameterValues = new String[] { "existingValue" };
        String[] expectedAdditionalParameterValues = new String[] { "additionalValue" };

        assertThat( existingParameterValues, is( expectedExistingParameterValues ) );
        assertThat( additionalParameterValues, is( expectedAdditionalParameterValues ) );
    }

    @Test
    public void testGetParameterValuesWithNotExistingParameterShouldReturnNull()
                            throws Exception {
        KvpRequestWrapper kvpRequestWrapper = new KvpRequestWrapper( request, additionalKeyValuePair );
        String[] notExistingParameterValues = kvpRequestWrapper.getParameterValues( "netExisting" );

        assertThat( notExistingParameterValues, is( nullValue() ) );
    }

    @Test
    public void testGetParameterNames()
                            throws Exception {
        KvpRequestWrapper kvpRequestWrapper = new KvpRequestWrapper( request, additionalKeyValuePair );
        Enumeration<String> parameterNames = kvpRequestWrapper.getParameterNames();
        List<String> parameterNamesList = new ArrayList<String>();
        while ( parameterNames.hasMoreElements() ) {
            parameterNamesList.add( parameterNames.nextElement() );
        }

        assertThat( parameterNamesList.size(), is( 2 ) );
        assertThat( parameterNamesList, hasItem( "existingKey" ) );
        assertThat( parameterNamesList, hasItem( "additionalKey" ) );
    }

    @Test
    public void testGetParameterNamesWithThreeAdditionalEntries()
                            throws Exception {
        KvpRequestWrapper kvpRequestWrapper = new KvpRequestWrapper( request, additionalKeyValuePairsWithThreeEntries );
        Enumeration<String> parameterNames = kvpRequestWrapper.getParameterNames();
        List<String> parameterNamesList = new ArrayList<String>();
        while ( parameterNames.hasMoreElements() ) {
            parameterNamesList.add( parameterNames.nextElement() );
        }

        assertThat( parameterNamesList.size(), is( 4 ) );
        assertThat( parameterNamesList, hasItem( "existingKey" ) );
        assertThat( parameterNamesList, hasItem( "additionalKey1" ) );
        assertThat( parameterNamesList, hasItem( "additionalKey2" ) );
        assertThat( parameterNamesList, hasItem( "additionalKey3" ) );
    }

    private Map<String, String[]> createAdditionalKeyValuePair() {
        Map<String, String[]> additionalKeyValuePair = new HashMap<String, String[]>();
        additionalKeyValuePair.put( "additionalKey", new String[] { "additionalValue" } );
        return additionalKeyValuePair;
    }

    private Map<String, String[]> createAdditionalKeyValuePairsWithThreeEntries() {
        Map<String, String[]> additionalKeyValuePairs = new HashMap<String, String[]>();
        additionalKeyValuePairs.put( "additionalKey1", new String[] { "additionalValue1" } );
        additionalKeyValuePairs.put( "additionalKey2", new String[] { "additionalValue2" } );
        additionalKeyValuePairs.put( "additionalKey3", new String[] { "additionalValue3" } );
        return additionalKeyValuePairs;
    }

    private Map<String, String[]> createAdditionalKeyValuePairsWithTwoValues() {
        Map<String, String[]> additionalKeyValuePair = new HashMap<String, String[]>();
        additionalKeyValuePair.put( "additionalKey", new String[] { "additionalValue1", "additionalValue2" } );
        return additionalKeyValuePair;
    }

    private Map<String, String[]> createExistingParameterMap() {
        Map<String, String[]> existingParameterMap = new HashMap<String, String[]>();
        existingParameterMap.put( "existingKey", new String[] { "existingValue" } );
        return existingParameterMap;
    }

    private HttpServletRequest mockHttpServletRequest() {
        HttpServletRequest request = mock( HttpServletRequest.class );
        doReturn( "?existingKey=existingValue" ).when( request ).getQueryString();
        Map<String, String[]> existingParameterMap = createExistingParameterMap();
        doReturn( existingParameterMap ).when( request ).getParameterMap();
        return request;
    }

    private HttpServletRequest mockHttpServletRequestWithoutParams() {
        HttpServletRequest request = mock( HttpServletRequest.class );
        doReturn( null ).when( request ).getQueryString();
        Map<String, String[]> existingParameterMap = emptyMap();
        doReturn( existingParameterMap ).when( request ).getParameterMap();
        return request;
    }
}
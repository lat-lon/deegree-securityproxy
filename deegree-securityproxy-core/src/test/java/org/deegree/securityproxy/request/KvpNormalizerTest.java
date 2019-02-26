package org.deegree.securityproxy.request;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

/**
 * @author <a href="mailto:erben@lat-lon.de">Alexander Erben</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class KvpNormalizerTest {

    @Test
    public void testNormalizeKvpMapDifferentEntries()
                            throws Exception {
        Map<String, String[]> inputMap = new TreeMap<String, String[]>();
        inputMap.put( "key", new String[] { "val" } );
        inputMap.put( "key2", new String[] { "val2" } );
        Map<String, String[]> normalized = KvpNormalizer.normalizeKvpMap( inputMap );
        assertThat( inputMap, is( normalized ) );
    }

    @Test
    public void testNormalizeKvpMapDuplicateEntriesDifferentCase()
                            throws Exception {
        Map<String, String[]> inputMap = new TreeMap<>();
        inputMap.put( "key", new String[] { "val" } );
        inputMap.put( "Key", new String[] { "val2" } );
        Map<String, String[]> normalizedMap = KvpNormalizer.normalizeKvpMap( inputMap );
        Map<String, String[]> expectedMap = new HashMap<String, String[]>();
        expectedMap.put( "key", new String[] { "val", "val2" } );
        assertThat( normalizedMap.get( "key" ), is( expectedMap.get( "key" ) ) );
    }

}
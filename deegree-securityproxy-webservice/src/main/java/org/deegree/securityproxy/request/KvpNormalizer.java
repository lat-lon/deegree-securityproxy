package org.deegree.securityproxy.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.ArrayUtils;

public class KvpNormalizer {

    public static Map<String, String[]> normalizeKvpMap( Map<String, String[]> parameterMap ) {
        Map<String, String[]> normalizedMap = new HashMap<String, String[]>();
        for ( Entry<String, String[]> entry : parameterMap.entrySet() ) {
            String key = entry.getKey().toLowerCase();
            String[] value = entry.getValue();
            if ( !normalizedMap.containsKey( key ) ) {
                normalizedMap.put( key, value );
            } else {
                String[] newValue = (String[]) ArrayUtils.addAll( value, normalizedMap.get( key ) );
                normalizedMap.put( key, newValue );
            }
        }
        return normalizedMap;
    }
}

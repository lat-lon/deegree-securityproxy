package org.deegree.securityproxy.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

/**
 * Custom Request wrapper that allows to add additional key-value-pairs.
 *
 * @author <a href="stenger@lat-lon.de">Dirk Stenger</a>
 * @author <a href="wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author last edited by: $Author: stenger $
 * @version $Revision: $, $Date: $
 */
public class KvpRequestWrapper extends HttpServletRequestWrapper {

    private final Map<String, String[]> additionalParameters;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request
     * @throws IllegalArgumentException if the request is null
     */
    public KvpRequestWrapper( final HttpServletRequest request, final Map<String, String[]> additionalKeyValuePairs ) {
        super( request );
        additionalParameters = new TreeMap<String, String[]>();
        additionalParameters.putAll( additionalKeyValuePairs );
    }

    @Override
    public String getQueryString() {
        String originalQueryString = super.getQueryString();
        String additionalKvps = retrieveAdditionalKeyValuePairsAsString();
        return originalQueryString + additionalKvps;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> parameters = new TreeMap<String, String[]>();
        parameters.putAll( super.getParameterMap() );
        parameters.putAll( additionalParameters );
        return Collections.unmodifiableMap( parameters );
    }

    @Override
    public String getParameter( final String name ) {
        String[] strings = getParameterMap().get( name );
        if ( strings != null ) {
            return strings[0];
        }
        return super.getParameter( name );
    }

    @Override
    public String[] getParameterValues( final String name ) {
        return getParameterMap().get( name );
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration( getParameterMap().keySet() );
    }

    private String retrieveAdditionalKeyValuePairsAsString() {
        StringBuilder additionalKvp = new StringBuilder();
        for ( Map.Entry<String, String[]> entry : additionalParameters.entrySet() ) {
            String key = entry.getKey();
            String value = entry.getValue()[0];
            additionalKvp.append( "&" ).append( key ).append( "=" ).append( value );
        }
        return fixParametersIfOriginalQueryStringIsEmpty( additionalKvp.toString() );
    }

    private String fixParametersIfOriginalQueryStringIsEmpty( String kvps ) {
        if ( super.getQueryString() == null || super.getQueryString() == "" )
            return "?" + kvps.substring( 1 );
        return kvps;
    }

}

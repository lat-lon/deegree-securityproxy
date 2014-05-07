package org.deegree.securityproxy.filter;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

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
     * @throws IllegalArgumentException
     *             if the request is null
     */
    public KvpRequestWrapper( final HttpServletRequest request ) {
        this( request, new TreeMap<String, String[]>() );
    }

    /**
     * Constructs a request object wrapping the given request.
     * 
     * @param request
     * @throws IllegalArgumentException
     *             if the request is null
     */
    public KvpRequestWrapper( final HttpServletRequest request, final Map<String, String[]> additionalKeyValuePairs ) {
        super( request );
        additionalParameters = new TreeMap<String, String[]>();
        additionalParameters.putAll( additionalKeyValuePairs );
    }

    @Override
    public String getQueryString() {
        String originalQueryString = super.getQueryString();
        return addAdditionalKeyValuePairsAsString( originalQueryString );
    }

    @SuppressWarnings("unchecked")
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

    /**
     * Appends a new additional key value pair. If an entry with the same key already exists the existing key is
     * overwritten.
     * 
     * @param key
     *            of the entry, if <code>null</code> the entry is not added
     * @param value
     *            of the new entry
     */
    public void addParameter( String key, String value ) {
        if ( key != null )
            additionalParameters.put( key, new String[] { value } );
    }

    private String addAdditionalKeyValuePairsAsString( String originalQueryString ) {
        StringBuilder queryString = new StringBuilder();
        boolean appendAmp = appendBeginning( originalQueryString, queryString );
        for ( Map.Entry<String, String[]> entry : additionalParameters.entrySet() ) {
            String key = entry.getKey();
            String value = commaSeparatedValue( entry );
            if ( appendAmp )
                queryString.append( "&" );
            queryString.append( key ).append( "=" ).append( value );
            appendAmp = true;
        }
        return queryString.toString();
    }

    private boolean appendBeginning( String originalQueryString, StringBuilder queryString ) {
        if ( originalQueryString == null || originalQueryString.isEmpty() ) {
            queryString.append( '?' );
            return false;
        } else {
            queryString.append( originalQueryString );
            return true;
        }
    }

    private String commaSeparatedValue( Map.Entry<String, String[]> entry ) {
        StringBuilder valuesAsString = new StringBuilder();
        String[] values = entry.getValue();
        boolean appendComma = false;
        for ( String value : values ) {
            if ( appendComma )
                valuesAsString.append( ',' );
            valuesAsString.append( value );
            appendComma = true;
        }
        return valuesAsString.toString();
    }

}
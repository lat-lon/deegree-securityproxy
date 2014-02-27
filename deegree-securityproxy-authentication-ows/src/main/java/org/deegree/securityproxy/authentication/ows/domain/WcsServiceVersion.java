package org.deegree.securityproxy.authentication.ows.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Discriminates WCS service versions. All entries shall be prefixed with "VERSION_" and end with the service version
 * number without dots.
 */
public enum WcsServiceVersion {

    VERSION_100( "1.0.0" ), VERSION_110( "1.1.0" ), VERSION_200( "2.0.0" );

    private final String versionString;

    private WcsServiceVersion( String versionString ) {
        this.versionString = versionString;
    }

    public String getVersionString() {
        return versionString;
    }

    /**
     * Parses a version string with boolean operator into a {@link List} of {@link WcsServiceVersion}.
     * 
     * @param versionToParse
     *            may be <code>null</code>
     * 
     * @return a list of {@link WcsServiceVersion}. Empty if the parsing fails.
     */
    public static List<WcsServiceVersion> parseVersions( String versionToParse ) {
        if ( versionToParse != null ) {
            versionToParse = versionToParse.trim();
            if ( versionToParse.startsWith( ">=" ) || versionToParse.startsWith( "=>" ) )
                return greaterThanEqualsCheck( versionToParse );
            if ( versionToParse.startsWith( "<=" ) || versionToParse.startsWith( "=<" ) )
                return lowerThanEqualsCheck( versionToParse );
            if ( versionToParse.startsWith( "<" ) )
                return lowerThanCheck( versionToParse );
            if ( versionToParse.startsWith( ">" ) )
                return greaterThanCheck( versionToParse );
            if ( versionToParse.startsWith( "=" ) )
                return equalityCheck( versionToParse );
            WcsServiceVersion version = parseWithoutOperator( versionToParse );
            if ( version != null )
                return Collections.singletonList( version );
            else
                return Collections.emptyList();
        }
        return Collections.emptyList();
    }

    private static List<WcsServiceVersion> greaterThanCheck( String versionToParse ) {
        String versionString = stripBooleanOperators( versionToParse );
        versionString = versionString.trim();
        WcsServiceVersion version = parseWithoutOperator( versionString );
        if ( version == null )
            return Collections.emptyList();
        List<WcsServiceVersion> result = new ArrayList<WcsServiceVersion>();
        switch ( version ) {
        case VERSION_100:
            result.add( VERSION_110 );
            result.add( VERSION_200 );
            return result;
        case VERSION_110:
            return Collections.singletonList( VERSION_200 );
        case VERSION_200:
            return Collections.emptyList();
        default:
            return Collections.emptyList();
        }
    }

    private static List<WcsServiceVersion> lowerThanCheck( String versionToParse ) {
        String versionString = stripBooleanOperators( versionToParse );
        versionString = versionString.trim();
        WcsServiceVersion version = parseWithoutOperator( versionString );
        if ( version == null )
            return Collections.emptyList();
        List<WcsServiceVersion> result = new ArrayList<WcsServiceVersion>();
        switch ( version ) {
        case VERSION_100:
            return Collections.emptyList();
        case VERSION_110:
            return Collections.singletonList( VERSION_100 );
        case VERSION_200:
            result.add( VERSION_100 );
            result.add( VERSION_110 );
            return result;
        default:
            return Collections.emptyList();
        }
    }

    private static List<WcsServiceVersion> lowerThanEqualsCheck( String versionToParse ) {
        String versionString = stripBooleanOperators( versionToParse );
        versionString = versionString.trim();
        WcsServiceVersion version = parseWithoutOperator( versionString );
        if ( version == null )
            return Collections.emptyList();
        List<WcsServiceVersion> result = new ArrayList<WcsServiceVersion>();
        switch ( version ) {
        case VERSION_100:
            return Collections.singletonList( VERSION_100 );
        case VERSION_110:
            result.add( VERSION_100 );
            result.add( VERSION_110 );
            return result;
        case VERSION_200:
            result.add( VERSION_100 );
            result.add( VERSION_110 );
            result.add( VERSION_200 );
            return result;
        default:
            return Collections.emptyList();
        }
    }

    private static List<WcsServiceVersion> greaterThanEqualsCheck( String versionToParse ) {
        String versionString = stripBooleanOperators( versionToParse );
        versionString = versionString.trim();
        WcsServiceVersion version = parseWithoutOperator( versionString );
        if ( version == null )
            return Collections.emptyList();
        List<WcsServiceVersion> result = new ArrayList<WcsServiceVersion>();
        switch ( version ) {
        case VERSION_100:
            result.add( VERSION_100 );
            result.add( VERSION_110 );
            result.add( VERSION_200 );
            return result;
        case VERSION_110:
            result.add( VERSION_110 );
            result.add( VERSION_200 );
            return result;
        case VERSION_200:
            return Collections.singletonList( VERSION_200 );

        default:
            return Collections.emptyList();
        }
    }

    private static WcsServiceVersion parseWithoutOperator( String versionToParse ) {
        if ( "1.0.0".equals( versionToParse ) )
            return WcsServiceVersion.VERSION_100;
        if ( "1.1.0".equals( versionToParse ) )
            return WcsServiceVersion.VERSION_110;
        if ( "2.0.0".equals( versionToParse ) )
            return WcsServiceVersion.VERSION_200;
        return null;
    }

    private static List<WcsServiceVersion> equalityCheck( String versionToParse ) {
        String versionString = stripBooleanOperators( versionToParse );
        versionString = versionString.trim();
        WcsServiceVersion version = parseWithoutOperator( versionString );
        if ( version == null )
            return Collections.emptyList();
        switch ( version ) {
        case VERSION_100:
            return Collections.singletonList( VERSION_100 );
        case VERSION_110:
            return Collections.singletonList( VERSION_110 );
        case VERSION_200:
            return Collections.singletonList( VERSION_200 );
        default:
            return Collections.emptyList();
        }
    }

    private static String stripBooleanOperators( String versionToParse ) {
        versionToParse = StringUtils.replace( versionToParse, "=", "" );
        versionToParse = StringUtils.replace( versionToParse, "<", "" );
        versionToParse = StringUtils.replace( versionToParse, ">", "" );
        return versionToParse;
    }

}
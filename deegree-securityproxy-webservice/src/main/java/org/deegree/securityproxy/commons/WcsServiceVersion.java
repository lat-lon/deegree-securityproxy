package org.deegree.securityproxy.commons;

import java.util.ArrayList;
import java.util.List;

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

    public static List<WcsServiceVersion> parseVersions( String versionToParse ) {
        List<WcsServiceVersion> parsedVersions = new ArrayList<WcsServiceVersion>();
        if ( versionToParse != null ) {

        }
        return parsedVersions;
    }
    
}
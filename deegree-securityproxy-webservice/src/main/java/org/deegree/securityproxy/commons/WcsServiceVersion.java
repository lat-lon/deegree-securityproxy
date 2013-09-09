package org.deegree.securityproxy.commons;

/**
 * Discriminates WCS service versions. All entries shall be prefixed with "VERSION_" and end with the service version
 * number without dots.
 */
public enum WcsServiceVersion {

    VERSION_100( "1.0.0" ), VERSION_110( "1.1.0" ), VERSION_130( "1.3.0" );

    private final String versionString;

    private WcsServiceVersion( String versionString ) {
        this.versionString = versionString;
    }

    public String getVersionString() {
        return versionString;
    }

}
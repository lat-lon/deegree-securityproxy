package org.deegree.securityproxy.commons;

/**
 * Discriminates WCS service versions. All entries shall be prefixed with "VERSION_" and end with the service version
 * number without dots.
 */
public enum WcsServiceVersion {
    VERSION_100, VERSION_110, VERSION_130;
}
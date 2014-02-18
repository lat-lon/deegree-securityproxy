package org.deegree.securityproxy.domain;

/**
 * Discriminates WCS service operation types common to all WCS versions.
 */
public enum WcsOperationType {
    GETCAPABILITIES, GETCOVERAGE, DESCRIBECOVERAGE;
}
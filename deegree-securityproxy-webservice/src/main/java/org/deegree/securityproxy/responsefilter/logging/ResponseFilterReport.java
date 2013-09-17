package org.deegree.securityproxy.responsefilter.logging;

/**
 * 
 * @author stenger
 * 
 */
public class ResponseFilterReport {

    private final String message;

    private final boolean isFiltered;

    public ResponseFilterReport( String message, boolean isFiltered ) {
        this.message = message;
        this.isFiltered = isFiltered;
    }

    public String getMessage() {
        return message;
    }

    public boolean isFiltered() {
        return isFiltered;
    }
}

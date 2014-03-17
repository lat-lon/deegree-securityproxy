package org.deegree.securityproxy.responsefilter.logging;

/**
 * Encapsulates detailed information about the capabilities filtering.
 * 
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * 
 * @version $Revision: $, $Date: $
 */
public class ResponseCapabilitiesReport implements ResponseFilterReport {

    private String message;

    private boolean isFiltered;

    /**
     * Instantiates a new {@link ResponseCapabilitiesReport} with detailed information about the capabilities filtering.
     * 
     * @param message
     *            message gives information about the capabilities filtering
     * @param isFiltered
     *            if clipping was required or not
     */
    public ResponseCapabilitiesReport( String message, boolean isFiltered ) {
        this.message = message;
        this.isFiltered = isFiltered;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public boolean isFiltered() {
        return isFiltered;
    }

    @Override
    public String toString() {
        return getMessage();
    }

}

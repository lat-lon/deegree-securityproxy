package org.deegree.securityproxy.responsefilter.logging;

/**
 * Encapsulates detailed information about the capabilities filtering.
 * 
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * 
 * @version $Revision: $, $Date: $
 */
public class DefaultResponseFilterReport implements ResponseFilterReport {

    private String message;

    private boolean isFiltered;

    private boolean isFailed;

    /**
     * Instantiates a new {@link DefaultResponseFilterReport} with an error message, isFiltered is set to
     * <code>false</code>.
     * 
     * @param failureMessage
     *            the error message, may not be <code>null</code>
     * @throws IllegalArgumentException
     *             if required parameter is null
     */
    public DefaultResponseFilterReport( String failureMessage ) {
        if ( failureMessage == null )
            throw new IllegalArgumentException( "failureMessage must not be null!" );
        this.message = failureMessage;
        this.isFiltered = false;
        this.isFailed = true;
    }

    /**
     * Instantiates a new {@link DefaultResponseFilterReport} with detailed information about the capabilities filtering.
     * 
     * @param message
     *            message gives information about the capabilities filtering, never <code>null</code>
     * @param isFiltered
     *            if filtering was required or not
     * @throws IllegalArgumentException
     *             if required parameter is null
     */
    public DefaultResponseFilterReport( String message, boolean isFiltered ) {
        if ( message == null )
            throw new IllegalArgumentException( "message must not be null!" );
        this.message = message;
        this.isFiltered = isFiltered;
        this.isFailed = false;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public boolean isFailed() {
        return isFailed;
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

package org.deegree.securityproxy.responsefilter.logging;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Encapsulates detailed information about the clipping result (geometry as well as failures).
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class ResponseClippingReport extends DefaultResponseFilterReport {

    private final Geometry returnedVisibleArea;

    /**
     * Instantiates a new {@link ResponseClippingReport} with an error message, the geometry is set to <code>null</code>
     * , isFiltered to <code>false</code>
     * 
     * @param failure
     *            the error message, may not be <code>null</code>
     * @throws IllegalArgumentException
     *             if required parameter is null
     */
    public ResponseClippingReport( String failure ) {
        super( failure );
        this.returnedVisibleArea = null;
    }

    /**
     * Instantiates a new {@link ResponseClippingReport} with the resulting visible area and the information if clipping
     * was required
     * 
     * @param returnedVisibleArea
     *            never <code>null</code>
     * @param isFiltered
     *            if clipping was required or not
     * @throws IllegalArgumentException
     *             if required parameter is null
     */
    public ResponseClippingReport( Geometry returnedVisibleArea, boolean isFiltered ) {
        super( createMessage( returnedVisibleArea, isFiltered ), isFiltered );
        if ( returnedVisibleArea == null )
            throw new IllegalArgumentException( "returnedVisibleArea must not be null!" );
        this.returnedVisibleArea = returnedVisibleArea;
    }

    private static String createMessage( Geometry returnedVisibleArea, boolean isFiltered ) {
        return "Image was " + ( isFiltered ? "" : "not " ) + "clipped. Returned visible area: " + returnedVisibleArea;
    }

    /**
     * @return the returnedVisibleArea <code>null</code> if an error occurred (failure is not null)
     */
    public Geometry getReturnedVisibleArea() {
        return returnedVisibleArea;
    }

}

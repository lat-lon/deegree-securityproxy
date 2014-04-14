package org.deegree.securityproxy.service.commons.responsefilter;

import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.responsefilter.ResponseFilterManager;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by stenger on 14.04.14.
 */
public abstract class AbstractResponseFilterManager implements ResponseFilterManager {
    @Override
    public boolean canBeFiltered( OwsRequest request ) {
        checkIfRequestIsNull( request );
        return isCorrectServiceType( request ) && isCorrectRequestParameter( request );
    }

    /**
     * @param request
     *            never <code>null</code>
     * @return true if request is from the supported type
     */
    protected abstract boolean isCorrectServiceType( OwsRequest request );

    /**
     * @param owsRequest
     *            never <code>null</code>
     * @return true if request is a supported request
     */
    protected abstract boolean isCorrectRequestParameter( OwsRequest owsRequest );

    protected void checkIfRequestIsNull( OwsRequest request ) {
        if ( request == null )
            throw new IllegalArgumentException( "Request must not be null!" );
    }

    protected void checkParameters( HttpServletResponse servletResponse, OwsRequest request ) {
        if ( servletResponse == null )
            throw new IllegalArgumentException( "Parameter servletResponse may not be null!" );
        if ( request == null )
            throw new IllegalArgumentException( "Parameter request may not be null!" );
        if ( !isCorrectServiceType( request ) )
            throw new IllegalArgumentException( "OwsRequest of class " + request.getClass().getCanonicalName()
                                                + " is not supported!" );
    }
}

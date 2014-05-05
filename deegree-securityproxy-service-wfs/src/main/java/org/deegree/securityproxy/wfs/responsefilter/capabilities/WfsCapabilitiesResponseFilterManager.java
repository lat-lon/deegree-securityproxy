package org.deegree.securityproxy.wfs.responsefilter.capabilities;

import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.AbstractCapabilitiesResponseFilterManager;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.CapabilitiesFilter;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.DecisionMakerCreator;
import org.deegree.securityproxy.wfs.request.WfsRequest;

import static org.deegree.securityproxy.wfs.request.WfsGetRequestParser.GETCAPABILITIES;

/**
 * {@link org.deegree.securityproxy.responsefilter.ResponseFilterManager} filtering wfs capabilities documents.
 * 
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * 
 * @version $Revision: $, $Date: $
 */
public class WfsCapabilitiesResponseFilterManager extends AbstractCapabilitiesResponseFilterManager {

    /**
     * @param capabilitiesFilter
     *            used to filter the capabilities, never <code>null</code>
     * @param decisionMakerCreator
     *            used to create the
     *            {@link org.deegree.securityproxy.service.commons.responsefilter.capabilities.DecisionMaker}, never
     *            <code>null</code>
     */
    public WfsCapabilitiesResponseFilterManager( CapabilitiesFilter capabilitiesFilter,
                                                 DecisionMakerCreator decisionMakerCreator ) {
        super( capabilitiesFilter, decisionMakerCreator );
    }

    @Override
    protected boolean isCorrectServiceType( OwsRequest owsRequest ) {
        return WfsRequest.class.equals( owsRequest.getClass() );
    }

    @Override
    protected boolean isCorrectRequestParameter( OwsRequest owsRequest ) {
        return GETCAPABILITIES.equalsIgnoreCase( owsRequest.getOperationType() );
    }

}

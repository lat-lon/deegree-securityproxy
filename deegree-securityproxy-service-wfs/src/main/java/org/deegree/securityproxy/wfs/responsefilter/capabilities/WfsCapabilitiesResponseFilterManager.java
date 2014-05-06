package org.deegree.securityproxy.wfs.responsefilter.capabilities;

import static org.deegree.securityproxy.wfs.request.WfsGetRequestParser.GETCAPABILITIES;

import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.AbstractCapabilitiesResponseFilterManager;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.DecisionMaker;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.XmlModificationManagerCreator;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.XmlFilter;
import org.deegree.securityproxy.wfs.request.WfsRequest;

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
     *            used to create the {@link DecisionMaker}, never <code>null</code>
     */
    public WfsCapabilitiesResponseFilterManager( XmlFilter capabilitiesFilter, XmlModificationManagerCreator decisionMakerCreator ) {
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

package org.deegree.securityproxy.authentication.ows.domain;

import org.deegree.securityproxy.request.OwsServiceVersion;

/**
 * Implementation of {@link LimitedServiceVersion} which contains all {@link OwsServiceVersion}s.
 *
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * 
 * @version $Revision: $, $Date: $
 */
public class UnlimitedServiceVersion implements LimitedServiceVersion {

    @Override
    public boolean contains( OwsServiceVersion version ) {
        return true;
    }

}
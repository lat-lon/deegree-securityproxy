package org.deegree.securityproxy.authentication.ows.domain;

import org.deegree.securityproxy.request.OwsServiceVersion;

/**
 * Interface for limited service version. Can be used to check whether a service version contains another version.
 *
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 *
 * @version $Revision: $, $Date: $
 */
public interface LimitedServiceVersion {

    /**
     * Checks if service version contains another version.
     * 
     * @param version
     *            never <code>null</code>
     * @return true if {@link LimitedServiceVersion} contains {@link OwsServiceVersion}, false otherwise
     */
    boolean contains( OwsServiceVersion version );

}
package org.deegree.securityproxy.wps;

import org.deegree.securityproxy.authorization.RequestAuthorizationManager;
import org.deegree.securityproxy.exception.ServiceExceptionWrapper;
import org.deegree.securityproxy.filter.ServiceManager;
import org.deegree.securityproxy.filter.ServiceManagerAbstractTest;
import org.deegree.securityproxy.request.parser.OwsRequestParser;

import java.util.List;

import static org.mockito.Mockito.mock;

/**
 * Interface tests of {@link org.deegree.securityproxy.filter.ServiceManager} for
 * {@link org.deegree.securityproxy.wps.WpsServiceManager}.
 *
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 *
 * @version $Revision: $, $Date: $
 */
public class WpsServiceManagerIServiceManagerTest extends ServiceManagerAbstractTest {

    @Override
    protected ServiceManager createServiceManager() {
        return new WpsServiceManager( mock( OwsRequestParser.class ), mock( RequestAuthorizationManager.class ),
                        mock( List.class ), mock( ServiceExceptionWrapper.class ) );
    }

}
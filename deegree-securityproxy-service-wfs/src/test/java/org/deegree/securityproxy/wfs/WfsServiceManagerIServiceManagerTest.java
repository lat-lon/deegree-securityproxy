package org.deegree.securityproxy.wfs;

import org.deegree.securityproxy.exception.ServiceExceptionWrapper;
import org.deegree.securityproxy.filter.ServiceManager;
import org.deegree.securityproxy.filter.ServiceManagerAbstractTest;
import org.deegree.securityproxy.request.parser.OwsRequestParser;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;

/**
 * Interface tests of {@link org.deegree.securityproxy.filter.ServiceManager} for
 * {@link org.deegree.securityproxy.wfs.WfsServiceManager}.
 *
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 *
 * @version $Revision: $, $Date: $
 */
public class WfsServiceManagerIServiceManagerTest extends ServiceManagerAbstractTest {

    @Override
    protected ServiceManager createServiceManager() {
        return new WfsServiceManager( mock( OwsRequestParser.class ), mock( List.class ),
                        mock( ServiceExceptionWrapper.class ), mock( Map.class ) );
    }

}

package org.deegree.securityproxy.filter;

import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.mock;

/**
 * Abstract tests for {@link org.deegree.securityproxy.filter.ServiceManager}.
 *
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 *
 * @version $Revision: $, $Date: $
 */
public abstract class ServiceManagerAbstractTest {

    @Test
    public void testIsServiceTypeSupportedWithNullServiceType()
                    throws Exception {
        createServiceManager().isServiceTypeSupported( null, mock( HttpServletRequest.class ) );
    }

    protected abstract ServiceManager createServiceManager();

}
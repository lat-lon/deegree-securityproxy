package org.deegree.securityproxy.logging;

import org.deegree.securityproxy.logger.Log4JReportLogger;
import org.deegree.securityproxy.logger.ProxyReportLogger;
import org.junit.Test;

/**
 * Tests for {@link Log4JReportLogger}
 * 
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author <a href="goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
public class Log4JReportLoggerTest {

    @Test(expected = IllegalArgumentException.class)
    public void testReportLoggerLogInfoShouldThrowIllegalArgumentExceptionOnNullReport() {
        ProxyReportLogger logger = new Log4JReportLogger();
        logger.logProxyReportInfo( null );
    }
}

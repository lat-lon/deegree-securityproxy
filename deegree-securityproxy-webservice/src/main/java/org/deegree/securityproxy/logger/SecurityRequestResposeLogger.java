package org.deegree.securityproxy.logger;

import org.deegree.securityproxy.report.SecurityReport;

/**
 * Interface for loggers that can handle the logging of {@link SecurityReport}s.
 * 
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author <a href="goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
public interface SecurityRequestResposeLogger {

    /**
     * Log a {@link SecurityReport} on log level INFO
     * 
     * @param report never <code>null</code>
     * @throws IllegalArgumentException if report is <code>null</code>
     */
    public void logProxyReportInfo(SecurityReport report) throws IllegalArgumentException;
}

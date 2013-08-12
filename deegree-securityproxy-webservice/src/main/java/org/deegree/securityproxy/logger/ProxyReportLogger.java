package org.deegree.securityproxy.logger;

import org.deegree.securityproxy.report.ProxyReport;

/**
 * Interface for loggers that can handle the logging of {@link ProxyReport}s.
 * 
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
public interface ProxyReportLogger {

    /**
     * Log a {@link ProxyReport} on log level INFO
     * 
     * @param report never <code>null</code>
     * @throws IllegalArgumentException if report is <code>null</code>
     */
    public void logProxyReportInfo(ProxyReport report) throws IllegalArgumentException;
}

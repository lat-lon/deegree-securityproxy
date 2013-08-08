package org.deegree.securityproxy.logger;

import org.apache.log4j.Logger;
import org.deegree.securityproxy.report.ProxyReport;

/**
 * This implementation of {@link ProxyReportLogger} uses Apache Log4J as logging framework
 * 
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
public class Log4JReportLogger implements ProxyReportLogger {

    static Logger log = Logger.getLogger( "ProxyLogger" );
                            
    @Override
    public void logProxyReportInfo( ProxyReport report )
                            throws IllegalArgumentException {
        if ( report == null )
            throw new IllegalArgumentException( "ProxyReport must not be null!" );
        log.info( report.toString() );
    }

}

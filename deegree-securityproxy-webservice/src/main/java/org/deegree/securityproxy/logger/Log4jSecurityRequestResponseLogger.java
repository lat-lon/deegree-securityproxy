package org.deegree.securityproxy.logger;

import static java.io.File.separator;
import static java.lang.System.getenv;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.deegree.securityproxy.report.SecurityReport;

/**
 * This implementation of {@link SecurityRequestResposeLogger} uses Apache Log4J as logging framework
 * 
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author <a href="goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
public class Log4jSecurityRequestResponseLogger implements SecurityRequestResposeLogger {

    private static Logger log = Logger.getLogger( "ProxyLogger" );

    private String proxyConfEnv;

    private String log4JFileName;
                          
    public Log4jSecurityRequestResponseLogger (String proxyConfEnv, String log4JFileName) {
    	this.proxyConfEnv = proxyConfEnv;
    	this.log4JFileName = log4JFileName;
    	configureLogging();
    }
    
    public Log4jSecurityRequestResponseLogger () {
        this.proxyConfEnv = "PROXY_CONF";
        this.log4JFileName = "log4j.properties";
        configureLogging();
    }
    
    @Override
    public void logProxyReportInfo( SecurityReport report )
                            throws IllegalArgumentException {
        if ( report == null )
            throw new IllegalArgumentException( "ProxyReport must not be null!" );
        log.info( report.toString() );
    }

    private void configureLogging() {
        String log4jConfigurationPath = buildLog4JConfigurationPath();
        if ( log4jConfigurationPath != null ) {
            PropertyConfigurator.configure( log4jConfigurationPath );
        } else {
            log.warn( "Could not retrieve log4j.properties from configuration directory. Please set the value of PROXY_CONFIG environment variable and place the log4j.properties in it." );
        }
    }

    private String buildLog4JConfigurationPath() {
        String path = getenv( proxyConfEnv );
        if ( path != null ) {
            StringBuilder builder = new StringBuilder( path );
            if ( !path.endsWith( separator ) )
                builder.append( separator );
            return builder.append( log4JFileName ).toString();
        }
        return null;
    }
    
}

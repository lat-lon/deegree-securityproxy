package org.deegree.securityproxy;

import static java.io.File.createTempFile;
import static org.apache.commons.io.IOUtils.copy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Checks if the applicationContext.xml could be loaded successful.
 * 
 * Therefore a new temp directory is created as configuration directory. The required properties file is copied into
 * this and the system property 'PROXY_CONFIG' is set to this configuration directory. After the test is finshed the
 * property is set to the old value.
 * 
 * @author <a href="goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
public class LoadApplicationContextTest {

    private static final String CONFIG_PROPERTIES_FILE_NAME = "config.properties";

    private static final String PROXY_CONFIG_PROPERTY_KEY = "PROXY_CONFIG";

    private static String oldProperty;

    @BeforeClass
    public static void copyPropertiesFileAndSetProxyConfigSystemVaraiable()
                            throws IOException {
        File configDir = copyPropertiesFileToNewConfigDir();
        oldProperty = System.getProperty( PROXY_CONFIG_PROPERTY_KEY );
        System.setProperty( PROXY_CONFIG_PROPERTY_KEY, configDir.toString() );
    }

    @AfterClass
    public static void resetProxyConfigSystemProperty() {
        if ( oldProperty != null )
            System.setProperty( PROXY_CONFIG_PROPERTY_KEY, oldProperty );
    }

    @Test
    public void testLoadApplicationContext() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext( "applicationContext.xml", "authenticationContext.xml" );
        ctx.close();
    }

    private static File copyPropertiesFileToNewConfigDir()
                            throws IOException {
        File configDirectory = createConfigDirectory();
        File configPropertiesFile = new File( configDirectory, CONFIG_PROPERTIES_FILE_NAME );
        FileOutputStream fileOutputStream = new FileOutputStream( configPropertiesFile );

        InputStream testConfigProperties = LoadApplicationContextTest.class.getResourceAsStream( "test_config.properties" );
        copy( testConfigProperties, fileOutputStream );

        testConfigProperties.close();
        fileOutputStream.close();

        return configDirectory;
    }

    private static File createConfigDirectory()
                            throws IOException {
        File configDirectory = createTempFile( "blackbridge-config", "" );
        configDirectory.delete();
        configDirectory.mkdir();
        return configDirectory;
    }

}
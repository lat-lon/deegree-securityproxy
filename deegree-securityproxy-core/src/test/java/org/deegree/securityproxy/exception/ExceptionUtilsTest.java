package org.deegree.securityproxy.exception;

import static org.deegree.securityproxy.exception.ExceptionUtils.readExceptionBodyFromFile;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class ExceptionUtilsTest {

    private static final String PATH_TO_EXCEPTION_FILE = "/ogc_wcs_100_serviceexception.xml";

    @Test
    public void testReadExceptionBodyFromFileBothNull()
                            throws Exception {
        String exception = readExceptionBodyFromFile( null, null );
        assertThat( exception, is( nullValue() ) );
    }

    @Test
    public void testReadExceptionBodyFromFileUnknownPathNullDefault()
                            throws Exception {
        String exception = readExceptionBodyFromFile( "file:///Path/To/Unknown", null );
        assertThat( exception, is( nullValue() ) );
    }

    @Test
    public void testReadExceptionBodyFromFilePathNullDefaultBody()
                            throws Exception {
        String defaultBody = "default";
        String exception = readExceptionBodyFromFile( null, defaultBody );
        assertThat( exception, is( defaultBody ) );
    }

    @Test
    public void testReadExceptionBodyFromFileUnknownPathDefaultBody()
                            throws Exception {
        String defaultBody = "default";
        String exception = readExceptionBodyFromFile( "file:///Path/To/Unknown", defaultBody );
        assertThat( exception, is( defaultBody ) );
    }

    @Test
    public void testReadExceptionBodyFromFileKnownPathDefaultBody()
                            throws Exception {
        String exception = readExceptionBodyFromFile( getPathToException(), "defaultBody" );
        assertThat( exception, is( responseBodyFromFile() ) );
    }

    private String getPathToException() {
        return ServiceExceptionHandlerTest.class.getResource( PATH_TO_EXCEPTION_FILE ).getPath();
    }

    private String responseBodyFromFile()
                            throws IOException {
        InputStream resourceAsStream = ServiceExceptionHandlerTest.class.getResourceAsStream( PATH_TO_EXCEPTION_FILE );
        return IOUtils.toString( resourceAsStream );
    }

}
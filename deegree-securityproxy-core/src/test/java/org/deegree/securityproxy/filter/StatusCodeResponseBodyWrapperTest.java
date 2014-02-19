package org.deegree.securityproxy.filter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

/**
 * @author <a href="stenger@lat-lon.de">Dirk Stenger</a>
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
public class StatusCodeResponseBodyWrapperTest {

    @Test
    public void testGetBufferBodyForWriter()
                            throws Exception {
        HttpServletResponse mockResponse = mockResponse();
        StatusCodeResponseBodyWrapper wrapper = new StatusCodeResponseBodyWrapper( mockResponse );
        PrintWriter writer = wrapper.getWriter();
        writer.write( 1 );
        assertThat( (int) wrapper.getBufferedBody()[0], is( 1 ) );
    }

    @Test
    public void testgetBufferBodyForOutputStream()
                            throws Exception {
        HttpServletResponse mockResponse = mockResponse();
        StatusCodeResponseBodyWrapper wrapper = new StatusCodeResponseBodyWrapper( mockResponse );
        ServletOutputStream outputStream = wrapper.getOutputStream();
        outputStream.write( 1 );
        assertThat( (int) wrapper.getBufferedBody()[0], is( 1 ) );
    }

    @Test
    public void testCopyBufferedStreamToRealStreamWithWriter()
                            throws Exception {
        String textToWrite = "TEXT";
        ByteArrayOutputStream realStream = new ByteArrayOutputStream();
        HttpServletResponse mockResponse = mockResponse( realStream );

        StatusCodeResponseBodyWrapper wrapper = new StatusCodeResponseBodyWrapper( mockResponse );
        PrintWriter writer = wrapper.getWriter();
        writer.write( textToWrite );

        wrapper.copyBufferedStreamToRealStream();

        String writtenTextToRealStream = realStream.toString();
        assertThat( writtenTextToRealStream, is( textToWrite ) );
    }

    @Test
    public void testCopyBufferedStreamToRealStreamWithStream()
                            throws Exception {
        String textToWrite = "TEXT";
        ByteArrayOutputStream realStream = new ByteArrayOutputStream();
        HttpServletResponse mockResponse = mockResponse( realStream );

        StatusCodeResponseBodyWrapper wrapper = new StatusCodeResponseBodyWrapper( mockResponse );
        ServletOutputStream writer = wrapper.getOutputStream();
        writer.print( textToWrite );

        wrapper.copyBufferedStreamToRealStream();

        String writtenTextToRealStream = realStream.toString();
        assertThat( writtenTextToRealStream, is( textToWrite ) );
    }

    @Test
    public void testGetRealStreamWithoutCopyShouldBeEmpty()
                            throws Exception {
        String textToWrite = "TEXT";
        ByteArrayOutputStream realStream = new ByteArrayOutputStream();
        HttpServletResponse mockResponse = mockResponse( realStream );

        StatusCodeResponseBodyWrapper wrapper = new StatusCodeResponseBodyWrapper( mockResponse );
        ServletOutputStream writer = wrapper.getOutputStream();
        writer.print( textToWrite );

        String writtenTextToRealStream = realStream.toString();
        assertThat( writtenTextToRealStream, is( "" ) );
    }

    private HttpServletResponse mockResponse()
                            throws IOException {
        HttpServletResponse mock = mock( HttpServletResponse.class );
        when( mock.getWriter() ).thenReturn( new PrintWriter( new ByteArrayOutputStream() ) );
        when( mock.getOutputStream() ).thenReturn( new ServletOutputStream() {

            private final OutputStream inner = new ByteArrayOutputStream();

            @Override
            public void write( int b )
                                    throws IOException {
                inner.write( b );
            }
        } );
        return mock;
    }

    private HttpServletResponse mockResponse( final ByteArrayOutputStream realStream )
                            throws IOException {
        HttpServletResponse mock = mock( HttpServletResponse.class );
        when( mock.getWriter() ).thenReturn( new PrintWriter( new ByteArrayOutputStream() ) );
        when( mock.getOutputStream() ).thenReturn( new ServletOutputStream() {

            @Override
            public void write( int b )
                                    throws IOException {
                realStream.write( b );
            }
        } );
        return mock;
    }
}
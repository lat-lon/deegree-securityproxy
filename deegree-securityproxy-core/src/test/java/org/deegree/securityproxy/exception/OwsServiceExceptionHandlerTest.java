package org.deegree.securityproxy.exception;

import static org.deegree.securityproxy.exception.OwsCommonException.INVALID_PARAMETER;
import static org.deegree.securityproxy.exception.OwsServiceExceptionHandler.DEFAULT_BODY;
import static org.deegree.securityproxy.exception.OwsServiceExceptionHandler.DEFAULT_STATUS_CODE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;

import org.deegree.securityproxy.filter.StatusCodeResponseBodyWrapper;
import org.junit.Test;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class OwsServiceExceptionHandlerTest {

    private static final int STATUS_CODE = 404;

    private static final String PATH_TO_EXCEPTION_FILE = "text-exception.txt";

    private final OwsServiceExceptionHandler defaultExceptionHandler = new OwsServiceExceptionHandler();

    private final OwsServiceExceptionHandler exceptionHandler = new OwsServiceExceptionHandler( getPathToException(),
                                                                                                STATUS_CODE );

    @Test
    public void testWriteExceptionDefaultHandler()
                            throws Exception {
        StatusCodeResponseBodyWrapper response = mockResponse();

        defaultExceptionHandler.writeException( response, null, null );

        verify( response ).setStatus( DEFAULT_STATUS_CODE );
        verify( response.getWriter() ).write( DEFAULT_BODY );
    }

    @Test
    public void testWriteExceptionHandlerWithExceptionCodeAndMessade()
                            throws Exception {
        StatusCodeResponseBodyWrapper response = mockResponse();

        exceptionHandler.writeException( response, INVALID_PARAMETER, "msg" );

        verify( response ).setStatus( STATUS_CODE );
        verify( response.getWriter() ).write( "Code:" + INVALID_PARAMETER.getExceptionCode() + "Msg:Code:msg" );
    }

    @Test
    public void testWriteExceptionHandlerWithExceptionCode()
                            throws Exception {
        StatusCodeResponseBodyWrapper response = mockResponse();

        exceptionHandler.writeException( response, INVALID_PARAMETER, null );

        verify( response ).setStatus( STATUS_CODE );
        verify( response.getWriter() ).write( "Code:" + INVALID_PARAMETER.getExceptionCode() + "Msg:Code:" );
    }

    @Test
    public void testWriteExceptionHandlerWithExceptionMessage()
                            throws Exception {
        StatusCodeResponseBodyWrapper response = mockResponse();

        exceptionHandler.writeException( response, null, "msg" );

        verify( response ).setStatus( STATUS_CODE );
        verify( response.getWriter() ).write( "Code:Msg:Code:msg" );
    }

    private StatusCodeResponseBodyWrapper mockResponse() {
        StatusCodeResponseBodyWrapper mock = mock( StatusCodeResponseBodyWrapper.class );
        try {
            when( mock.getWriter() ).thenReturn( mock( PrintWriter.class ) );
        } catch ( Exception e ) {
        }
        return mock;
    }

    private String getPathToException() {
        return ServiceExceptionHandlerTest.class.getResource( PATH_TO_EXCEPTION_FILE ).getPath();
    }

}
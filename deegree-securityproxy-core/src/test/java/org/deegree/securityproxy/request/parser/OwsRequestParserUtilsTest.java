package org.deegree.securityproxy.request.parser;

import static org.deegree.securityproxy.request.parser.OwsRequestParserUtils.checkRequiredParameter;
import static org.deegree.securityproxy.request.parser.OwsRequestParserUtils.checkSingleRequiredParameter;
import static org.deegree.securityproxy.request.parser.OwsRequestParserUtils.evaluateServiceName;
import static org.deegree.securityproxy.request.parser.OwsRequestParserUtils.isNotSet;
import static org.deegree.securityproxy.request.parser.OwsRequestParserUtils.isNotSingle;
import static org.deegree.securityproxy.request.parser.OwsRequestParserUtils.throwException;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class OwsRequestParserUtilsTest {

    private static final String SINGLE = "single";

    private static final String MULTIPLE = "multiple";

    private static final String NOT_IN_MAP = "notinmap";

    private static final String[] SINGLE_VALUE = new String[] { "one" };

    private static final String[] MULTIPLE_VALUE = new String[] { "a", "b" };

    private static final String[] EMPTY_VALUE = new String[] {};

    private static final String[] NULL_VALUE = null;

    private Map<String, String[]> normalizedParameterMap = createTestParameterMap();

    private static final String SERVICE_NAME = "serviceName";

    private static final String SERVICE_NAME_WITH_PATH = "path/" + SERVICE_NAME;

    @Test
    public void testCheckSingleRequiredParameter() {
        String parameterValue = checkSingleRequiredParameter( normalizedParameterMap, SINGLE );
        assertThat( parameterValue, is( SINGLE_VALUE[0] ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckSingleRequiredParameterFromMultipleShouldFail() {
        checkSingleRequiredParameter( normalizedParameterMap, MULTIPLE );
    }

    @Test
    public void testCheckRequiredParameter() {
        String[] parameter = checkRequiredParameter( normalizedParameterMap, SINGLE );
        assertThat( parameter, is( SINGLE_VALUE ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckRequiredParameterNotExistShouldFail() {
        checkRequiredParameter( normalizedParameterMap, NOT_IN_MAP );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowExceptionWithName() {
        throwException( "paramName" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowExceptionWithNameAndValue() {
        throwException( "paramName", new String[] { "paramValue" } );
    }

    @Test
    public void testIsNotSetFromMultipleValueShouldReturnFalse() {
        boolean isNotSet = isNotSet( MULTIPLE_VALUE );
        assertThat( isNotSet, is( false ) );
    }

    @Test
    public void testIsNotSetFromEmptyValueShouldReturnTrue() {
        boolean isNotSet = isNotSet( EMPTY_VALUE );
        assertThat( isNotSet, is( true ) );
    }

    @Test
    public void testIsNotSetFromNullValueShouldReturnTrue() {
        boolean isNotSet = isNotSet( NULL_VALUE );
        assertThat( isNotSet, is( true ) );
    }

    @Test
    public void testIsNotSingleFromSingleValueShouldReturnTrue() {
        boolean isNotSingle = isNotSingle( SINGLE_VALUE );
        assertThat( isNotSingle, is( false ) );
    }

    @Test
    public void testIsNotSingleFromMultipleValueShouldReturnTrue() {
        boolean isNotSingle = isNotSingle( MULTIPLE_VALUE );
        assertThat( isNotSingle, is( true ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsNotSingleFromEmptyValueShouldFail() {
        boolean isNotSingle = isNotSingle( EMPTY_VALUE );
        assertThat( isNotSingle, is( true ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsNotSingleFromNullValueShouldFail() {
        boolean isNotSingle = isNotSingle( NULL_VALUE );
        assertThat( isNotSingle, is( true ) );
    }

    @Test
    public void testEvaluateServiceName()
                    throws Exception {
        HttpServletRequest request = mockRequest( SERVICE_NAME_WITH_PATH );

        String serviceName = evaluateServiceName( request );
        assertThat( serviceName, is( SERVICE_NAME ) );
    }

    @Test
    public void testEvaluateServiceNameWithExtendedPath()
                    throws Exception {
        HttpServletRequest request = mockRequest( SERVICE_NAME );

        String serviceName = evaluateServiceName( request );
        assertThat( serviceName, is( SERVICE_NAME ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEvaluateServiceNameWithoutServiceName()
                    throws Exception {
        HttpServletRequest request = mockRequest( null );
        evaluateServiceName( request );
    }

    private HttpServletRequest mockRequest( String serviceName ) {
        HttpServletRequest servletRequest = Mockito.mock( HttpServletRequest.class );
        when( servletRequest.getServletPath() ).thenReturn( serviceName );
        return servletRequest;
    }

    private Map<String, String[]> createTestParameterMap() {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put( SINGLE, SINGLE_VALUE );
        params.put( MULTIPLE, MULTIPLE_VALUE );
        return params;
    }

}
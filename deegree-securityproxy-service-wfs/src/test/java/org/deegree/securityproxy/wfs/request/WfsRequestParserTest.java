//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2014 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact information:

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/

package org.deegree.securityproxy.wfs.request;

import org.deegree.securityproxy.request.OwsRequestParser;
import org.deegree.securityproxy.request.UnsupportedRequestTypeException;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link WfsRequestParser}.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class WfsRequestParserTest {

    private final OwsRequestParser parser = new WfsRequestParser();

    @Test(expected = IllegalArgumentException.class)
    public void testParse()
                            throws UnsupportedRequestTypeException {
        HttpServletRequest putRequest = mockPutRequest();
        parser.parse( putRequest );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseWithNullShouldThrowException()
                            throws UnsupportedRequestTypeException {
        parser.parse( null );
    }

    private HttpServletRequest mockPutRequest() {
        HttpServletRequest request = mock( HttpServletRequest.class );
        doReturn( "PUT" ).when( request ).getMethod();
        return request;
    }

}

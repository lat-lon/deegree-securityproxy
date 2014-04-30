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
package org.deegree.securityproxy.authentication.wass;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.xmlmatchers.XmlMatchers.equivalentTo;
import static org.xmlmatchers.XmlMatchers.hasXPath;
import static org.xmlmatchers.transform.XmlConverters.the;
import static org.xmlmatchers.xpath.XpathReturnType.returningAString;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;
import org.xmlmatchers.namespace.SimpleNamespaceContext;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class AddAttributeAtFirstElementStrategyTest {

    private static final String PARAMETER_VALUE = "paramValue";

    private AddAttributeAtFirstElementStrategy strategy = new AddAttributeAtFirstElementStrategy( "sessionID" );

    @Test(expected = IllegalArgumentException.class)
    public void testAddAttributeAtFirstElementStrategyWithNullAttributeNameShouldFail()
                            throws Exception {
        new AddAttributeAtFirstElementStrategy( null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddAttributeAtFirstElementStrategyWithEmptyAttributeNameShouldFail()
                            throws Exception {
        new AddAttributeAtFirstElementStrategy( "" );
    }

    @Test
    public void testModifyPostRequestShouldContainSessionId()
                            throws Exception {
        InputStream originalStream = AddAttributeAtFirstElementStrategy.class.getResourceAsStream( "wfsRequest.xml" );
        ByteArrayOutputStream modifiedStream = new ByteArrayOutputStream();
        strategy.modifyPostRequest( originalStream, modifiedStream, PARAMETER_VALUE );

        assertThat( theXml( modifiedStream ),
                    hasXPath( "/wfs:GetFeature/@sessionID", nsContext(), returningAString(), is( PARAMETER_VALUE ) ) );
    }

    @Test
    public void testModifyPostRequestShouldBeSameAsInputWithSessionId()
                            throws Exception {
        InputStream originalStream = AddAttributeAtFirstElementStrategyTest.class.getResourceAsStream( "wfsRequest.xml" );
        ByteArrayOutputStream modifiedStream = new ByteArrayOutputStream();
        strategy.modifyPostRequest( originalStream, modifiedStream, PARAMETER_VALUE );

        assertThat( theXml( modifiedStream ), equivalentTo( the( xmlWithSessionId() ) ) );
    }

    @Test
    public void testModifyPostRequestWithNullValueShouldBeSameAsInput()
                            throws Exception {
        InputStream originalStream = AddAttributeAtFirstElementStrategyTest.class.getResourceAsStream( "wfsRequest.xml" );
        ByteArrayOutputStream modifiedStream = new ByteArrayOutputStream();
        strategy.modifyPostRequest( originalStream, modifiedStream, null );

        assertThat( theXml( modifiedStream ), equivalentTo( the( xmlWithoutSessionId() ) ) );
    }

    private Source xmlWithSessionId() {
        InputStream resource = AddAttributeAtFirstElementStrategyTest.class.getResourceAsStream( "wfsRequestWithSessionId.xml" );
        return new StreamSource( resource );
    }

    private Source xmlWithoutSessionId() {
        InputStream resource = AddAttributeAtFirstElementStrategyTest.class.getResourceAsStream( "wfsRequest.xml" );
        return new StreamSource( resource );
    }

    private SimpleNamespaceContext nsContext() {
        SimpleNamespaceContext nsContext = SimpleNamespaceContext.aNamespaceContext();
        nsContext.bind( "wfs", "http://www.opengis.net/wfs" );
        return nsContext;
    }

    private Source theXml( ByteArrayOutputStream modifiedStream ) {
        return the( modifiedStream.toString() );
    }

}
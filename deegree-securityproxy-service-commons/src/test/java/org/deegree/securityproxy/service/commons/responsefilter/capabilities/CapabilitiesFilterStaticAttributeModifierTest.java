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
package org.deegree.securityproxy.service.commons.responsefilter.capabilities;

import static java.util.Collections.singletonList;
import static org.deegree.securityproxy.service.commons.responsefilter.capabilities.XmlTestUtils.asXml;
import static org.deegree.securityproxy.service.commons.responsefilter.capabilities.XmlTestUtils.expectedXml;
import static org.deegree.securityproxy.service.commons.responsefilter.capabilities.XmlTestUtils.mockResponse;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.xmlmatchers.XmlMatchers.isEquivalentTo;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.deegree.securityproxy.filter.StatusCodeResponseBodyWrapper;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.element.ElementPathStep;
import org.deegree.securityproxy.service.commons.responsefilter.capabilities.text.StaticAttributeModifier;
import org.junit.Test;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class CapabilitiesFilterStaticAttributeModifierTest {

    private static final String NS_SIMPLE1 = "http://simple1.de";

    private static final String NS_EXTENDED = "http://extended.de";

    @Test
    public void testDetermineNewAttributeValue()
                            throws Exception {
        StaticAttributeModifier staticAttributeModifier = new StaticAttributeModifier( "newAttValue", createPath() );

        CapabilitiesFilter capabilitiesFilter = new CapabilitiesFilter();
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockResponse( "simpleResponse.xml", filteredCapabilities );

        capabilitiesFilter.filterCapabilities( response, mockDecisionMaker(), staticAttributeModifier );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "simpleModified.xml" ) ) );
    }

    @Test
    public void testDetermineNewAttributeValueOnePahtMultipleMatches()
                            throws Exception {
        StaticAttributeModifier staticAttributeModifier = new StaticAttributeModifier( "newAttValue",
                                                                                       createPathMultipleMatches() );

        CapabilitiesFilter capabilitiesFilter = new CapabilitiesFilter();
        ByteArrayOutputStream filteredCapabilities = new ByteArrayOutputStream();
        StatusCodeResponseBodyWrapper response = mockResponse( "extendedResponse.xml", filteredCapabilities );

        capabilitiesFilter.filterCapabilities( response, mockDecisionMaker(), staticAttributeModifier );

        assertThat( asXml( filteredCapabilities ), isEquivalentTo( expectedXml( "extendedModified.xml" ) ) );
    }

    private List<LinkedList<ElementPathStep>> createPath() {
        LinkedList<ElementPathStep> path = new LinkedList<ElementPathStep>();
        path.add( new ElementPathStep( new QName( "A" ) ) );
        path.add( new ElementPathStep( new QName( "B" ) ) );
        path.add( new ElementPathStep( new QName( NS_SIMPLE1, "d" ), new QName( "datt" ) ) );
        return singletonList( path );
    }

    private List<LinkedList<ElementPathStep>> createPathMultipleMatches() {
        LinkedList<ElementPathStep> path = new LinkedList<ElementPathStep>();
        path.add( new ElementPathStep( new QName( NS_EXTENDED, "A" ) ) );
        path.add( new ElementPathStep( new QName( NS_EXTENDED, "B" ) ) );
        path.add( new ElementPathStep( new QName( NS_EXTENDED, "e" ) ) );
        path.add( new ElementPathStep( new QName( NS_EXTENDED, "f" ) ) );
        path.add( new ElementPathStep( new QName( NS_EXTENDED, "f" ), new QName( "att" ) ) );
        return singletonList( path );
    }

    private DecisionMaker mockDecisionMaker() {
        return mock( DecisionMaker.class );
    }

}
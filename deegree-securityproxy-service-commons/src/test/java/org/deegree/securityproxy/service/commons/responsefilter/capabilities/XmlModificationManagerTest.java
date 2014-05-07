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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.LinkedList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.deegree.securityproxy.service.commons.responsefilter.capabilities.text.AttributeModifier;
import org.junit.Test;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class XmlModificationManagerTest {

    private static final String NEW_VALUE = "newValue";

    private static final boolean IS_IGNORED = true;

    @Test
    public void testIsModificationRequiredWithAttributeModifierShouldReturnTrue()
                            throws Exception {
        XmlModificationManager xmlModificationManager = new XmlModificationManager( createAttributeModifier() );
        boolean modificationRequired = xmlModificationManager.isModificationRequired();

        assertThat( modificationRequired, is( true ) );
    }

    @Test
    public void testIsModificationRequiredWithDecisionMakerShouldReturnTrue()
                            throws Exception {
        XmlModificationManager xmlModificationManager = new XmlModificationManager( createDecisionMaker() );
        boolean modificationRequired = xmlModificationManager.isModificationRequired();

        assertThat( modificationRequired, is( true ) );
    }

    @Test
    public void testIsModificationRequiredWithBothShouldReturnTrue()
                            throws Exception {
        XmlModificationManager xmlModificationManager = new XmlModificationManager( createDecisionMaker(),
                                                                                    createAttributeModifier() );
        boolean modificationRequired = xmlModificationManager.isModificationRequired();

        assertThat( modificationRequired, is( true ) );
    }

    @Test
    public void testIsModificationRequiredAllNullShouldReturnTrue()
                            throws Exception {
        XmlModificationManager xmlModificationManager = new XmlModificationManager( null, null );
        boolean modificationRequired = xmlModificationManager.isModificationRequired();

        assertThat( modificationRequired, is( false ) );
    }

    @Test
    public void testDetermineNewAttributeValueWithAllNullShouldReturnNull()
                            throws Exception {
        XmlModificationManager xmlModificationManager = new XmlModificationManager( null, null );
        String newAttributeValue = xmlModificationManager.determineNewAttributeValue( nockReader(), mockStartElement(),
                                                                                      mockAttribute(), mockList() );

        assertThat( newAttributeValue, is( nullValue() ) );
    }

    @Test
    public void testDetermineNewAttributeValueWithAllNullShouldReturnFalse()
                            throws Exception {
        XmlModificationManager xmlModificationManager = new XmlModificationManager( null, null );
        boolean ignore = xmlModificationManager.ignore( nockReader(), mockEvent(), mockList() );

        assertThat( ignore, is( false ) );
    }

    @Test
    public void testDetermineNewAttributeValueWithAttributeModifier()
                            throws Exception {
        XmlModificationManager xmlModificationManager = new XmlModificationManager( createAttributeModifier() );
        String newAttributeValue = xmlModificationManager.determineNewAttributeValue( nockReader(), mockStartElement(),
                                                                                      mockAttribute(), mockList() );

        assertThat( newAttributeValue, is( NEW_VALUE ) );
    }

    @Test
    public void testDetermineNewAttributeValueWithDecisionMaker()
                            throws Exception {
        XmlModificationManager xmlModificationManager = new XmlModificationManager( createDecisionMaker() );
        boolean ignore = xmlModificationManager.ignore( nockReader(), mockEvent(), mockList() );

        assertThat( ignore, is( IS_IGNORED ) );
    }

    @SuppressWarnings("unchecked")
    private LinkedList<StartElement> mockList() {
        return mock( LinkedList.class );
    }

    private StartElement mockStartElement() {
        return mock( StartElement.class );
    }

    private XMLEvent mockEvent() {
        return mock( XMLEvent.class );
    }

    private Attribute mockAttribute() {
        return mock( Attribute.class );
    }

    private BufferingXMLEventReader nockReader() {
        return mock( BufferingXMLEventReader.class );
    }

    private AttributeModifier createAttributeModifier()
                            throws XMLStreamException {
        return new AttributeModifier() {
            @Override
            public String determineNewAttributeValue( BufferingXMLEventReader reader, StartElement currentStartElement,
                                                      Attribute attribute, LinkedList<StartElement> visitedElements )
                                    throws XMLStreamException {
                return NEW_VALUE;
            }
        };
    }

    private DecisionMaker createDecisionMaker()
                            throws XMLStreamException {
        return new DecisionMaker() {
            @Override
            public boolean ignore( BufferingXMLEventReader reader, XMLEvent event, List<StartElement> visitedElements )
                                    throws XMLStreamException {
                return IS_IGNORED;
            }
        };
    }

}
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
package org.deegree.securityproxy.service.commons.responsefilter.capabilities.element;

import static java.util.Collections.singletonList;
import static org.deegree.securityproxy.service.commons.responsefilter.capabilities.element.PathUtils.isAttributeMatching;
import static org.deegree.securityproxy.service.commons.responsefilter.capabilities.element.PathUtils.isPathMatching;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;

import org.junit.Test;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class PathUtilsTest {

    private static final XMLEventFactory EVENT_FACTORY = XMLEventFactory.newFactory();

    private static final String NS_URL = "http://testUrl.de";

    @Test
    public void testIsPathMatchingWithSimpleMatchingPath()
                    throws Exception {
        boolean isMatching = PathUtils.isPathMatching( createSimplePath(), createLongerVisitedElements() );

        assertThat( isMatching, is( false ) );
    }

    @Test
    public void testIsPathMatchingWithNotMatchingPath()
                    throws Exception {
        boolean isMatching = isPathMatching( createSimplePath(), createSimpleVisitedElements() );

        assertThat( isMatching, is( true ) );
    }

    @Test
    public void testIsPathMatchingWithMatchingLongPath()
                    throws Exception {
        boolean isMatching = isPathMatching( createLongerPath(), createLongerVisitedElements() );

        assertThat( isMatching, is( true ) );
    }

    @Test
    public void testIsPathMatchingWithMatchingPathWithNamespace()
                    throws Exception {
        boolean isMatching = isPathMatching( createSimplePathWithNamespace(),
                                             createSimpleVisitedElementsWithNamespace() );

        assertThat( isMatching, is( true ) );
    }

    @Test
    public void testIsPathMatchingWithNotMatchingPathWithNamespace()
                    throws Exception {
        boolean isMatching = isPathMatching( createSimplePathWithNamespace(), createSimpleVisitedElements() );

        assertThat( isMatching, is( false ) );
    }

    @Test
    public void testIsPathMatchingWithMatchingPathAttribute()
                    throws Exception {
        boolean isMatching = isPathMatching( createSimplePathWithAttribute(),
                                             createSimpleVisitedElementsWithAttribute() );

        assertThat( isMatching, is( true ) );
    }

    @Test
    public void testIsPathMatchingWithNotMatchingPathWithAttribute()
                    throws Exception {
        boolean isMatching = isPathMatching( createSimplePathWithAttribute(), createSimpleVisitedElements() );

        assertThat( isMatching, is( false ) );
    }

    @Test
    public void testIsPathMatchingWithMatchingPathAttributeWithouValue()
                    throws Exception {
        boolean isMatching = isPathMatching( createSimplePathWithAttributeWithoutValue(),
                                             createSimpleVisitedElementsWithAttribute( "xValue" ) );

        assertThat( isMatching, is( true ) );
    }

    @Test
    public void testIsPathMatchingWithMatchingPathNamespaceAndAttribute()
                    throws Exception {
        boolean isMatching = isPathMatching( createSimplePathWithNamespaceAndAttribute(),
                                             createSimpleVisitedElementsWithNamespaceAndAttribute() );

        assertThat( isMatching, is( true ) );
    }

    @Test
    public void testIsPathMatchingWithNotMatchingPathWithNamespaceAndAttribute()
                    throws Exception {
        boolean isMatching = isPathMatching( createSimplePathWithNamespaceAndAttribute(), createSimpleVisitedElements() );

        assertThat( isMatching, is( false ) );
    }

    @Test
    public void testIsAttributeMatchingShouldNotMatch()
                    throws Exception {
        boolean isAttributeMatching = isAttributeMatching( createAttribute( "zAtt", "http://zUri" ), "aAtt",
                                                           "http://aUri" );

        assertThat( isAttributeMatching, is( false ) );
    }

    @Test
    public void testIsAttributeMatchingShouldMatch()
                    throws Exception {
        boolean isAttributeMatching = isAttributeMatching( createAttribute( "aAtt", "http://aUri" ), "aAtt",
                                                           "http://aUri" );

        assertThat( isAttributeMatching, is( true ) );
    }

    @Test
    public void testIsAttributeMatchingWithoutNamespaceShouldMatch()
                    throws Exception {
        boolean isAttributeMatching = isAttributeMatching( createAttribute( "aAtt", null ), "aAtt", null );

        assertThat( isAttributeMatching, is( true ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsAttributeMatchingNullAttributeShouldFail()
                    throws Exception {
        isAttributeMatching( null, "aAtt", "http://aUri" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsAttributeMatchingNullAttributeNameShouldFail()
                    throws Exception {
        isAttributeMatching( createAttribute( "aAtt", "http://aUri" ), null, "http://aUri" );
    }

    private List<ElementPathStep> createSimplePath() {
        List<ElementPathStep> path = new ArrayList<ElementPathStep>();
        path.add( new ElementPathStep( new QName( "A" ) ) );
        return path;
    }

    private List<ElementPathStep> createLongerPath() {
        List<ElementPathStep> path = new ArrayList<ElementPathStep>();
        path.add( new ElementPathStep( new QName( "A" ) ) );
        path.add( new ElementPathStep( new QName( "B" ) ) );
        path.add( new ElementPathStep( new QName( "c" ) ) );
        path.add( new ElementPathStep( new QName( "d" ) ) );
        return path;
    }

    private List<ElementPathStep> createSimplePathWithNamespace() {
        List<ElementPathStep> path = new ArrayList<ElementPathStep>();
        path.add( new ElementPathStep( new QName( NS_URL, "A" ) ) );
        return path;
    }

    private List<ElementPathStep> createSimplePathWithAttribute() {
        List<ElementPathStep> path = new ArrayList<ElementPathStep>();
        path.add( new ElementPathStep( new QName( "A" ), new QName( "zAtt" ), "zValue" ) );
        return path;
    }

    private List<ElementPathStep> createSimplePathWithAttributeWithoutValue() {
        List<ElementPathStep> path = new ArrayList<ElementPathStep>();
        path.add( new ElementPathStep( new QName( "A" ), new QName( "zAtt" ) ) );
        return path;
    }

    private List<ElementPathStep> createSimplePathWithNamespaceAndAttribute() {
        List<ElementPathStep> path = new ArrayList<ElementPathStep>();
        path.add( new ElementPathStep( new QName( NS_URL, "A" ), new QName( "zAtt" ), "zValue" ) );
        return path;
    }

    private List<StartElement> createSimpleVisitedElements() {
        List<StartElement> startElements = new ArrayList<StartElement>();
        startElements.add( EVENT_FACTORY.createStartElement( new QName( "A" ), null, null ) );
        return startElements;
    }

    private List<StartElement> createSimpleVisitedElementsWithNamespace() {
        List<StartElement> startElements = new ArrayList<StartElement>();
        startElements.add( EVENT_FACTORY.createStartElement( new QName( NS_URL, "A" ), null, null ) );
        return startElements;
    }

    private List<StartElement> createSimpleVisitedElementsWithAttribute() {
        return createSimpleVisitedElementsWithAttribute( "zValue" );
    }

    private List<StartElement> createSimpleVisitedElementsWithAttribute( String value ) {
        List<StartElement> startElements = new ArrayList<StartElement>();
        Iterator<?> attributes = singletonList( EVENT_FACTORY.createAttribute( "zAtt", "zValue" ) ).iterator();
        startElements.add( EVENT_FACTORY.createStartElement( new QName( "A" ), attributes, null ) );
        return startElements;
    }

    private List<StartElement> createSimpleVisitedElementsWithNamespaceAndAttribute() {
        List<StartElement> startElements = new ArrayList<StartElement>();
        Iterator<?> attributes = singletonList( EVENT_FACTORY.createAttribute( "zAtt", "zValue" ) ).iterator();
        startElements.add( EVENT_FACTORY.createStartElement( new QName( NS_URL, "A" ), attributes, null ) );
        return startElements;
    }

    private Attribute createAttribute( String name, String namespace ) {
        return EVENT_FACTORY.createAttribute( new QName( namespace, name ), "value" );
    }

    private List<StartElement> createLongerVisitedElements() {
        List<StartElement> startElements = new ArrayList<StartElement>();
        startElements.add( EVENT_FACTORY.createStartElement( new QName( "A" ), null, null ) );
        startElements.add( EVENT_FACTORY.createStartElement( new QName( "B" ), null, null ) );
        startElements.add( EVENT_FACTORY.createStartElement( new QName( "c" ), null, null ) );
        startElements.add( EVENT_FACTORY.createStartElement( new QName( "d" ), null, null ) );
        return startElements;
    }

}
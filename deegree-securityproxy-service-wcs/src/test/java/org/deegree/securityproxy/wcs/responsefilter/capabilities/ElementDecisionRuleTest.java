package org.deegree.securityproxy.wcs.responsefilter.capabilities;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.xml.namespace.QName;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.junit.Test;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class ElementDecisionRuleTest {

    private static final String NAME_TO_FILTER = "nameToFilter";

    private ElementDecisionRule elementDecisionRule = new ElementDecisionRule( NAME_TO_FILTER );

    @Test(expected = IllegalArgumentException.class)
    public void testElementDecisionRuleWithNullName()
                            throws Exception {
        new ElementDecisionRule( null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testElementDecisionRuleWithEmptyName()
                            throws Exception {
        new ElementDecisionRule( "" );
    }

    @Test
    public void testIgnoreWithStartElementAndFilteredNameShouldReturnTrue()
                            throws Exception {
        boolean ignore = elementDecisionRule.ignore( mockEventToIgnore() );

        assertThat( ignore, is( true ) );
    }

    @Test
    public void testIgnoreWithNotStartElementAndFilteredNameShouldReturnFalse()
                            throws Exception {
        boolean ignore = elementDecisionRule.ignore( mockEventNotStart() );

        assertThat( ignore, is( false ) );
    }

    @Test
    public void testIgnoreWithStartElementAndOtherNameShouldReturnFalse()
                            throws Exception {
        boolean ignore = elementDecisionRule.ignore( mockEventNotIgnoredName() );

        assertThat( ignore, is( false ) );
    }

    private XMLEvent mockEventToIgnore() {
        return mockEvent( true, NAME_TO_FILTER );
    }

    private XMLEvent mockEventNotStart() {
        return mockEvent( false, NAME_TO_FILTER );
    }

    private XMLEvent mockEventNotIgnoredName() {
        return mockEvent( true, "notIgnored" );
    }

    private XMLEvent mockEvent( boolean isStart, String elementName ) {
        XMLEvent event = mock( XMLEvent.class );
        when( event.isStartElement() ).thenReturn( isStart );
        StartElement startElement = mock( StartElement.class );
        QName elementQName = new QName( "http://namespace.uri.de", elementName, "pref" );
        when( startElement.getName() ).thenReturn( elementQName );
        when( event.asStartElement() ).thenReturn( startElement );
        return event;
    }

}
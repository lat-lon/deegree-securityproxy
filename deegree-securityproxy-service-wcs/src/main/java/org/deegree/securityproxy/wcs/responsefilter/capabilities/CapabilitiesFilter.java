//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2011 by:
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
package org.deegree.securityproxy.wcs.responsefilter.capabilities;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletOutputStream;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.apache.log4j.Logger;
import org.deegree.securityproxy.filter.StatusCodeResponseBodyWrapper;
import org.springframework.security.core.Authentication;

/**
 * Filters capabilities documents.
 * 
 * // TODO: check if this is a CapabiltiesFilter or XmlFilter
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class CapabilitiesFilter {

    private static final Logger LOG = Logger.getLogger( CapabilitiesFilter.class );

    private final ElementDecisionMaker elementDecisionRule;

    /**
     * @param elementDecisionMaker
     *            never <code>null</code>
     */
    public CapabilitiesFilter( ElementDecisionMaker elementDecisionMaker ) {
        this.elementDecisionRule = elementDecisionMaker;
    }

    /**
     * Filters the incoming response.
     * 
     * @param servletResponse
     *            containing the response to filter, never <code>null</code>
     * @param auth
     *            containing rules to apply for filtering, never <code>null</code>
     * @throws IOException
     *             if an error occurred during stream handling
     * @throws XMLStreamException
     *             if an error occurred during reading or writing the response
     */
    public void filterCapabilities( StatusCodeResponseBodyWrapper servletResponse, Authentication auth )
                            throws IOException, XMLStreamException {
        BufferingXMLEventReader reader = null;
        XMLEventWriter writer = null;
        try {
            reader = createReader( servletResponse );
            writer = createWriter( servletResponse );

            copyResponse( reader, writer );

        } finally {
            closeQuietly( reader );
            closeQuietly( writer );
        }
    }

    private void copyResponse( BufferingXMLEventReader reader, XMLEventWriter writer )
                            throws XMLStreamException {
        while ( reader.hasNext() ) {
            XMLEvent next = reader.nextEvent();
            if ( next.isStartElement() && ignoreElement( reader, next ) ) {
                LOG.info( "Event " + next + " is ignored." );
                skipElementContent( reader );
            } else
                writer.add( next );
        }
    }

    private boolean ignoreElement( BufferingXMLEventReader reader, XMLEvent next )
                            throws XMLStreamException {
        return elementDecisionRule != null && elementDecisionRule.ignore( reader, next );
    }

    private XMLEventWriter createWriter( StatusCodeResponseBodyWrapper servletResponse )
                            throws IOException, FactoryConfigurationError, XMLStreamException {
        ServletOutputStream filteredCapabilitiesStreamToWriteIn = servletResponse.getRealOutputStream();

        XMLOutputFactory outFactory = XMLOutputFactory.newInstance();
        return outFactory.createXMLEventWriter( filteredCapabilitiesStreamToWriteIn );
    }

    private BufferingXMLEventReader createReader( StatusCodeResponseBodyWrapper servletResponse )
                            throws FactoryConfigurationError, XMLStreamException {
        InputStream originalCapabilities = servletResponse.getBufferedStream();
        XMLInputFactory inFactory = XMLInputFactory.newInstance();
        XMLEventReader reader = inFactory.createXMLEventReader( originalCapabilities );
        return new BufferingXMLEventReader( reader );
    }

    private void skipElementContent( XMLEventReader reader )
                            throws XMLStreamException {
        int depth = 0;
        while ( depth >= 0 ) {
            XMLEvent nextEvent = reader.nextEvent();
            if ( nextEvent.isStartElement() ) {
                depth++;
            } else if ( nextEvent.isEndElement() ) {
                depth--;
            }
        }
    }

    private void closeQuietly( XMLEventReader reader ) {
        try {
            if ( reader != null )
                reader.close();
        } catch ( XMLStreamException e ) {
            LOG.warn( "Reader could not be closed: " + e.getMessage() );
        }
    }

    private void closeQuietly( XMLEventWriter writer ) {
        try {
            if ( writer != null )
                writer.close();
        } catch ( XMLStreamException e ) {
            LOG.warn( "Reader could not be closed: " + e.getMessage() );
        }
    }

}
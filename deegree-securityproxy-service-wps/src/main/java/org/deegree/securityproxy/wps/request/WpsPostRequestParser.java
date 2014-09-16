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
package org.deegree.securityproxy.wps.request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.request.OwsServiceVersion;
import org.deegree.securityproxy.request.UnsupportedRequestTypeException;
import org.deegree.securityproxy.request.parser.OwsRequestParser;
import org.deegree.securityproxy.request.parser.OwsRequestParserUtils;
import org.deegree.securityproxy.request.parser.RequestParsingException;

/**
 * Parses wps post requests.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class WpsPostRequestParser implements OwsRequestParser {

    private static final QName EXECUTE_NAME = new QName( "http://www.opengis.net/wps/1.0.0", "Execute" );

    private static final QName IDENTIFIER_NAME = new QName( "http://www.opengis.net/ows/1.1", "Identifier" );

    @Override
    public OwsRequest parse( HttpServletRequest request )
                    throws UnsupportedRequestTypeException, RequestParsingException {
        try {
            XMLStreamReader reader = createReader( request );

            checkElementName( reader );
            checkServiceAttribute( reader );

            return parseRequest( request, reader );
        } catch ( IOException e ) {
            throw new RequestParsingException( e );
        } catch ( XMLStreamException e ) {
            throw new RequestParsingException( e );
        }
    }

    private OwsRequest parseRequest( HttpServletRequest request, XMLStreamReader reader )
                    throws XMLStreamException {
        String serviceName = OwsRequestParserUtils.evaluateServiceName( request );
        OwsServiceVersion version = parseVersion( reader );

        List<String> identifiers = parseIdentifiers( reader );
        return new WpsRequest( "Execute", version, serviceName, identifiers );
    }

    private OwsServiceVersion parseVersion( XMLStreamReader parser ) {
        String versionValue = parser.getAttributeValue( null, "version" );
        if ( versionValue != null )
            return new OwsServiceVersion( versionValue );
        throw new IllegalArgumentException( "version is not set" );
    }

    private List<String> parseIdentifiers( XMLStreamReader reader )
                    throws XMLStreamException {
        List<String> identifiers = new ArrayList<String>();
        while ( reader.getEventType() != XMLStreamConstants.END_DOCUMENT ) {
            if ( reader.isStartElement() && IDENTIFIER_NAME.equals( reader.getName() ) ) {
                String identifier = reader.getElementText();
                identifiers.add( identifier );
                return identifiers;
            }
            reader.next();
        }
        return identifiers;
    }

    private void checkServiceAttribute( XMLStreamReader parser ) {
        String serviceValue = parser.getAttributeValue( null, "service" );
        if ( !"WPS".equalsIgnoreCase( serviceValue ) )
            throw new IllegalArgumentException( "Request is not WPS request!" );
    }

    private void checkElementName( XMLStreamReader parser ) {
        QName rootElementName = parser.getName();
        if ( !EXECUTE_NAME.equals( rootElementName ) )
            throw new IllegalArgumentException( "Request is not Execute request!" );
    }

    private XMLStreamReader createReader( HttpServletRequest request )
                    throws IOException, FactoryConfigurationError, XMLStreamException {
        ServletInputStream inputStream = request.getInputStream();
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader( inputStream );
        forwardToRootElement( reader );
        return reader;
    }

    private void forwardToRootElement( XMLStreamReader parser )
                    throws XMLStreamException {
        while ( !parser.isStartElement() ) {
            parser.nextTag();
        }
    }

}
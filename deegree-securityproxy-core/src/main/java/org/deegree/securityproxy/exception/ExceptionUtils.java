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
package org.deegree.securityproxy.exception;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/**
 * Contains useful methods for exception handlings
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class ExceptionUtils {

    private static final Logger LOG = Logger.getLogger( ServiceExceptionWrapper.class );

    /**
     * Reads the exception from a file, if the exception is not available the default body is returned.
     * 
     * @param pathToExceptionFile
     *            may be <code>null</code> or unavailable (default body is returned).
     * @param defaultBody
     *            th edefault body to return if pathToExceptionFile is <code>null</code> or unavailable, may be
     *            <code>null</code>
     * @return the content of the pathToExceptionFile if not <code>null</code> and available, else the defaultBody,
     *         <code>null</code> if both are <code>null</code>/unavailable
     */
    public static String readExceptionBodyFromFile( String pathToExceptionFile, String defaultBody ) {
        LOG.info( "Reading exception body from " + pathToExceptionFile );
        if ( pathToExceptionFile != null && pathToExceptionFile.length() > 0 ) {
            InputStream exceptionAsStream = null;
            try {
                File exceptionFile = new File( pathToExceptionFile );
                exceptionAsStream = new FileInputStream( exceptionFile );
                return IOUtils.toString( exceptionAsStream );
            } catch ( FileNotFoundException e ) {
                LOG.warn( "Could not read exception message from file: File not found! Defaulting to " + defaultBody );
            } catch ( IOException e ) {
                LOG.warn( "Could not read exception message from file. Defaulting to " + defaultBody + "Reason: "
                          + e.getMessage() );
            } finally {
                closeQuietly( exceptionAsStream );
            }
        }
        return defaultBody;
    }

}
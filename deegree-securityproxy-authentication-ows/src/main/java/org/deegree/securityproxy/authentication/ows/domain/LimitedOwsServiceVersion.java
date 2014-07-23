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
package org.deegree.securityproxy.authentication.ows.domain;

import static java.lang.Character.isDigit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.deegree.securityproxy.request.OwsServiceVersion;

/**
 * Encapsulates a version range (less than, greater than ... a specified version)
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class LimitedOwsServiceVersion {

    private static final String GREATER_EQUAL = ">=";

    private static final String GREATER = ">";

    private static final String LESS_EQUAL = "<=";

    private static final String LESS = "<";

    private static final String EQUAL = "==";

    private final OwsServiceVersion version;

    private final String limitType;

    /**
     * Expects one of:
     * 
     * <pre>
     *  <= X.Y.Z, same as =< X.Y.Z
     *  < X.Y.Z
     *  => X.Y.Z, same as >= X.Y.Z
     *  == X.Y.Z, same as = X.Y.Z and X.Y.Z
     * </pre>
     * 
     * @param limitedVersionToParse
     *            never <code>null</code>
     * @throws IllegalArgumentException
     *             if the version to parse is <code>null</code> or does not match to one of the expected pattern
     */
    public LimitedOwsServiceVersion( String limitedVersionToParse ) {
        if ( limitedVersionToParse == null )
            throw new IllegalArgumentException( "Limited version to parse must not be null." );

        String versionToParse = limitedVersionToParse.trim();
        if ( versionToParse.startsWith( ">=" ) || versionToParse.startsWith( "=>" ) ) {
            limitType = GREATER_EQUAL;
            version = parseAsVersion( versionToParse );
        } else if ( versionToParse.startsWith( "<=" ) || versionToParse.startsWith( "=<" ) ) {
            limitType = LESS_EQUAL;
            version = parseAsVersion( versionToParse );
        } else if ( versionToParse.startsWith( "<" ) ) {
            limitType = LESS;
            version = parseAsVersion( versionToParse );
        } else if ( versionToParse.startsWith( ">" ) ) {
            limitType = GREATER;
            version = parseAsVersion( versionToParse );
        } else if ( versionToParse.startsWith( "==" ) || versionToParse.startsWith( "=" ) ) {
            limitType = EQUAL;
            version = parseAsVersion( versionToParse );
        } else if ( startsWithADigit( versionToParse ) ) {
            limitType = EQUAL;
            version = parseAsVersion( versionToParse );
        } else {
            String msg = "Limited version " + limitedVersionToParse
                         + " could not be parsed (does not match the expected pattern).";
            throw new IllegalArgumentException( msg );
        }
    }

    /**
     * @param limitType
     *            never <code>null</code>
     * @param owsServiceVersion
     *            never <code>null</code>
     */
    public LimitedOwsServiceVersion( String limitType, OwsServiceVersion owsServiceVersion ) {
        this.limitType = limitType;
        version = owsServiceVersion;
    }

    /**
     * @return the version, never <code>null</code>
     */
    public OwsServiceVersion getVersion() {
        return version;
    }

    /**
     * @return the limitType, never <code>null</code>
     */
    public String getLimitType() {
        return limitType;
    }

    public boolean contains( OwsServiceVersion requestedServiceVersion ) {
        int compareTo = requestedServiceVersion.compareTo( version );
        if ( compareTo > 0 ) {
            return GREATER.equals( limitType ) || GREATER_EQUAL.equals( limitType );
        } else if ( compareTo < 0 )
            return LESS.equals( limitType ) || LESS_EQUAL.equals( limitType );
        return EQUAL.equals( limitType ) || GREATER_EQUAL.equals( limitType ) || LESS_EQUAL.equals( limitType );
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( limitType == null ) ? 0 : limitType.hashCode() );
        result = prime * result + ( ( version == null ) ? 0 : version.hashCode() );
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        LimitedOwsServiceVersion other = (LimitedOwsServiceVersion) obj;
        if ( limitType == null ) {
            if ( other.limitType != null )
                return false;
        } else if ( !limitType.equals( other.limitType ) )
            return false;
        if ( version == null ) {
            if ( other.version != null )
                return false;
        } else if ( !version.equals( other.version ) )
            return false;
        return true;
    }

    private OwsServiceVersion parseAsVersion( String versionToParse ) {
        int beginIndex = parseIndexOfFirstDigit( versionToParse );
        String trimmedVersion = versionToParse.substring( beginIndex ).trim();
        return new OwsServiceVersion( trimmedVersion );
    }

    private int parseIndexOfFirstDigit( String versionToParse ) {
        Pattern pattern = Pattern.compile( "[0-9]" );
        Matcher matcher = pattern.matcher( versionToParse );
        return matcher.find() ? matcher.start() : -1;
    }

    private boolean startsWithADigit( String versionToParse ) {
        return !versionToParse.isEmpty() && isDigit( versionToParse.charAt( 0 ) );
    }

}
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
package org.deegree.securityproxy.service.commons.responsefilter.capabilities.element;

import java.util.Collections;
import java.util.List;

/**
 * Encapsulates the elements to check for filtering.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class ElementRule {

    private final String name;

    private final String namespace;

    private final List<String> text;

    private final ElementRule subRule;

    private final List<ElementPathStep> path;

    private final boolean textShouldMatch;

    private boolean isApplied;

    /**
     * Use this if only the name of the element is interesting for filtering.
     * 
     * @param name
     *            never <code>null</code>
     */
    public ElementRule( String name ) {
        this( name, null );
    }

    /**
     * 
     * Use this if name and namespace are interesting for filtering.
     * 
     * @param name
     *            never <code>null</code>
     * @param namespace
     *            may be <code>null</code>
     */
    public ElementRule( String name, String namespace ) {
        this( name, namespace, (List<String>) null );
    }

    /**
     * 
     * Use this if name and namespace as well as the element text are interesting for filtering.
     * 
     * @param name
     *            never <code>null</code>
     * @param namespace
     *            may be <code>null</code>
     * @param text
     *            may be <code>null</code>
     */
    public ElementRule( String name, String namespace, String text ) {
        this( name, namespace, ( text != null ? Collections.singletonList( text ) : (List<String>) null ) );
    }

    /**
     * 
     * Use this if name and namespace as well as the element text are interesting for filtering.
     * 
     * @param name
     *            never <code>null</code>
     * @param namespace
     *            may be <code>null</code>
     * @param text
     *            may be <code>null</code>
     */
    public ElementRule( String name, String namespace, List<String> text ) {
        this( name, namespace, text, (ElementRule) null );
    }

    /**
     * 
     * Use this if name and namespace as well as the element text are interesting for filtering.
     * 
     * @param name
     *            never <code>null</code>
     * @param namespace
     *            may be <code>null</code>
     * @param text
     *            may be <code>null</code>
     */
    public ElementRule( String name, String namespace, List<String> text, boolean textShouldMatch ) {
        this( name, namespace, text, (ElementRule) null, null, textShouldMatch );
    }

    /**
     * 
     * Use this if name and namespace are interesting for filtering. Furthermore a sub element may be passed.
     * 
     * @param name
     *            never <code>null</code>
     * @param namespace
     *            may be <code>null</code>
     * @param subRule
     *            may be <code>null</code>
     */
    public ElementRule( String name, String namespace, ElementRule subRule ) {
        this( name, namespace, null, subRule );
    }

    /**
     * 
     * Use this if name and namespace as well as the element text are interesting for filtering. Furthermore a sub
     * element may be passed.
     * 
     * @param name
     *            never <code>null</code>
     * @param namespace
     *            may be <code>null</code>
     * @param text
     *            may be <code>null</code>
     * @param subRule
     *            may be <code>null</code>
     */
    public ElementRule( String name, String namespace, List<String> text, ElementRule subRule ) {
        this( name, namespace, text, subRule, null );
    }

    /**
     * 
     * Use this if name and namespace as well as the element text are interesting for filtering. Furthermore a sub
     * element may be passed.
     * 
     * @param name
     *            never <code>null</code>
     * @param namespace
     *            may be <code>null</code>
     * @param text
     *            may be <code>null</code>
     * @param path
     *            may be <code>null</code>
     */
    public ElementRule( String name, String namespace, String text, List<ElementPathStep> path ) {
        this( name, namespace, ( text != null ? Collections.singletonList( text ) : (List<String>) null ), null, path );
    }

    /**
     * 
     * Use this if name and namespace as well as the element text are interesting for filtering. Furthermore a sub
     * element may be passed.
     * 
     * @param name
     *            never <code>null</code>
     * @param namespace
     *            may be <code>null</code>
     * @param text
     *            may be <code>null</code>
     * @param path
     *            may be <code>null</code>
     */
    public ElementRule( String name, String namespace, List<String> text, List<ElementPathStep> path ) {
        this( name, namespace, text, null, path );
    }

    /**
     * 
     * Use this if name and namespace as well as the element text are interesting for filtering. Furthermore a sub
     * element may be passed.
     * 
     * @param name
     *            never <code>null</code>
     * @param namespace
     *            may be <code>null</code>
     * @param text
     *            may be <code>null</code>
     * @param subRule
     *            may be <code>null</code>
     * @param path
     *            may be <code>null</code>
     */
    public ElementRule( String name, String namespace, List<String> text, ElementRule subRule,
                        List<ElementPathStep> path ) {
        this( name, namespace, text, subRule, path, true );
    }

    /**
     * 
     * Use this if name and namespace as well as the element text are interesting for filtering. Furthermore a sub
     * element may be passed.
     * 
     * @param name
     *            never <code>null</code>
     * @param namespace
     *            may be <code>null</code>
     * @param text
     *            may be <code>null</code>
     * @param subRule
     *            may be <code>null</code>
     * @param path
     *            may be <code>null</code>
     * @param textShouldMatch
     *            - indicates if the passed text should match or not
     */
    public ElementRule( String name, String namespace, List<String> text, ElementRule subRule,
                        List<ElementPathStep> path, boolean textShouldMatch ) {
        this.name = name;
        this.namespace = namespace;
        this.text = text;
        this.subRule = subRule;
        this.path = path;
        this.textShouldMatch = textShouldMatch;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the namespace
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * @return the text
     */
    public List<String> getText() {
        return text;
    }

    /**
     * @return the subRule, may be <code>null</code> if a subRule should not be applied
     */
    public ElementRule getSubRule() {
        return subRule;
    }

    /**
     * @return the path, may be <code>null</code> if the path should not be applied
     */
    public List<ElementPathStep> getPath() {
        return path;
    }

    /**
     * @return true if the text should match, false if not
     */
    public boolean isTextShouldMatch() {
        return textShouldMatch;
    }

    /**
     * @return <code>true</code> if this rule was applied, <code>false</code> otherwise
     */
    public boolean isApplied() {
        return isApplied;
    }

    /**
     * @param isApplied
     *            <code>true</code> if this rule was applied, <code>false</code> otherwise
     */
    public void setApplied( boolean isApplied ) {
        this.isApplied = isApplied;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
        result = prime * result + ( ( namespace == null ) ? 0 : namespace.hashCode() );
        result = prime * result + ( ( path == null ) ? 0 : path.hashCode() );
        result = prime * result + ( ( subRule == null ) ? 0 : subRule.hashCode() );
        result = prime * result + ( ( text == null ) ? 0 : text.hashCode() );
        result = prime * result + ( textShouldMatch ? 1231 : 1237 );
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
        ElementRule other = (ElementRule) obj;
        if ( name == null ) {
            if ( other.name != null )
                return false;
        } else if ( !name.equals( other.name ) )
            return false;
        if ( namespace == null ) {
            if ( other.namespace != null )
                return false;
        } else if ( !namespace.equals( other.namespace ) )
            return false;
        if ( path == null ) {
            if ( other.path != null )
                return false;
        } else if ( !path.equals( other.path ) )
            return false;
        if ( subRule == null ) {
            if ( other.subRule != null )
                return false;
        } else if ( !subRule.equals( other.subRule ) )
            return false;
        if ( text == null ) {
            if ( other.text != null )
                return false;
        } else if ( !text.equals( other.text ) )
            return false;
        if ( textShouldMatch != other.textShouldMatch )
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ElementRule [name=" + name + ", namespace=" + namespace + ", text=" + text + ", subRule=" + subRule
               + ", path=" + path + ", textShouldMatch=" + textShouldMatch + "]";
    }

}
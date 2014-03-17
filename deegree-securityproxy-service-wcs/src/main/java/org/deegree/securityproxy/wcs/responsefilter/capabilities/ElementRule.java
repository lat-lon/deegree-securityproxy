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

    private final String text;

    private final ElementRule subRule;

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
        this( name, namespace, (String) null );
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
        this( name, namespace, text, null );
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
    public ElementRule( String name, String namespace, String text, ElementRule subRule ) {
        this.name = name;
        this.namespace = namespace;
        this.text = text;
        this.subRule = subRule;
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
    public String getText() {
        return text;
    }

    /**
     * @return the subRule
     */
    public ElementRule getSubRule() {
        return subRule;
    }

}
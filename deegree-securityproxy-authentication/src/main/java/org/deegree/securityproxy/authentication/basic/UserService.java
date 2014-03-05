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
package org.deegree.securityproxy.authentication.basic;

import org.apache.log4j.Logger;
import org.deegree.securityproxy.authentication.repository.UserDao;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Implementation of a {@link UserDetailsService} using a {@link UserDao} to retrieve {@link UserDetails} by the user
 * name.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class UserService implements UserDetailsService {

    private static final Logger LOG = Logger.getLogger( UserService.class );

    private final UserDao userDao;

    /**
     * @param userDao
     *            used to retrieve {@link UserDetails}, never <code>null</code>
     */
    public UserService( UserDao userDao ) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername( String userName )
                            throws UsernameNotFoundException {
        LOG.info( "Search user " + userName );
        UserDetails user = userDao.retrieveUserByName( userName );
        if ( user != null )
            return user;
        throw new UsernameNotFoundException( "User with name " + userName + " not found!" );
    }

}
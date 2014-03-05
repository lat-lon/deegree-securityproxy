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

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.deegree.securityproxy.authentication.AddHeaderHttpServletRequestWrapper;
import org.deegree.securityproxy.authentication.OwsUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * Extended {@link BasicAuthenticationFilter}, adding the access_token as header to the request
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
@SuppressWarnings("deprecation")
public class AddHeaderBasicAuthenticationFilter extends BasicAuthenticationFilter {

    private final String headerName;

    /**
     * @param headerName
     *            name of the header added when the authentication was successful, may be <code>null</code> if the
     *            header should not be set
     */
    public AddHeaderBasicAuthenticationFilter( String headerName ) {
        this.headerName = headerName;
    }

    @Override
    public void doFilter( ServletRequest req, ServletResponse res, FilterChain chain )
                            throws IOException, ServletException {
        AddHeaderHttpServletRequestWrapper request = new AddHeaderHttpServletRequestWrapper( (HttpServletRequest) req );
        super.doFilter( request, res, chain );
    }

    @Override
    protected void onSuccessfulAuthentication( HttpServletRequest request, HttpServletResponse response,
                                               Authentication authResult )
                            throws IOException {
        super.onSuccessfulAuthentication( request, response, authResult );
        if ( headerName != null ) {
            addHeader( request, authResult );
        }
    }

    private void addHeader( HttpServletRequest request, Authentication authResult ) {
        OwsUserDetails principal = (OwsUserDetails) authResult.getPrincipal();
        String accessToken = principal.getAccessToken();
        ( (AddHeaderHttpServletRequestWrapper) request ).addHeader( headerName, accessToken );
    }

}
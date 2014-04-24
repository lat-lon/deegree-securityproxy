package org.deegree.securityproxy.authentication.wass;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.deegree.securityproxy.filter.KvpRequestWrapper;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

/**
 * {@link AbstractAuthenticationProcessingFilter} implementation using a {@link AuthenticationManager} to authenticate a
 * anonymous user. All requests are authenticated. After authentication the credentials are added as parameter to the
 * request.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class AddParameterAnonymousAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final Logger LOG = Logger.getLogger( AddParameterAnonymousAuthenticationFilter.class );

    private static final String ANONYMOUS_USER = "anonymousUser";

    private final String parameterKey;

    public AddParameterAnonymousAuthenticationFilter( String parameterKey ) {
        // path is not used, cause method #requiresAuthentication() authentication is overwritten!
        super( "/unusedPath" );
        this.parameterKey = parameterKey;
    }

    @Override
    public void doFilter( ServletRequest req, ServletResponse res, FilterChain chain )
                            throws IOException, ServletException {
        LOG.debug( "Wrap request with query string " + ( (HttpServletRequest) req ).getQueryString() );
        KvpRequestWrapper request = new KvpRequestWrapper( (HttpServletRequest) req );
        super.doFilter( request, res, chain );
    }

    @Override
    protected boolean requiresAuthentication( HttpServletRequest request, HttpServletResponse response ) {
        return true;
    }

    @Override
    public Authentication attemptAuthentication( HttpServletRequest request, HttpServletResponse response )
                            throws AuthenticationException, IOException, ServletException {
        Authentication authRequest = new AnonymousAuthenticationToken( ANONYMOUS_USER, ANONYMOUS_USER,
                                                                       createAuthorityList( "ROLE_ANONYMOUS" ) );
        return this.getAuthenticationManager().authenticate( authRequest );
    }

    @Override
    protected void successfulAuthentication( HttpServletRequest request, HttpServletResponse response,
                                             FilterChain chain, Authentication authResult )
                            throws IOException, ServletException {
        addParameter( request, authResult );
        chain.doFilter( request, response );
    }

    private void addParameter( HttpServletRequest request, Authentication authResult ) {
        Object credentials = authResult.getCredentials();
        if ( credentials != null && credentials instanceof String ) {
            LOG.info( "Append parameter " + parameterKey + "=" + credentials );
            ( (KvpRequestWrapper) request ).addParameter( parameterKey, (String) credentials );
        }
    }

}
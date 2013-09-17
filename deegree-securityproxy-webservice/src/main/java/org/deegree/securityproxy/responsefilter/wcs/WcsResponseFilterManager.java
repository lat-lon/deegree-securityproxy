package org.deegree.securityproxy.responsefilter.wcs;

import javax.servlet.http.HttpServletResponse;

import org.deegree.securityproxy.request.WcsRequest;
import org.deegree.securityproxy.responsefilter.ResponseFilterManager;
import org.deegree.securityproxy.responsefilter.logging.ResponseFilterReport;
import org.springframework.security.core.Authentication;

public class WcsResponseFilterManager implements ResponseFilterManager {

    @Override
    public ResponseFilterReport filterResponse( HttpServletResponse servletResponse, WcsRequest wcsResquest,
                                                Authentication auth ) {
        // TODO Auto-generated method stub
        return null;
    }

}

package org.deegree.securityproxy.responsefilter;

import javax.servlet.http.HttpServletResponse;

import org.deegree.securityproxy.request.WcsRequest;
import org.deegree.securityproxy.responsefilter.logging.ResponseFilterReport;
import org.springframework.security.core.Authentication;

public interface ResponseFilterManager {

    ResponseFilterReport filterResponse( HttpServletResponse servletResponse, WcsRequest wcsResquest,
                                         Authentication auth );

}
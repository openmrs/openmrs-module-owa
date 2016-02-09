/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0 + Health disclaimer. If a copy of the MPL was not distributed with
 * this file, You can obtain one at http://license.openmrs.org
 */
package org.openmrs.module.owa.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.openmrs.api.context.Context;
import org.openmrs.module.owa.AppManager;

/**
 * @author sunbiz
 */
public class OwaFilter implements Filter {
	
	private String openmrsPath;
	
	@Override
	public void init(FilterConfig fc) throws ServletException {
		openmrsPath = fc.getServletContext().getContextPath();
	}
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		String requestURL = request.getRequestURL().toString();
		
		String owaBasePath = Context.getAdministrationService().getGlobalProperty(AppManager.KEY_APP_BASE_URL);
		
		if (Context.isAuthenticated()) {
			if (requestURL.startsWith(owaBasePath)) {
				String newURL = requestURL.replace(owaBasePath, "/ms/owa/fileServlet");
				req.getRequestDispatcher(newURL).forward(req, res);
			} else {
				chain.doFilter(req, res);
			}
		} else {
			if (requestURL.startsWith(owaBasePath)) {
				String newURL = requestURL.replace(owaBasePath, "/ms/owa/redirectServlet");
				req.getRequestDispatcher(newURL).forward(req, res);
			} else {
				chain.doFilter(req, res);
			}
		}
	}
	
	@Override
	public void destroy() {
	}
	
}

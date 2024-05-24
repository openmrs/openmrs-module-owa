/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0 + Health disclaimer. If a copy of the MPL was not distributed with
 * this file, You can obtain one at http://license.openmrs.org
 */
package org.openmrs.module.owa.filter;

import org.openmrs.Privilege;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.context.Context;
import org.openmrs.module.owa.AppManager;
import org.openmrs.util.PrivilegeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author sunbiz
 */
public class OwaFilter implements Filter {
	
	public static final String DEFAULT_BASE_URL = "/owa";
	
	private static final String ADD_ON_MANAGER = "addonmanager";
	
	private static final Logger logger = LoggerFactory.getLogger(OwaFilter.class);
	
	private String openmrsPath;
	
	@Override
	public void init(FilterConfig fc) throws ServletException {
		openmrsPath = fc.getServletContext().getContextPath();
	}
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		
		String owaBasePath = DEFAULT_BASE_URL;
		
		try {
			Context.addProxyPrivilege(PrivilegeConstants.GET_GLOBAL_PROPERTIES);
			owaBasePath = Context.getAdministrationService()
			        .getGlobalProperty(AppManager.KEY_APP_BASE_URL, DEFAULT_BASE_URL);
		}
		finally {
			Context.removeProxyPrivilege(PrivilegeConstants.GET_GLOBAL_PROPERTIES);
		}
		
		String requestURL = null;
		if (isFullBasePath(owaBasePath)) {
			requestURL = request.getRequestURL().toString();
		} else {
			requestURL = request.getServletPath();
		}
		
		String loginUrl;
		try {
			Context.addProxyPrivilege(PrivilegeConstants.GET_GLOBAL_PROPERTIES);
			loginUrl = Context.getAdministrationService().getGlobalProperty("login.url", "login.htm");
		}
		finally {
			Context.removeProxyPrivilege(PrivilegeConstants.GET_GLOBAL_PROPERTIES);
		}
		
		if (Context.isAuthenticated()) {
			if (requestURL.startsWith(owaBasePath)) {
				String newURL = requestURL.replace(owaBasePath, "/ms/owa/fileServlet");
				req.getRequestDispatcher(newURL).forward(req, res);
			} else {
				doFilter(req, res, chain, loginUrl);
			}
		} else {
			if (requestURL.startsWith(owaBasePath)) {
				String newURL = requestURL.replace(owaBasePath, "/ms/owa/redirectServlet");
				if (requestURL.contains(loginUrl) || requestURL.contains(ADD_ON_MANAGER)) {
					newURL = requestURL.replace(owaBasePath, "/ms/owa/fileServlet");
				}
				req.getRequestDispatcher(newURL).forward(req, res);
			} else {
				doFilter(req, res, chain, loginUrl);
			}
		}
	}
	
	//owaBasePath can be either full path (must contain protocol) or relative servlet path
	public static boolean isFullBasePath(String owaBasePath) {
		return owaBasePath.contains("://");
	}
	
	@Override
	public void destroy() {
	}
	
	private void doFilter(ServletRequest req, ServletResponse res, FilterChain chain, String loginUrl) throws IOException,
	        ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		if (loginUrl.contains(ADD_ON_MANAGER)
		        && (request.getServletPath().equals("/index.htm") || request.getServletPath().equals("/login.htm") || request
		                .getServletPath().equals("/"))) {
			((HttpServletResponse) res).sendRedirect(request.getContextPath() + "/" + loginUrl);
		} else {
			chain.doFilter(req, res);
		}
	}
	
}

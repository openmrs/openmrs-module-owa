/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0 + Health disclaimer. If a copy of the MPL was not distributed with
 * this file, You can obtain one at http://license.openmrs.org
 */
package org.openmrs.module.owa.filter;

import org.openmrs.api.context.Context;
import org.openmrs.module.owa.AppManager;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author sunbiz
 */
public class OwaFilter implements Filter {
	
	public static final String DEFAULT_BASE_URL = "/owa";
	
	private String openmrsPath;
	
	@Override
	public void init(FilterConfig fc) throws ServletException {
		openmrsPath = fc.getServletContext().getContextPath();
	}
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		String owaBasePath = Context.getAdministrationService().getGlobalProperty(AppManager.KEY_APP_BASE_URL,
		    DEFAULT_BASE_URL);
		
		System.out.println("kkkkkkkkkkkkkkkkkkkk="+owaBasePath);
		String requestURL = null;
		if (isFullBasePath(owaBasePath)) {
			requestURL = request.getRequestURL().toString();
		} else {
			requestURL = request.getServletPath();
		}
		
		System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb="+requestURL);
		if (Context.isAuthenticated()) {
			System.out.println("ccccccccccccccccc="+requestURL);
			if (requestURL.startsWith(owaBasePath)) {
				System.out.println("ddddddddddddddddddd="+requestURL);
				String newURL = requestURL.replace(owaBasePath, "/ms/owa/fileServlet");
				System.out.println("ooooooooooooooooooooooooooo="+newURL);
				req.getRequestDispatcher(newURL).forward(req, res);
			} else {
				System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeee="+requestURL);
				chain.doFilter(req, res);
			}
		} else {
			System.out.println("fffffffffffffffffffffffffffffffffffffff="+requestURL);
			if (requestURL.startsWith(owaBasePath)) {
				System.out.println("ggggggggggggggggggggggggggggggggggg="+requestURL);
				String newURL = requestURL.replace(owaBasePath, "/ms/owa/redirectServlet");
				System.out.println("pppppppppppppppppppppppp="+newURL);
				req.getRequestDispatcher(newURL).forward(req, res);
			} else {
				System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh="+requestURL);
				chain.doFilter(req, res);
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
	
}

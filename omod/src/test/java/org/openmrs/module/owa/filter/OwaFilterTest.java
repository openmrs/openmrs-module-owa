/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.owa.filter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.module.owa.AppManager;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OwaFilterTest extends BaseModuleWebContextSensitiveTest {
	
	private static String DEFAULT_APP_BASE_URL = "http://localhost:80/openmrs/owa";
	
	private static String DEFAULT_APP_BASE_URI = "/openmrs/owa";
	
	private static String SOME_RANDOM_BASE_URL = "http://localhost:80/openmrs/somethingbesidesthedefault";
	
	private static String SOME_RANDOM_BASE_URI = "/openmrs/somethingbesidesthedefault";
	
	private static String SOME_PATH_IN_APP = "/anything/index.hml";
	
	private static String DEFAULT_APP_BASE_SERVLET_PATH = "/owa" + SOME_PATH_IN_APP;
	
	private static String DUMMY_CONTEXT_PATH = "http://localhost:80/openmrs";
	
	private static String FILE_SERVLET_REDIRECT_URL = "/ms/owa/fileServlet";
	
	private static String REDIRECT_SERVLET_URL = "/ms/owa/redirectServlet";
	
	private static String ADD_ON_MANAGER_REDIRECT_URL = "owa/addonmanager/index.html";
	
	FilterConfig filterConfig;
	
	ServletContext servletContext;
	
	public OwaFilterTest() {
		
	}
	
	@Before
	public void setUpMocks() {
		filterConfig = mock(FilterConfig.class);
		servletContext = mock(ServletContext.class);
		
		when(servletContext.getContextPath()).thenReturn(DUMMY_CONTEXT_PATH);
		when(filterConfig.getServletContext()).thenReturn(servletContext);
	}
	
	/**
	 * Test that the OwaFilter class actually services from the base path defined in the global
	 * property and ignore any other requests.
	 */
	@Test
	public void testOwaFilterUsesGlobalProperty() throws Exception {
		OwaFilter owaFilter = new OwaFilter();
		owaFilter.init(filterConfig);
		
		MockFilterChain mockFilterChain = new MockFilterChain();
		MockHttpServletResponse rsp = new MockHttpServletResponse();
		
		// First make sure that it works with the default base URL
		MockHttpServletRequest req = new MockHttpServletRequest("GET", DEFAULT_APP_BASE_URI + SOME_PATH_IN_APP);
		//have to explicitly set servlet path because constructor doesn't do that
		req.setServletPath(DEFAULT_APP_BASE_SERVLET_PATH);
		owaFilter.doFilter(req, rsp, mockFilterChain);
		Assert.assertEquals(rsp.getStatus(), 200);
		Assert.assertEquals(FILE_SERVLET_REDIRECT_URL + SOME_PATH_IN_APP, rsp.getForwardedUrl());
		
		// Now try a custom base URL
		Context.getAdministrationService().saveGlobalProperty(
		    new GlobalProperty(AppManager.KEY_APP_BASE_URL, SOME_RANDOM_BASE_URL));
		mockFilterChain = new MockFilterChain();
		req = new MockHttpServletRequest("GET", SOME_RANDOM_BASE_URI + SOME_PATH_IN_APP);
		rsp = new MockHttpServletResponse();
		owaFilter.doFilter(req, rsp, mockFilterChain);
		Assert.assertEquals(rsp.getStatus(), 200);
		Assert.assertEquals(FILE_SERVLET_REDIRECT_URL + SOME_PATH_IN_APP, rsp.getForwardedUrl());
		
		// Ensure non-OWA base URLs are ignored
		req = new MockHttpServletRequest("GET", DEFAULT_APP_BASE_URI + SOME_PATH_IN_APP); // we can reuse this URL because the global property has been reset
		rsp = new MockHttpServletResponse();
		owaFilter.doFilter(req, rsp, mockFilterChain);
		Assert.assertEquals(rsp.getStatus(), 200);
		Assert.assertNull(rsp.getForwardedUrl());
		
		// testing that OwaFilter uses redirects to 'Add On Manager' using 'login.url'
		Context.getAdministrationService().setGlobalProperty("login.url", ADD_ON_MANAGER_REDIRECT_URL);
		Context.logout();
		mockFilterChain = new MockFilterChain();
		req = new MockHttpServletRequest("GET", "openmrs/index.htm");
		req.setServletPath("/index.htm");
		owaFilter.doFilter(req, rsp, mockFilterChain);
		Assert.assertEquals(rsp.getStatus(), 302);
		Assert.assertEquals("/" + ADD_ON_MANAGER_REDIRECT_URL, rsp.getRedirectedUrl());
	}
	
	/**
	 * Test that requests for apps from unauthenticated users are sent to the redirect servlet by
	 * default
	 */
	@Test
	public void testOwaFilterRedirectsUnauthenticatedRequestsToLoginUrlByDefault() throws Exception {
		Context.logout();
		MockFilterChain mockFilterChain = new MockFilterChain();
		MockHttpServletResponse rsp = new MockHttpServletResponse();
		unauthenticatedFilter().doFilter(unauthenticatedAppRequest(), rsp, mockFilterChain);
		Assert.assertEquals(REDIRECT_SERVLET_URL + SOME_PATH_IN_APP, rsp.getForwardedUrl());
		Assert.assertNull(mockFilterChain.getRequest());
	}
	
	/**
	 * Test that requests for apps from unauthenticated users are passed on, for another filter to
	 * handle, when redirecting to login.url is disabled
	 */
	@Test
	public void testOwaFilterPassesOnUnauthenticatedRequestsIfRedirectToLoginUrlDisabled() throws Exception {
		Context.getAdministrationService().setGlobalProperty(OwaFilter.REDIRECT_TO_LOGIN_URL, "false");
		Context.logout();
		MockFilterChain mockFilterChain = new MockFilterChain();
		MockHttpServletResponse rsp = new MockHttpServletResponse();
		MockHttpServletRequest req = unauthenticatedAppRequest();
		unauthenticatedFilter().doFilter(req, rsp, mockFilterChain);
		Assert.assertNull(rsp.getForwardedUrl());
		Assert.assertNull(rsp.getRedirectedUrl());
		Assert.assertEquals(req, mockFilterChain.getRequest());
	}
	
	private OwaFilter unauthenticatedFilter() throws Exception {
		OwaFilter owaFilter = new OwaFilter();
		owaFilter.init(filterConfig);
		return owaFilter;
	}
	
	private MockHttpServletRequest unauthenticatedAppRequest() {
		MockHttpServletRequest req = new MockHttpServletRequest("GET", DEFAULT_APP_BASE_URI + SOME_PATH_IN_APP);
		req.setServletPath(DEFAULT_APP_BASE_SERVLET_PATH);
		return req;
	}
}

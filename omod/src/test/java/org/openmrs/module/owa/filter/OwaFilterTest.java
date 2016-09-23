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
	}
}

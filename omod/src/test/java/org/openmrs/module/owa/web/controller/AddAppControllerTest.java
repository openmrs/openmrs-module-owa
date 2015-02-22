/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.owa.web.controller;

import javax.servlet.http.HttpServletRequest;
import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import org.mockito.Mockito;
import org.openmrs.api.context.ServiceContext;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;

/**
 * @author sunbiz
 */
public class AddAppControllerTest extends BaseModuleWebContextSensitiveTest {
	
	MessageSourceService originalMessageSourceService;
	
	MessageSourceService messageSourceService;
	
	public AddAppControllerTest() {
		
	}
	
	@Before
	public void setUpMockMessageSourceService() {
		originalMessageSourceService = ServiceContext.getInstance().getMessageSourceService();
		messageSourceService = Mockito.mock(MessageSourceService.class);
	}
	
	@After
	public void restoreOriginalMessageSourceService() {
		if (originalMessageSourceService != null) {
			ServiceContext.getInstance().setMessageSourceService(originalMessageSourceService);
		}
	}
	
	/**
	 * Test of upload method, of class AddAppController.
	 */
	@Test
	public void testUpload() throws Exception {
		System.out.println("upload");
		HttpServletRequest request = new MockHttpServletRequest(new MockServletContext(), "POST", "/module/owa/addApp.htm");
		AddAppController instance = new AddAppController();
		String expResult = "";
	}
	
}

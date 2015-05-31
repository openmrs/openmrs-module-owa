/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.owa.web.controller;

import java.io.FileInputStream;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.mockito.Mockito;
import org.openmrs.api.context.ServiceContext;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.util.OpenmrsClassLoader;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
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
	public void testUploadofNotaZipArchieve() throws Exception {
		HttpServletRequest request = new MockHttpServletRequest(new MockServletContext(), "POST", "/module/owa/addApp.htm");		
		URL url = OpenmrsClassLoader.getInstance().getResource("testing");
		MockMultipartFile multifile = new MockMultipartFile("testFile", "testing", null, new FileInputStream(url.getFile()));		
		AddAppController controller = (AddAppController) applicationContext
		        .getBean("addAppController");		
		controller.upload(multifile, request);
		Assert.assertEquals(request.getSession().getAttribute("errormessage"),"owa.not_a_zip");			
	}
	
	@Test
	public void testUploadofEmptyFile() throws Exception {
		HttpServletRequest request = new MockHttpServletRequest(new MockServletContext(), "POST", "/module/owa/addApp.htm");		
		URL url = OpenmrsClassLoader.getInstance().getResource("file");
		MockMultipartFile multifile = new MockMultipartFile("testFile","file", null, new FileInputStream(url.getFile()));		
		AddAppController controller = (AddAppController) applicationContext
		        .getBean("addAppController");		
		controller.upload(multifile, request);
		Assert.assertEquals(request.getSession().getAttribute("errormessage"),"owa.blank_zip");			
	}
	
	@Test
	public void testUploadofZipFilewithoutManifest() throws Exception {
		HttpServletRequest request = new MockHttpServletRequest(new MockServletContext(), "POST", "/module/owa/addApp.htm");		
		URL url = OpenmrsClassLoader.getInstance().getResource("ZipFilewithoutmanifest.zip");
		MockMultipartFile multifile = new MockMultipartFile("testFile","ZipFilewithoutmanifest.zip", null, new FileInputStream(url.getFile()));		
		AddAppController controller = (AddAppController) applicationContext
		        .getBean("addAppController");		
		controller.upload(multifile, request);
		Assert.assertEquals(request.getSession().getAttribute("errormessage"),"owa.manifest_not_found");			
	}
}
	


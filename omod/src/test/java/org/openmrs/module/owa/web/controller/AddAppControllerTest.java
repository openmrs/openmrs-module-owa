package org.openmrs.module.owa.web.controller;

import java.io.File;
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
import org.openmrs.web.WebConstants;
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
		AddAppController controller = (AddAppController) applicationContext.getBean("addAppController");
		controller.upload(multifile, request);
		Assert.assertEquals("owa.not_a_zip", request.getSession().getAttribute(WebConstants.OPENMRS_ERROR_ATTR));
	}
	
	@Test
	public void testUploadofEmptyZipFile() throws Exception {
		HttpServletRequest request = new MockHttpServletRequest(new MockServletContext(), "POST", "/module/owa/addApp.htm");
		FileInputStream file = new FileInputStream(new File("src/test/resources/Blank_Zip.zip"));
		MockMultipartFile multifile = new MockMultipartFile("testFile", "Blank_Zip.zip", null, file);
		AddAppController controller = (AddAppController) applicationContext.getBean("addAppController");
		controller.upload(multifile, request);
		Assert.assertEquals("owa.blank_zip", request.getSession().getAttribute(WebConstants.OPENMRS_ERROR_ATTR));
	}
	
	@Test
	public void testUploadofZipFilewithoutManifest() throws Exception {
		HttpServletRequest request = new MockHttpServletRequest(new MockServletContext(), "POST", "/module/owa/addApp.htm");
		FileInputStream file = new FileInputStream(new File("src/test/resources/Zipwithoutmanifest.zip"));
		MockMultipartFile multifile = new MockMultipartFile("testFile", "Zipwithoutmanifest.zip", "application/zip,.zip",
		        file);
		AddAppController controller = (AddAppController) applicationContext.getBean("addAppController");
		controller.upload(multifile, request);
		Assert.assertEquals("owa.manifest_not_found", request.getSession().getAttribute(WebConstants.OPENMRS_ERROR_ATTR));
	}
	
	@Test
	public void testUploadofZipFilewithProperManifest() throws Exception {
		HttpServletRequest request = new MockHttpServletRequest(new MockServletContext(), "POST", "/module/owa/addApp.htm");
		FileInputStream file = new FileInputStream(new File("src/test/resources/owabasicapp.zip"));
		MockMultipartFile multifile = new MockMultipartFile("testFile", "owabasicapp.zip", "application/zip,.zip", file);
		AddAppController controller = (AddAppController) applicationContext.getBean("addAppController");
		controller.upload(multifile, request);
		Assert.assertEquals("owa.app_installed", request.getSession().getAttribute(WebConstants.OPENMRS_MSG_ATTR));
	}
	
}

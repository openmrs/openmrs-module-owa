package org.openmrs.module.owa.web.controller;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ServiceContext;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.owa.App;
import org.openmrs.module.owa.AppManager;
import org.openmrs.web.WebConstants;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockServletContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

public class OwaRestControllerTest extends BaseModuleWebContextSensitiveTest {
	
	MessageSourceService originalMessageSourceService;
	
	MessageSourceService messageSourceService;
	
	public OwaRestControllerTest() {
		
	}
	
	@Before
	public void setUpMockMessageSourceService() {
		originalMessageSourceService = ServiceContext.getInstance().getMessageSourceService();
		messageSourceService = Mockito.mock(MessageSourceService.class);
		
		Context.getAdministrationService().saveGlobalProperty(new GlobalProperty(AppManager.KEY_APP_FOLDER_PATH, "owa"));
	}
	
	@After
	public void restoreOriginalMessageSourceService() {
		if (originalMessageSourceService != null) {
			ServiceContext.getInstance().setMessageSourceService(originalMessageSourceService);
		}
	}
	
	@Test
	public void getAppList_shouldNotReturnNull() throws Exception {
		OwaRestController controller = (OwaRestController) applicationContext.getBean("owaRestController");
		Assert.assertNotNull(controller.getAppList());
	}
	
	@Test
	public void getSettings_shouldNotReturnNull() throws Exception {
		OwaRestController controller = (OwaRestController) applicationContext.getBean("owaRestController");
		Assert.assertNotNull(controller.getSettings());
	}
	
	@Test
	public void updateSettings_shouldNotReturnNull() throws Exception {
		OwaRestController controller = (OwaRestController) applicationContext.getBean("owaRestController");
		List<GlobalProperty> settings = Context.getAdministrationService().getGlobalProperties();
		Assert.assertNotNull(controller.updateSettings(settings));
	}
	
	/**
	 * OwaRestController upload method test casing.
	 */
	@Test
	public void upload_caseNotAZipFile() throws Exception {
		HttpServletRequest request = new MockHttpServletRequest(new MockServletContext(), "POST", "/rest/owa/addapp");
		HttpServletResponse response = new MockHttpServletResponse();
		FileInputStream file = new FileInputStream(new File("src/test/resources/testing".replace("/", File.separator)));
		MockMultipartFile mmf = new MockMultipartFile("nonZipFile", "testing", null, file);
		OwaRestController controller = (OwaRestController) applicationContext.getBean("owaRestController");
		controller.upload(mmf, request, response);
		Assert.assertEquals("owa.not_a_zip", request.getSession().getAttribute(WebConstants.OPENMRS_ERROR_ATTR));
	}
	
	@Test
	public void upload_caseEmptyZipFile() throws Exception {
		HttpServletRequest request = new MockHttpServletRequest(new MockServletContext(), "POST", "/rest/owa/addapp");
		HttpServletResponse response = new MockHttpServletResponse();
		FileInputStream file = new FileInputStream(new File("src/test/resources/Blank_Zip.zip".replace("/", File.separator)));
		MockMultipartFile mmf = new MockMultipartFile("emptyZipFile", "Blank_Zip.zip", null, file);
		OwaRestController controller = (OwaRestController) applicationContext.getBean("owaRestController");
		controller.upload(mmf, request, response);
		Assert.assertEquals("owa.blank_zip", request.getSession().getAttribute(WebConstants.OPENMRS_ERROR_ATTR));
	}
	
	@Test
	public void upload_caseZipFileWithoutManifest() throws Exception {
		HttpServletRequest request = new MockHttpServletRequest(new MockServletContext(), "POST", "/rest/owa/addapp");
		HttpServletResponse response = new MockHttpServletResponse();
		FileInputStream file = new FileInputStream(new File("src/test/resources/Zipwithoutmanifest.zip".replace("/",
		    File.separator)));
		MockMultipartFile mmf = new MockMultipartFile("zipFileNoManifest", "Zipwithoutmanifest.zip", "application/zip,.zip",
		        file);
		OwaRestController controller = (OwaRestController) applicationContext.getBean("owaRestController");
		controller.upload(mmf, request, response);
		Assert.assertEquals("owa.manifest_not_found", request.getSession().getAttribute(WebConstants.OPENMRS_ERROR_ATTR));
	}
	
	@Test
	public void upload_caseProperZipFile() throws Exception {
		HttpServletRequest request = new MockHttpServletRequest(new MockServletContext(), "POST", "/rest/owa/addapp");
		HttpServletResponse response = new MockHttpServletResponse();
		FileInputStream file = new FileInputStream(new File("src/test/resources/designer.zip".replace("/", File.separator)));
		MockMultipartFile multifile = new MockMultipartFile("properZipFile", "designer.zip", "application/zip,.zip", file);
		OwaRestController controller = (OwaRestController) applicationContext.getBean("owaRestController");
		controller.upload(multifile, request, response);
		Assert.assertEquals("owa.app_installed", request.getSession().getAttribute(WebConstants.OPENMRS_MSG_ATTR));
	}
	
	@Test
	public void install_rightDownloadUrl() throws Exception {
		HttpServletRequest request = new MockHttpServletRequest(new MockServletContext(), "POST", "/rest/owa/installapp");
		HttpServletResponse response = new MockHttpServletResponse();
		String downloadUrl = "https://bintray.com/openmrs/owa/download_file?file_path=cohortbuilder-1.0.0-beta.zip";
		OwaRestController controller = (OwaRestController) applicationContext.getBean("owaRestController");
		InstallAppRequestObject requestData = new InstallAppRequestObject(downloadUrl);
		List<App> appList = controller.install(requestData, request, response);
		Assert.assertEquals("owa.app_installed", request.getSession().getAttribute(WebConstants.OPENMRS_MSG_ATTR));
	}
	
	@Test
	public void install_wrongDownloadUrl() throws Exception {
		HttpServletRequest request = new MockHttpServletRequest(new MockServletContext(), "POST", "/rest/owa/installapp");
		HttpServletResponse response = new MockHttpServletResponse();
		String downloadUrl = "https://bintray.com/openmrs/owa/download_file?file_path=notAZip.zip";
		OwaRestController controller = (OwaRestController) applicationContext.getBean("owaRestController");
		InstallAppRequestObject requestData = new InstallAppRequestObject(downloadUrl);
		List<App> appList = controller.install(requestData, request, response);
		Assert.assertEquals("owa.not_a_zip", request.getSession().getAttribute(WebConstants.OPENMRS_ERROR_ATTR));
	}
}

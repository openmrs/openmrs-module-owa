package org.openmrs.module.owa.web.controller;

import org.apache.struts.mock.MockHttpServletResponse;
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
import org.springframework.mock.web.MockServletContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class InstallAppControllerTest extends BaseModuleWebContextSensitiveTest {
	
	MessageSourceService originalMessageSourceService;
	
	MessageSourceService messageSourceService;
	
	public InstallAppControllerTest() {
		
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
	
	/**
	 * Test of install method, of class InstallAppController.
	 */
	@Test
	public void install_rightDownloadUrl() throws Exception {
		HttpServletRequest request = new MockHttpServletRequest(new MockServletContext(), "POST", "/module/owa/installapp");
		HttpServletResponse response = new MockHttpServletResponse();
		String downloadUrl = "https://bintray.com/openmrs/owa/download_file?file_path=cohortbuilder-1.0.0-beta.zip";
		InstallAppController controller = (InstallAppController) applicationContext.getBean("installAppController");
		InstallAppRequestObject requestData = new InstallAppRequestObject(downloadUrl);
		List<App> appList = controller.install(requestData, request, response);
		Assert.assertEquals("owa.app_installed", request.getSession().getAttribute(WebConstants.OPENMRS_MSG_ATTR));
	}
	
	@Test
	public void install_wrongDownloadUrl() throws Exception {
		HttpServletRequest request = new MockHttpServletRequest(new MockServletContext(), "POST", "/module/owa/installapp");
		HttpServletResponse response = new MockHttpServletResponse();
		String downloadUrl = "https://bintray.com/openmrs/owa/download_file?file_path=notAZip.zip";
		InstallAppController controller = (InstallAppController) applicationContext.getBean("installAppController");
		InstallAppRequestObject requestData = new InstallAppRequestObject(downloadUrl);
		List<App> appList = controller.install(requestData, request, response);
		Assert.assertEquals("owa.not_a_zip", request.getSession().getAttribute(WebConstants.OPENMRS_ERROR_ATTR));
	}
}

package org.openmrs.module.owa.impl;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.Module;
import org.openmrs.module.owa.App;
import org.openmrs.module.owa.AppManager;
import org.openmrs.module.owa.OwaListener;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.ReflectionUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class DefaultAppManagerTest extends BaseModuleWebContextSensitiveTest {
	
	DefaultAppManager appManager;
	
	@Autowired
	@Qualifier("adminService")
	AdministrationService administrationService;
	
	@Mock
	OwaListener listener;
	
	File owaDir;
	
	Module module1 = module1 = new Module(null, null, "org.openmrs.module.owa", null, null, "1.9.*");	
	Module module2 = module2 = new Module(null, null, "org.openmrs.module.webservices.rest", null, null, "2.23.0-SNAPSHOT");	
	Module module3 = module3 = new Module(null, null, "org.openmrs.module.fhir", null, null, "1.13.1");	
	Module module4 = module4 = new Module(null, null, "org.openmrs.module.fhir", null, null, "1.9.2");
	
	List<Module> installedModules1 = new ArrayList<>();
	List<Module> installedModules2 = new ArrayList<>();
	
	@Before
	public void setUp() throws Exception {
		initMocks(this);
		appManager = new DefaultAppManager();
		appManager.setOwaListeners(Arrays.asList(listener));
		owaDir = Files.createTempDirectory("owa").toFile();
		administrationService.saveGlobalProperty(new GlobalProperty(AppManager.KEY_APP_FOLDER_PATH, owaDir.getPath()));
	}
	
	@After
	public void deleteTempOwaDir() {
		owaDir.delete();
	}
	
	@Test
	public void shouldNotifyListenerWhenInstallingApp() throws Exception {
		appManager.installApp(getFile("/designer.zip"), "designer.zip", "http://localhost:8080");
		verify(listener).installedApp(any(App.class));
	}
	
	@Test
	public void shouldNotifyListenerWhenDeletingApp() throws Exception {
		String APP_NAME = "Polymer designer";
		appManager.installApp(getFile("/designer.zip"), "designer.zip", "http://localhost:8080");
		App app = findApp(APP_NAME);
		Mockito.reset(listener);
		
		appManager.deleteApp(APP_NAME);
		verify(listener).deletedApp(app);
	}
	
	private File getFile(String file) {
		URL url = getClass().getResource(file);
		return new File(url.getPath());
	}
	
	@Test
	public void shouldDeployToDeployedNameDirectory() throws Exception {
		//given
		File destinationDirectory = new File(owaDir, "uicommons-customized");
		File file = getFile("/refapp-uicommons-customized.zip");
		
		//when
		appManager.installApp(file, "refapp-uicommons-customized.zip", "http://localhost:8080");
		
		//then
		App deployedApp = findApp("Reference Application customized uicommons");
		assertThat(deployedApp, notNullValue());
		assertThat(deployedApp.getFolderName(), is(deployedApp.getDeployedName()));
		
		assertThat(destinationDirectory, exists());
		assertThat(new File(destinationDirectory, "manifest.webapp"), exists());
	}
	
	@Test
	public void shouldOverwriteAppWithSameDeployedName() throws Exception {
		File destinationDirectory = new File(owaDir, "uicommons-customized");
		//given
		File refappCustomizedApp = getFile("/refapp-uicommons-customized.zip");
		appManager.installApp(refappCustomizedApp, "refapp-uicommons-customized.zip", "http://localhost:8080");
		// file 'otherdistro.marker' is used to determine whether refapp-uicommons-customized app
		// had been replaced with otherdistro-uicommons-customized app
		assertThat(new File(destinationDirectory, "otherdistro.marker"), not(exists()));
		File otherCustomizedApp = getFile("/otherdistro-uicommons-customized.zip");
		
		//when other app with deployed name 'uicommons-customized'
		appManager.installApp(otherCustomizedApp, "otherdistro-uicommons-customized.zip", "http://localhost:8080");
		
		//then check if marker file has been deployed
		assertThat(new File(destinationDirectory, "otherdistro.marker"), exists());
	}
	
	private App findApp(String appName) {
		for (App app : appManager.getApps()) {
			if (app.getName().equals(appName)) {
				return app;
			}
		}
		return null;
	}
	
	private Matcher<File> exists() {
		return new BaseMatcher<File>() {
			
			@Override
			public boolean matches(Object item) {
				final File file = (File) item;
				return file.exists();
			}
			
			@Override
			public void describeTo(Description description) {
				description.appendText("file should exist");
			}
			
			@Override
			public void describeMismatch(Object item, Description description) {
				description.appendText("file does not exist");
			}
		};
	}
	
	@Test
	public void shouldNotifyListenerOnStartup() throws Exception {
		// Use reflection since AppManager.init() is private
		Method initMethod = ReflectionUtils.findMethod(DefaultAppManager.class, "init");
		ReflectionUtils.makeAccessible(initMethod);
		initMethod.invoke(appManager);
		
		verify(listener).appsReloaded(anyListOf(App.class));
	}
	
	@Test
	public void shouldReturnEmptyStringWhenRequirementsSatisfied() throws Exception {
		String errorMessage = null;
		installedModules1.add(module1);
		installedModules1.add(module2);
		installedModules1.add(module3);
		
		try {
			errorMessage = appManager.extractMissingRequirementsMessage(getFile("/dummy1-cohortbuilder-1.0.0-beta.zip"), installedModules1);
		}catch(Exception e) {
			
		}
		Assert.assertTrue(errorMessage.contains(""));
	}
	
	@Test
	public void shouldReturnStringWithMissingOpenmrsCoreVersion() throws Exception {
		String errorMessage = null;
		installedModules2.add(module1);
		installedModules2.add(module2);
		installedModules2.add(module4);
		
		try {
			errorMessage = appManager.extractMissingRequirementsMessage(getFile("/dummy2-cohortbuilder-1.0.0-beta.zip"), installedModules2);
		}catch(Exception e) {
			
		}
		Assert.assertTrue(errorMessage.contains("OpenMRS-core version: 2.1.*"));
		Assert.assertTrue(errorMessage.contains("fhir version: 1.13.1"));
		Assert.assertFalse(errorMessage.contains("webservices.rest version: 2.22.0"));
	}
}

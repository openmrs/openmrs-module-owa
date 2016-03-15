package org.openmrs.module.owa.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.owa.App;
import org.openmrs.module.owa.AppManager;
import org.openmrs.module.owa.OwaListener;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.ReflectionUtils;

public class DefaultAppManagerTest extends BaseModuleWebContextSensitiveTest {
	
	DefaultAppManager appManager;
	
	@Autowired
	@Qualifier("adminService")
	AdministrationService administrationService;
	
	@Mock
	OwaListener listener;
	
	@Before
	public void setUp() throws Exception {
		initMocks(this);
		appManager = new DefaultAppManager();
		appManager.setOwaListeners(Arrays.asList(listener));
		administrationService.saveGlobalProperty(new GlobalProperty(AppManager.KEY_APP_FOLDER_PATH, "owa"));
	}
	
	@Test
	public void shouldNotifyListenerWhenInstallingApp() throws Exception {
		File file = new File("src/test/resources/designer.zip".replace("/", File.separator));
		appManager.installApp(file, "designer.zip", "http://localhost:8080");
		verify(listener).installedApp(any(App.class));
	}
	
	@Test
	public void shouldNotifyListenerWhenDeletingApp() throws Exception {
		String APP_NAME = "Polymer designer";
		File file = new File("src/test/resources/designer.zip".replace("/", File.separator));
		appManager.installApp(file, "designer.zip", "http://localhost:8080");
		App app = null;
		for (App candidate : appManager.getApps()) {
			if (candidate.getName().equals(APP_NAME)) {
				app = candidate;
				break;
			}
		}
		Mockito.reset(listener);
		
		appManager.deleteApp(APP_NAME);
		verify(listener).deletedApp(app);
	}
	
	@Test
	public void shouldNotifyListenerOnStartup() throws Exception {
		// Use reflection since AppManager.init() is private
		Method initMethod = ReflectionUtils.findMethod(DefaultAppManager.class, "init");
		ReflectionUtils.makeAccessible(initMethod);
		initMethod.invoke(appManager);
		
		verify(listener).appsReloaded(anyListOf(App.class));
	}
}

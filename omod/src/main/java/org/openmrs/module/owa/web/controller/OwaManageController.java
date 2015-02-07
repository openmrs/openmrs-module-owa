/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0 + Health disclaimer. If a copy of the MPL was not distributed with
 * this file, You can obtain one at http://license.openmrs.org
 */
package org.openmrs.module.owa.web.controller;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.owa.App;
import org.openmrs.module.owa.AppManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * The main controller.
 */
@Controller
public class OwaManageController {
	
	protected final Log log = LogFactory.getLog(OwaManageController.class);
	
	@Autowired
	AppManager appManager;
	
	@ModelAttribute("appList")
	@RequestMapping(value = "/module/owa/manage", method = RequestMethod.GET)
	public List<App> manage(ModelMap model) {
		appManager.reloadApps();
		List<App> appList = appManager.getApps();
		return appList;
	}
	
	@ModelAttribute("settingsValid")
	public boolean settingsValid() {
		boolean settingsValid = false;
		String appFolderPath = Context.getAdministrationService().getGlobalProperty(AppManager.KEY_APP_FOLDER_PATH);
		if (null != appFolderPath) {
			File file = new File(appFolderPath);
			if (file.isDirectory() && Files.isWritable(file.toPath())) {
				settingsValid = true;
			}
		}
		return settingsValid;
	}
	
	@ModelAttribute("appBaseUrl")
	public String getAppBaseUrl() {
		return Context.getAdministrationService().getGlobalProperty(AppManager.KEY_APP_BASE_URL);
	}
	
	@ModelAttribute("appStoreUrl")
	public String getStoreUrl() {
		return Context.getAdministrationService().getGlobalProperty(AppManager.KEY_APP_STORE_URL);
	}
}

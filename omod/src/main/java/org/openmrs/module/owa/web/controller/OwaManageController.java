/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0 + Health disclaimer. If a copy of the MPL was not distributed with
 * this file, You can obtain one at http://license.openmrs.org
 */
package org.openmrs.module.owa.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleUtil;
import org.openmrs.module.owa.App;
import org.openmrs.module.owa.AppManager;
import org.openmrs.module.owa.filter.OwaFilter;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * The main controller.
 */
@Controller
@RequestMapping("/module/owa")
public class OwaManageController {
	
	protected final Log log = LogFactory.getLog(OwaManageController.class);
	
	@Autowired
	AppManager appManager;
	
	@ModelAttribute("appList")
	@RequestMapping(value = "/manage", method = RequestMethod.GET)
	public List<App> manage(ModelMap model) {
		List<App> appList = new ArrayList<>();
		if (Context.hasPrivilege("Manage OWA")) {
			appManager.reloadApps();
			appList = appManager.getApps();
		}
		return appList;
	}
	
	@RequestMapping(value = "/deleteApp", method = RequestMethod.GET)
	public String deleteApp(@RequestParam("appName") String appName, @RequestParam(value= "returnURL", required = false) String returnURL, ModelMap model) {
		if (appName != null && Context.hasPrivilege("Manage OWA")) {
			appManager.deleteApp(appName);
			model.clear();
		}
		return returnURL != null ? returnURL : "redirect:manage.form";
	}
	
	@RequestMapping(value = "/manager", method = RequestMethod.GET)
	public String loadSettings(HttpServletRequest request, ModelMap model) {
		if (Context.hasPrivilege("Manage OWA")) {
			String appFolderPath = appManager.getAppFolderPath();
			String appStoreUrl = getStoreUrl();
			
			if (null == appFolderPath) {
				String owaAppFolderPath = OpenmrsUtil.getApplicationDataDirectory()
				        + (OpenmrsUtil.getApplicationDataDirectory().endsWith(File.separator) ? "owa" : File.separator
				                + "owa");
				appManager.setAppFolderPath(owaAppFolderPath);
			}
			
			if (null == appStoreUrl) {
				appManager.setAppStoreUrl("https://modules.openmrs.org");
			}
			
			model.clear();
		}
		return "redirect:manage.form";
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
	public String getAppBaseUrl(HttpServletRequest request) {
		String serverContext = request.getContextPath();
		String owaBasePath = Context.getAdministrationService().getGlobalProperty(AppManager.KEY_APP_BASE_URL,
		    OwaFilter.DEFAULT_BASE_URL);
		if (OwaFilter.isFullBasePath(owaBasePath)) {
			return owaBasePath;
		} else {
			return serverContext + owaBasePath;
		}
	}
	
	@ModelAttribute("allowAdmin")
	public boolean isAllowAdmin() {
		return ModuleUtil.allowAdmin();
	}
	
	@ModelAttribute("appStoreUrl")
	public String getStoreUrl() {
		return Context.getAdministrationService().getGlobalProperty(AppManager.KEY_APP_STORE_URL);
	}
}

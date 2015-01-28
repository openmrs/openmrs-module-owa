/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0 + Health disclaimer. If a copy of the MPL was not distributed with
 * this file, You can obtain one at http://license.openmrs.org
 */
package org.openmrs.module.owa.web.controller;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.owa.App;
import org.openmrs.module.owa.AppManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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
	
	List<App> appList;
	
	@RequestMapping(value = "/module/owa/manage", method = RequestMethod.GET)
	public void manage(ModelMap model) {
		appManager.reloadApps();
		appList = appManager.getApps();
		System.out.println("APP FOLDER PATH = " + appManager.getAppFolderPath());
		System.out.println("APP BASE URL = " + appManager.getAppBaseUrl());
		System.out.println("APPSTORE URL = " + appManager.getAppStoreUrl());
		System.out.println("AppList = " + appManager.getApps().size());
	}
}

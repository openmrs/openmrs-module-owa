/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0 + Health disclaimer. If a copy of the MPL was not distributed with
 * this file, You can obtain one at http://license.openmrs.org
 */
package org.openmrs.module.owa.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@Autowired
	AppManager appManager;
	
	@RequestMapping(value = "/module/owa/manage", method = RequestMethod.GET)
	public void manage(ModelMap model) {
		appManager.reloadApps();
		log.info("APP FOLDER PATH = " + appManager.getAppFolderPath());
		log.info("APP BASE URL = " + appManager.getAppBaseUrl());
		log.info("APPSTORE URL = " + appManager.getAppStoreUrl());
		log.info("AppList = " + appManager.getApps().size());
	}
}

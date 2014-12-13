package org.openmrs.module.owa.web.controller;

/*
 * Copyright (c) 2004-2014, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.File;
import java.util.List;
import org.openmrs.module.owa.App;
import org.openmrs.module.owa.AppManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Saptarshi Purkayastha
 */
public class AppSettingsAction {
	
	boolean isSaved;
	
	// -------------------------------------------------------------------------
	// Dependencies
	// -------------------------------------------------------------------------
	
	//@Autowired
	private AppManager appManager;
	
	// -------------------------------------------------------------------------
	// Input & Output
	// -------------------------------------------------------------------------
	
	private String appFolderPath;
	
	public String getAppFolderPath() {
		appFolderPath = appManager.getAppFolderPath();
		
		if (null == appFolderPath || appFolderPath.isEmpty()) {
			String realPath = "";/* = ServletActionContext.getServletContext().getRealPath("/")*/
			;
			if (realPath.endsWith("/") || realPath.endsWith("\\")) {
				appFolderPath = realPath + "apps";
			} else {
				appFolderPath = realPath + File.separatorChar + "apps";
			}
			
			appManager.setAppFolderPath(appFolderPath);
		}
		
		return appFolderPath;
	}
	
	public void setAppFolderPath(String appFolderPath) {
		isSaved = true;
		appManager.setAppFolderPath(appFolderPath);
	}
	
	private String appBaseUrl;
	
	public String getAppBaseUrl() {
		appBaseUrl = appManager.getAppBaseUrl();
		
		/*if (null == appBaseUrl || appBaseUrl.isEmpty()) {
			HttpServletRequest request = ServletActionContext.getRequest();
			String realPath = ServletActionContext.getServletContext().getRealPath("/");
			String appsPath = appManager.getAppFolderPath();
			String baseUrl = ContextUtils.getBaseUrl(request);
			String contextPath = request.getContextPath();
			
			if (!contextPath.isEmpty()) {
				appBaseUrl = baseUrl.substring(0, baseUrl.length() - 1) + request.getContextPath() + "/"
				        + ((appsPath.replace("//", "/")).replace(realPath, "")).replace('\\', '/');
			} else {
				appBaseUrl = baseUrl.substring(0, baseUrl.length() - 1)
				        + ((appsPath.replace("//", "/")).replace(realPath, "")).replace('\\', '/');
			}
			
			appManager.setAppBaseUrl(appBaseUrl);
		}*/
		
		return appBaseUrl;
	}
	
	public void setAppBaseUrl(String appBaseUrl) {
		appManager.setAppBaseUrl(appBaseUrl);
	}
	
	public List<App> getAppList() {
		return appManager.getApps();
	}
	
	private String message;
	
	public String getMessage() {
		return message;
	}
	
	// -------------------------------------------------------------------------
	// Action implementation
	// -------------------------------------------------------------------------
	@RequestMapping(value = "/module/owa/appSettings", method = RequestMethod.GET)
	public String execute() {
		//message = i18n.getString("appmanager_saved_settings");
		appManager.reloadApps();
		
		return isSaved ? "test" : "getSuccess";
	}
}

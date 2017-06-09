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
package org.openmrs.module.owa;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * @author Saptarshi Purkayastha
 */
@Component
public interface AppManager {
	
	final String ID = AppManager.class.getName();
	
	final String KEY_APP_FOLDER_PATH = "owa.appFolderPath";
	
	final String KEY_APP_BASE_URL = "owa.appBaseUrl";
	
	final String KEY_APP_STORE_URL = "owa.appStoreUrl";
	
	final String DEFAULT_APP_STORE_URL = "http://appstore.openmrs.org";
	
	/**
	 * Returns a list of all the installed apps at @see getAppFolderPath
	 * 
	 * @return list of installed apps
	 */
	List<App> getApps();
	
	/**
	 * Installs the app.
	 * 
	 * @param file the app file.
	 * @param fileName the name of the app file.
	 * @param rootPath the root path of the instance.
	 * @throws IOException if the app manifest file could not be read.
	 */
	void installApp(File file, String fileName, String rootPath) throws IOException;
	
	/**
	 * Does the app with name appName exist?
	 * 
	 * @param appName
	 * @return
	 */
	boolean exists(String appName);
	
	/**
	 * Deletes the app with the given name.
	 * 
	 * @param name the app name.
	 * @return true if the delete was successful, false if there is no app with the given name or if
	 *         the app could not be removed from the file system.
	 */
	boolean deleteApp(String name);
	
	/**
	 * Reload list of apps.
	 */
	void reloadApps();
	
	/**
	 * Returns the full path to the folder where apps are extracted
	 * 
	 * @return app folder path
	 */
	String getAppFolderPath();
	
	/**
	 * Saves the folder in which apps will be expanded
	 * 
	 * @param appFolderPath
	 */
	void setAppFolderPath(String appFolderPath);
	
	/**
	 * Gets the Base URL for accessing the apps
	 * 
	 * @return the apps baseurl
	 */
	String getAppBaseUrl();
	
	/**
	 * Saves the base URL where apps are installed
	 * 
	 * @param appBaseUrl
	 */
	void setAppBaseUrl(String appBaseUrl);
	
	/**
	 * Returns the url of the app repository
	 * 
	 * @return url of appstore
	 */
	String getAppStoreUrl();
	
	/**
	 * Saves the URL of the apps repository
	 * 
	 * @param appStoreUrl
	 */
	void setAppStoreUrl(String appStoreUrl);
}

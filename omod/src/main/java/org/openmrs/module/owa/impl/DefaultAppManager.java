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
package org.openmrs.module.owa.impl;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.module.owa.App;
import org.openmrs.module.owa.AppManager;
import org.openmrs.module.owa.OwaListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class DefaultAppManager implements AppManager {
	
	private static final Log log = LogFactory.getLog(DefaultAppManager.class);
	
	@Autowired(required = false)
	private List<OwaListener> owaListeners;
	
	/**
	 * In-memory singleton list holding state for apps.
	 */
	private List<App> apps = new ArrayList();
	
	private void init() {
		reloadApps();
	}
	
	public void setOwaListeners(List<OwaListener> owaListeners) {
		this.owaListeners = owaListeners;
	}
	
	@Override
	public List<App> getApps() {
		String baseUrl = getAppBaseUrl();
		
		for (App app : apps) {
			app.setBaseUrl(baseUrl);
		}
		
		return apps;
	}
	
	@Override
	public void installApp(File file, String fileName, String rootPath) throws IOException {
		App app;
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		try (ZipFile zip = new ZipFile(file)) {
			ZipArchiveEntry entry = zip.getEntry("manifest.webapp");
			try (InputStream inputStream = zip.getInputStream(entry)) {
				String manifest = org.apache.commons.io.IOUtils.toString(inputStream);
				app = mapper.readValue(manifest, App.class);
			}
		}

		// ---------------------------------------------------------------------
		// Delete if app is already installed
		// If app specified 'deployed.owa.name', use it instead of default name based on package name
		// ---------------------------------------------------------------------
		String deployedName = fileName.substring(0, fileName.lastIndexOf('.'));
		if(StringUtils.isNotBlank(app.getDeployedName())){
			deployedName = app.getDeployedName();
			//delete app deployed in the same directory
			deleteApp(app.getDeployedName());
		} else {
			deleteApp(app.getName());
		}

		String dest = getAppFolderPath() + File.separator + deployedName;
		unzip(file, dest);

		// ---------------------------------------------------------------------
		// Set openmrs server location
		// ---------------------------------------------------------------------
		File updateManifest = new File(dest + File.separator + "manifest.webapp");

		app.setBaseUrl(getAppBaseUrl());
		app.setFolderName(deployedName);

		if (null != app.getActivities() && null != app.getActivities().getOpenmrs()) {
			if (null != app.getActivities().getOpenmrs().getHref()) {
				if (app.getActivities().getOpenmrs().getHref().equals("*")) {
					app.getActivities().getOpenmrs().setHref(rootPath);
				}
			}
		}

		mapper.writeValue(updateManifest, app);

		if (owaListeners != null) {
			for (OwaListener listener : owaListeners) {
				try {
					listener.installedApp(app);
				} catch (Exception ex) {
					log.error("installedApp listener " + listener + " failed", ex);
				}
			}
		}

		reloadApps(); // Reload app state
	}
	
	private void unzip(File file, String dest) throws IOException {
		try (ZipFile zip = new ZipFile(file)) {
			Enumeration<? extends ZipArchiveEntry> entries = zip.getEntries();
			while (entries.hasMoreElements()) {
				ZipArchiveEntry entry = entries.nextElement();
				File entryDestination = new File(dest, entry.getName());
				if (entry.isDirectory()) {
					entryDestination.mkdirs();
				} else {
					if (!entryDestination.getParentFile().exists()) {
						entryDestination.getParentFile().mkdirs();
					}
					try (InputStream in = zip.getInputStream(entry);
						OutputStream out = new FileOutputStream(entryDestination)) {
						IOUtils.copy(in, out);
					}
				}
			}
		}
	}
	
	@Override
	public boolean exists(String appName) {
		for (App app : getApps()) {
			if (app.getName().equals(appName) || app.getFolderName().equals(appName)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean deleteApp(String name) {
		for (App app : getApps()) {
			if (app.getName().equals(name) || app.getFolderName().equals(name)) {
				try {
					String folderPath = getAppFolderPath() + File.separator + app.getFolderName();
					FileUtils.forceDelete(new File(folderPath));
					if (owaListeners != null) {
						for (OwaListener listener : owaListeners) {
							try {
								listener.deletedApp(app);
							}
							catch (Exception ex) {
								log.error("deleteApp listener " + listener + " failed", ex);
							}
						}
					}
					return true;
				}
				catch (IOException ex) {
					log.error("Could not delete app: " + name, ex);
					return false;
				}
				finally {
					reloadApps(); // Reload app state
				}
			}
		}
		
		return false;
	}
	
	@Override
	public String getAppFolderPath() {
		String appFolderPath = Context.getAdministrationService().getGlobalProperty(KEY_APP_FOLDER_PATH);
		
		File folder = new File(appFolderPath);
		if (!folder.exists()) {
			setAppFolderPath(appFolderPath); // If the global property is set, make sure the folder exists
		}
		
		return appFolderPath;
	}
	
	@Override
	public void setAppFolderPath(String appFolderPath) {
		if (!appFolderPath.isEmpty()) {
			try {
				File folder = new File(appFolderPath);
				if (!folder.exists()) {
					FileUtils.forceMkdir(folder);
				}
			}
			catch (IOException ex) {
				log.error(ex.getLocalizedMessage(), ex);
			}
		}
		Context.getAdministrationService().saveGlobalProperty(new GlobalProperty(KEY_APP_FOLDER_PATH, appFolderPath));
	}
	
	@Override
	public String getAppBaseUrl() {
		return Context.getAdministrationService().getGlobalProperty(KEY_APP_BASE_URL);
	}
	
	@Override
	public void setAppBaseUrl(String appBaseUrl) {
		Context.getAdministrationService().saveGlobalProperty(new GlobalProperty(KEY_APP_BASE_URL, appBaseUrl));
	}
	
	@Override
	public String getAppStoreUrl() {
		return Context.getAdministrationService().getGlobalProperty(KEY_APP_STORE_URL, DEFAULT_APP_STORE_URL);
	}
	
	@Override
	public void setAppStoreUrl(String appStoreUrl) {
		Context.getAdministrationService().saveGlobalProperty(new GlobalProperty(KEY_APP_STORE_URL, appStoreUrl));
	}
	
	// -------------------------------------------------------------------------
	// Supportive methods
	// -------------------------------------------------------------------------
	
	/**
	 * Sets the list of apps with detected apps from the file system.
	 */
	@Override
	public void reloadApps() {
		List<App> appList = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		if (null != getAppFolderPath()) {
			File appFolderPath = new File(getAppFolderPath());
			if (appFolderPath.isDirectory()) {
				File[] listFiles = appFolderPath.listFiles();
				for (File folder : listFiles) {
					if (folder.isDirectory()) {
						File appManifest = new File(folder, "manifest.webapp");
						if (appManifest.exists()) {
							try {
								App app = mapper.readValue(appManifest, App.class);
								app.setFolderName(folder.getName());
								appList.add(app);
							}
							catch (IOException ex) {
								log.error("app manifest is non-standard", ex);
							}
						} else {
							log.error("app doesn't have a manifest");
						}
					}
				}
			} else {
				log.error("appFolder settings is not a directory");
			}
		} else {
			log.error("Incorrect appFolder Path");
		}

		this.apps = appList;
		log.info("Detected apps: " + apps);
		if (owaListeners != null) {
			for (OwaListener listener : owaListeners) {
				try {
					listener.appsReloaded(appList);
				} catch (Exception ex) {
					log.error("appsReloaded listener " + listener + " failed", ex);
				}
			}
		}
	}
}

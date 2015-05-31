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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.ant.compress.taskdefs.Unzip;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.module.owa.App;
import org.openmrs.module.owa.AppManager;

/**
 * @author Saptarshi Purkayastha
 */
public class DefaultAppManager implements AppManager {
	
	private static final Log log = LogFactory.getLog(DefaultAppManager.class);
	
	/**
	 * In-memory singleton list holding state for apps.
	 */
	private List<App> apps = new ArrayList();
	
	private void init() {
		reloadApps();
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
        try (ZipFile zip = new ZipFile(file)) {
            ZipEntry entry = zip.getEntry("manifest.webapp");

            try (InputStream inputStream = zip.getInputStream(entry)) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                App app = mapper.readValue(inputStream, App.class);

                // ---------------------------------------------------------------------
                // Delete if app is already installed
                // ---------------------------------------------------------------------
                String dest = getAppFolderPath() + File.separator + fileName.substring(0, fileName.lastIndexOf('.'));

                if (getApps().contains(app)) {
                    deleteApp(app.getName());
                }

                Unzip unzip = new Unzip();
                unzip.setSrc(file);
                unzip.setDest(new File(dest));
                unzip.execute();

                // ---------------------------------------------------------------------
                // Set openmrs server location
                // ---------------------------------------------------------------------
                File updateManifest = new File(dest + File.separator + "manifest.webapp");
                App installedApp = mapper.readValue(updateManifest, App.class);

                installedApp.setBaseUrl(getAppBaseUrl());
                installedApp.setFolderName(fileName.substring(0, fileName.lastIndexOf('.')));

                if (null != installedApp.getActivities() && null != installedApp.getActivities().getOpenmrs()) {
                    if (null != installedApp.getActivities().getOpenmrs().getHref()) {
                        if (installedApp.getActivities().getOpenmrs().getHref().equals("*")) {
                            installedApp.getActivities().getOpenmrs().setHref(rootPath);
                        }
                    }
                }

                mapper.writeValue(updateManifest, installedApp);
            }
        }

        reloadApps(); // Reload app state
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
		return Context.getAdministrationService().getGlobalProperty(KEY_APP_FOLDER_PATH);
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
                            } catch (IOException ex) {
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
    }
}

/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0 + Health disclaimer. If a copy of the MPL was not
 * distributed with this file, You can obtain one at http://license.openmrs.org
 */
package org.openmrs.module.owa.activator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.owa.AppManager;
import org.openmrs.module.owa.impl.DefaultAppManager;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 */
public class OwaActivator implements ModuleActivator, ServletContextAware {
	
	protected Log log = LogFactory.getLog(getClass());
	
	private static ServletContext servletContext;
	
	public void setServletContext(ServletContext context) {
		this.servletContext = context;
	}
	
	/**
	 * @see ModuleActivator#willRefreshContext()
	 */
	@Override
	public void willRefreshContext() {
		log.info("Refreshing OWA Module");
	}
	
	/**
	 * @see ModuleActivator#contextRefreshed()
	 */
	@Override
	public void contextRefreshed() {
		String owaStarted = Context.getAdministrationService().getGlobalProperty("owa.started");
		if (!Boolean.valueOf(owaStarted)) {
			//not sure if it ever happens, but it was originally implemented that way
			return;
		}
		
		/**
		 * Used to move the files from omod webapp resources to owa folder when omod webapps contain
		 * manifest.webapp or .owa files.
		 */
		String owaAppFolderPath = Context.getAdministrationService().getGlobalProperty(AppManager.KEY_APP_FOLDER_PATH);
		if (owaAppFolderPath == null) {
			owaAppFolderPath = OpenmrsUtil.getApplicationDataDirectory()
			        + (OpenmrsUtil.getApplicationDataDirectory().endsWith(File.separator) ? "owa" : File.separator + "owa");
			Context.getAdministrationService().setGlobalProperty(AppManager.KEY_APP_FOLDER_PATH, owaAppFolderPath);
		}
		
		File owaAppFolder = new File(owaAppFolderPath);
		if (!owaAppFolder.exists()) {
			owaAppFolder.mkdirs();
		}
		
		String openmrsRootPath = servletContext.getRealPath("/");
		String absPath = new File(openmrsRootPath, "WEB-INF" + File.separator + "view" + File.separator + "module")
		        .getAbsolutePath();
		
		File dir = new File(absPath);
		try {
			List<File> files = (List<File>) FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
			for (File file : files) {
				if (file.getName().endsWith(".owa")) {
					File dest = new File(owaAppFolder, file.getName());
					FileUtils.deleteQuietly(dest);
					FileUtils.copyFile(file, dest);
					log.info("Copying owa file from: " + file + " to " + dest);
				} else if (file.getCanonicalPath().contains("manifest.webapp")) {
					File source = file.getParentFile();
					File dest = new File(owaAppFolder, source.getName());
					FileUtils.deleteQuietly(dest);
					FileUtils.copyDirectory(source, dest);
					log.info("Copying owa dir from: " + source + " to " + dest);
				}
			}
		}
		catch (IOException e) {
			log.error(e);
		}
		
		/**
		 * Check if there is any '.zip' or '.owa' file in OWA app directory and deploy them this app
		 * manager is out of Spring context, so apps have to be reloaded here
		 */
		AppManager appManager = new DefaultAppManager();
		appManager.reloadApps();
		File[] files = owaAppFolder.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.getName().endsWith(".zip") || file.getName().endsWith(".owa")) {
					try {
						log.info("Deploying OWA from: " + file.getName() + "...");
						appManager.installApp(file, file.getName(),
						    Context.getAdministrationService().getGlobalProperty(AppManager.KEY_APP_BASE_URL));
						file.delete();
					}
					catch (IOException e) {
						log.error("Failed to deploy OWA from zip file: " + file.getName(), e);
					}
				}
			}
		}
		
		log.info("OWA Module refreshed");
	}
	
	/**
	 * @see ModuleActivator#willStart()
	 */
	@Override
	public void willStart() {
		log.info("Starting OWA Module");
	}
	
	/**
	 * @see ModuleActivator#started()
	 */
	@Override
	public void started() {
		log.info("OWA started");
	}
	
	/**
	 * @see ModuleActivator#willStop()
	 */
	@Override
	public void willStop() {
		log.info("Stopping OWA Module");
	}
	
	/**
	 * @see ModuleActivator#stopped()
	 */
	@Override
	public void stopped() {
		log.info("OWA Module stopped");
	}
	
}

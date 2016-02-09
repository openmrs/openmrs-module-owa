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

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.openmrs.module.owa.AppManager;
import org.openmrs.util.OpenmrsUtil;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 */
public class OwaActivator implements ModuleActivator {

	protected Log log = LogFactory.getLog(getClass());

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
		/**
		 * Used to move the files from omod webapp resources to owa folder when omod webapps contain
		 * manifest.webapp
		 */
		String owaAppFolderPath = Context.getAdministrationService().getGlobalProperty(AppManager.KEY_APP_FOLDER_PATH);
		if (null == owaAppFolderPath) {
			owaAppFolderPath = OpenmrsUtil.getApplicationDataDirectory() + (OpenmrsUtil.getApplicationDataDirectory()
					.endsWith(File.separator) ? "owa" : File.separator + "owa");
			Context.getAdministrationService().setGlobalProperty(AppManager.KEY_APP_FOLDER_PATH, owaAppFolderPath);
		}
		String owaStarted = Context.getAdministrationService().getGlobalProperty("owa.started");
		String realPath = System.getProperty("user.dir");
		realPath = realPath.substring(0, realPath.length() - 3);
		StringBuffer tomcatPath = new StringBuffer(realPath);
		tomcatPath.append("webapps/openmrs");
		StringBuilder absPath = new StringBuilder(tomcatPath + "/WEB-INF");
		absPath.append("/view/module/");
		File dir = new File(absPath.toString().replace("/", File.separator));
		try {
			List<File> files = (List<File>) FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
			for (File file : files) {
				if (file.getCanonicalPath().contains("manifest.webapp") && owaStarted.equalsIgnoreCase("true")) {
					File source = file.getParentFile();
					File dest = new File(owaAppFolderPath + File.separator + source.getName());
					FileUtils.moveDirectory(source, dest);
					log.info("Moving file from: " + source + " to " + dest);
				}
			}
		}
		catch (IOException e) {
			log.error(e);
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

package org.openmrs.module.owa.web.controller;

/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0 + Health disclaimer. If a copy of the MPL was not distributed with
 * this file, You can obtain one at http://license.openmrs.org
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.ModuleUtil;
import org.openmrs.module.owa.App;
import org.openmrs.module.owa.AppManager;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import org.openmrs.module.owa.utils.OwaUtils;

/**
 * @author Saptarshi Purkayastha
 */

/**
 * @deprecated use {@link OwaManagerRestController}
 */
@Deprecated
@Controller
public class OwaRestController {
	
	private static final Log log = LogFactory.getLog(OwaRestController.class);
	
	// -------------------------------------------------------------------------
	// Dependencies
	// -------------------------------------------------------------------------
	@Autowired
	private AppManager appManager;
	
	@Autowired
	private MessageSourceService messageSourceService;
	
	// -------------------------------------------------------------------------
	// REST implementation
	// -------------------------------------------------------------------------
	@RequestMapping(value = "/rest/owa/applist", method = RequestMethod.GET)
	@ResponseBody
	public List<App> getAppList() {
		List<App> appList = new ArrayList<>();
		if(Context.hasPrivilege("Manage OWA")){
			appManager.reloadApps();
			appList = appManager.getApps();
		}
		return appList;
	}
	
	@RequestMapping(value = "/rest/owa/settings", method = RequestMethod.GET)
	@ResponseBody
	public List<GlobalProperty> getSettings() {
		List<GlobalProperty> owaSettings = new ArrayList<>();
		if(Context.hasPrivilege("Manage OWA")){
			owaSettings.add(Context.getAdministrationService().getGlobalPropertyObject(AppManager.KEY_APP_FOLDER_PATH));
			owaSettings.add(Context.getAdministrationService().getGlobalPropertyObject(AppManager.KEY_APP_BASE_URL));
			owaSettings.add(Context.getAdministrationService().getGlobalPropertyObject(AppManager.KEY_APP_STORE_URL));
		}
		return owaSettings;
	}
	
	@RequestMapping(value = "/rest/owa/settings", method = RequestMethod.POST)
	@ResponseBody
	public List<GlobalProperty> updateSettings(List<GlobalProperty> settings) {
		List<GlobalProperty> owaSettings = new ArrayList<>();
		if(Context.hasPrivilege("Manage OWA")){
			if (null != settings) {
				for (GlobalProperty gp : settings) {
					Context.getAdministrationService().saveGlobalProperty(gp);
					owaSettings.add(gp);
				}
			}
		}
		return owaSettings;
	}
	
	@RequestMapping(value = "/rest/owa/addapp", method = RequestMethod.POST)
	@ResponseBody
	public List<App> upload(@RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws IOException {
		List<App> appList = new ArrayList<>();
		if(Context.hasPrivilege("Manage OWA")){
			String message;
			HttpSession session = request.getSession();
			if (!file.isEmpty()) {
				String fileName = OwaUtils.removeVersionNumber(file.getOriginalFilename());
				File uploadedFile = new File(file.getOriginalFilename());
				file.transferTo(uploadedFile);
				try (ZipFile zip = new ZipFile(uploadedFile)) {
					if (zip.size() == 0) {
						message = messageSourceService.getMessage("owa.blank_zip");
						uploadedFile.delete();
						log.warn("Zip file is empty");
						session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, message);
						response.sendError(500, message);
					} else {
						ZipEntry entry = zip.getEntry("manifest.webapp");
						if (entry == null) {
							message = messageSourceService.getMessage("owa.manifest_not_found");
							log.warn("Manifest file could not be found in app");
							uploadedFile.delete();
							session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, message);
							response.sendError(500, message);
						} else {
							String contextPath = request.getScheme() + "://" + request.getServerName() + ":"
									+ request.getServerPort() + request.getContextPath();
							appManager.installApp(uploadedFile, fileName, contextPath);
							uploadedFile.delete();
							message = messageSourceService.getMessage("owa.app_installed");
							session.setAttribute(WebConstants.OPENMRS_MSG_ATTR, message);
						}
					}
				} catch (Exception e) {
					message = e.getMessage();
					log.warn(message);
					uploadedFile.delete();
					session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, message);
					response.sendError(500, message);
				}
			}
			appManager.reloadApps();
			appList = appManager.getApps();
		}
		return appList;
	}
	
	@RequestMapping(value = "/rest/owa/installapp", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<List<App>> install(@RequestBody InstallAppRequestObject urlObject,
	        HttpServletRequest request, HttpServletResponse response) throws IOException {
		String message;
		String fileName = null;
		List<App> appList = new ArrayList<>();
		Boolean checkInstall = false;

		if (Context.hasPrivilege("Manage OWA")) {
			HttpSession session = request.getSession();
			URL downloadUrl = null;
			String installUrl = urlObject.getUrlValue();

			if (!installUrl.contains(".zip")) {
				message = messageSourceService.getMessage("owa.invalid_owa_Url");
				log.warn("Invalid URL to OWA download. The URL is expected to link to a zip file");
				session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, message);
				response.sendError(400, message);
				return new ResponseEntity<List<App>>(HttpStatus.BAD_REQUEST);
			}

			try {
				downloadUrl = ResourceUtils.getURL(installUrl);
				fileName = OwaUtils.getFileName(installUrl);
				if (fileName == null || fileName.isEmpty()) {
					message = messageSourceService.getMessage("owa.missing_owa_file");
					log.warn("No specified download file in Installation URL");
					session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, message);
					response.sendError(400, message);
					return new ResponseEntity<List<App>>(HttpStatus.BAD_REQUEST);
				}
				InputStream inputStream = ModuleUtil.getURLStream(downloadUrl);
				log.info("Url pathname: " + downloadUrl.getPath());
				File file = ModuleUtil.insertModuleFile(inputStream, fileName);

				try(ZipFile zip = new ZipFile(file)){
					if (zip.size() == 0) {
						message = messageSourceService.getMessage("owa.blank_zip");
						log.warn("Zip file is empty");
						session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, message);
						response.sendError(400, message);
						file.delete();
						return new ResponseEntity<List<App>>(HttpStatus.BAD_REQUEST);
					} else {
						ZipEntry entry = zip.getEntry("manifest.webapp");
						if (entry == null) {
							message = messageSourceService.getMessage("owa.manifest_not_found");
							log.warn("Manifest file could not be found in app");
							session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, message);
							response.sendError(400, message);
							file.delete();
							return new ResponseEntity<List<App>>(HttpStatus.BAD_REQUEST);
						} else {
							String contextPath = request.getScheme() + "://" + request.getServerName() + ":"
									+ request.getServerPort() + request.getContextPath();
							appManager.installApp(file, fileName, contextPath);
							message = messageSourceService.getMessage("owa.app_installed");
							response.setStatus(200);
							file.delete();
							session.setAttribute(WebConstants.OPENMRS_MSG_ATTR, message);
							appManager.reloadApps();
							appList = appManager.getApps();
							return new ResponseEntity<List<App>>(appList, HttpStatus.OK);
						}
					}
				}catch(Exception e){
					message = messageSourceService.getMessage("owa.not_a_zip");
					log.warn("App is not a zip archive");
					file.delete();
					session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, message);
					response.sendError(400, message);
					return new ResponseEntity<List<App>>(HttpStatus.BAD_REQUEST);
				}
			}
			catch (FileNotFoundException e) {
				log.warn(e.getMessage());
				session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, e.getMessage());
				response.sendError(400, e.getMessage());
				return new ResponseEntity<List<App>>(HttpStatus.BAD_REQUEST);
			}
		}
		return new ResponseEntity<List<App>>(HttpStatus.FORBIDDEN);
	}
	
	@RequestMapping(value = "/rest/owa/allowModuleWebUpload", method = RequestMethod.GET)
	@ResponseBody
	public Boolean allowWebAdmin(HttpServletRequest request, HttpServletResponse response) {
		return ModuleUtil.allowAdmin();
	}
}

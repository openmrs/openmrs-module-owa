package org.openmrs.module.owa.web.controller;

import java.io.File;
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
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.ModuleUtil;
import org.openmrs.module.owa.App;
import org.openmrs.module.owa.AppManager;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.util.ResourceUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class InstallAppController {
	
	private static final Log log = LogFactory.getLog(AddAppController.class);
	
	@Autowired
	private MessageSourceService messageSourceService;
	
	@Autowired
	private AppManager appManager;
	
	public String message;
	
	@RequestMapping(value = "/module/owa/installapp", method = RequestMethod.POST)
	@ResponseBody
	public List<App> install(@RequestBody InstallAppRequestObject urlObject, HttpServletRequest request,
	        HttpServletResponse response) throws IOException {
		List<App> appList = new ArrayList<>();

		String url = urlObject.getUrlValue();
		URL downloadUrl = null;
		if (ResourceUtils.isUrl(url)) {
			downloadUrl = new URL(url);
		}
		if (Context.hasPrivilege("Manage OWA")) {
			HttpSession session = request.getSession();
			if (!url.isEmpty() && downloadUrl != null) {
				InputStream inputStream = ModuleUtil.getURLStream(downloadUrl);
				log.warn("url pathname: " + downloadUrl.getPath());
				String fileName = downloadUrl.getQuery().substring(downloadUrl.getQuery().lastIndexOf("=") + 1);
				File file = ModuleUtil.insertModuleFile(inputStream, fileName);
				try (ZipFile zip = new ZipFile(file)) {
					if (zip.size() == 0) {
						message = messageSourceService.getMessage("owa.blank_zip");
						log.warn("Zip file is empty");
						session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, message);
						// response.sendError(500, message);
					} else {
						ZipEntry entry = zip.getEntry("manifest.webapp");
						if (entry == null) {
							message = messageSourceService.getMessage("owa.manifest_not_found");
							log.warn("Manifest file could not be found in app");
							file.delete();
							session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, message);
							// response.sendError(500, message);
						} else {
							String contextPath = request.getScheme() + "://" + request.getServerName() + ":"
							        + request.getServerPort() + request.getContextPath();
							appManager.installApp(file, fileName, contextPath);
							message = messageSourceService.getMessage("owa.app_installed");
							session.setAttribute(WebConstants.OPENMRS_MSG_ATTR, message);
						}
					}
				}
				catch (Exception e) {
					message = messageSourceService.getMessage("owa.not_a_zip");
					log.warn("App is not a zip archive");
					file.delete();
					session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, message);
					// response.sendError(500, message);
				}
			} else {
				message = "Invalid url";
				log.warn("Invalid url");
				session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, message);
				// response.sendError(400, message);
			}
			appManager.reloadApps();
			appList = appManager.getApps();
		}
		return appList;
	}
}

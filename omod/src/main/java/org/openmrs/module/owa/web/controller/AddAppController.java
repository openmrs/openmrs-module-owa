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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.owa.AppManager;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Saptarshi Purkayastha
 */
@Controller
public class AddAppController {
	
	private static final Log log = LogFactory.getLog(AddAppController.class);
	
	private static final String FAILURE = "failure";
	
	//@Autowired
	//private AppManager appManager;
	
	// -------------------------------------------------------------------------
	// Input & Output
	// -------------------------------------------------------------------------
	private File file;
	
	public void setUpload(File file) {
		this.file = file;
	}
	
	private String fileName;
	
	public void setUploadFileName(String fileName) {
		this.fileName = fileName;
	}
	
	private String message;
	
	public String getMessage() {
		return message;
	}
	
	// -------------------------------------------------------------------------
	// Action implementation
	// -------------------------------------------------------------------------
	@RequestMapping(value = "/module/owa/addApp", method = RequestMethod.GET)
	public void execute() {
		/*HttpServletRequest request = Context.getC..getRequest();

		 if (file == null) {
		 message = i18n.getString("appmanager_no_file_specified");
		 log.warn("No file specified");
		 return FAILURE;
		 }

		 if (!StreamUtils.isZip(new BufferedInputStream(new FileInputStream(file)))) {
		 message = i18n.getString("appmanager_not_zip");
		 log.warn("App is not a zip archive");
		 return FAILURE;
		 }

		 try (ZipFile zip = new ZipFile(file)) {
		 ZipEntry entry = zip.getEntry("manifest.webapp");

		 if (entry == null) {
		 zip.close();
		 message = i18n.getString("appmanager_manifest_not_found");
		 log.warn("Manifest file could not be found in app");
		 return FAILURE;
		 }

		 try {
		 String contextPath = Context.getContextPath(request);

		 appManager.installApp(file, fileName, contextPath);

		 message = i18n.getString("appmanager_install_success");

		 return SUCCESS;
		 } catch (JsonParseException ex) {
		 message = i18n.getString("appmanager_invalid_json");
		 log.error("Error parsing JSON in manifest", ex);
		 return FAILURE;
		 } catch (IOException ex) {
		 message = i18n.getString("appmanager_could_not_read_file_check_server_permissions");
		 log.error("App could not not be read, check server permissions");
		 return FAILURE;
		 }
		 }*/
	}
}

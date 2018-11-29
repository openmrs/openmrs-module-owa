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
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.owa.AppManager;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Saptarshi Purkayastha
 */
@Controller
public class AddAppController {
	
	private static final Log log = LogFactory.getLog(AddAppController.class);
	
	@Autowired
	private MessageSourceService messageSourceService;
	
	@Autowired
	private AppManager appManager;
	
	private String message;
	
	@RequestMapping(value = "/module/owa/addApp", method = RequestMethod.POST)
        public String upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {
                if(Context.hasPrivilege("Manage OWA")){
                        HttpSession session = request.getSession();		
                        if (!file.isEmpty()) {
                            String fileName = file.getOriginalFilename();
                            File uploadedFile = new File(file.getOriginalFilename());
                            file.transferTo(uploadedFile);            
                                try (ZipFile zip = new ZipFile(uploadedFile)){                	 
                                    if(zip.size() == 0){
                                        message = messageSourceService.getMessage("owa.blank_zip");
                                        log.warn("Zip file is empty");       
                                        session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, message); 
                                        return "redirect:manage.form";
                                        }
                                    ZipEntry entry = zip.getEntry("manifest.webapp");
                                    if (entry == null) {
                                        message = messageSourceService.getMessage("owa.manifest_not_found");
                                        log.warn("Manifest file could not be found in app");
                                        uploadedFile.delete();
                                        session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, message);
                                    } 
                                    else {
                                        String contextPath = request.getScheme() + "://" + request.getServerName() + ":"
                                                + request.getServerPort() + request.getContextPath();
                                        appManager.installApp(uploadedFile, fileName, contextPath);
                                        message = messageSourceService.getMessage("owa.app_installed");
                                        session.setAttribute(WebConstants.OPENMRS_MSG_ATTR, message);
                                    }
                                }
                                catch(Exception e) {
                                    message = e.getMessage();
                                        log.warn(message);
                                    uploadedFile.delete();
                                    session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, message);
                                    return "redirect:manage.form";
                                }
                        }
                }
                return "redirect:manage.form";
	}
}

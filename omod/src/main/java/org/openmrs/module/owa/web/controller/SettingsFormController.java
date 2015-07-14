package org.openmrs.module.owa.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/module/owa")
public class SettingsFormController {
	
	private static final Log log = LogFactory.getLog(SettingsFormController.class);
	
	@Autowired
	private MessageSourceService messageSourceService;
	
	private String message;
	
	@ModelAttribute("globalProps")
	@RequestMapping(value = "/settings", method = RequestMethod.GET)
	public List<GlobalProperty> globalProperties(ModelMap model) {
		List<GlobalProperty> globalProps = Context.getAdministrationService().getGlobalPropertiesByPrefix("owa");
		return globalProps;
	}
	
	@RequestMapping(value = "/settings", method = RequestMethod.POST)
	public String handleSubmission(@RequestParam("PROP_VAL_NAME") String[] PROP_VAL_NAME,
	        @RequestParam("PROP_NAME") String[] PROP_NAME, Object obj,
	        @RequestParam("PROP_DESC_NAME") String[] PROP_DESC_NAME, HttpServletRequest request) throws IOException {
		
		HttpSession session = request.getSession();
		List<GlobalProperty> formBackingObject = extracted(obj);
		Map<String, GlobalProperty> formBackingObjectMap = new HashMap<String, GlobalProperty>();
		for (GlobalProperty prop : formBackingObject) {
			formBackingObjectMap.put(prop.getProperty(), prop);
		}
		
		List<GlobalProperty> globalPropList = new ArrayList<GlobalProperty>();
		String[] keys = PROP_NAME;
		String[] values = PROP_VAL_NAME;
		String[] descriptions = PROP_DESC_NAME;
		
		for (int x = 0; x < keys.length; x++) {
			String key = keys[x];
			String val = values[x];
			String desc = descriptions[x];
			GlobalProperty tmpGlobalProperty = formBackingObjectMap.get(key);
			if (tmpGlobalProperty != null) {
				tmpGlobalProperty.setPropertyValue(val);
				tmpGlobalProperty.setDescription(desc);
				globalPropList.add(tmpGlobalProperty);
			}
		}
		
		Context.getAdministrationService().saveGlobalProperties(globalPropList);
		message = messageSourceService.getMessage("owa.saved");
		session.setAttribute(WebConstants.OPENMRS_MSG_ATTR, message);
		return "redirect:settings.form";
	}
	
	private List<GlobalProperty> extracted(Object obj) {
		return (Context.getAdministrationService().getGlobalPropertiesByPrefix("owa"));
	}
	
	protected Object formBackingObject(HttpServletRequest request) throws ServletException {
		if (Context.isAuthenticated()) {
			return Context.getAdministrationService().getGlobalPropertiesByPrefix("owa");
		} else {
			return new ArrayList<GlobalProperty>();
		}
	}
}

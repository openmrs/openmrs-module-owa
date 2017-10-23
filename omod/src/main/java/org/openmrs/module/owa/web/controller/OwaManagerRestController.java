package org.openmrs.module.owa.web.controller;

import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Once OwaRestController is deleted, this class should be renamed to OwaRestController
 */
@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + OwaManagerRestController.OWA_NAMESPACE)
public class OwaManagerRestController extends MainResourceController {
	
	public static final String OWA_NAMESPACE = "/owa";
	
	@Override
	public String getNamespace() {
		return RestConstants.VERSION_1 + OWA_NAMESPACE;
	}
	
}

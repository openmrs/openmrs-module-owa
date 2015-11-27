/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0 + Health disclaimer. If a copy of the MPL was not 
 * distributed with this file, You can obtain one at http://license.openmrs.org
 */
package org.openmrs.module.owa.extension.html;

import java.util.LinkedHashMap;
import java.util.Map;

import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.AdministrationSectionExt;

/**
 * This class defines the links that will appear on the administration page
 */
public class AdminList extends AdministrationSectionExt {
	
	/**
	 * @return @see AdministrationSectionExt#getMediaType()
	 */
	@Override
	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}
	
	/**
	 * @return @see AdministrationSectionExt#getTitle()
	 */
	@Override
	public String getTitle() {
		return "owa.title";
	}
	
	/**
	 * @return @see AdministrationSectionExt#getLinks()
	 */
	@Override
	public Map<String, String> getLinks() {
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		map.put("/module/owa/manager.form", "owa.manage");
		map.put("/module/owa/settings.form", "owa.settings");
		return map;
	}
}

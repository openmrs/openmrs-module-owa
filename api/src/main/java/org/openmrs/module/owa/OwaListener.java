/*
 * TODO copyright
 */
package org.openmrs.module.owa;

import java.util.List;

public interface OwaListener {
	
	/**
	 * Called when a single app is added
	 */
	void installedApp(App app);
	
	/**
	 * Called when a single app is removed
	 */
	void deletedApp(App app);
	
	/**
	 * Called when the list of apps is reloaded (including at OpenMRS startup, and OpenMRS context
	 * refresh)
	 */
	void appsReloaded(List<App> allApps);
	
}

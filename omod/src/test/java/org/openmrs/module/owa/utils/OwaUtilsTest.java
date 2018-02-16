package org.openmrs.module.owa.utils;

import org.junit.Assert;
import org.junit.Test;

public class OwaUtilsTest {

	private static String DEFAULT_APP_BASE_URL = "http://localhost:80/openmrs/owa";
	
	private static String ADDONMANGER_URL = "/addonmanager/index.html";
	
	private static String SOME_PATH_IN_APP = "/anything/index.html";
	
	/**
	 * Test that checkIfAddOnManager() returns true if the request URL contains "addonmanager"
	 */
	@Test
	public void testCheckIfAddOnManagerReturnsTrueIfAddonmanagerExists() throws Exception {
		Assert.assertEquals(new Boolean(true), OwaUtils.checkIfAddonManager(DEFAULT_APP_BASE_URL + ADDONMANGER_URL));
	}
	
	/**
	 * Test that checkIfAddOnManager() returns false if the request URL does not contain "addonmanager"
	 */
	@Test
	public void testCheckIfAddOnManagerReturnsFalseIfAddonmanagerDoesNotExist() throws Exception {
		Assert.assertEquals(new Boolean(false), OwaUtils.checkIfAddonManager(DEFAULT_APP_BASE_URL + SOME_PATH_IN_APP));
	}
	
	@Test
	public void testgetFileNameWithNoQueryString() {
		String testString = "https://dl.bintray.com/openmrs/owa/cohortbuilder-1.0.0-beta.zip";
		String fileName = OwaUtils.getFileName(testString);
		Assert.assertEquals(fileName, "cohortbuilder.zip");
	}
	
	@Test
	public void testgetFileNameWithQueryString() {
		String testString = "https://bintray.com/openmrs/owa/download_file?file_path=cohortbuilder-1.0.0-beta.zip";
		String fileName = OwaUtils.getFileName(testString);
		Assert.assertEquals(fileName, "cohortbuilder.zip");
	}
	
	@Test
	public void testRemoveVersionNumber() {
		String testString = "cohortbuilder-1.0.0-beta.zip";
		String fileName = OwaUtils.removeVersionNumber(testString);
		Assert.assertEquals(fileName, "cohortbuilder.zip");
	}

}

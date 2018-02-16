package org.openmrs.module.owa.utils;

import org.junit.Assert;
import org.junit.Test;

public class OwaUtilsTest {
	
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

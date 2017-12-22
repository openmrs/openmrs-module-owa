package org.openmrs.module.owa.web.controller;

public class InstallAppRequestObject {
	
	private String urlValue;

	private String fileName;

	public InstallAppRequestObject() {
	}
	
	public InstallAppRequestObject(String urlValue) {
		this.urlValue = urlValue;
	}
	
	public InstallAppRequestObject(String urlValue, String fileName) {
		this.urlValue = urlValue;
		this.fileName = fileName;
	}

	public String getUrlValue() {
		return urlValue;
	}
	
	public String getFileName() {
		return fileName;
	}
}

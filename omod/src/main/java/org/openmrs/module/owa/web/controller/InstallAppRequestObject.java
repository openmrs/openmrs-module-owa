package org.openmrs.module.owa.web.controller;

public class InstallAppRequestObject {
	
	private String urlValue;
	
	public InstallAppRequestObject() {
	}
	
	public InstallAppRequestObject(String urlValue) {
		this.urlValue = urlValue;
	}
	
	public String getUrlValue() {
		return this.urlValue;
	}
}

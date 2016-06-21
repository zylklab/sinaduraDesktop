package net.esle.sinadura.cloud.model;

import java.util.Map;

public class ConfigVO {

	private Map<String, String> properties;

	public ConfigVO() {
	}
	
	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

}

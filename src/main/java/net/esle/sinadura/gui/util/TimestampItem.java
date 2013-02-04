package net.esle.sinadura.gui.util;

public class TimestampItem {

	private String key;
	private String name;
	private String url;
	private String ocspUrl;
	
	public TimestampItem(String key, String name, String url, String ocspUrl) {
		super();
		this.key = key;
		this.name = name;
		this.setUrl(url);
		this.setOcspUrl(ocspUrl);
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setOcspUrl(String ocspUrl) {
		this.ocspUrl = ocspUrl;
	}

	public String getOcspUrl() {
		return ocspUrl;
	}

}
package net.esle.sinadura.cloud.utils;

public class DesktopUtils {

	public static final String PathHttp = "h";
	public static final String PathHttps = "s";
	
	// ===============================================
	//  Protocol URL Argument processing
	// ===============================================
	// sinadura://localhost:8080/sinaduraCloud/rest/v1/h/d7c0c9f9-1733-4ad6-a431-1ad54d5d9245
	public static String getServiceURL(String protURL) {
		
		String s = protURL.substring(0, protURL.lastIndexOf("/"));
		String url = s.substring(0, s.lastIndexOf("/"));
		
		String[] array = protURL.split("/");
		String isHttps = array[array.length - 2];
		
		if (isHttps.equals(PathHttps)) {
			url = url.replaceFirst("sinadura", "https");
		} else {
			url = url.replaceFirst("sinadura", "http");
		}
		
		return url;
	}
	
	public static String getToken(String protURL) {

		String[] array = protURL.split("/");
		return array[array.length - 1];
	}
	
}


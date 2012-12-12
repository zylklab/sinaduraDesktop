package net.esle.sinadura.gui.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PropertiesServerUtil {

	private static Log log = LogFactory.getLog(PropertiesServerUtil.class);

	public static final String APPLICATION_SERVER_VERSION = "application.server.version";
	public static final String STATISTICS_SERVER_URL = "statistics.server.url";
	
	private static final String PATH_CONFIGURATION = "configuration.properties";

	private static Properties configuration = null;

	public static Properties getConfiguration() {

		if (configuration == null) {
			configuration = new Properties();
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(PATH_CONFIGURATION);
			try {
				configuration.load(is);
			} catch (IOException e) {
				log.error("", e);
			}
		}
		return configuration;
	}
}
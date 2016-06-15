/*
 * # Copyright 2008 zylk.net # # This file is part of Sinadura. # # Sinadura is free software: you can redistribute it
 * and/or modify # it under the terms of the GNU General Public License as published by # the Free Software Foundation,
 * either version 2 of the License, or # (at your option) any later version. # # Sinadura is distributed in the hope
 * that it will be useful, # but WITHOUT ANY WARRANTY; without even the implied warranty of # MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the # GNU General Public License for more details. # # You should have received a copy
 * of the GNU General Public License # along with Sinadura. If not, see <http://www.gnu.org/licenses/>. [^] # # See
 * COPYRIGHT.txt for copyright notices and details. #
 */
package net.esle.sinadura.gui.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;

import net.esle.sinadura.gui.Sinadura;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author zylk.net
 */
public class PropertiesUtil {
	
	private static Log log = LogFactory.getLog(PropertiesUtil.class);
	

	private static String APP_BASE_ABSOLUTE_PATH = null; // No utilizar! (solo cuando no se pueda cargar los recursos por classpath)
	private static String RESOURCES_BASE_ABSOLUTE_PATH = null; // No utilizar! (solo cuando no se pueda cargar los recursos por classpath)
	
	
	// CONSTANTES de aplicacion TODO estaria bien encapsular el acceso a las constantes y a las keys -> getProperty(key/constante) 
	public static final String APPLICATION_NAME = "Sinadura";

	public static final String USER_BASE_PATH = Sinadura.PRIVATE_USER_BASE_PATH;	
	public static final String LOG_FOLDER_PATH = Sinadura.PRIVATE_LOG_FOLDER_PATH;
	public static final String STATISTICS_FOLDER_PATH = Sinadura.PRIVATE_LOG_FOLDER_PATH;
	public static final String TMP_FOLDER_PATH = System.getProperty("java.io.tmpdir");

	public static final String LICENSE_PATH = "LICENSE.txt";
	public static final String CREDITS_PATH = "credits.txt";
	public static final String INTROKEY = "RETURN";
	
	
	private static final String PATH_CONFIGURATION = "config/configuration.properties";
	
	
	// KEYS que se fijan de forma programatica
	public static final String SINADURA_CLOUD_MODE = "sinadura.cloud.mode";
	
	// KEYS del configuration.properties
	public static final String NEWS = "news.sinadura";
	public static final String SERVER_VERSION_URL = "server.versions.url";
	public static final String APPLICATION_VERSION_STRING = "application.local.version.string";
	public static final String APPLICATION_VERSION_NUMBER = "application.local.version";

	public static final String SINADURA_MAIN_URL = "sinadura.main.url";
	public static final String SINADURA_DOCUMENTATION = "documentation.uri";
	
	public static final String ENABLE_SEND_BUTTON = "enable.send.button";
	
	public static final String SPONSOR_LABEL_ENABLE = "sponsor.label.enable";
	public static final String SPONSOR_IMAGE_PATH = "sponsor.image.path";
	
	// zain
	public static final String ZAIN_P12_PASSWORD = "zain.p12.password";
	public static final String ZAIN_TRUSTED_PASSWORD = "zain.trusted.password";
	public static final String ZAIN_ENDPOINT = "zain.endpoint";
	public static final String ZAIN_LOG_ACTIVE = "zain.log.active";
	public static final String ZAIN_P12_PATH_ABSOLUTE = "zain.p12.path.absolute"; // son paths de file system
	public static final String ZAIN_TRUSTED_PATH_ABSOLUTE = "zain.trusted.path.absolute"; // son paths de file system
	public static final String ZAIN_LOG_REQUEST_FOLDER_PATH = "zain.log.request.path"; // son paths de file system
	public static final String ZAIN_LOG_RESPONSE_FOLDER_PATH = "zain.log.response.path"; // son paths de file system
	
	// Visibilidad de las preferencias
	public static final String PREFERENCES_VISIBLE_ALL = "preferences.visible.all";
	public static final String PREFERENCES_VISIBLE_GENERAL_OUTPUT_AUTO_ENABLE = "preferences.visible.general.output.auto.enable";
	public static final String PREFERENCES_VISIBLE_GENERAL_OUTPUT_DIR = "preferences.visible.general.output.dir";
	public static final String PREFERENCES_VISIBLE_GENERAL_SAVE_EXTENSION = "preferences.visible.general.save.extension";
	public static final String PREFERENCES_VISIBLE_GENERAL_AUTO_VALIDATE = "preferences.visible.general.auto.validate";
	public static final String PREFERENCES_VISIBLE_SIGN_TS_ENABLE = "preferences.visible.sign.ts.enable";
	public static final String PREFERENCES_VISIBLE_SIGN_TS_TSA = "preferences.visible.sign.ts.tsa";
	public static final String PREFERENCES_VISIBLE_SIGN_OCSP_ENABLE = "preferences.visible.sign.ocsp.enable";
	public static final String PREFERENCES_VISIBLE_PDF_SECTION = "preferences.visible.pdf.section";
	public static final String PREFERENCES_VISIBLE_XADES_SECTION = "preferences.visible.xades.section";
	public static final String PREFERENCES_VISIBLE_XADES_XL_OCSP_ADD_ALL = "preferences.visible.xades.xl.ocsp.add_all";
	public static final String PREFERENCES_VISIBLE_VALIDATION_SECTION = "preferences.visible.validation.section";
	// Visibilidad de las preferencias - VALUES
	public static final String VISIBLE_TYPE_VISIBLE = "0";
	public static final String VISIBLE_TYPE_HIDDEN_DEPENDANT = "1";
	public static final String VISIBLE_TYPE_HIDDEN_ALWAYS = "2";
	
	public static final String VERSION_CHECK_UPDATE_ENABLED = "version.check.update.enabled";
	public static final String PROXY_ENABLED = "proxy.enabled"; // se habilita el soporte para proxy (solo EE)
	
	private static Properties configuration = null;
	

	private static void init() {

		configuration = new Properties();
		
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(PATH_CONFIGURATION);
		try {
			configuration.load(is);
		} catch (IOException e) {
			log.error("", e);
		}
		
		try {
			String libClassesPath = Sinadura.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
			
			APP_BASE_ABSOLUTE_PATH = new File(libClassesPath).getParentFile().getParentFile().getAbsolutePath();
			RESOURCES_BASE_ABSOLUTE_PATH = APP_BASE_ABSOLUTE_PATH + File.separatorChar + "resources";
			// solucionar esto? (es para que funcione en eclipse)
			if (new File(libClassesPath).getParentFile().getName().equals("target")) {
				APP_BASE_ABSOLUTE_PATH = new File(libClassesPath).getParentFile().getAbsolutePath();
				RESOURCES_BASE_ABSOLUTE_PATH = new File(libClassesPath).getAbsolutePath();
			}
			log.info("APP_BASE_PATH: " + APP_BASE_ABSOLUTE_PATH);
			log.info("RESOURCES_BASE_PATH: " + RESOURCES_BASE_ABSOLUTE_PATH);
			
		} catch (URISyntaxException e) {
			log.error(e);
		}
		
		// ZAIN
		// log-zain
		String zainLogMainPath = LOG_FOLDER_PATH + File.separatorChar + "zain";
		File f = new File(zainLogMainPath);
		if (!f.exists()) {
			f.mkdir();
		}
		// log-zain-request
		String zainLogRequestPath = zainLogMainPath + File.separatorChar + "request";
		f = new File(zainLogRequestPath);
		if (!f.exists()) {
			f.mkdir();
		}		
		// log-zain-response
		String zainLogResponsePath = zainLogMainPath + File.separatorChar + "response";
		f = new File(zainLogResponsePath);
		if (!f.exists()) {
			f.mkdir();
		}
		
		configuration.setProperty(ZAIN_LOG_REQUEST_FOLDER_PATH, zainLogRequestPath);
		configuration.setProperty(ZAIN_LOG_RESPONSE_FOLDER_PATH, zainLogResponsePath);
		configuration.setProperty(ZAIN_P12_PATH_ABSOLUTE, RESOURCES_BASE_ABSOLUTE_PATH + File.separatorChar + "zain" + File.separatorChar + "zain.p12");
		configuration.setProperty(ZAIN_TRUSTED_PATH_ABSOLUTE, RESOURCES_BASE_ABSOLUTE_PATH + File.separatorChar + "zain" + File.separatorChar + "zain.jks");	
	}
	
	
	// este tendria que estar a private, y solo usar los metodos de get(). Así que hay que ir migrando las llamadas.
	public static Properties getConfiguration() {

		if (configuration == null) {
			init();
		}
		
		return configuration;
	}
	
	
	public static String get(String key) {
		
		return getConfiguration().getProperty(key);
	}
	
	public static boolean getBoolean(String key) {
		
		return Boolean.parseBoolean(getConfiguration().getProperty(key));
	}
	
	
	/**
	 * Solo para sobreescribir propiedades en memoria (no se persisten)
	 * 
	 */
	public static void set(String key, String value) {
		
		getConfiguration().setProperty(key, value);
	}
}
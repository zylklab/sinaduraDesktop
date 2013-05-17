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

	public static String APP_BASE_PATH = null;
	
	
	// CONSTANTES de aplicacion TODO estaria bien encapsular el acceso a las constantes y a las keys -> getProperty(key/constante) 
	public static final String APPLICATION_NAME = "Sinadura";
	
	// este valor tambien esta hardcode en el fichero del logger
	public static final String USER_BASE_PATH = System.getProperty("user.home") + File.separatorChar + ".sinadura";
	
	public static final String LOG_FOLDER_PATH = USER_BASE_PATH + File.separatorChar + "log";
	
	
	
	public static final String STATISTICS_FOLDER_PATH = USER_BASE_PATH + File.separatorChar + "statistics";
	public static final String TMP_FOLDER_PATH = System.getProperty("java.io.tmpdir");
	


	public static final String LICENSE_PATH = "LICENSE.txt";
	public static final String CREDITS_PATH = "credits.txt";
	
	public static final String INTROKEY = "RETURN";
	
	
	private static final String PATH_CONFIGURATION = "configuration.properties";
	
	
	// KEYS del configuration.properties
	public static final String NEWS = "news.sinadura";
	public static final String SERVER_VERSION_URL = "server.versions.url";
	public static final String APPLICATION_VERSION_STRING  = "application.local.version.string";
	public static final String APPLICATION_VERSION_NUMBER  = "application.local.version";

	public static final String SINADURA_MAIN_URL = "sinadura.main.url";
	public static final String SINADURA_DOCUMENTATION = "documentation.uri";
	
	public static final String ENABLE_SEND_BUTTON = "enable.send.button";
	
	// zain
	public static final String ZAIN_P12_PASSWORD = "zain.p12.password";
	public static final String ZAIN_TRUSTED_PASSWORD = "zain.trusted.password";
	public static final String ZAIN_ENDPOINT = "zain.endpoint";
	public static final String ZAIN_LOG_ACTIVE = "zain.log.active";
	public static final String ZAIN_P12_PATH_ABSOLUTE = "zain.p12.path.absolute"; // son paths de file system
	public static final String ZAIN_TRUSTED_PATH_ABSOLUTE = "zain.trusted.path.absolute"; // son paths de file system
	public static final String ZAIN_LOG_REQUEST_FOLDER_PATH = "zain.log.request.path"; // son paths de file system
	public static final String ZAIN_LOG_RESPONSE_FOLDER_PATH = "zain.log.response.path"; // son paths de file system
	
	
	// visibilidad de opciones de las preferencias
	public static final String PREFERENCES_SUFFIX_ENABLED 	= "preferences.suffix.enabled";
	public static final String PREFERENCES_PDF_ENABLED 		= "preferences.pdf.enabled";
	public static final String PREFERENCES_XADES_XL_OCSP_ADD_ALL_VISIBLE = "preferences.xades.xl.ocsp.add_all.visible";
	
	// valor por defecto de las preferencias.
	public static final String DEFAULT_IMAGE_FILE_PATH = USER_BASE_PATH + File.separatorChar + "sinadura150.png"; 	// imagen por defecto para el sello
	public static final String PREFERENCES_PDF_TIPO_DEFAULT = "preferences.pdf.tipo.default";
	public static final String PREFERENCES_SAVE_EXTENSION_DEFAULT = "preferences.save.extension.default";
	public static final String PREFERENCES_XADES_VALIDATOR_IMPL_DEFAULT = "preferences.xades.validator.impl.default";
	
	public static final String VERSION_CHECK_UPDATE_ENABLED = "version.check.update.enabled";

	public static final String PROXY_ENABLED 				= "proxy.enabled"; // se habilita el soporte para proxy (solo EE)
	
	private static Properties configuration = null;
	
	private static Log log = LogFactory.getLog(PropertiesUtil.class);
	
	
	static {
		
		configuration = new Properties();
		
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(PATH_CONFIGURATION);
		try {
			configuration.load(is);
		} catch (IOException e) {
			log.error("", e);
		}
		
		try {
			String classesPath = Sinadura.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
			APP_BASE_PATH = new File(classesPath).getParentFile().getAbsolutePath() + File.separatorChar + "config" + File.separatorChar;
			
			// TODO arreglar esto (es para que funcione en eclipse)
			if (new File(classesPath).getParentFile().getName().equals("target")) {
				APP_BASE_PATH = new File(classesPath).getAbsolutePath();
			}
			
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
		configuration.setProperty(ZAIN_P12_PATH_ABSOLUTE, APP_BASE_PATH + File.separatorChar + "zain" + File.separatorChar + "EntidadZylkdesarrollo.p12");
		configuration.setProperty(ZAIN_TRUSTED_PATH_ABSOLUTE, APP_BASE_PATH + File.separatorChar + "zain" + File.separatorChar + "zain-truststore-des-2ik_4k.jks");	
	}
	
	
	// este tendria que estar a private, y solo usar los metodos de get(). Así que hay que ir migrando las llamadas.
	public static Properties getConfiguration() {

		return configuration;
	}
	
	
	public static String get(String key) {
		
		return configuration.getProperty(key);
	}
	
	public static boolean getBoolean(String key) {
		
		return Boolean.parseBoolean(configuration.getProperty(key));
	}
	
}
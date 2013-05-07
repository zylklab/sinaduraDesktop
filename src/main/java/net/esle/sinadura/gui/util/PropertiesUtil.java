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
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author zylk.net
 */
public class PropertiesUtil {

	// CONSTANTES de aplicacion TODO estaria bien encapsular el acceso a las constantes y a las keys -> getProperty(key/constante) 
	public static final String APPLICATION_NAME = "Sinadura";
	
	// este valor tambien esta hardcode en el fichero del logger
	public static final String USER_BASE_PATH = System.getProperty("user.home") + File.separatorChar + ".sinadura";
	
	public static final String LOG_FOLDER_PATH = USER_BASE_PATH + File.separatorChar + "log";
	public static final String LOG_ZAIN_FOLDER_PATH = LOG_FOLDER_PATH + File.separatorChar + "zain";
	public static final String LOG_ZAIN_REQUEST_FOLDER_PATH = LOG_ZAIN_FOLDER_PATH + File.separatorChar + "request";
	public static final String LOG_ZAIN_RESPONSE_FOLDER_PATH = LOG_ZAIN_FOLDER_PATH + File.separatorChar + "response";
	
	
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
	
	public static final String PROXY_ENABLED 				= "proxy.enabled";
	
	private static Properties configuration = null;

	private static Log log;
	
	public static Properties getConfiguration() {

		 log = LogFactory.getLog(PropertiesUtil.class);
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
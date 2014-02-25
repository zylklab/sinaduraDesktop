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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author zylk.net
 */
public class PreferencesDefaultUtil {
	
	private static Log log = LogFactory.getLog(PreferencesDefaultUtil.class);

	
	private static final String PATH_CONFIGURATION = "preferences/preferences-default.properties";

	
	private static Properties configuration = null;

	
	private static Properties getConfiguration() {

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
	
	
	public static String get(String key) {
		
		return getConfiguration().getProperty(key);
	}
	
	public static boolean getBoolean(String key) {
		
		return Boolean.parseBoolean(getConfiguration().getProperty(key));
	}
	
}
/*
 * # Copyright 2008 zylk.net 
 * # 
 * # This file is part of Sinadura. 
 * # 
 * # Sinadura is free software: you can redistribute it and/or modify 
 * # it under the terms of the GNU General Public License as published by 
 * # the Free Software Foundation, either version 2 of the License, or 
 * # (at your option) any later version. 
 * # 
 * # Sinadura is distributed in the hope that it will be useful, 
 * # but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
 * # GNU General Public License for more details. 
 * # 
 * # You should have received a copy of the GNU General Public License 
 * # along with Sinadura. If not, see <http://www.gnu.org/licenses/>. [^] 
 * # 
 * # See COPYRIGHT.txt for copyright notices and details. 
 * #
 */
package net.esle.sinadura.gui;

import java.io.File;
import java.util.Locale;

import javax.swing.JOptionPane;

import net.esle.sinadura.gui.exceptions.FileNotValidException;
import net.esle.sinadura.gui.util.PropertiesUtil;
import net.esle.sinadura.gui.util.StatisticsUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.xml.utils.URI.MalformedURIException;

/**
 * @author zylk.net
 */
public class Sinadura {

	private static Log log;
	
	/*************************************
	 * init, creación de carpetas base
	 *************************************/
	static{
		
		// base
		File f = new File(PropertiesUtil.USER_BASE_PATH);
		if (!f.exists()) {
			f.mkdir();
		}

		// log
		f = new File(PropertiesUtil.LOG_FOLDER_PATH);
		if (!f.exists()) {
			f.mkdir();
		}

		// statistics
		f = new File(PropertiesUtil.STATISTICS_FOLDER_PATH);
		if (!f.exists()) {
			f.mkdir();
		}
		
		 log = LogFactory.getLog(Sinadura.class);
	}
	
	
	public static void main(String[] args) throws FileNotValidException, FileSystemException, MalformedURIException {
		
		try {
			
			//===========================
			// env
			//===========================
			log.info("Iniciando Sinadura");
			
			log.info("Sinadura version: " + PropertiesUtil.getConfiguration().getProperty(PropertiesUtil.APPLICATION_VERSION_STRING));
			
			log.info("Java vendor: " + System.getProperty("java.vendor"));
			log.info("Java version: " + System.getProperty("java.version"));
			
			log.info("Class path: " + System.getProperty("java.class.path"));
			log.info("User home: " + System.getProperty("user.home"));
			
			log.info("Os name: " + System.getProperty("os.name"));
			log.info("Os version: " + System.getProperty("os.version"));
			log.info("Os arch: " + System.getProperty("os.arch"));
			
			log.info("Locale country: " + Locale.getDefault().getCountry());
			log.info("Locale language: " + Locale.getDefault().getLanguage());
			
			
			new LoadingWindow(args);

			StatisticsUtil.log(StatisticsUtil.KEY_CLOSING_SINADURA);
			log.info("Finalizando Sinadura");
			
		} catch (java.lang.UnsatisfiedLinkError e) {
			
			e.printStackTrace();
			String message = e.getMessage();
			if (message.equals("Cannot load 64-bit SWT libraries on 32-bit JVM")) {
				message = "You have the 32 bits version of Java installed, and this is the 64 bits version of Sinadura.\nTry installing the 32 bits version.";
			}
			JOptionPane.showMessageDialog(null, message, "Sinadura", JOptionPane.ERROR_MESSAGE);
			
		} catch (RuntimeException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.toString(), "Sinadura", JOptionPane.ERROR_MESSAGE);
		}
	}
}

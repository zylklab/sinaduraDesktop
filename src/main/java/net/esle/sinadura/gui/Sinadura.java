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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.xml.utils.URI.MalformedURIException;

import net.esle.sinadura.cloud.CloudLoadingWindow;
import net.esle.sinadura.core.util.PropertiesCoreUtil;
import net.esle.sinadura.gui.exceptions.FileNotValidException;
import net.esle.sinadura.gui.util.PropertiesUtil;
import net.esle.sinadura.gui.util.StatisticsUtil;

/**
 * @author zylk.net
 */
public class Sinadura {

	private static Log log;

	// Como es necesario crear los directorios de log antes de obtener un Log (LogFactory.getLog), se inicializan y crean aqui las variables.
	// No usar estas variables!, utilizar las correspondientes de PropertiesUtil (para que este el acceso centralizado).
	// Este valor tambien esta hardcode en el fichero del logger
	public static final String PRIVATE_USER_BASE_PATH = System.getProperty("user.home") + File.separatorChar + ".sinadura";
	public static final String PRIVATE_LOG_FOLDER_PATH = PRIVATE_USER_BASE_PATH + File.separatorChar + "log";
	public static final String PRIVATE_STATISTICS_FOLDER_PATH = PRIVATE_USER_BASE_PATH + File.separatorChar + "statistics";
	
	
	/*************************************
	 * init, creación de carpetas base
	 *************************************/
	static {
		
		// base
		File f = new File(PRIVATE_USER_BASE_PATH);
		if (!f.exists()) {
			f.mkdir();
		}

		// log
		f = new File(PRIVATE_LOG_FOLDER_PATH);
		if (!f.exists()) {
			f.mkdir();
		}

		// statistics
		f = new File(PRIVATE_STATISTICS_FOLDER_PATH);
		if (!f.exists()) {
			f.mkdir();
		}
		
		log = LogFactory.getLog(Sinadura.class);
	}
	
	
	public static void main(String[] args) throws FileNotValidException, FileSystemException, MalformedURIException {
		
		// TODO BORRAR ESTO
//		String token = "0eed04e5-a68a-4ba8-9558-2fd75ef5aa23";
////		args = new String[] {"sinadura://localhost:8080/sinaduraCloud/rest/v1/h/" + token};
//		args = new String[] {"sinadura://faut.zylk.net:8080/sinaduraCloud/rest/v1/h/" + token};
		
		try {
			
			log.info("Iniciando Sinadura");
			log.info("Sinadura version: " + PropertiesUtil.getConfiguration().getProperty(PropertiesUtil.APPLICATION_VERSION_STRING));
			log.info("Sinadura core version: " + PropertiesCoreUtil.getProperty(PropertiesCoreUtil.KEY_CORE_VERSION));
			log.info("Java vendor: " + System.getProperty("java.vendor"));
			log.info("Java version: " + System.getProperty("java.version"));
			log.info("Class path: " + System.getProperty("java.class.path"));
			log.info("User home: " + System.getProperty("user.home"));
			log.info("Os name: " + System.getProperty("os.name"));
			log.info("Os version: " + System.getProperty("os.version"));
			log.info("Os arch: " + System.getProperty("os.arch"));
			log.info("Locale country: " + Locale.getDefault().getCountry());
			log.info("Locale language: " + Locale.getDefault().getLanguage());
			
			int mb = 1024*1024; // solo un 1024 para medirlo en KB 
	        Runtime runtime = Runtime.getRuntime();
	        log.info("Init - Used memory:" + (runtime.totalMemory() - runtime.freeMemory()) / mb + " MB");
	        log.info("Init - Free memory:" + runtime.freeMemory() / mb + " MB");
	        log.info("Init - Total memory:" + runtime.totalMemory() / mb + " MB");
	        log.info("Init - Max memory:" + runtime.maxMemory() / mb + " MB");
			
	        
	        // CLOUD OR DESKTOP
	        PropertiesUtil.set(PropertiesUtil.SINADURA_CLOUD_MODE, "false");	        
			if (args != null && args.length > 0) {
				String arg = args[0];
				if (arg.startsWith("sinadura://")) {
					PropertiesUtil.set(PropertiesUtil.SINADURA_CLOUD_MODE, "true");
				}
			}
			boolean cloudMode = PropertiesUtil.getBoolean(PropertiesUtil.SINADURA_CLOUD_MODE);
			if (!cloudMode) {
				new LoadingWindow(args);
			} else {
				new CloudLoadingWindow(args);
			}
			

			StatisticsUtil.log(StatisticsUtil.KEY_CLOSING_SINADURA);
			log.info("Close - Used memory:" + (runtime.totalMemory() - runtime.freeMemory()) / mb + " MB");
	        log.info("Close - Free memory:" + runtime.freeMemory() / mb + " MB");
	        log.info("Close - Total memory:" + runtime.totalMemory() / mb + " MB");
	        log.info("Close - Max memory:" + runtime.maxMemory() / mb + " MB");
			log.info("Finalizando Sinadura");
			
		} catch (java.lang.UnsatisfiedLinkError e) {
			
			e.printStackTrace();
			String message = e.getMessage();
			if (message.equals("Cannot load 64-bit SWT libraries on 32-bit JVM")) {
				message = "You have the 32 bits version of Java installed, and this is the 64 bits version of Sinadura.\nTry installing the 32 bits version of Sinadura.";
			}
			JOptionPane.showMessageDialog(null, message, "Sinadura", JOptionPane.ERROR_MESSAGE);
			
		} catch (RuntimeException e) {
			e.printStackTrace();
//			String message = e.toString(); // solo el message
			String message = getTextStackTrace(e); // toda la pila
			JOptionPane.showMessageDialog(null, message, "Sinadura", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	// TODO este metodo deberia estar en alguna clase Util. ExceptionUtil del core por ejemplo.
	private static String getTextStackTrace(Throwable t) {
        
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        String stackTrace = sw.toString();
       
        return stackTrace;
	}
	
}

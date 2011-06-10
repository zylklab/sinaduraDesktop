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

package net.esle.sinadura.gui.util;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWTException;

/**
 * @author zylk.net
 */
public class DesktopUtil {
	
	private static Log log = LogFactory.getLog(DesktopUtil.class);

	
	public static void openDefaultBrowser(String url) {
		
		Desktop desktop = null;
		// Before more Desktop API is used, first check
		// whether the API is supported by this particular
		// virtual machine (VM) on this particular host.
		if (Desktop.isDesktopSupported()) {

			desktop = Desktop.getDesktop();
			if (desktop.isSupported(Desktop.Action.BROWSE)) {

				URI uri = null;
				try {
					uri = new URI(url);
					desktop.browse(uri);
					
				} catch (IOException e) {
					log.error("", e);
				} catch (URISyntaxException e) {
					log.error("", e);
				}
				log.info(LanguageUtil.getLanguage().getString("info.browser.open"));
				LoggingDesktopController.printInfo(MessageFormat.format(LanguageUtil.getLanguage().getString(
						"info.browser.open"), url));
			} else {
				log.error(LanguageUtil.getLanguage().getString("error.no_desktop_support"));
				LoggingDesktopController.printError(LanguageUtil.getLanguage().getString("error.no_desktop_support"));
			}
		} else {
			log.error(LanguageUtil.getLanguage().getString("error.no_desktop_support"));
			LoggingDesktopController.printError(LanguageUtil.getLanguage().getString("error.no_desktop_support"));
		}
	}
	
	
	public static void openSystemFile(String path) {
		
		// Before more Desktop API is used, first check
		// whether the API is supported by this particular
		// virtual machine (VM) on this particular host.
		if (Desktop.isDesktopSupported()) {
			
			// TODO input a output
			File file = new File(path);
			if (file.exists()) {
				try {
					Desktop.getDesktop().open(file);
				} catch (IOException e) {
					log.error(LanguageUtil.getLanguage().getString("error.unknown_system_application"), e);
					LoggingDesktopController
							.printError(LanguageUtil.getLanguage().getString("error.unknown_system_application"));
				}
			} else {
				log.error(LanguageUtil.getLanguage().getString("error.no_file.exists"));
				LoggingDesktopController.printError(LanguageUtil.getLanguage().getString("error.no_file.exists"));
			}
			
		} else {
			log.error(LanguageUtil.getLanguage().getString("error.no_desktop_support"));
			LoggingDesktopController.printError(LanguageUtil.getLanguage().getString("error.no_desktop_support"));
		}
	}
	
	
	public static void openDefaultMailClient(List<String> attachmentList) {
		
		Desktop desktop = null;
		// Before more Desktop API is used, first check
		// whether the API is supported by this particular
		// virtual machine (VM) on this particular host.
		boolean windows = System.getProperty("os.name").trim().toLowerCase().contains("windows");
		if (windows) {		

			try {
				OutlookUtil.openOutlook(attachmentList);
			} catch (SWTException e) {

				log.error(LanguageUtil.getLanguage().getString("error.outlook.not.found"));
				LoggingDesktopController.printError(LanguageUtil.getLanguage().getString("error.outlook.not.found"));
				return;
			}
			log.info(LanguageUtil.getLanguage().getString("info.email_client.open"));
			LoggingDesktopController.printInfo(LanguageUtil.getLanguage().getString("info.email_client.open"));
		} else if (Desktop.isDesktopSupported()) {

			desktop = Desktop.getDesktop();
			
			if (desktop.isSupported(Desktop.Action.MAIL)) {

					URI uri = null;
					
					String attachmentString = "";
					for (String attachment : attachmentList) {
						attachmentString +=  "&attachment=" + attachment;
					}
					
					try {
						uri = new URI("mailto", "?SUBJECT=" + attachmentString, null);
						desktop.mail(uri);
						
					} catch (IOException e) {
						log.error("", e);
						log.error(LanguageUtil.getLanguage().getString("error.no_desktop_support"));
						LoggingDesktopController.printError(LanguageUtil.getLanguage().getString("error.no_desktop_support"));
						return;
					} catch (URISyntaxException e) {
						log.error("", e);
					}
					
					log.info(LanguageUtil.getLanguage().getString("info.email_client.open"));
					LoggingDesktopController.printInfo(LanguageUtil.getLanguage().getString("info.email_client.open"));
			} else {
				log.error(LanguageUtil.getLanguage().getString("error.no_desktop_support"));
				LoggingDesktopController.printError(LanguageUtil.getLanguage().getString("error.no_desktop_support"));
			}
		} else {
			log.error(LanguageUtil.getLanguage().getString("error.no_desktop_support"));
			LoggingDesktopController.printError(LanguageUtil.getLanguage().getString("error.no_desktop_support"));
		}
			
	}
}
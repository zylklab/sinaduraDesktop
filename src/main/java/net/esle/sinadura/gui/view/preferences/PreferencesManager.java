/*
# Copyright 2008 zylk.net
#
# This file is part of Sinadura.
#
# Sinadura is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 2 of the License, or
# (at your option) any later version.
#
# Sinadura is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with Sinadura.  If not, see <http://www.gnu.org/licenses/>. [^]
#
# See COPYRIGHT.txt for copyright notices and details.
#
*/
package net.esle.sinadura.gui.view.preferences;

import java.io.IOException;

import net.esle.sinadura.gui.util.LanguageUtil;
import net.esle.sinadura.gui.util.PreferencesUtil;
import net.esle.sinadura.gui.util.PropertiesUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.widgets.Shell;


public class PreferencesManager {
	
	private static Log log = LogFactory.getLog(PreferencesManager.class);

	/**
	 * @param mainShell
	 */
	public void run(Shell mainShell) {

		// Create the preference manager
		PreferenceManager mgr = new PreferenceManager();

		// Create the nodes
		GeneralPreferences generalPreferences = new GeneralPreferences();
		generalPreferences.setTitle(LanguageUtil.getLanguage().getString("preferences.main.title"));
		generalPreferences.setDescription(LanguageUtil.getLanguage().getString("preferences.main.description") + "\n");

		ProxyPreferences proxyPreferences = new ProxyPreferences();
		proxyPreferences.setTitle(LanguageUtil.getLanguage().getString("preferences.proxy.title"));
		proxyPreferences.setDescription(LanguageUtil.getLanguage().getString("preferences.proxy.description") + "\n");
		
		SignPreferences signPreferences = new SignPreferences();
		signPreferences.setTitle(LanguageUtil.getLanguage().getString("preferences.sign.title"));
		signPreferences.setDescription(LanguageUtil.getLanguage().getString("preferences.sign.description") + "\n");
		
			CertPreferences certPreferences = new CertPreferences();
			certPreferences.setTitle(LanguageUtil.getLanguage().getString("preferences.cert.title"));
			certPreferences.setDescription(LanguageUtil.getLanguage().getString("preferences.cert.description") + "\n");
			
				SoftwareCertPreferences softwareCertPreferences = new SoftwareCertPreferences();
				softwareCertPreferences.setTitle(LanguageUtil.getLanguage().getString("preferences.cert.software.title"));
				softwareCertPreferences.setDescription(LanguageUtil.getLanguage().getString("preferences.cert.software.description") + "\n");
				
				HardwareCertPreferences hardwareCertPreferences = new HardwareCertPreferences();
				hardwareCertPreferences.setTitle(LanguageUtil.getLanguage().getString("preferences.cert.hardware.title"));
				hardwareCertPreferences.setDescription(LanguageUtil.getLanguage().getString("preferences.cert.hardware.description") + "\n");
		
			PdfPreferences pdfPreferences = new PdfPreferences();
			pdfPreferences.setTitle(LanguageUtil.getLanguage().getString("preferences.pdf.title"));
			pdfPreferences.setDescription(LanguageUtil.getLanguage().getString("preferences.pdf.description") + "\n");

			XadesPreferences xadesPreferences = new XadesPreferences();
			xadesPreferences.setTitle(LanguageUtil.getLanguage().getString("preferences.xades.title"));
			xadesPreferences.setDescription(LanguageUtil.getLanguage().getString("preferences.xades.description") + "\n");

		TrustedCertsPreferences trustedPreferences = new TrustedCertsPreferences();
		trustedPreferences.setTitle(LanguageUtil.getLanguage().getString("preferences.trusted.title"));
		trustedPreferences.setDescription(LanguageUtil.getLanguage().getString("preferences.trusted.description") + "\n");
		
			CacheCertsPreferences cachePreferences = new CacheCertsPreferences();
			cachePreferences.setTitle(LanguageUtil.getLanguage().getString("preferences.cache.title"));
			cachePreferences.setDescription(LanguageUtil.getLanguage().getString("preferences.cache.description") + "\n");	


		PreferenceNode generalNode = new PreferenceNode("generalNode", generalPreferences);
		PreferenceNode proxyNode = new PreferenceNode("proxyNode", proxyPreferences);
		PreferenceNode signNode = new PreferenceNode("signNode", signPreferences);
			PreferenceNode certNode = new PreferenceNode("certNode", certPreferences);
				PreferenceNode softwareCertNode = new PreferenceNode("softwareCertNode", softwareCertPreferences);
				PreferenceNode hardwareCertNode = new PreferenceNode("hardwareCertNode", hardwareCertPreferences);
			PreferenceNode pdfNode = new PreferenceNode("pdfNode", pdfPreferences);
			PreferenceNode xadesNode = new PreferenceNode("xadesNode", xadesPreferences);
		PreferenceNode trustedNode = new PreferenceNode("trustedNode", trustedPreferences);
			PreferenceNode cacheNode = new PreferenceNode("cacheNode", cachePreferences);
		

		mgr.addToRoot(generalNode);
		// ee
		if (Boolean.valueOf(PropertiesUtil.getConfiguration().getProperty(PropertiesUtil.PROXY_ENABLED))){
			mgr.addToRoot(proxyNode);			
		}
		
		mgr.addToRoot(signNode);
			mgr.addTo(signNode.getId(), certNode);
				mgr.addTo(signNode.getId() + "." + certNode.getId(), softwareCertNode);
				mgr.addTo(signNode.getId() + "." + certNode.getId(), hardwareCertNode);
			
			if (Boolean.valueOf(PropertiesUtil.getConfiguration().getProperty(PropertiesUtil.PREFERENCES_PDF_ENABLED))){
				mgr.addTo(signNode.getId(), pdfNode);
			}
			mgr.addTo(signNode.getId(), xadesNode);
		mgr.addToRoot(trustedNode);
			mgr.addTo(trustedNode.getId(), cacheNode);
		

		// Create the preferences dialog
		PreferenceDialog dlg = new PreferenceDialog(mainShell, mgr);

		// Set the preference store
		PreferenceStore ps = PreferencesUtil.getPreferences();

		try {
			ps.load();
		} catch (IOException e) {
			log.error("", e);
		}
		dlg.setPreferenceStore(ps);

		
		// Open the dialog
		dlg.open();

		try {
			// Save the preferences
			ps.save();

		} catch (IOException e) {
			log.error("", e);
		}

	}

	/**
	 * The application entry point
	 *
	 * @param mainShell
	 */
	public void abrirVentana(Shell mainShell) {
		
		this.run(mainShell);
	}

}

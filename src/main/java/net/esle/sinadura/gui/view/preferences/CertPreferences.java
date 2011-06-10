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
package net.esle.sinadura.gui.view.preferences;


import java.util.logging.Logger;

import net.esle.sinadura.gui.Sinadura;
import net.esle.sinadura.gui.util.LanguageUtil;
import net.esle.sinadura.gui.util.PreferencesUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;

/**
* This class demonstrates field editors
*/

public class CertPreferences extends FieldEditorPreferencePage {
	
	private static Log log = LogFactory.getLog(CertPreferences.class);
	

	public CertPreferences() {
		// Use the "flat" layout
		super(FLAT);
	}

	@Override
	protected void createFieldEditors() {
		
		String[][] values = {
				{ LanguageUtil.getLanguage().getString("preferences.cert.type.software"), PreferencesUtil.CERT_TYPE_VALUE_SOFTWARE },
				{ LanguageUtil.getLanguage().getString("preferences.cert.type.hardware"), PreferencesUtil.CERT_TYPE_VALUE_HARDWARE }, 
				{ LanguageUtil.getLanguage().getString("preferences.cert.type.mscapi"), PreferencesUtil.CERT_TYPE_VALUE_MSCAPI }};
				//{ "MSCAPI", PreferencesUtil.CERT_TYPE_VALUE_MSCAPI }};
		
		
		RadioGroupFieldEditor checkBoxSoftware = new RadioGroupFieldEditor(PreferencesUtil.CERT_TYPE, 
				LanguageUtil.getLanguage().getString("preferences.cert.type"), 3, values, getFieldEditorParent());
		
		addField(checkBoxSoftware);
		
	}
}
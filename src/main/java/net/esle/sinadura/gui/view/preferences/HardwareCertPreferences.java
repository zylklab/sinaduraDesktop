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


import java.util.Map;
import java.util.logging.Logger;

import net.esle.sinadura.gui.Sinadura;
import net.esle.sinadura.gui.util.LanguageUtil;
import net.esle.sinadura.gui.util.PreferencesUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.util.PropertyChangeEvent;

/**
* This class demonstrates field editors
*/

public class HardwareCertPreferences extends FieldEditorPreferencePage {
	
	private static Log log = LogFactory.getLog(HardwareCertPreferences.class);

	private boolean reiniciar = false;
	
	/**
	 * @param messages
	 */
	public HardwareCertPreferences() {
		// Use the "flat" layout
		super(FLAT);
	}

	/**
	 * Creates the field editors
	 */
	@Override
	protected void createFieldEditors() {
		
		Map<String, String> map = PreferencesUtil.getHardwarePreferences();
		
		String[][] comboFields = new String[map.size()][2];
		
		int i = 0;
		for (String name : map.keySet()) {
			String[] campo = { name, name };
			comboFields[i] = campo;
			i++;
		}
		
		ComboFieldEditor cfe = new ComboFieldEditor(PreferencesUtil.HARDWARE_DISPOSITIVE, LanguageUtil.getLanguage().getString(
				"preferences.cert.hardware.dispositive"), comboFields, getFieldEditorParent());
		addField(cfe);
		
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {

		super.propertyChange(event);
		
		reiniciar = true;
		
	}
	
	@Override
	public boolean performOk() {
		
		boolean ok = super.performOk();
		
		if (reiniciar) {
//			InfoDialog id = new InfoDialog(Display.getDefault().getActiveShell());
//			id.open(LanguageResource.getLanguage().getString("info.preferences_changed"));
			reiniciar = false;
		}
		
		return ok;
	}
}
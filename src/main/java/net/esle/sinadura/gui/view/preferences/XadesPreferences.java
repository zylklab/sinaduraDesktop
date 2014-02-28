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


import net.esle.sinadura.gui.util.LanguageUtil;
import net.esle.sinadura.gui.util.PreferencesUtil;
import net.esle.sinadura.gui.util.PropertiesUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;


public class XadesPreferences extends FieldEditorPreferencePage {
	
	private static Log log = LogFactory.getLog(XadesPreferences.class);

	private BooleanFieldEditor archiveField = null;
	private BooleanFieldEditor xlOcspAddAllField = null;
	
	
	public XadesPreferences() {
		
		super(GRID);
	}

	@Override
	protected void createFieldEditors() {
		
		archiveField = new BooleanFieldEditor(PreferencesUtil.XADES_ARCHIVE, LanguageUtil.getLanguage().getString(
			"preferences.xades.archive"), getFieldEditorParent());
		addField(archiveField);
		
		boolean ocspAddAllVisible = Boolean.valueOf(PropertiesUtil.getConfiguration().getProperty(PropertiesUtil.PREFERENCES_XADES_XL_OCSP_ADD_ALL_VISIBLE));
		
		if (ocspAddAllVisible) {
			xlOcspAddAllField = new BooleanFieldEditor(PreferencesUtil.XADES_XL_OCSP_ADD_ALL, LanguageUtil.getLanguage().getString(
				"preferences.xades.xl.ocsp.add_all"), getFieldEditorParent());
			addField(xlOcspAddAllField);		
		}
	}
	
	
}
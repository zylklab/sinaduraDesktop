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

import java.util.Locale;
import java.util.StringTokenizer;

import net.esle.sinadura.gui.util.LanguageUtil;
import net.esle.sinadura.gui.util.PreferencesUtil;
import net.esle.sinadura.gui.util.PropertiesUtil;
import net.esle.sinadura.gui.view.main.InfoDialog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * @author zylk.net
 */
public class GeneralPreferences extends FieldEditorPreferencePage {

	private static Log log = LogFactory.getLog(GeneralPreferences.class);
	
	private static final String	DISPLAY_LANGUAGE	= "EN";
	
	private GenericStringFieldEditor saveExtension = null; 
	
	private BooleanFieldEditor checkOutput = null;
	
	private GenericDirectoryFieldEditor outputDir2 = null; 
	
	private BooleanFieldEditor autoValidate = null;
	
	private BooleanFieldEditor addDirRecursive = null;
	
	private BooleanFieldEditor statistics = null;

	private Shell parent;
	
	/**
	 * @param messages
	 */
	public GeneralPreferences() {
		// Use the "grid" layout
		super(GRID);
	}

	/**
	 * Creates the field editors
	 */
	@Override
	protected void createFieldEditors() {

		this.parent = this.getShell();
		
		StringTokenizer stringTokenizer = new StringTokenizer(PropertiesUtil.getConfiguration().getProperty(
				"idiomas.soportados"), ",");

		int numIdiomas = stringTokenizer.countTokens();

		String[][] comboFields = new String[numIdiomas][2];

		int index = 0;
		while (stringTokenizer.hasMoreElements()) {
			String idioma_value = (String) stringTokenizer.nextElement();
			StringTokenizer tokenizerIdiomaValue = new StringTokenizer(idioma_value, PreferencesUtil.TOKEN_LOCALE);
			String idioma = (String) tokenizerIdiomaValue.nextElement();
			String pais = (String) tokenizerIdiomaValue.nextElement();

			String idioma_label = new Locale(idioma, pais).getDisplayLanguage(new Locale(idioma, pais));
				// + " - " + new Locale(idioma, pais).getDisplayLanguage(new Locale(DISPLAY_LANGUAGE));
			
			if (idioma != null && idioma.equals("eu") && pais != null && pais.equalsIgnoreCase("es"))
				idioma_label = "Euskera";
			if (idioma!= null && idioma.equals("es") && pais != null && pais.equalsIgnoreCase("es"))
				idioma_label = "Castellano";

			String[] campo = { idioma_label.toLowerCase(), idioma_value };
			comboFields[index] = campo;
			index++;
		}

		ComboFieldEditor cfe = new ComboFieldEditor(PreferencesUtil.IDIOMA, LanguageUtil.getLanguage().getString("preferences.main.idiom"),
				comboFields, getFieldEditorParent());

		addField(cfe);

		if (PropertiesUtil.get(PropertiesUtil.PREFERENCES_VISIBLE_GENERAL_OUTPUT_AUTO_ENABLE).equals(PropertiesUtil.VISIBLE_TYPE_VISIBLE) ||
			(PropertiesUtil.get(PropertiesUtil.PREFERENCES_VISIBLE_GENERAL_OUTPUT_AUTO_ENABLE).equals(PropertiesUtil.VISIBLE_TYPE_HIDDEN_DEPENDANT) && 
			PropertiesUtil.getBoolean(PropertiesUtil.PREFERENCES_VISIBLE_ALL))) {
				
			checkOutput = new BooleanFieldEditor(PreferencesUtil.OUTPUT_AUTO_ENABLE, LanguageUtil.getLanguage().getString(
				"preferences.main.output.auto.enable"), getFieldEditorParent());
			addField(checkOutput);
			
			if (PropertiesUtil.get(PropertiesUtil.PREFERENCES_VISIBLE_GENERAL_OUTPUT_DIR).equals(PropertiesUtil.VISIBLE_TYPE_VISIBLE) ||
					(PropertiesUtil.get(PropertiesUtil.PREFERENCES_VISIBLE_GENERAL_OUTPUT_DIR).equals(PropertiesUtil.VISIBLE_TYPE_HIDDEN_DEPENDANT) && 
					PropertiesUtil.getBoolean(PropertiesUtil.PREFERENCES_VISIBLE_ALL))) {
				
				checkOutput.getDescriptionControl(getFieldEditorParent()).addListener(SWT.MouseUp, new Listener() {
					
					@Override
					public void handleEvent(Event arg0) {
						if (!checkOutput.getBooleanValue()) {
							outputDir2.setEnabled(true, getFieldEditorParent());
						} else {
							outputDir2.setEnabled(false, getFieldEditorParent());
						}
					}
				});
			}
		
		}
		
		if (PropertiesUtil.get(PropertiesUtil.PREFERENCES_VISIBLE_GENERAL_OUTPUT_DIR).equals(PropertiesUtil.VISIBLE_TYPE_VISIBLE) ||
				(PropertiesUtil.get(PropertiesUtil.PREFERENCES_VISIBLE_GENERAL_OUTPUT_DIR).equals(PropertiesUtil.VISIBLE_TYPE_HIDDEN_DEPENDANT) && 
				PropertiesUtil.getBoolean(PropertiesUtil.PREFERENCES_VISIBLE_ALL))) {
				
			// Add a directory field
			outputDir2 = new GenericDirectoryFieldEditor(PreferencesUtil.OUTPUT_DIR, LanguageUtil
					.getLanguage().getString("preferences.main.output_dir"), getFieldEditorParent());
			addField(outputDir2);
			
			if (PreferencesUtil.getPreferences().getString(PreferencesUtil.OUTPUT_AUTO_ENABLE).equals("true")) {
				outputDir2.setEnabled(false, getFieldEditorParent());
			} else {
				outputDir2.setEnabled(true, getFieldEditorParent());
			}
		}
		
		// -- sufijo
		//---------------
		
		if (PropertiesUtil.get(PropertiesUtil.PREFERENCES_VISIBLE_GENERAL_SAVE_EXTENSION).equals(PropertiesUtil.VISIBLE_TYPE_VISIBLE) ||
				(PropertiesUtil.get(PropertiesUtil.PREFERENCES_VISIBLE_GENERAL_SAVE_EXTENSION).equals(PropertiesUtil.VISIBLE_TYPE_HIDDEN_DEPENDANT) && 
				PropertiesUtil.getBoolean(PropertiesUtil.PREFERENCES_VISIBLE_ALL))) {	
			
			saveExtension = new GenericStringFieldEditor(PreferencesUtil.SAVE_EXTENSION, LanguageUtil.getLanguage()
					.getString("preferences.main.extension"), getFieldEditorParent(), 150);
			
			saveExtension.getTextControl(getFieldEditorParent()).addListener(SWT.KeyUp, new Listener() {
				
				@Override
				public void handleEvent(Event arg0) {
					if (saveExtension.getStringValue().trim().equals("")){
						InfoDialog dialog = new InfoDialog(parent);
						dialog.open(LanguageUtil.getLanguage().getString("warning.save_extension.empty"));
					}
				}
			});
			addField(saveExtension);
		}
		
		if (PropertiesUtil.get(PropertiesUtil.PREFERENCES_VISIBLE_GENERAL_AUTO_VALIDATE).equals(PropertiesUtil.VISIBLE_TYPE_VISIBLE) ||
				(PropertiesUtil.get(PropertiesUtil.PREFERENCES_VISIBLE_GENERAL_AUTO_VALIDATE).equals(PropertiesUtil.VISIBLE_TYPE_HIDDEN_DEPENDANT) && 
				PropertiesUtil.getBoolean(PropertiesUtil.PREFERENCES_VISIBLE_ALL))) {
			
			autoValidate = new BooleanFieldEditor(PreferencesUtil.AUTO_VALIDATE, LanguageUtil.getLanguage().getString(
				"preferences.main.auto.validate"), getFieldEditorParent());
			addField(autoValidate);
		}
		
		addDirRecursive = new BooleanFieldEditor(PreferencesUtil.ADD_DIR_RECURSIVE, LanguageUtil.getLanguage().getString(
			"preferences.main.add.dir.recursive"), getFieldEditorParent());
		addField(addDirRecursive);
		
		statistics = new BooleanFieldEditor(PreferencesUtil.ENABLE_STATISTICS, LanguageUtil.getLanguage().getString(
			"preferences.main.enable.statistics"), getFieldEditorParent());
		addField(statistics);
	}
 	
	@Override
	public boolean performOk() {
		boolean ok = super.performOk();
		LanguageUtil.reloadLanguage();
		return ok;
	}
}

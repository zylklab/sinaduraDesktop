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


import java.text.MessageFormat;

import net.esle.sinadura.ee.EEModulesController;
import net.esle.sinadura.ee.exceptions.EEModuleGenericException;
import net.esle.sinadura.ee.exceptions.EEModuleNotFoundException;
import net.esle.sinadura.ee.interfaces.IEEProxyModule;
import net.esle.sinadura.gui.util.LanguageUtil;
import net.esle.sinadura.gui.util.PreferencesUtil;
import net.esle.sinadura.gui.view.main.InfoDialog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;


public class ProxyPreferences extends FieldEditorPreferencePage {
	
	private static Log log = LogFactory.getLog(ProxyPreferences.class);
	
	private BooleanFieldEditor proxyPac = null;
	private StringFieldEditor user  = null;
	private PasswordStringFieldEditor pass = null;
	
	public ProxyPreferences() {	
		super(GRID);
	}
	
	@Override
	protected void createFieldEditors() {

		user = new StringFieldEditor(PreferencesUtil.PROXY_USER, LanguageUtil.getLanguage().getString("preferences.proxy.user"), getFieldEditorParent());
		addField(user);
		
		pass = new PasswordStringFieldEditor(PreferencesUtil.PROXY_PASS, LanguageUtil.getLanguage().getString("preferences.proxy.pass"), getFieldEditorParent());
		addField(pass);
		
		proxyPac = new BooleanFieldEditor(PreferencesUtil.PROXY_SYSTEM , LanguageUtil.getLanguage().getString("preferences.proxy.pac"), getFieldEditorParent());
		addField(proxyPac);
		proxyPac.getDescriptionControl(getFieldEditorParent()).addListener(SWT.MouseDown, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				if (!proxyPac.getBooleanValue()) {
					user.setEnabled(true, getFieldEditorParent());
					pass.setEnabled(true, getFieldEditorParent());
				} else {
					user.setEnabled(false, getFieldEditorParent());
					pass.setEnabled(false, getFieldEditorParent());
				}
			}
		});

		if (PreferencesUtil.getPreferences().getString(PreferencesUtil.PROXY_SYSTEM).equals("true")) {
			user.setEnabled(true, getFieldEditorParent());
			pass.setEnabled(true, getFieldEditorParent());
		} else {
			user.setEnabled(false, getFieldEditorParent());
			pass.setEnabled(false, getFieldEditorParent());
		}
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);	
	}
	
	@Override
	public boolean performOk() {
		
		boolean ok = super.performOk();
		
		if (user != null && pass != null && proxyPac != null && proxyPac.getBooleanValue()) {
			// ee (proxy)			
			try{
				EEModulesController eeController = new EEModulesController();
				IEEProxyModule proxyUtil = eeController.getProxyModule();
				proxyUtil.configureProxy(user.getStringValue(), pass.getStringValue());
				
			}catch(EEModuleNotFoundException e){
				InfoDialog dialog = new InfoDialog(this.getShell());
				dialog.open(MessageFormat.format(LanguageUtil.getLanguage().getString("ee.proxy.disabled"), "proxy"));
				
			}catch(EEModuleGenericException e){
				log.error(e);
			}
		}
		return ok;
	}
	
}
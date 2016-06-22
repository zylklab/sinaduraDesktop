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

import net.esle.sinadura.gui.util.ImagesUtil;
import net.esle.sinadura.gui.util.LanguageUtil;
import net.esle.sinadura.gui.util.PdfProfile;
import net.esle.sinadura.gui.util.PreferencesUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;

/**
 * @author zylk.net
 */
public class PdfPreferences extends FieldEditorPreferencePage {

	private static Log log = LogFactory.getLog(PdfPreferences.class);

	private Map<String, PdfProfile> pdfProfiles;
	private String tempDefault;
	
	private Composite compositeMain = null;
	private Combo comboTipoFirmaPDF = null;
	private List visualList = null;
	private Combo comboDefault = null;
	private Button checkAsk = null;
	
	private Button buttonAdd;
	private Button buttonEdit;
	private Button buttonRemove;
	
	
	public PdfPreferences() {
		
		super(FLAT);
	
		this.pdfProfiles = PreferencesUtil.getPdfProfiles();
		this.tempDefault = PreferencesUtil.getPreferenceStore().getString(PreferencesUtil.PDF_PROFILE_SELECTED_NAME);
	}

	@Override
	protected void createFieldEditors() {

	}

	@Override
	protected Control createContents(Composite parent) {

		// composite que contiene todos los elementos de la pantalla
		this.compositeMain = new Composite(parent, SWT.NONE);
		
		GridLayout gridLayoutPrincipal = new GridLayout();
		gridLayoutPrincipal.numColumns = 1;
		gridLayoutPrincipal.marginBottom = 5;
		this.compositeMain.setLayout(gridLayoutPrincipal);

		GridData gdPrincipal = new GridData();
		gdPrincipal.horizontalAlignment = GridData.FILL;
		gdPrincipal.verticalAlignment = GridData.FILL;
		gdPrincipal.grabExcessHorizontalSpace = true;
		gdPrincipal.grabExcessVerticalSpace = true;
		this.compositeMain.setLayoutData(gdPrincipal);

		createTopArea();

		createListArea();

		createDefaultArea();
		
		trigerComboSelectTipoFirma();
		
		return this.compositeMain;
	}

	private void createTopArea() {

		Composite topComposite = new Composite(this.compositeMain, SWT.NONE);
		
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = false;
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.FILL;
		topComposite.setLayoutData(gd);
		
		GridLayout gl = new GridLayout();
		gl.numColumns = 2;
		gl.marginBottom = 10;
		topComposite.setLayout(gl);
		
		// tipo firma
		Label labelCombo = new Label(topComposite, SWT.NONE);
		labelCombo.setText(LanguageUtil.getLanguage().getString("preferences.pdf.sign.type"));

		comboTipoFirmaPDF = new Combo(topComposite, SWT.NONE | SWT.READ_ONLY);
		comboTipoFirmaPDF.add(LanguageUtil.getLanguage().getString("preferences.pdf.sign.type.pdf"), 0);
		comboTipoFirmaPDF.add(LanguageUtil.getLanguage().getString("preferences.pdf.sign.type.xades"), 1);
		comboTipoFirmaPDF.select(Integer.valueOf(PreferencesUtil.getPreferenceStore().getString((PreferencesUtil.PDF_TIPO))));
		comboTipoFirmaPDF.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				trigerComboSelectTipoFirma();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				widgetSelected(arg0);
			}
		});
	}

	private void createListArea() {
		
		Composite compositeList = new Composite(this.compositeMain, SWT.NONE);
		
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		compositeList.setLayoutData(gd);
		
		GridLayout gl = new GridLayout();
		gl.numColumns = 2;
		gl.marginBottom = 5;
		compositeList.setLayout(gl);
		
		// title
		Label labelSello = new Label(compositeList, SWT.NONE);
		labelSello.setText(LanguageUtil.getLanguage().getString("preferences.pdf.profile.title"));
		labelSello.setFont(new Font(compositeMain.getDisplay(), Display.getDefault().getSystemFont().getFontData()[0].getName(),
				Display.getDefault().getSystemFont().getFontData()[0].getHeight(), SWT.BOLD));
		
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = false;
		labelSello.setLayoutData(gd);
		
		// lista
		this.visualList = new org.eclipse.swt.widgets.List(compositeList, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		this.visualList.addKeyListener(new SupButtonKeyListener());

		reloadVisualList();

		GridData gdList = new GridData();
		gdList.verticalSpan = 3;
		gdList.horizontalAlignment = GridData.FILL;
		gdList.verticalAlignment = GridData.FILL;
		gdList.grabExcessHorizontalSpace = true;
		gdList.grabExcessVerticalSpace = true;
		this.visualList.setLayoutData(gdList);

		// buttons
		buttonAdd = new Button(compositeList, SWT.NONE);
		GridData gdAdd = new GridData();
		gdAdd.horizontalAlignment = GridData.FILL;
		buttonAdd.setLayoutData(gdAdd);
		buttonAdd.setText(LanguageUtil.getLanguage().getString("button.add"));
		buttonAdd.setImage(new Image(this.compositeMain.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.ADD_IMG)));
		buttonAdd.addSelectionListener(new ButtonAddListener());

		buttonEdit = new Button(compositeList, SWT.NONE);
		GridData gdMod = new GridData();
		gdMod.horizontalAlignment = GridData.FILL;
		buttonEdit.setLayoutData(gdMod);
		buttonEdit.setText(LanguageUtil.getLanguage().getString("button.modify"));
		buttonEdit.setImage(new Image(this.compositeMain.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.EDIT_IMG)));
		buttonEdit.addSelectionListener(new ButtonModifyListener());

		buttonRemove = new Button(compositeList, SWT.NONE);
		GridData gdRemove = new GridData();
		gdRemove.verticalAlignment = GridData.BEGINNING;
		gdRemove.horizontalAlignment = GridData.FILL;
		buttonRemove.setLayoutData(gdRemove);
		buttonRemove.setText(LanguageUtil.getLanguage().getString("button.remove"));
		buttonRemove.setImage(new Image(this.compositeMain.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.REMOVE_IMG)));
		buttonRemove.addSelectionListener(new ButtonRemoveListener());
	}
	
	private void createDefaultArea() {
		
		Composite composite = new Composite(this.compositeMain, SWT.NONE);
		
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = false;
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.FILL;
		composite.setLayoutData(gd);
		
		GridLayout gl = new GridLayout();
		gl.numColumns = 1;
		gl.marginBottom = 10;
		composite.setLayout(gl);
		

		Label labelDefaultAreaDesc = new Label(composite, SWT.NONE);
		labelDefaultAreaDesc.setText(LanguageUtil.getLanguage().getString("preferences.pdf.profile.selected"));

		comboDefault = new Combo(composite, SWT.NONE | SWT.READ_ONLY);
		comboDefault.addSelectionListener(new ComboDefaultChangeListener());
		
		// ask profile
		checkAsk = new Button(composite, SWT.CHECK);
		checkAsk.setText(LanguageUtil.getLanguage().getString("preferences.pdf.profile.selection.ask"));
		checkAsk.setSelection(true);
		gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		checkAsk.setLayoutData(gd);
		
		boolean selectionAsk = PreferencesUtil.getPreferenceStore().getBoolean(PreferencesUtil.PDF_PROFILE_SELECTION_ASK);
		checkAsk.setSelection(selectionAsk);
		
		reloadComboDefault();
	}
	
	
	private void reloadComboDefault() {
		
		// cargar combo
		comboDefault.removeAll();
		for (PdfProfile pdfProfile : pdfProfiles.values()) {
			
			comboDefault.add(pdfProfile.getName());
		}
		comboDefault.getParent().layout();
		
		comboDefault.setText(tempDefault);
		if (comboDefault.getText().equals("")) {
			if (comboDefault.getItemCount() != 0 ) {
				comboDefault.select(0);
				tempDefault = comboDefault.getText();
			}
		} 
	}

	private void savePreferences() {

		PreferencesUtil.savePdfProfiles(pdfProfiles);
		PreferencesUtil.getPreferenceStore().setValue(PreferencesUtil.PDF_TIPO, String.valueOf(comboTipoFirmaPDF.getSelectionIndex()));
		PreferencesUtil.getPreferenceStore().setValue(PreferencesUtil.PDF_PROFILE_SELECTION_ASK, String.valueOf(checkAsk.getSelection()));
		
		if (comboDefault != null) {
			PreferencesUtil.getPreferenceStore().setValue(PreferencesUtil.PDF_PROFILE_SELECTED_NAME, comboDefault.getText());
		}
		
	}

	@Override
	protected void performApply() {
		
		savePreferences();
		super.performApply();
	}
	

	@Override
	public boolean performOk() {
		
		PreferencesUtil.savePdfProfiles(pdfProfiles);		
		return super.performOk();
	}

	private void trigerComboSelectTipoFirma() {
		
		if (comboTipoFirmaPDF.getSelectionIndex() == 1) {
			visualList.setEnabled(false);
			buttonAdd.setEnabled(false);
			buttonRemove.setEnabled(false);
			buttonEdit.setEnabled(false);
			comboDefault.setEnabled(false);
			checkAsk.setEnabled(false);
			
		} else {
			visualList.setEnabled(true);
			buttonAdd.setEnabled(true);
			buttonRemove.setEnabled(true);
			buttonEdit.setEnabled(true);
			comboDefault.setEnabled(true);
			checkAsk.setEnabled(true);
		}
	}

	// ==============================================
	// Table actions
	// ==============================================
	
	private void reloadVisualList() {
		
		// inicializar lista
		visualList.removeAll();
		
		for (PdfProfile profile : this.pdfProfiles.values()) {
			visualList.add(profile.getName());
		}
	}

	
	private void removeTableFile() {
		
		if (visualList.getSelectionCount() > 0) {
		
			if (visualList.getItemCount() > 1) {
				
				String selectedName = visualList.getSelection()[0];
				this.pdfProfiles.remove(selectedName);
				
				reloadVisualList();
				reloadComboDefault();
			}
		}
	}

	class ComboDefaultChangeListener implements SelectionListener {
		
		public void widgetSelected(SelectionEvent event) {

			tempDefault = comboDefault.getText();
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}
	}
	
	// ===================================
	// Button Listeners
	// ====================================

	private class ButtonAddListener implements SelectionListener {

		public void widgetSelected(SelectionEvent event) {
			
			PdfProfilePreferencesDialog pdfDialog = new PdfProfilePreferencesDialog(compositeMain.getShell(), null);
			pdfDialog.open();
			PdfProfile newProfile = pdfDialog.getPdfProfile();
			
			if (newProfile != null) {
				pdfProfiles.put(newProfile.getName(), newProfile);
				
				reloadVisualList();
				reloadComboDefault();
			}
			
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}
	}

	private class ButtonModifyListener implements SelectionListener {

		public void widgetSelected(SelectionEvent event) {
			
			if (visualList.getSelectionCount() > 0) {
			
				String selectedName = visualList.getSelection()[0];
				
				if (selectedName != null) {
					PdfProfile updatedProfile = pdfProfiles.get(selectedName);
					PdfProfilePreferencesDialog pdfDialog = new PdfProfilePreferencesDialog(compositeMain.getShell(), updatedProfile);
					pdfDialog.open();
					updatedProfile = pdfDialog.getPdfProfile();
					
					if (updatedProfile != null) {
						pdfProfiles.remove(selectedName);
						pdfProfiles.put(updatedProfile.getName(), updatedProfile);
						
						reloadVisualList();
						reloadComboDefault();
					}
				}
			}
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}
	}

	private class ButtonRemoveListener implements SelectionListener {

		public void widgetSelected(SelectionEvent event) {
			removeTableFile();
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}
	}

	private class SupButtonKeyListener implements KeyListener {

		public void keyPressed(KeyEvent e) {
		}

		public void keyReleased(KeyEvent e) {
			if (SWT.DEL == e.character) {
				removeTableFile();
			}
		}
	}
}

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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

/**
 * @author zylk.net
 */
public class PdfPreferences extends FieldEditorPreferencePage {

	private static Log log = LogFactory.getLog(PdfPreferences.class);

	private Composite compositeMain = null;
	private Combo comboTipoFirmaPDF = null;

	private java.util.List<PdfProfile> pdfProfiles;
	private List visualList = null;
	private Button buttonAdd;
	private Button buttonShow;
	private Button buttonRemove;
	
	public PdfPreferences() {
		super(FLAT);
	}

	@Override
	protected void createFieldEditors() {

	}

	@Override
	protected Control createContents(Composite parent) {

		// composite que contiene todos los elementos de la pantalla
		this.compositeMain = new Composite(parent, SWT.NONE);
		GridLayout gridLayoutPrincipal = new GridLayout();
		gridLayoutPrincipal.numColumns = 2;
		gridLayoutPrincipal.verticalSpacing = 5;
		gridLayoutPrincipal.marginBottom = 5;
		this.compositeMain.setLayout(gridLayoutPrincipal);

		GridData gdPrincipal = new GridData();
		gdPrincipal.horizontalAlignment = GridData.FILL;
		gdPrincipal.verticalAlignment = GridData.FILL;
		gdPrincipal.grabExcessHorizontalSpace = true;
		gdPrincipal.grabExcessVerticalSpace = true;
		this.compositeMain.setLayoutData(gdPrincipal);

		createArea();
		createListArea();

		trigerComboSelectTipoFirma();
		return this.compositeMain;
	}

	private void createArea() {

		// tipo firma
		GridData gdComboTipoFirma = new GridData();
		gdComboTipoFirma.verticalAlignment = SWT.NONE;
		gdComboTipoFirma.horizontalAlignment = SWT.NONE;
		gdComboTipoFirma.grabExcessHorizontalSpace = true;

		Label labelCombo = new Label(this.compositeMain, SWT.NONE);
		labelCombo.setText("i18n-Tipo Firma");
		labelCombo.setLayoutData(gdComboTipoFirma);

		comboTipoFirmaPDF = new Combo(this.compositeMain, SWT.NONE | SWT.READ_ONLY);
		comboTipoFirmaPDF.add("i18n-Firma PDF", 0);
		comboTipoFirmaPDF.add("i18n-Firma XML", 1);
		comboTipoFirmaPDF.select(Integer.valueOf(PreferencesUtil.getPreferences().getString((PreferencesUtil.PDF_TIPO))));
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

		Label labelSello = new Label(this.compositeMain, SWT.FILL);
		labelSello.setText("i18n-Perfiles disponibles para el sello de 'Firma PDF'");
		
		Composite profileComposite = new Composite(this.compositeMain, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.widthHint = 0;
		gd.heightHint = 0;
		profileComposite.setLayoutData(gd);
		
		GridLayout gl = new GridLayout();
		gl.numColumns = 2;
		gl.verticalSpacing = 5;
		gl.marginBottom = 5;
		profileComposite.setLayout(gl);
		
		Composite compositeList = new Composite(profileComposite, SWT.NONE);
		GridData gdListComposite = new GridData(GridData.FILL_BOTH);
		gdListComposite.grabExcessHorizontalSpace = true;
		gdListComposite.grabExcessVerticalSpace = true;
		gdListComposite.widthHint = 0;
		gdListComposite.heightHint = 0;
		compositeList.setLayoutData(gdListComposite);
		compositeList.setLayout(new GridLayout());

		this.visualList = new org.eclipse.swt.widgets.List(compositeList, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		this.visualList.addKeyListener(new SupButtonKeyListener());

		reloadVisualList();

		GridData gdList = new GridData();
		gdList.horizontalAlignment = GridData.FILL;
		gdList.verticalAlignment = GridData.FILL;
		gdList.grabExcessHorizontalSpace = true;
		gdList.grabExcessVerticalSpace = true;
		this.visualList.setLayoutData(gdList);

		Composite compositeButtons = new Composite(profileComposite, SWT.NONE);
		GridData gdButtonsComposite = new GridData();
		gdButtonsComposite.grabExcessHorizontalSpace = false;
		gdButtonsComposite.grabExcessVerticalSpace = false;
		compositeButtons.setLayoutData(gdButtonsComposite);
		compositeButtons.setLayout(new GridLayout());

		buttonAdd = new Button(compositeButtons, SWT.NONE);
		GridData gdAdd = new GridData();
		gdAdd.horizontalAlignment = GridData.FILL;
		buttonAdd.setLayoutData(gdAdd);
		buttonAdd.setText(LanguageUtil.getLanguage().getString("button.add"));
		buttonAdd.setImage(new Image(this.compositeMain.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.ADD_IMG)));
		buttonAdd.addSelectionListener(new ButtonAddListener());

		buttonShow = new Button(compositeButtons, SWT.NONE);
		GridData gdMod = new GridData();
		gdMod.horizontalAlignment = GridData.FILL;
		buttonShow.setLayoutData(gdMod);
		buttonShow.setText(LanguageUtil.getLanguage().getString("button.show"));
		buttonShow.setImage(new Image(this.compositeMain.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.EDIT_IMG)));
		buttonShow.addSelectionListener(new ButtonShowListener());

		buttonRemove = new Button(compositeButtons, SWT.NONE);
		GridData gdRemove = new GridData();
		gdRemove.horizontalAlignment = GridData.FILL;
		gdRemove.verticalAlignment = GridData.BEGINNING;
		buttonRemove.setLayoutData(gdRemove);
		buttonRemove.setText(LanguageUtil.getLanguage().getString("button.remove"));
		buttonRemove.setImage(new Image(this.compositeMain.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.REMOVE_IMG)));
		buttonRemove.addSelectionListener(new ButtonRemoveListener());
	}

	private void savePreferences() {

		PreferencesUtil.savePdfProfiles(pdfProfiles);
		PreferencesUtil.getPreferences().setValue(PreferencesUtil.PDF_TIPO, String.valueOf(comboTipoFirmaPDF.getSelectionIndex()));
		// TODO guardamos nuevo perfil
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
			buttonShow.setEnabled(false);
			
		} else {
			visualList.setEnabled(true);
			buttonAdd.setEnabled(true);
			buttonRemove.setEnabled(true);
			buttonShow.setEnabled(true);
		}
	}

	// ==============================================
	// Table actions
	// ==============================================
	private void reloadVisualList() {
		reloadVisualList(true);
	}
	
	private void reloadVisualList(boolean fetch) {
		
		// inicializar lista
		visualList.removeAll();
		
		if (fetch) {
			this.pdfProfiles = PreferencesUtil.getPdfProfiles();			
		}
		for(PdfProfile profile : this.pdfProfiles){
			visualList.add(profile.toString());
		}
	}

	
	private void removeTableFile() {
		
		int index = visualList.getSelectionIndex();
		this.pdfProfiles.remove(index);
		visualList.remove(index);
	}

	// ===================================
	// Button Listeners
	// ====================================

	private class ButtonAddListener implements SelectionListener {

		public void widgetSelected(SelectionEvent event) {
			
			PdfProfile newProfile = null;
			PdfProfilePreferencesDialog pdfDialog= new PdfProfilePreferencesDialog(compositeMain.getShell(), newProfile);
			pdfDialog.open();
			
			pdfProfiles.add(newProfile);
			
			reloadVisualList(false);

		}

		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}
	}

	private class ButtonShowListener implements SelectionListener {

		public void widgetSelected(SelectionEvent event) {
			
			int i = visualList.getSelectionIndex();
			if (i != -1) {
				PdfProfile selectedProfile = pdfProfiles.get(i);
				PdfProfilePreferencesDialog pdfDialog = new PdfProfilePreferencesDialog(compositeMain.getShell(), selectedProfile);
				pdfDialog.open();
				pdfProfiles.set(i, selectedProfile);
				
				reloadVisualList(false);				
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

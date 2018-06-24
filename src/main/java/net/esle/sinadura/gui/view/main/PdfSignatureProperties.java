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
package net.esle.sinadura.gui.view.main;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.itextpdf.text.pdf.PdfSignatureAppearance;

import net.esle.sinadura.core.util.FileUtil;
import net.esle.sinadura.gui.util.LanguageUtil;
import net.esle.sinadura.gui.util.PdfProfile;

/**
 * @author zylk.net
 */
public class PdfSignatureProperties {

	private static Log log = LogFactory.getLog(PdfSignatureProperties.class);

	private Composite compositeMain = null;
	private PdfProfile pdfProfile;

	
	private Button checkVisible = null;
	private Button checkAskPosition = null;
	private Button checkSello = null;
	private Text textRuta = null;
	private Text textReason = null;
	private Text textLocation = null;
	private Button buttonBrowse = null;
	private Label label = null;
	private Combo comboCertified = null;
	private Label labelCertified = null;

	
	public PdfSignatureProperties(Composite composite, PdfProfile profile) {

		this.compositeMain = composite;
		
		if (profile != null) {
			this.pdfProfile = new PdfProfile(profile);
		}
		
		createContents();
		
		updateProfile();
		
		updateControlState();
	}
	

	private Control createContents() {

		// composite que contiene todos los elementos de la pantalla
		GridLayout gridLayoutPrincipal = new GridLayout();
		gridLayoutPrincipal.numColumns = 3;
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
		
		return this.compositeMain;
	}

	private void createArea() {

		GridData gd = new GridData();
		
		// firma visible
		checkVisible = new Button(this.compositeMain, SWT.CHECK);
		checkVisible.setText(LanguageUtil.getLanguage().getString("preferences.pdf.sign_visible"));
		checkVisible.setSelection(true);
		gd = new GridData();
		gd.horizontalSpan = 3;
		gd.grabExcessHorizontalSpace = true;
		checkVisible.setLayoutData(gd);
		checkVisible.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				
				updateControlState();
			}
		});
		
		// askPosition
		checkAskPosition = new Button(this.compositeMain, SWT.CHECK);
		checkAskPosition.setText(LanguageUtil.getLanguage().getString("preferences.pdf.profile.position.ask"));
		checkAskPosition.setSelection(true);
		gd = new GridData();
		gd.horizontalSpan = 3;
		gd.grabExcessHorizontalSpace = true;
		checkAskPosition.setLayoutData(gd);
		checkAskPosition.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				
				updateControlState();
			}
		});
		
		// imagen
		checkSello = new Button(this.compositeMain, SWT.CHECK);
		checkSello.setText(LanguageUtil.getLanguage().getString("preferences.pdf.stamp_active"));
		gd = new GridData();
		gd.horizontalSpan = 3;
		gd.grabExcessHorizontalSpace = true;
		checkSello.setLayoutData(gd);
		checkSello.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				
				updateControlState();
			}
		});

		label = new Label(this.compositeMain, SWT.NONE);
		label.setText(LanguageUtil.getLanguage().getString("preferences.pdf.stamp.input"));

		textRuta = new Text(this.compositeMain, SWT.BORDER);
		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		textRuta.setLayoutData(gd);
		
		buttonBrowse = new Button(this.compositeMain, SWT.NONE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		gd.grabExcessHorizontalSpace = false;
		buttonBrowse.setLayoutData(gd);
		buttonBrowse.setText(LanguageUtil.getLanguage().getString("button.browse"));
		buttonBrowse.addSelectionListener(new ButtonBrowseListener());

		// certified
		String[][] comboFields2 = {
				{ LanguageUtil.getLanguage().getString("preferences.pdf.combo.not.certified"),
						"" + PdfSignatureAppearance.NOT_CERTIFIED },
				{ LanguageUtil.getLanguage().getString("preferences.pdf.combo.no.changes.allowed"),
						"" + PdfSignatureAppearance.CERTIFIED_NO_CHANGES_ALLOWED },
				{ LanguageUtil.getLanguage().getString("preferences.pdf.combo.form.filling"),
						"" + PdfSignatureAppearance.CERTIFIED_FORM_FILLING },
				{ LanguageUtil.getLanguage().getString("preferences.pdf.combo.form.filling.and.annotations"),
						"" + PdfSignatureAppearance.CERTIFIED_FORM_FILLING_AND_ANNOTATIONS } };

		this.labelCertified = new Label(this.compositeMain, SWT.NONE);
		this.labelCertified.setText(LanguageUtil.getLanguage().getString("preferences.pdf.certified"));

		this.comboCertified = new Combo(this.compositeMain, SWT.NONE | SWT.READ_ONLY);
		GridData gdCert = new GridData();
		gdCert.horizontalSpan = 2;
		gdCert.grabExcessHorizontalSpace = true;
		this.comboCertified.setLayoutData(gdCert);

		int k = 0;
		for (String[] value : comboFields2) {
			this.comboCertified.add(value[0], k);
			k++;
		}

		// reason
		Label labelReason = new Label(this.compositeMain, SWT.NONE);
		labelReason.setText(LanguageUtil.getLanguage().getString("preferences.pdf.reason"));
		gd = new GridData();
		gd.verticalAlignment = GridData.BEGINNING;
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalIndent = 3;
		labelReason.setLayoutData(gd);

		textReason = new Text(this.compositeMain, SWT.WRAP | SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gdTextReason = new GridData();
		gdTextReason.horizontalSpan = 2;
		gdTextReason.grabExcessHorizontalSpace = true;
		gdTextReason.horizontalAlignment = SWT.FILL;
		gdTextReason.grabExcessVerticalSpace = true;
		gdTextReason.verticalAlignment = SWT.FILL;
		textReason.setLayoutData(gdTextReason);
		
		// location
		Label labelLocation = new Label(this.compositeMain, SWT.NONE);
		labelLocation.setText(LanguageUtil.getLanguage().getString("preferences.pdf.location"));
		gd = new GridData();
		gd.verticalAlignment = GridData.BEGINNING;
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalIndent = 2;
		labelLocation.setLayoutData(gd);

		textLocation = new Text(this.compositeMain, SWT.WRAP | SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gdTextLocation = new GridData();
		gdTextLocation.horizontalSpan = 2;
		gdTextLocation.grabExcessHorizontalSpace = true;
		gdTextLocation.horizontalAlignment = SWT.FILL;
		gdTextLocation.grabExcessVerticalSpace = true;
		gdTextLocation.verticalAlignment = SWT.FILL;
		textLocation.setLayoutData(gdTextLocation);
		
	}

	class ButtonBrowseListener implements SelectionListener {

		public void widgetSelected(SelectionEvent event) {

			String ruta = FileDialogs.openFileDialog(compositeMain.getShell(), new String[] { FileUtil.EXTENSION_PNG, FileUtil.EXTENSION_JPG, FileUtil.EXTENSION_GIF }, true);
			if (ruta != null) {
				textRuta.setText(ruta);
			}
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}
	}
	
	private void updateControlState() {
		
		if (checkVisible.getSelection()) {
			
			checkSello.setEnabled(true);
			
			if (checkSello.getSelection()) {
				label.setEnabled(true);
				textRuta.setEnabled(true);
				buttonBrowse.setEnabled(true);
			} else {
				label.setEnabled(false);
				textRuta.setEnabled(false);
				buttonBrowse.setEnabled(false);
			}
			
		} else {
			
			checkSello.setEnabled(false);
			label.setEnabled(false);
			textRuta.setEnabled(false);
			buttonBrowse.setEnabled(false);
		}

	}
	
	private void updateProfile() {
		
		if (pdfProfile != null) {
			checkVisible.setSelection(pdfProfile.getVisible());
			checkAskPosition.setSelection(pdfProfile.getAskPosition());
			checkSello.setSelection(pdfProfile.hasImage());
			textRuta.setText(pdfProfile.getImagePath());
			
			switch (pdfProfile.getCertified()) {
			case PdfSignatureAppearance.CERTIFIED_NO_CHANGES_ALLOWED:
				this.comboCertified.select(1);
				break;
			case PdfSignatureAppearance.CERTIFIED_FORM_FILLING:
				this.comboCertified.select(2);
				break;
			case PdfSignatureAppearance.CERTIFIED_FORM_FILLING_AND_ANNOTATIONS:
				this.comboCertified.select(3);
				break;
			default:
				this.comboCertified.select(0);
			}
			
			if (pdfProfile.getReason() != null){
				textReason.setText(pdfProfile.getReason());			
			}
			
			if (pdfProfile.getLocation() != null) {
				textLocation.setText(pdfProfile.getLocation());	
			}
		}
	}
	
	public void reloadProfile(PdfProfile newProfile) {
		
		pdfProfile = new PdfProfile(newProfile);
		
		updateProfile();
		updateControlState();
	}
	
	public PdfProfile getProfile() {

		pdfProfile.setVisible(checkVisible.getSelection());
		pdfProfile.setAskPosition(checkAskPosition.getSelection());
		pdfProfile.setHasImage(checkSello.getSelection());
		pdfProfile.setImagePath(textRuta.getText());
		pdfProfile.setCertified(comboCertified.getSelectionIndex());
		pdfProfile.setReason(textReason.getText());
		pdfProfile.setLocation(textLocation.getText());
		
		return pdfProfile;
	}
}

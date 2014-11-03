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

import java.io.IOException;

import net.esle.sinadura.core.model.PdfSignatureField;
import net.esle.sinadura.core.util.FileUtil;
import net.esle.sinadura.gui.util.ImagesUtil;
import net.esle.sinadura.gui.util.LanguageUtil;
import net.esle.sinadura.gui.util.PdfProfile;
import net.esle.sinadura.gui.view.main.FileDialogs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.itextpdf.text.pdf.PdfSignatureAppearance;

/**
 * @author zylk.net
 */
public class PdfProfilePreferences {

	private static Log log = LogFactory.getLog(PdfProfilePreferences.class);

	private Composite compositeMain = null;
	private PdfProfile profile;

	private Text textName = null;
	private Text textAcroField;
	private Button checkVisible = null;
	private Button checkAskPosition = null;
	
	private Button checkSello = null;
	private Text textRuta = null;
	private Text textReason = null;
	private Text textLocation = null;
	private Label labelPosicion;
	private Button buttonPosition = null;
	private Button buttonBrowse = null;
	private Label label = null;
	private Combo comboSelectPage = null;
	private Label labelPages = null;
	private Text textSelectPage = null;
	private Combo comboOCSP = null;
	private Label labelOCSP = null;
	
	private PdfSignatureField signatureField = null;

	
	public PdfProfilePreferences(Composite composite, PdfProfile profile) {

		this.compositeMain = composite;
		this.profile = profile;

		this.signatureField = new PdfSignatureField();
		this.signatureField.setStartX(profile.getStartX());
		this.signatureField.setStartY(profile.getStartY());
		this.signatureField.setWidht(profile.getWidht());
		this.signatureField.setHeight(profile.getHeight());
		
		createContents();
		
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
		
		// profile name
		label = new Label(this.compositeMain, SWT.NONE);
		label.setText("i18n - Nombre Perfil");
		
		textName = new Text(this.compositeMain, SWT.BORDER);
		textName.setText(profile.getName());
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalSpan = 2;
		textName.setLayoutData(gd);
		
		// firma visible
		checkVisible = new Button(this.compositeMain, SWT.CHECK);
		checkVisible.setText(LanguageUtil.getLanguage().getString("preferences.pdf.sign_visible"));
		checkVisible.setSelection(true);
		gd = new GridData();
		gd.horizontalSpan = 3;
		gd.grabExcessHorizontalSpace = true;
		checkVisible.setLayoutData(gd);
		checkVisible.setSelection(profile.getVisible());
		checkVisible.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				
				updateControlState();
			}
		});

		// askPosition
		checkAskPosition = new Button(this.compositeMain, SWT.CHECK);
		checkAskPosition.setText("i18n - preguntar la posicion en el momento de la firma");
		checkAskPosition.setSelection(true);
		gd = new GridData();
		gd.horizontalSpan = 3;
		gd.grabExcessHorizontalSpace = true;
		checkAskPosition.setLayoutData(gd);
		checkAskPosition.setSelection(profile.getAskPosition());
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
		checkSello.setSelection(profile.hasImage());
		checkSello.setEnabled(profile.getVisible());
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
		textRuta.setText(profile.getImagePath());	
		
		buttonBrowse = new Button(this.compositeMain, SWT.NONE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		gd.grabExcessHorizontalSpace = false;
		buttonBrowse.setLayoutData(gd);
		buttonBrowse.setText(LanguageUtil.getLanguage().getString("button.browse"));
		buttonBrowse.addSelectionListener(new ButtonBrowseListener());

		// posicion
		labelPosicion = new Label(this.compositeMain, SWT.NONE);
		labelPosicion.setText(LanguageUtil.getLanguage().getString("preferences.pdf.image.position"));
		labelPosicion.setLayoutData(new GridData());

		buttonPosition = new Button(this.compositeMain, SWT.NONE);
		buttonPosition.setImage(new Image(this.compositeMain.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.STAMP_POSITION_IMG)));
		buttonPosition.addSelectionListener(new ButtonPositionListener());
		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.grabExcessHorizontalSpace = false;
		buttonPosition.setLayoutData(gd);

		// pagina
		labelPages = new Label(this.compositeMain, SWT.NONE);
		labelPages.setText(LanguageUtil.getLanguage().getString("preferences.pdf.page.location"));

		comboSelectPage = new Combo(this.compositeMain, SWT.NONE | SWT.READ_ONLY);
		comboSelectPage.add(LanguageUtil.getLanguage().getString("preferences.pdf.last.page"), 0);
		comboSelectPage.add(LanguageUtil.getLanguage().getString("preferences.pdf.first.page"), 1);
		comboSelectPage.add(LanguageUtil.getLanguage().getString("preferences.pdf.select.page"), 2);
		comboSelectPage.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (comboSelectPage.getSelectionIndex() > 1) {
					textSelectPage.setEnabled(true);
				} else {
					textSelectPage.setEnabled(false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				widgetSelected(arg0);
			}
		});
		GridData gdComboSelPage = new GridData(GridData.FILL_HORIZONTAL);
		gdComboSelPage.grabExcessHorizontalSpace = true;
		gdComboSelPage.horizontalSpan = 1;
		comboSelectPage.setLayoutData(gdComboSelPage);

		textSelectPage = new Text(this.compositeMain, SWT.NONE | SWT.BORDER);
		textSelectPage.setEnabled(false);
		GridData gdTextSelpage = new GridData();
		gdTextSelpage.grabExcessHorizontalSpace = false;
		textSelectPage.setLayoutData(gdTextSelpage);

		int page = profile.getPage();
		switch(page){
			case 0:
				comboSelectPage.select(0);
				break;
			case 1:
				comboSelectPage.select(1);
				break;
			default:
				comboSelectPage.select(2);
				textSelectPage.setText(String.valueOf(page));
				textSelectPage.setEnabled(true);
				break;
		}

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

		this.labelOCSP = new Label(this.compositeMain, SWT.NONE);
		this.labelOCSP.setText(LanguageUtil.getLanguage().getString("preferences.pdf.certified"));

		this.comboOCSP = new Combo(this.compositeMain, SWT.NONE | SWT.READ_ONLY);
		GridData gdCert = new GridData();
		gdCert.horizontalSpan = 2;
		gdCert.grabExcessHorizontalSpace = true;
		this.comboOCSP.setLayoutData(gdCert);

		int k = 0;
		for (String[] value : comboFields2) {
			this.comboOCSP.add(value[0], k);
			k++;
		}

		switch(profile.getCertified()){
			case PdfSignatureAppearance.CERTIFIED_NO_CHANGES_ALLOWED:
				this.comboOCSP.select(1);
				break;
			case PdfSignatureAppearance.CERTIFIED_FORM_FILLING:
				this.comboOCSP.select(2);
				break;
			case PdfSignatureAppearance.CERTIFIED_FORM_FILLING_AND_ANNOTATIONS:
				this.comboOCSP.select(3);
				break;
			default:
				this.comboOCSP.select(0);	
		}

		// acroField
		Label labelAcroField = new Label(this.compositeMain, SWT.NONE);
		labelAcroField.setText("i18n-AcroField name");
		labelAcroField.setLayoutData(new GridData());

		textAcroField = new Text(this.compositeMain, SWT.NONE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		textAcroField.setLayoutData(gd);
		
		if (profile.getAcroField() != null) {
			textAcroField.setText(profile.getAcroField());			
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
		if (profile.getReason() != null){
			textReason.setText(profile.getReason());			
		}

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
		if (profile.getLocation() != null) {
			textLocation.setText(profile.getLocation());	
		}
		

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

	class ButtonPositionListener implements SelectionListener {

		public void widgetSelected(SelectionEvent event) {
			
			String path = null;
			if (checkSello.getSelection()) {
				path = textRuta.getText();
			}
			
			try {
				PdfSignatureFieldPositionDialog pdfSignatureFieldPositionDialog = new PdfSignatureFieldPositionDialog(Display.getDefault().getActiveShell(), path, signatureField);
				PdfSignatureField psf = pdfSignatureFieldPositionDialog.createSShell();
				if (psf != null) {
					signatureField = psf;
				}	
				
			} catch (IOException e) {
				// no se puede dar en este caso
				log.error(e);
			}
			
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}
	}
	
	
	private void updateControlState() {
		
		if (checkVisible.getSelection()) {
			
			checkAskPosition.setEnabled(true);
			checkSello.setEnabled(true);
			labelPosicion.setEnabled(true);
			buttonPosition.setEnabled(true);
			labelPages.setEnabled(true);
			comboSelectPage.setEnabled(true);
			
			if (comboSelectPage.getSelectionIndex() > 1) {
				textSelectPage.setEnabled(true);
			} else {
				textSelectPage.setEnabled(false);
			}
			
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
			checkAskPosition.setEnabled(false);
			checkSello.setEnabled(false);
			labelPosicion.setEnabled(false);
			buttonPosition.setEnabled(false);
			labelPages.setEnabled(false);
			comboSelectPage.setEnabled(false);
			textSelectPage.setEnabled(false);
			label.setEnabled(false);
			textRuta.setEnabled(false);
			buttonBrowse.setEnabled(false);
		}

	}
	
	
	public PdfProfile getProfile() {

		profile.setName(textName.getText());
		profile.setAcroField(textAcroField.getText());
		profile.setVisible(checkVisible.getSelection());
		profile.setAskPosition(checkAskPosition.getSelection());
		profile.setReason(textReason.getText());
		profile.setLocation(textLocation.getText());
		profile.setHasImage(checkSello.getSelection());
		profile.setImagePath(textRuta.getText());
		profile.setCertified(comboOCSP.getSelectionIndex());
		
		switch(comboSelectPage.getSelectionIndex()){
			case 0:
			case 1:
				profile.setPage(comboSelectPage.getSelectionIndex());
				break;
				
			default:
				profile.setPage(Integer.valueOf(textSelectPage.getText()));
				break;
		}
		
		profile.setStartX(signatureField.getStartX());
		profile.setStartY(signatureField.getStartY());
		profile.setWidht(signatureField.getWidht());
		profile.setHeight(signatureField.getHeight());
		
		return profile;
	}
}

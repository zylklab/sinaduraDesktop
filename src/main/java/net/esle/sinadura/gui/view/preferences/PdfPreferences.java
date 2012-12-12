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

import java.awt.Rectangle;

import net.esle.sinadura.core.util.FileUtil;
import net.esle.sinadura.gui.util.ImagesUtil;
import net.esle.sinadura.gui.util.LanguageUtil;
import net.esle.sinadura.gui.util.PreferencesUtil;
import net.esle.sinadura.gui.view.main.FileDialogs;
import net.esle.sinadura.gui.view.main.InfoDialog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
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
public class PdfPreferences extends FieldEditorPreferencePage {

	private static Log log = LogFactory.getLog(PdfPreferences.class);

	private Composite compositeMain = null;

	private Button checkVisible = null;
	private Combo comboTipoFirmaPDF = null;
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

	private Rectangle rectangle = null;
	// diferencial entre el sistema de medición de itext y el de esta pantalla
	private static final Float RELACION = new Float(1.375816993);

	public PdfPreferences() {
		// Use the "flat" layout
		super(FLAT);

		this.rectangle = new Rectangle(new Float(PreferencesUtil.getPreferences().getInt(PreferencesUtil.PDF_STAMP_X) / RELACION)
				.intValue(), new Float(PreferencesUtil.getPreferences().getInt(PreferencesUtil.PDF_STAMP_Y) / RELACION).intValue(),
				new Float(PreferencesUtil.getPreferences().getInt(PreferencesUtil.PDF_STAMP_WIDTH) / RELACION).intValue(), new Float(
						PreferencesUtil.getPreferences().getInt(PreferencesUtil.PDF_STAMP_HEIGHT) / RELACION).intValue());

	}

	@Override
	protected void createFieldEditors() {

	}

	@Override
	protected Control createContents(Composite parent) {

		// composite que contiene todos los elementos de la pantalla
		this.compositeMain = new Composite(parent, SWT.NONE);
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
		trigerComboSelectTipoFirma();
		return this.compositeMain;
	}

	private void createArea() {

		
		// tipo firma
		GridData gdComboTipoFirma = new GridData();
		gdComboTipoFirma.verticalSpan = 3;
		gdComboTipoFirma.verticalAlignment = SWT.NONE;
		gdComboTipoFirma.horizontalAlignment = SWT.NONE;
		gdComboTipoFirma.grabExcessHorizontalSpace = true;

		Label labelCombo = new Label(this.compositeMain, SWT.NONE);
		labelCombo.setText("Tipo Firma");
		labelCombo.setLayoutData(gdComboTipoFirma);
		
		
		comboTipoFirmaPDF = new Combo(this.compositeMain, SWT.NONE | SWT.READ_ONLY);
		comboTipoFirmaPDF.add("Firma PDF", 0);
		comboTipoFirmaPDF.add("Firma XML", 1);
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
		
		
		// firma visible
		checkVisible = new Button(this.compositeMain, SWT.CHECK);
		checkVisible.setText(LanguageUtil.getLanguage().getString("preferences.pdf.sign_visible"));
		GridData gdVisible = new GridData();
		gdVisible.horizontalSpan = 3;
		gdVisible.grabExcessHorizontalSpace = true;
		checkVisible.setLayoutData(gdVisible);
		checkVisible.setSelection(PreferencesUtil.getPreferences().getBoolean(PreferencesUtil.PDF_VISIBLE));
		checkVisible.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				if (checkVisible.getSelection()) {
					checkSello.setEnabled(true);
					if (checkSello.getSelection()) {
						label.setEnabled(true);
						textRuta.setEnabled(true);
						buttonBrowse.setEnabled(true);
						labelPosicion.setEnabled(true);
						buttonPosition.setEnabled(true);
						labelPages.setEnabled(true);
						comboSelectPage.setEnabled(true);
					} else {
						label.setEnabled(false);
						textRuta.setEnabled(false);
						buttonBrowse.setEnabled(false);
						labelPosicion.setEnabled(false);
						buttonPosition.setEnabled(false);
						labelPages.setEnabled(false);
						comboSelectPage.setEnabled(false);
						textSelectPage.setEnabled(false);
					}
				} else {
					checkSello.setEnabled(false);
					label.setEnabled(false);
					textRuta.setEnabled(false);
					buttonBrowse.setEnabled(false);
					labelPosicion.setEnabled(false);
					buttonPosition.setEnabled(false);
					labelPages.setEnabled(false);
					comboSelectPage.setEnabled(false);
					textSelectPage.setEnabled(false);
				}
			}
		});

		
		checkSello = new Button(this.compositeMain, SWT.CHECK);
		checkSello.setText(LanguageUtil.getLanguage().getString("preferences.pdf.stamp_active"));
		
		GridData gdSello = new GridData();
		gdSello.horizontalSpan = 3;
		gdSello.grabExcessHorizontalSpace = true;
		checkSello.setLayoutData(gdSello);
		checkSello.setSelection(PreferencesUtil.getPreferences().getBoolean(PreferencesUtil.PDF_STAMP_ENABLE));
		checkSello.setEnabled(PreferencesUtil.getPreferences().getBoolean(PreferencesUtil.PDF_VISIBLE));
		checkSello.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				if (checkVisible.getSelection() && checkSello.getSelection()) {
					label.setEnabled(true);
					textRuta.setEnabled(true);
					buttonBrowse.setEnabled(true);
					labelPosicion.setEnabled(true);
					buttonPosition.setEnabled(true);
					labelPages.setEnabled(true);
					comboSelectPage.setEnabled(true);
					if (comboSelectPage.getSelectionIndex() > 1) {
						textSelectPage.setEnabled(true);
					} else {
						textSelectPage.setEnabled(false);
					}
				} else {
					label.setEnabled(false);
					textRuta.setEnabled(false);
					buttonBrowse.setEnabled(false);
					labelPosicion.setEnabled(false);
					buttonPosition.setEnabled(false);
					labelPages.setEnabled(false);
					comboSelectPage.setEnabled(false);
					textSelectPage.setEnabled(false);
				}
			}

		});

		label = new Label(this.compositeMain, SWT.NONE);
		label.setText(LanguageUtil.getLanguage().getString("preferences.pdf.stamp.input"));

		textRuta = new Text(this.compositeMain, SWT.BORDER);
		GridData gdTextRuta = new GridData();
		gdTextRuta.horizontalAlignment = SWT.FILL;
		gdTextRuta.grabExcessHorizontalSpace = true;
		textRuta.setLayoutData(gdTextRuta);
		textRuta.setText(PreferencesUtil.getPreferences().getString(PreferencesUtil.PDF_STAMP_PATH));

		buttonBrowse = new Button(this.compositeMain, SWT.NONE);
		GridData gdBrowse = new GridData(GridData.FILL_HORIZONTAL);
		gdBrowse.horizontalSpan = 1;
		gdBrowse.grabExcessHorizontalSpace = true;
		buttonBrowse.setLayoutData(gdBrowse);
		buttonBrowse.setText(LanguageUtil.getLanguage().getString("button.browse"));
		buttonBrowse.addSelectionListener(new ButtonBrowseListener());

		
		// posicion
		labelPosicion = new Label(this.compositeMain, SWT.NONE);
		labelPosicion.setText(LanguageUtil.getLanguage().getString("preferences.pdf.image.position"));
		labelPosicion.setLayoutData(new GridData());

		buttonPosition = new Button(this.compositeMain, SWT.NONE);
		buttonPosition.setImage(new Image(this.compositeMain.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.STAMP_POSITION_IMG)));
		buttonPosition.addSelectionListener(new ButtonPositionListener());
		GridData gdPosicionBt = new GridData();
		gdPosicionBt.horizontalSpan = 2;
		gdPosicionBt.grabExcessHorizontalSpace = false;
		buttonPosition.setLayoutData(gdPosicionBt);
		

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
		
		if (PreferencesUtil.getPreferences().getString(PreferencesUtil.PDF_PAGE).equals("0")) {
			comboSelectPage.select(0);
		} else if (PreferencesUtil.getPreferences().getString(PreferencesUtil.PDF_PAGE).equals("1")) {
			comboSelectPage.select(1);
		} else {
			comboSelectPage.select(2);
			textSelectPage.setText(PreferencesUtil.getPreferences().getString(PreferencesUtil.PDF_PAGE));
			textSelectPage.setEnabled(true);
		}

		if (checkVisible.getSelection() && checkSello.getSelection()) {
			label.setEnabled(true);
			textRuta.setEnabled(true);
			buttonBrowse.setEnabled(true);
			labelPosicion.setEnabled(true);
			buttonPosition.setEnabled(true);
			labelPages.setEnabled(true);
			if (comboSelectPage.getSelectionIndex() > 1) {
				textSelectPage.setEnabled(true);
			} else {
				textSelectPage.setEnabled(false);
			}
		} else {
			label.setEnabled(false);
			textRuta.setEnabled(false);
			buttonBrowse.setEnabled(false);
			labelPosicion.setEnabled(false);
			buttonPosition.setEnabled(false);
			labelPages.setEnabled(false);
			comboSelectPage.setEnabled(false);
			textSelectPage.setEnabled(false);
		}
		
		String[][] comboFields2 = { { LanguageUtil.getLanguage().getString("preferences.pdf.combo.not.certified"), "" + PdfSignatureAppearance.NOT_CERTIFIED},
				{ LanguageUtil.getLanguage().getString("preferences.pdf.combo.no.changes.allowed"), "" + PdfSignatureAppearance.CERTIFIED_NO_CHANGES_ALLOWED},
				{ LanguageUtil.getLanguage().getString("preferences.pdf.combo.form.filling"), "" + PdfSignatureAppearance.CERTIFIED_FORM_FILLING},
				{ LanguageUtil.getLanguage().getString("preferences.pdf.combo.form.filling.and.annotations"), "" + PdfSignatureAppearance.CERTIFIED_FORM_FILLING_AND_ANNOTATIONS} };

		
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

		if (PreferencesUtil.getPreferences().getString(PreferencesUtil.PDF_CERTIFIED).equals("" + PdfSignatureAppearance.CERTIFIED_NO_CHANGES_ALLOWED)) {
			this.comboOCSP.select(1);
		} else if (PreferencesUtil.getPreferences().getString(PreferencesUtil.PDF_CERTIFIED).equals("" + PdfSignatureAppearance.CERTIFIED_FORM_FILLING)) {
			this.comboOCSP.select(2);
		} else if (PreferencesUtil.getPreferences().getString(PreferencesUtil.PDF_CERTIFIED).equals("" + PdfSignatureAppearance.CERTIFIED_FORM_FILLING_AND_ANNOTATIONS)) {
			this.comboOCSP.select(3);
		} else {
			this.comboOCSP.select(0);
		}
		
		
		// reason
		Label labelReason = new Label(this.compositeMain, SWT.NONE);
		labelReason.setText(LanguageUtil.getLanguage().getString("preferences.pdf.reason"));

		textReason = new Text(this.compositeMain, SWT.WRAP | SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gdTextReason = new GridData();
		gdTextReason.horizontalSpan = 2;
		gdTextReason.grabExcessHorizontalSpace = true;
		gdTextReason.horizontalAlignment = SWT.FILL;
		gdTextReason.grabExcessVerticalSpace = true;
		gdTextReason.verticalAlignment = SWT.FILL;
		textReason.setLayoutData(gdTextReason);
		textReason.setText(PreferencesUtil.getPreferences().getString(PreferencesUtil.PDF_REASON));

		// location
		Label labelLocation = new Label(this.compositeMain, SWT.NONE);
		labelLocation.setText(LanguageUtil.getLanguage().getString("preferences.pdf.location"));

		textLocation = new Text(this.compositeMain, SWT.WRAP | SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gdTextLocation = new GridData();
		gdTextLocation.horizontalSpan = 2;
		gdTextLocation.grabExcessHorizontalSpace = true;
		gdTextLocation.horizontalAlignment = SWT.FILL;
		gdTextLocation.grabExcessVerticalSpace = true;
		gdTextLocation.verticalAlignment = SWT.FILL;
		textLocation.setLayoutData(gdTextLocation);
		textLocation.setText(PreferencesUtil.getPreferences().getString(PreferencesUtil.PDF_LOCATION));

	}

	class ButtonBrowseListener implements SelectionListener {

		public void widgetSelected(SelectionEvent event) {

			String ruta = FileDialogs.openFileDialog(compositeMain.getShell(), new String[] { FileUtil.EXTENSION_PNG,
					FileUtil.EXTENSION_JPG, FileUtil.EXTENSION_GIF }, true);
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

			ImagePositionDialog imagePositionDialog = new ImagePositionDialog(Display.getDefault().getActiveShell(), textRuta.getText(),
					rectangle);
			rectangle = imagePositionDialog.createSShell();
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}
	}

	private void savePreferences() {

		if (checkVisible != null) {

			PreferencesUtil.getPreferences().setValue(PreferencesUtil.PDF_TIPO, String.valueOf(comboTipoFirmaPDF.getSelectionIndex()));
			PreferencesUtil.getPreferences().setValue(PreferencesUtil.PDF_VISIBLE, checkVisible.getSelection());
			PreferencesUtil.getPreferences().setValue(PreferencesUtil.PDF_REASON, textReason.getText());
			PreferencesUtil.getPreferences().setValue(PreferencesUtil.PDF_LOCATION, textLocation.getText());
			PreferencesUtil.getPreferences().setValue(PreferencesUtil.PDF_STAMP_ENABLE, checkSello.getSelection());
			PreferencesUtil.getPreferences().setValue(PreferencesUtil.PDF_STAMP_PATH, textRuta.getText());
			if (comboSelectPage.getSelectionIndex() == 0) {
				PreferencesUtil.getPreferences().setValue(PreferencesUtil.PDF_PAGE, "0");
			}
			if (comboSelectPage.getSelectionIndex() == 1) {
				PreferencesUtil.getPreferences().setValue(PreferencesUtil.PDF_PAGE, "1");
			}
			if (comboSelectPage.getSelectionIndex() == 2) {
				PreferencesUtil.getPreferences().setValue(PreferencesUtil.PDF_PAGE, textSelectPage.getText());
			}
			PreferencesUtil.getPreferences().setValue(PreferencesUtil.PDF_STAMP_X, new Float(rectangle.x * RELACION).intValue());
			PreferencesUtil.getPreferences().setValue(PreferencesUtil.PDF_STAMP_Y, new Float(rectangle.y * RELACION).intValue());
			PreferencesUtil.getPreferences().setValue(PreferencesUtil.PDF_STAMP_WIDTH, new Float(rectangle.width * RELACION).intValue());
			PreferencesUtil.getPreferences().setValue(PreferencesUtil.PDF_STAMP_HEIGHT, new Float(rectangle.height * RELACION).intValue());
			PreferencesUtil.getPreferences().setValue(PreferencesUtil.PDF_CERTIFIED, this.comboOCSP.getSelectionIndex());
		}
	}

	@Override
	protected void performApply() {

		savePreferences();

		super.performApply();
	}

	@Override
	public boolean performOk() {

		if (checkSello != null && checkVisible != null && checkSello.getSelection() && checkVisible.getSelection()) {
			try {
				if (comboSelectPage.getSelectionIndex() > 1) {
					int i = Integer.parseInt(textSelectPage.getText());
					if (i > 0) {
						savePreferences();
						return super.performOk();
					} else {
						InfoDialog id = new InfoDialog(this.getShell());
						id.open(LanguageUtil.getLanguage().getString("error.format.number.page"));
						return false;
					}
				} else {
					savePreferences();
					return super.performOk();
				}
			} catch (NumberFormatException e) {
				InfoDialog id = new InfoDialog(this.getShell());
				id.open(LanguageUtil.getLanguage().getString("error.format.number.page"));
				return false;
			}
		}
		savePreferences();
		return super.performOk();
	}
	
	private void trigerComboSelectTipoFirma(){
		if (comboTipoFirmaPDF.getSelectionIndex() == 1) {
			checkVisible.setEnabled(false);
			checkSello.setEnabled(false);
			textRuta.setEnabled(false);
			textReason.setEnabled(false);
			textLocation.setEnabled(false);
			labelPosicion.setEnabled(false);
			buttonPosition.setEnabled(false);
			buttonBrowse.setEnabled(false);
			label.setEnabled(false);
			comboSelectPage.setEnabled(false);
			labelPages.setEnabled(false);
			textSelectPage.setEnabled(false);
			comboOCSP.setEnabled(false);
			labelOCSP.setEnabled(false);
			
		} else {
			checkVisible.setEnabled(true);
			checkSello.setEnabled(true);
			textRuta.setEnabled(true);
			textReason.setEnabled(true);
			textLocation.setEnabled(true);
			labelPosicion.setEnabled(true);
			buttonPosition.setEnabled(true);
			buttonBrowse.setEnabled(true);
			label.setEnabled(true);
			comboSelectPage.setEnabled(true);
			labelPages.setEnabled(true);
			comboOCSP.setEnabled(true);
			labelOCSP.setEnabled(true);
		}		
	}
}

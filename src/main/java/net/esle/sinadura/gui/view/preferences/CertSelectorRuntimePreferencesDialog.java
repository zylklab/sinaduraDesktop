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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import net.esle.sinadura.gui.events.BotonCancelarListener;
import net.esle.sinadura.gui.exceptions.DriversNotFoundException;
import net.esle.sinadura.gui.util.HardwareItem;
import net.esle.sinadura.gui.util.ImagesUtil;
import net.esle.sinadura.gui.util.LanguageUtil;
import net.esle.sinadura.gui.util.PreferencesUtil;

/**
 * @author zylk.net
 */
public class CertSelectorRuntimePreferencesDialog extends Dialog {

	
	private Shell sShell = null;
	private Composite ButtonsComposite = null;
	private Composite compositeMain = null;
	private Combo comboCertType = null;
	private Label labelCert = null;
	private Combo comboCertPath = null;
	private Button buttonP12 = null;
	private Button bottonAceptar = null;
	private Button bottonCancelar = null;

	private Map<String, String> tmpP12Map = null;
	private String tempP12Default = null;
	private Map<String, HardwareItem> pkcs11Map = null;
	private List<HardwareItem> pkcs11List = new ArrayList<HardwareItem>();
	private String pkcs11Default = null;
	
	// seleccion
	private String selectedCertType = null;
	private String selectedCertPath = null;
	
	
	public CertSelectorRuntimePreferencesDialog(Shell parent) {
		
		super(parent);
		
		// p12
		tmpP12Map = PreferencesUtil.getSoftwarePreferences();
		tempP12Default = PreferencesUtil.getString(PreferencesUtil.SOFTWARE_DISPOSITIVE);
		if (tempP12Default == null) {
			tempP12Default = "";
		}
		// pkcs11
		pkcs11Map = PreferencesUtil.getHardwarePreferences();
		for (HardwareItem hardwareItem : pkcs11Map.values()) { // es un treeMap
			pkcs11List.add(hardwareItem);
		}
		try {
			pkcs11Default = PreferencesUtil.getDefaultHardware().getName();
		} catch (DriversNotFoundException e) {
			pkcs11Default = "";
		}
		
	}

	
	public int open() {

		Shell parent = getParentShell();

		this.sShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
		this.sShell.setImage(new Image(sShell.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.SINADURA_LOGO_IMG)));
		this.sShell.setText("Selección del tipo de certificado");
		

		GridLayout shellGridLayout = new GridLayout();
		shellGridLayout.numColumns = 1;
		shellGridLayout.marginTop = 10;
		this.sShell.setLayout(shellGridLayout);
		
		Composite profileComposite = new Composite(this.sShell, SWT.NONE);
		
		this.compositeMain = profileComposite;
		
		
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

		GridData gd = new GridData();
		
		// label description
		Label label = new Label(this.compositeMain, SWT.NONE);
		label.setText("Seleccione el tipo de certificado con el que desea firmar:");
		
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalSpan = 3;
		label.setLayoutData(gd);
		

		// 1- combo select cert
		Label labelPages = new Label(this.compositeMain, SWT.NONE);
		labelPages.setText("Tipo de certificado:");

		comboCertType = new Combo(this.compositeMain, SWT.NONE | SWT.READ_ONLY);
		comboCertType.add("Certificado software (pkx, p12...)", 0);
		comboCertType.add("Tarjeta criptografica (Pkcs11, Opensc...)", 1);
		comboCertType.add("Almacen de certificados de Windows (CAPI)", 2);
		
		comboCertType.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				
				changeComboCertType();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				widgetSelected(arg0);
			}
		});
		
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalSpan = 1;
		comboCertType.setLayoutData(gd);

		Label label10 = new Label(this.compositeMain, SWT.NONE);

		// TODO set default
		comboCertType.select(0);
		
		
		// 2- combo select p12 o pkcs11
		labelCert = new Label(this.compositeMain, SWT.NONE);
		labelCert.setText("Certificado:");

		comboCertPath = new Combo(this.compositeMain, SWT.NONE | SWT.READ_ONLY);
		
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalSpan = 1;
		comboCertPath.setLayoutData(gd);

		// button add p12
		buttonP12 = new Button(this.compositeMain, SWT.NONE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		gd.grabExcessHorizontalSpace = false;
		buttonP12.setLayoutData(gd);
		buttonP12.setText("Añadir");
		buttonP12.setImage(new Image(this.compositeMain.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.ADD_IMG)));
		buttonP12.addSelectionListener(new ButtonAddP12Listener());

		this.ButtonsComposite = new Composite(this.sShell, SWT.NONE);
		this.ButtonsComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		GridLayout compositeGridLayout = new GridLayout();
		compositeGridLayout.numColumns = 2;
		compositeGridLayout.horizontalSpacing = 50;
		this.ButtonsComposite.setLayout(compositeGridLayout);

		this.bottonAceptar = new Button(this.ButtonsComposite, SWT.NONE);
		this.bottonAceptar.setText(LanguageUtil.getLanguage().getString("button.accept"));
		this.bottonAceptar.setImage(new Image(this.sShell.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.ACEPTAR_IMG)));
		this.bottonAceptar.addSelectionListener(new BotonAceptarListener());

		this.bottonCancelar = new Button(this.ButtonsComposite, SWT.NONE);
		this.bottonCancelar.setText(LanguageUtil.getLanguage().getString("button.cancel"));
		this.bottonCancelar.setImage(new Image(this.sShell.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.CANCEL_IMG)));
		this.bottonCancelar.addSelectionListener(new BotonCancelarListener());

		reloadComboCert();
		
		this.sShell.pack();

		Monitor primary = this.sShell.getDisplay().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = this.sShell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 3;
		this.sShell.setLocation(x, y);

		this.sShell.open();

		Display display = parent.getDisplay();
		while (!this.sShell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		return 0;
	}

	
	private void changeComboCertType() {
		
		 reloadComboCert();
	}
	
	
	private void reloadComboCert() {
			
		if (comboCertType.getSelectionIndex() == 0) { // p12
		
			comboCertPath.removeAll();
			
			// load
			int i = 0;
			for (String key : tmpP12Map.keySet()) {
				comboCertPath.add(key, i);
				i++;
			}
			
			// default
			comboCertPath.setText(tempP12Default); // TODO validar null o ""??

			// visible
			labelCert.setVisible(true);
			comboCertPath.setVisible(true);
			buttonP12.setVisible(true);

			// enable aceptar
			if (comboCertPath.getText() != null && !comboCertPath.getText().equals("")) {
				bottonAceptar.setEnabled(true);
			} else {
				bottonAceptar.setEnabled(false);
			}
			
			
		} else if (comboCertType.getSelectionIndex() == 1) { // pkcs11
			
			comboCertPath.removeAll();
			
			// load
			int i = 0;
			for (HardwareItem hi : pkcs11Map.values()) {
				comboCertPath.add(hi.getName(), i);
				i++;
			}
			
			// default			
			comboCertPath.setText(pkcs11Default);
			
			// visible
			labelCert.setVisible(true);
			comboCertPath.setVisible(true);
			buttonP12.setVisible(false);

			// enable aceptar
			if (comboCertPath.getText() != null && !comboCertPath.getText().equals("")) {
				bottonAceptar.setEnabled(true);
			} else {
				bottonAceptar.setEnabled(false);
			}
			
		} else if (comboCertType.getSelectionIndex() == 2) { // capi
			
			comboCertPath.removeAll();

			// visible
			labelCert.setVisible(false);
			comboCertPath.setVisible(false);
			buttonP12.setVisible(false);
			
			// enable aceptar
			bottonAceptar.setEnabled(true);
		}

	}
	
	public String getCertType() {
		return this.selectedCertType;
	}
	
	public String getCertPath() {
		return this.selectedCertPath;
	}
	
	class ButtonAddP12Listener implements SelectionListener {
		
		public void widgetSelected(SelectionEvent event) {

			SoftwareCertUpdateDialog softwareStoreDialog = new SoftwareCertUpdateDialog(compositeMain.getShell(), tmpP12Map);
			softwareStoreDialog.open();
			if (softwareStoreDialog.getSelectedName() != null) {
				// se marca el nuevo p12 como valor por defecto del combo (solo de forma temporal para esta pantalla)
				tempP12Default = softwareStoreDialog.getSelectedName();
				reloadComboCert();
			}
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}
	}

	private class BotonAceptarListener implements SelectionListener {

		public void widgetSelected(SelectionEvent event) {
			
			selectedCertType = String.valueOf(comboCertType.getSelectionIndex());
			
			if (comboCertType.getSelectionIndex() == 0) { // p12
				
				// los nuevos p12 se persisten (el selected p12 no)
				PreferencesUtil.saveSoftwarePreferences(tmpP12Map);
				
				selectedCertPath = tmpP12Map.get(comboCertPath.getText());

			} else if (comboCertType.getSelectionIndex() == 1) { // pkcs11
	
				HardwareItem hardwareItem = pkcs11List.get(comboCertPath.getSelectionIndex()); // es un TreeMap
				selectedCertPath = hardwareItem.getPath();	
			}
			
			sShell.dispose();
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}
	}
}


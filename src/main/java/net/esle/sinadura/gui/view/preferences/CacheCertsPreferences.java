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


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import net.esle.sinadura.core.certificate.CertificateUtil;
import net.esle.sinadura.core.util.FileUtil;
import net.esle.sinadura.core.util.KeystoreUtil;
import net.esle.sinadura.gui.util.ImagesUtil;
import net.esle.sinadura.gui.util.LanguageUtil;
import net.esle.sinadura.gui.util.PreferencesUtil;
import net.esle.sinadura.gui.util.StatisticsUtil;
import net.esle.sinadura.gui.view.main.FileDialogs;
import net.esle.sinadura.gui.view.main.InfoDialog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.X509Principal;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;


/**
 * @author zylk.net
 */
public class CacheCertsPreferences extends FieldEditorPreferencePage {

	private static Log log = LogFactory.getLog(CacheCertsPreferences.class);

	private Composite compositeMain = null;
	
	private org.eclipse.swt.widgets.List visualList = null;
	private List<String> aliasesPosition = null;
	
	private KeyStore  ksTemp = null;
	

	public CacheCertsPreferences() {
		
		// Use the "flat" layout
		super(FLAT);
		
		// inicializo la lista
		aliasesPosition = new ArrayList<String>();
		
		// hago una copia del keystore
		
		
		KeyStore kss = PreferencesUtil.getCacheKeystorePreferences();
		try {
			ksTemp = KeystoreUtil.copyKeystore(kss);
		} catch (KeyStoreException e) {
			log.error("", e);
		} catch (NoSuchAlgorithmException e) {
			log.error("", e);
		} catch (CertificateException e) {
			log.error("", e);
		} catch (IOException e) {
			log.error("", e);
		}
		
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
		gridLayoutPrincipal.verticalSpacing = 10;
		gridLayoutPrincipal.marginBottom = 50;
		this.compositeMain.setLayout(gridLayoutPrincipal);
		
		GridData gdPrincipal = new GridData();
		gdPrincipal.horizontalAlignment = GridData.FILL;
		gdPrincipal.verticalAlignment = GridData.FILL;
		gdPrincipal.grabExcessHorizontalSpace = true;
		gdPrincipal.grabExcessVerticalSpace = true;
		this.compositeMain.setLayoutData(gdPrincipal);

		// composite con la lista y los 3 botones
		createListArea();
		

		return this.compositeMain;
	}
	
	private void createListArea() {
		
		Composite compositeList = new Composite(this.compositeMain, SWT.NONE);
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
		
		Composite compositeButtons = new Composite(this.compositeMain, SWT.NONE);
		GridData gdButtonsComposite = new GridData(GridData.FILL_BOTH);
		gdButtonsComposite.grabExcessHorizontalSpace = false;
		gdButtonsComposite.grabExcessVerticalSpace = false;
		compositeButtons.setLayoutData(gdButtonsComposite);
		compositeButtons.setLayout(new GridLayout());

		Button buttonAdd = new Button(compositeButtons, SWT.NONE);
		GridData gdAdd = new GridData();
		gdAdd.horizontalAlignment = GridData.FILL;
		buttonAdd.setLayoutData(gdAdd);
		buttonAdd.setText(LanguageUtil.getLanguage().getString("button.add"));
		buttonAdd.setImage(new Image(this.compositeMain.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.ADD_IMG)));
		buttonAdd.addSelectionListener(new ButtonAddListener());

		Button buttonShow = new Button(compositeButtons, SWT.NONE);
		GridData gdMod = new GridData();
		gdMod.horizontalAlignment = GridData.FILL;
		buttonShow.setLayoutData(gdMod);
		buttonShow.setText(LanguageUtil.getLanguage().getString("button.show"));
		buttonShow.setImage(new Image(this.compositeMain.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.EDIT_IMG)));
		buttonShow.addSelectionListener(new ButtonShowListener());

		Button buttonExport = new Button(compositeButtons, SWT.NONE);
		GridData gdExport = new GridData();
		gdExport.horizontalAlignment = GridData.FILL;
		buttonExport.setLayoutData(gdMod);
		buttonExport.setText(LanguageUtil.getLanguage().getString("button.export"));
		buttonExport.setImage(new Image(this.compositeMain.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.EXPORT_IMG)));
		buttonExport.addSelectionListener(new ButtonExportListener());
		
		Button buttonRemove = new Button(compositeButtons, SWT.NONE);
		GridData gdRemove = new GridData();
		gdRemove.horizontalAlignment = GridData.FILL;
		gdRemove.verticalAlignment = GridData.BEGINNING;
		buttonRemove.setLayoutData(gdRemove);
		buttonRemove.setText(LanguageUtil.getLanguage().getString("button.remove"));
		buttonRemove.setImage(new Image(this.compositeMain.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.REMOVE_IMG)));
		buttonRemove.addSelectionListener(new ButtonRemoveListener());

	}
	
	private void reloadVisualList() {
		
		// inicializar lista
		visualList.removeAll();
		aliasesPosition = new ArrayList<String>(); 
			
		Enumeration<String> aliases = null;;
		
		try {
			aliases = ksTemp.aliases();
		
			while (aliases.hasMoreElements()) {
				
				String alias = aliases.nextElement(); 
	
				X509Certificate cert = (X509Certificate)ksTemp.getCertificate(alias);
				aliasesPosition.add(alias);
				
				String s = CertificateUtil.getFormattedName(cert);
				visualList.add(s);
				
			}
		
		} catch (KeyStoreException e) {
			
			log.error("", e);
		}
	}

	private void removeTableFile() {

		try {
			int [] indices = visualList.getSelectionIndices();
			for (int i : indices) {
			
				ksTemp.deleteEntry(aliasesPosition.get(i));
			}
		} catch (KeyStoreException e) {
			log.error("", e);
		}
		
		reloadVisualList();
	}

	
	private class ButtonAddListener implements SelectionListener {
		
		public void widgetSelected(SelectionEvent event) {

			String path = FileDialogs.openFileDialog(compositeMain.getShell(), new String[] { FileUtil.EXTENSION_CER,
				FileUtil.EXTENSION_CRT}, true);
			
			if (path != null) {
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(path);
					CertificateFactory cacertf = CertificateFactory.getInstance("X.509");
					X509Certificate cert = (X509Certificate) cacertf.generateCertificate(fis);
					
					// Subject name
					X509Principal principal = null;
					try {
						principal = new X509Principal(cert.getSubjectX500Principal().getEncoded());
					} catch (IOException e) {
						log.error("", e);
					}
				
					Vector<String> vec = principal.getValues();
					String s = "";
					for (String value : vec) {
						s += value + " - ";
					}
					
						
					ksTemp.setCertificateEntry(CertificateUtil.getUniqueID(cert), cert);
					StatisticsUtil.log(StatisticsUtil.KEY_ADD_CACHECERT, cert.getSubjectX500Principal().getName());
					log.info("added new cache cert: " + cert.getSubjectX500Principal().getName());
					
					reloadVisualList();
					
					
				} catch (FileNotFoundException e) {
					log.error("", e);
					
				} catch (CertificateException e) {
					log.error("", e);
					// muestro los datos en pantalla
					InfoDialog id = new InfoDialog(compositeMain.getShell());
					id.open(LanguageUtil.getLanguage().getString("error.importing.certificate"));
					
				} catch (KeyStoreException e) {
					log.error("", e);
				}
				
			}
			
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}
	}
	
	private class ButtonShowListener implements SelectionListener {
		
		public void widgetSelected(SelectionEvent event) {

			try {
				int [] indices = visualList.getSelectionIndices();
				
				for (int i : indices) {
				
					// obtengo el certificado
					X509Certificate cert = (X509Certificate)ksTemp.getCertificate(aliasesPosition.get(i));
					
					// inicializo el mensaje a mostrar
					String s = "";
					
					// Subject name
					s += LanguageUtil.getLanguage().getString("preferences.trusted.show.subject") +  "\n";
					s += cert.getSubjectX500Principal().getName();
					s += "\n\n";

					// validity
					s += LanguageUtil.getLanguage().getString("preferences.trusted.show.valid") + "\n";
					SimpleDateFormat dateFormat = LanguageUtil.getFullFormater();
					s += LanguageUtil.getLanguage().getString("preferences.trusted.show.valid.from") + " "
							+ dateFormat.format(cert.getNotBefore()) + "\n";
					s += LanguageUtil.getLanguage().getString("preferences.trusted.show.valid.until") + " "
							+ dateFormat.format(cert.getNotAfter()) + "\n\n";
					
					s += LanguageUtil.getLanguage().getString("preferences.trusted.show.uses") +  "\n";
					s += CertificateUtil.getKeyUsage(cert);
					s += "\n\n";

					// Issuer name
					s += LanguageUtil.getLanguage().getString("preferences.trusted.show.issuer") +  "\n";
					s += cert.getIssuerX500Principal().getName();
					s += "\n";
											
					// muestro los datos en pantalla
					InfoDialog id = new InfoDialog(compositeMain.getShell());
					id.open(s);
				}
				
			} catch (KeyStoreException e) {
				log.error("", e);
			}
			
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}
	}
	
	
	private class ButtonExportListener implements SelectionListener {

		public void widgetSelected(SelectionEvent event) {

			try {
				X509Certificate cert = (X509Certificate)ksTemp.getCertificate(aliasesPosition.get(visualList.getSelectionIndex()));

				FileDialog fileDialog = new FileDialog(compositeMain.getShell(), SWT.SAVE);
				fileDialog.setFileName(CertificateUtil.getFormattedName(cert).trim()+".crt");
				fileDialog.setFilterExtensions(new String[] {"crt" });
				String filePath = fileDialog.open();
				
				if (filePath != null){
					FileUtil.export(cert, new File(filePath), false);					
				}

			} catch (Exception e) {
				e.printStackTrace();
				log.error("", e);
			}
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}
	}

	
	class ButtonRemoveListener implements SelectionListener {
		
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
	
	private void savePreferences() {
	
		PreferencesUtil.setCacheKeystorePreferences(ksTemp);
	}
	
	@Override
	protected void performApply() {
		
		savePreferences();
		super.performApply();
	}
	
	@Override
	public boolean performOk() {
		
		savePreferences();
		return super.performOk();
	}

}




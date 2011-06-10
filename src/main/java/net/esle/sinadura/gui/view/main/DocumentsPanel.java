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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Logger;

import net.esle.sinadura.gui.Sinadura;
import net.esle.sinadura.gui.events.AddDirectoryListener;
import net.esle.sinadura.gui.events.AddDocumentListener;
import net.esle.sinadura.gui.events.ButtonRemoveListener;
import net.esle.sinadura.gui.events.SendPDFListener;
import net.esle.sinadura.gui.events.SignListener;
import net.esle.sinadura.gui.events.ValidatePDFListener;
import net.esle.sinadura.gui.events.ViewPDFListener;
import net.esle.sinadura.gui.util.ImagesUtil;
import net.esle.sinadura.gui.util.LanguageUtil;
import net.esle.sinadura.gui.util.PropertiesUtil;
import net.esle.sinadura.gui.util.ValidatorUtil;

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
import org.eclipse.swt.widgets.Label;


public class DocumentsPanel extends Composite {

	private static Log log = LogFactory.getLog(DocumentsPanel.class);
	
	
	private Button				buttonAddPDF			= null;
	private Button				buttonAddDir				= null;
	private Button				buttonSend				= null;
	private Button				buttonRemove				= null;
	private Button				buttonView				= null;
	
	private Button				buttonSign				= null;
	private Button				buttonValidate				= null;
	
	private Combo				combo					= null;
	
	private DocumentsTable 			tablePDF  = null;
	
	
	public DocumentsPanel(Composite parent, int style) {
		
		super(parent, style);
		initialize();
	}

	private void initialize() {
		
		GridData gdPanel = new GridData();
		gdPanel.horizontalAlignment = GridData.FILL;
		gdPanel.verticalAlignment = GridData.FILL;
		gdPanel.grabExcessHorizontalSpace = true;
		gdPanel.grabExcessVerticalSpace = true;
		this.setLayoutData(gdPanel);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		this.setLayout(gridLayout);
		
		tablePDF = new DocumentsTable (this, SWT.NONE);
		GridData gdTable = new GridData();
		gdTable.horizontalAlignment = GridData.FILL;
		gdTable.verticalAlignment = GridData.FILL;
		gdTable.grabExcessHorizontalSpace = true;
		gdTable.grabExcessVerticalSpace = true;
		tablePDF.setLayoutData(gdTable);
		
		
		// Composite buttons
		Composite buttonsComposite = new Composite (this, SWT.NONE);
		GridData gdButtons = new GridData();
		gdButtons.horizontalAlignment = GridData.FILL;
		gdButtons.verticalAlignment = GridData.FILL;
		buttonsComposite.setLayoutData(gdButtons);
		
		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.numColumns = 1;
		buttonsComposite.setLayout(gridLayout2);
		
		
		this.buttonAddPDF = new Button(buttonsComposite, SWT.NONE);
		this.buttonAddPDF.setText(ValidatorUtil.formatedTextButton(LanguageUtil.getLanguage().getString(
				"section.sign.button.add.pdf")));
		this.buttonAddPDF.setImage(new Image(this.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.FILE_IMG)));
		GridData gdAddFile = new GridData();
		gdAddFile.horizontalAlignment = GridData.FILL;
		this.buttonAddPDF.setLayoutData(gdAddFile);

		this.buttonAddDir = new Button(buttonsComposite, SWT.NONE);
		this.buttonAddDir.setText(ValidatorUtil.formatedTextButton(LanguageUtil.getLanguage().getString(
				"section.sign.button.add.dir")));
		this.buttonAddDir.setImage(new Image(this.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.ADD_DIR_IMG)));
		GridData gdUpdate = new GridData();
		gdUpdate.horizontalAlignment = GridData.FILL;
		this.buttonAddDir.setLayoutData(gdUpdate);

		this.buttonRemove = new Button(buttonsComposite, SWT.NONE);
		this.buttonRemove.setText(ValidatorUtil.formatedTextButton(LanguageUtil.getLanguage().getString(
				"section.sign.button.remove")));
		this.buttonRemove.setImage(new Image(this.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.DELETE_FILE_IMG)));
		GridData gdDelete = new GridData();
		gdDelete.horizontalAlignment = GridData.FILL;
		this.buttonRemove.setLayoutData(gdDelete);
		
		this.buttonView = new Button(buttonsComposite, SWT.NONE);
		this.buttonView.setText(ValidatorUtil.formatedTextButton(LanguageUtil.getLanguage().getString(
				"section.sign.button.view")));
		this.buttonView.setImage(new Image(this.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.VIEW_IMG)));
		GridData gdView = new GridData();
		gdView.horizontalAlignment = GridData.FILL;
		this.buttonView.setLayoutData(gdView);
		
		if (Boolean.parseBoolean(PropertiesUtil.getConfiguration().getProperty(PropertiesUtil.ENABLE_SEND_BUTTON))) {
			this.buttonSend = new Button(buttonsComposite, SWT.NONE);
			this.buttonSend.setText(ValidatorUtil.formatedTextButton(LanguageUtil.getLanguage().getString(
					"section.sign.button.send")));
			this.buttonSend.setImage(new Image(this.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.SEND_FILE_IMG)));
			GridData gdSend = new GridData();
			gdSend.horizontalAlignment = GridData.FILL;
			this.buttonSend.setLayoutData(gdSend);
		}
		
		this.buttonSign = new Button(buttonsComposite, SWT.NONE);
		this.buttonSign.setText(ValidatorUtil.formatedTextButton(LanguageUtil.getLanguage().getString(
				"section.sign.button.sign")));
		this.buttonSign.setImage(new Image(this.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.SIGN_FILE_IMG)));
		GridData gdExportar = new GridData();
		gdExportar.horizontalAlignment = GridData.FILL;
		this.buttonSign.setLayoutData(gdExportar);
		
		this.buttonValidate = new Button(buttonsComposite, SWT.NONE);
		this.buttonValidate.setText(ValidatorUtil.formatedTextButton(LanguageUtil.getLanguage().getString(
				"section.sign.button.validate")));
		this.buttonValidate.setImage(new Image(this.getDisplay(), ClassLoader
				.getSystemResourceAsStream(ImagesUtil.VALIDATE_FILE_IMG)));
		GridData gdValidate = new GridData();
		gdValidate.horizontalAlignment = GridData.FILL;
		this.buttonValidate.setLayoutData(gdValidate);
		
//		this.combo = new Combo(buttonsComposite, SWT.READ_ONLY);
//		GridData gdCombo = new GridData();
//		gdCombo.horizontalAlignment = GridData.FILL;
//		this.combo.setLayoutData(gdCombo);
//		this.combo.setText("Noticias");
		
//		this.combo = new ComboFieldEditor("Noticias", LanguageResource.getLanguage().getString(
//		"preferences.validate.certified"), comboFields2, getFieldEditorParent());
		
//		//TODO: añadir el lector de RSS y las dos últimas feeds
		
		
//		URL feedUrl;
//		List<String> links = new ArrayList<String>();
//		try {	
//			feedUrl = new URL("http://www.sinadura.net/noticias/-/journal/rss/18043/45610");
//			InetAddress in;
//			SyndFeedInput input = new SyndFeedInput();
//	        SyndFeed feed = input.build(new XmlReader(feedUrl));
//	        List<SyndEntry> list = feed.getEntries();
//	       
//	        int count = 0;
//	        if(list.size() > 2)
//	        {
//	        	count = 2;
//	        }
//	        else
//	        {
//	        	count = list.size();
//	        }
//	        GridData gd = null;
//	        for (int i = 0; i < count; i++) {
//	    		String text = list.get(i).getTitle();
//	    		this.combo.add(text);
//	    		String link =list.get(i).getLink();
//	    		links.add(link);
//	    		LoggingDesktopController.printInfo(text+" en: "+link);
//	    		
//			}
//	    }catch (UnknownHostException e){
//			e.printStackTrace();
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		} catch (FeedException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
				
		Label labelEsle = new Label(buttonsComposite, SWT.NONE);
		labelEsle.setImage(new Image(this.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.SPONSOR_LOGO_IMG)));
		
		GridData gdLabel = new GridData();
		gdLabel.horizontalAlignment = GridData.CENTER;
		gdLabel.verticalAlignment = GridData.FILL;
		gdLabel.grabExcessVerticalSpace = true;
		labelEsle.setLayoutData(gdLabel);
				
//		this.tablePDF.addSelectionListener(new ButtonControlerListener());
		
		this.buttonAddPDF.addSelectionListener(new AddDocumentListener(tablePDF));
		this.buttonAddDir.addSelectionListener(new AddDirectoryListener(tablePDF));
		this.buttonRemove.addSelectionListener(new ButtonRemoveListener(tablePDF));
		this.buttonView.addSelectionListener(new ViewPDFListener(tablePDF));
		if (Boolean.parseBoolean(PropertiesUtil.getConfiguration().getProperty(PropertiesUtil.ENABLE_SEND_BUTTON))) {
			this.buttonSend.addSelectionListener(new SendPDFListener(tablePDF));
		}
		this.buttonSign.addSelectionListener(new SignListener(tablePDF));
		this.buttonValidate.addSelectionListener(new ValidatePDFListener(tablePDF));
//		this.combo.addSelectionListener(new ComboListener(this.combo, links));
		
//		composite.addListener(SWT.KeyUp, new WindowKeyListener());
//		this.table.addListener(SWT.KeyUp, new WindowKeyListener());
//		this.botonAddArchivo.addListener(SWT.KeyUp, new WindowKeyListener());
//		this.botonAddDirectorio.addListener(SWT.KeyUp, new WindowKeyListener());
//		this.botonModificar.addListener(SWT.KeyUp, new WindowKeyListener());
//		this.botonEliminar.addListener(SWT.KeyUp, new WindowKeyListener());
	}	
	
	public DocumentsTable getTablePDF() {
		return tablePDF;
	}

	private void changeButtonEnabled() {
		
		if (tablePDF.getSelectedDocuments().size() == 0) {
			
			buttonAddPDF.setEnabled(true);
			buttonAddDir.setEnabled(true);
			buttonRemove.setEnabled(false);
			buttonSign.setEnabled(false);
			buttonValidate.setEnabled(false);
			
		} else if (tablePDF.getSelectedDocuments().size() == 1) {
			
			buttonAddPDF.setEnabled(true);
			buttonAddDir.setEnabled(true);
			buttonRemove.setEnabled(true);
			buttonSign.setEnabled(true);
			buttonValidate.setEnabled(true);
			
		} else {
			
			buttonAddPDF.setEnabled(true);
			buttonAddDir.setEnabled(true);
			buttonRemove.setEnabled(true);
			buttonSign.setEnabled(true);
			buttonValidate.setEnabled(true);
		}
	}
	
	
	class ButtonControlerListener implements SelectionListener {

		public void widgetSelected(SelectionEvent event) {
			
			changeButtonEnabled();

		}

		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}
	}
	
//	class ComboListener implements SelectionListener{
//		
//		private Combo combo = null;
//		private List<String> links;
//		public ComboListener(Combo c, List<String> l){
//			this.combo = c;
//			this.links = l;
//		}
//		
//		public void widgetSelected(SelectionEvent event) {
//			DesktopHelper.openDefaultBrowser(this.links.get(this.combo.getSelectionIndex()));
//			this.combo.deselectAll();
//		}
//
//		public void widgetDefaultSelected(SelectionEvent event) {
//			widgetSelected(event);
//		}
//	}
//	
	
//	// Listener para las hotkeys generales
//	class WindowKeyListener implements Listener {
//
//		public void handleEvent(Event e) {
//
//			if (SWT.F1 == e.keyCode) {
//				WebBrowser.openDefaultBrowser(PropertiesUtil.getConfiguration().getProperty(PropertiesUtil.DOCUMENTATION_URI));
//			}
//			if (SWT.F5 == e.keyCode) {
//				firmarArchivos();
//			}
//			if (((e.stateMask & SWT.CTRL) != 0) && (e.keyCode == 'o')) {
//				abrirArchivo();
//			}
//			if (((e.stateMask & SWT.CTRL) != 0) && (e.keyCode == 'd')) {
//				abrirDirectorio();
//			}
//			if (SWT.F2 == e.keyCode) {
//				// hotkey para pruebas
//
//			}
//		}
//	}

}
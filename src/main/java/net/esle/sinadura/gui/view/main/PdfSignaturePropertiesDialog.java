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
import net.esle.sinadura.gui.util.ImagesUtil;
import net.esle.sinadura.gui.util.LanguageUtil;
import net.esle.sinadura.gui.util.PdfProfile;
import net.esle.sinadura.gui.util.PreferencesUtil;

/**
 * @author zylk.net
 */
public class PdfSignaturePropertiesDialog extends Dialog {

	private Map<String, PdfProfile> availableProfiles = null; 
	private PdfProfile pdfProfile = null;
	private PdfProfile selectedProfile = null;
	
	private Shell sShell = null;

	private Combo comboProfile = null; 
	
	private PdfSignatureProperties profilePanel;
	
	private Button bottonAceptar = null;
	private Button bottonCancelar = null;

	
	public PdfSignaturePropertiesDialog(Shell parent) {
		
		super(parent);
		
		availableProfiles = PreferencesUtil.getPdfProfiles();
		pdfProfile = availableProfiles.get(PreferencesUtil.getString(PreferencesUtil.PDF_PROFILE_SELECTED_NAME));
	}

	
	public int open() {

		Shell parent = getParentShell();

		this.sShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
		this.sShell.setImage(new Image(sShell.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.SINADURA_LOGO_IMG)));
		this.sShell.setText(LanguageUtil.getLanguage().getString("pdf.properties.dialog.window.title"));

		GridLayout shellGridLayout = new GridLayout();
		shellGridLayout.numColumns = 1;
		shellGridLayout.marginTop = 10;
		this.sShell.setLayout(shellGridLayout);
		
		// composite intro
		Composite headerComposite = new Composite(this.sShell, SWT.NONE);
		headerComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.horizontalSpacing = 20;
		headerComposite.setLayout(gridLayout);

		// label
		Label headerLabel = new Label(headerComposite, SWT.NONE);
		headerLabel.setText(LanguageUtil.getLanguage().getString("pdf.properties.dialog.header"));
		
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 2;
		headerLabel.setLayoutData(gridData);
		
		// combo
		Label labelCert = new Label(headerComposite, SWT.NONE);
		labelCert.setText(LanguageUtil.getLanguage().getString("pdf.properties.dialog.profile"));
		comboProfile = new Combo(headerComposite, SWT.NONE | SWT.READ_ONLY);
		
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 1;
		comboProfile.setLayoutData(gridData);
		
		// combo load
		int i = 0;
		for (String profileName : availableProfiles.keySet()) {
			
			comboProfile.add(profileName, i);
			i++;
		}
		// combo default
		if (pdfProfile != null) {
			comboProfile.setText(pdfProfile.getName());
		}
		
		comboProfile.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				
				changeComboProfile();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				widgetSelected(arg0);
			}
		});

	    // separator
	    Label separator = new Label(headerComposite, SWT.HORIZONTAL | SWT.SEPARATOR);
	    separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 2;
		separator.setLayoutData(gridData);
	    
		
		// profile composite 
		Composite profileComposite = new Composite(this.sShell, SWT.NONE);
		profilePanel = new PdfSignatureProperties(profileComposite, pdfProfile);

		// buttons composite
		Composite buttonsComposite = new Composite(this.sShell, SWT.NONE);
		buttonsComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.horizontalSpacing = 50;
		buttonsComposite.setLayout(gridLayout);

		this.bottonAceptar = new Button(buttonsComposite, SWT.NONE);
		this.bottonAceptar.setText(LanguageUtil.getLanguage().getString("button.accept"));
		this.bottonAceptar.setImage(new Image(this.sShell.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.ACEPTAR_IMG)));
		this.bottonAceptar.addSelectionListener(new BotonAceptarListener());

		this.bottonCancelar = new Button(buttonsComposite, SWT.NONE);
		this.bottonCancelar.setText(LanguageUtil.getLanguage().getString("button.cancel"));
		this.bottonCancelar.setImage(new Image(this.sShell.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.CANCEL_IMG)));
		this.bottonCancelar.addSelectionListener(new BotonCancelarListener());

//		this.sShell.pack();
		this.sShell.setSize(600, 600);

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
	
	private void changeComboProfile() {
		
		String newProfileName = comboProfile.getText();
		PdfProfile newProfile = availableProfiles.get(newProfileName);
		profilePanel.reloadProfile(newProfile);
	} 
	
	
	public PdfProfile getPdfProfile() {
		
		return selectedProfile;
	}

	private class BotonAceptarListener implements SelectionListener {

		public void widgetSelected(SelectionEvent event) {
			
			selectedProfile = profilePanel.getProfile();
			sShell.dispose();
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}
	}
}


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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import net.esle.sinadura.gui.events.BotonCancelarListener;
import net.esle.sinadura.gui.util.ImagesUtil;
import net.esle.sinadura.gui.util.LanguageUtil;
import net.esle.sinadura.gui.util.PdfProfile;

/**
 * @author zylk.net
 */
public class PdfSignaturePropertiesDialog extends Dialog {

	private PdfProfile tmpProfile = null;
	private PdfProfile selectedProfile = null;
	
	private Shell sShell = null;

	private PdfSignatureProperties profilePanel;
	private Composite ButtonsComposite = null;
	
	private Button bottonAceptar = null;
	private Button bottonCancelar = null;

	
	public PdfSignaturePropertiesDialog(Shell parent, PdfProfile profile) {
		
		super(parent);
		
		tmpProfile = profile;
	}

	
	public int open() {

		Shell parent = getParentShell();

		this.sShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
		this.sShell.setImage(new Image(sShell.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.SINADURA_LOGO_IMG)));
		this.sShell.setText(LanguageUtil.getLanguage().getString("preferences.pdf.profile.window.title"));
		

		GridLayout shellGridLayout = new GridLayout();
		shellGridLayout.numColumns = 1;
		shellGridLayout.marginTop = 10;
		this.sShell.setLayout(shellGridLayout);
		
		Composite profileComposite = new Composite(this.sShell, SWT.NONE);
		profilePanel = new PdfSignatureProperties(profileComposite, tmpProfile);

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


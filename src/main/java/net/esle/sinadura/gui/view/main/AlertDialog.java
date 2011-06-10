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


import net.esle.sinadura.gui.util.ImagesUtil;
import net.esle.sinadura.gui.util.LanguageUtil;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

/**
 * @author zylk.net
 */
public class AlertDialog extends Dialog {

	private Shell	sShell			= null;
	private Button	bottonAceptar	= null;
	private Button	bottonCancelar	= null;
	private Label	textoLabel		= null;
	private boolean	result			= false;

	/**
	 * @param parent
	 */
	public AlertDialog(Shell parent) {
		super(parent);
	}

	/**
	 * @param storeName
	 * @param mensaje
	 * @return
	 */
	public boolean open(String mensaje) {

		Shell parent = getParentShell();

		this.sShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		this.sShell.setImage(new Image(this.sShell.getDisplay(), ClassLoader
				.getSystemResourceAsStream(ImagesUtil.SINADURA_LOGO_IMG)));
		
		this.sShell.setText(LanguageUtil.getLanguage().getString("alert.dialog.title"));
		
		GridLayout shellGridLayout = new GridLayout();
		shellGridLayout.numColumns = 1;
		shellGridLayout.verticalSpacing = 10;
		shellGridLayout.marginTop = 15;
		this.sShell.setLayout(shellGridLayout);

		this.textoLabel = new Label(this.sShell, SWT.WRAP | SWT.NONE);
		this.textoLabel.setText(mensaje);
		
		GridData gdTexto = new GridData();
		gdTexto.widthHint = 600;
		textoLabel.setLayoutData(gdTexto);

		Composite ButtonsComposite = new Composite(this.sShell, SWT.NONE);
		ButtonsComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		GridLayout compositeGridLayout = new GridLayout();
		compositeGridLayout.numColumns = 2;
		compositeGridLayout.horizontalSpacing = 50;
		ButtonsComposite.setLayout(compositeGridLayout);

		
		this.bottonAceptar = new Button(ButtonsComposite, SWT.NONE);
		this.bottonAceptar.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		this.bottonAceptar.setText(LanguageUtil.getLanguage().getString("button.accept"));
		this.bottonAceptar.setImage(new Image(this.sShell.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.ACEPTAR_IMG)));
		this.bottonAceptar.addSelectionListener(new BotonAceptarListener());

		this.bottonCancelar = new Button(ButtonsComposite, SWT.NONE);
		this.bottonCancelar.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		this.bottonCancelar.setText(LanguageUtil.getLanguage().getString("button.cancel"));
		this.bottonCancelar.setImage(new Image(this.sShell.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.CANCEL_IMG)));
		this.bottonCancelar.addSelectionListener(new BotonCancelarListener());

		this.sShell.pack();
		
		// to center the shell on the screen
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

		return this.result;

	}

	class BotonAceptarListener implements SelectionListener {
		public void widgetSelected(SelectionEvent event) {
			result = true;
			sShell.dispose();
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}
	}

	class BotonCancelarListener implements SelectionListener {
		public void widgetSelected(SelectionEvent event) {
			sShell.dispose();
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}
	}

}
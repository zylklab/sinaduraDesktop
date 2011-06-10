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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.esle.sinadura.gui.events.BotonCancelarListener;
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

/**
 * @author zylk.net
 */
public class AliasDialog extends Dialog {

	private Shell sShell = null;
	
	private Combo aliasCombo;
	private String alias = null;
	
	private Composite ButtonsComposite = null;
	private Button bottonAceptar = null;
	private Button bottonCancelar = null;
	
	public List<String> aliases = new ArrayList<String>(); 
	

	/**
	 * @param parent
	 */
	public AliasDialog(Shell parent) {
		super(parent);
	}

	/**
	 * @param storeName
	 * @param mensaje
	 * @return
	 */
	public String open(List<String> listaAliases) {

		Shell parent = getParentShell();

		this.sShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		this.sShell
				.setImage(new Image(sShell.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.SINADURA_LOGO_IMG)));
		this.sShell.setText(LanguageUtil.getLanguage().getString("alias.dialog.title"));

		GridLayout shellGridLayout = new GridLayout();
		shellGridLayout.numColumns = 1;
		shellGridLayout.verticalSpacing = 10;
		shellGridLayout.marginTop = 10;
		this.sShell.setLayout(shellGridLayout);

		Label textoLabel = new Label(this.sShell, SWT.NONE);
		textoLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		textoLabel.setText(LanguageUtil.getLanguage().getString("alias.dialog.message"));
		
		GridData gdTexto = new GridData();
		gdTexto.widthHint = 400;
		textoLabel.setLayoutData(gdTexto);
		
		this.aliasCombo = new Combo(this.sShell, SWT.NONE | SWT.READ_ONLY);
		this.aliasCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		for (String string : listaAliases) 
		{
			aliases.add(string);

			if(string.length() > 83)
			{
				string = string.substring(0,80) + "...";
			}
			this.aliasCombo.add(string);
			this.aliasCombo.select(0);
		}
		
		
		this.ButtonsComposite = new Composite(this.sShell, SWT.NONE);
		this.ButtonsComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		GridLayout compositeGridLayout = new GridLayout();
		compositeGridLayout.numColumns = 2;
		compositeGridLayout.horizontalSpacing = 50;
		this.ButtonsComposite.setLayout(compositeGridLayout);

		this.bottonAceptar = new Button(this.ButtonsComposite, SWT.NONE);
		this.bottonAceptar.setText(LanguageUtil.getLanguage().getString("button.accept"));
		this.bottonAceptar.setImage(new Image(this.sShell.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.ACEPTAR_IMG)));
		this.bottonAceptar.addSelectionListener(new BotonAceptarListener());

		this.bottonCancelar = new Button(this.ButtonsComposite, SWT.NONE);
		this.bottonCancelar.setText(LanguageUtil.getLanguage().getString("button.cancel"));
		this.bottonCancelar.setImage(new Image(this.sShell.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.CANCEL_IMG)));
		this.bottonCancelar.addSelectionListener(new BotonCancelarListener());
		
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

		return alias;
	}

	class BotonAceptarListener implements SelectionListener {
		
		public void widgetSelected(SelectionEvent event) {
			alias = aliases.get(aliasCombo.getSelectionIndex());
			sShell.dispose();
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}
	}
}
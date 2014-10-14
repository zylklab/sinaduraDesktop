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
/**
 * Excmo. Cabildo Insular de Tenerife
 * Instituto Insular de Informática y Comunicaciones
 *
 * sinaduraDesktop
 * martind - 13/06/2011
 */
package net.esle.sinadura.gui.controller;

import java.util.List;

import net.esle.sinadura.gui.view.main.SignatureFieldSelectorDialog;

import org.eclipse.swt.widgets.Shell;

/**
 * Runnable para abrir el diálogo de seleccion de campo de firma.
 * 
 */
public class SignatureFieldSelectorRunnable implements Runnable {

	
	private Shell shell;
	private String signatureName;
	private List<String> signatureFields;
	

	SignatureFieldSelectorRunnable(Shell shell, List<String> signatureFields) {
		
		this.shell = shell;
		this.signatureFields = signatureFields;
	}

	@Override
	public void run() {
		
		SignatureFieldSelectorDialog sfsd = new SignatureFieldSelectorDialog(shell);
		this.signatureName = sfsd.open(signatureFields);
	}

	public String getSignatureName() {
		return signatureName;
	}

}

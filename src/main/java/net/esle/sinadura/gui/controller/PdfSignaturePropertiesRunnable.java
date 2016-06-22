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

import org.eclipse.swt.widgets.Shell;

import net.esle.sinadura.gui.util.PdfProfile;
import net.esle.sinadura.gui.view.main.PdfSignaturePropertiesDialog;

/**
 * Runnable para abrir el diálogo de seleccion de properties.
 * 
 */
public class PdfSignaturePropertiesRunnable implements Runnable {

	
	private PdfProfile selectedPdfProfile = null;
	private Shell shell;

	PdfSignaturePropertiesRunnable(Shell shell) {
		
		this.shell = shell;	
	}

	@Override
	public void run() {
			
		PdfSignaturePropertiesDialog pdfDialog = new PdfSignaturePropertiesDialog(shell);
		pdfDialog.open();
		PdfProfile newProfile = pdfDialog.getPdfProfile();
		if (newProfile != null) {
			selectedPdfProfile = newProfile;
		}
		
	}

	public PdfProfile getPdfProfile() {
		return selectedPdfProfile;
	}

}

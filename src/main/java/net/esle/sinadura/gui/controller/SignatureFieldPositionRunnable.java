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

import net.esle.sinadura.core.model.PdfSignatureField;
import net.esle.sinadura.core.util.FileUtil;
import net.esle.sinadura.gui.model.DocumentInfo;
import net.esle.sinadura.gui.view.preferences.PdfSignatureFieldPositionDialog;

import org.eclipse.swt.widgets.Shell;

/**
 * Runnable para abrir el diálogo de seleccion de posicion.
 * 
 */
public class SignatureFieldPositionRunnable implements Runnable {

	
	private PdfSignatureField signatureField = null;
	private Shell shell;
	private DocumentInfo pdfParameter;
	private String stampPath;
	
	private PdfSignatureField selectedSignatureField = null;
	

	SignatureFieldPositionRunnable(Shell shell, PdfSignatureField pdfBlankSignatureInfo, DocumentInfo pdfParameter, String stampPath) {
		
		this.shell = shell;
		this.signatureField = pdfBlankSignatureInfo;
		this.pdfParameter = pdfParameter;
		this.stampPath = stampPath;
	}

	@Override
	public void run() {
		
		PdfSignatureFieldPositionDialog pdfSignatureFieldPositionDialog = new PdfSignatureFieldPositionDialog(shell, stampPath, signatureField, FileUtil.getLocalPathFromURI(pdfParameter.getPath()));
		selectedSignatureField = pdfSignatureFieldPositionDialog.createSShell();
		
	}

	public PdfSignatureField getSignatureField() {
		return selectedSignatureField;
	}

}

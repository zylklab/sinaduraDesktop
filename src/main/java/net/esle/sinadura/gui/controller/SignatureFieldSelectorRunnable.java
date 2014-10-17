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

import net.esle.sinadura.core.model.PdfSignatureField;
import net.esle.sinadura.core.util.FileUtil;
import net.esle.sinadura.gui.model.DocumentInfo;
import net.esle.sinadura.gui.view.main.PdfSignatureFieldSelectorDialog;

import org.eclipse.swt.widgets.Shell;

/**
 * Runnable para abrir el diálogo de seleccion de campo de firma.
 * 
 */
public class SignatureFieldSelectorRunnable implements Runnable {

	
	private PdfSignatureField selectedSignatureField = null;
	private Shell shell;
	private List<PdfSignatureField> signatureFields;
	private DocumentInfo pdfParameter;
	private String stampPath;
	

	SignatureFieldSelectorRunnable(Shell shell, DocumentInfo pdfParameter, List<PdfSignatureField> signatureFields, String stampPath) {
		
		this.shell = shell;
		this.signatureFields = signatureFields;
		this.pdfParameter = pdfParameter;
		this.stampPath = stampPath; 
	}

	@Override
	public void run() {
		
//		// TODO mantener este dialog a modo de como fallback?? sí por config
//		SignatureFieldSelectorDialog sfsd = new SignatureFieldSelectorDialog(shell);
//		this.signatureName = sfsd.open(signatureFields);
		
		PdfSignatureFieldSelectorDialog pdfSignatureFieldSelectorDialog = new PdfSignatureFieldSelectorDialog(shell, signatureFields, stampPath, FileUtil.getLocalPathFromURI(pdfParameter.getPath()));
		selectedSignatureField = pdfSignatureFieldSelectorDialog.createSShell();
		
	}

	public PdfSignatureField getSelectedSignatureField() {
		return selectedSignatureField;
	}

}

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

import java.io.IOException;
import java.security.KeyStore.PasswordProtection;
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
	private PasswordProtection ownerPassword;
	private String stampPath;
	
	// Esto deberia ser una Exception en vez de una IOException para que sea un tratamiento generico. Pero como ahora unicamente
	// se puede producir una IOException y ademas no quiero cambiar el tratamiento de fuera (habria que empezar a tratar Exception
	// de forma generica en vez de forma individual como se esta haciendo ahora) lo dejo asi.
	private IOException exception = null;
	

	SignatureFieldSelectorRunnable(Shell shell, DocumentInfo pdfParameter, PasswordProtection ownerPassword, List<PdfSignatureField> signatureFields, String stampPath) {
		
		this.shell = shell;
		this.signatureFields = signatureFields;
		this.pdfParameter = pdfParameter;
		this.ownerPassword = ownerPassword;
		this.stampPath = stampPath; 
	}

	@Override
	public void run() {
		
		try {
		
//			// TODO mantener este dialog a modo de como fallback?? sí por config
//			SignatureFieldSelectorDialog sfsd = new SignatureFieldSelectorDialog(shell);
//			this.signatureName = sfsd.open(signatureFields);
			
			PdfSignatureFieldSelectorDialog pdfSignatureFieldSelectorDialog = new PdfSignatureFieldSelectorDialog(shell, signatureFields, stampPath, FileUtil.getLocalPathFromURI(pdfParameter.getPath()), ownerPassword);
			selectedSignatureField = pdfSignatureFieldSelectorDialog.createSShell();
			
		} catch (IOException e) {
			exception = e;
		}
	}

	public PdfSignatureField getSelectedSignatureField() {
		return selectedSignatureField;
	}

	// Esto deberia ser una Exception en vez de una IOException para que sea un tratamiento generico. Pero como ahora unicamente
	// se puede producir una IOException y ademas no quiero cambiar el tratamiento de fuera (habria que empezar a tratar Exception
	// de forma generica en vez de forma individual como se esta haciendo ahora) lo dejo asi.
	// IOException getIOException() -> Exception getException()
	public IOException getIOException() {
		return exception;
	}
	
}

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
package net.esle.sinadura.gui.controller;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import net.esle.sinadura.core.exceptions.Pkcs7Exception;
import net.esle.sinadura.core.exceptions.ValidationFatalException;
import net.esle.sinadura.core.exceptions.XadesValidationFatalException;
import net.esle.sinadura.core.model.PDFSignatureInfo;
import net.esle.sinadura.core.model.XadesSignatureInfo;
import net.esle.sinadura.core.service.PdfService;
import net.esle.sinadura.core.service.Pkcs7Service;
import net.esle.sinadura.core.service.XadesService;
import net.esle.sinadura.core.util.FileUtil;
import net.esle.sinadura.gui.events.ProgressWriter;
import net.esle.sinadura.gui.model.DocumentInfo;
import net.esle.sinadura.gui.model.SignatureInfo;
import net.esle.sinadura.gui.util.LanguageUtil;
import net.esle.sinadura.gui.util.PreferencesUtil;
import net.esle.sinadura.gui.util.ValidationInterpreterUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Display;

public class ValidateController {

	private static final Log log = LogFactory.getLog(ValidateController.class);
	
	public static void validate(DocumentInfo pdfParameter) {

		if (pdfParameter.getMimeType() != null && pdfParameter.getMimeType().equals(FileUtil.MIMETYPE_PDF)) {
			validatePDF(pdfParameter);
		} else if (pdfParameter.getMimeType() != null && pdfParameter.getMimeType().equals(FileUtil.MIMETYPE_SAR)) {
			validateXades(pdfParameter);
		} else if (pdfParameter.getMimeType() != null && pdfParameter.getMimeType().equals(FileUtil.MIMETYPE_XML)) {
			validateXades(pdfParameter);
		} else if (pdfParameter.getMimeType() != null && pdfParameter.getMimeType().equals(FileUtil.MIMETYPE_P7S)) {
			validateP7(pdfParameter);
		} else {
			// documento no firmado
			// actualizo la entrada de la tabla
			pdfParameter.setSignatures(new ArrayList<SignatureInfo>());
		}
	}

	private static void validatePDF(DocumentInfo pdfParameter) {

		try {
			List<PDFSignatureInfo> pdfSignatureInfos = PdfService.validate(pdfParameter.getPath(),
					PreferencesUtil.getCacheKeystoreComplete(), PreferencesUtil.getTrustedKeystoreComplete());
			
			pdfParameter.setSignatures(ValidationInterpreterUtil.parsePdfSignatureInfo(pdfSignatureInfos));

		} catch (ValidationFatalException e) {

			String m = MessageFormat.format(LanguageUtil.getLanguage().getString("error.certificate.validation.unexpected"),
					pdfParameter.getPath(), e.getMessage());
			log.error(m, e);
			Display.getDefault().syncExec(new ProgressWriter(ProgressWriter.ERROR, m));
		}

	}

	private static void validateXades(DocumentInfo pdfParameter) {

		try {
			List<XadesSignatureInfo> resultados = XadesService.validateArchiver(pdfParameter.getPath(), PreferencesUtil.getCacheKeystoreComplete(),
					PreferencesUtil.getTrustedKeystoreComplete());
			
			pdfParameter.setSignatures(ValidationInterpreterUtil.parseResultadoValidacion(resultados));
			
		} catch (XadesValidationFatalException e) {
			
			String m = MessageFormat.format(LanguageUtil.getLanguage().getString("error.certificate.validation.unexpected"),
					pdfParameter.getPath(), e.getMessage());
			log.error(m, e);
			Display.getDefault().syncExec(new ProgressWriter(ProgressWriter.ERROR, m));
		}

	}

	private static void validateP7(DocumentInfo pdfParameter) {
		
		try {
			byte[] bytes = FileUtil.getBytesFromFile(new File(pdfParameter.getPath()));
			
			List<PDFSignatureInfo> pdfSignatureInfos = Pkcs7Service.validate(bytes, PreferencesUtil.getCacheKeystoreComplete(),
					PreferencesUtil.getTrustedKeystoreComplete());
			
			
			pdfParameter.setSignatures(ValidationInterpreterUtil.parsePdfSignatureInfo(pdfSignatureInfos));

		} catch (Pkcs7Exception e) {

			String m = MessageFormat.format(LanguageUtil.getLanguage().getString("error.certificate.validation.unexpected"),
					pdfParameter.getPath(), e.getMessage());
			log.error(m, e);
			Display.getDefault().syncExec(new ProgressWriter(ProgressWriter.ERROR, m));
			
		} catch (IOException e) {
			String m = MessageFormat.format(LanguageUtil.getLanguage().getString("error.certificate.validation.unexpected"),
					pdfParameter.getPath(), e.getMessage());
			log.error(m, e);
			Display.getDefault().syncExec(new ProgressWriter(ProgressWriter.ERROR, m));
		}
		
	}

}

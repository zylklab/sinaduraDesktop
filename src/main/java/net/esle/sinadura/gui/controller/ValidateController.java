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
import net.esle.sinadura.core.interpreter.SignatureInfo;
import net.esle.sinadura.core.interpreter.ValidationInterpreterUtil;
import net.esle.sinadura.core.model.PDFSignatureInfo;
import net.esle.sinadura.core.model.Status;
import net.esle.sinadura.core.model.ValidationError;
import net.esle.sinadura.core.model.XadesSignatureInfo;
import net.esle.sinadura.core.service.PdfService;
import net.esle.sinadura.core.service.Pkcs7Service;
import net.esle.sinadura.core.service.XadesService;
import net.esle.sinadura.core.util.FileUtil;
import net.esle.sinadura.core.xades.validator.XadesValidator;
import net.esle.sinadura.core.xades.validator.XadesValidatorFactory;
import net.esle.sinadura.gui.events.ProgressWriter;
import net.esle.sinadura.gui.model.DocumentInfo;
import net.esle.sinadura.gui.util.LanguageUtil;
import net.esle.sinadura.gui.util.PreferencesUtil;
import net.esle.sinadura.gui.util.PropertiesUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Display;

public class ValidateController {

	private static final Log log = LogFactory.getLog(ValidateController.class);
	
	public static void validate(DocumentInfo pdfParameter) {

		if (pdfParameter.getMimeType() != null && pdfParameter.getMimeType().equals(FileUtil.MIMETYPE_PDF)) {
			validatePDF(pdfParameter);
		} else if (pdfParameter.getMimeType() != null
				&& (pdfParameter.getMimeType().equals(FileUtil.MIMETYPE_SAR) || pdfParameter.getMimeType().equals(
						FileUtil.MIMETYPE_XML))) {
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
					FileUtil.getLocalPathFromURI(pdfParameter.getPath()), e.getMessage());
			log.error(m, e);
			Display.getDefault().syncExec(new ProgressWriter(ProgressWriter.ERROR, m));
		}

	}

	private static void validateXades(DocumentInfo pdfParameter) {

		try {
			
			XadesValidator xadesValidator;
			
			String xadesValidatorImpl = PreferencesUtil.getPreferences().getString(PreferencesUtil.XADES_VALIDATOR_IMPL);
			
			if (xadesValidatorImpl != null && xadesValidatorImpl.equals("zain")) { // TODO hardcode!
				
				String endPoint = PreferencesUtil.getPreferences().getString(PreferencesUtil.ZAIN_ENDPOINT);
				String truststorePath = PreferencesUtil.getPreferences().getString(PreferencesUtil.ZAIN_TRUSTED_PATH);
				String truststorePassword = PreferencesUtil.getPreferences().getString(PreferencesUtil.ZAIN_TRUSTED_PASSWORD);
				String keystorePath = PreferencesUtil.getPreferences().getString(PreferencesUtil.ZAIN_P12_PATH);
				String keystorePassword = PreferencesUtil.getPreferences().getString(PreferencesUtil.ZAIN_P12_PASSWORD);
				boolean logActive = PreferencesUtil.getPreferences().getBoolean(PreferencesUtil.ZAIN_LOG_ACTIVE);
				String requestLogSavePath = PropertiesUtil.LOG_ZAIN_REQUEST_FOLDER_PATH;
				String responseLogSavePath = PropertiesUtil.LOG_ZAIN_RESPONSE_FOLDER_PATH;
				
				xadesValidator = XadesValidatorFactory.getZainInstance(endPoint, truststorePath, truststorePassword,
						keystorePath, keystorePassword, logActive, requestLogSavePath, responseLogSavePath);
				
			} else if (xadesValidatorImpl != null && xadesValidatorImpl.equals("sinadura")) {
				
				xadesValidator = XadesValidatorFactory.getSinaduraInstance();
			} else {
				throw new XadesValidationFatalException("unknown xades validator impl");
			}
			
			List<XadesSignatureInfo> resultados = null;
			if (pdfParameter.getMimeType() != null && pdfParameter.getMimeType().equals(FileUtil.MIMETYPE_SAR)) {
				resultados = XadesService.validateArchiver(xadesValidator, pdfParameter.getPath(),
						PreferencesUtil.getCacheKeystoreComplete(), PreferencesUtil.getTrustedKeystoreComplete());
			} else if (pdfParameter.getMimeType() != null && pdfParameter.getMimeType().equals(FileUtil.MIMETYPE_XML)) {
				// TODO posibilitar el envio de documentos adjuntos (para detached). De momento los detached solo a partir de sar.
				resultados = XadesService.validateXml(xadesValidator, pdfParameter.getPath(), null,
						PreferencesUtil.getCacheKeystoreComplete(), PreferencesUtil.getTrustedKeystoreComplete());
			}
			
			pdfParameter.setSignatures(ValidationInterpreterUtil.parseResultadoValidacion(resultados));
			
		} catch (XadesValidationFatalException e) {
			
			String m = MessageFormat.format(LanguageUtil.getLanguage().getString("error.certificate.validation.unexpected"),
					FileUtil.getLocalPathFromURI(pdfParameter.getPath()), e.getMessage());
			log.error(m, e);
			Display.getDefault().syncExec(new ProgressWriter(ProgressWriter.ERROR, m));
		}

	}

	private static void validateP7(DocumentInfo p7path) {
		
		try {
			byte[] bytes = FileUtil.getBytesFromFile(FileUtil.getLocalFileFromURI(p7path.getPath()));
			
			List<PDFSignatureInfo> pdfSignatureInfos = Pkcs7Service.validate(bytes, PreferencesUtil.getCacheKeystoreComplete(),
					PreferencesUtil.getTrustedKeystoreComplete());
			
			
			p7path.setSignatures(ValidationInterpreterUtil.parsePdfSignatureInfo(pdfSignatureInfos));

		} catch (Pkcs7Exception e) {

			String m = MessageFormat.format(LanguageUtil.getLanguage().getString("error.certificate.validation.unexpected"),
					FileUtil.getLocalPathFromURI(p7path.getPath()), e.getMessage());
			log.error(m, e);
			Display.getDefault().syncExec(new ProgressWriter(ProgressWriter.ERROR, m));
			
			// Pkcs7Exception > error 
			List<PDFSignatureInfo> pdfSignaturesList = new ArrayList<PDFSignatureInfo>();
			PDFSignatureInfo pdfSignature = new PDFSignatureInfo(); 
			pdfSignature.setError(ValidationError.CORRUPT);
			pdfSignature.setStatus(Status.INVALID);
			pdfSignaturesList.add(pdfSignature);
			p7path.setSignatures(ValidationInterpreterUtil.parsePdfSignatureInfo(pdfSignaturesList));
			
		} catch (IOException e) {
			
			String m = MessageFormat.format(LanguageUtil.getLanguage().getString("error.certificate.validation.unexpected"),
					FileUtil.getLocalPathFromURI(p7path.getPath()), e.getMessage());
			log.error(m, e);
			Display.getDefault().syncExec(new ProgressWriter(ProgressWriter.ERROR, m));
		}
		
	}

}

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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.esle.sinadura.core.exceptions.CipherException;
import net.esle.sinadura.core.exceptions.Pkcs7Exception;
import net.esle.sinadura.core.exceptions.ValidationFatalException;
import net.esle.sinadura.core.exceptions.XadesValidationFatalException;
import net.esle.sinadura.core.interpreter.SignatureInfo;
import net.esle.sinadura.core.interpreter.ValidationInterpreterUtil;
import net.esle.sinadura.core.model.PDFSignatureInfo;
import net.esle.sinadura.core.model.Status;
import net.esle.sinadura.core.model.ValidationError;
import net.esle.sinadura.core.model.ValidationPreferences;
import net.esle.sinadura.core.model.XadesSignatureInfo;
import net.esle.sinadura.core.service.PdfService;
import net.esle.sinadura.core.service.Pkcs7Service;
import net.esle.sinadura.core.service.XadesService;
import net.esle.sinadura.core.util.CipherUtil;
import net.esle.sinadura.core.util.FileUtil;
import net.esle.sinadura.core.util.PropertiesCoreUtil;
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
		} else if (pdfParameter.getMimeType() != null && (pdfParameter.getMimeType().equals(FileUtil.MIMETYPE_SAR)
						|| pdfParameter.getMimeType().equals(FileUtil.MIMETYPE_CXSIG)
						|| pdfParameter.getMimeType().equals(FileUtil.MIMETYPE_XML))) {
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
				
				String endPoint = PropertiesUtil.get(PropertiesUtil.ZAIN_ENDPOINT);
				String truststorePath = PropertiesUtil.get(PropertiesUtil.ZAIN_TRUSTED_PATH_ABSOLUTE);
				String truststorePassword = PropertiesUtil.get(PropertiesUtil.ZAIN_TRUSTED_PASSWORD);
				String keystorePath = PropertiesUtil.get(PropertiesUtil.ZAIN_P12_PATH_ABSOLUTE);
				String encryptedPassword = PropertiesUtil.get(PropertiesUtil.ZAIN_P12_PASSWORD);
				String keystorePassword;
				try {
					keystorePassword = CipherUtil.decrypt(encryptedPassword);
				} catch (CipherException e) {
					throw new XadesValidationFatalException(e);
				}
				
				boolean logActive = PropertiesUtil.getBoolean(PropertiesUtil.ZAIN_LOG_ACTIVE);
				String requestLogSavePath = PropertiesUtil.get(PropertiesUtil.ZAIN_LOG_REQUEST_FOLDER_PATH);
				String responseLogSavePath = PropertiesUtil.get(PropertiesUtil.ZAIN_LOG_RESPONSE_FOLDER_PATH);
				
				String proxyUser = null;
				String proxyPass = null;
				boolean proxyEnabled = Boolean.valueOf(PropertiesUtil.getConfiguration().getProperty(PropertiesUtil.PROXY_ENABLED));
				if (proxyEnabled && PreferencesUtil.getPreferences().getBoolean(PreferencesUtil.PROXY_SYSTEM)) {
					proxyUser = PreferencesUtil.getPreferences().getString(PreferencesUtil.PROXY_USER);
					proxyPass = PreferencesUtil.getPreferences().getString(PreferencesUtil.PROXY_PASS);
				}
				
				String language = Locale.getDefault().getLanguage();
				
				xadesValidator = XadesValidatorFactory.getZainInstance(endPoint, truststorePath, truststorePassword,
						keystorePath, keystorePassword, proxyUser, proxyPass, logActive, requestLogSavePath, responseLogSavePath, language);
				
			} else if (xadesValidatorImpl != null && xadesValidatorImpl.equals("sinadura")) {
				
				xadesValidator = XadesValidatorFactory.getSinaduraInstance();
			} else {
				throw new XadesValidationFatalException("unknown xades validator impl: " + xadesValidatorImpl);
			}
			
			ValidationPreferences validationPreferences = new ValidationPreferences();
			validationPreferences.setCheckRevocation(PreferencesUtil.getPreferences().getBoolean(PreferencesUtil.VALIDATION_CHECK_REVOCATION));
			validationPreferences.setValidateEpesPolicy(PreferencesUtil.getPreferences().getBoolean(PreferencesUtil.VALIDATION_CHECK_POLICY));
			validationPreferences.setKsCache(PreferencesUtil.getCacheKeystoreComplete());
			validationPreferences.setKsTrust(PreferencesUtil.getTrustedKeystoreComplete());

			// configuracion estatica, lo dejo aqui para que este con el resto de preferencias de validacion
			PropertiesCoreUtil.setCheckNodeName(PreferencesUtil.getPreferences().getBoolean(PreferencesUtil.VALIDATION_CHECK_NODE_NAME));
			
			List<XadesSignatureInfo> resultados = null;
			
			if (pdfParameter.getMimeType() != null && pdfParameter.getMimeType().equals(FileUtil.MIMETYPE_SAR)) {
				resultados = XadesService.validateArchiver(xadesValidator, pdfParameter.getPath(), validationPreferences);
				
			} else if (pdfParameter.getMimeType() != null && pdfParameter.getMimeType().equals(FileUtil.MIMETYPE_CXSIG)) {
				// TODO URI?? y polimorfismo de la funcion de validacion (por path en vez de IS)
				try {
					URI uri = new URI(pdfParameter.getPath());
					File file = new File(uri);
					InputStream is = new FileInputStream(file);
					resultados = XadesService.validateCxsig(xadesValidator, is, validationPreferences);
				} catch (FileNotFoundException e) {
					throw new XadesValidationFatalException(e);
				} catch (URISyntaxException e) {
					throw new XadesValidationFatalException(e);
				}
				
			} else if (pdfParameter.getMimeType() != null && pdfParameter.getMimeType().equals(FileUtil.MIMETYPE_XML)) {
				// TODO posibilitar el envio de documentos adjuntos (para detached). De momento los detached solo a partir de sar.
				resultados = XadesService.validateXml(xadesValidator, pdfParameter.getPath(), null, validationPreferences);
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

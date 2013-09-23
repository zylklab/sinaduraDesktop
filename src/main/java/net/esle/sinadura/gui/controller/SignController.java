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
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.esle.sinadura.core.certificate.CertificateUtil;
import net.esle.sinadura.core.exceptions.ConnectionException;
import net.esle.sinadura.core.exceptions.CoreException;
import net.esle.sinadura.core.exceptions.CorePKCS12Exception;
import net.esle.sinadura.core.exceptions.NoSunPkcs11ProviderException;
import net.esle.sinadura.core.exceptions.OCSPCoreException;
import net.esle.sinadura.core.exceptions.OCSPIssuerRequiredException;
import net.esle.sinadura.core.exceptions.OCSPUnknownUrlException;
import net.esle.sinadura.core.exceptions.PKCS11Exception;
import net.esle.sinadura.core.exceptions.PasswordCallbackCanceledException;
import net.esle.sinadura.core.exceptions.PdfSignatureException;
import net.esle.sinadura.core.exceptions.RevokedException;
import net.esle.sinadura.core.exceptions.XadesSignatureException;
import net.esle.sinadura.core.keystore.KeyStoreBuilderFactory;
import net.esle.sinadura.core.keystore.KeyStoreBuilderFactory.KeyStoreTypes;
import net.esle.sinadura.core.keystore.PKCS11Helper;
import net.esle.sinadura.core.model.KsSignaturePreferences;
import net.esle.sinadura.core.model.PdfSignaturePreferences;
import net.esle.sinadura.core.model.XadesSignaturePreferences;
import net.esle.sinadura.core.password.DummyCallbackHandler;
import net.esle.sinadura.core.password.PasswordExtractor;
import net.esle.sinadura.core.service.PdfService;
import net.esle.sinadura.core.service.XadesService;
import net.esle.sinadura.core.util.FileUtil;
import net.esle.sinadura.gui.events.ProgressWriter;
import net.esle.sinadura.gui.exceptions.AliasesNotFoundException;
import net.esle.sinadura.gui.exceptions.DriversNotFoundException;
import net.esle.sinadura.gui.exceptions.OverwritingException;
import net.esle.sinadura.gui.model.DocumentInfo;
import net.esle.sinadura.gui.util.LanguageUtil;
import net.esle.sinadura.gui.util.PreferencesUtil;
import net.esle.sinadura.gui.util.StatisticsUtil;

import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;
import com.itextpdf.text.exceptions.BadPasswordException;

import es.mityc.firmaJava.libreria.utilidades.URIEncoder;

public class SignController {

	private static final Log log = LogFactory.getLog(SignController.class);

	public static Map<String, Long> loadSlot() throws NoSuchAlgorithmException, KeyStoreException, PKCS11Exception,
			CoreException, CorePKCS12Exception, DriversNotFoundException {

		String certificadoType = PreferencesUtil.getPreferences().getString(PreferencesUtil.CERT_TYPE);

		// compruebo las preferencias y cargo el certificado del dispositivo
		// hardware, o del el file-system
		Map<String, Long> slotsByReader = new HashMap<String, Long>();
		
		if (certificadoType.equalsIgnoreCase(PreferencesUtil.CERT_TYPE_VALUE_HARDWARE)) 
		{
			if (!System.getProperty("os.name").toLowerCase().contains("win")) {
			
				String pkcs11Path = PreferencesUtil.getDefaultHardware();
				PKCS11Helper pk11h = new PKCS11Helper(pkcs11Path, "");
				// TODO revisar si es necesario
				// long[] slots = null;
				// slots = pk11h.getSignatureCapableSlots();
				slotsByReader = pk11h.getSoltsByReaderName();
			}

		} else if (certificadoType.equalsIgnoreCase(PreferencesUtil.CERT_TYPE_VALUE_SOFTWARE)) {

			//do nothing
		}

		else if (certificadoType.equalsIgnoreCase(PreferencesUtil.CERT_TYPE_VALUE_MSCAPI)) {

			//do nothing

		}

		return slotsByReader;
	}

	public static KsSignaturePreferences loadKeyStore(Shell sShell, String slot) throws NoSuchAlgorithmException, KeyStoreException, PKCS11Exception, NoSunPkcs11ProviderException, 
			CoreException, CorePKCS12Exception, DriversNotFoundException {

		String certificadoType = PreferencesUtil.getPreferences().getString(PreferencesUtil.CERT_TYPE);

		PasswordCallbackHandlerDialog o = new PasswordCallbackHandlerDialog(sShell);
		PasswordExtractor pe = (PasswordExtractor) o;

		KsSignaturePreferences ksSignaturePreferences = new KsSignaturePreferences();
		KeyStore ks = null;

		// compruebo las preferencias y cargo el certificado del dispositivo
		// hardware, o del el file-system
		if (certificadoType.equalsIgnoreCase(PreferencesUtil.CERT_TYPE_VALUE_HARDWARE)) {

			String pkcs11Path = PreferencesUtil.getDefaultHardware();
			
			StatisticsUtil.log(StatisticsUtil.KEY_SIGN_CERTTYPE, StatisticsUtil.VALUE_HARD);
			log.info("sign type: " + StatisticsUtil.VALUE_HARD);
			
			StatisticsUtil.log(StatisticsUtil.KEY_LOAD_HARDWAREDRIVER, pkcs11Path);
			log.info("sign driver: " + pkcs11Path);

			ks = KeyStoreBuilderFactory.getKeyStore("HARD", KeyStoreTypes.PKCS11, pkcs11Path, slot, new KeyStore.CallbackHandlerProtection(
					o));

		} else if (certificadoType.equalsIgnoreCase(PreferencesUtil.CERT_TYPE_VALUE_SOFTWARE)) {

			String pkcs12Path = PreferencesUtil.getSoftwarePreferences().get(
					PreferencesUtil.getPreferences().getString(PreferencesUtil.SOFTWARE_DISPOSITIVE));
			StatisticsUtil.log(StatisticsUtil.KEY_SIGN_CERTTYPE, StatisticsUtil.VALUE_SOFT);
			ks = KeyStoreBuilderFactory.getKeyStore("SOFT", KeyStoreTypes.PKCS12, pkcs12Path, new KeyStore.CallbackHandlerProtection(o));
		}

		else if (certificadoType.equalsIgnoreCase(PreferencesUtil.CERT_TYPE_VALUE_MSCAPI)) {

			StatisticsUtil.log(StatisticsUtil.KEY_SIGN_CERTTYPE, StatisticsUtil.VALUE_MSCAPI);
			DummyCallbackHandler a = new DummyCallbackHandler(null);
			pe = (PasswordExtractor) a;
			ks = KeyStoreBuilderFactory.getKeyStore("MSCAPI", KeyStoreTypes.MSCAPI, null, new KeyStore.CallbackHandlerProtection(a));

		}

		ksSignaturePreferences.setKs(ks);

		// fijo el passwordprotection para el PKCS12, para el PKCS11 no es
		// necesario pero por coherencia lo uso tambien.
		ksSignaturePreferences.setPasswordProtection(pe.getPasswordProtection());

		return ksSignaturePreferences;
	}

	public static void logout(KeyStore ks, String alias) throws NoSunPkcs11ProviderException {

		// una vez que se ha firmado... hago un logout de la session del
		// provider
		KeyStoreBuilderFactory.logout(ks, alias);
	}

	/************************************************************
	 * Se obtien el alias del certificado con el que firmar, 
	 * pero siguiendo unas preferencias (si está el check habilitado)
	 * #13187
	 *************************************************************/
	public static List<String> getAlias(KeyStore ks) throws AliasesNotFoundException, KeyStoreException {

		// clasificamos los certificados en base a la selección de preferencias
		//--------------
		boolean aplicarPreferencias = PreferencesUtil.getPreferences().getBoolean(PreferencesUtil.APLICAR_PREFERENCIAS_USAGE_CERT);
		
		List<String> certificadosNonRepudiation = new ArrayList<String>();
		List<String> certificadosDigitalSignature = new ArrayList<String>();
		List<String> certificadosSinKeyUsage = new ArrayList<String>();
		List<String> certificadosTodos = new ArrayList<String>();
		
		String certAlias;
		X509Certificate certificado;
		Enumeration<String> aliases = ks.aliases();
		
		StatisticsUtil.log(StatisticsUtil.KEY_CERTIFICADO_NUMERO, String.valueOf(ks.size()));
		log.info("Estadisticas | " + StatisticsUtil.KEY_CERTIFICADO_NUMERO + ": " + String.valueOf(ks.size()));
		
		while (aliases.hasMoreElements()) {

			certAlias = aliases.nextElement();
			certificado = (X509Certificate)ks.getCertificate(certAlias);
			
			StatisticsUtil.log(StatisticsUtil.KEY_CERTIFICADO_USAGE, CertificateUtil.getKeyUsage(certificado));
			log.info("Estadisticas | " + StatisticsUtil.KEY_CERTIFICADO_USAGE + ": " + CertificateUtil.getKeyUsage(certificado));

			
			if (aplicarPreferencias){
				if (CertificateUtil.esNonRepudiation(certificado)){
					certificadosNonRepudiation.add(certAlias);	
				}else if (CertificateUtil.esDigitalSignature(certificado)){
					certificadosDigitalSignature.add(certAlias);
				}else if (CertificateUtil.keyUsageNoDefinido(certificado)){
					certificadosSinKeyUsage.add(certAlias);
				}	
			}else{
				certificadosTodos.add(certAlias);				
			}
		}
		
		// obtenemos los certificados
		//--------------
		if (aplicarPreferencias){
			if (certificadosNonRepudiation.size() == 0 && 
				certificadosDigitalSignature.size() == 0 &&
				certificadosSinKeyUsage.size() == 0) {
				throw new AliasesNotFoundException();
			}else{
				if (certificadosNonRepudiation.size() > 0){
					return certificadosNonRepudiation;				
				}else if (certificadosDigitalSignature.size() > 0){
					return certificadosDigitalSignature;
				}else{
					return certificadosSinKeyUsage;
				}
			}
		}else{
			if (certificadosTodos.size() == 0) {
				throw new AliasesNotFoundException();
			}else{
				return certificadosTodos;
			}
		}
	}
	

	public static void sign(DocumentInfo pdfParameter, KsSignaturePreferences ksSignaturePreferences) throws OCSPCoreException,
			RevokedException, ConnectionException, CertificateExpiredException, CertificateNotYetValidException,
			OCSPIssuerRequiredException, OCSPUnknownUrlException {

		try {
			StatisticsUtil.log(StatisticsUtil.KEY_SIGN_DOCUMENT_EXTENSION, FileUtil.getExtension(pdfParameter.getPath()));
			log.info("extension del documento: " + FileUtil.getExtension(FileUtil.getLocalPathFromURI(pdfParameter.getPath())));
			
			StatisticsUtil.log(StatisticsUtil.KEY_SIGN_MIMETYPE, pdfParameter.getMimeType());
			log.info("mimetype del documento: " + pdfParameter.getMimeType());
			
			StatisticsUtil.log(StatisticsUtil.KEY_SIGN_DOCUMENT_SIZE, new File(pdfParameter.getPath()).length() + "");
			log.info("size del documento: " + new File(pdfParameter.getPath()).length() + "");

			StatisticsUtil.log(StatisticsUtil.KEY_SIGN_TSA, PreferencesUtil.getPreferences().getBoolean(PreferencesUtil.SIGN_TS_ENABLE)
					+ "");
			log.info("tsa enable: " + PreferencesUtil.getPreferences().getBoolean(PreferencesUtil.SIGN_TS_ENABLE)
					+ "");

			// firma
			if (pdfParameter.getMimeType() != null && pdfParameter.getMimeType().equals(FileUtil.MIMETYPE_PDF)) {
				
				if (PreferencesUtil.getPreferences().getString(PreferencesUtil.PDF_TIPO).equals(PreferencesUtil.PDF_TIPO_XML)){
					signDetached(pdfParameter, ksSignaturePreferences);
					
				}else{
					boolean successfullySigned = false;
					PasswordProtection ownerPassword = null;
					
					while (!successfullySigned) {
						try {
							signPDF(pdfParameter, ksSignaturePreferences, ownerPassword);
							successfullySigned = true;
							
						} catch (BadPasswordException e) {

							PasswordDialogRunnable runnable = new PasswordDialogRunnable(null, LanguageUtil.getLanguage().getString(
									"password.dialog.passwordprotected"));
							
							Display.getDefault().syncExec(runnable);
							if (runnable.getPasswordProtection() == null) {
								throw new PasswordCallbackCanceledException();
							}
							ownerPassword = runnable.getPasswordProtection();
						}
					}					
				}
					
			} else if (pdfParameter.getMimeType() != null && pdfParameter.getMimeType().equals(FileUtil.MIMETYPE_XML)) {
				// TODO Aqui tendria sentido hacer una firma enveloped
				// TODO Como ahora solo es detached -> mostrar error??? -> Para evitar firmar un xml que sea ya una firma.
				signDetached(pdfParameter, ksSignaturePreferences);
				
			} else if (pdfParameter.getMimeType() != null && pdfParameter.getMimeType().equals(FileUtil.MIMETYPE_SAR)) {
				signDetached(pdfParameter, ksSignaturePreferences);
				
			} else { // un documento cualquiera
				signDetached(pdfParameter, ksSignaturePreferences);
			}
			
		} catch (PasswordCallbackCanceledException e) {
			
			String m = MessageFormat.format(LanguageUtil.getLanguage().getString(
				"error.document.notsigned.passwordlocked"), FileUtil.getLocalPathFromURI(pdfParameter.getPath()));
			log.error(m, e);
			Display.getDefault().syncExec(new ProgressWriter(ProgressWriter.ERROR, m));
			
		} catch (OverwritingException e) {

			File file = FileUtil.getLocalFileFromURI(pdfParameter.getPath());
			String fileDestino = PreferencesUtil.getOutputDir(file) + File.separatorChar + PreferencesUtil.getOutputName(file.getName());

			String m = MessageFormat.format(LanguageUtil.getLanguage().getString("error.overwrite"), FileUtil.getLocalFileFromURI(pdfParameter.getPath()), FileUtil.getLocalPathFromURI(fileDestino));

			log.error(m, e);
			Display.getDefault().syncExec(new ProgressWriter(ProgressWriter.ERROR, m));

		} catch (IOException e) {

			String m = MessageFormat.format(LanguageUtil.getLanguage().getString("error.certificate.sign.unexpected"),
					FileUtil.getLocalPathFromURI(pdfParameter.getPath()), e.toString());
			log.error(m, e);
			Display.getDefault().syncExec(new ProgressWriter(ProgressWriter.ERROR, m));
		}
	}

	private static void signPDF(DocumentInfo pdfParameter, KsSignaturePreferences ksSignaturePreferences, PasswordProtection ownerPassword) throws OCSPCoreException,
			RevokedException, OverwritingException, ConnectionException, IOException, CertificateExpiredException,
			CertificateNotYetValidException, OCSPIssuerRequiredException, OCSPUnknownUrlException {

		try {

			PdfSignaturePreferences signaturePreferences = new PdfSignaturePreferences();
			signaturePreferences.setKsSignaturePreferences(ksSignaturePreferences);

			signaturePreferences.setKsCache(PreferencesUtil.getCacheKeystoreComplete());

			String reason = PreferencesUtil.getPreferences().getString(PreferencesUtil.PDF_REASON);
			signaturePreferences.setReason(reason);

			String location = PreferencesUtil.getPreferences().getString(PreferencesUtil.PDF_LOCATION);
			signaturePreferences.setLocation(location);

			boolean selloVisible = PreferencesUtil.getPreferences().getBoolean(PreferencesUtil.PDF_VISIBLE);
			signaturePreferences.setVisible(selloVisible);

			signaturePreferences.setPage(PreferencesUtil.getPreferences().getInt(PreferencesUtil.PDF_PAGE));

			signaturePreferences.setStartX(PreferencesUtil.getPreferences().getInt(PreferencesUtil.PDF_STAMP_X));
			signaturePreferences.setStartY(PreferencesUtil.getPreferences().getInt(PreferencesUtil.PDF_STAMP_Y));
			signaturePreferences.setWidht(PreferencesUtil.getPreferences().getInt(PreferencesUtil.PDF_STAMP_WIDTH));
			signaturePreferences.setHeight(PreferencesUtil.getPreferences().getInt(PreferencesUtil.PDF_STAMP_HEIGHT));

			Image sello = null;
			if (PreferencesUtil.getPreferences().getBoolean(PreferencesUtil.PDF_STAMP_ENABLE)) {
				try {
					sello = Image.getInstance(PreferencesUtil.getPreferences().getString(PreferencesUtil.PDF_STAMP_PATH));

				} catch (BadElementException e) {
					log.error("", e);

				} catch (MalformedURLException e) {
					log.error("", e);

				} catch (IOException e) {
					log.error("", e);
				}
			}
			signaturePreferences.setImage(sello);

			signaturePreferences.setCertified(PreferencesUtil.getPreferences().getInt(PreferencesUtil.PDF_CERTIFIED));

			String tsurl = null;
			String tsaOcspUrl = null; // en la firma de PDF este dato no es necesario, pero lo añado igualmente.
			if (PreferencesUtil.getPreferences().getBoolean(PreferencesUtil.SIGN_TS_ENABLE) == true) {
				tsurl = PreferencesUtil.getTimestampPreferences().get(PreferencesUtil.getPreferences().getString(PreferencesUtil.SIGN_TS_TSA)).getUrl();
				tsaOcspUrl = PreferencesUtil.getTimestampPreferences().get(PreferencesUtil.getPreferences().getString(PreferencesUtil.SIGN_TS_TSA)).getOcspUrl();
			}
			signaturePreferences.setTimestampUrl(tsurl);
			signaturePreferences.setTimestampOcspUrl(tsaOcspUrl);
			signaturePreferences.setTimestampUser(null);
			signaturePreferences.setTimestampPassword(null);

			boolean addOCSP = PreferencesUtil.getPreferences().getBoolean(PreferencesUtil.SIGN_OCSP_ENABLE);
			signaturePreferences.setAddOCSP(addOCSP);

			StatisticsUtil.log(StatisticsUtil.KEY_SIGN_OCSP, addOCSP + "");
			log.info("ocsp enable: " + addOCSP);

			
			InputStream is;
			try {
				is = FileUtil.getInputStreamFromURI(pdfParameter.getPath());
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				log.error("error creando el InputStream asociado al fichero de entrada " + FileUtil.getLocalPathFromURI(pdfParameter.getPath()) , e);
				throw new IOException(e);
			}
			
			
			URI fileUri;
			String outputPath = null;
			try {
				fileUri = new URI(FileUtil.urlEncoder(pdfParameter.getPath()));
			} catch (URISyntaxException e1) {
				log.error(e1);
				throw new IOException(e1);
			}
			
			log.info("File path pasado a URI : "+fileUri);
			log.info("File path pasado a URI (protocol) :"+fileUri.getScheme()+", si no es file o es null usa /");
			
		 	
			/*
			 * Si es schema file:// se pueden manejar ficheros grandes usando un patch VFS
			 * Se usan strings para ubicar el fichero en filesystem
			 * Al PDFService el input/out string tiene que ir en formato no URI
			 */
			if(fileUri.getScheme() == null || fileUri.getScheme().equalsIgnoreCase("file"))
			{
				// cogemos el path en formato no URI
				String inputPath = FileUtil.getLocalPathFromURI(fileUri);
				
				File file = new File(pdfParameter.getPath());
				outputPath = PreferencesUtil.getOutputDir(file) + File.separatorChar + PreferencesUtil.getOutputName(file.getName()) + "." + FileUtil.EXTENSION_PDF;
				
				PdfService.sign(inputPath, outputPath, signaturePreferences, ownerPassword);
					
				
			/*
			 * Si no es schema file:// se manipulan los ficheros con streams 
			 * y no se pueden tratar grandes tamaños ya que se hace un volcado a memoria del stream para su manipulación 
			 */
			}else 
			{
				String sss = PreferencesUtil.getOutputNameFromCompletePath(pdfParameter.getPath());
				outputPath = PreferencesUtil.getOutputDir(pdfParameter.getPath()) + "/" + sss + "." + FileUtil.EXTENSION_PDF;
				try {
					PdfService.sign(is, FileUtil.getOutputStreamFromURI(outputPath), signaturePreferences, ownerPassword);
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					log.error("error creando el OutputStream asociado al fichero de entrada " + FileUtil.getLocalPathFromURI(pdfParameter.getPath()) , e);
					throw new IOException(e);
				}
			}
						
			// TODO centralizar esto
			// actualizo la entrada de la tabla
			pdfParameter.setPath(outputPath);
			pdfParameter.setSignatures(null);
			String mimeType = FileUtil.getMimeType(outputPath);
			pdfParameter.setMimeType(mimeType);

			// validar
			if (PreferencesUtil.getPreferences().getBoolean(PreferencesUtil.AUTO_VALIDATE)) {
				ValidateController.validate(pdfParameter);
			}

			// mensaje
			String m = MessageFormat.format(LanguageUtil.getLanguage().getString("info.document.signed"), FileUtil.getLocalPathFromURI(pdfParameter.getPath()));
			Display.getDefault().syncExec(new ProgressWriter(ProgressWriter.INFO, m));

		} catch (PdfSignatureException e) {

			String m = MessageFormat.format(LanguageUtil.getLanguage().getString("error.certificate.sign.unexpected"),
					FileUtil.getLocalPathFromURI(pdfParameter.getPath()), e.getMessage());
			log.error(m, e);
			Display.getDefault().syncExec(new ProgressWriter(ProgressWriter.ERROR, m));
		}
	}

	private static void signDetached(DocumentInfo pdfParameter, KsSignaturePreferences ksSignaturePreferences) throws OverwritingException,
			IOException, OCSPUnknownUrlException, CertificateExpiredException, CertificateNotYetValidException, RevokedException,
			OCSPCoreException, ConnectionException, OCSPIssuerRequiredException {

		try {
			// firmar
			byte[] bytes = signXades(pdfParameter.getPath(), ksSignaturePreferences);

			File inputFile = new File(pdfParameter.getPath());
			
			String outputDir = PreferencesUtil.getOutputDir(inputFile);
			
			/*
			 * si el dir está URIEncodeado, encodeamos el nombre, si no lo decodeamos
			 * esto ocurre porque el path de salida puede ser el definido por el usuario (decoded) 
			 * o el del fichero per se (encoded)
			 */
			String outputName = PreferencesUtil.getOutputName(inputFile.getName());
			
			if (FileUtil.isURIEncoded(outputDir)) {
				if (!FileUtil.isURIEncoded(outputName)) {
					outputName = URIEncoder.encode(outputName, "utf-8");
				}
			} else {
				outputName = URIUtil.decode(outputName, "utf-8");
			}
			
			String outputPath = null;
			
			if (pdfParameter.getMimeType() != null && pdfParameter.getMimeType().equals(FileUtil.MIMETYPE_SAR)) {
				outputPath = outputDir + File.separatorChar + outputName + "." + FileUtil.EXTENSION_SAR;

			} else {

				// sar
				if (PreferencesUtil.getPreferences().getBoolean(PreferencesUtil.XADES_ARCHIVE)) {
					outputPath = outputDir + File.separatorChar	+ outputName + "." + FileUtil.EXTENSION_SAR;
				// xml
				} else {
					outputPath = outputDir + File.separatorChar	+ outputName + "." + FileUtil.EXTENSION_XML;
					
					/*
					 * unsupported
					 * validamos que siendo firma XML, el archivo origen y destino no sea el mismo
					 * // TODO esto se podría hacer con una firma enveloped/ing
					 * @see SignController (core; desde consola no se pueden hacer firmas sar) + SignController (desktop)
					 */
					if (inputFile.getPath().equals(outputPath)){
						log.error("Se está intentando firmar un fichero XML sobre si mismo. Modifique las preferencias para que esto no ocurra (firma detached sar, sufijo o directorio destino diferente)");
						throw new OverwritingException();
					}
				}
			}

			FileUtil.bytesToFile(bytes, outputPath);

			// TODO centralizar esto
			// actualizo la entrada de la tabla
			pdfParameter.setPath(outputPath);
			pdfParameter.setSignatures(null);
			String mimeType = FileUtil.getMimeType(outputPath);
			pdfParameter.setMimeType(mimeType);

			// validar
			if (PreferencesUtil.getPreferences().getBoolean(PreferencesUtil.AUTO_VALIDATE)) {
				ValidateController.validate(pdfParameter);
			}

			// mensaje
			String m = MessageFormat.format(LanguageUtil.getLanguage().getString("info.document.signed"), FileUtil.getLocalPathFromURI(pdfParameter.getPath()));
			Display.getDefault().syncExec(new ProgressWriter(ProgressWriter.INFO, m));

		} catch (XadesSignatureException e) {

			String m = MessageFormat.format(LanguageUtil.getLanguage().getString("error.certificate.sign.unexpected"),
					FileUtil.getLocalPathFromURI(pdfParameter.getPath()), e.getMessage());
			log.error(m, e);
			Display.getDefault().syncExec(new ProgressWriter(ProgressWriter.ERROR, m));
		}
	}

	private static byte[] signXades(String documentPath, KsSignaturePreferences ksSignaturePreferences) throws XadesSignatureException,
			OCSPUnknownUrlException, CertificateExpiredException, CertificateNotYetValidException, RevokedException, OCSPCoreException,
			ConnectionException, OCSPIssuerRequiredException {

		XadesSignaturePreferences signaturePreferences = new XadesSignaturePreferences();
		signaturePreferences.setKsSignaturePreferences(ksSignaturePreferences);
		signaturePreferences.setType(XadesSignaturePreferences.Type.Detached);
		signaturePreferences.setGenerateArchiver(PreferencesUtil.getPreferences().getBoolean(PreferencesUtil.XADES_ARCHIVE));
		signaturePreferences.setKsCache(PreferencesUtil.getCacheKeystoreComplete());

		String tsurl = null;
		String tsaOcspUrl = null;
		if (PreferencesUtil.getPreferences().getBoolean(PreferencesUtil.SIGN_TS_ENABLE) == true) {
			tsurl = PreferencesUtil.getTimestampPreferences().get(PreferencesUtil.getPreferences().getString(PreferencesUtil.SIGN_TS_TSA)).getUrl();
			tsaOcspUrl = PreferencesUtil.getTimestampPreferences().get(PreferencesUtil.getPreferences().getString(PreferencesUtil.SIGN_TS_TSA)).getOcspUrl();
		}

		signaturePreferences.setTimestampUrl(tsurl);
		signaturePreferences.setTimestampOcspUrl(tsaOcspUrl);
		signaturePreferences.setTimestampUser(null);
		signaturePreferences.setTimestampPassword(null);
		

		boolean addOCSP = PreferencesUtil.getPreferences().getBoolean(PreferencesUtil.SIGN_OCSP_ENABLE);
		signaturePreferences.setAddOCSP(addOCSP);

		StatisticsUtil.log(StatisticsUtil.KEY_SIGN_OCSP, addOCSP + "");
		log.info("ocsp enable: " + addOCSP);
		
		boolean xlOcspAddAll = PreferencesUtil.getPreferences().getBoolean(PreferencesUtil.XADES_XL_OCSP_ADD_ALL);
		signaturePreferences.setXlOcspAddAll(xlOcspAddAll);

		// firmar
		byte[] bytes = XadesService.signArchiver(documentPath, signaturePreferences);

		return bytes;
	}

}

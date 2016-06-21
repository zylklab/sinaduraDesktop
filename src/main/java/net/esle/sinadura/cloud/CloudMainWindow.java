package net.esle.sinadura.cloud;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Shell;

import net.esle.sinadura.core.util.FileUtil;
import net.esle.sinadura.gui.controller.SignControllerHelper;
import net.esle.sinadura.gui.exceptions.FileNotValidException;
import net.esle.sinadura.gui.model.DocumentInfo;
import net.esle.sinadura.gui.util.DocumentInfoUtil;
import net.esle.sinadura.gui.util.LanguageUtil;
import net.esle.sinadura.gui.util.LoggingDesktopController;
import net.esle.sinadura.gui.util.PreferencesUtil;
import net.esle.sinadura.gui.util.PropertiesUtil;
import net.esle.sinadura.protocol.exceptions.CloudAppException;
import net.esle.sinadura.protocol.exceptions.RestServiceException;
import net.esle.sinadura.protocol.model.ConfigVO;
import net.esle.sinadura.protocol.model.DocumentVO;
import net.esle.sinadura.protocol.services.ServiceManager;
import net.esle.sinadura.protocol.utils.DesktopUtils;
import net.esle.sinadura.protocol.utils.HttpUtils;

public class CloudMainWindow {

	private static Log log = LogFactory.getLog(CloudMainWindow.class);
	
	private static Shell shell;
	
	public static void initCloud(Shell mainShell, String[] args)  {

		shell = mainShell;
    	
		// args parse
		String protURL = args[0];
		String url = DesktopUtils.getServiceURL(protURL);
		String token = DesktopUtils.getToken(protURL);
		if (url == null || token == null){
			// revisar si Runtime esta ok o Exception
			throw new RuntimeException("Parameters are malformed '" + protURL + "'");
		}
		
		log.info("protocol, url: " + url);
		log.info("protocol, token: " + token);
		
		signCloud(shell, url, token);
		
		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {
				shell.dispose();
			}
		});
	}
	
	
	private static void signCloud(Shell shell, String url, String token) {
		
		ServiceManager serviceManager = new ServiceManager(url, token);
		
		try {
			
			try {
				
				// TODO hardcode
				serviceManager.setStatus("SIGN_STARTED");
			
				// obtenemos config del server
				ConfigVO config = serviceManager.getConfig();
				log.info("config: " + config);
				
				// Se sobreescriben en memoria las preferencias que asi lo requieran.
				// Tendria mas sentido hacer una implementacion de lectura de preferencias alternativa.

				String localeConfig = config.getProperties().get("locale");

				if (localeConfig != null) {
					try {
						
						Locale locale = LanguageUtil.getLocale(localeConfig);
						// en este caso quizas no sea necesario cambiar el valor de la preferencia en Runtime (con el
						// reloadLanguage es sufiente), pero lo hago aun asi por si acaso.
						PreferencesUtil.setRuntimePreference(PreferencesUtil.IDIOMA, locale.toString());
						LanguageUtil.reloadLanguage(locale.toString());
						
					} catch (RuntimeException e) {
						// si no se puede instanciar un locale es que el formato no es correto
					}
				}

				// General
				PreferencesUtil.setRuntimePreference(PreferencesUtil.OUTPUT_AUTO_ENABLE, true);
				PreferencesUtil.setRuntimePreference(PreferencesUtil.AUTO_VALIDATE, false);
				// Xades
				PreferencesUtil.setRuntimePreference(PreferencesUtil.XADES_ARCHIVE, false);
				// Pdf
				// TODO revisar. No se si tiene sentido sobreescribir estos valores. De momento los pongo a "true" por defecto en la instalacion.
//				Map<String, PdfProfile> availableProfiles = PreferencesUtil.getPdfProfiles();
//				PdfProfile defaultPdfProfile = availableProfiles.get(PreferencesUtil.getPreferences().getString(PreferencesUtil.PDF_PROFILE_SELECTED_NAME));
//				defaultPdfProfile.setAskPosition(true); 
//				defaultPdfProfile.setAskProperties(true); // este no tiene sentido ponerlo siempre a true


				
				
				// INPUTS				
				createTsFolder(token);

				List<DocumentVO> documentVOList = serviceManager.getDocuments();

				List<File> fileList = new ArrayList<File>();
				
				// copiamos documents a FS
				for (DocumentVO documentVO : documentVOList) {
					
					if (documentVO.getType().equals("url")) {
						
						InputStream is = HttpUtils.getHttp(documentVO.getUrl());
						File tmpFolderFile = new File(PropertiesUtil.TMP_FOLDER_PATH); 
						String path = tmpFolderFile.getAbsolutePath() + File.separator + token + File.separator + documentVO.getName();
						OutputStream os = new FileOutputStream(path);
						IOUtils.copy(is, os);
						fileList.add(new File(path));
						
					} else {
						throw new CloudAppException("document type not supported");
					}
					
				}

				List<DocumentInfo> documentInfoList = DocumentInfoUtil.fileToDocumentInfo(fileList);
				
				// firma con Sinadura
				SignControllerHelper.signDocuments(documentInfoList, shell);
				
				// TODO revisar el tratamiento de Errores (LoggingDesktopController)
				List<String> errorList = LoggingDesktopController.getErrorList();
				if (errorList != null && errorList.size() > 0) {
					
					throw new CloudAppException(errorList.get(0));
					
				} else {
					
					// es un poco cutre, pero de momento la asociacion (entre DocumentInfo e DocumentVO) la hago por indice
					for (int i = 0; i < documentInfoList.size(); i++) {
						
						DocumentInfo documentInfo = documentInfoList.get(i);
						DocumentVO documentVO = documentVOList.get(i);
						
						ByteArrayOutputStream os = new ByteArrayOutputStream();
						InputStream is = FileUtil.getInputStreamFromURI(documentInfo.getPath());
						IOUtils.copy(is, os);
	
						// se suben al server
						serviceManager.addSignatureFile(documentVO.getId(), os.toByteArray());
					}
					
					// se borra la carpeta temporal
					removeTsFolder(token);
					
					// TODO hardcode
					serviceManager.setStatus("SIGN_EXECUTED");
				}
	
			} catch (RestServiceException e) {
				throw new CloudAppException(e);
			} catch (FileNotValidException e) {
				throw new CloudAppException(e);
			} catch (FileNotFoundException e) {
				throw new CloudAppException(e);
			} catch (IOException e) {
				throw new CloudAppException(e);
			} catch (URISyntaxException e) {
				throw new CloudAppException(e);
			} catch (RuntimeException e) {
				throw new CloudAppException(e);
			}
			
		} catch (CloudAppException e) {
			
			log.error("Code: " + e.getCode(), e);
			try {
				serviceManager.setError(e.getCode(), e.getMessage());
				
			} catch (RestServiceException e1) {
				throw new RuntimeException(e1);
			}
		}

	}
	
	private static void createTsFolder(String uuid) {

		File tmpFolderFile = new File(PropertiesUtil.TMP_FOLDER_PATH);
		File tsFolderFile = new File(tmpFolderFile.getAbsolutePath() + File.separator + uuid);
		if (!tsFolderFile.exists()) {
			boolean success  = tsFolderFile.mkdirs();
			if (!success) {
				throw new RuntimeException("error creating ts directory");
			}
		}
		
	}
	
	private static void removeTsFolder(String uuid) {

		try {
			File tmpFolderFile = new File(PropertiesUtil.TMP_FOLDER_PATH);
			FileUtils.deleteDirectory(new File(tmpFolderFile.getAbsolutePath() + File.separator + uuid));
		} catch (IOException e) {
			log.warn("could not delete ts directory");
			// nothing
		}

	}
	
}


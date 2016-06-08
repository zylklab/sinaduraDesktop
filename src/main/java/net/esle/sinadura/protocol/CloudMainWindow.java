package net.esle.sinadura.protocol;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Shell;

import net.esle.sinadura.core.keystore.KeyStoreBuilderFactory;
import net.esle.sinadura.core.keystore.KeyStoreBuilderFactory.KeyStoreTypes;
import net.esle.sinadura.core.util.FileUtil;
import net.esle.sinadura.gui.controller.SignControllerHelper;
import net.esle.sinadura.gui.exceptions.FileNotValidException;
import net.esle.sinadura.gui.model.DocumentInfo;
import net.esle.sinadura.gui.util.DocumentInfoUtil;
import net.esle.sinadura.gui.util.LanguageUtil;
import net.esle.sinadura.gui.util.LoggingDesktopController;
import net.esle.sinadura.gui.util.PdfProfile;
import net.esle.sinadura.gui.util.PreferencesDefaultUtil;
import net.esle.sinadura.gui.util.PreferencesUtil;
import net.esle.sinadura.gui.util.PropertiesUtil;
import net.esle.sinadura.gui.util.StatisticsUtil;
import net.esle.sinadura.gui.view.main.PdfSignaturePropertiesDialog;
import net.esle.sinadura.gui.view.preferences.PdfProfilePreferencesDialog;
import net.esle.sinadura.protocol.exceptions.ProtocolAppException;
import net.esle.sinadura.protocol.exceptions.RestServiceException;
import net.esle.sinadura.protocol.model.ConfigVO;
import net.esle.sinadura.protocol.model.InputVO;
import net.esle.sinadura.protocol.services.ServiceManager;
import net.esle.sinadura.protocol.utils.DesktopUtils;
import net.esle.sinadura.protocol.utils.HttpUtils;

public class CloudMainWindow {

	private static Log log = LogFactory.getLog(CloudMainWindow.class);
	
	private static Shell shell;
	
	public static void initCloud(Shell mainShell, String[] args)  {

		shell = mainShell;
    	log.info("desktop-protocol | input args: " + Arrays.asList(args));
    	
		// args parse
		String protURL = args[0];
		String url = DesktopUtils.getServiceURL(protURL);
		String token = DesktopUtils.getToken(protURL);
		if (url == null || token == null){
			// revisar si Runtime esta ok o Exception
			throw new RuntimeException("Parameters are malformed '" + protURL + "'");
		}
		
		log.info("desktop-protocol | url: " + url);
		log.info("desktop-protocol | token: " + token);
		
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
				
				String locale = config.getProperties().get("locale");
				// TODO validar locale
				
				// Se sobreescriben en memoria las preferencias que asi lo requieran.
				// Tendria mas sentido hacer una implementacion de lectura de preferencias alternativa.
				// **********
				// General
				PreferencesUtil.getPreferences().setValue(PreferencesUtil.IDIOMA, locale);
				PreferencesUtil.getPreferences().setValue(PreferencesUtil.OUTPUT_AUTO_ENABLE, true);
				PreferencesUtil.getPreferences().setValue(PreferencesUtil.AUTO_VALIDATE, false);
				// Xades
				PreferencesUtil.getPreferences().setValue(PreferencesUtil.XADES_ARCHIVE, false);
				// Pdf
				// TODO revisar. No se si tiene sentido sobreescribir estos valores. De momento los pongo a "true" por defecto en la instalacion.
//				Map<String, PdfProfile> availableProfiles = PreferencesUtil.getPdfProfiles();
//				PdfProfile defaultPdfProfile = availableProfiles.get(PreferencesUtil.getPreferences().getString(PreferencesUtil.PDF_PROFILE_SELECTED_NAME));
//				defaultPdfProfile.setAskPosition(true); 
//				defaultPdfProfile.setAskProperties(true); // este no tiene sentido ponerlo siempre a true

				// reload language
				LanguageUtil.reloadLanguage();
				
				
				// INPUTS				
				createTsFolder(token);

				List<InputVO> inputVOList = serviceManager.getInputs();

				List<File> fileList = new ArrayList<File>();
				
				// copiamos inputs a FS
				for (InputVO inputVO : inputVOList) {
					
					if (inputVO.getType().equals("url")) {
						
						InputStream is = HttpUtils.getHttp(inputVO.getUrl());
						File tmpFolderFile = new File(PropertiesUtil.TMP_FOLDER_PATH); 
						String path = tmpFolderFile.getAbsolutePath() + File.separator + token + File.separator + inputVO.getName();
						OutputStream os = new FileOutputStream(path);
						IOUtils.copy(is, os);
						fileList.add(new File(path));
						
					} else {
						throw new ProtocolAppException("input type not supported");
					}
					
				}

				List<DocumentInfo> documentInfoList = DocumentInfoUtil.fileToDocumentInfo(fileList);
				
				// firma con Sinadura
				SignControllerHelper.signDocuments(documentInfoList, shell);
				
				// TODO revisar el tratamiento de Errores (LoggingDesktopController)
				List<String> errorList = LoggingDesktopController.getErrorList();
				if (errorList != null && errorList.size() > 0) {
					
					throw new ProtocolAppException(errorList.get(0));
					
				} else {
					
					// es un poco cutre, pero de momento la asociacion (entre DocumentInfo e InputVO) la hago por indice
					for (int i = 0; i < documentInfoList.size(); i++) {
						
						DocumentInfo documentInfo = documentInfoList.get(i);
						InputVO inputVO = inputVOList.get(i);
						
						ByteArrayOutputStream os = new ByteArrayOutputStream();
						InputStream is = FileUtil.getInputStreamFromURI(documentInfo.getPath());
						IOUtils.copy(is, os);
	
						// se suben al server
						serviceManager.addSignatureFile(inputVO.getId(), os.toByteArray());
					}
					
					// se borra la carpeta temporal
					removeTsFolder(token);
					
					// TODO hardcode
					serviceManager.setStatus("SIGN_EXECUTED");
				}
	
			} catch (RestServiceException e) {
				throw new ProtocolAppException(e);
			} catch (FileNotValidException e) {
				throw new ProtocolAppException(e);
			} catch (FileNotFoundException e) {
				throw new ProtocolAppException(e);
			} catch (IOException e) {
				throw new ProtocolAppException(e);
			} catch (URISyntaxException e) {
				throw new ProtocolAppException(e);
			} catch (RuntimeException e) {
				throw new ProtocolAppException(e);
			}
			
		} catch (ProtocolAppException e) {
			
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


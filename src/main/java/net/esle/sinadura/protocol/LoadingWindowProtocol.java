package net.esle.sinadura.protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.xml.utils.URI.MalformedURIException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import net.esle.sinadura.core.exceptions.ConnectionException;
import net.esle.sinadura.core.util.FileUtil;
import net.esle.sinadura.ee.EEModulesManager;
import net.esle.sinadura.ee.exceptions.EEModuleGenericException;
import net.esle.sinadura.ee.exceptions.EEModuleNotFoundException;
import net.esle.sinadura.ee.interfaces.ProxyEEModule;
import net.esle.sinadura.gui.controller.SignControllerHelper;
import net.esle.sinadura.gui.exceptions.FileNotValidException;
import net.esle.sinadura.gui.model.DocumentInfo;
import net.esle.sinadura.gui.model.LoggerMessage;
import net.esle.sinadura.gui.model.LoggerMessage.Level;
import net.esle.sinadura.gui.util.DocumentInfoUtil;
import net.esle.sinadura.gui.util.ImagesUtil;
import net.esle.sinadura.gui.util.LanguageUtil;
import net.esle.sinadura.gui.util.PreferencesUtil;
import net.esle.sinadura.gui.util.PropertiesUtil;
import net.esle.sinadura.gui.util.StatisticsUtil;
import net.esle.sinadura.gui.util.VersionUtil;
import net.esle.sinadura.protocol.exceptions.ProtocolAppException;
import net.esle.sinadura.protocol.exceptions.RestServiceException;
import net.esle.sinadura.protocol.model.ConfigVO;
import net.esle.sinadura.protocol.model.InputVO;
import net.esle.sinadura.protocol.services.ServiceManager;
import net.esle.sinadura.protocol.utils.DesktopUtils;
import net.esle.sinadura.protocol.utils.HttpUtils;

public class LoadingWindowProtocol {

	private static Log log = LogFactory.getLog(LoadingWindowProtocol.class);
	
	private String[] args;

	public LoadingWindowProtocol(String[] args) throws FileNotValidException, FileSystemException, MalformedURIException {
		this.args = args;
		init();
	}

	public void init() throws FileNotValidException, FileSystemException, MalformedURIException {

		Display.setAppName(PropertiesUtil.APPLICATION_NAME);
		Display display = new Display();

		Shell shell = new Shell(SWT.APPLICATION_MODAL | SWT.BORDER);
		shell.setText(LanguageUtil.getLanguage().getString("loading.windowtitle"));
		shell.setImage(new Image(shell.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.SINADURA_LOGO_IMG)));

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.verticalSpacing = 10;
		gridLayout.marginTop = 10;
		shell.setLayout(gridLayout);

		shell.setSize(new Point(400, 250));

		Monitor primary = display.getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 3;
		shell.setLocation(x, y);

		Label labelImage = new Label(shell, SWT.NONE);
		GridData gdImage = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		gdImage.minimumHeight = 0;
		gdImage.grabExcessVerticalSpace = true;
		labelImage.setLayoutData(gdImage);

		Image imageSinadura = new Image(shell.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.SINADURA_FULL_IMG));
		labelImage.setImage(imageSinadura);

		Label messages = new Label(shell, SWT.NONE);
		messages.setLayoutData(new GridData(GridData.CENTER));

		ProgressBar progressBar = new ProgressBar(shell, SWT.INDETERMINATE);
		progressBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		List<LoggerMessage> listMessages = new ArrayList<LoggerMessage>();

		new ThreadOperations(shell, messages, listMessages).start();

		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		// *****************************************
		// START 
		// *****************************************
		
		// en sinadura estaba esto: 
		// new MainWindow(display, );
		
		// ejemplo de consume de prefs en Sinadura
		PreferencesUtil.getPreferences().getString(PreferencesUtil.CERT_TYPE);
		
		
		
		
		
    	log.info("desktop-protocol | input args: " + Arrays.asList(args));
    	
    	/*
    	 * params
    	 *  - url servidor
  		 * - token
  		 * 
  		 * @see /desktop-protocol/src/com/izenpe/desktop/protocol/test/TestService.java
  		 * ej1: idazki://idazki-test-external.zylk.net:8080/idazki-protocol-services/rest/api/v1/h/1d739c32-6a9b-42ec-a7b5-f7a5341ecbe4
  		 * ej2: idazki://idazki-test-external.zylk.net:8080/idazki-protocol-services/rest/api/v1/s/1d739c32-6a9b-42ec-a7b5-f7a5341ecbe4
    	 */

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
		
		idazkiDesktopManagerExecuteAction(url, token, display);
		
		
		// *****************************************
		// END
		// *****************************************

		display.dispose();
	}
	
	
	private void idazkiDesktopManagerExecuteAction(String url, String token, Display display) {
		
		ServiceManager serviceManager = new ServiceManager(url, token);
		
		try {
			
			try {
				
				// TODO hardcode
				serviceManager.setStatus("SIGN_STARTED");
			
				// obtenemos config del server
				ConfigVO config = serviceManager.getConfig();
				log.info("config: " + config);
				
				// TODO config params

				List<InputVO> inputVOList = serviceManager.getInputs();

				List<File> fileList = new ArrayList<File>();
				
				// copiamos inputs a FS
				for (InputVO inputVO : inputVOList) {
					
					if (inputVO.getType().equals("url")) {
						
						InputStream is = HttpUtils.getHttp(inputVO.getUrl());
						String path = "/home/alfredo/Escritorio/" + inputVO.getName();
						OutputStream os = new FileOutputStream(path);
						IOUtils.copy(is, os);
						fileList.add(new File(path));
						
					} else {
						throw new ProtocolAppException("input type not supported");
					}
					
				}

				List<DocumentInfo> documentInfoList = DocumentInfoUtil.fileToDocumentInfo(fileList);
				
				// firma con Sinadura
				SignControllerHelper.signDocuments(documentInfoList, display.getActiveShell());
				
				// es un poco cutre, pero de momento la asociacion (entre DocumentInfo e InputVO) la hago por indice
				for (int i = 0; i < documentInfoList.size(); i++) {
					
					DocumentInfo documentInfo = documentInfoList.get(i);
					InputVO inputVO = inputVOList.get(i);
					
					ByteArrayOutputStream os =  new ByteArrayOutputStream();
					InputStream is = FileUtil.getInputStreamFromURI(documentInfo.getPath());
					IOUtils.copy(is, os);

					// se suben al server
					serviceManager.addSignatureFile(inputVO.getUuid(), os.toByteArray());
					
					// TODO borrar tmp file, y doc file
				}
				
				// TODO hardcode
				serviceManager.setStatus("SIGN_EXECUTED");
	
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
				serviceManager.setError(e.getCode());
			} catch (RestServiceException e1) {
				throw new RuntimeException(e1);
			}
		}


	}

	
}



class ThreadOperations extends Thread {
	
	private static final Log log = LogFactory.getLog(ThreadOperations.class);

	private Shell shell;
	private Label messages;
	private List<LoggerMessage> listMessages;

	public ThreadOperations(Shell shell, Label messages, List<LoggerMessage> listMessages) {
		this.shell = shell;
		this.messages = messages;
		this.listMessages = listMessages;
	}

	@Override
	public void run() {
		
		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {
				messages.setText(LanguageUtil.getLanguage().getString("loading.checking.conection"));
				messages.pack();
			}
		});
		
		// ee (proxy)
		boolean proxyEnabled = Boolean.valueOf(PropertiesUtil.getConfiguration().getProperty(PropertiesUtil.PROXY_ENABLED));
		if (proxyEnabled && PreferencesUtil.getPreferences().getBoolean(PreferencesUtil.PROXY_SYSTEM)) {
			try {
				ProxyEEModule proxyUtil = EEModulesManager.getProxyModule();
				proxyUtil.configureProxy(PreferencesUtil.getPreferences().getString(PreferencesUtil.PROXY_USER), PreferencesUtil
						.getPreferences().getString(PreferencesUtil.PROXY_PASS));

			} catch (EEModuleNotFoundException e) {
				listMessages.add(new LoggerMessage(Level.INFO, MessageFormat.format(
						LanguageUtil.getLanguage().getString("ee.proxy.disabled"), "proxy")));

			} catch (EEModuleGenericException e) {
				log.error(e);
			}
		}
		

		// estadisticas
		StatisticsUtil.log(StatisticsUtil.KEY_SO, System.getProperty("os.name"));
		StatisticsUtil.log(StatisticsUtil.KEY_SO_VERSION, System.getProperty("os.version"));
		StatisticsUtil.log(StatisticsUtil.KEY_SO_ARCHITECTURE, System.getProperty("os.arch"));
		StatisticsUtil.log(StatisticsUtil.KEY_SINADURA_VERSION, PropertiesUtil.getConfiguration().getProperty(
				PropertiesUtil.APPLICATION_VERSION_STRING));
		StatisticsUtil.log(StatisticsUtil.KEY_SO_LOCALE_COUNTRY, Locale.getDefault().getCountry());
		StatisticsUtil.log(StatisticsUtil.KEY_SO_LOCALE_LANGUAGE, Locale.getDefault().getLanguage());
		StatisticsUtil.log(StatisticsUtil.KEY_JAVA_VENDOR, System.getProperty("java.vendor"));
		StatisticsUtil.log(StatisticsUtil.KEY_JAVA_VERSION, System.getProperty("java.version"));


		// check new version
		boolean checkNewVersion = Boolean.valueOf(PropertiesUtil.getConfiguration().getProperty(PropertiesUtil.VERSION_CHECK_UPDATE_ENABLED));
		if (checkNewVersion) {
			try {
				if (VersionUtil.isThereApplicationNewVersion()) {
					
					listMessages.add(new LoggerMessage(Level.INFO, MessageFormat.format(
							LanguageUtil.getLanguage().getString("loading.new.version"),
							PropertiesUtil.getConfiguration().getProperty(PropertiesUtil.SINADURA_MAIN_URL))));
				}
			} catch (ConnectionException e) {
				log.error("", e);
				String m = MessageFormat
						.format(LanguageUtil.getLanguage().getString("error.certificate.connection"), e.getCause().toString());
				listMessages.add(new LoggerMessage(Level.ERROR, m));
			}
		}

		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {
				shell.dispose();
			}
		});
	}
}
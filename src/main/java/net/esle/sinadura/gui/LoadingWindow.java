package net.esle.sinadura.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.esle.sinadura.core.exceptions.ConnectionException;
import net.esle.sinadura.core.util.ProxyUtil;
import net.esle.sinadura.core.validate.OcspUtil;
import net.esle.sinadura.gui.model.LoggerMessage;
import net.esle.sinadura.gui.model.LoggerMessage.Level;
import net.esle.sinadura.gui.util.ImagesUtil;
import net.esle.sinadura.gui.util.LanguageUtil;
import net.esle.sinadura.gui.util.PreferencesUtil;
import net.esle.sinadura.gui.util.PropertiesUtil;
import net.esle.sinadura.gui.util.StatisticsUtil;
import net.esle.sinadura.gui.util.VersionUtil;
import net.esle.sinadura.gui.view.main.MainWindow;

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

public class LoadingWindow {

	private String[] args;

	public LoadingWindow(String[] args) throws FileSystemException, MalformedURIException {
		this.args = args;
		init();
	}

	public void init() throws FileSystemException, MalformedURIException {

		Display.setAppName(PropertiesUtil.APPLICATION_NAME);
		Display display = new Display();

		Shell shell = new Shell(SWT.APPLICATION_MODAL | SWT.BORDER);
		shell.setText(LanguageUtil.getLanguage().getString("loading.windowtitle"));
		shell.setImage(new Image(shell.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.SINADURA_LOGO_IMG)));

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

		Image imageSinadura = new Image(shell.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.SINADURA_FULL_IMG));
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

		new MainWindow(display, listMessages, args);

		display.dispose();
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
		
		// init proxy
		if (PreferencesUtil.getPreferences().getBoolean(PreferencesUtil.PROXY_SYSTEM)) {
			ProxyUtil.configureProxy(PreferencesUtil.getPreferences().getString(PreferencesUtil.PROXY_USER), PreferencesUtil
					.getPreferences().getString(PreferencesUtil.PROXY_PASS));
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

		// creacion de carpetas
		// base
		File f = new File(PropertiesUtil.USER_BASE_PATH);
		if (!f.exists()) {
			f.mkdir();
		}

		// log
		f = new File(PropertiesUtil.LOG_FOLDER_PATH);
		if (!f.exists()) {
			f.mkdir();
		}

		// statistics
		f = new File(PropertiesUtil.STATISTICS_FOLDER_PATH);
		if (!f.exists()) {
			f.mkdir();
		}

		// imagen por defecto para el sello
		try {
			f = new File(PropertiesUtil.DEFAULT_IMAGE_FILE_PATH);
			if (!f.exists()) {
				InputStream is = ClassLoader.getSystemResourceAsStream(ImagesUtil.EXT_SINADURA_LOGO_150_IMG);
				FileOutputStream fos = new FileOutputStream(PropertiesUtil.DEFAULT_IMAGE_FILE_PATH);
				IOUtils.copy(is, fos);
			}
		} catch (FileNotFoundException e) {
			log.error("", e);
		} catch (IOException e) {
			log.error("", e);
		}

		// check version
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

		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {
				shell.dispose();
			}
		});
	}
}
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
package net.esle.sinadura.gui.view.main;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import net.esle.sinadura.gui.events.ValidatePDFProgress;
import net.esle.sinadura.gui.exceptions.FileNotValidException;
import net.esle.sinadura.gui.model.DocumentInfo;
import net.esle.sinadura.gui.model.LoggerMessage;
import net.esle.sinadura.gui.util.DocumentInfoUtil;
import net.esle.sinadura.gui.util.ImagesUtil;
import net.esle.sinadura.gui.util.LanguageUtil;
import net.esle.sinadura.gui.util.LoggingDesktopController;
import net.esle.sinadura.gui.util.PreferencesUtil;
import net.esle.sinadura.gui.util.PropertiesUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.xml.utils.URI.MalformedURIException;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class MainWindow {

	private static Log log = LogFactory.getLog(MainWindow.class);
	private String path = null;
	private boolean directory = false;

	public MainWindow (Display display, List<LoggerMessage> list, String[] args) throws FileNotValidException, FileSystemException, MalformedURIException {
		initialize(display, list, args);
	}	

	public void initialize(Display display, List<LoggerMessage> list, String[] args) throws FileNotValidException, FileSystemException, MalformedURIException {

		Shell mainShell = new Shell(SWT.APPLICATION_MODAL | SWT.SHELL_TRIM);
		
		try {
			
			mainShell.setSize(new Point(950, 720));
			mainShell.setMaximized(true);
			mainShell.setText(PropertiesUtil.APPLICATION_NAME);
			mainShell.setImage(new Image(display,Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.SINADURA_LOGO_IMG)));
			GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 1;
			gridLayout.verticalSpacing = 15;
			gridLayout.marginTop = 5;
			mainShell.setLayout(gridLayout);
	
			
			// Crea el panel central donde se integraran los distintos paneles.
			Composite compositeCentro = new Composite(mainShell, SWT.NONE);
			GridData gd = new GridData();
			gd.horizontalAlignment = GridData.FILL;
			gd.verticalAlignment = GridData.FILL;
			gd.grabExcessHorizontalSpace = true;
			gd.grabExcessVerticalSpace = true;
			compositeCentro.setLayoutData(gd);
			GridLayout gridLayoutCentro = new GridLayout();
			gridLayoutCentro.numColumns = 1;
			compositeCentro.setLayout(gridLayoutCentro);
			
			// CONSOLA DE EVENTOS
			LoggingDesktopController.initialize(mainShell);
			for(LoggerMessage message : list) {
				if(message.getLevel().equals(LoggerMessage.Level.INFO)) {
					LoggingDesktopController.printInfo(message.getText());
				} else if (message.getLevel().equals(LoggerMessage.Level.ERROR)) {
					LoggingDesktopController.printError(message.getText());
				}
			}
			
			// INICIALIZA EL PANEL CON EL SIGNPANEL
			DocumentsPanel panelPDF = new DocumentsPanel(compositeCentro, SWT.NONE);			
			
			// MENU
			MainMenu menuPrincipal = new MainMenu(mainShell, compositeCentro, panelPDF.getTablePDF());
		
			mainShell.open();
			mainShell.layout();
		
			initializeTable(panelPDF, args);
			
			while (!mainShell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		
		} catch (RuntimeException e) {

			log.error("Error general de la aplicacion (runtime)", e);
			InfoDialog id = new InfoDialog(mainShell);
			id.open(LanguageUtil.getLanguage().getString("error.runtime"));
		}
		
	}
	
	
	/**
	 * Añade un documento si se ha metido como argumento su ruta por precarga
	 * 
	 * @param panelPDF
	 * @throws FileSystemException 
	 * @throws MalformedURIException 
	 */
	private void initializeTable(DocumentsPanel panelPDF, String[] args) throws FileNotValidException, FileSystemException, MalformedURIException {

		List<String> files = new ArrayList<String>();
		for (String filePath : args) {
			if (net.esle.sinadura.core.util.FileUtil.isFile(filePath)) 
			{
				files.add(filePath);
			}
		}

		if (files.size() > 0) {

			List<DocumentInfo> openWithDocuments = DocumentInfoUtil.fileToDocumentInfoFromUris(files);
			// TODO duplicado
			if (PreferencesUtil.getBoolean(PreferencesUtil.AUTO_VALIDATE)) {

				try {
					ProgressMonitorDialog progressMonitorDialog = new ProgressMonitorDialog(panelPDF.getTablePDF().getShell());
					progressMonitorDialog.run(true, true, new ValidatePDFProgress(openWithDocuments));

				} catch (InvocationTargetException e) {

					// runtimes - error inesperado
					String m = MessageFormat.format(LanguageUtil.getLanguage().getString("error.validation.unexpected"), e.getCause()
							.toString());
					log.error("", e);
					LoggingDesktopController.printError(m);

				} catch (InterruptedException e) {

					String m = LanguageUtil.getLanguage().getString("error.operacion_cancelada");
					LoggingDesktopController.printError(m);
					log.error(m);
				}

			}

			panelPDF.getTablePDF().addDocuments(openWithDocuments);
			panelPDF.getTablePDF().reloadTable();
		}
	}
	
}

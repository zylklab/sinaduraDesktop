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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;

import net.esle.sinadura.core.exceptions.ConnectionException;
import net.esle.sinadura.gui.events.AddDirectoryListener;
import net.esle.sinadura.gui.events.AddDocumentListener;
import net.esle.sinadura.gui.model.LoggerMessage;
import net.esle.sinadura.gui.model.LoggerMessage.Level;
import net.esle.sinadura.gui.util.DesktopUtil;
import net.esle.sinadura.gui.util.ImagesUtil;
import net.esle.sinadura.gui.util.LanguageUtil;
import net.esle.sinadura.gui.util.LoggingDesktopController;
import net.esle.sinadura.gui.util.PropertiesUtil;
import net.esle.sinadura.gui.util.VersionUtil;
import net.esle.sinadura.gui.view.documentation.AboutUsWindow;
import net.esle.sinadura.gui.view.documentation.CreditsWindow;
import net.esle.sinadura.gui.view.documentation.NewsWindow;
import net.esle.sinadura.gui.view.documentation.LicenciaWindow;
import net.esle.sinadura.gui.view.preferences.PreferencesManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class MainMenu {

	private static Log log = LogFactory.getLog(MainMenu.class);

	private Shell sShell = null;

	private Menu menuBar = null;
	private Menu fileMenu, ayudaMenu = null;
	private MenuItem fileMenuHeader, ayudaMenuHeader = null;

	private MenuItem preferenciasItem = null;
	private MenuItem salirItem = null;
	private MenuItem addDocument = null;
	private MenuItem addDirectory = null;

	private MenuItem documentacionItem = null;
	private MenuItem licenciaItem = null;
	private MenuItem noticiasItem = null;	
	private MenuItem creditsItem = null;

	private MenuItem acercaDeItem = null;

	private DocumentsTable documentsTable = null;

	public MainMenu(Shell sShell, Composite compositeCentro, DocumentsTable documentsTable) {

		this.sShell = sShell;
		this.documentsTable = documentsTable;
		initialize();
	}

	private void initialize() {

		// Creación del menu
		this.menuBar = new Menu(sShell, SWT.BAR);
		this.sShell.setMenuBar(this.menuBar);
		this.fileMenuHeader = new MenuItem(this.menuBar, SWT.CASCADE);
		this.fileMenu = new Menu(sShell, SWT.DROP_DOWN);
		this.fileMenuHeader.setMenu(this.fileMenu);
		this.addDocument = new MenuItem(this.fileMenu, SWT.PUSH);
		this.addDirectory = new MenuItem(this.fileMenu, SWT.PUSH);
		this.preferenciasItem = new MenuItem(this.fileMenu, SWT.PUSH);
		this.salirItem = new MenuItem(this.fileMenu, SWT.PUSH);
		this.ayudaMenuHeader = new MenuItem(this.menuBar, SWT.CASCADE);
		this.ayudaMenu = new Menu(sShell, SWT.DROP_DOWN);
		this.ayudaMenuHeader.setMenu(this.ayudaMenu);
		this.documentacionItem = new MenuItem(this.ayudaMenu, SWT.PUSH);
		this.noticiasItem = new MenuItem(this.ayudaMenu, SWT.PUSH);
		this.licenciaItem = new MenuItem(this.ayudaMenu, SWT.PUSH);
		this.creditsItem = new MenuItem(this.ayudaMenu, SWT.PUSH);
		this.acercaDeItem = new MenuItem(this.ayudaMenu, SWT.PUSH);

		this.addDocument.setImage(new Image(this.sShell.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.MENU_FILE)));
		this.addDirectory.setImage(new Image(this.sShell.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.MENU_ADDDIR)));
		this.salirItem.setImage(new Image(this.sShell.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.MENU_EXIT)));
		this.preferenciasItem.setImage(new Image(this.sShell.getDisplay(), ClassLoader
				.getSystemResourceAsStream(ImagesUtil.MENU_PREFERENCES)));
		this.licenciaItem.setImage(new Image(this.sShell.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.MENU_LICENSE)));
		this.creditsItem.setImage(new Image(this.sShell.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.CREDITS_IMG_PATH)));
		this.acercaDeItem.setImage(new Image(this.sShell.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.MENU_ABOUT)));
		this.documentacionItem.setImage(new Image(this.sShell.getDisplay(), ClassLoader
				.getSystemResourceAsStream(ImagesUtil.MENU_DOCUMENTATION)));
		this.noticiasItem.setImage(new Image(this.sShell.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.NEWS_IMG_PATH)));

		this.addDocument.addSelectionListener(new AddDocumentListener(documentsTable));
		this.addDirectory.addSelectionListener(new AddDirectoryListener(documentsTable));
		this.salirItem.addSelectionListener(new SalirItemListener());
		this.preferenciasItem.addSelectionListener(new PreferenciasItemListener());
		this.acercaDeItem.addSelectionListener(new AboutItemListener());
		this.licenciaItem.addSelectionListener(new LicenciaItemListener());
		this.documentacionItem.addSelectionListener(new DocumentacionItemListener());
		this.creditsItem.addSelectionListener(new CreditsItemListener());
		this.noticiasItem.addSelectionListener(new NoticiasItemListener());

		changeLanguage();

	}

	private void changeLanguage() {

		this.addDocument.setText(LanguageUtil.getLanguage().getString("section.sign.button.add.pdf"));
		this.addDirectory.setText(LanguageUtil.getLanguage().getString("section.sign.button.add.dir"));
		this.salirItem.setText(LanguageUtil.getLanguage().getString("submenu.exit"));
		this.fileMenuHeader.setText(LanguageUtil.getLanguage().getString("menu.file"));
		this.preferenciasItem.setText(LanguageUtil.getLanguage().getString("submenu.preferences"));
		this.ayudaMenuHeader.setText(LanguageUtil.getLanguage().getString("menu.help"));
		this.licenciaItem.setText(LanguageUtil.getLanguage().getString("submenu.license"));
		this.acercaDeItem.setText(LanguageUtil.getLanguage().getString("submenu.about"));
		this.documentacionItem.setText(LanguageUtil.getLanguage().getString("submenu.documentation"));
		this.creditsItem.setText(LanguageUtil.getLanguage().getString("submenu.credits"));		
		this.noticiasItem.setText(LanguageUtil.getLanguage().getString("submenu.news"));

	}

	/*
	 * private void disposeChildren(Composite composite) {
	 * 
	 * Control[] array = composite.getChildren(); for (int i = 0; i < array.length; i++) { array[i].dispose(); }
	 * 
	 * }
	 */

	// class SignPanelItemListener implements SelectionListener {
	//		
	// public void widgetSelected(SelectionEvent event) {
	//			
	// disposeChildren(compositeCentro);
	//
	// PanelPDF panelPDF = new PanelPDF(compositeCentro, SWT.NONE);
	//
	// compositeCentro.layout();
	// }
	//
	// public void widgetDefaultSelected(SelectionEvent event) {
	// widgetSelected(event);
	// }
	// }

	class PreferenciasItemListener implements SelectionListener {

		public void widgetSelected(SelectionEvent event) {

			PreferencesManager ventanaPreferencias = new PreferencesManager();
			ventanaPreferencias.abrirVentana(sShell);

			changeLanguage();
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}
	}

	// listener del item acerca de
	class AboutItemListener implements SelectionListener {

		public void widgetSelected(SelectionEvent event) {

			AboutUsWindow aboutUsDialog = new AboutUsWindow(sShell);
			aboutUsDialog.open();
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}
	}

	class CreditsItemListener implements SelectionListener {
		public void widgetSelected(SelectionEvent event) {
			CreditsWindow creditsDialog = new CreditsWindow(sShell);
			creditsDialog.open();
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}
	}
	
	class AyudaItemListener implements SelectionListener {

		public void widgetSelected(SelectionEvent event) {

			DesktopUtil.openDefaultBrowser(LanguageUtil.getLanguage().getString("help.url"));

		}

		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}
	}

	// listener del menu cerrar
	class SalirItemListener implements SelectionListener {
		public void widgetSelected(SelectionEvent event) {
			Display.getCurrent().dispose();
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}
	}

	// listener del menu de documentacion
	class DocumentacionItemListener implements SelectionListener {
		public void widgetSelected(SelectionEvent event) {

			DesktopUtil.openDefaultBrowser(PropertiesUtil.getConfiguration().getProperty(PropertiesUtil.SINADURA_DOCUMENTATION));
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}
	}

	// listener del menu de Licencia
	class LicenciaItemListener implements SelectionListener {
		public void widgetSelected(SelectionEvent event) {

			LicenciaWindow licenciaVentana = new LicenciaWindow(sShell);
			licenciaVentana.open();

		}

		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}
	}

	class NoticiasItemListener implements SelectionListener {

		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) {

			widgetSelected(arg0);
		}

		@Override
		public void widgetSelected(SelectionEvent arg0) {

			SyndFeedInput input = new SyndFeedInput();		
			try {
				URL url = new URL(PropertiesUtil.getConfiguration().getProperty(PropertiesUtil.NEWS));

				URLConnection connection = url.openConnection();
				connection.setConnectTimeout(3000);
				
				SyndFeed feed = input.build( new XmlReader(connection));

				NewsWindow newsDialog = new NewsWindow(sShell, feed);
				newsDialog.open();
				
			} catch (IllegalArgumentException e) {
				
				log.error("", e);
				String m = MessageFormat.format(LanguageUtil.getLanguage().getString("error.certificate.connection"), e.toString());
				LoggingDesktopController.printError(m);
				
			} catch (MalformedURLException e) {
				
				log.error("", e);
				String m = MessageFormat.format(LanguageUtil.getLanguage().getString("error.certificate.connection"), e.toString());
				LoggingDesktopController.printError(m);
				
			} catch (FeedException e) {
				
				log.error("", e);
				String m = MessageFormat.format(LanguageUtil.getLanguage().getString("error.certificate.connection"), e.toString());
				LoggingDesktopController.printError(m);
				
			} catch (IOException e) {
				
				log.error("", e);
				String m = MessageFormat.format(LanguageUtil.getLanguage().getString("error.certificate.connection"), e.toString());
				LoggingDesktopController.printError(m);
			}
		}
	}

}
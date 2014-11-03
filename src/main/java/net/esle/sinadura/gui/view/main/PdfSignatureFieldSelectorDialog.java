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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore.PasswordProtection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.esle.sinadura.core.model.PdfSignatureField;
import net.esle.sinadura.core.util.PdfUtil;
import net.esle.sinadura.gui.events.BotonCancelarListener;
import net.esle.sinadura.gui.model.PdfSignatureFieldGui;
import net.esle.sinadura.gui.util.ImagesUtil;
import net.esle.sinadura.gui.util.LanguageUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class PdfSignatureFieldSelectorDialog extends Dialog {

	private static Log log = LogFactory.getLog(PdfSignatureFieldSelectorDialog.class);
	
	private Shell sShell = null;

	private Label labelPosicion;
	private Text textRuta = null;
	private Button bottonAnterior = null;
	private Button bottonSiguiente = null;
	
	private Button bottonAceptar = null;
	private Button bottonCancelar = null;

	private PdfSignatureFieldSelectorPanel pdfSignatureFieldSelectorPanel = null;
	
	private String documentPath = null;
	private String stampPath = null;
	
	private int currentPage = 1;
	private int numberOfPages = 1;
	
	private PdfSignatureField returnSignatureField = null;
	
	private Map<String, PdfSignatureField> originalSignatureFieldsMap = null;
	private List<PdfSignatureFieldGui> signatureFields = null;
	private Map<Integer, List<PdfSignatureFieldGui>> signatureFieldsMapPerPage = null;
	private List<Integer> pages = null;
	private int pagesIndex = 0;
	
	/*
	 * Diferencial entre el sistema de medición de itext y el de esta pantalla. 
	 * 
	 * Size de la pagina en itext:
	 * 595 x 842 (A4??)
	 * 
	 * Size del la imagen con pdfbox (con 52 como dpi):
	 * 430 x 608
	 * 
	 * 595 / 430 = 1.38372093
	 * 842 / 608 = 1.384868421
	 * 
	 * No se porque el ratio es distinto entre X e Y (por la conversion de pdfBox?), asi que hay que manejar dos valores.
	 * 
	 */ 
	private static final Float RELACION_X = new Float(1.38372093);
	private static final Float RELACION_Y = new Float(1.384868421);
	

	public PdfSignatureFieldSelectorDialog(Shell parent, List<PdfSignatureField> pdfSignatureFields, String stampPath, String documentPath, PasswordProtection pwdProtection) throws IOException {

		super(parent);
		
		this.stampPath = stampPath;
		
		this.documentPath = documentPath;
		
		this.numberOfPages = PdfUtil.getNumberOfPages(documentPath, pwdProtection);
		
		this.originalSignatureFieldsMap = new HashMap<String, PdfSignatureField>();
		this.signatureFields = new ArrayList<PdfSignatureFieldGui>();
		
		for (PdfSignatureField psi : pdfSignatureFields) {
		
			PdfSignatureFieldGui pdfSignatureField = new PdfSignatureFieldGui();
			pdfSignatureField.setName(psi.getName());
			pdfSignatureField.setPage(psi.getPage());			
			pdfSignatureField.setStartX(Math.round(psi.getStartX() / RELACION_X));
			pdfSignatureField.setStartY(Math.round(psi.getStartY() / RELACION_Y));
			pdfSignatureField.setWidht(Math.round(psi.getWidht() / RELACION_X));
			pdfSignatureField.setHeight(Math.round(psi.getHeight() / RELACION_X));
			
			this.signatureFields.add(pdfSignatureField);
			
			this.originalSignatureFieldsMap.put(psi.getName(), psi);
		}

		// se agrupan en un map por pagina
		signatureFieldsMapPerPage = new HashMap<Integer, List<PdfSignatureFieldGui>>();
		pages = new ArrayList<Integer>();
		
		for (PdfSignatureFieldGui psi : signatureFields) {
			
			List<PdfSignatureFieldGui> list = signatureFieldsMapPerPage.get(psi.getPage());
			if (list == null) { // pagina nueva
				pages.add(psi.getPage());
				list = new ArrayList<PdfSignatureFieldGui>();
				signatureFieldsMapPerPage.put(psi.getPage(), list);	
			}
			
			list.add(psi);
		}
		
		// se ordena el listado de paginas
		Collections.sort(pages);
		
		pagesIndex = 0;
		
		this.currentPage = pages.get(pagesIndex);
	}

	
	public PdfSignatureField createSShell() throws IOException {
		
		Shell parent = getParent();
		sShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
		sShell.setImage(new Image(this.sShell.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.SINADURA_LOGO_IMG)));
		sShell.setText(LanguageUtil.getLanguage().getString("preferences.pdf.stamp.position.title"));
		
		sShell.setLayout(new GridLayout());
		
		createCompositeNavigator();
		
		createCompositeCampos();
		
		createCompositeBotones();

		sShell.setSize(new Point(478, 830));
		
		// to center the shell on the screen
		Monitor primary = this.sShell.getDisplay().getPrimaryMonitor();
	    org.eclipse.swt.graphics.Rectangle bounds = primary.getBounds();
	    org.eclipse.swt.graphics.Rectangle rect = this.sShell.getBounds();
	    int x = bounds.x + (bounds.width - rect.width) / 2;
	    int y = bounds.y + (bounds.height - rect.height) / 3;
	    this.sShell.setLocation(x, y);
		
		sShell.open();
		
		Display display = parent.getDisplay();
		
		while (!sShell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		
		return returnSignatureField;
	}
	
	private void createCompositeNavigator() {

		// info top
		Label labelInfo = new Label(sShell, SWT.WRAP);
		labelInfo.setText(LanguageUtil.getLanguage().getString("preferences.pdf.stamp.dialog.fields.ask.help"));
		GridData gd = new GridData();
		gd.verticalIndent = 10;
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = false;
		labelInfo.setLayoutData(gd);
		
		// navegacion
		Composite compositeBotones = new Composite(sShell, SWT.NONE);
		GridData gd10 = new GridData();
		gd10.horizontalAlignment = GridData.CENTER;
		gd10.horizontalSpan = 1;
		compositeBotones.setLayoutData(gd10);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.horizontalSpacing = 10;
		compositeBotones.setLayout(gridLayout);
		
		bottonAnterior = new Button(compositeBotones, SWT.NONE);
		bottonAnterior.setText(LanguageUtil.getLanguage().getString("preferences.pdf.stamp.dialog.anterior"));
		bottonAnterior.addSelectionListener(new BotonAnteriorListener());

		Composite compositePagination = new Composite(compositeBotones, SWT.NONE);
		GridData gd11 = new GridData();
		gd11.horizontalAlignment = GridData.CENTER;
		gd11.horizontalSpan = 1;
		compositePagination.setLayoutData(gd11);

		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.numColumns = 2;
		gridLayout2.horizontalSpacing = 3;
		compositePagination.setLayout(gridLayout2);
		
		textRuta = new Text(compositePagination, SWT.BORDER);
		textRuta.setText(String.valueOf(currentPage));
		textRuta.setEnabled(false); 
		gd = new GridData();
		gd.widthHint = 30;
		textRuta.setLayoutData(gd);

		labelPosicion = new Label(compositePagination, SWT.NONE);
		labelPosicion.setText("/ " + numberOfPages);

		bottonSiguiente = new Button(compositeBotones, SWT.NONE);
		bottonSiguiente.setText(LanguageUtil.getLanguage().getString("preferences.pdf.stamp.dialog.siguiente"));
		bottonSiguiente.addSelectionListener(new BotonSiguienteListener());
		
		updateNavigatorButtons();
	}
	
	private void createCompositeCampos() throws IOException { 
		
		// ScrolledComposite
		ScrolledComposite composite = new ScrolledComposite(sShell, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		composite.setLayoutData(gd);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		composite.setLayout(gridLayout);
		
		composite.setMinSize(1500, 1500);
		composite.setExpandVertical(true);
		composite.setExpandHorizontal(true);
		composite.setAlwaysShowScrollBars(true);
		composite.setBackground(new Color(Display.getDefault(), 255, 255, 255));
		
		// subComposite
		Composite subComposite = new Composite(composite, SWT.BORDER);
		composite.setContent(subComposite);
		
		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		subComposite.setLayoutData(gd);

		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		subComposite.setLayout(gridLayout);
		
		subComposite.setBackground(new Color(Display.getDefault(), 255, 255, 255));
		
		Image backgroundImage = null;
		
		FileInputStream fis = new FileInputStream(documentPath);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfUtil.getPageImage(fis, baos, currentPage);
		InputStream is = new ByteArrayInputStream(baos.toByteArray());
		backgroundImage = new Image(sShell.getDisplay(), is);
		
		
		pdfSignatureFieldSelectorPanel = new PdfSignatureFieldSelectorPanel(subComposite,  signatureFieldsMapPerPage.values().iterator().next(), stampPath, backgroundImage);
	}
	
	private void createCompositeBotones() {

		Composite compositeBotones = new Composite(sShell, SWT.NONE);
		GridData gd10 = new GridData();
		gd10.horizontalAlignment = GridData.CENTER;
		gd10.horizontalSpan = 1;
		compositeBotones.setLayoutData(gd10);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.horizontalSpacing = 30;
		compositeBotones.setLayout(gridLayout);

		bottonAceptar = new Button(compositeBotones, SWT.NONE);
		bottonAceptar.setText(LanguageUtil.getLanguage().getString("button.accept"));
		bottonAceptar.setImage(new Image(sShell.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.ACEPTAR_IMG)));
		bottonAceptar.addSelectionListener(new BotonAceptarListener());

		bottonCancelar = new Button(compositeBotones, SWT.NONE);
		bottonCancelar.setText(LanguageUtil.getLanguage().getString("button.cancel"));
		bottonCancelar.setImage(new Image(sShell.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.CANCEL_IMG)));
		bottonCancelar.addSelectionListener(new BotonCancelarListener());

	}
	
	class BotonAceptarListener implements SelectionListener {
		
		public void widgetSelected(SelectionEvent event) {
			
			// se setean los datos
			PdfSignatureFieldGui pdfSignatureField = pdfSignatureFieldSelectorPanel.getSignatureField();
			
			returnSignatureField = originalSignatureFieldsMap.get(pdfSignatureField.getName());
			
			sShell.dispose();
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);			
		}
	}
	
	class BotonAnteriorListener implements SelectionListener {
		
		public void widgetSelected(SelectionEvent event) {
			
			try {
				pagesIndex = pagesIndex - 1;	
				currentPage = pages.get(pagesIndex);
				
				updateNavigatorButtons();
				updateSignatureFieldPanel();
				
			} catch (IOException e) {
				log.error(e);
			}
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);			
		}
	}

	class BotonSiguienteListener implements SelectionListener {
		
		public void widgetSelected(SelectionEvent event) {
			
			try {
				pagesIndex = pagesIndex + 1;			
				currentPage = pages.get(pagesIndex);
			
				updateNavigatorButtons();
				updateSignatureFieldPanel();
				
			} catch (IOException e) {
				log.error(e);
			}
		}
	
		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);			
		}
	}
	
	private void updateNavigatorButtons() {
		
		textRuta.setText(String.valueOf(currentPage));
		
		if (pages.size() == 1) {
			bottonAnterior.setEnabled(false);
			bottonSiguiente.setEnabled(false);
		} else if (pagesIndex == 0) {
			bottonAnterior.setEnabled(false);
			bottonSiguiente.setEnabled(true);
		} else if (pagesIndex == pages.size() - 1) {
			bottonAnterior.setEnabled(true);
			bottonSiguiente.setEnabled(false);
		} else {
			bottonAnterior.setEnabled(true);
			bottonSiguiente.setEnabled(true);
		}
	}
	
	private void updateSignatureFieldPanel() throws IOException {
		
		Image backgroundImage = null;
		
		FileInputStream fis = new FileInputStream(documentPath);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfUtil.getPageImage(fis, baos, currentPage);
		InputStream is = new ByteArrayInputStream(baos.toByteArray());
		backgroundImage = new Image(sShell.getDisplay(), is);
		
		
		pdfSignatureFieldSelectorPanel.reloadBackgroundImage(backgroundImage);
		pdfSignatureFieldSelectorPanel.reloadSignatureFields(signatureFieldsMapPerPage.get(currentPage));
		
		pdfSignatureFieldSelectorPanel.redraw();
	}

}
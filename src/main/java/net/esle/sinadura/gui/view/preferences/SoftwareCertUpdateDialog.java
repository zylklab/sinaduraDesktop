/*
 * # Copyright 2008 zylk.net # # This file is part of Sinadura. # # Sinadura is free software: you can redistribute it
 * and/or modify # it under the terms of the GNU General Public License as published by # the Free Software Foundation,
 * either version 2 of the License, or # (at your option) any later version. # # Sinadura is distributed in the hope
 * that it will be useful, # but WITHOUT ANY WARRANTY; without even the implied warranty of # MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the # GNU General Public License for more details. # # You should have received a copy
 * of the GNU General Public License # along with Sinadura. If not, see <http://www.gnu.org/licenses/>. [^] # # See
 * COPYRIGHT.txt for copyright notices and details. #
 */
package net.esle.sinadura.gui.view.preferences;

import java.util.Map;

import net.esle.sinadura.gui.events.BotonCancelarListener;
import net.esle.sinadura.core.util.FileUtil;
import net.esle.sinadura.gui.util.ImagesUtil;
import net.esle.sinadura.gui.util.LanguageUtil;
import net.esle.sinadura.gui.view.main.FileDialogs;
import net.esle.sinadura.gui.view.main.InfoDialog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
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

/**
 * @author zylk.net
 */
public class SoftwareCertUpdateDialog extends Dialog {

	private static Log log = LogFactory.getLog(SoftwareCertUpdateDialog.class);

	private Shell				sShell				= null;
	
	private Text				textName			= null;
	private Text				textPath			= null;

	private Map<String, String>  tempMap = null;
	private String 			selectedName = null;
	
	
	public SoftwareCertUpdateDialog(Shell parent, Map<String, String> map) {
		
		super(parent);
		this.tempMap = map;
		
	}
	
	public SoftwareCertUpdateDialog(Shell parent, Map<String, String> map, String selectedName) {
		
		super(parent);
		this.tempMap = map;
		this.selectedName = selectedName;
	}

	
	public void open() {

		Shell parent = getParent();

		sShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		sShell.setText(LanguageUtil.getLanguage().getString("preferences.cert.software.dialog.title"));
		sShell.setImage(new Image(sShell.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.SINADURA_LOGO_IMG)));
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.verticalSpacing = 10;
		gridLayout.horizontalSpacing = 10;
		gridLayout.marginTop = 5;
		sShell.setLayout(gridLayout);

		createCompositeFields();
		createCompositeButtons();

		sShell.pack();
		
		// to center the shell on the screen
		Monitor primary = this.sShell.getDisplay().getPrimaryMonitor();
	    Rectangle bounds = primary.getBounds();
	    Rectangle rect = this.sShell.getBounds();
	    int x = bounds.x + (bounds.width - rect.width) / 2;
	    int y = bounds.y + (bounds.height - rect.height) / 3;
	    this.sShell.setLocation(x, y);
		
		sShell.open();
		
		Display display = parent.getDisplay();
		
		while (!sShell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

	}

	
	private void createCompositeFields() {

		Label labelName = new Label(sShell, SWT.NONE);
		labelName.setText(LanguageUtil.getLanguage().getString("preferences.cert.software.dialog.name"));
		textName = new Text(sShell, SWT.BORDER);
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		gd.minimumWidth = 400;
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = GridData.FILL;
		textName.setLayoutData(gd);
		
		Label labelPath = new Label(sShell, SWT.NONE);
		labelPath.setText(LanguageUtil.getLanguage().getString("preferences.cert.software.dialog.path"));
		textPath = new Text(sShell, SWT.BORDER);
		textPath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button buttonExaminar = new Button(sShell, SWT.NONE);
		buttonExaminar.setText(LanguageUtil.getLanguage().getString("button.browse"));
		buttonExaminar.addSelectionListener(new ButtonBrowseListener());
		
		if (selectedName != null) { // modo edit
			textName.setText(selectedName);
			textPath.setText(tempMap.get(selectedName));
		}
		
	}


	private void createCompositeButtons() {

		Composite compositeButtons = new Composite(sShell, SWT.NONE);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		gd.horizontalSpan = 4;
		compositeButtons.setLayoutData(gd);

		GridLayout gridLayout5 = new GridLayout();
		gridLayout5.numColumns = 2;
		gridLayout5.horizontalSpacing = 50;
		compositeButtons.setLayout(gridLayout5);

		Button buttonAceptar = new Button(compositeButtons, SWT.NONE);
		buttonAceptar.setText(LanguageUtil.getLanguage().getString("button.accept"));
		buttonAceptar.setImage(new Image(this.sShell.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.ACEPTAR_IMG)));
		buttonAceptar.setLayoutData(new GridData(GridData.END));
		buttonAceptar.addSelectionListener(new ButtonOkListener());
		
		Button buttonCancel = new Button(compositeButtons, SWT.NONE);
		buttonCancel.setText(LanguageUtil.getLanguage().getString("button.cancel"));
		buttonCancel.setImage(new Image(this.sShell.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.CANCEL_IMG)));
		buttonCancel.setLayoutData(new GridData(GridData.END));
		buttonCancel.addSelectionListener(new BotonCancelarListener());

	}
	
	
	public String getSelectedName() {
		
		return selectedName;
	}
	
	class ButtonOkListener implements SelectionListener {
		
		public void widgetSelected(SelectionEvent event) {


			if (!textName.getText().equals("") && !textPath.getText().equals("")) {
	
				if (selectedName == null) { // modo new
					tempMap.put(textName.getText(), textPath.getText());
					selectedName = textName.getText();
				} else { // modo edicion
					tempMap.remove(selectedName);
					tempMap.put(textName.getText(), textPath.getText());
					selectedName = textName.getText();
				}
			
				sShell.dispose();
				
			} else {
				
				InfoDialog id = new InfoDialog(sShell);
				id.open(LanguageUtil.getLanguage().getString("info.campos_obligatorios"));
			}
			
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}
	}
	
	class ButtonBrowseListener implements SelectionListener {
		
		public void widgetSelected(SelectionEvent event) {

			String filePath = FileDialogs.openFileDialog(sShell, new String[] {FileUtil.EXTENSION_P12, FileUtil.EXTENSION_PFX}, true);
			
			if (filePath != null) {
				textPath.setText(filePath);
			}	
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}
	}
}

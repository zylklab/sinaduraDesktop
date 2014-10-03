package net.esle.sinadura.gui.view.main;

import java.util.List;

import net.esle.sinadura.core.model.PdfSignaturePreferences;
import net.esle.sinadura.gui.model.PdfProfileResolution;
import net.esle.sinadura.gui.util.ImagesUtil;
import net.esle.sinadura.gui.util.LanguageUtil;

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
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

public class PdfProfileSelectionDialog extends Dialog{

	private Shell parent;
	private Shell content;
	
	private Composite divButtons = null;
	private Button btnAccept = null;
	private Button btnCancel = null;
	
	private List<PdfProfileResolution> pdfProfileResolutions;
	
	public PdfProfileSelectionDialog(Shell parent, List<PdfProfileResolution> pdfResolution){
		super(parent);
		this.parent = parent;
		this.pdfProfileResolutions = pdfResolution;
	}
	
	public String openDialog(){

		GridData gd = new GridData();
		GridLayout gl = new GridLayout();
		gl.numColumns = 1;
		gl.verticalSpacing = 10;
		gl.marginTop = 10;
		
		content = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		content.setImage(new Image(content.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.SINADURA_LOGO_IMG)));
//		content.setText(LANGUAGEUTIL.GETLANGUAGE().GETSTRING("SLOT.DIALOG.TITLE"));
		content.setText("i18n - Seleccionar perfil PDF");
		content.setLayout(gl);
		
		// perfiles
		//-------------------
		Composite divProfiles = new Composite(content, SWT.NONE);
		divProfiles.setBackground(new org.eclipse.swt.graphics.Color(Display.getCurrent(), 255, 0, 0));
		
		gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.minimumHeight = 200;
		divProfiles.setData(gd);
		
		org.eclipse.swt.widgets.List resolvedAcroFields = new org.eclipse.swt.widgets.List(divProfiles, SWT.NONE);
		resolvedAcroFields.setLayoutData(gl);
		for (PdfProfileResolution profileResolution : pdfProfileResolutions){
			if (profileResolution.hasResolution()){
				resolvedAcroFields.add(profileResolution.getPreferences().getAcroField());				
			}
		}
		
		
		// botonera
		//-------------------
		divButtons = new Composite(content, SWT.NONE);
		divButtons.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		gl = new GridLayout();
		gl.horizontalSpacing = 50;
		gl.numColumns = 2;
		divButtons.setLayout(gl);

		btnAccept = new Button(this.divButtons, SWT.NONE);
		btnAccept.setText(LanguageUtil.getLanguage().getString("button.accept"));
		btnAccept.setImage(new Image(content.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.ACEPTAR_IMG)));
		btnAccept.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				content.dispose();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				widgetSelected(arg0);
			}
		});

		btnCancel = new Button(this.divButtons, SWT.NONE);
		btnCancel.setText(LanguageUtil.getLanguage().getString("button.cancel"));
		btnCancel.setImage(new Image(content.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.CANCEL_IMG)));
		btnCancel.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				content.dispose();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				widgetSelected(arg0);				
			}
		});
		
		content.pack();
		
		Monitor primary = content.getDisplay().getPrimaryMonitor();
	    Rectangle bounds = primary.getBounds();
	    Rectangle rect = content.getBounds();
	    int x = bounds.x + (bounds.width - rect.width) / 2;
	    int y = bounds.y + (bounds.height - rect.height) / 3;
	    content.setLocation(x, y);
	    content.open();
		
		Display display = parent.getDisplay();
		while (!content.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		
		return "sinadura-field";
	}
	
	public PdfSignaturePreferences getSelectedPreference(){
		return null;
	}
}
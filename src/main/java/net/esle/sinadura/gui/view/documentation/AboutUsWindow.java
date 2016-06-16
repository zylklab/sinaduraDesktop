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
package net.esle.sinadura.gui.view.documentation;

import net.esle.sinadura.gui.util.DesktopUtil;
import net.esle.sinadura.gui.util.ImagesUtil;
import net.esle.sinadura.gui.util.LanguageUtil;
import net.esle.sinadura.gui.util.PropertiesServerUtil;
import net.esle.sinadura.gui.util.PropertiesUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

/**
 * @author zylk.net
 */
public class AboutUsWindow extends Dialog {

	private static Log log = LogFactory.getLog(AboutUsWindow.class);
	
	
	private Shell sShell = null;

	private Label labelImage = null;
	private StyledText textTitle = null;
	private StyledText textDesc = null;
	private StyledText textCopyRight = null;
	private Link linkSinadura = null;
	private Link linkZylk = null;
	private Label labelImageZylk = null;

	private Button buttonClose = null;

	/**
	 * @param parent
	 */
	public AboutUsWindow(Shell parent) {
		super(parent);
	}

	/**
	 * @param storeName
	 * @param alias
	 * @return
	 */
	@Override
	public int open() {

		Shell parent = getParentShell();

		this.sShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

		this.sShell.setText(LanguageUtil.getLanguage().getString("about.windowtitle"));
		this.sShell.setSize(new Point(400, 550));
		this.sShell.setImage(new Image(this.sShell.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.SINADURA_LOGO_IMG)));
		
		GridLayout gl = new GridLayout();
		this.sShell.setLayout(gl);
		
		Composite compositeMain = new Composite(this.sShell, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.verticalSpacing = 10;
		gridLayout.marginTop = 10;		
		compositeMain.setLayout(gridLayout);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.minimumHeight = 0;
		gd.minimumWidth = 0;
		compositeMain.setLayoutData(gd);

		// image sinadura
		this.labelImage = new Label(compositeMain, SWT.NONE);
		this.labelImage.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		Image imageSinadura = new Image(compositeMain.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.SINADURA_FULL_IMG));
		this.labelImage.setImage(imageSinadura);

		// title sinadura
		this.textTitle = new StyledText(compositeMain, SWT.NONE);
		String textoTitle = PropertiesUtil.APPLICATION_NAME + " "
				+ PropertiesUtil.getConfiguration().getProperty(PropertiesUtil.APPLICATION_VERSION_STRING);
		this.textTitle.setText(textoTitle);
		this.textTitle.setEditable(false);
		this.textTitle.setBackground(compositeMain.getBackground());

		StyleRange stylerangeTxtTitle = new StyleRange();
		stylerangeTxtTitle.start = 0;
		stylerangeTxtTitle.length = textoTitle.length();
		stylerangeTxtTitle.font = new Font(this.textTitle.getDisplay(), "", 16, SWT.BOLD);
		this.textTitle.setStyleRange(stylerangeTxtTitle);
		this.textTitle.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		
		// desc sinadura
		this.textDesc = new StyledText(compositeMain, SWT.NONE);
		String textoDesc = LanguageUtil.getLanguage().getString("about.descripcion");
		this.textDesc.setText(textoDesc);
		this.textDesc.setEditable(false);
		this.textDesc.setBackground(compositeMain.getBackground());

		StyleRange stylerangeTxtDesc = new StyleRange();
		stylerangeTxtDesc.start = 0;
		stylerangeTxtDesc.length = textoDesc.length();
		stylerangeTxtDesc.font = new Font(this.textDesc.getDisplay(), "", 8, SWT.CENTER);
		stylerangeTxtDesc.foreground = new Color(this.textDesc.getDisplay(), new RGB(100, 100, 100));
		this.textDesc.setStyleRange(stylerangeTxtDesc);
		this.textDesc.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));

		// link sinadura
		this.linkSinadura = new Link(compositeMain, SWT.NONE);
		String textoURL = PropertiesUtil.getConfiguration().getProperty(PropertiesUtil.SINADURA_MAIN_URL);
		String text = "<a>" + textoURL + "</a>";
		this.linkSinadura.setText(text);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		gd.heightHint = 40;
		this.linkSinadura.setLayoutData(gd);
		this.linkSinadura.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				DesktopUtil.openDefaultBrowser(PropertiesUtil.getConfiguration().getProperty(PropertiesUtil.SINADURA_MAIN_URL));
			}
		});

		// image zylk
		this.labelImageZylk = new Label(compositeMain, SWT.NONE);
		Image imageZylk = new Image(compositeMain.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.ZYLK_LOGO_IMG));
		this.labelImageZylk.setImage(imageZylk);
		this.labelImageZylk.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		
		// copyright zylk
		this.textCopyRight = new StyledText(compositeMain, SWT.NONE);
		String textoCopyRight = "Copyright  2012";
		this.textCopyRight.setText(textoCopyRight);
		this.textCopyRight.setEditable(false);
		this.textCopyRight.setBackground(compositeMain.getBackground());
		StyleRange stylerangeTxtCopyRight = new StyleRange();
		stylerangeTxtCopyRight.start = 0;
		stylerangeTxtCopyRight.length = textoCopyRight.length();
		stylerangeTxtCopyRight.font = new Font(this.textCopyRight.getDisplay(), "", 9, SWT.CENTER);
		stylerangeTxtCopyRight.foreground = new Color(this.textCopyRight.getDisplay(), new RGB(80, 80, 80));
		this.textCopyRight.setStyleRange(stylerangeTxtCopyRight);
		this.textCopyRight.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		
		// link zylk
		this.linkZylk = new Link(compositeMain, SWT.NONE);
		text = "<a>http://www.zylk.net</a>";
		this.linkZylk.setText(text);
		this.linkZylk.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		gd = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		gd.heightHint = 35;
		this.linkZylk.setLayoutData(gd);
		this.linkZylk.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				DesktopUtil.openDefaultBrowser("http://www.zylk.net");
			}
		});

		// buttons composite
		Composite composite = new Composite(compositeMain, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		GridLayout gridLayoutButtons = new GridLayout();
		gridLayoutButtons.numColumns = 1;
		gridLayoutButtons.horizontalSpacing = 100;
		composite.setLayout(gridLayoutButtons);

		this.buttonClose = new Button(composite, SWT.NONE);
		this.buttonClose.setText(LanguageUtil.getLanguage().getString("button.back"));
		this.buttonClose.setImage(new Image(this.sShell.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.BACK_IMG)));
		this.buttonClose.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		this.buttonClose.addSelectionListener(new BotonCloseListener());

		// to center the shell on the screen
		Monitor primary = this.sShell.getDisplay().getPrimaryMonitor();
	    Rectangle bounds = primary.getBounds();
	    Rectangle rect = this.sShell.getBounds();
	    int x = bounds.x + (bounds.width - rect.width) / 2;
	    int y = bounds.y + (bounds.height - rect.height) / 3;
	    this.sShell.setLocation(x, y);
		
		this.sShell.open();
		
		this.sShell.pack();
		
		Display display = parent.getDisplay();
		
		while (!this.sShell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		
		return (0);
	}

	/**
	 *
	 */

	class BotonCloseListener implements SelectionListener {
		public void widgetSelected(SelectionEvent event) {

			sShell.dispose();
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}
	}

}
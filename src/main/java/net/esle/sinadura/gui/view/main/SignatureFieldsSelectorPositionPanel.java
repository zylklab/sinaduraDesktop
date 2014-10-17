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



import java.util.List;

import net.esle.sinadura.gui.model.PdfBlankSignatureInfoDesktop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;


public class SignatureFieldsSelectorPositionPanel extends Composite {
	
	private static Log log = LogFactory.getLog(SignatureFieldsSelectorPositionPanel.class);
	
	private Image stampOriginalImage = null;
	private Image stampImage = null;
	
	private Image backgroundImage = null;
	
	private Cursor oldCursor = null;
	
	private List<PdfBlankSignatureInfoDesktop> pdfBlankSignatureInfos = null;
	private PdfBlankSignatureInfoDesktop selectedPdfBlankSignatureInfo = null;
	
	private Display display;
	
	
	public SignatureFieldsSelectorPositionPanel(Composite parent, List<PdfBlankSignatureInfoDesktop> pdfBlankSignatureInfos2, String stampPath, Image backgroundImage2) {
		
		super(parent, SWT.NONE);
		
		display = this.getDisplay();
		
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		this.setLayoutData(gd);
		
		oldCursor = this.getShell().getCursor();
		
		if (stampPath != null) {
			stampOriginalImage = new Image(this.getDisplay(), stampPath);
			stampImage = new Image(this.getDisplay(), stampPath);
		}
		
		this.backgroundImage = backgroundImage2;
		
		reloadPdfBlankSignatureInfos(pdfBlankSignatureInfos2);
		
		this.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
			
		this.addPaintListener(new PaintListener() {

			public void paintControl(PaintEvent event)	{
				
				GC gc = event.gc;
				
				// background
				gc.drawImage(backgroundImage, 0, 0);
				gc.setForeground(new Color(display, 100, 100, 100));
				event.gc.drawRectangle(new Rectangle(0, 0, backgroundImage.getBounds().width - 1, backgroundImage.getBounds().height - 1));
				
				// fields
				for (PdfBlankSignatureInfoDesktop pbsi : pdfBlankSignatureInfos) {

					// border
					gc.setForeground(new Color(display, 150, 150, 150));
					gc.setAlpha(255);
					event.gc.drawRectangle(new Rectangle(pbsi.getStartX(), pbsi.getStartY(), pbsi.getWidht() - 1, pbsi.getHeight() - 1));
					
					if (pbsi.getName().equals(selectedPdfBlankSignatureInfo.getName())) { // selected
						
						if (stampImage != null) {
							gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
							gc.setAlpha(255);
							gc.drawImage(stampImage, selectedPdfBlankSignatureInfo.getStartX(), selectedPdfBlankSignatureInfo.getStartY());
						} else {
							gc.setBackground(display.getSystemColor(SWT.COLOR_GREEN));
				            gc.setAlpha(75);
							gc.fillRectangle(new Rectangle(pbsi.getStartX(), pbsi.getStartY(), pbsi.getWidht() - 1,
									pbsi.getHeight() - 1));	
						}
						
					} else {
						
						gc.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
						gc.setAlpha(25);
						gc.fillRectangle(new Rectangle(pbsi.getStartX(), pbsi.getStartY(), pbsi.getWidht() - 1,
								pbsi.getHeight() - 1));
					}
					
				}
			
			}
			
		});
		
		this.addMouseListener(new MousePaintListener());
		
		this.addMouseMoveListener(new MouseMovePaintListener());
	}
	
	
	public void reloadBackgroundImage(Image backgroundImage) {
		
		this.backgroundImage = backgroundImage;
	}
	
	public void reloadPdfBlankSignatureInfos(List<PdfBlankSignatureInfoDesktop> pdfBlankSignatureInfos2) {
		
		this.pdfBlankSignatureInfos = pdfBlankSignatureInfos2;
		// el primero seleccionado por defecto
		selectedPdfBlankSignatureInfo = pdfBlankSignatureInfos.get(0);
		if (stampImage != null) {
			stampImage = resize(stampOriginalImage, selectedPdfBlankSignatureInfo.getWidht(), selectedPdfBlankSignatureInfo.getHeight());
		}
	}
	
	public PdfBlankSignatureInfoDesktop getSignatureField() {
		
		return selectedPdfBlankSignatureInfo;
	}
	
	
	private static Image resize(Image image, int width, int height) {
		
		// con este código la imagen tiene más calidad, pero no se refresca lo suficientemente rapido. 
//		Image scaled = new Image(Display.getDefault(), width, height);
//		GC gc = new GC(scaled);
//		gc.setAntialias(SWT.ON);
//		gc.setInterpolation(SWT.HIGH);
//		gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, 0, 0, width, height);
//		gc.dispose();
////		image.dispose(); // don't forget about me!
//		return scaled;
		
		return (new Image (Display.getDefault(), image.getImageData().scaledTo(width, height)));
	}
	
	
	class MousePaintListener implements MouseListener {

		@Override
		public void mouseDoubleClick(MouseEvent arg0) {	
		}

		@Override
		public void mouseDown(MouseEvent clickEvent) {			
		}

		@Override
		public void mouseUp(MouseEvent event) {

			for (PdfBlankSignatureInfoDesktop pdfBlankSignatureInfo : pdfBlankSignatureInfos) {
				
				if (isInside(pdfBlankSignatureInfo, event)) {
					
					selectedPdfBlankSignatureInfo = pdfBlankSignatureInfo;
					
					if (stampImage != null) {
						stampImage = resize(stampOriginalImage, selectedPdfBlankSignatureInfo.getWidht(),
								selectedPdfBlankSignatureInfo.getHeight());
					}
					((Composite) event.getSource()).redraw();
				}
			}
			
		}	
	}
	
	
	class MouseMovePaintListener implements MouseMoveListener {

		@Override
		public void mouseMove(MouseEvent clickEvent) {

			boolean inside = false;
			for (PdfBlankSignatureInfoDesktop pdfBlankSignatureInfo : pdfBlankSignatureInfos) {
				if (isInside(pdfBlankSignatureInfo, clickEvent)) {
					inside = true;
				}
			}
			
			if (inside) {
				// si el usuario esta dentro de un hueco
				Cursor handCursor = new Cursor(clickEvent.display, SWT.CURSOR_HAND);
				((Composite)clickEvent.getSource()).setCursor(handCursor);
			} else { // sino se pone el normal
				((Composite)clickEvent.getSource()).setCursor(oldCursor);		
			}
			
		}

	}
	
	
	private boolean isInside(PdfBlankSignatureInfoDesktop pdfBlankSignatureInfoDesktop, MouseEvent clickEvent) {
		
		int margin = 0;
		
		// si el usuario clicka dentro de la imagen
		if (clickEvent.x > pdfBlankSignatureInfoDesktop.getStartX() + margin && clickEvent.x < (pdfBlankSignatureInfoDesktop.getStartX() + pdfBlankSignatureInfoDesktop.getWidht()) - margin
				&& clickEvent.y > pdfBlankSignatureInfoDesktop.getStartY() + margin && clickEvent.y < (pdfBlankSignatureInfoDesktop.getStartY() + pdfBlankSignatureInfoDesktop.getHeight()) - margin) {
			
			return true;
		}
		
		return false;
	}
	
}






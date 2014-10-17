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
package net.esle.sinadura.gui.view.preferences;



import java.io.FileNotFoundException;

import net.esle.sinadura.gui.model.PdfSignatureFieldGui;
import net.esle.sinadura.gui.util.ImagesUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;


public class PdfSignatureFieldPositionPanel extends Composite {
	
	private static final int MARGIN = 5;
	
	private boolean movable = false;
	private boolean up = false;
	private boolean down = false;
	private boolean left = false;
	private boolean right = false;
	
	private PdfSignatureFieldGui stampPosition;
	
	private int xImage2Click = 0; // Distancia entre el punto anterior y el punto donde hace click el usuario.
	private int yImage2Click = 0;
	
	private Image stampOriginalImage = null;
	private Image stampImage = null;
	private Image backgroundImage = null;
	
	private Cursor oldCursor = null;
	
	private Display display;
	
	
	public PdfSignatureFieldPositionPanel(Composite parent, String stampPath, PdfSignatureFieldGui stampPosition2, Image backgroundImage2) {
		
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
			try {
				stampOriginalImage = new Image(this.getDisplay(), stampPath);
				stampImage = new Image(this.getDisplay(), stampPath);
			} catch (SWTException e) {
				if (e.getCause() != null && e.getCause() instanceof FileNotFoundException) {
					stampOriginalImage = null;
					stampImage = null;	
				} else {
					throw e;
				}
			}
		}	
			
		if (backgroundImage2 == null) {
			this.backgroundImage = new Image(this.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.STAMP_BACKGROUND_IMG));
		} else {
				
			this.backgroundImage = backgroundImage2;
		}
		
		
		this.reloadStampPosition(stampPosition2);
		
		this.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
			
		this.addPaintListener(new PaintListener() {

			public void paintControl(PaintEvent event)	{
				
				GC gc = event.gc;
				
				gc.drawImage(backgroundImage, 0, 0);
				event.gc.drawRectangle(new Rectangle(0, 0, backgroundImage.getBounds().width - 1, backgroundImage.getBounds().height - 1));
				
				if (stampImage != null) {
					gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
					gc.setAlpha(255);
					
					gc.drawImage(stampImage, stampPosition.getStartX(), stampPosition.getStartY());
					event.gc.drawRectangle(new Rectangle(stampPosition.getStartX(), stampPosition.getStartY(), stampPosition.getWidht() - 1, stampPosition.getHeight() - 1));
					
				} else {
					gc.setBackground(display.getSystemColor(SWT.COLOR_GREEN));
		            gc.setAlpha(75);
		            
					gc.fillRectangle(new Rectangle(stampPosition.getStartX(), stampPosition.getStartY(), stampPosition.getWidht() - 1,
							stampPosition.getHeight() - 1));	
				}
				
			}
		});
		
		this.addMouseListener(new MousePaintListener());
		
		this.addMouseMoveListener(new MouseMovePaintListener());
		
	}
	
	
	public void setBackgroundImage(Image backgroundImage) {
		
		this.backgroundImage = backgroundImage;
	}
	
	private void reloadStampPosition(PdfSignatureFieldGui stampPosition) {
		
		this.stampPosition = stampPosition;
		
		if (stampImage != null) {
			stampImage = resize(stampImage, this.stampPosition.getWidht(), this.stampPosition.getHeight());
		}
	}
	
	public PdfSignatureFieldGui getStampPosition() {
		
		return stampPosition;
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
			
			// si el usuario clicka dentro de la imagen
			if (clickEvent.x > stampPosition.getStartX() + MARGIN && clickEvent.x < (stampPosition.getStartX() + stampPosition.getWidht()) -MARGIN
					&& clickEvent.y > stampPosition.getStartY() + MARGIN && clickEvent.y < (stampPosition.getStartY() + stampPosition.getHeight()) - MARGIN) {
				
				// accionamos el estado en el que se mueve la imagen (se repinta el composite).
				movable = true;
				
				// y se almacena la distancia entre el comienzo de la imagen y el click del usuario.  
				xImage2Click = clickEvent.x - stampPosition.getStartX();
				yImage2Click = clickEvent.y - stampPosition.getStartY();
			}
			
			
			// si el usuario clicka en el margen inferior de la imagen
			if (clickEvent.x > stampPosition.getStartX() + MARGIN && clickEvent.x < (stampPosition.getStartX() + stampPosition.getWidht()) -MARGIN
					&& clickEvent.y > (stampPosition.getStartY() + stampPosition.getHeight()) - MARGIN
					&& clickEvent.y < (stampPosition.getStartY() + stampPosition.getHeight()) + MARGIN) {
			
				down = true;
			}
			
			// si el usuario clicka en el margen superior de la imagen
			if (clickEvent.x > stampPosition.getStartX() + MARGIN && clickEvent.x < (stampPosition.getStartX() + stampPosition.getWidht()) -MARGIN
					&& clickEvent.y > stampPosition.getStartY() - MARGIN
					&& clickEvent.y < stampPosition.getStartY() + MARGIN) {
			
				up = true;
			}
			
			// si el usuario clicka en el margen izquierdo de la imagen
			if (clickEvent.y > stampPosition.getStartY() + MARGIN && clickEvent.y < (stampPosition.getStartY() + stampPosition.getHeight()) - MARGIN
					&& clickEvent.x > stampPosition.getStartX() - MARGIN
					&& clickEvent.x < stampPosition.getStartX() + MARGIN) {
			
				left = true;
			}
			
			// si el usuario clicka en el margen derecho de la imagen
			if (clickEvent.y > stampPosition.getStartY() + MARGIN && clickEvent.y < (stampPosition.getStartY() + stampPosition.getHeight()) - MARGIN
					&& clickEvent.x > (stampPosition.getStartX() + stampPosition.getWidht()) - MARGIN
					&& clickEvent.x < (stampPosition.getStartX() + stampPosition.getWidht()) + MARGIN) {
			
				right = true;
			}
			
		}

		@Override
		public void mouseUp(MouseEvent event) {
			
			// finalizamos el estado movable
			movable = false;
			down = false;
			up = false;
			left = false;
			right = false;
		}	
	}
	
	
	class MouseMovePaintListener implements MouseMoveListener {

		@Override
		public void mouseMove(MouseEvent clickEvent) {

			if (clickEvent.x > stampPosition.getStartX() + MARGIN
					&& clickEvent.x < (stampPosition.getStartX() + stampPosition.getWidht()) - MARGIN
					&& clickEvent.y > stampPosition.getStartY() + MARGIN
					&& clickEvent.y < (stampPosition.getStartY() + stampPosition.getHeight()) - MARGIN) {
				
				// si el usuario esta dentro de la imagen
				Cursor handCursor = new Cursor(clickEvent.display, SWT.CURSOR_HAND);
				((Composite) clickEvent.getSource()).setCursor(handCursor);

			} else if (clickEvent.x > stampPosition.getStartX() + MARGIN
					&& clickEvent.x < (stampPosition.getStartX() + stampPosition.getWidht()) - MARGIN
					&& clickEvent.y > (stampPosition.getStartY() + stampPosition.getHeight()) - MARGIN
					&& clickEvent.y < (stampPosition.getStartY() + stampPosition.getHeight()) + MARGIN) {
				
				// si el usuario esta en el margen inferior de la imagen
				Cursor handCursor = new Cursor(clickEvent.display, SWT.CURSOR_SIZES);
				((Composite) clickEvent.getSource()).setCursor(handCursor);
			} else if (clickEvent.x > stampPosition.getStartX() + MARGIN
					&& clickEvent.x < (stampPosition.getStartX() + stampPosition.getWidht()) - MARGIN
					&& clickEvent.y > stampPosition.getStartY() - MARGIN && clickEvent.y < stampPosition.getStartY() + MARGIN) {

				// si el usuario esta en el margen superior de la imagen
				Cursor handCursor = new Cursor(clickEvent.display, SWT.CURSOR_SIZEN);
				((Composite) clickEvent.getSource()).setCursor(handCursor);

			} else if (clickEvent.y > stampPosition.getStartY() + MARGIN
					&& clickEvent.y < (stampPosition.getStartY() + stampPosition.getHeight()) - MARGIN
					&& clickEvent.x > stampPosition.getStartX() - MARGIN && clickEvent.x < stampPosition.getStartX() + MARGIN) {
				
				// si el usuario clicka en el margen izquierdo de la imagen
				Cursor handCursor = new Cursor(clickEvent.display, SWT.CURSOR_SIZEW);
				((Composite) clickEvent.getSource()).setCursor(handCursor);

			} else if (clickEvent.y > stampPosition.getStartY() + MARGIN
					&& clickEvent.y < (stampPosition.getStartY() + stampPosition.getHeight()) - MARGIN
					&& clickEvent.x > (stampPosition.getStartX() + stampPosition.getWidht()) - MARGIN
					&& clickEvent.x < (stampPosition.getStartX() + stampPosition.getWidht()) + MARGIN) {
				
				// si el usuario clicka en el margen derecho de la imagen
				Cursor handCursor = new Cursor(clickEvent.display, SWT.CURSOR_SIZEE);
				((Composite) clickEvent.getSource()).setCursor(handCursor);
				
			} else {
				// sino se pone el normal
				((Composite) clickEvent.getSource()).setCursor(oldCursor);
			}
			
			
			
			// Mientras el usuario mueve el raton, y estando en el estado en el que se permite mover la imagen
			if (movable) {
			
				// actualizamos la posicion de la imagen
				stampPosition.setStartX(clickEvent.x - xImage2Click);
				stampPosition.setStartY(clickEvent.y - yImage2Click);
				
				// y mandamos repintar el composite.
				((Composite)clickEvent.getSource()).redraw();
			}
			
			if (down) {
				
				int newHeight = clickEvent.y - stampPosition.getStartY();
				stampPosition.setHeight(newHeight);
				
				if (stampImage != null) {
					stampImage = resize(stampOriginalImage, stampPosition.getWidht(), newHeight);
				}
				((Composite)clickEvent.getSource()).redraw();
			}
			if (up) {
				
				int newHeight = stampPosition.getHeight() + stampPosition.getStartY() - clickEvent.y;
				stampPosition.setStartY(clickEvent.y);
				stampPosition.setHeight(newHeight);
				
				if (stampImage != null) {
					stampImage = resize(stampOriginalImage, stampPosition.getWidht(),  newHeight);
				}
				((Composite)clickEvent.getSource()).redraw();
			}
			if (right) {
				
				int newWidht = clickEvent.x - stampPosition.getStartX();
				stampPosition.setWidht(newWidht);
				
				if (stampImage != null) {
					stampImage = resize(stampOriginalImage, newWidht, stampPosition.getHeight());
				}
				((Composite)clickEvent.getSource()).redraw();
			}
			if (left) {
				
				int newWidht = stampPosition.getWidht() + stampPosition.getStartX() - clickEvent.x; 
				stampPosition.setStartX(clickEvent.x);
				stampPosition.setWidht(newWidht);
				
				if (stampImage != null) {
					stampImage = resize(stampOriginalImage, newWidht, stampPosition.getHeight());
				}
				((Composite)clickEvent.getSource()).redraw();
			}
		}

	}	
}


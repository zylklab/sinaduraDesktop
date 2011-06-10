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



import net.esle.sinadura.gui.util.ImagesUtil;

import org.eclipse.swt.SWT;
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


public class ImagePositionPanel extends Composite {
	
	private boolean movable = false;
	
	private boolean up = false;
	private boolean down = false;
	private boolean left = false;
	private boolean right = false;
	
	private int xImagePosition = 10; // coordenadas de la posición de la imagen (vertice superior izquierdo).
	private int yImagePosition = 10;
	
	private int xImage2Click = 0; // Distancia entre el punto anterior y el punto donde hace click el usuario.
	private int yImage2Click = 0;
	
	private Image originalImage = null;
	private Image image = null;
	
	private Image imageBackground = null;
	
	private Cursor oldCursor = null;
	
	public ImagePositionPanel(Composite parent, String ruta, java.awt.Rectangle rectangle) {
		
		super(parent, SWT.NONE);
		
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		this.setLayoutData(gd);
		
		oldCursor = this.getShell().getCursor();
		originalImage = new Image(this.getDisplay(), ruta);
		image = new Image(this.getDisplay(), ruta);
		
		imageBackground = new Image(this.getDisplay(), ClassLoader.getSystemResourceAsStream(ImagesUtil.STAMP_BACKGROUND_IMG));
		
//		image = resize(image, 116, 40);
		
		setImagePosition(rectangle);
		
		
		this.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		
		this.addPaintListener(new PaintListener() {

			public void paintControl(PaintEvent event)	{
				
				GC gc = event.gc;
				gc.drawImage(imageBackground, 0, 0);
				event.gc.drawRectangle(new Rectangle(xImagePosition -1 , yImagePosition - 1, image.getBounds().width +1 , image.getBounds().height + 1));
				gc.drawImage(image, xImagePosition, yImagePosition);
			}
		});
		
		this.addMouseListener(new MousePaintListener());
		
		this.addMouseMoveListener(new MouseMovePaintListener());
		
	}
	
	public void setImagePosition(java.awt.Rectangle rectangle) {
		
		xImagePosition = rectangle.x; 
		yImagePosition = rectangle.y;
		
		image = resize(image, rectangle.width, rectangle.height);
	}
	
	public java.awt.Rectangle getImagePosition() {
		
		return (new java.awt.Rectangle (xImagePosition, yImagePosition, image.getBounds().width, image.getBounds().height ));
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
			if (clickEvent.x > xImagePosition + 5 && clickEvent.x < (xImagePosition + image.getBounds().width) -5
					&& clickEvent.y > yImagePosition + 5 && clickEvent.y < (yImagePosition + image.getBounds().height) - 5) {
				
				// accionamos el estado en el que se mueve la imagen (se repinta el composite).
				movable = true;
				
				// y se almacena la distancia entre el comienzo de la imagen y el click del usuario.  
				xImage2Click = clickEvent.x - xImagePosition;
				yImage2Click = clickEvent.y - yImagePosition;
			}
			
			
			// si el usuario clicka en el margen inferior de la imagen
			if (clickEvent.x > xImagePosition + 5 && clickEvent.x < (xImagePosition + image.getBounds().width) -5
					&& clickEvent.y > (yImagePosition + image.getBounds().height) - 5
					&& clickEvent.y < (yImagePosition + image.getBounds().height) + 5) {
			
				down = true;
			}
			
			// si el usuario clicka en el margen superior de la imagen
			if (clickEvent.x > xImagePosition + 5 && clickEvent.x < (xImagePosition + image.getBounds().width) -5
					&& clickEvent.y > yImagePosition - 5
					&& clickEvent.y < yImagePosition + 5) {
			
				up = true;
			}
			
			// si el usuario clicka en el margen izquierdo de la imagen
			if (clickEvent.y > yImagePosition + 5 && clickEvent.y < (yImagePosition + image.getBounds().height) - 5
					&& clickEvent.x > xImagePosition - 5
					&& clickEvent.x < xImagePosition + 5) {
			
				left = true;
			}
			
			// si el usuario clicka en el margen derecho de la imagen
			if (clickEvent.y > yImagePosition + 5 && clickEvent.y < (yImagePosition + image.getBounds().height) - 5
					&& clickEvent.x > (xImagePosition + image.getBounds().width) - 5
					&& clickEvent.x < (xImagePosition + image.getBounds().width) + 5) {
			
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
			

			if (clickEvent.x > xImagePosition + 5 && clickEvent.x < (xImagePosition + image.getBounds().width) -5
					&& clickEvent.y > yImagePosition + 5 && clickEvent.y < (yImagePosition + image.getBounds().height) - 5) {
				// si el usuario esta dentro de la imagen
				
				Cursor handCursor = new Cursor(clickEvent.display, SWT.CURSOR_HAND);
				((Composite)clickEvent.getSource()).setCursor(handCursor);
				
			} else if (clickEvent.x > xImagePosition + 5 && clickEvent.x < (xImagePosition + image.getBounds().width) -5 
					&& clickEvent.y > (yImagePosition + image.getBounds().height) - 5
					&& clickEvent.y < (yImagePosition + image.getBounds().height) + 5) {
				// si el usuario esta en el margen inferior de la imagen
				
				Cursor handCursor = new Cursor(clickEvent.display, SWT.CURSOR_SIZES);
				((Composite)clickEvent.getSource()).setCursor(handCursor);
			} else if (clickEvent.x > xImagePosition + 5 && clickEvent.x < (xImagePosition + image.getBounds().width) -5
					&& clickEvent.y > yImagePosition - 5
					&& clickEvent.y < yImagePosition + 5) {
			
				// si el usuario esta en el margen superior de la imagen
				Cursor handCursor = new Cursor(clickEvent.display, SWT.CURSOR_SIZEN);
				((Composite)clickEvent.getSource()).setCursor(handCursor);
				
			} else if (clickEvent.y > yImagePosition + 5 && clickEvent.y < (yImagePosition + image.getBounds().height) - 5
					&& clickEvent.x > xImagePosition - 5
					&& clickEvent.x < xImagePosition + 5) {
				// si el usuario clicka en el margen izquierdo de la imagen
				Cursor handCursor = new Cursor(clickEvent.display, SWT.CURSOR_SIZEW);
				((Composite)clickEvent.getSource()).setCursor(handCursor);
				
			} else if (clickEvent.y > yImagePosition + 5 && clickEvent.y < (yImagePosition + image.getBounds().height) - 5
						&& clickEvent.x > (xImagePosition + image.getBounds().width) - 5
						&& clickEvent.x < (xImagePosition + image.getBounds().width) + 5) { 
				// si el usuario clicka en el margen derecho de la imagen
				Cursor handCursor = new Cursor(clickEvent.display, SWT.CURSOR_SIZEE);
				((Composite)clickEvent.getSource()).setCursor(handCursor);
			}	
			else { // sino se pone el normal
				((Composite)clickEvent.getSource()).setCursor(oldCursor);	
			}
			
			
			
			// Mientras el usuario mueve el raton, y estando en el estado en el que se permite mover la imagen
			if (movable) {
			
				// actualizamos la posicion de la imagen
				xImagePosition = clickEvent.x - xImage2Click;
				yImagePosition = clickEvent.y - yImage2Click;
				
				// y mandamos repintar el composite.
				((Composite)clickEvent.getSource()).redraw();
			}
			
			if (down) {
				image = resize(originalImage, image.getBounds().width,  clickEvent.y - yImagePosition);
				((Composite)clickEvent.getSource()).redraw();
			}
			if (up) {
				int newHeight = image.getBounds().height + yImagePosition - clickEvent.y; 
				yImagePosition = clickEvent.y;
				image = resize(originalImage, image.getBounds().width,  newHeight );
				((Composite)clickEvent.getSource()).redraw();
			}
			if (right) {
				image = resize(originalImage, clickEvent.x - xImagePosition, image.getBounds().height);
				((Composite)clickEvent.getSource()).redraw();
			}
			if (left) {
				int newWidth = image.getBounds().width + xImagePosition - clickEvent.x; 
				xImagePosition = clickEvent.x;
				image = resize(originalImage, newWidth, image.getBounds().height );
				((Composite)clickEvent.getSource()).redraw();
			}
		}

	}	
}






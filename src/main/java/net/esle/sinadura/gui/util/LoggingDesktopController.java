/*
# Copyright 2008 zylk.net
#
# This file is part of Sinadura.
#
# Sinadura is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 2 of the License, or
# (at your option) any later version.
#
# Sinadura is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with Sinadura.  If not, see <http://www.gnu.org/licenses/>. [^]
#
# See COPYRIGHT.txt for copyright notices and details.
#
*/
package net.esle.sinadura.gui.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.PaintObjectEvent;
import org.eclipse.swt.custom.PaintObjectListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * @author zylk.net
 */
public class LoggingDesktopController {

	private static Log log = LogFactory.getLog(LoggingDesktopController.class);
	
	private static final String			MSG_TYPE_INFO	= "INFO";
	private static final String			MSG_TYPE_ERROR	= "ERROR";
	
	private static Image				IMAGE_ERROR;
	private static Image				IMAGE_INFO;
	private static StyledText			cajaLogging;
	
	private static final RGB			RGB_ERROR		= new RGB(220, 0, 0);
	private static final RGB			RGB_INFO		= new RGB(0, 0, 180);

	private static Map<String, String>	ImagesMap		= new HashMap<String, String>();
	
	private static List<String>	errorList = new ArrayList<String>();
	

	/**
	 * Inicializa el area de logging.
	 *
	 * @param textBox
	 */
	public static void initialize(Shell sShell) {
		
		/* inicializar el loggingcontroller */
	
		Composite composite = new Composite (sShell, SWT.NONE);
		GridLayout layout = new GridLayout ();
		layout.marginLeft = 10;
		layout.marginRight = 10;
		layout.marginBottom = 10;
		composite.setLayout(layout);
		GridData gdComposite = new GridData();
		gdComposite.horizontalAlignment = GridData.FILL;
		gdComposite.grabExcessHorizontalSpace = true;
		gdComposite.heightHint = 150;
		composite.setLayoutData(gdComposite);
		
		StyledText textLogs = new StyledText(composite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		textLogs.setEditable(false);
		textLogs.setFont(new Font(sShell.getDisplay(), "", 9, 0));
		GridData gdLogs = new GridData();
		gdLogs.horizontalAlignment = GridData.FILL;
		gdLogs.grabExcessHorizontalSpace = true;
		gdLogs.verticalAlignment = GridData.FILL;
		gdLogs.grabExcessVerticalSpace = true;
		textLogs.setLayoutData(gdLogs);
		
		cajaLogging = textLogs;
		
	}

	
	/**
	 * Añade un mensaje de log al panel de logging de la aplicación
	 *
	 * @param mensajeLog
	 */
	public static void printError(String mensajeLog) {

		errorList.add(mensajeLog);
		println(mensajeLog, MSG_TYPE_ERROR);

	}
	
	public static List<String> getErrorList() {
		return errorList;
	}

	/**
	 * Añade un mensaje de log al panel de logging de la aplicación
	 *
	 * @param mensajeLog
	 */
	public static void printInfo(String mensajeLog) {

		println(mensajeLog, MSG_TYPE_INFO);

	}

	/**
	 * @param mensajeLog
	 * @param tipo
	 */
	private static void println(String mensajeLog, String tipo) {
		
		
		
		if (cajaLogging != null) {
			
			log.info("CONSOLA DE LA APP: " + mensajeLog);
			
			cajaLogging.append(getDate() + "   " + mensajeLog + "\n");

			/* estableciendo el estilo */
			if (tipo.equalsIgnoreCase(MSG_TYPE_ERROR)) {
				setErrorStyle(mensajeLog.length());
			} else if (tipo.equalsIgnoreCase(MSG_TYPE_INFO)) {
					setInfoStyle(mensajeLog.length());
			}
			
			cajaLogging.addPaintObjectListener(new PaintObjectListener() {
				public void paintObject(PaintObjectEvent event) {
					GC gc = event.gc;
					StyleRange style = event.style;

					Set<String> setOffsets = ImagesMap.keySet();
					Iterator<String> iterator = setOffsets.iterator();
					int start = style.start;
					while (iterator.hasNext()) {
						String offset = iterator.next();
						String imagenType = ImagesMap.get(offset);
						Image image;
						if (imagenType.equalsIgnoreCase(MSG_TYPE_ERROR))
							image = getImageError();
						else
							image = getImageInfo();
						if (start == Integer.valueOf(offset)) {
							int x = event.x;
							int y = event.y + event.ascent - style.metrics.ascent;
							gc.drawImage(image, x, y);
						}

					}

				}
			});
			
			/* para mantener el log en la ultima linea escrita */
			cajaLogging.setTopIndex(Integer.MAX_VALUE);
			
			/* update de la consola de mensajes */
			cajaLogging.update();

		} else {
			log.warn("La caja de texto para el logging está a null.");
		}
	}

	/**
	 * Devuelve un String con la hora actual formateada
	 *
	 * @return
	 */
	private static String getDate() {

		SimpleDateFormat dateFormat = LanguageUtil.getTimeFormater();
		return dateFormat.format(new Date());
	}

	/**
	 * @param length -
	 *            Longitud del mensaje a escribir
	 */
	private static void setInfoStyle(int length) {

		int start = cajaLogging.getText().length() - (length + 3);

		StyleRange stylerangeImg = getImgStyle(start);
		addImage(stylerangeImg, getImageInfo());
		ImagesMap.put(Integer.toString(start), MSG_TYPE_INFO);
		cajaLogging.setStyleRange(stylerangeImg);

		/* estilo texto */
		StyleRange stylerangeTxt = getTxtStyle(length, start);
		stylerangeTxt.foreground = new Color(cajaLogging.getDisplay(), RGB_INFO);

		cajaLogging.setStyleRange(stylerangeTxt);

	}

	/**
	 * @param length -
	 *            Longitud del mensaje a escribir
	 */
	private static void setErrorStyle(int length) {

		int start = cajaLogging.getText().length() - (length + 3);

		StyleRange stylerangeImg = getImgStyle(start);
		addImage(stylerangeImg, getImageError());
		ImagesMap.put(Integer.toString(start), MSG_TYPE_ERROR);
		cajaLogging.setStyleRange(stylerangeImg);

		/* estilo texto */
		StyleRange stylerangeTxt = getTxtStyle(length, start);
		stylerangeTxt.foreground = new Color(cajaLogging.getDisplay(), RGB_ERROR);

		cajaLogging.setStyleRange(stylerangeTxt);

	}

	private static StyleRange getTxtStyle(int length, int start) {
		StyleRange stylerangeTxt = new StyleRange();
		stylerangeTxt.start = start + 1;
		stylerangeTxt.length = length + 2;
		return stylerangeTxt;
	}

	private static StyleRange getImgStyle(int start) {
		StyleRange stylerangeImg = new StyleRange();

		stylerangeImg.start = start;
		stylerangeImg.length = 2;
		return stylerangeImg;
	}

	private static Image getImageInfo() {
		
		if (null == IMAGE_INFO) {			
			IMAGE_INFO = new Image(cajaLogging.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.INFO_IMG_PATH));
		}
		return IMAGE_INFO;
	}

	private static Image getImageError() {
		
		if (null == IMAGE_ERROR) {
			IMAGE_ERROR = new Image(cajaLogging.getDisplay(), Thread.currentThread().getContextClassLoader().getResourceAsStream(ImagesUtil.ERROR_IMG_PATH));
		}
		return IMAGE_ERROR;
	}

	private static void addImage(StyleRange style, Image image) {
		Rectangle rect = image.getBounds();
		style.metrics = new GlyphMetrics(rect.height, 0, rect.width);
	}

}

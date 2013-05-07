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
package net.esle.sinadura.gui.events;

import java.io.File;
import java.io.IOException;

import net.esle.sinadura.core.exceptions.ArchiverException;
import net.esle.sinadura.core.exceptions.Pkcs7Exception;
import net.esle.sinadura.core.model.Archiver;
import net.esle.sinadura.core.service.Pkcs7Service;
import net.esle.sinadura.core.util.FileUtil;
import net.esle.sinadura.gui.model.DocumentInfo;
import net.esle.sinadura.gui.util.DesktopUtil;
import net.esle.sinadura.gui.util.LanguageUtil;
import net.esle.sinadura.gui.util.LoggingDesktopController;
import net.esle.sinadura.gui.util.PropertiesUtil;
import net.esle.sinadura.gui.view.main.DocumentsTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

public class ViewPDFListener implements SelectionListener {

	private static Log log = LogFactory.getLog(ViewPDFListener.class);

	private DocumentsTable tablePDF = null;

	public ViewPDFListener(DocumentsTable t) {

		this.tablePDF = t;
	}

	@Override
	public void widgetSelected(SelectionEvent event) {

		if (tablePDF.getSelectedDocument() != null) {

			DocumentInfo pdfParameter = tablePDF.getSelectedDocument();

			if (pdfParameter.getMimeType() != null && pdfParameter.getMimeType().equals(FileUtil.MIMETYPE_P7S)) {

				try {
					File file = FileUtil.getLocalFileFromURI(pdfParameter.getPath());
					byte[] bytes = FileUtil.getBytesFromFile(file);
					byte[] content = Pkcs7Service.getSignedContent(bytes);

					String name = generarNombreFichero(file.getName());
					String tmpFile = PropertiesUtil.TMP_FOLDER_PATH + File.separatorChar + name;
					
					FileUtil.bytesToFile(content, tmpFile);
					DesktopUtil.openSystemFile(tmpFile);
					
				} catch (IOException e) {
					log.error("controled exception", e);
					LoggingDesktopController.printError(LanguageUtil.getLanguage().getString("error.pkcs7.extract"));
				} catch (Pkcs7Exception e) {
					log.error("controled exception", e);
					LoggingDesktopController.printError(LanguageUtil.getLanguage().getString("error.pkcs7.extract"));
				}
				
			} else if (pdfParameter.getMimeType() != null && pdfParameter.getMimeType().equals(FileUtil.MIMETYPE_SAR)) {
				
				try {
					Archiver archiver = new Archiver(pdfParameter.getPath());
					String path = archiver.getDocument();
						
					DesktopUtil.openSystemFile(path);
					// se tiene que quedar abierto el archiver
				
				} catch (ArchiverException e) {
					log.error(e);
				}
			} else {
				DesktopUtil.openSystemFile(pdfParameter.getPath());
			}

		} else {
			log.error(LanguageUtil.getLanguage().getString("error.no_selected_file"));
			LoggingDesktopController.printError(LanguageUtil.getLanguage().getString("error.no_selected_file"));
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent event) {
		widgetSelected(event);
	}
	
	/*****************************************
	 * FIXES varios para intentar arreglar la
	 * renombración de ficheros que hace el Outlook
	 * // TODO generar la extensión por mime-type generado con el base64
	 * #13179
	 *****************************************/
	private static String generarNombreFichero(String filename){
		String name = filename;
		name = name.substring(0, name.lastIndexOf("."));
		
		// si acaba con número, quitamos el último número
		name = name.replaceAll("[0-9]$", "");
		
		// si lo que hay tras el primer espacio no es un '.' de extensión, lo generamos
		int indexOfLastDot = name.lastIndexOf(".");
		int indexOfLastSpace = name.lastIndexOf(" ");

		if (indexOfLastDot < indexOfLastSpace){
			// no existe replaceLast
			char[] nameInArray = name.toCharArray();
			nameInArray[indexOfLastSpace] = '.';
			name = new String(nameInArray);
		}
		
		log.info("Procedemos a abrir fichero: " + name);
		return name;
	}
	
	public static void main(String[] args){
		System.out.println(generarNombreFichero("Hello.world.txt1.ps7"));
		System.out.println(generarNombreFichero("Hello.world pdf.ps7"));
		System.out.println(generarNombreFichero("Hello world pdf.ps7"));
	}
}

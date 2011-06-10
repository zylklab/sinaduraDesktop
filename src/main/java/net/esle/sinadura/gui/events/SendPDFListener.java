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


import java.util.ArrayList;
import java.util.List;

import net.esle.sinadura.gui.model.DocumentInfo;
import net.esle.sinadura.gui.util.DesktopUtil;
import net.esle.sinadura.gui.util.LanguageUtil;
import net.esle.sinadura.gui.util.LoggingDesktopController;
import net.esle.sinadura.gui.view.main.DocumentsTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

public class SendPDFListener implements SelectionListener {
	
	private static Log log = LogFactory.getLog(SendPDFListener.class);
	
	private DocumentsTable tablePDF = null;
	
	public SendPDFListener (DocumentsTable t) {
		
		this.tablePDF = t;
	}
	
	public void widgetSelected(SelectionEvent event) {
		
		if (tablePDF.getSelectedDocuments().size() != 0) {
			
			List<String> attachemntList = new ArrayList<String>();
			for (DocumentInfo pdfParameter : tablePDF.getSelectedDocuments()) {
				
				attachemntList.add(pdfParameter.getPath());
			}
				
			DesktopUtil.openDefaultMailClient(attachemntList);
		}
		else {
			log.error(LanguageUtil.getLanguage().getString("error.no_selected_file"));
			LoggingDesktopController.printError(LanguageUtil.getLanguage().getString("error.no_selected_file"));
		}
	}

	public void widgetDefaultSelected(SelectionEvent event) {
		widgetSelected(event);
	}
}
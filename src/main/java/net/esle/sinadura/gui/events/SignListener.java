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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import net.esle.sinadura.gui.controller.SignControllerHelper;
import net.esle.sinadura.gui.model.DocumentInfo;
import net.esle.sinadura.gui.util.LanguageUtil;
import net.esle.sinadura.gui.util.LoggingDesktopController;
import net.esle.sinadura.gui.view.main.DocumentsTable;

public class SignListener implements SelectionListener {

	private static Log log = LogFactory.getLog(SignListener.class);

	private DocumentsTable tablePDF = null;

	public SignListener(DocumentsTable t) {

		this.tablePDF = t;
	}

	public void widgetSelected(SelectionEvent event) {

		if (tablePDF.getDocuments() != null && tablePDF.getDocuments().size() != 0) {

			//=======================================
			// DocumentInfo list
			//=======================================
			List<DocumentInfo> list = new ArrayList<DocumentInfo>();
			if (tablePDF.getSelectedDocuments().size() == 0) {
				list = tablePDF.getDocuments();
			} else {
				list = tablePDF.getSelectedDocuments();
			}
			
			SignControllerHelper.signDocuments(list, tablePDF.getShell());

			// refrescar tabla
			tablePDF.reloadTable();

		} else {
			LoggingDesktopController.printError(LanguageUtil.getLanguage().getString("error.no_selected_file"));
		}
	}

	public void widgetDefaultSelected(SelectionEvent event) {
		widgetSelected(event);
	}
}
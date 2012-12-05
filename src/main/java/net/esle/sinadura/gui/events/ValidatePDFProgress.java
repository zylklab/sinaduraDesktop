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

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.List;

import net.esle.sinadura.core.util.FileUtil;
import net.esle.sinadura.gui.controller.ValidateController;
import net.esle.sinadura.gui.model.DocumentInfo;
import net.esle.sinadura.gui.util.LanguageUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;


public class ValidatePDFProgress implements IRunnableWithProgress {
	
	private static final Log log = LogFactory.getLog(ValidatePDFProgress.class);

	private List<DocumentInfo> pdfParameterList = null;

	public ValidatePDFProgress(List<DocumentInfo> pdfParameter) {
		
		this.pdfParameterList = pdfParameter;
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		for (DocumentInfo pdfParameter : this.pdfParameterList) {

			if (!monitor.isCanceled()) {

				String m2 = null;
				m2 = MessageFormat.format(LanguageUtil.getLanguage().getString("info.document.validating"), FileUtil.getLocalPathFromURI(pdfParameter.getPath()));
				monitor.beginTask(m2, IProgressMonitor.UNKNOWN);

				ValidateController.validate(pdfParameter);

			} else {

				throw new InterruptedException("The long running operation was cancelled");
			}
		}

		monitor.done();
	}

}
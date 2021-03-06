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

/**
 * @author zylk.net
 */

import java.io.File;

import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;

/**
 * A field editor for a directory path type preference. A standard directory dialog appears when the user presses the
 * change button.
 */
public class GenericDirectoryFieldEditor extends StringButtonFieldEditor {
	/**
	 * Creates a new directory field editor
	 */
	protected GenericDirectoryFieldEditor() {
	}

	/**
	 * Creates a directory field editor.
	 * 
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 */
	public GenericDirectoryFieldEditor(String name, String labelText, Composite parent) {
		init(name, labelText);
		setErrorMessage(JFaceResources.getString("DirectoryFieldEditor.errorMessage"));//$NON-NLS-1$
		setChangeButtonText(JFaceResources.getString("openBrowse"));//$NON-NLS-1$
		setValidateStrategy(VALIDATE_ON_FOCUS_LOST);
		createControl(parent);
		// añadido griddata a la label
		Control[] array = parent.getChildren();
		GridData gd = new GridData();
		gd.widthHint = 150;
		if (array.length > 0)
			array[0].setLayoutData(gd);

	}

	/*
	 * (non-Javadoc) Method declared on StringButtonFieldEditor. Opens the directory chooser dialog and returns the
	 * selected directory.
	 */
	@Override
	protected String changePressed() {
		File f = new File(getTextControl().getText());
		if (!f.exists())
			f = null;
		File d = getDirectory(f);
		if (d == null)
			return null;

		return d.getAbsolutePath();
	}

	/*
	 * (non-Javadoc) Method declared on StringFieldEditor. Checks whether the text input field contains a valid
	 * directory.
	 */
	@Override
	protected boolean doCheckState() {
		String fileName = getTextControl().getText();
		fileName = fileName.trim();
		if (fileName.length() == 0 && isEmptyStringAllowed())
			return true;
		File file = new File(fileName);
		return file.isDirectory();
	}

	/**
	 * Helper that opens the directory chooser dialog.
	 * 
	 * @param startingDirectory
	 *            The directory the dialog will open in.
	 * @return File File or <code>null</code>.
	 */
	private File getDirectory(File startingDirectory) {

		DirectoryDialog fileDialog = new DirectoryDialog(getShell(), SWT.OPEN);
		if (startingDirectory != null)
			fileDialog.setFilterPath(startingDirectory.getPath());
		String dir = fileDialog.open();
		if (dir != null) {
			dir = dir.trim();
			if (dir.length() > 0)
				return new File(dir);
		}

		return null;
	}
}

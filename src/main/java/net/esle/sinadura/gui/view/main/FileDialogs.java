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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.esle.sinadura.core.util.FileUtil;
import net.esle.sinadura.gui.util.LanguageUtil;
import net.esle.sinadura.gui.util.PreferencesUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class FileDialogs {

	private static Log log = LogFactory.getLog(FileDialogs.class);

	
//	public static String saveFileDialog(Shell sShell, String tipo) {
//
//		FileDialog archivoDialog = new FileDialog(sShell, SWT.SAVE);
//		archivoDialog.setText(LanguageResource.getLanguage().getString("save.dialog.title"));
//		archivoDialog.setFilterNames(namesMap.get(tipo));
//		archivoDialog.setFilterExtensions( extensionsMap.get(tipo));
////		archivoDialog.setOverwrite(true); new SWT.jar needed
//		String selectedFile = archivoDialog.open();
//		if (selectedFile != null && !selectedFile.endsWith("." + tipo)) {
//			selectedFile = selectedFile + "." + tipo;
//		}
//		return selectedFile;
//	}

	
//  just one file
	public static String openFileDialog(Shell sShell, String extension, boolean allFilter) {
		
		return openFileDialog(sShell, new String[] {extension}, allFilter);
	}
	
//  just one file
	public static String openFileDialog(Shell sShell, String[] extensions, boolean allFilter) {

		FileDialog archivoDialog = new FileDialog(sShell, SWT.OPEN);
		archivoDialog.setFilterPath(PreferencesUtil.getString(PreferencesUtil.FILEDIALOG_PATH));
		archivoDialog.setText(LanguageUtil.getLanguage().getString("open.dialog.title"));
		
		List<String> names = new ArrayList<String>();
		List<String> extensionsList = new ArrayList<String>();
		
		for (String extension : extensions) {
			names.add("." + extension);
			extensionsList.add("*." + extension);
		}
		if (allFilter) {
			names.add("All files");
			extensionsList.add("*.*");
		}
		
		archivoDialog.setFilterNames((String[]) names.toArray(new String[names.size()]));
		archivoDialog.setFilterExtensions((String[]) extensionsList.toArray(new String[names.size()]));
		
		String selectedFile = archivoDialog.open();
		PreferencesUtil.savePreference(PreferencesUtil.FILEDIALOG_PATH, archivoDialog.getFilterPath());
		
		if (selectedFile != null) {
			File f = new File(selectedFile);
			if (!f.exists()) {
				selectedFile = null;
			}
		}
		return selectedFile;
	}
	
	// TODO quitar este y añadir param de entrada "multi"
	// multiple files
	public static List<File> openFilesDialog(Shell sShell, String extension, boolean allFilter) {
		
		return openFilesDialog(sShell, new String[] {extension}, allFilter);
	}
	
	// TODO quitar este y añadir param de entrada "multi"
	// multiple files
	public static List<File> openFilesDialog(Shell sShell, String[] extensions, boolean allFilter) {

		FileDialog archivoDialog = new FileDialog(sShell, SWT.MULTI);
		archivoDialog.setFilterPath(PreferencesUtil.getString(PreferencesUtil.FILEDIALOG_PATH));
		archivoDialog.setText(LanguageUtil.getLanguage().getString("open.dialog.title"));
		
		List<String> names = new ArrayList<String>();
		List<String> extensionsList = new ArrayList<String>();
		
		if (allFilter) {
			names.add("All files");
			extensionsList.add("*.*");
		}
		
		for (String extension : extensions) {
			names.add("." + extension);
			extensionsList.add("*." + extension);
		}
		
		archivoDialog.setFilterNames((String[]) names.toArray(new String[names.size()]));
		archivoDialog.setFilterExtensions((String[]) extensionsList.toArray(new String[names.size()]));
		
		String selectedFile = archivoDialog.open();
		PreferencesUtil.savePreference(PreferencesUtil.FILEDIALOG_PATH, archivoDialog.getFilterPath());
		
		List<File> fileList = new ArrayList<File>();
		if (selectedFile != null) {
			
			String[] selectedFiles = archivoDialog.getFileNames();
			for (int i = 0; i < selectedFiles.length; i++) {
				File f = new File(archivoDialog.getFilterPath() + File.separatorChar + selectedFiles[i]);
				if (f.exists())
					fileList.add(f);
			}
		}
		return fileList;
	}

	// TODO este deberia de devolver el directorio, y llamar fuera a getFilesFromDir
	public static List<File> openDirDialog(Shell sShell) {

		DirectoryDialog dirDialog = new DirectoryDialog(sShell, SWT.OPEN);
		dirDialog.setFilterPath(PreferencesUtil.getString(PreferencesUtil.FILEDIALOG_PATH));
		dirDialog.setText(LanguageUtil.getLanguage().getString("open.dialog.title"));
		String dir = dirDialog.open();
		PreferencesUtil.savePreference(PreferencesUtil.FILEDIALOG_PATH, dirDialog.getFilterPath());
		
		List<File> fileList = new ArrayList<File>();
		
		boolean addDirRecursive = PreferencesUtil.getBoolean(PreferencesUtil.ADD_DIR_RECURSIVE);
		
		if (dir != null) {
			File dirFile = new File(dir);
			fileList = FileUtil.getFilesFromDir(dirFile, addDirRecursive);
		}
		
		return fileList;
	}

}


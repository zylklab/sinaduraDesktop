package net.esle.sinadura.gui.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.esle.sinadura.gui.Sinadura;
import net.esle.sinadura.gui.model.DocumentInfo;
import net.esle.sinadura.core.util.FileUtil;

public class DocumentInfoUtil {

	private static Log log = LogFactory.getLog(DocumentInfoUtil.class);
	
	
	public static DocumentInfo fileToDocumentInfo(String filePath) {
		
		File file = new File(filePath);
		return fileToDocumentInfo(file);
	}
	
	
	public static DocumentInfo fileToDocumentInfo(File file) {

		DocumentInfo d = new DocumentInfo();
		d.setPath(file.getAbsolutePath());
		String mimeType = FileUtil.getMimeType(file.getAbsolutePath());
		d.setMimeType(mimeType);
		return d;
	}
	
	public static List<DocumentInfo> fileToDocumentInfo(List<File> files) {
		
		List<DocumentInfo> list = new ArrayList<DocumentInfo>();
		for (File file : files) {			
			list.add(fileToDocumentInfo(file));
		}		
		return list;
	}
	
}

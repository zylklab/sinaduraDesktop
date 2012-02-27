package net.esle.sinadura.gui.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.esle.sinadura.core.util.FileUtil;
import net.esle.sinadura.gui.model.DocumentInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.xml.utils.URI;
import org.apache.xml.utils.URI.MalformedURIException;

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
	
	
	public static List<DocumentInfo> fileToDocumentInfoFromUris(List<String> files) throws FileSystemException, MalformedURIException {
		
		List<DocumentInfo> list = new ArrayList<DocumentInfo>();
		for (String file : files) {			
			list.add(uriToDocumentInfo(file));
		}		
		return list;
	}
	
	public static DocumentInfo uriToDocumentInfo(String uri) throws FileSystemException, MalformedURIException {
		
		DocumentInfo d = new DocumentInfo();
		d.setPath(uri);
		String mimeType = FileUtil.getMimeType((new URI(uri)).getPath());
		d.setMimeType(mimeType);
		return d;
	}
	
}


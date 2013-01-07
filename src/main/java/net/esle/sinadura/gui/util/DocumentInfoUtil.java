package net.esle.sinadura.gui.util;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import net.esle.sinadura.core.util.FileUtil;
import net.esle.sinadura.gui.model.DocumentInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileSystemException;

public class DocumentInfoUtil {

	private static Log log = LogFactory.getLog(DocumentInfoUtil.class);
	
	
	public static DocumentInfo fileToDocumentInfo(String filePath) throws FileSystemException {
		
		File file = new File(filePath);
		return fileToDocumentInfo(file);
	}
	
	public static DocumentInfo fileToDocumentInfo(File file) throws FileSystemException {
		return uriToDocumentInfo(file.getPath());
	}
	
	
	/**************************************************
	 * Canal de entrada 1. precarga / carga
	 * (Canal de entrada 2. DocumentsTable.reload (el propio documento firmado)) 
	 * Normalización de path a URI
	 * @see FileUtil#normaliceLocalURI(String)
	 ***************************************************/
	public static DocumentInfo uriToDocumentInfo(String path) throws FileSystemException{

		log.info("Path sin normalizar:  " + path);
		URI uri = null;
		try{
			path = FileUtil.normaliceLocalURI(path);
			uri = new URI(path);
		}catch(URISyntaxException e){
			e.printStackTrace();
		}

		// document info
		DocumentInfo d = new DocumentInfo();
		d.setPath(path);
		log.info("Path normalizado: " + path);
		String mimeType = FileUtil.getMimeType(uri.getPath());
		d.setMimeType(mimeType);
		return d;
	}
	
	
	public static List<DocumentInfo> fileToDocumentInfo(List<File> files) throws FileSystemException {
		
		List<DocumentInfo> list = new ArrayList<DocumentInfo>();
		for (File file : files) {			
			list.add(fileToDocumentInfo(file));
		}		
		return list;
	}
	
	
	public static List<DocumentInfo> fileToDocumentInfoFromUris(List<String> files) throws FileSystemException {
		
		List<DocumentInfo> list = new ArrayList<DocumentInfo>();
		for (String file : files) {			
			list.add(uriToDocumentInfo(file));
		}		
		return list;
	}
}

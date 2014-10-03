package net.esle.sinadura.gui.model;

import net.esle.sinadura.core.model.PdfSignaturePreferences;



/**
 * Container para retornar los resultados de análisis de pdf stamp 
 */
public class PdfProfileResolution {

	private DocumentInfo doc;
	private boolean hasResolution = false;
	private PdfSignaturePreferences preferences;

	/**
	 * without preference resolution
	 */
	public PdfProfileResolution(DocumentInfo doc){
		this.doc = doc;
	}
	
	/**
	 * with preference but hasResolution (default: true)
	 */
	public PdfProfileResolution(DocumentInfo doc, PdfSignaturePreferences preferences){
		this(doc, preferences, true);
	}
	
	public PdfProfileResolution(DocumentInfo doc, PdfSignaturePreferences preferences, boolean hasResolution){
		
		this.doc = doc;
		this.preferences = preferences;
		this.hasResolution = hasResolution;
	}
	
	public DocumentInfo getDoc() {
		return doc;
	}
	public PdfSignaturePreferences getPreferences() {
		return preferences;
	}
	public boolean hasResolution(){
		return hasResolution;
	}
}




package net.esle.sinadura.gui.util;

import java.io.IOException;
import java.security.KeyStore.PasswordProtection;
import java.util.ArrayList;
import java.util.List;

import net.esle.sinadura.core.model.PdfSignaturePreferences;
import net.esle.sinadura.gui.model.DocumentInfo;
import net.esle.sinadura.gui.model.PdfProfileResolution;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itextpdf.text.pdf.PdfException;
import com.itextpdf.text.pdf.PdfReader;


/*
 * TODO mover al core > hay que refactorizar PreferencesUtil (desktop) extends PreferenceUtil (core)
 */
public class PdfUtils {

	private static Log log = LogFactory.getLog(PdfUtils.class);
	
	public static List<PdfProfileResolution> getPdfStampResolutionOptions(List<DocumentInfo> documents) throws PdfException{

		List<PdfSignaturePreferences> availableProfiles = PreferencesUtil.getPdfProfiles();

		List<PdfProfileResolution> fields = new ArrayList<PdfProfileResolution>();
		for (DocumentInfo doc: documents){
			try{
				// TODO gestionar el ownerPassword?
				PasswordProtection pwdProtection = null;
				byte[] ownerPassword = null;
	//				if ((pwdProtection != null) && (pwdProtection.getPassword() != null)) {
	//					ownerPassword = new String(pwdProtection.getPassword()).getBytes();
	//				}
	
				PdfReader reader;
				if (doc.getPath() != null){
					reader = new PdfReader(doc.getPath(), ownerPassword);
					
					// documento con acrofield
					if (reader.getAcroFields().getFields().size() > 0){
						for (String usedAcroFieldName : reader.getAcroFields().getFields().keySet()){
							boolean hasResolution = false;
							for (PdfSignaturePreferences availableProfile : availableProfiles){
								if (availableProfile.getAcroField().toLowerCase().contains(usedAcroFieldName)){
									fields.add(new PdfProfileResolution(doc, availableProfile));
									hasResolution = true;
								}
							}
							
							// documento con acrofield no resuelto
							if (!hasResolution){
								fields.add(new PdfProfileResolution(doc, new PdfSignaturePreferences(usedAcroFieldName), false));
							}
						}
						
					// documento sin acrofield
					}else{
						fields.add(new PdfProfileResolution(doc));
					}
				}	
			}catch(IOException e){
				// TODO si es un error tipo no es pdf, no se loguea el error ni se lanza excepcion
				log.error(e);
				throw new PdfException(e);
			}
		}
		return fields;
	}
}

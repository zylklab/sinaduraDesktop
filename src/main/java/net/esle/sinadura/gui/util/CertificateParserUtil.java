package net.esle.sinadura.gui.util;

import java.io.IOException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.X509Principal;

public class CertificateParserUtil {
	
	private static Log log = LogFactory.getLog(CertificateParserUtil.class);
	
	public static String getKeyUsage(X509Certificate cert) {
		
		String s = "";
		
		// key usage
		boolean[] usage = cert.getKeyUsage();
		
		List<String> extended = null;
		try {
			extended = cert.getExtendedKeyUsage();
		} catch (CertificateParsingException e) {
			log.error("", e);
		}
		
		if (usage != null && extended != null) {
			for (int j = 0 ; j< usage.length; j++) {
				
				if (j == 0 && usage[j]) 
					s += "Digital signature, ";
				if (j == 1 && usage[j]) 
					s += "Non repudiation, ";
				if (j == 2 && usage[j]) 
					s += "Key encipherment, ";
				if (j == 3 && usage[j]) 
					s += "Data encipherment, ";
				if (j == 4 && usage[j]) 
					s += "Key agreement, ";
				if (j == 5 && usage[j]) 
					s += "KeyCert sign, ";
				if (j == 6 && usage[j]) 
					s += "CRL sign, ";
				if (j == 7 && usage[j]) 
					s += "Encipher only, ";
				if (j == 8 && usage[j]) 
					s += "Decipher only, ";
			}
			
			for (String ext : extended) {
				
				if (ext.equals("1.3.6.1.5.5.7.3.1"))
					s += "Server authentication , " ;
				if (ext.equals("1.3.6.1.5.5.7.3.2"))
					s += "Client authentication, " ;
				if (ext.equals("1.3.6.1.5.5.7.3.3"))
					s += "Code signing, " ;
				if (ext.equals("1.3.6.1.5.5.7.3.4"))
					s += "E-mail protection , " ;
				if (ext.equals("1.3.6.1.5.5.7.3.5"))
					s += "IP security end system, " ;
				if (ext.equals("1.3.6.1.5.5.7.3.6"))
					s += "IP security tunnel termination, " ;
				if (ext.equals("1.3.6.1.5.5.7.3.7"))
					s += "IP security user, " ;
				if (ext.equals("1.3.6.1.5.5.7.3.8"))
					s += "Timestamping, " ;
				if (ext.equals("1.3.6.1.5.5.7.3.9"))
					s += "OCSP signing , " ;
			}
			
		}
		
		return s;
	}
	
	public static String getIssuerDescription(X509Certificate cert) throws IOException {
		return getDescription(new X509Principal(cert.getIssuerX500Principal().getEncoded()));
	}
	
	public static String getSubjectDescription(X509Certificate cert) throws IOException {
		return getDescription(new X509Principal(cert.getSubjectX500Principal().getEncoded()));
	}
	
	private static String getDescription(X509Principal x509Principal) {
		String s = "";
		
		Vector<String> vec = x509Principal.getValues();
		for (String value : vec) {
			
			s += value + " - ";
			
		}
		
		return s;
	}
}
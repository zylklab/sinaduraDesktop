package net.esle.sinadura.gui.util;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StatisticsUtil {

	private static Log log = LogFactory.getLog(StatisticsUtil.class);
	private static Log logStats = LogFactory.getLog("Statistics.Sinadura");
	
	// Si se modifica el separador en esta clase, también hay que hacerlo en el fichero de propiedades de log4j (log4j.properties)
	private static final String CSV_SEPARATOR = ";";
	private static final String MAC;

	// KEYS
	public static final String KEY_SINADURA_VERSION = "KEY_SINADURA_VERSION";

	public static final String KEY_SO = "KEY_SO";
	public static final String KEY_SO_VERSION = "KEY_SO_VERSION";
	public static final String KEY_SO_ARCHITECTURE = "KEY_SO_ARCHITECTURE";
	public static final String KEY_SO_LOCALE_LANGUAGE = "KEY_SO_LOCALE_LANGUAGE";
	public static final String KEY_SO_LOCALE_COUNTRY = "KEY_SO_LOCALE_COUNTRY";
	public static final String KEY_JAVA_VENDOR = "KEY_JAVA_VENDOR";
	public static final String KEY_JAVA_VERSION = "KEY_JAVA_VERSION";

	public static final String KEY_SIGN_MIMETYPE = "KEY_SIGN_MIMETYPE";
	public static final String KEY_SIGN_DOCUMENT_EXTENSION = "KEY_SIGN_DOCUMENT_EXTENSION";
	public static final String KEY_SIGN_DOCUMENT_SIZE = "KEY_SIGN_DOCUMENT_SIZE";
	public static final String KEY_SIGN_TSA = "KEY_SIGN_TSA";
	public static final String KEY_SIGN_OCSP = "KEY_SIGN_OCSP";
	public static final String KEY_SIGN_ISSUER = "KEY_SIGN_ISSUER";
	
	public static final String KEY_SIGN_CERTTYPE = "KEY_SIGN_CERTTYPE";
	public static final String KEY_LOAD_HARDWAREDRIVER = "KEY_LOAD_HARDWAREDRIVER";
	
	public static final String KEY_ADD_CACHECERT = "KEY_ADD_CACHECERT";
	public static final String KEY_ADD_TRUSTEDCERT = "KEY_ADD_TRUSTEDCERT";

	public static final String KEY_CLOSING_SINADURA = "KEY_CLOSING_SINADURA";

	// VALUES 
	// de la key KEY_SIGN_CERTTYPE
	public static final String VALUE_SOFT = "Software";
	public static final String VALUE_MSCAPI = "MSCapi";
	public static final String VALUE_HARD = "Hardware";
	
	static {
		MAC = getMacAddress();
		log.info("mac adress: " + MAC);
	}

	public static void log(String key) {

		log(key, "");
	}
	
	public static void log(String key, String value) {
		
		if (value != null) {
			value = value.replace("\"", "\"\"");
		}

		logStats.info(StatisticsUtil.MAC + CSV_SEPARATOR + key + CSV_SEPARATOR + "\"" + value + "\"" + CSV_SEPARATOR);
	}

	private static String getMacAddress() {
		
		String mac = "";
		try {
			Enumeration<NetworkInterface> enumerationInterfaces = NetworkInterface.getNetworkInterfaces();
			while (enumerationInterfaces.hasMoreElements()) {
				NetworkInterface ni = enumerationInterfaces.nextElement();
				if (ni != null) {
					byte[] bytesMac = ni.getHardwareAddress();

					if (bytesMac != null && bytesMac.length > 0) {
						/*
						 * Extract each array of mac address and convert it to hexa with the following format 08-00-27-DC-4A-9E.
						 */
						for (int i = 0; i < bytesMac.length; i++) {
							mac += String.format("%02X%s", bytesMac[i], (i < bytesMac.length - 1) ? "-" : "");
						}
						break;
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return mac;
	}
}
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
package net.esle.sinadura.gui.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.esle.sinadura.core.util.KeystoreUtil;
import net.esle.sinadura.gui.exceptions.DriversNotFoundException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.util.Os;
import org.eclipse.jface.preference.PreferenceStore;


public class PreferencesUtil {

	private static Log log = LogFactory.getLog(PreferencesUtil.class);
	
	// *******************************
	// PREFERENCES KEYS
	
	// General
	public static final String IDIOMA = "idioma";
	public static final String TOKEN_LOCALE = "_";
	public static final String OUTPUT_AUTO_ENABLE = "output.auto.enable";
	public static final String OUTPUT_DIR = "output.dir";
	public static final String SAVE_EXTENSION = "save.extension";
	public static final String AUTO_VALIDATE = "auto.validate";
	public static final String ADD_DIR_RECURSIVE = "add.dir.recursive";
	public static final String ENABLE_STATISTICS = "enable.statistics";

	// Proxy
	public static final String PROXY_USER = "proxy.http.user";
	public static final String PROXY_PASS = "proxy.http.pass";
	public static final String PROXY_SYSTEM = "proxy.http.system";
	
	// Certifications
	public static final String CERT_TYPE = "preferencias.radioCertType.active";
	public static final String CERT_TYPE_VALUE_SOFTWARE = "0";
	public static final String CERT_TYPE_VALUE_HARDWARE = "1";
	public static final String CERT_TYPE_VALUE_MSCAPI = "2";
	
	public static final String HARDWARE_DISPOSITIVE = "hardware.dispositive";
	public static final String SOFTWARE_DISPOSITIVE = "software.dispositive";

	// Opciones certificados
	public static final String APLICAR_PREFERENCIAS_USAGE_CERT = "certificado.aplicar.preferencias";

	// Sign
	public static final String SIGN_TS_ENABLE = "sign.ts.enable";
	public static final String SIGN_TS_TSA = "sign.ts.tsa";
	public static final String SIGN_OCSP_ENABLE = "sign.ocsp.enable";
	
	// Pdf
	public static final String PDF_TIPO = "pdf.tipo";
	public static final String PDF_TIPO_PDF = "0";
	public static final String PDF_TIPO_XML = "1";
	public static final String PDF_VISIBLE = "pdf.visible";
	public static final String PDF_PAGE = "pdf.page";
	public static final String PDF_REASON = "pdf.reason";
	public static final String PDF_LOCATION = "pdf.location";
	public static final String PDF_STAMP_ENABLE = "pdf.stamp.enable";
	public static final String PDF_STAMP_PATH = "pdf.stamp.path";
	public static final String PDF_STAMP_WIDTH = "pdf.stamp.width";
	public static final String PDF_STAMP_HEIGHT = "pdf.stamp.height";
	public static final String PDF_STAMP_X = "pdf.stamp.x";
	public static final String PDF_STAMP_Y = "pdf.stamp.y";
	public static final String PDF_CERTIFIED = "pdf.certified";
	
	// xades
	public static final String XADES_ARCHIVE = "xades.archive";
	public static final String XADES_XL_OCSP_ADD_ALL = "xades.xl.ocsp.add_all";
	public static final String XADES_VALIDATOR_IMPL = "xades.validator.impl";
	
	// validation
	public static final String VALIDATION_CHECK_REVOCATION = "validation.check_revocation";
	public static final String VALIDATION_CHECK_POLICY = "validation.check_policy";
	public static final String VALIDATION_CHECK_NODE_NAME = "validation.check_node_name";

	// FileDialogs path
	public static final String FILEDIALOG_PATH = "filedialog.path";
	
	
	// *******************************
	// PATHS
	private static final String USER_HOME = "user.home";
	
	// preferencias
	private static final String FOLDER_PREFERENCES_PATH = PropertiesUtil.USER_BASE_PATH + File.separatorChar + "preferences";
	
	private static final String PATH_USER_PREFERENCES_MAIN = FOLDER_PREFERENCES_PATH + File.separatorChar + "preferences.properties";
	private static final String PATH_USER_PREFERENCES_SOFTWARE = FOLDER_PREFERENCES_PATH + File.separatorChar + "software-preferences.csv";
	private static final String PATH_USER_PREFERENCES_TRUSTED_KEYSTORE = FOLDER_PREFERENCES_PATH + File.separatorChar + "trusted.jks";
	private static final String PATH_USER_PREFERENCES_CACHE_KEYSTORE = FOLDER_PREFERENCES_PATH + File.separatorChar + "cache.jks";
	
	// default files (se cargan por classloader)
	private static final String PACKAGE_PATH = "preferences";
	private static final String PATH_DEFAULT_PREFERENCES_SOFTWARE = PACKAGE_PATH + "/" + "software-preferences.csv";
	private static final String PATH_DEFAULT_PREFERENCES_HARDWARE = PACKAGE_PATH + "/" + "hardware-preferences.csv";
	private static final String PATH_DEFAULT_PREFERENCES_TIMESTAMP = PACKAGE_PATH + "/"+ "timestamp-preferences.csv";
	private static final String PATH_DEFAULT_PREFERENCES_TRUSTED_KEYSTORE = PACKAGE_PATH + "/" + "trusted.jks";
	private static final String PATH_DEFAULT_PREFERENCES_CACHE_KEYSTORE = PACKAGE_PATH + "/" + "cache.jks";
	
	
	// STATIC ATRIBUTES
	private static PreferenceStore preferences = null;
	private static Map<String, String> softwarePrefs = null;
	private static Map<String, HardwareItem> hardwarePrefs = null;
	private static Map<String, TimestampItem> timestampPrefs = null;
	private static KeyStore trustedKeystore = null;
	private static KeyStore cacheKeystore = null;
	
	
	static {
		
		// base (home)
		File f = new File(PropertiesUtil.USER_BASE_PATH);
		if (!f.exists()) {
			f.mkdir();
		}
		
		// folder (preferences)
		f = new File(FOLDER_PREFERENCES_PATH);
		if (!f.exists()) {
			f.mkdir();
		}
		
		// main (preferences.properties)
		f = new File(PATH_USER_PREFERENCES_MAIN);
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				log.error("", e);
			}
		}
		
		// software
		f = new File(PATH_USER_PREFERENCES_SOFTWARE);
		if (!f.exists()) {
			try {
				InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(PATH_DEFAULT_PREFERENCES_SOFTWARE);
				FileOutputStream os = new FileOutputStream(PATH_USER_PREFERENCES_SOFTWARE);
				IOUtils.copy(is, os);	
			} catch (IOException e) {
				log.error("", e);
			}
		}
		
		// ks trust
		f = new File(PATH_USER_PREFERENCES_TRUSTED_KEYSTORE);
		if (!f.exists()) {
			try {
				InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(PATH_DEFAULT_PREFERENCES_TRUSTED_KEYSTORE);
				FileOutputStream os = new FileOutputStream(PATH_USER_PREFERENCES_TRUSTED_KEYSTORE);
				IOUtils.copy(is, os);	
			} catch (IOException e) {
				log.error("", e);
			}
		}
		
		// ks cache
		f = new File(PATH_USER_PREFERENCES_CACHE_KEYSTORE);
		if (!f.exists()) {
			try {
				InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(PATH_DEFAULT_PREFERENCES_CACHE_KEYSTORE);
				FileOutputStream os = new FileOutputStream(PATH_USER_PREFERENCES_CACHE_KEYSTORE);
				IOUtils.copy(is, os);	
			} catch (IOException e) {
				log.error("", e);
			}
		}
		
		
		
	}
	
	public static PreferenceStore getPreferences() {

		if (preferences == null) {
			
			try {
				preferences = new PreferenceStore(PATH_USER_PREFERENCES_MAIN);
				preferences.load();

				// VALORES POR DEFECTO
				
				// General
				preferences.setDefault(IDIOMA, PreferencesDefaultUtil.get(IDIOMA));
				preferences.setDefault(OUTPUT_AUTO_ENABLE,PreferencesDefaultUtil.get(OUTPUT_AUTO_ENABLE)); // "true");
				preferences.setDefault(OUTPUT_DIR, System.getProperty(USER_HOME)); // esta necesita definirse en runtime
				/*
				 * "-signed" - sinadura
				 * ""  		 - parlamento
				 */
				preferences.setDefault(SAVE_EXTENSION, PreferencesDefaultUtil.get(SAVE_EXTENSION));
				preferences.setDefault(AUTO_VALIDATE, PreferencesDefaultUtil.get(AUTO_VALIDATE)); //"true");
				preferences.setDefault(ADD_DIR_RECURSIVE, PreferencesDefaultUtil.get(ADD_DIR_RECURSIVE)); //"false");
				preferences.setDefault(ENABLE_STATISTICS, PreferencesDefaultUtil.get(ENABLE_STATISTICS)); //"true");
				
				// Proxy
				preferences.setDefault(PROXY_USER, PreferencesDefaultUtil.get(PROXY_USER)); //"");
				preferences.setDefault(PROXY_PASS, PreferencesDefaultUtil.get(PROXY_PASS)); //"");
				preferences.setDefault(PROXY_SYSTEM, PreferencesDefaultUtil.get(PROXY_SYSTEM)); //"true");
				
				// Sign
				preferences.setDefault(SIGN_TS_ENABLE, PreferencesDefaultUtil.get(SIGN_TS_ENABLE)); //"true");
				preferences.setDefault(SIGN_TS_TSA, PreferencesDefaultUtil.get(SIGN_TS_TSA)); //"izenpe");
				preferences.setDefault(SIGN_OCSP_ENABLE, PreferencesDefaultUtil.get(SIGN_OCSP_ENABLE)); //"true");

				// Certificado
				preferences.setDefault(APLICAR_PREFERENCIAS_USAGE_CERT, PreferencesDefaultUtil.get(APLICAR_PREFERENCIAS_USAGE_CERT)); //true);
				
				// Pdf
				/*
				 * 0 (pdf) - sinadura
				 * 1 (xml) - parlamento
				 */
				preferences.setDefault(PDF_TIPO, PreferencesDefaultUtil.get(PDF_TIPO));
				preferences.setDefault(PDF_VISIBLE, PreferencesDefaultUtil.get(PDF_VISIBLE)); // "true");
				preferences.setDefault(PDF_PAGE, PreferencesDefaultUtil.get(PDF_PAGE)); // "1");
				preferences.setDefault(PDF_REASON, PreferencesDefaultUtil.get(PDF_REASON)); // "powered by zylk.net");
				preferences.setDefault(PDF_LOCATION, PreferencesDefaultUtil.get(PDF_LOCATION)); // "Bilbao");
				preferences.setDefault(PDF_STAMP_ENABLE, PreferencesDefaultUtil.get(PDF_STAMP_ENABLE)); // "true");
				preferences.setDefault(PDF_STAMP_PATH, PropertiesUtil.DEFAULT_IMAGE_FILE_PATH); // esta necesita definirse en runtime
				preferences.setDefault(PDF_STAMP_X, PreferencesDefaultUtil.get(PDF_STAMP_X)); // "20");
				preferences.setDefault(PDF_STAMP_Y, PreferencesDefaultUtil.get(PDF_STAMP_Y)); // "20");
				preferences.setDefault(PDF_STAMP_WIDTH, PreferencesDefaultUtil.get(PDF_STAMP_WIDTH)); // "125");
				preferences.setDefault(PDF_STAMP_HEIGHT, PreferencesDefaultUtil.get(PDF_STAMP_HEIGHT)); // "125");
				preferences.setDefault(PDF_CERTIFIED, PreferencesDefaultUtil.get(PDF_CERTIFIED)); // (PdfSignatureAppearance.NOT_CERTIFIED);
				
				// xades
				preferences.setDefault(XADES_ARCHIVE, PreferencesDefaultUtil.get(XADES_ARCHIVE)); //"true");
				preferences.setDefault(XADES_XL_OCSP_ADD_ALL, PreferencesDefaultUtil.get(XADES_XL_OCSP_ADD_ALL)); //"true");
				preferences.setDefault(XADES_VALIDATOR_IMPL, PreferencesDefaultUtil.get(XADES_VALIDATOR_IMPL));
				
				// validation
				preferences.setDefault(VALIDATION_CHECK_REVOCATION, PreferencesDefaultUtil.get(VALIDATION_CHECK_REVOCATION)); //"true");
				preferences.setDefault(VALIDATION_CHECK_POLICY, PreferencesDefaultUtil.get(VALIDATION_CHECK_POLICY)); //"true");
				preferences.setDefault(VALIDATION_CHECK_NODE_NAME, PreferencesDefaultUtil.get(VALIDATION_CHECK_NODE_NAME)); //"true");
				
				// carga de certificado
				if (Os.isFamily(Os.OS_FAMILY_WINDOWS.getName())) { // esta necesita definirse en runtime
					preferences.setDefault(CERT_TYPE, CERT_TYPE_VALUE_MSCAPI);
				} else{
					preferences.setDefault(CERT_TYPE, CERT_TYPE_VALUE_HARDWARE);					
				}

				preferences.setDefault(HARDWARE_DISPOSITIVE, PreferencesDefaultUtil.get(HARDWARE_DISPOSITIVE)); //"izenpe");
				
				// FileDialogs path
				preferences.setDefault(FILEDIALOG_PATH, System.getProperty(USER_HOME)); // esta necesita definirse en runtime
				
			} catch (IOException e) {
				
				log.error("", e);
			}
		}
		
		return preferences;
	}
	
	public static void savePreferences() {
	
		try {
			preferences.save();
	
		} catch (IOException e) {
			log.error("", e);
		}
		
	}
	
	public static String getDefaultHardware() throws DriversNotFoundException {
		
		Map<String, HardwareItem> map = PreferencesUtil.getHardwarePreferences();
		
		if (map != null && map.size() > 0) {
		
			HardwareItem driver = map.get(PreferencesUtil.getPreferences().getString(PreferencesUtil.HARDWARE_DISPOSITIVE));
			if (driver == null || driver.equals("")) {
				// sino devolver el primero
				driver = map.values().iterator().next();
			}
			
			return driver.getPath();
			
		} else {
			throw new DriversNotFoundException();
		}
	}

	
	
	public static Map<String, HardwareItem> getHardwarePreferences() {

		if (hardwarePrefs == null) {
			
			Map<String, HardwareItem> map = new TreeMap<String, HardwareItem>();

			try {
				
				// leer el csv
				InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(PATH_DEFAULT_PREFERENCES_HARDWARE);
				List<List<String>> array = CsvUtil.parseCSV(is);
		
				// generar el map
				String key, name, path, so;
				for (int i = 1; i < array.size(); i++) {
		
					List<String> list = array.get(i);
					key = list.get(0);
					name = list.get(1);
					path = list.get(2);
					so 	= list.get(3);
					File file = new File(path);
					
					/*
					 *  el dispositivo pertence al So actual
					 *  está instalado en path 
					 */
					if (pertenceSOActual(so) && file.exists()) {
						map.put(key, new HardwareItem(key, name, path, so));
					}
				}
				
			} catch (IOException e) {
				log.error("", e);
			}
			hardwarePrefs = map;
		}

		return hardwarePrefs;
	}
	
	
	/***************************************************************
	 * @return true si soCol pertence al sistema operativo actual
	 * osPreferencias  	linux, win, mac
	 ***************************************************************/
	private static boolean pertenceSOActual(String osPreferencias){
		return System.getProperty("os.name").toLowerCase().contains(osPreferencias);
	}
	
	
	private static Map<String, String> loadSoftwarePreferences() {

		Map<String, String> map = new TreeMap<String, String>();

		// leer el csv
		List<List<String>> array = CsvUtil.importCSV(PATH_USER_PREFERENCES_SOFTWARE);

		// generar el map
		for (int i = 1; i < array.size(); i++) {

			List<String> list = array.get(i);
			String name = list.get(0);
			String path = list.get(1);
			map.put(name, path);
		}

		return map;
	}
	
	
	public static Map<String, String> getSoftwarePreferences() {

		if (softwarePrefs == null) {
			softwarePrefs = loadSoftwarePreferences();
		} 
		
		return softwarePrefs;
	}
	
	public static void saveSoftwarePreferences(Map<String, String> map) {

		List<List<String>> array = new ArrayList<List<String>>();
		
		// header
		List<String> list = new ArrayList<String>();
		list.add(0, "name");
		list.add(1, "path");
		array.add(list);
		
		for (String name : map.keySet()) {
			
			String path = map.get(name);
			list = new ArrayList<String>();
			list.add(0, name);
			list.add(1, path);
			array.add(list);
		}
		
		CsvUtil.exportCSV(PATH_USER_PREFERENCES_SOFTWARE, array);
		
		softwarePrefs = map;

	}
	
	
	
	public static Map<String, TimestampItem> getTimestampPreferences() {

		if (timestampPrefs == null) {
			
			Map<String, TimestampItem> map = new TreeMap<String, TimestampItem>();

			try {
				
				// leer el csv
				InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(PATH_DEFAULT_PREFERENCES_TIMESTAMP);
				List<List<String>> array = CsvUtil.parseCSV(is);
		
				// generar el map
				String key, name, url, ocspUrl;
				for (int i = 1; i < array.size(); i++) {
		
					List<String> list = array.get(i);
					key = list.get(0);
					name = list.get(1);
					url = list.get(2);
					ocspUrl	= list.get(3);
					
					map.put(key, new TimestampItem(key, name, url, ocspUrl));
				}
				
			} catch (IOException e) {
				log.error("", e);
			}
			timestampPrefs = map;
		}

		return timestampPrefs;
	}
	
	// KEYSTORES
	
	private static KeyStore loadKeystorePreferences(String path) {
		
		KeyStore ks = null;
		try {
			ks = KeystoreUtil.loadKeystorePreferences(path, "sinadura");
			
		} catch (KeyStoreException e) {
			log.error("", e);
		} catch (NoSuchAlgorithmException e) {
			log.error("", e);
		} catch (CertificateException e) {
			log.error("", e);
		} catch (IOException e) {
			log.error("", e);
		}
		
		return ks;
	}
	
	
	public static KeyStore getTrustedKeystorePreferences() {

		if (trustedKeystore == null) {	
			trustedKeystore = loadKeystorePreferences(PATH_USER_PREFERENCES_TRUSTED_KEYSTORE);
		}		
		return trustedKeystore; 
	}

	public static KeyStore getCacheKeystorePreferences() {

		if (cacheKeystore == null) {
			cacheKeystore = loadKeystorePreferences(PATH_USER_PREFERENCES_CACHE_KEYSTORE);
		}
		return cacheKeystore; 
	}
	
	public static void setTrustedKeystorePreferences(KeyStore ks) {
		
		saveKeystorePreferences(ks, PATH_USER_PREFERENCES_TRUSTED_KEYSTORE);
		trustedKeystore = ks;
	}
		
	public static void setCacheKeystorePreferences(KeyStore ks) {
		
		saveKeystorePreferences(ks, PATH_USER_PREFERENCES_CACHE_KEYSTORE);
		cacheKeystore = ks;
	}
	
	private static void saveKeystorePreferences(KeyStore ks, String path) {
		
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(path);
			
		} catch (FileNotFoundException e) {
			log.error("", e);
		}
		
		char [] password = {'s','i','n','a','d','u','r','a'};
		
		try {
			ks.store(fos, password);
			
		} catch (KeyStoreException e) {
			log.error("", e);
		} catch (NoSuchAlgorithmException e) {
			log.error("", e);
		} catch (CertificateException e) {
			log.error("", e);
		} catch (IOException e) {
			log.error("", e);
		}
	}
	
	/**
	 *  userCache + aplicationCache
	 *   
	 * @return
	 */
	public static KeyStore getCacheKeystoreComplete() {

		// TODO Implementar bien (user + aplication). Tambien hay que cambiar el metodo que carga los keystores, para que no copien el
		// default al userhome en caso de que no exista.
		return getCacheKeystorePreferences(); // mock
	}
	
	/**
	 * userTrust + aplicationTrust
	 * 
	 * @return
	 */
	public static KeyStore getTrustedKeystoreComplete() {

		// TODO Implementar bien (user + aplication). Tambien hay que cambiar el metodo que carga los keystores, para que no copien el
		// default al userhome en caso de que no exista.
		return getTrustedKeystorePreferences(); // mock
	}

	
	// UTILS
	
	public static String getOutputName(String name) {
		
		return (name.substring(0, name.lastIndexOf("."))
				+ PreferencesUtil.getPreferences().getString(PreferencesUtil.SAVE_EXTENSION));	
	}
	
	public static String getOutputNameFromCompletePath(String name) 
	{
//		String name2 = name.substring(name.lastIndexOf(File.separatorChar)+1, name.length());
		String name2 = name.substring(name.lastIndexOf("/")+1, name.length());
		String sufijo = PreferencesUtil.getPreferences().getString(PreferencesUtil.SAVE_EXTENSION);
		
		name2 = (name2.substring(0, name2.lastIndexOf("."))) + sufijo;
		return 	name2;
	}
	
	public static String getOutputDir(File file) {
		
		if (PreferencesUtil.getPreferences().getBoolean(PreferencesUtil.OUTPUT_AUTO_ENABLE)) {
			return file.getParentFile().getPath();
		} else {
			return PreferencesUtil.getPreferences().getString(PreferencesUtil.OUTPUT_DIR);
		}
	}
	
	public static String getOutputDir(String filePath) {
		
		if (PreferencesUtil.getPreferences().getBoolean(PreferencesUtil.OUTPUT_AUTO_ENABLE)) 
		{
			return filePath.substring(0, filePath.lastIndexOf("/"));
//			return filePath.substring(0, filePath.lastIndexOf(File.separatorChar));	
		} else {
			return PreferencesUtil.getPreferences().getString(PreferencesUtil.OUTPUT_DIR);
		}
	}
	
}

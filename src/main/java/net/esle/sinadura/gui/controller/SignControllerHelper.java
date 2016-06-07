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
package net.esle.sinadura.gui.controller;

import java.lang.reflect.InvocationTargetException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Shell;

import net.esle.sinadura.core.certificate.CertificateUtil;
import net.esle.sinadura.core.exceptions.ConnectionException;
import net.esle.sinadura.core.exceptions.CoreException;
import net.esle.sinadura.core.exceptions.CorePKCS12Exception;
import net.esle.sinadura.core.exceptions.NoSunPkcs11ProviderException;
import net.esle.sinadura.core.exceptions.OCSPCoreException;
import net.esle.sinadura.core.exceptions.OCSPIssuerRequiredException;
import net.esle.sinadura.core.exceptions.OCSPUnknownUrlException;
import net.esle.sinadura.core.exceptions.PKCS11Exception;
import net.esle.sinadura.core.exceptions.PasswordCallbackCanceledException;
import net.esle.sinadura.core.exceptions.RevokedException;
import net.esle.sinadura.core.keystore.KeyStoreBuilderFactory;
import net.esle.sinadura.core.model.KsSignaturePreferences;
import net.esle.sinadura.core.util.ExceptionUtil;
import net.esle.sinadura.gui.events.LoadKeyStoreProgress;
import net.esle.sinadura.gui.events.LoadSlotProgress;
import net.esle.sinadura.gui.events.SignProgress;
import net.esle.sinadura.gui.exceptions.AliasesNotFoundException;
import net.esle.sinadura.gui.exceptions.DriversNotFoundException;
import net.esle.sinadura.gui.exceptions.SignProgressInterruptedException;
import net.esle.sinadura.gui.model.DocumentInfo;
import net.esle.sinadura.gui.util.LanguageUtil;
import net.esle.sinadura.gui.util.LoggingDesktopController;
import net.esle.sinadura.gui.util.PreferencesUtil;
import net.esle.sinadura.gui.util.StatisticsUtil;
import net.esle.sinadura.gui.view.main.AliasDialog;
import net.esle.sinadura.gui.view.main.SlotDialog;
import net.esle.sinadura.gui.view.preferences.CertSelectorRuntimePreferencesDialog;


public class SignControllerHelper {

	private static final Log log = LogFactory.getLog(SignControllerHelper.class);

	public static void signDocuments(List<DocumentInfo> list, Shell shell) {

		
		//=======================================
		// KeyStore Preferences
		//=======================================
		
		// alias
		String alias = null;
		KsSignaturePreferences ksSignaturePreferences = null;
		String certType = null;
		String certPath = null;
		
		try {
			
			try {
		
				// DIALOGO PARA IDENTIFICAR EL TIPO DE CERTIFICADO A UTILIZAR (la config)
				if (PreferencesUtil.getPreferences().getBoolean(PreferencesUtil.CERT_TYPE)) {
					
					// preguntar al usuario el tipo de certificado
					CertSelectorRuntimePreferencesDialog csrpd = new CertSelectorRuntimePreferencesDialog(shell);
					csrpd.open();
					
					if (csrpd.getCertType() != null) {
						certType = csrpd.getCertType();
						certPath = csrpd.getCertPath();
						
					} else {
						// cancelar
						throw new InterruptedException();
					}
				
				} else {
					
					certType = PreferencesUtil.getPreferences().getString(PreferencesUtil.CERT_TYPE);
	
					if (certType.equalsIgnoreCase(PreferencesUtil.CERT_TYPE_VALUE_HARDWARE)) {
	
						certPath = PreferencesUtil.getDefaultHardware().getPath();
	
					} else if (certType.equalsIgnoreCase(PreferencesUtil.CERT_TYPE_VALUE_SOFTWARE)) {
	
						certPath = PreferencesUtil.getSoftwarePreferences().get(
								PreferencesUtil.getPreferences().getString(PreferencesUtil.SOFTWARE_DISPOSITIVE));
					}
				}
				
			} catch (DriversNotFoundException e) {

				String m = LanguageUtil.getLanguage().getString("error.drivers.no_encontrados");
				log.error(m);
				LoggingDesktopController.printError(m);
				throw new SignProgressInterruptedException();
				
			} catch (InterruptedException e) {

				String m = LanguageUtil.getLanguage().getString("error.operacion_cancelada");
				log.error(m);
				LoggingDesktopController.printError(m);
				throw new SignProgressInterruptedException();
			}
			
			System.out.println("certType: " + certType);
			System.out.println("certPath: " + certPath);
			
			// SLOT
			Map<String, Long> slotByReader = null;
			try {
				ProgressMonitorDialog ksProgress = new ProgressMonitorDialog(shell);
				LoadSlotProgress lsp = new LoadSlotProgress(certType, certPath);
				ksProgress.run(true, false, lsp);
				slotByReader = lsp.getSlotsByReader();

			} catch (InvocationTargetException e) {

				if (e.getCause() instanceof DriversNotFoundException) {

					String m = LanguageUtil.getLanguage().getString("error.drivers.no_encontrados");
					log.error(m);
					LoggingDesktopController.printError(m);

				} else if (e.getCause() instanceof KeyStoreException) {

					Throwable cause = ExceptionUtil.getRootCause(e);

					if (cause instanceof PasswordCallbackCanceledException) {
						// Causas: si se pulsa cancelar en el password hander
						String m = LanguageUtil.getLanguage().getString("error.operacion_cancelada");
						LoggingDesktopController.printError(m);
						log.error(m);

					} else {
						// Causas: error generico?
						String m = LanguageUtil.getLanguage().getString("error.cargar_store");
						log.error("KeyStoreException: " + e.getCause().getMessage());
						LoggingDesktopController.printError(m);
					}

				} else if (e.getCause() instanceof PKCS11Exception) {

					String m = "";
					if (e.getCause().getMessage() == KeyStoreBuilderFactory.CKR_PIN_INCORRECT) {
						m = LanguageUtil.getLanguage().getString("error.pin_incorrecto");

					} else if (e.getCause().getMessage() == KeyStoreBuilderFactory.CKR_PIN_LOCKED) {
						m = LanguageUtil.getLanguage().getString("error.tarjeta_bloqueada");
					} else {
						// Causas: -Meter la tarjeta al reves. -Password en blanco y pulsar aceptar?
						m = LanguageUtil.getLanguage().getString("error.cargar_store");
					}
					log.error("PKCS11Exception: " + e.getCause().getMessage());
					LoggingDesktopController.printError(m);

				} else if (e.getCause() instanceof NoSuchAlgorithmException) {

					// Causas: -Tarjeta no introducida. -Modelo no soportado?
					String m = LanguageUtil.getLanguage().getString("error.tarjeta_incorrecta");
					log.error(m);
					LoggingDesktopController.printError(m);

				} else if (e.getCause() instanceof CoreException) {

					// error generico?
					String m = LanguageUtil.getLanguage().getString("error.configuracion_incorrecta");
					log.error(m, e);
					e.printStackTrace();
					LoggingDesktopController.printError(m);

				} else if (e.getCause() instanceof CorePKCS12Exception) {

					String m = LanguageUtil.getLanguage().getString("error.pkcs12_pin_incorrecto");
					log.error(m);
					LoggingDesktopController.printError(m);

				} else { // runtimes - error inesperado
					String m = MessageFormat.format(LanguageUtil.getLanguage().getString("error.sign.unexpected"), e.getCause()
							.toString());
					log.error("", e);
					LoggingDesktopController.printError(m);
				}

				throw new SignProgressInterruptedException();

			} catch (InterruptedException e) {

				String m = LanguageUtil.getLanguage().getString("error.operacion_cancelada");
				LoggingDesktopController.printError(m);
				log.error(m);
				throw new SignProgressInterruptedException();
			}

			// comienzo de la petición de slot
			String slot = "0";

			if (slotByReader != null && slotByReader.size() > 0) {
				if (slotByReader.size() > 1) {
					SlotDialog solicitarSlotDialog = new SlotDialog(shell);
					String selectedSlot = solicitarSlotDialog.open(slotByReader);
					if (selectedSlot == null) {
						// si cierra la ventana
						throw new SignProgressInterruptedException();
					} else {
						slot = selectedSlot + "";
					}
				} else {
					slot = slotByReader.get(slotByReader.keySet().iterator().next()) + "";
				}
			}

			// carga
			try {
				ProgressMonitorDialog ksProgress = new ProgressMonitorDialog(shell);
				LoadKeyStoreProgress lkp = new LoadKeyStoreProgress(shell, certType, certPath, slot);
				ksProgress.run(true, false, lkp);
				ksSignaturePreferences = lkp.getKsSignaturePreferences();

			} catch (InvocationTargetException e) {

				if (e.getCause() instanceof DriversNotFoundException) {

					String m = LanguageUtil.getLanguage().getString("error.drivers.no_encontrados");
					log.error(m);
					LoggingDesktopController.printError(m);

				} else if (e.getCause() instanceof KeyStoreException) {

					Throwable cause = ExceptionUtil.getRootCause(e);

					if (cause instanceof PasswordCallbackCanceledException) {
						// Causas: si se pulsa cancelar en el password hander
						String m = LanguageUtil.getLanguage().getString("error.operacion_cancelada");
						LoggingDesktopController.printError(m);
						log.error(m);

					} else {
						// Causas: error generico?
						String m = LanguageUtil.getLanguage().getString("error.cargar_store");
						log.error("KeyStoreException: " + e.getCause().getMessage());
						LoggingDesktopController.printError(m);
					}

				} else if (e.getCause() instanceof NoSunPkcs11ProviderException){
					
					String m = LanguageUtil.getLanguage().getString("error.sun_provider.not.found");
					log.error("NoSunPkcs11ProviderException: " + e.getCause().getMessage());
					LoggingDesktopController.printError(m);
				
				} else if (e.getCause() instanceof PKCS11Exception) {

					String m = "";
					if (e.getCause().getMessage() == KeyStoreBuilderFactory.CKR_PIN_INCORRECT) {
						m = LanguageUtil.getLanguage().getString("error.pin_incorrecto");

					} else if (e.getCause().getMessage() == KeyStoreBuilderFactory.CKR_PIN_LOCKED) {
						m = LanguageUtil.getLanguage().getString("error.tarjeta_bloqueada");
					} else {
						// Causas: -Meter la tarjeta al reves. -Password en blanco y pulsar aceptar?
						m = LanguageUtil.getLanguage().getString("error.cargar_store");
					}
					log.error("PKCS11Exception: " + e.getCause().getMessage());
					LoggingDesktopController.printError(m);

				} else if (e.getCause() instanceof NoSuchAlgorithmException) {

					// Causas: -Tarjeta no introducida. -Modelo no soportado?
					String m = LanguageUtil.getLanguage().getString("error.tarjeta_incorrecta");
					log.error(m);
					LoggingDesktopController.printError(m);

				} else if (e.getCause() instanceof CoreException) {

					// error generico?
					String m = LanguageUtil.getLanguage().getString("error.configuracion_incorrecta");
					log.error(m, e);
					e.printStackTrace();
					LoggingDesktopController.printError(m);

				} else if (e.getCause() instanceof CorePKCS12Exception) {

					String m = LanguageUtil.getLanguage().getString("error.pkcs12_pin_incorrecto");
					log.error(m);
					LoggingDesktopController.printError(m);

				} else { // runtimes - error inesperado
					String m = MessageFormat.format(LanguageUtil.getLanguage().getString("error.sign.unexpected"), e.getCause()
							.toString());
					log.error("", e);
					LoggingDesktopController.printError(m);
				}

				throw new SignProgressInterruptedException();

			} catch (InterruptedException e) {

				String m = LanguageUtil.getLanguage().getString("error.operacion_cancelada");
				LoggingDesktopController.printError(m);
				log.error(m);
				throw new SignProgressInterruptedException();
			}
			
			try {
				List<String> aliases = SignController.getAlias(ksSignaturePreferences.getKs());
				if (aliases.size() > 1) {
					AliasDialog solicitarAliasDialog = new AliasDialog(shell);
					String selectedAlias = solicitarAliasDialog.open(aliases);
					if (selectedAlias == null) {
						// si cierra la ventana
						throw new SignProgressInterruptedException();
					} else {
						alias = selectedAlias;
					}
				} else {
					alias = aliases.get(0);
				}
				ksSignaturePreferences.setAlias(alias);
				log.info("alias: " + alias);
				
				String usageSeleccionado = CertificateUtil.getKeyUsage((X509Certificate) ksSignaturePreferences.getKs().getCertificate(alias));
				StatisticsUtil.log(StatisticsUtil.KEY_CERTIFICADO_USAGE_SELECTED, usageSeleccionado);
				log.info("Estadisticas | " + StatisticsUtil.KEY_CERTIFICADO_USAGE_SELECTED + ": " + usageSeleccionado);
				

			} catch (AliasesNotFoundException e) {

				log.error("", e);
				String m = LanguageUtil.getLanguage().getString("error.aliases.no_encontrados");
				LoggingDesktopController.printError(m);
				throw new SignProgressInterruptedException();

			} catch (KeyStoreException e) {

				log.error("", e);
				String m = LanguageUtil.getLanguage().getString("error.aliases.no_encontrados");
				LoggingDesktopController.printError(m);
				throw new SignProgressInterruptedException();
			}
			
			//=======================================
			// statistics
			//=======================================
			try {
				X509Certificate cert = (X509Certificate) ksSignaturePreferences.getKs().getCertificate(alias);
				StatisticsUtil.log(StatisticsUtil.KEY_SIGN_ISSUER, cert.getIssuerX500Principal().getName());
				log.info("sign issuer: " + cert.getIssuerX500Principal().getName());
				
			} catch (KeyStoreException e) {
				log.error("", e);
			}

			//=======================================
			// firmamos
			//=======================================
			try {
				ProgressMonitorDialog signProgress = new ProgressMonitorDialog(shell);
				signProgress.run(true, true, new SignProgress(list, ksSignaturePreferences, shell));

			} catch (InvocationTargetException e) {

				if (e.getCause() instanceof ConnectionException) {

					String m = MessageFormat.format(LanguageUtil.getLanguage().getString("error.certificate.connection"), e.getCause()
							.toString());
					log.error(m);
					LoggingDesktopController.printError(m);

				} else if (e.getCause() instanceof RevokedException) {

					String m = LanguageUtil.getLanguage().getString("error.certificate.revoked");
					log.error(m);
					LoggingDesktopController.printError(m);

				} else if (e.getCause() instanceof OCSPIssuerRequiredException) {

					String m = LanguageUtil.getLanguage().getString("error.certificate.ocsp.issuer_required");
					log.error(m);
					LoggingDesktopController.printError(m);

				} else if (e.getCause() instanceof OCSPUnknownUrlException) {

					String m = LanguageUtil.getLanguage().getString("error.certificate.ocsp.unknown_url");
					log.error(m);
					LoggingDesktopController.printError(m);

				} else if (e.getCause() instanceof OCSPCoreException) {

					String m = MessageFormat.format(LanguageUtil.getLanguage().getString("error.certificate.ocsp_error"), e.getCause()
							.getMessage());
					log.error(m);
					LoggingDesktopController.printError(m);

				} else if (e.getCause() instanceof CertificateExpiredException) {

					String m = LanguageUtil.getLanguage().getString("error.certificate.expired");
					log.error(m);
					LoggingDesktopController.printError(m);

				} else if (e.getCause() instanceof CertificateNotYetValidException) {

					String m = LanguageUtil.getLanguage().getString("error.certificate.notyetvalid");
					log.error(m);
					LoggingDesktopController.printError(m);

				} else { // runtimes - error inesperado

					String m = MessageFormat.format(LanguageUtil.getLanguage().getString("error.sign.unexpected"), e.getCause()
							.toString());
					log.error("", e);
					LoggingDesktopController.printError(m);
				}

				throw new SignProgressInterruptedException();

			} catch (InterruptedException e) {

				String m = LanguageUtil.getLanguage().getString("error.operacion_cancelada");
				LoggingDesktopController.printError(m);
				log.error(m);

				throw new SignProgressInterruptedException();
			}

		} catch (SignProgressInterruptedException e) {
			// solo es para interrumpir el proceso de firma
		}

		// cierro la session con la tarjeta para que pida el pin otra vez después de una firma masiva o una firma simple
		if (ksSignaturePreferences != null) {
			try {
				SignController.logout(ksSignaturePreferences.getKs(), alias);
			} catch (NoSunPkcs11ProviderException e) {
				log.warn("NoSunPkcs11ProviderException | No se puede realizar un logout del ks");
			}
		}
		
		
    }

}

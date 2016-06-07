package net.esle.sinadura.gui;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.esle.sinadura.core.exceptions.ConnectionException;
import net.esle.sinadura.ee.EEModulesManager;
import net.esle.sinadura.ee.exceptions.EEModuleGenericException;
import net.esle.sinadura.ee.exceptions.EEModuleNotFoundException;
import net.esle.sinadura.ee.interfaces.ProxyEEModule;
import net.esle.sinadura.gui.model.LoggerMessage;
import net.esle.sinadura.gui.model.LoggerMessage.Level;
import net.esle.sinadura.gui.util.LanguageUtil;
import net.esle.sinadura.gui.util.PreferencesUtil;
import net.esle.sinadura.gui.util.PropertiesUtil;
import net.esle.sinadura.gui.util.StatisticsUtil;
import net.esle.sinadura.gui.util.VersionUtil;

public class LoadingOperations {

	private static final Log log = LogFactory.getLog(LoadingOperations.class);
	
	public static void run(List<LoggerMessage> listMessages) {
		
		// ee (proxy)
		boolean proxyEnabled = Boolean.valueOf(PropertiesUtil.getConfiguration().getProperty(PropertiesUtil.PROXY_ENABLED));
		if (proxyEnabled && PreferencesUtil.getPreferences().getBoolean(PreferencesUtil.PROXY_SYSTEM)) {
			try {
				ProxyEEModule proxyUtil = EEModulesManager.getProxyModule();
				proxyUtil.configureProxy(PreferencesUtil.getPreferences().getString(PreferencesUtil.PROXY_USER), PreferencesUtil
						.getPreferences().getString(PreferencesUtil.PROXY_PASS));

			} catch (EEModuleNotFoundException e) {
				listMessages.add(new LoggerMessage(Level.INFO, MessageFormat.format(
						LanguageUtil.getLanguage().getString("ee.proxy.disabled"), "proxy")));

			} catch (EEModuleGenericException e) {
				log.error(e);
			}
		}
		
		// estadisticas
		StatisticsUtil.log(StatisticsUtil.KEY_SO, System.getProperty("os.name"));
		StatisticsUtil.log(StatisticsUtil.KEY_SO_VERSION, System.getProperty("os.version"));
		StatisticsUtil.log(StatisticsUtil.KEY_SO_ARCHITECTURE, System.getProperty("os.arch"));
		StatisticsUtil.log(StatisticsUtil.KEY_SINADURA_VERSION, PropertiesUtil.getConfiguration().getProperty(
				PropertiesUtil.APPLICATION_VERSION_STRING));
		StatisticsUtil.log(StatisticsUtil.KEY_SO_LOCALE_COUNTRY, Locale.getDefault().getCountry());
		StatisticsUtil.log(StatisticsUtil.KEY_SO_LOCALE_LANGUAGE, Locale.getDefault().getLanguage());
		StatisticsUtil.log(StatisticsUtil.KEY_JAVA_VENDOR, System.getProperty("java.vendor"));
		StatisticsUtil.log(StatisticsUtil.KEY_JAVA_VERSION, System.getProperty("java.version"));

		// check new version
		boolean checkNewVersion = Boolean.valueOf(PropertiesUtil.getConfiguration().getProperty(PropertiesUtil.VERSION_CHECK_UPDATE_ENABLED));
		if (checkNewVersion) {
			try {
				if (VersionUtil.isThereApplicationNewVersion()) {
					
					listMessages.add(new LoggerMessage(Level.INFO, MessageFormat.format(
							LanguageUtil.getLanguage().getString("loading.new.version"),
							PropertiesUtil.getConfiguration().getProperty(PropertiesUtil.SINADURA_MAIN_URL))));
				}
			} catch (ConnectionException e) {
				log.error("", e);
				String m = MessageFormat
						.format(LanguageUtil.getLanguage().getString("error.certificate.connection"), e.getCause().toString());
				listMessages.add(new LoggerMessage(Level.ERROR, m));
			}
		}
		
	}
}

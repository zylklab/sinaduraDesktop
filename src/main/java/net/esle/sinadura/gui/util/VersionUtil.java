package net.esle.sinadura.gui.util;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Properties;

import net.esle.sinadura.core.exceptions.ConnectionException;

public class VersionUtil {

	public static boolean isThereApplicationNewVersion() throws ConnectionException {
		
		try {
			URL url = new URL(PropertiesUtil.getConfiguration().getProperty(PropertiesUtil.SERVER_VERSION_URL));

			URLConnection connection = url.openConnection();
			connection.setConnectTimeout(3000);

			Properties pf = new Properties();
			connection.connect();
			pf.load(connection.getInputStream());

			if (Integer.parseInt(PropertiesUtil.getConfiguration().getProperty(PropertiesUtil.APPLICATION_VERSION_NUMBER)) >= Integer
					.parseInt(pf.getProperty(PropertiesServerUtil.APPLICATION_SERVER_VERSION))) {
				return false;
			} else {
				return true;
			}
		} catch (SocketTimeoutException e) {
			throw new ConnectionException(e);
		} catch (UnknownHostException e) {
			throw new ConnectionException(e);
		} catch (IOException e) {
			throw new ConnectionException(e);
		}
	}
}
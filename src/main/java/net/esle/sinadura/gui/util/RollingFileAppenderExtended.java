package net.esle.sinadura.gui.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.RollingFileAppender;

public class RollingFileAppenderExtended extends RollingFileAppender {

	private static Log log = LogFactory.getLog(RollingFileAppenderExtended.class);

	private void send() {
		try {
			URL dest = new URL(PropertiesServerUtil.getConfiguration().getProperty(PropertiesServerUtil.STATISTICS_SERVER_URL));
			URLConnection urlCon = dest.openConnection();

			// prepare input and output
			urlCon.setDoInput(true);
			urlCon.setDoOutput(true);

			// Disable caching
			urlCon.setUseCaches(false);
			urlCon.setConnectTimeout(3000);
			urlCon.connect();

			// Post output
			DataOutputStream out = new DataOutputStream(urlCon.getOutputStream());
			
			File archivo = new File (getFile());
			FileReader fr = new FileReader (archivo);
			BufferedReader br = new BufferedReader(fr);
			
	        while(br.readLine() != null) {
				out.writeBytes(br.readLine() + "\n");
	        }

			out.flush();
			out.close();
			
			DataInputStream in = new DataInputStream(urlCon.getInputStream());
			
		} catch (Exception e) {
		}

	}

	@Override
	public void rollOver() {
		if (PreferencesUtil.getBoolean(PreferencesUtil.ENABLE_STATISTICS)) {
			send();
		}
		super.rollOver();
	}
}

package net.esle.sinadura.protocol.utils;

import static java.lang.System.currentTimeMillis;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.URLConnection.guessContentTypeFromName;
import static java.text.MessageFormat.format;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.esle.sinadura.protocol.services.ServiceManager;

public class MultipartUtility {
	
	private static Log log = LogFactory.getLog(MultipartUtility.class);

	private static final String CRLF = "\r\n";
	private static final String CHARSET = "UTF-8";

	// lo mismo que en Utils
	private static final int CONNECT_TIMEOUT = 5000;
	private static final int READ_TIMEOUT = 5000;

	private final HttpURLConnection connection;
	private final OutputStream outputStream;
	private final PrintWriter writer;
	private final String boundary;

	
	public MultipartUtility(HttpURLConnection httpURLConnection) throws IOException {

		boundary = "---------------------------" + currentTimeMillis();

		connection = httpURLConnection;
		connection.setConnectTimeout(CONNECT_TIMEOUT);
		connection.setReadTimeout(READ_TIMEOUT);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Accept-Charset", CHARSET);
		connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);

		outputStream = connection.getOutputStream();
		writer = new PrintWriter(new OutputStreamWriter(outputStream, CHARSET), true);
	}

	public void addFormField(final String name, final String value) {
		writer.append("--").append(boundary).append(CRLF).append("Content-Disposition: form-data; name=\"").append(name)
				.append("\"").append(CRLF).append("Content-Type: text/plain; charset=").append(CHARSET).append(CRLF).append(CRLF)
				.append(value).append(CRLF);
	}

	public void addFilePart(final String fieldName, final InputStream inputStream, final String fileName) throws IOException {
		
		writer.append("--").append(boundary).append(CRLF).append("Content-Disposition: form-data; name=\"").append(fieldName)
				.append("\"; filename=\"").append(fileName).append("\"").append(CRLF).append("Content-Type: ")
				.append(guessContentTypeFromName(fileName)).append(CRLF).append("Content-Transfer-Encoding: binary").append(CRLF)
				.append(CRLF);

		writer.flush();
		outputStream.flush();
		
		try {
			
			final byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
			outputStream.flush();
			
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				// nothing
			}
		}

		writer.append(CRLF);
	}

	public void addHeaderField(String name, String value) {
		
		writer.append(name).append(": ").append(value).append(CRLF);
	}

	public byte[] finish() throws IOException {
		
		writer.append(CRLF).append("--").append(boundary).append("--").append(CRLF);
		writer.close();

		final int status = connection.getResponseCode();
		if (status != HTTP_OK) {
			throw new IOException(format("request failed with HTTP status: {1}", status));
		}

		InputStream is = null;
		try {
			is = connection.getInputStream();	
			final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			final byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = is.read(buffer)) != -1) {
				bytes.write(buffer, 0, bytesRead);
			}

			log.info("upload request ok");
			return bytes.toByteArray();
			
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				// nothing
			}
			connection.disconnect();
		}
	}
}


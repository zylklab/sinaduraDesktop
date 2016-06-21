package net.esle.sinadura.protocol.exceptions;

/**
 * Esta excepcion getiona el envio del error al servicio REST. 
 * Por defecto se traducen todas a "ERR_APP_INTERNAL".
 *
 */
public class CloudAppException extends Exception {

	public static final String ERR_APP_INTERNAL = "ERR_APP_INTERNAL"; // default
	public static final String ERR_APP_CLIENTINCOMPATIBLE = "ERR_APP_CLIENTINCOMPATIBLE";
	
	
	public String code = ERR_APP_INTERNAL;

	
	public CloudAppException(String message) {
		super(message);
	}
	
	public CloudAppException(String message, String code) {
		super(message);
		this.code = code;
	}

	public CloudAppException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public CloudAppException(String message, Throwable cause, String code) {
		super(message, cause);
		this.code = code;
	}

	public CloudAppException(Throwable cause) {
		super(cause);
	}
	
	public CloudAppException(Throwable cause, String code) {
		super(cause);
		this.code = code;
	}

	// getter
	public String getCode() {
		return code;
	}


}
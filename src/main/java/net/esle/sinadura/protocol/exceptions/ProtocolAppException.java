package net.esle.sinadura.protocol.exceptions;

/**
 * Esta excepcion getiona el envio del error al servicio REST. Unicamente se envia el codigo de error ("code") al servidor. Por
 * defecto se traducen todas a "ERR_APP_INTERNAL".
 * 
 * La traduccion del codigo en las excepciones del core se hace de forma "manual", porque tal y como esta programado el core (a
 * modo de API applet) hay que hacer un getLastError una vez finalizado el metodo en correspondiente.
 *
 */
public class ProtocolAppException extends Exception {

	public static final String ERR_APP_INTERNAL = "ERR_APP_INTERNAL"; // default
	public static final String ERR_APP_CLIENTINCOMPATIBLE = "ERR_APP_CLIENTINCOMPATIBLE";
	
	
	public String code = ERR_APP_INTERNAL;

	
	public ProtocolAppException(String message) {
		super(message);
	}
	
	public ProtocolAppException(String message, String code) {
		super(message);
		this.code = code;
	}

	public ProtocolAppException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ProtocolAppException(String message, Throwable cause, String code) {
		super(message, cause);
		this.code = code;
	}

	public ProtocolAppException(Throwable cause) {
		super(cause);
	}
	
	public ProtocolAppException(Throwable cause, String code) {
		super(cause);
		this.code = code;
	}

	// getter
	public String getCode() {
		return code;
	}


}
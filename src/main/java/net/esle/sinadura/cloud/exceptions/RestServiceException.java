package net.esle.sinadura.cloud.exceptions;

public class RestServiceException extends Exception {

	public RestServiceException() {
		super();
	}

	public RestServiceException(String message) {
		super(message);
	}

	public RestServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public RestServiceException(Throwable cause) {
		super(cause);
	}

}
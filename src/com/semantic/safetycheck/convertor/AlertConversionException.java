package com.semantic.safetycheck.convertor;

public class AlertConversionException extends Exception {

	private static final long serialVersionUID = -4177094231266241995L;

	public AlertConversionException() {
	}

	public AlertConversionException(String message) {
		super(message);
	}

	public AlertConversionException(Throwable cause) {
		super(cause);
	}

	public AlertConversionException(String message, Throwable cause) {
		super(message, cause);
	}

	public AlertConversionException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}

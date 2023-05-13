package krause.common.exception;

/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 */
public class InitializationException extends ProcessingException {

	public InitializationException() {
		super();
	}

	public InitializationException(String message, Throwable cause) {
		super(message, cause);
	}

	public InitializationException(String message) {
		super(message);
	}

	public InitializationException(Throwable cause) {
		super(cause);
	}
}

package krause.common.exception;
/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 */
public class DuplicateKeyException extends ProcessingException {
	public DuplicateKeyException() {
		super();
	}
	public DuplicateKeyException(String message) {
		super(message);
	}
	public DuplicateKeyException(String message, Throwable cause) {
		super(message, cause);
	}
	public DuplicateKeyException(Throwable cause) {
		super(cause);
	}
}

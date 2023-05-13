package krause.common.exception;
/**
  *  Filderwetter - framework for weather aquisistion and analysis
  * 
  *  Copyright (C) 2003 Dietmar Krause, DL2SBA
  *
  */
public class IllegalResponseException extends ProcessingException {
	/**
	 * Constructor for IllegalResponseException.
	 */
	public IllegalResponseException() {
		super();
	}
	/**
	 * Constructor for IllegalResponseException.
	 * @param message
	 */
	public IllegalResponseException(String message) {
		super(message);
	}
	/**
	 * Constructor for IllegalResponseException.
	 * @param message
	 * @param cause
	 */
	public IllegalResponseException(String message, Throwable cause) {
		super(message, cause);
	}
	/**
	 * Constructor for IllegalResponseException.
	 * @param cause
	 */
	public IllegalResponseException(Throwable cause) {
		super(cause);
	}
}

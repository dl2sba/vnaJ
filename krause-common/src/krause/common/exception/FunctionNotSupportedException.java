package krause.common.exception;
/**
  *  Filderwetter - framework for weather aquisistion and analysis
  * 
  *  Copyright (C) 2003 Dietmar Krause, DL2SBA
  */
public class FunctionNotSupportedException extends ProcessingException {
	/**
	 * Constructor for FunctionNotSupportedException.
	 */
	public FunctionNotSupportedException() {
		super();
	}
	/**
	 * Constructor for FunctionNotSupportedException.
	 * @param message
	 */
	public FunctionNotSupportedException(String message) {
		super(message);
	}
	/**
	 * Constructor for FunctionNotSupportedException.
	 * @param message
	 * @param cause
	 */
	public FunctionNotSupportedException(String message, Throwable cause) {
		super(message, cause);
	}
	/**
	 * Constructor for FunctionNotSupportedException.
	 * @param cause
	 */
	public FunctionNotSupportedException(Throwable cause) {
		super(cause);
	}
}

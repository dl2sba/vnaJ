package krause.common.exception;
/**
  *  Filderwetter - framework for weather aquisistion and analysis
  * 
  *  Copyright (C) 2003 Dietmar Krause, DL2SBA
  */
public class PropertyNotFoundException extends Exception {
	/**
	 * PropertyNotFoundException constructor.
	 */
	public PropertyNotFoundException() {
		super();
	}
	/**
	 * Constructor for PropertyNotFoundException.
	 * @param cause
	 */
	public PropertyNotFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor for PropertyNotFoundException.
	 * @param message
	 * @param cause
	 */
	public PropertyNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * PropertyNotFoundException constructor with error details argument.
	 *
	 * @param message the details of the occured error
	 */
	public PropertyNotFoundException(String message) {
		super(message);
	}
}
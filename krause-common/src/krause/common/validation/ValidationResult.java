/**
 * ********************************************************************************** 
 * Copyright (C) 2009 Dietmar Krause, DL2SBA
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package krause.common.validation;

public class ValidationResult {
	public enum ValidationType {
		INFO, WARNING, ERROR
	};

	private Object errorObject;
	private Exception exception;
	private ValidationType type = ValidationType.ERROR;
	private String message;

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * 
	 * @param pEx
	 * @param pMessage
	 */
	public ValidationResult(String pMessage) {
		setMessage(pMessage);
	}

	/**
	 * 
	 * @param pEx
	 * @param pMessage
	 */
	public ValidationResult(Exception pEx, String pMessage) {
		setMessage(pMessage);
		setException(pEx);
	}

	/**
	 * @param exception
	 *            the exception to set
	 */
	public void setException(Exception exception) {
		this.exception = exception;
	}

	/**
	 * @return the exception
	 */
	public Exception getException() {
		return exception;
	}

	/**
	 * 
	 * @param errorObject
	 */
	public void setErrorObject(Object errorObject) {
		this.errorObject = errorObject;
	}

	/**
	 * 
	 * @return
	 */
	public Object getErrorObject() {
		return errorObject;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(ValidationType type) {
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public ValidationType getType() {
		return type;
	}

	@Override
	public String toString() {
		StringBuffer rc = new StringBuffer();

		if (getErrorObject() != null) {
			rc.append(errorObject + ": ");
		}
		rc.append(message);
		if (exception != null) {
			rc.append(" [" + exception.getLocalizedMessage() + "]");
		}
		return rc.toString();
	}
}

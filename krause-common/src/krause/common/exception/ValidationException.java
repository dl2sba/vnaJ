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
package krause.common.exception;

import krause.common.validation.ValidationResults;

public class ValidationException extends ProcessingException {
	private final ValidationResults results;

	public ValidationException() {
		super();
		this.results = null;
	}

	public ValidationException(String message) {
		super(message);
		this.results = null;
	}

	public ValidationException(String message, Throwable cause) {
		super(message, cause);
		this.results = null;
	}

	public ValidationException(Throwable cause) {
		super(cause);
		this.results = null;
	}

	public ValidationException(Throwable cause, ValidationResults pResults) {
		super(cause);
		this.results = pResults;
	}

	public ValidationException(ValidationResults pResults) {
		this.results = pResults;
	}

	public ValidationResults getResults() {
		return this.results;
	}
}

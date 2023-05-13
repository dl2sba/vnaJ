package krause.common.exception;


public class IllegalStateTransitionException extends ProcessingException {

	public IllegalStateTransitionException() {
		super();
	}

	public IllegalStateTransitionException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalStateTransitionException(String message) {
		super(message);
	}

	public IllegalStateTransitionException(Throwable cause) {
		super(cause);
	}
}

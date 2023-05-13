package krause.common.exception;

public class InvalidParameterException extends ProcessingException {

	public InvalidParameterException(String string, Exception e) {
		super(string, e);
	}

}

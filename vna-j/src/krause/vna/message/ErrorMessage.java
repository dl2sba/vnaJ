package krause.vna.message;

public class ErrorMessage extends GenericMessage {

	public ErrorMessage(String error) {
		super("Error", MESSAGE_TYPE.ERROR);
		setMessage(getMessage() + error);
	}
}

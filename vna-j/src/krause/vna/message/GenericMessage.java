package krause.vna.message;

import krause.vna.resources.VNAMessages;

public abstract class GenericMessage {
	private String message;
	private MESSAGE_TYPE type;

	public enum MESSAGE_TYPE {
		INFO, WARN, ERROR, FATAL
	}

	public GenericMessage() {
	}

	public GenericMessage(String id, MESSAGE_TYPE info) {
		setMessage(VNAMessages.getString(id));
		setType(info);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the type
	 */
	public MESSAGE_TYPE getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(MESSAGE_TYPE type) {
		this.type = type;
	}
}

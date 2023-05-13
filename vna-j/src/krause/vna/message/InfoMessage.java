package krause.vna.message;

import java.text.MessageFormat;

import krause.vna.resources.VNAMessages;

public class InfoMessage extends GenericMessage {

	public InfoMessage(String id) {
		super(id, MESSAGE_TYPE.INFO);
	}

	public InfoMessage(String id, long i) {
		setMessage(MessageFormat.format(VNAMessages.getString(id), i));
		setType(MESSAGE_TYPE.INFO);
	}

	public InfoMessage(String id, String s) {
		setMessage(MessageFormat.format(VNAMessages.getString(id), s));
		setType(MESSAGE_TYPE.INFO);
	}

	public InfoMessage(String id, long i, long j) {
		setMessage(MessageFormat.format(VNAMessages.getString(id), i, j));
		setType(MESSAGE_TYPE.INFO);
	}

	public InfoMessage(String id, long a, long b, long c) {
		setMessage(MessageFormat.format(VNAMessages.getString(id), a, b, c));
		setType(MESSAGE_TYPE.INFO);
	}

	public InfoMessage(String id, long start, long stop, long i, long j) {
		setMessage(MessageFormat.format(VNAMessages.getString(id), start, stop, i, j));
		setType(MESSAGE_TYPE.INFO);
	}

	public InfoMessage(String id, long start, long stop, long i, long j, long k) {
		setMessage(MessageFormat.format(VNAMessages.getString(id), start, stop, i, j, k));
		setType(MESSAGE_TYPE.INFO);
	}
}

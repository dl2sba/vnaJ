package krause.vna.gui;

import java.awt.Window;
import java.text.MessageFormat;

import javax.swing.JOptionPane;

import krause.vna.resources.VNAMessages;

public interface OptionDialogHelper {

	/**
	 * Do a JOptionPane.showMessageDialog with the parsed message.
	 * 
	 * {0} is ex.getLocalizedMessage() {1} is ex.getClass().getName()
	 * 
	 * @param mainFrame
	 *            the frame for which this dialog should display
	 * @param titleId
	 *            the id of the message to display as message title
	 * @param messageId
	 *            the id of the message to display as text
	 * @param ex
	 *            the exception which caused the error
	 */
	public static void showExceptionDialog(Window mainFrame, String titleId, String messageId, Exception ex) {
		String msg = MessageFormat.format(VNAMessages.getString(messageId), ex.getLocalizedMessage(), ex.getClass().getName());
		String title = VNAMessages.getString(titleId);
		JOptionPane.showMessageDialog(mainFrame, msg, title, JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * 
	 * @param mainFrame
	 * @param titleId
	 * @param messageId
	 */
	public static void showInfoDialog(Window mainFrame, String titleId, String messageId) {
		String msg = VNAMessages.getString(messageId);
		String title = VNAMessages.getString(titleId);
		JOptionPane.showMessageDialog(mainFrame, msg, title, JOptionPane.INFORMATION_MESSAGE);
	}

}

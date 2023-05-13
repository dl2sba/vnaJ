/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 */

package krause.vna.firmware;

/**
 * @author Dietmar
 * 
 */
public interface StringMessenger {
	/**
	 * this method is used to be called from classes used inside the
	 * doInBackground loop
	 * 
	 * @param message
	 *            message to publish to the consumers
	 */
	public void publishMessage(String message);

}

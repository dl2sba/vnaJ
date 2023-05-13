/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 *
 *	This file: IVNABackgorundTaskStatusListener.java
 *  Part of:   vna-j
 */

package krause.vna.background;

/**
 * @author Dietmar
 *
 */
public interface IVNABackgroundTaskStatusListener {
	
	public void publishProgress(int percentage);

}

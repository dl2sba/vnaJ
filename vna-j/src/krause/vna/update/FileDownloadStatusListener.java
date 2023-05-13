/**
 * Copyright (C) 2011 Dietmar Krause, DL2SBA
 *
 *	This file: FileDownloadStatusListener.java
 *  Part of:   vna-j
 */

package krause.vna.update;

/**
 * @author Dietmar
 * 
 */
public interface FileDownloadStatusListener {
	void publishState(FileDownloadJob job);
	void done();
}

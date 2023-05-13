/**
 * Copyright (C) 2011 Dietmar Krause, DL2SBA
 *
 *	This file: FileDownloadJob.java
 *  Part of:   vna-j
 */

package krause.vna.update;

/**
 * @author Dietmar
 * 
 */
public class FileDownloadJob {
	public static final int STATUS_NEW = -1;
	public static final int STATUS_STARTED = 0;
	public static final int STATUS_DOWNLOADED = 1;
	public static final int STATUS_ERROR = 2;
	public static final int STATUS_ABORTED = 3;

	private String localDirectory;
	private DownloadFile file;
	private int status = STATUS_NEW;

	public void setLocalDirectory(String localDirectory) {
		this.localDirectory = localDirectory;
	}

	public String getLocalDirectory() {
		return localDirectory;
	}

	public void setFile(DownloadFile file) {
		this.file = file;
	}

	public DownloadFile getFile() {
		return file;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

}

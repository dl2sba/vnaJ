package krause.util.file;

import java.io.File;

public class TimestampControlledFile {
	private String filename;
	private long lastTimestamp;

	private long getFileTS() {
		final File file = new File(this.filename);
		return file.lastModified();
	}

	/**
	 * Create a new instance
	 * 
	 * @param fn
	 *            the filename to watch
	 */
	public TimestampControlledFile(final String fn) {
		this.filename = fn;
		this.lastTimestamp = getFileTS();
	}

	/**
	 * check whether the files was changed on the filesystem in the meantime
	 * 
	 * @return true==files was changed
	 */
	public boolean needsReload() {
		return (this.lastTimestamp != getFileTS());
	}

	public String getFilename() {
		return filename;
	}

	public long getLastTimestamp() {
		return lastTimestamp;
	}
}

package krause.vna.update;

import krause.vna.config.VNASystemConfig.OS_PLATFORM;
import krause.vna.update.UpdateChecker.FILE_TYPE;

public class DownloadFile {
	private String remoteFileName;
	private String localFileName;
	private long fileSize = -1;
	private FILE_TYPE type;
	private OS_PLATFORM plattform;
	private String hash;

	/**
	 * @return the url
	 */
	public String getRemoteFileName() {
		return remoteFileName;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setRemoteFileName(String url) {
		this.remoteFileName = url;
	}

	/**
	 * @return the localFileName
	 */
	public String getLocalFileName() {
		return localFileName;
	}

	/**
	 * @param localFileName
	 *            the localFileName to set
	 */
	public void setLocalFileName(String localFileName) {
		this.localFileName = localFileName;
	}

	@Override
	public String toString() {
		return "DownloadFile [fileSize=" + fileSize + ", localFileName=" + localFileName + ", plattform=" + plattform + ", remoteFileName=" + remoteFileName + ", type=" + type + "]";
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public void setPlattform(OS_PLATFORM plattform) {
		this.plattform = plattform;
	}

	public OS_PLATFORM getPlattform() {
		return plattform;
	}

	public void setType(FILE_TYPE type) {
		this.type = type;
	}

	public FILE_TYPE getType() {
		return type;
	}

	/**
	 * @param hash the hash to set
	 */
	public void setHash(String hash) {
		this.hash = hash;
	}

	/**
	 * @return the hash
	 */
	public String getHash() {
		return hash;
	}

}

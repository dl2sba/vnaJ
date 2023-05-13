package krause.vna.update;

import java.util.ArrayList;
import java.util.List;

import krause.vna.update.UpdateChecker.FILE_TYPE;

public class UpdateInfoBlock {
	private String version;
	private String comment;
	private List<DownloadFile> files;

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @return the files
	 */
	public List<DownloadFile> getFiles() {
		return files;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param comment
	 *            the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @param files
	 *            the files to set
	 */
	public void setFiles(List<DownloadFile> files) {
		this.files = files;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UpdateInfoBlock [comment=" + comment + ", files=" + files
				+ ", version=" + version + "]";
	}

	/**
	 * 
	 * @param selectedType
	 * @return
	 */
	public List<DownloadFile> getFilesForType(FILE_TYPE selectedType) {
		List<DownloadFile> rc = new ArrayList<DownloadFile>();

		for (DownloadFile file : files) {
			if (file.getType() == selectedType) {
				rc.add(file);
			}
		}
		return rc;
	}
}

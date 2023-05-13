/**
 * Copyright (C) 2011 Dietmar Krause, DL2SBA
 *
 *	This file: FileDownloadTask.java
 *  Part of:   vna-j
 */

package krause.vna.update;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import krause.common.exception.ProcessingException;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;

/**
 * @author Dietmar
 * 
 */
public class FileDownloadTask extends SwingWorker<List<FileDownloadJob>, FileDownloadJob> {
	private List<FileDownloadJob> jobs = new ArrayList<>();
	private FileDownloadStatusListener listener = null;
	private boolean abort = false;

	public FileDownloadTask(FileDownloadStatusListener pList) {
		super();
		this.listener = pList;
	}

	public void abort() {
		abort = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected List<FileDownloadJob> doInBackground() throws Exception {
		TraceHelper.entry(this, "doInBackground");
		for (FileDownloadJob job : jobs) {
			job.setStatus(FileDownloadJob.STATUS_STARTED);
			publish(job);
			int rc = downloadFile(job.getFile(), job.getLocalDirectory());
			job.setStatus(rc);
			publish(job);
		}
		TraceHelper.exit(this, "doInBackground");
		return jobs;
	}

	@Override
	/**
	 * This method is called in context of AWTThread Here we can handle all AWT call etc.
	 */
	protected void process(List<FileDownloadJob> jobs) {
		TraceHelper.entry(this, "process");
		if (listener != null) {
			for (FileDownloadJob job : jobs) {
				listener.publishState(job);
			}
		}
		TraceHelper.exit(this, "process");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.background.IVNABackgorundTaskStatusListener#publishProgress (int)
	 */
	public void publishProgress(int percentage) {
		// not used
	}

	public void addJob(FileDownloadJob job) {
		jobs.add(job);
	}

	public void addJobs(List<FileDownloadJob> jobs) {
		for (FileDownloadJob job : jobs) {
			addJob(job);
		}
	}

	/**
	 * 
	 * @param file
	 * @param targetDirectory
	 * @throws ProcessingException
	 */
	private int downloadFile(DownloadFile file, String targetDirectory) {
		final String methodName = "downloadFile";
		TraceHelper.entry(this, methodName, file.getRemoteFileName());
		int rc = FileDownloadJob.STATUS_STARTED;
		URL url = null;
		URLConnection urlConn = null;
		InputStream inpStream = null;
		BufferedInputStream bufInpStream = null;
		FileOutputStream fileOutStream = null;
		BufferedOutputStream buffOutStream = null;
		String outputFileName = targetDirectory + System.getProperty("file.separator") + file.getLocalFileName();

		if (abort) {
			rc = FileDownloadJob.STATUS_ABORTED;
		} else {
			try {
				final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
				messageDigest.reset();

				// first create the target directory
				new File(targetDirectory).mkdirs();

				// now download the file
				url = new URL(file.getRemoteFileName());
				urlConn = url.openConnection();
				inpStream = urlConn.getInputStream();
				bufInpStream = new BufferedInputStream(inpStream);
				fileOutStream = new FileOutputStream(outputFileName);
				buffOutStream = new BufferedOutputStream(fileOutStream);
				int i;
				long filesize = 0;
				while (((i = bufInpStream.read()) != -1) && !abort) {
					buffOutStream.write(i);
					++filesize;
					messageDigest.update((byte) (i & 0xff));
				}
				file.setFileSize(filesize);
				buffOutStream.flush();
				if (abort) {
					rc = FileDownloadJob.STATUS_ABORTED;
				} else {
					rc = FileDownloadJob.STATUS_DOWNLOADED;
				}
				final byte[] resultByte = messageDigest.digest();
				StringBuilder sb = new StringBuilder();
				for (i = 0; i < resultByte.length; ++i) {
					sb.append(Integer.toHexString((resultByte[i] & 0xFF) | 0x100).substring(1, 3));
				}
				TraceHelper.text(this, methodName, "md5 for file=[" + sb.toString() + "] xml=[" + file.getHash() + "]");
			} catch (NoSuchAlgorithmException | IOException e) {
				ErrorLogHelper.exception(this, methodName, e);
				rc = FileDownloadJob.STATUS_ERROR;
			} finally {
				if (buffOutStream != null) {
					try {
						buffOutStream.close();
						if (abort) {
							File fi = new File(outputFileName);
							fi.delete();
						}
					} catch (IOException e) {
						ErrorLogHelper.exception(this, methodName, e);
					}
				}
				if (fileOutStream != null) {
					try {
						fileOutStream.close();
					} catch (IOException e) {
						ErrorLogHelper.exception(this, methodName, e);
					}
				}
				if (bufInpStream != null) {
					try {
						bufInpStream.close();
					} catch (IOException e) {
						ErrorLogHelper.exception(this, methodName, e);
					}
				}
				if (inpStream != null) {
					try {
						inpStream.close();
					} catch (IOException e) {
						ErrorLogHelper.exception(this, methodName, e);
					}
				}
			}
		}
		TraceHelper.exitWithRC(this, methodName, "rc=%d - [%s]", rc, outputFileName);
		return rc;
	}

	@Override
	protected void done() {
		final String methodName = "done";

		TraceHelper.entry(this, methodName);
		listener.done();
		TraceHelper.exit(this, methodName);

	}
}

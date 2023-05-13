/**
 * Copyright (C) 2007 Dietmar Krause, DL2SBA
 */
package krause.vna.gui.update;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import krause.util.ras.logging.TraceHelper;
import krause.vna.resources.VNAMessages;
import krause.vna.update.FileDownloadJob;

/**
 * @author Dietmar Krause
 * 
 */
public class VNAUpdateTableModel extends AbstractTableModel {

	private List<FileDownloadJob> jobs = new ArrayList<FileDownloadJob>();

	private String[] columnNames = new String[] {
			VNAMessages.getString("VNAUpdateTableModel.filename"),
			VNAMessages.getString("VNAUpdateTableModel.filesize"),
			VNAMessages.getString("VNAUpdateTableModel.status"),
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return jobs.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return columnNames.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object rc = "";
		FileDownloadJob job = jobs.get(rowIndex);
		if (columnIndex == 0) {
			rc = job.getFile().getLocalFileName();
		} else if (columnIndex == 1) {
			rc = Long.valueOf(job.getFile().getFileSize());
		} else if (columnIndex == 2) {
			int s = job.getStatus();
			if (s == FileDownloadJob.STATUS_NEW) {
				rc = "New";
			} else if (s == FileDownloadJob.STATUS_STARTED) {
				rc = "Downloading ...";
			} else if (s == FileDownloadJob.STATUS_DOWNLOADED) {
				rc = "OK";
			} else if (s == FileDownloadJob.STATUS_ABORTED) {
				rc = "Abort";
			} else if (s == FileDownloadJob.STATUS_ERROR) {
				rc = "Error";
			} else {
				rc = "???";
			}
		} else {
			rc = "???";
		}
		return rc;
	}

	/**
	 * @return Returns the data.
	 */
	public List<FileDownloadJob> getJobs() {
		return jobs;
	}

	/**
	 * @param pair
	 */
	public void addElement(FileDownloadJob block) {
		jobs.add(block);
		fireTableDataChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	/**
	 * removes all data from the model
	 */
	public void clear() {
		jobs.clear();
		fireTableDataChanged();
	}

	/**
	 * @param job
	 */
	public void updateElement(FileDownloadJob job) {
		TraceHelper.entry(this, "updateElement");
		fireTableDataChanged();
		TraceHelper.exit(this, "updateElement");
	}
}

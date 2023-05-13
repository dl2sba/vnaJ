/**
 * Copyright (C) 2007 Dietmar Krause, DL2SBA
 */
package krause.vna.firmware;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import krause.util.ras.logging.TraceHelper;
import krause.vna.update.FileDownloadJob;

/**
 * @author Dietmar Krause
 * 
 */
public class SimpleStringListboxModel extends AbstractTableModel {

	private List<String> messages = new ArrayList<String>();
	private String columnTitle;

	public SimpleStringListboxModel(String title) {
		columnTitle = title;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return messages.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object rc = "";
		String msg = messages.get(rowIndex);
		if (columnIndex == 0) {
			rc = msg;
		} else {
			rc = "???";
		}
		return rc;
	}

	/**
	 * @return Returns the data.
	 */
	public List<String> getMessages() {
		return messages;
	}

	/**
	 * @param pair
	 */
	public void addElement(String message) {
		messages.add(message);
		fireTableDataChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {
		return columnTitle;
	}

	/**
	 * removes all data from the model
	 */
	public void clear() {
		messages.clear();
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

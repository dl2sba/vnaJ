/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 *
 *	This file: HistorizedLabelTableModel.java
 *  Part of:   vna-j
 */

package krause.vna.gui;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * @author Dietmar
 * 
 */
public class HistorizedLabelTableModel extends AbstractTableModel {
	private transient List<HistorizedLabelEntry> data = null;

	public HistorizedLabelTableModel(List<HistorizedLabelEntry> data) {
		super();
		this.data = data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return 2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		if (data != null) {
			return data.size();
		} else {
			return 0;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object rc = null;
		HistorizedLabelEntry pair = data.get(rowIndex);
		if (columnIndex == 0) {
			rc = DateFormat.getDateTimeInstance().format(new Date(pair.getTimestamp()));
		} else if (columnIndex == 1) {
			rc = pair.getText();
		} else {
			rc = "???";
		}
		return rc;
	}

	public List<HistorizedLabelEntry> getData() {
		return data;
	}
}

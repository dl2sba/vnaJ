/**
 * Copyright (C) 2007 Dietmar Krause, DL2SBA
 */
package krause.vna.gui.panels.data.table;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import krause.vna.gui.util.VNAFrequencyPair;

public class VNAFrequencyPairTableModel extends AbstractTableModel {
	private List<VNAFrequencyPair> data = new ArrayList<VNAFrequencyPair>();

	/**
	 * @param newPair
	 */
	public void addElement(VNAFrequencyPair newPair) {
		boolean isNew = true;
		for (VNAFrequencyPair pair : data) {
			if (pair.equals(newPair)) {
				isNew = false;
				break;
			}
		}
		if (isNew) {
			data.add(newPair);
			fireTableDataChanged();
		} else {
			Toolkit.getDefaultToolkit().beep();
		}
	}

	/**
	 * removes all data from the model
	 */
	public void clear() {
		data.clear();
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
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {
		if (column == 0) {
			return "Start";
		} else if (column == 1) {
			return "Stop";
		} else {
			return "???";
		}
	}

	/**
	 * @return Returns the data.
	 */
	public List<VNAFrequencyPair> getData() {
		return data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return data.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object rc = null;
		VNAFrequencyPair pair = data.get(rowIndex);
		if (columnIndex == 0) {
			rc = pair.getStartFrequency();
		} else if (columnIndex == 1) {
			rc = pair.getStopFrequency();
		} else {
			rc = "???";
		}
		return rc;
	}

}

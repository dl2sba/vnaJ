/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 */
package krause.vna.gui.generator.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import krause.util.ras.logging.TraceHelper;
import krause.vna.resources.VNAMessages;

public class VNAFrequencyTableModel extends AbstractTableModel {

	private List<Long> data = new ArrayList<Long>();
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
		Object rc = null;
		Long pair = data.get(rowIndex);
		if (columnIndex == 0) {
			rc = pair;
		} else {
			rc = "???";
		}
		return rc;
	}

	/**
	 * @return Returns the data.
	 */
	public List<Long> getData() {
		return data;
	}

	/**
	 * @param pair
	 */
	public void addElement(Long pair) {
		TraceHelper.entry(this, "addElement", pair.toString());
		data.add(pair);
		Collections.sort(data);
		fireTableDataChanged();
		TraceHelper.exit(this, "addElement");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {
		if (column == 0) {
			return VNAMessages.getString("FrequencyTable.Header");
		} else {
			return "???";
		}
	}

	/**
	 * removes all data from the model
	 */
	public void clear() {
		data.clear();
	}

}

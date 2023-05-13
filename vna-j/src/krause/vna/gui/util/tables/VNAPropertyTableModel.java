/**
 * Copyright (C) 2020 Dietmar Krause, DL2SBA
 * 
 */
package krause.vna.gui.util.tables;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * @author Dietmar Krause
 * 
 */
public class VNAPropertyTableModel extends AbstractTableModel {

	private List<VNAProperty> data = new ArrayList<VNAProperty>();

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
		return 2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object rc = null;
		VNAProperty pair = data.get(rowIndex);
		if (columnIndex == 0) {
			rc = pair.getKey();
		} else if (columnIndex == 1) {
			rc = pair.getValue();
		} else {
			rc = "???";
		}
		return rc;
	}

	/**
	 * @return Returns the data.
	 */
	public List<VNAProperty> getData() {
		return data;
	}

	/**
	 * @param pair
	 */
	public void addElement(VNAProperty pair) {
		data.add(pair);
		fireTableDataChanged();
	}

	@Override
	public String getColumnName(int column) {
		if (column == 0) {
			return "Key";
		} else if (column == 1) {
			return "Value";
		} else {
			return "???";
		}
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return (col == 1);
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		String val = (String) value;
		data.get(row).setValue(val);
		fireTableCellUpdated(row, col);
	}

}

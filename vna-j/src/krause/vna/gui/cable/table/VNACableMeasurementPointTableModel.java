/**
 * Copyright (C) 2007 Dietmar Krause, DL2SBA
 */
package krause.vna.gui.cable.table;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import krause.vna.gui.cable.VNACableMeasurementPoint;
import krause.vna.gui.format.VNAFormatFactory;

public class VNACableMeasurementPointTableModel extends AbstractTableModel {

	private transient List<VNACableMeasurementPoint> data = new ArrayList<>();

	/**
	 * @param newPair
	 */
	public void addElement(VNACableMeasurementPoint newPair) {
		boolean isNew = true;
		for (VNACableMeasurementPoint pair : data) {
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
		return 3;
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
		} else if (column == 2) {
			return "Length";
		} else {
			return "???";
		}
	}

	/**
	 * @return Returns the data.
	 */
	public List<VNACableMeasurementPoint> getData() {
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
		VNACableMeasurementPoint pair = data.get(rowIndex);
		if (columnIndex == 0) {
			rc = VNAFormatFactory.getFrequencyFormat().format(pair.getStart().getFrequency()) + " - " + VNAFormatFactory.getPhaseFormat().format(pair.getStart().getReflectionPhase());
		} else if (columnIndex == 1) {
			rc = VNAFormatFactory.getFrequencyFormat().format(pair.getStop().getFrequency()) + " - " + VNAFormatFactory.getPhaseFormat().format(pair.getStop().getReflectionPhase());
		} else if (columnIndex == 2) {
			rc = VNAFormatFactory.getLengthFormat().format(pair.getLength());
		} else {
			rc = "???";
		}
		return rc;
	}

}

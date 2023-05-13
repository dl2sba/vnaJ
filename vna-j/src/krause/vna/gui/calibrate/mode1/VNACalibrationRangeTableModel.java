/**
 * Copyright (C) 2013 Dietmar Krause, DL2SBA
 */
package krause.vna.gui.calibrate.mode1;

import javax.swing.table.AbstractTableModel;

import krause.vna.resources.VNAMessages;

/**
 * @author Dietmar Krause
 * 
 */
public class VNACalibrationRangeTableModel extends AbstractTableModel {

	private VNACalibrationRange[] data = null;

	private String[] columnNames = new String[] {
			VNAMessages.getString("VNASCollectorDialog.lblStartFrequency.text"),
			VNAMessages.getString("VNASCollectorDialog.lblStopFrequency.text"),
			VNAMessages.getString("CalibrationFileTableModel.nofSteps"),
			VNAMessages.getString("CalibrationFileTableModel.novOverscans"),
	};

	/**
	 * 
	 * @param calRanges
	 */
	public VNACalibrationRangeTableModel(VNACalibrationRange[] calRanges) {
		data = calRanges;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return data.length;
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
		Object rc = null;
		VNACalibrationRange block = data[rowIndex];
		if (columnIndex == 0) {
			rc = block.getStart();
		} else if (columnIndex == 1) {
			rc = block.getStop();
		} else if (columnIndex == 2) {
			rc = block.getNumScanPoints();
		} else if (columnIndex == 3) {
			rc = block.getNumOverScans();
		} else {
			rc = "???";
		}
		return rc;
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
	}

}

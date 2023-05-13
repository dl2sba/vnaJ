/**
 * Copyright (C) 2007 Dietmar Krause, DL2SBA
 */
package krause.vna.gui.calibrate.file;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import krause.util.ras.logging.TraceHelper;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.device.VNADriverFactorySymbols;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.resources.VNAMessages;

/**
 * @author Dietmar Krause
 * 
 */
public class VNACalibrationFileTableModel extends AbstractTableModel {

	private List<VNACalibrationBlock> data = new ArrayList<VNACalibrationBlock>();

	private String[] columnNames = new String[] {
			VNAMessages.getString("CalibrationFileTableModel.name"),
			VNAMessages.getString("CalibrationFileTableModel.date"),
			VNAMessages.getString("CalibrationFileTableModel.comment"),
			VNAMessages.getString("CalibrationFileTableModel.type"),
			VNAMessages.getString("CalibrationFileTableModel.mode"),
			VNAMessages.getString("CalibrationFileTableModel.nofSteps"),
			VNAMessages.getString("CalibrationFileTableModel.nofOverscans"),
	};

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
		return columnNames.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object rc = null;
		VNACalibrationBlock block = data.get(rowIndex);
		if (columnIndex == 0) {
			rc = block.getFile().getName();
		} else if (columnIndex == 1) {
			long zeit = block.getFile().lastModified();
			rc = VNAFormatFactory.getDateTimeFormat().format(zeit);
		} else if (columnIndex == 2) {
			rc = block.getComment();
		} else if (columnIndex == 3) {
			String x = block.getAnalyserType();
			if (VNADriverFactorySymbols.TYPE_SAMPLE.equals(x)) {
				rc = "Sample";
			} else if (VNADriverFactorySymbols.TYPE_MINIVNA.equals(x)) {
				rc = "miniVNA";
			} else if (VNADriverFactorySymbols.TYPE_MININVNAPRO.equals(x)) {
				rc = "miniVNA pro";
			} else if (VNADriverFactorySymbols.TYPE_MININVNAPRO2.equals(x)) {
				rc = "miniVNA pro2";
			} else if (VNADriverFactorySymbols.TYPE_MININVNAPRO_EXT.equals(x)) {
				rc = "miniVNA pro-ext";
			} else if (VNADriverFactorySymbols.TYPE_MAX6.equals(x)) {
				rc = "Max6";
			} else if (VNADriverFactorySymbols.TYPE_MAX6_500.equals(x)) {
				rc = "Max6-500";
			} else if (VNADriverFactorySymbols.TYPE_MINIVNA_LF.equals(x)) {
				rc = "miniVNA LF";
			} else if (VNADriverFactorySymbols.TYPE_MININVNAPRO_LF.equals(x)) {
				rc = "miniVNA pro-LF";
			} else if (VNADriverFactorySymbols.TYPE_MINIVNA_TEST.equals(x)) {
				rc = "miniVNA Test";
			} else if (VNADriverFactorySymbols.TYPE_TINYVNA.equals(x)) {
				rc = "tinyVNA";
			} else if (VNADriverFactorySymbols.TYPE_TINYVNA2.equals(x)) {
				rc = "tinyVNA2";
			} else if (VNADriverFactorySymbols.TYPE_METROVNA.equals(x)) {
				rc = "metroVNA";
			} else if (VNADriverFactorySymbols.TYPE_VNARDUINO.equals(x)) {
				rc = "VNArduino";
			} else if (VNADriverFactorySymbols.TYPE_MINIVNA2.equals(x)) {
				rc = "miniVNA V2";
			} else {
				rc = "?";
			}
		} else if (columnIndex == 4) {
			rc = block.getScanMode().shortText();
		} else if (columnIndex == 5) {
			rc = Integer.valueOf(block.getNumberOfSteps());
		} else if (columnIndex == 6) {
			rc = Integer.valueOf(block.getNumberOfOverscans());
		} else {
			rc = "???";
		}
		return rc;
	}

	/**
	 * @return Returns the data.
	 */
	public List<VNACalibrationBlock> getData() {
		return data;
	}

	/**
	 * @param pair
	 */
	public void addElement(VNACalibrationBlock block) {
		TraceHelper.entry(this, "addElement");
		data.add(block);
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
		return columnNames[column];
	}

	/**
	 * removes all data from the model
	 */
	public void clear() {
		data.clear();
	}

}

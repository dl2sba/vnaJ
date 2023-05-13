/**
 * Copyright (C) 2013 Dietmar Krause, DL2SBA
 */
package krause.vna.gui.calibrate.mode1;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import krause.util.ras.logging.TraceHelper;
import krause.vna.gui.util.tables.VNAFrequencyRenderer;

public class VNACalibrationRangeTable extends JTable {

	public VNACalibrationRangeTableModel getModel() {
		return (VNACalibrationRangeTableModel) super.getModel();
	}

	/**
	 * @param listModel
	 */
	public VNACalibrationRangeTable(VNACalibrationRange[] calRanges) {
		super(new VNACalibrationRangeTableModel(calRanges));
		TraceHelper.entry(this, "VNACalibrationRangeTable");
		//
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//
		setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		getColumnModel().getColumn(0).setPreferredWidth(100);
		getColumnModel().getColumn(1).setPreferredWidth(100);

		setDefaultRenderer(Long.class, new VNAFrequencyRenderer());
		//
		TraceHelper.exit(this, "VNACalibrationRangeTable");
	}

	/*
	 * JTable uses this method to determine the default renderer/ editor for each cell. If we didn't implement this method, then the last column would contain text ("true"/"false"), rather than a check box.
	 */
	@SuppressWarnings("unchecked")
	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	public void setSelected(int firstIndex) {
	}
}

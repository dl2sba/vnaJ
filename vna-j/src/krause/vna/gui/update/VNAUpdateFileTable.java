/**
 * Copyright (C) 2011 Dietmar Krause, DL2SBA
 */
package krause.vna.gui.update;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import krause.util.ras.logging.TraceHelper;

/**
 * @author Dietmar Krause
 * 
 */
public class VNAUpdateFileTable extends JTable {

	public VNAUpdateTableModel getModel() {
		return (VNAUpdateTableModel) super.getModel();
	}

	/**
	 * @param listModel
	 */
	public VNAUpdateFileTable() {
		super(new VNAUpdateTableModel());
		TraceHelper.entry(this, "VNAUpdateFileTable");
		//
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//
		getColumnModel().getColumn(0).setPreferredWidth(300);
		getColumnModel().getColumn(1).setPreferredWidth(100);
		getColumnModel().getColumn(2).setPreferredWidth(100);
		//
		TraceHelper.exit(this, "VNAUpdateFileTable");
	}

	/*
	 * JTable uses this method to determine the default renderer/ editor for
	 * each cell. If we didn't implement this method, then the last column would
	 * contain text ("true"/"false"), rather than a check box.
	 */
	@SuppressWarnings("unchecked")
	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}
}

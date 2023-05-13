/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 *
 *	This file: HistorizedLabelTable.java
 *  Part of:   vna-j
 */

package krause.vna.gui;

import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import krause.util.ras.logging.TraceHelper;

/**
 * @author Dietmar
 * 
 */
public class HistorizedLabelTable extends JTable {

	/**
	 * @param data
	 * 
	 */
	public HistorizedLabelTable(List<HistorizedLabelEntry> data) {
		super(new HistorizedLabelTableModel(data));
		TraceHelper.entry(this, "HistorizedLabelTable");
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		getColumnModel().getColumn(0).setPreferredWidth(100);
		getColumnModel().getColumn(1).setPreferredWidth(300);

		TraceHelper.exit(this, "HistorizedLabelTable");
	}

}

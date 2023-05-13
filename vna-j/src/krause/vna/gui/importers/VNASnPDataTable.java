/**
 * Copyright (C) 2007 Dietmar Krause, DL2SBA
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package krause.vna.gui.importers;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import krause.util.ras.logging.TraceHelper;
import krause.vna.gui.util.tables.VNAFrequencyRenderer;
import krause.vna.gui.util.tables.VNALossRenderer;
import krause.vna.gui.util.tables.VNAPhaseRenderer;

/**
 * @author Dietmar Krause
 * 
 */
public class VNASnPDataTable extends JTable {

	public VNASnPDataTableModel getModel() {
		return (VNASnPDataTableModel) super.getModel();
	}

	/**
	 * @param listModel
	 */
	public VNASnPDataTable() {
		super(new VNASnPDataTableModel());
		TraceHelper.entry(this, "VNASnPDataTable");
		//
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//
		getColumnModel().getColumn(0).setPreferredWidth(300);
		getColumnModel().getColumn(0).setWidth(300);
		for ( int i = 1; i < 9; ++i){
			getColumnModel().getColumn(i).setPreferredWidth(200);
			getColumnModel().getColumn(i).setWidth(200);
		}
		//
		getColumnModel().getColumn(0).setCellRenderer(new VNAFrequencyRenderer());
		getColumnModel().getColumn(1).setCellRenderer(new VNALossRenderer());
		getColumnModel().getColumn(2).setCellRenderer(new VNAPhaseRenderer());
		getColumnModel().getColumn(3).setCellRenderer(new VNALossRenderer());
		getColumnModel().getColumn(4).setCellRenderer(new VNAPhaseRenderer());
		getColumnModel().getColumn(5).setCellRenderer(new VNALossRenderer());
		getColumnModel().getColumn(6).setCellRenderer(new VNAPhaseRenderer());
		getColumnModel().getColumn(7).setCellRenderer(new VNALossRenderer());
		getColumnModel().getColumn(8).setCellRenderer(new VNAPhaseRenderer());
		//
		TraceHelper.exit(this, "VNASnPDataTable");
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

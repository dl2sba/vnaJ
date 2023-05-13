/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
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
package krause.vna.gui.cable.table;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import krause.util.ras.logging.TraceHelper;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.gui.cable.VNACableMeasurementPoint;
import krause.vna.gui.util.tables.VNAFrequencyRenderer;

/**
 * @author Dietmar Krause
 * 
 */
public class VNACableMeasurementPointTable extends JTable {

	public void addPoint(VNACableMeasurementPoint point) {
		getModel().addElement(point);
	}

	@Override
	public VNACableMeasurementPointTableModel getModel() {
		return (VNACableMeasurementPointTableModel) super.getModel();
	}

	/**
	 * @param listModel
	 */
	public VNACableMeasurementPointTable() {
		super(new VNACableMeasurementPointTableModel());
		TraceHelper.entry(this, "VNAFrequencyPairTable");
		//
		setDefaultRenderer(Integer.class, new VNAFrequencyRenderer());
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//
		TraceHelper.exit(this, "VNAFrequencyPairTable");
	}

	/*
	 * JTable uses this method to determine the default renderer/ editor for
	 * each cell. If we didn't implement this method, then the last column would
	 * contain text ("true"/"false"), rather than a check box.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	public void addPoint(VNACalibratedSample start, VNACalibratedSample stop) {
		VNACableMeasurementPoint point = new VNACableMeasurementPoint(false, false);
		point.setStart(start);
		point.setStop(stop);
		addPoint(point);
	}
}

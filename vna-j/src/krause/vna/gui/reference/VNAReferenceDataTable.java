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
package krause.vna.gui.reference;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import krause.util.ras.logging.TraceHelper;
import krause.vna.data.reference.VNAReferenceDataBlock;
import krause.vna.gui.util.tables.VNAFrequencyRenderer;

/**
 * @author Dietmar Krause
 * 
 */
public class VNAReferenceDataTable extends JTable {

	private IVNAReferenceDataSelectionListener owner;

	public void addReferenceData(VNAReferenceDataBlock block) {
		getModel().addElement(block);
	}

	public VNAReferenceDataTableModel getModel() {
		return (VNAReferenceDataTableModel) super.getModel();
	}

	/**
	 * @param listModel
	 */
	public VNAReferenceDataTable(IVNAReferenceDataSelectionListener pOwner) {
		super(new VNAReferenceDataTableModel());
		TraceHelper.entry(this, "VNACalibrationFileTable");
		//
		owner = pOwner;
		//
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//
		getColumnModel().getColumn(0).setPreferredWidth(300);
		getColumnModel().getColumn(1).setPreferredWidth(60);
		getColumnModel().getColumn(2).setPreferredWidth(120);
		getColumnModel().getColumn(3).setPreferredWidth(120);
		getColumnModel().getColumn(4).setPreferredWidth(150);
		getColumnModel().getColumn(5).setPreferredWidth(200);
		//
		getColumnModel().getColumn(2).setCellRenderer(new VNAFrequencyRenderer());
		getColumnModel().getColumn(3).setCellRenderer(new VNAFrequencyRenderer());
		//
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				TraceHelper.entry(this, "mouseClicked");
				int row = getSelectedRow();
				if (row >= 0) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						VNAReferenceDataBlock blk = getModel().getData().get(row);
						owner.valueChanged(blk, (e.getClickCount() > 1));
					} else {
						owner.valueChanged(null, false);
					}
				} else {
					owner.valueChanged(null, false);
				}
				TraceHelper.exit(this, "mouseClicked");
			}
		});
		//
		TraceHelper.exit(this, "VNACalibrationFileTable");
	}

	/*
	 * JTable uses this method to determine the default renderer/ editor for
	 * each cell. If we didn't implement this method, then the last column would
	 * contain text ("true"/"false"), rather than a check box.
	 */
	@SuppressWarnings("unchecked")
	public Class getColumnClass(int c) {
		Object o = getValueAt(0, c);
		if (o == null) {
			return String.class;
		} else {
			return o.getClass();
		}
	}

	public void setSelected(int firstIndex) {
	}
}

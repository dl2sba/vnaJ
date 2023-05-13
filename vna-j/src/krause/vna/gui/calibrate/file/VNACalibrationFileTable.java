/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 */
package krause.vna.gui.calibrate.file;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import krause.util.ras.logging.TraceHelper;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.gui.calibrate.IVNACalibrationSelectionListener;

public class VNACalibrationFileTable extends JTable {

	private IVNACalibrationSelectionListener owner;

	public void addCalibrationBlock(VNACalibrationBlock pair) {
		getModel().addElement(pair);
	}

	public VNACalibrationFileTableModel getModel() {
		return (VNACalibrationFileTableModel) super.getModel();
	}

	/**
	 * @param listModel
	 */
	public VNACalibrationFileTable(IVNACalibrationSelectionListener pOwner) {
		super(new VNACalibrationFileTableModel());
		TraceHelper.entry(this, "VNACalibrationFileTable");
		//
		owner = pOwner;
		//
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//
		getColumnModel().getColumn(0).setPreferredWidth(200);
		getColumnModel().getColumn(1).setPreferredWidth(150);
		getColumnModel().getColumn(2).setPreferredWidth(250);
		getColumnModel().getColumn(3).setPreferredWidth(100);
		getColumnModel().getColumn(4).setPreferredWidth(50);
		getColumnModel().getColumn(5).setPreferredWidth(80);
		getColumnModel().getColumn(6).setPreferredWidth(70);

		addMouseListener(new MouseAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent
			 * )
			 */
			@Override
			public void mouseClicked(MouseEvent e) {
				TraceHelper.entry(this, "mouseClicked");
				int row = getSelectedRow();
				if (row >= 0) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						VNACalibrationBlock blk = getModel().getData().get(row);
						owner.valueChanged(blk, (e.getClickCount() > 1));
					}
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
		return getValueAt(0, c).getClass();
	}

	public void setSelected(int firstIndex) {
	}
}

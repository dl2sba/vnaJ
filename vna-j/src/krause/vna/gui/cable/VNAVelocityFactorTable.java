package krause.vna.gui.cable;

import java.awt.Rectangle;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import krause.util.ras.logging.TraceHelper;

public class VNAVelocityFactorTable extends JTable {
	private DefaultTableCellRenderer crRight;
	private DefaultTableCellRenderer crLeft;

	@Override
	public VNAVelocityFactorTableModel getModel() {
		return (VNAVelocityFactorTableModel) super.getModel();
	}

	public VNAVelocityFactorTable() {
		super(new VNAVelocityFactorTableModel());
		TraceHelper.entry(this, "VNAVelocityFactorTable");

		crLeft = new DefaultTableCellRenderer();
		crRight = new DefaultTableCellRenderer();
		crRight.setHorizontalAlignment(SwingConstants.RIGHT);
		//
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//
		getColumnModel().getColumn(0).setPreferredWidth(100);
		getColumnModel().getColumn(1).setPreferredWidth(30);
		getColumnModel().getColumn(2).setPreferredWidth(30);
		//
		TraceHelper.exit(this, "VNAVelocityFactorTable");
	}

	@Override
	public Class<?> getColumnClass(int c) {
		return String.class;
	}

	@Override
	public TableCellRenderer getCellRenderer(int row, int col) {
		if (col == 0) {
			return crLeft;
		} else {
			return crRight;
		}
	}

	/**
	 * 
	 * @param row
	 */
	public void selectRow(int row) {
		//
		ListSelectionModel selectionModel = getSelectionModel();
		selectionModel.setSelectionInterval(row, row);

		Rectangle rect = getCellRect(row, 0, true);
		scrollRectToVisible(rect);
	}

	/**
	 * 
	 * @return
	 */
	public VNAVelocityFactor getSelectedItem() {
		int row = getSelectedRow();
		if (row >= 0) {
			return getModel().getDataAtRow(row);
		} else {
			return null;
		}
	}
}

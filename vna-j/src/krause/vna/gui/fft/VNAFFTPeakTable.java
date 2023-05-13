package krause.vna.gui.fft;

import java.awt.Rectangle;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import krause.util.ras.logging.TraceHelper;

public class VNAFFTPeakTable extends JTable {
	private DefaultTableCellRenderer crRight;
	private DefaultTableCellRenderer crLeft;

	@Override
	public VNAFFTPeakTableModel getModel() {
		return (VNAFFTPeakTableModel) super.getModel();
	}

	public VNAFFTPeakTable() {
		super(new VNAFFTPeakTableModel());
		final String methodName = "VNAFFTPeakTable";
		TraceHelper.entry(this, methodName);

		crLeft = new DefaultTableCellRenderer();
		crRight = new DefaultTableCellRenderer();
		crRight.setHorizontalAlignment(SwingConstants.RIGHT);
		//
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//
		getColumnModel().getColumn(0).setPreferredWidth(30);
		getColumnModel().getColumn(1).setPreferredWidth(60);
		TraceHelper.exit(this, methodName);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class getColumnClass(int c) {
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
	public VNAFFTPeakTableEntry getSelectedItem() {
		int row = getSelectedRow();
		if (row >= 0) {
			return getModel().getDataAtRow(row);
		} else {
			return null;
		}
	}
}

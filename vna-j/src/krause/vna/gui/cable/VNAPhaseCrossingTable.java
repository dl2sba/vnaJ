package krause.vna.gui.cable;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import krause.util.ras.logging.TraceHelper;

public class VNAPhaseCrossingTable extends JTable {
	private DefaultTableCellRenderer crRight;

	@Override
	public VNAPhaseCrossingTableModel getModel() {
		return (VNAPhaseCrossingTableModel) super.getModel();
	}

	public VNAPhaseCrossingTable() {
		super(new VNAPhaseCrossingTableModel());
		TraceHelper.entry(this, "VNAPhaseCrossingTable");

		crRight = new DefaultTableCellRenderer();
		crRight.setHorizontalAlignment(SwingConstants.RIGHT);
		//
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//
		getColumnModel().getColumn(0).setPreferredWidth(50);
		getColumnModel().getColumn(1).setPreferredWidth(50);

		//
		TraceHelper.exit(this, "VNAPhaseCrossingTable");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class getColumnClass(int c) {
		return String.class;
	}

	@Override
	public TableCellRenderer getCellRenderer(int row, int col) {
		return crRight;
	}
}

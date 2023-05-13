package krause.common.validation;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import krause.util.ras.logging.TraceHelper;

public class ValidationResultTable extends JTable {
	public ValidationResultTable() {
		super(new ValidationResultTableModel());
		TraceHelper.entry(this, "ValidationResultTable");
		//
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//
		getColumnModel().getColumn(0).setPreferredWidth(200);
		getColumnModel().getColumn(1).setPreferredWidth(500);
		getColumnModel().getColumn(2).setPreferredWidth(100);
		//
		TraceHelper.exit(this, "ValidationResultTable");
	}

	@Override
	public ValidationResultTableModel getModel() {
		return (ValidationResultTableModel) super.getModel();
	}

}

package krause.common.validation;

import javax.swing.table.AbstractTableModel;

import krause.common.resources.CommonMessages;

public class ValidationResultTableModel extends AbstractTableModel {

	private ValidationResults results;

	public int getColumnCount() {
		return 3;
	}

	public int getRowCount() {
		if (results != null) {
			return results.size();
		} else {
			return 0;
		}
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		ValidationResult result = results.get(rowIndex);
		if (columnIndex == 0) {
			return result.getErrorObject();
		} else if (columnIndex == 1) {
			return result.getMessage();
		} else if (columnIndex == 2) {
			return result.getException();
		} else {
			return "???";
		}
	}

	@Override
	public String getColumnName(int column) {
		if (column == 1) {
			return CommonMessages.getString("ValidationResultTableModel.Message");
		} else if (column == 0) {
			return CommonMessages.getString("ValidationResultTableModel.Field");
		} else if (column == 2) {
			return CommonMessages.getString("ValidationResultTableModel.Exception");
		}
		return "???";
	}

	public ValidationResults getResults() {
		return results;
	}

	public void setResults(ValidationResults results) {
		this.results = results;
		fireTableDataChanged();
	}

}

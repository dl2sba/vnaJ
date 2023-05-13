/**
 * Copyright (C) 2007 Dietmar Krause, DL2SBA
 */
package krause.vna.gui.reference;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import krause.util.ras.logging.TraceHelper;
import krause.vna.data.reference.VNAReferenceDataBlock;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.resources.VNAMessages;

/**
 * @author Dietmar Krause
 * 
 */
public class VNAReferenceDataTableModel extends AbstractTableModel {

	private List<VNAReferenceDataBlock> data = new ArrayList<VNAReferenceDataBlock>();

	private String[] columnNames = new String[] {
			VNAMessages.getString("VNAReferenceDataTableModel.name"),
			VNAMessages.getString("VNAReferenceDataTableModel.nofSteps"),
			VNAMessages.getString("VNAReferenceDataTableModel.startFreq"),
			VNAMessages.getString("VNAReferenceDataTableModel.stopFreq"),
			VNAMessages.getString("VNAReferenceDataTableModel.date"),
			VNAMessages.getString("VNAReferenceDataTableModel.comment"),
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return data.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return 6;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object rc = "";
		VNAReferenceDataBlock block = data.get(rowIndex);
		if (columnIndex == 0) {
			rc = block.getFile().getName();
		} else if (columnIndex == 1) {
			rc = Integer.valueOf(block.getSamples().length);
		} else if (columnIndex == 2) {
			rc = block.getMinFrequency();
		} else if (columnIndex == 3) {
			rc = block.getMaxFrequency();
		} else if (columnIndex == 4) {
			long zeit = block.getFile().lastModified();
			rc = VNAFormatFactory.getDateTimeFormat().format(zeit);
		} else if (columnIndex == 5) {
			rc = block.getComment();
		} else {
			rc = "???";
		}
		return rc;
	}

	/**
	 * @return Returns the data.
	 */
	public List<VNAReferenceDataBlock> getData() {
		return data;
	}

	/**
	 * @param pair
	 */
	public void addElement(VNAReferenceDataBlock block) {
		TraceHelper.entry(this, "addElement");
		data.add(block);
		fireTableDataChanged();
		TraceHelper.exit(this, "addElement");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	/**
	 * removes all data from the model
	 */
	public void clear() {
		data.clear();
	}

}

/**
 * Copyright (C) 2014 Dietmar Krause, DL2SBA
 */
package krause.vna.firmware;

import java.awt.Font;
import java.util.Locale;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import krause.util.ras.logging.TraceHelper;

/**
 * @author Dietmar Krause
 * 
 */
public class SimpleStringListbox extends JTable {

	/**
	 * add a message to the end of the list. Scroll the list so, that the last
	 * element is visible
	 * 
	 * @param message
	 */
	public void addMessage(String message) {
		getModel().addElement(message);

		scrollRectToVisible(getCellRect(getModel().getMessages().size() - 1, 0, true));
	}

	public SimpleStringListboxModel getModel() {
		return (SimpleStringListboxModel) super.getModel();
	}

	/**
	 * @param listModel
	 */
	public SimpleStringListbox(String title) {
		super(new SimpleStringListboxModel(title));
		TraceHelper.entry(this, "SimpleStringListbox");
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		//	only use fixed font for non japanese locales
		if (Locale.getDefault().getCountry().equals(Locale.JAPAN.getCountry())) {
			setFont(new Font("Monospaced", Font.PLAIN, 12));
		} else {
			setFont(new Font("Courier New", Font.PLAIN, 12));
		}

		TraceHelper.exit(this, "VNAUpdateFileTable");
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

	public void clear() {
		getModel().getMessages().clear();
		getModel().fireTableDataChanged();
	}
}

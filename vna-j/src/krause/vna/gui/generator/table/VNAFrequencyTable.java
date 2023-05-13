/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 */
package krause.vna.gui.generator.table;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.gui.util.tables.VNAFrequencyRenderer;

public class VNAFrequencyTable extends JTable {

	/**
	 * @param listModel
	 */
	public VNAFrequencyTable() {
		super(new VNAFrequencyTableModel());
		TraceHelper.entry(this, "VNAFrequencyTable");
		setDefaultRenderer(Long.class, new VNAFrequencyRenderer());
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//
		TraceHelper.exit(this, "VNAFrequencyTable");
	}

	public void addFrequency(Long pair) {
		getModel().addElement(pair);
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

	public VNAFrequencyTableModel getModel() {
		return (VNAFrequencyTableModel) super.getModel();
	}

	public boolean load(String myFileName) {
		TraceHelper.entry(this, "load");
		boolean result = false;
		XMLDecoder dec = null;
		FileInputStream fis = null;

		TraceHelper.text(this, "save", "Trying to read from [" + myFileName + "]");
		try {
			fis = new FileInputStream(myFileName);
			dec = new XMLDecoder(fis);
			//
			getModel().clear();
			//
			while (true) {
				getModel().addElement((Long) dec.readObject());
			}
		} catch (Exception e) {
			result = true;
		} finally {
			if (dec != null) {
				dec.close();
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					ErrorLogHelper.exception(this, "load", e);
				}
			}
		}
		if (getModel().getData().size() == 0) {
			loadDefaults();
		}
		TraceHelper.exitWithRC(this, "load", result);
		return result;
	}

	public void loadDefaults() {
	}

	/**
	 * write the table data out to the previously defined filename
	 * 
	 * @param filename
	 * @return
	 */
	public boolean save(String myFileName) {
		TraceHelper.entry(this, "save");
		boolean result = false;

		TraceHelper.text(this, "save", "Trying to write to [" + myFileName + "]");

		XMLEncoder enc = null;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(myFileName);
			enc = new XMLEncoder(fos);
			//
			for (Long obj : getModel().getData()) {
				enc.writeObject(obj);
			}
			result = true;
		} catch (Exception e) {
			ErrorLogHelper.exception(this, "save", e);
			ErrorLogHelper.text(this, "save", e.getMessage());
		} finally {
			if (enc != null) {
				enc.flush();
				enc.close();
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					ErrorLogHelper.exception(this, "save", e);
				}
			}
		}
		TraceHelper.exitWithRC(this, "save", result);
		return result;
	}

}

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
package krause.vna.gui.panels.data.table;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.comparators.VNAFrequencyPairComparator;
import krause.vna.gui.util.VNAFrequencyPair;
import krause.vna.gui.util.tables.VNAFrequencyRenderer;

/**
 * @author Dietmar Krause
 * 
 */
public class VNAFrequencyPairTable extends JTable {

	/**
	 * 
	 * @param pair
	 */
	public void addFrequencyPair(VNAFrequencyPair pair) {
		getModel().addElement(pair);
		Collections.sort(getModel().getData(), new VNAFrequencyPairComparator());
	}

	@Override
	public VNAFrequencyPairTableModel getModel() {
		return (VNAFrequencyPairTableModel) super.getModel();
	}

	public void loadDefaults() {
	}

	public boolean load(String myFileName) {
		TraceHelper.entry(this, "load");
		boolean result = false;
		XMLDecoder dec = null;
		FileInputStream fis = null;
		TraceHelper.text(this, "load", "Trying to read from [" + myFileName + "]");
		try {
			fis = new FileInputStream(myFileName);
			dec = new XMLDecoder(fis);
			//
			getModel().clear();
			//
			while (true) {
				getModel().addElement((VNAFrequencyPair) dec.readObject());
			}
		} catch (ArrayIndexOutOfBoundsException e2) {
			result = true;
		} catch (FileNotFoundException e) {
			TraceHelper.text(this, "load", "file [" + myFileName + "] not found. Using defaults.");
		} catch (Exception e) {
			ErrorLogHelper.exception(this, "load", e);
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
			for (Iterator<VNAFrequencyPair> it = getModel().getData().iterator(); it.hasNext();) {
				VNAFrequencyPair obj = it.next();
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

	/**
	 * @param listModel
	 */
	public VNAFrequencyPairTable() {
		super(new VNAFrequencyPairTableModel());
		TraceHelper.entry(this, "VNAFrequencyPairTable");
		//
		setDefaultRenderer(Long.class, new VNAFrequencyRenderer());
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//
		TraceHelper.exit(this, "VNAFrequencyPairTable");
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
}

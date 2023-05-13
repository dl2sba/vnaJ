/**
 * Copyright (C) 2007 Dietmar Krause, DL2SBA
 */
package krause.vna.gui.calibrate.calibrationkit;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

import krause.vna.data.calibrationkit.VNACalibrationKit;

public class VNACalibrationKitTableListModel extends DefaultListModel<VNACalibrationKit> {

	public VNACalibrationKitTableListModel() {
	}

	public List<VNACalibrationKit> getData() {
		List<VNACalibrationKit> rc = new ArrayList<VNACalibrationKit>();
		for (int i = 0; i < getSize(); ++i) {
			rc.add(getElementAt(i));
		}
		return rc;
	}

}

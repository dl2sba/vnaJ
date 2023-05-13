/**
 * Copyright (C) 2020 Dietmar Krause, DL2SBA
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
package krause.vna.gui.calibrate.calibrationkit;

import java.util.List;

import javax.swing.JList;
import javax.swing.ListSelectionModel;

import krause.util.ras.logging.TraceHelper;
import krause.vna.data.calibrationkit.VNACalibrationKit;

/**
 * @author Dietmar Krause
 * 
 */
public class VNACalibrationKitTable extends JList<VNACalibrationKit> {

	/**
	 * @param listModel
	 */
	public VNACalibrationKitTable() {
		TraceHelper.entry(this, "VNACalSetTable");
		//
		setModel(new VNACalibrationKitTableListModel());
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//
		TraceHelper.exit(this, "VNACalSetTable");
	}

	public void addCalSet(VNACalibrationKit newCalSet) {
		final VNACalibrationKitTableListModel model = (VNACalibrationKitTableListModel) getModel();
		model.addElement(newCalSet);
	}

	public void removeCalSet(VNACalibrationKit point) {
		final VNACalibrationKitTableListModel model = (VNACalibrationKitTableListModel) getModel();
		model.removeElement(point);
	}

	public void updateCalSet(VNACalibrationKit calSetToUpdate) {
		final VNACalibrationKitTableListModel model = (VNACalibrationKitTableListModel) getModel();
		final List<VNACalibrationKit> elements = model.getData();

		for (int i = 0; i < elements.size(); ++i) {
			final VNACalibrationKit element = elements.get(i);
			if (element.getId().equals(calSetToUpdate.getId())) {
				model.set(i, calSetToUpdate);
				break;
			}
		}
	}
}

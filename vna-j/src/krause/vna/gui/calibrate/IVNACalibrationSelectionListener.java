package krause.vna.gui.calibrate;

import krause.vna.data.calibrated.VNACalibrationBlock;

public interface IVNACalibrationSelectionListener {
	/**
	 * 
	 * @param blk
	 * @param doubleClick
	 */
	public void valueChanged(VNACalibrationBlock blk, boolean doubleClick);
}

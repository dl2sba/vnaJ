/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 * 
 */
package krause.vna.device.serial.std.lf;

import krause.vna.device.serial.std.VNADriverSerialStd;
import krause.vna.device.serial.std.VNADriverSerialStdMathHelper;
import krause.vna.gui.calibrate.mode1.VNACalibrationRange;

public class VNADriverSerialStdLf extends VNADriverSerialStd {
	/**
	 * 
	 */
	public VNADriverSerialStdLf() {
		setMathHelper(new VNADriverSerialStdMathHelper(this));
		setDeviceInfoBlock(new VNADriverSerialStdLfDIB());
		//
		getDeviceInfoBlock().restore(config, getDriverConfigPrefix());
	}

	public String getDriverConfigPrefix() {
		return "VNADriver.Serial.Std.Lf.";
	}

	@Override
	public VNACalibrationRange[] getSpecificCalibrationRanges() {
		return new VNACalibrationRange[] {
			new VNACalibrationRange(getDeviceInfoBlock().getMinFrequency(), getDeviceInfoBlock().getMaxFrequency(), MAXIMUM_SCAN_POINTS, 1)
		};
	}
}

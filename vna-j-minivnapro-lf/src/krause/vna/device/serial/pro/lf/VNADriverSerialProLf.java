/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 * 
 */
package krause.vna.device.serial.pro.lf;

import krause.util.ras.logging.TraceHelper;
import krause.vna.device.serial.pro.VNADriverSerialPro;
import krause.vna.device.serial.pro.VNADriverSerialProMathHelper;
import krause.vna.gui.calibrate.mode1.VNACalibrationRange;

public class VNADriverSerialProLf extends VNADriverSerialPro {

	/**
	 * 
	 */
	public VNADriverSerialProLf() {
		TraceHelper.entry(this, "VNADriverSerialProLf");
		setMathHelper(new VNADriverSerialProMathHelper(this));
		setDeviceInfoBlock(new VNADriverSerialProLfDIB());
		getDeviceInfoBlock().restore(config, getDriverConfigPrefix());
		TraceHelper.exit(this, "VNADriverSerialProLf");
	}

	public String getDriverConfigPrefix() {
		return "VNADriver.Serial.Pro.Lf.";
	}

	@Override
	public VNACalibrationRange[] getSpecificCalibrationRanges() {
		return new VNACalibrationRange[] {
			new VNACalibrationRange(getDeviceInfoBlock().getMinFrequency(), getDeviceInfoBlock().getMaxFrequency(), MAXIMUM_SCAN_POINTS, 1)
		};
	}
}

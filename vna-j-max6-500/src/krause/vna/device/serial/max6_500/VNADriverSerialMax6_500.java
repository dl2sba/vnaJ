/**
 * Copyright (C) 2020 Dietmar Krause, DL2SBA
 * 
 */
package krause.vna.device.serial.max6_500;

import krause.util.ras.logging.TraceHelper;
import krause.vna.device.serial.max6.VNADriverSerialMax6;
import krause.vna.device.serial.max6.VNADriverSerialMax6MathHelper;
import krause.vna.gui.calibrate.mode1.VNACalibrationRange;

public class VNADriverSerialMax6_500 extends VNADriverSerialMax6 {

	public VNADriverSerialMax6_500() {
		final String methodName = "VNADriverSerialMax6_500";
		TraceHelper.entry(this, methodName);

		setMathHelper(new VNADriverSerialMax6MathHelper(this));
		setDeviceInfoBlock(new VNADriverSerialMax6_500_DIB());
		//
		getDeviceInfoBlock().restore(this.config, getDriverConfigPrefix());
		TraceHelper.exit(this, methodName);
	}

	public String getDriverConfigPrefix() {
		return "VNADriver.Serial.MAX6_500.";
	}

	@Override
	public VNACalibrationRange[] getSpecificCalibrationRanges() {
		return new VNACalibrationRange[] {
				new VNACalibrationRange(getDeviceInfoBlock().getMinFrequency(), getDeviceInfoBlock().getMaxFrequency(), MAXIMUM_SCAN_POINTS, 1)
		};
	}
}
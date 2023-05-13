/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 */
package krause.vna.gui.scale.values;

import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.scale.VNAScaleSymbols.SCALE_TYPE;
import krause.vna.resources.VNAMessages;

public class VNAZAbsScale extends VNALinearScale {
	private static final double ABSOLUTE_MIN = 0;
	private static final double ABSOLUTE_MAX = 99999;
	private static final double DEFAULT_MIN = 0;
	private static final double DEFAULT_MAX = 1000;

	public VNAZAbsScale() {
		super(VNAMessages.getString("Scale.ZABS"), VNAMessages.getString("Scale.ZABS.Description"), SCALE_TYPE.SCALE_Z_ABS, "Ohm", VNAFormatFactory.getZFormat(), 0, 99999);
	}

	@Override
	public int getScaledSampleValue(VNACalibratedSample sample, int height) {
		return getScaledSampleValue(sample.getZ(), height);
	}

	@Override
	public int getScaledSampleValue(double z, int height) {
		int rc = 0;
		height -= 1;
		if ((getCurrentMaxValue() > 0) && (getCurrentMinValue() >= 0)) {
			rc = (int) (height * ((z - getCurrentMinValue()) / getRange()));
		} else if ((getCurrentMaxValue() > 0) && (getCurrentMinValue() < 0)) {
			rc = (int) (height * ((z - getCurrentMinValue()) / getRange()));
		} else {
			rc = (int) (height * ((z - getCurrentMinValue()) / getRange()));
		}
		return height - rc;
	}

	@Override
	public void initScaleFromConfigOrDib(VNADeviceInfoBlock block, VNAConfig config) {
		TraceHelper.entry(this, "initScaleFromConfigOrDib");
		setAbsolutMinValue(ABSOLUTE_MIN);
		setAbsolutMaxValue(ABSOLUTE_MAX);

		setDefaultMinValue(config.getDouble(this.getClass().getSimpleName() + ".defaultMinValue", DEFAULT_MIN));
		setDefaultMaxValue(config.getDouble(this.getClass().getSimpleName() + ".defaultMaxValue", DEFAULT_MAX));

		resetDefault();
		TraceHelper.exit(this, "initScaleFromConfigOrDib");
	}

	@Override
	public boolean supportsCustomScaling() {
		return true;
	}

}

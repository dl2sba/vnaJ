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

public class VNAThetaScale extends VNALinearScale {
	private static final double ABSOLUTE_MIN = -95;
	private static final double ABSOLUTE_MAX = 95;
	private static final double DEFAULT_MIN = -90;
	private static final double DEFAULT_MAX = 90;

	public VNAThetaScale() {
		super(VNAMessages.getString("Scale.THETA"), VNAMessages.getString("Scale.THETA.Description"), SCALE_TYPE.SCALE_THETA, "°", VNAFormatFactory.getThetaFormat(), -95, 95);
	}

	@Override
	public int getScaledSampleValue(VNACalibratedSample sample, int height) {
		return getScaledSampleValue(sample.getTheta(), height);
	}

	@Override
	public int getScaledSampleValue(double theta, int height) {
		int rc = 0;
		height -= 1;
		// both >0
		if ((getCurrentMaxValue() > 0) && (getCurrentMinValue() >= 0)) {
			rc = (int) ((height * 1.0) * ((theta - getCurrentMinValue()) / getRange()));
			// min <0 and max>0
		} else if ((getCurrentMaxValue() > 0) && (getCurrentMinValue() < 0)) {
			rc = (int) ((height * 1.0) * ((theta - getCurrentMinValue()) / getRange()));
			// both <0
		} else {
			rc = (int) ((height * 1.0) * ((theta - getCurrentMinValue()) / getRange()));
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

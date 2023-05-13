/**
 * Copyright (C) 2007 Dietmar Krause, DL2SBA
 */
package krause.vna.gui.scale.values;

import java.text.NumberFormat;

import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.gui.scale.VNAScaleSymbols.SCALE_TYPE;

public abstract class VNALossScale extends VNALinearScale {

	public VNALossScale(String scaleName, String scaleDescription, SCALE_TYPE scaleType, String pUnit, NumberFormat pFormat, double min, double max) {
		super(scaleName, scaleDescription, scaleType, pUnit, pFormat, min, max);
	}

	@Override
	public void initScaleFromConfigOrDib(VNADeviceInfoBlock block, VNAConfig config) {
		TraceHelper.entry(this, "initScaleFromConfigOrDib");
		setAbsolutMaxValue(block.getMinLoss());
		setAbsolutMinValue(block.getMaxLoss());

		setDefaultMinValue(config.getDouble(this.getClass().getSimpleName() + ".defaultMinValue", block.getMaxLoss()));
		setDefaultMaxValue(config.getDouble(this.getClass().getSimpleName() + ".defaultMaxValue", block.getMinLoss()));

		resetDefault();
		TraceHelper.exit(this, "initScaleFromConfigOrDib");
	}

	@Override
	public boolean supportsCustomScaling() {
		return true;
	}

	protected int internalGetScaledSampleValue(final double val, final int height) {
		return (int) ((height * 1.0) * ((getCurrentMaxValue() - val) / getRange()));
	}
}

/**
 * Copyright (C) 2007 Dietmar Krause, DL2SBA
 */
package krause.vna.gui.scale.values;

import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.scale.VNAScaleSymbols.SCALE_TYPE;

/**
 * @author Dietmar Krause
 * 
 */
public abstract class VNAPhaseScale extends VNALinearScale {
	public VNAPhaseScale(String scaleName, String desc, SCALE_TYPE scaleType) {
		super(scaleName, desc, scaleType, "°", VNAFormatFactory.getPhaseFormat(), -180, 180);
	}

	@Override
	public void initScaleFromConfigOrDib(VNADeviceInfoBlock block, VNAConfig config) {
		TraceHelper.entry(this, "initScaleFromConfigOrDib");
		setAbsolutMaxValue(block.getMaxPhase());
		setAbsolutMinValue(block.getMinPhase());

		setDefaultMinValue(config.getDouble(this.getClass().getSimpleName() + ".defaultMinValue", block.getMinPhase()));
		setDefaultMaxValue(config.getDouble(this.getClass().getSimpleName() + ".defaultMaxValue", block.getMaxPhase()));

		resetDefault();
		TraceHelper.exit(this, "initScaleFromConfigOrDib");
	}

	@Override
	public boolean supportsCustomScaling() {
		return true;
	}

	protected int internalGetScaleSampleValue(double val, int height) {
		int rc;
		rc = (int) ((height * 1.0) * ((val - getCurrentMinValue()) / getRange()));
		// invert
		rc = height - rc;
		return rc;
	}
}

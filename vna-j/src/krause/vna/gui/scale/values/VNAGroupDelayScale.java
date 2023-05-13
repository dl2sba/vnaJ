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

public class VNAGroupDelayScale extends VNALinearScale {
	private static final double ABSOLUTE_MAX = 1000;
	private static final double ABSOLUTE_MIN = -1000;
	private static final double DEFAULT_MAX = 100;
	private static final double DEFAULT_MIN = -100;

	public VNAGroupDelayScale() {
		super(VNAMessages.getString("Scale.GRPDLY"), VNAMessages.getString("Scale.GRPDLY.Description"), SCALE_TYPE.SCALE_GRPDLY, "ns", VNAFormatFactory.getGroupDelayFormat(), -1000, 1000);
	}

	public int getScaledSampleValue(VNACalibratedSample sample, int height) {
		return getScaledSampleValue(sample.getGroupDelay(), height);
	}

	public int getScaledSampleValue(double value, int height) {
		return height - (int) (height * ((value - getCurrentMinValue()) / getRange()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.gui.scale.VNAGenericScale#initScaleFromConfigOrDib(krause.vna.device.VNADeviceInfoBlock,
	 * krause.vna.config.VNAConfig)
	 */
	public void initScaleFromConfigOrDib(VNADeviceInfoBlock block, VNAConfig config) {
		TraceHelper.entry(this, "initScaleFromConfigOrDib");
		setAbsolutMaxValue(ABSOLUTE_MAX);
		setAbsolutMinValue(ABSOLUTE_MIN);

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

/**
 * Copyright (C) 2007 Dietmar Krause, DL2SBA
 */
package krause.vna.gui.scale.values;

import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.scale.VNAScaleSymbols.SCALE_TYPE;
import krause.vna.resources.VNAMessages;

public class VNARSSScale extends VNALinearScale {
	private static final double ABSOLUTE_MIN = -80;
	private static final double ABSOLUTE_MAX = 10;
	private static final double DEFAULT_MIN = -80;
	private static final double DEFAULT_MAX = 0;

	public VNARSSScale() {
		super(VNAMessages.getString("Scale.RSS"), VNAMessages.getString("Scale.RSS.Description"), SCALE_TYPE.SCALE_RSS, "dBm", VNAFormatFactory.getRSSFormat(), -99999, 99999);
	}

	@Override
	public int getScaledSampleValue(VNACalibratedSample sample, int height) {
		return getScaledSampleValue(sample.getRelativeSignalStrength1(), height);
	}

	@Override
	public int getScaledSampleValue(double rss, int height) {
		return height - (int) (height * ((rss - getCurrentMinValue()) / getRange()));
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

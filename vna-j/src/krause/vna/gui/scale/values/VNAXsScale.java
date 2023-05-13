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

public class VNAXsScale extends VNALinearScale {
	private static final double ABSOLUTE_MAX = 99999;
	private static final double ABSOLUTE_MIN = -99999;
	private static final double DEFAULT_MAX = 1000;
	private static final double DEFAULT_MIN = -1000;

	/**
	 * @param pName
	 * @param pType
	 */
	public VNAXsScale() {
		super(VNAMessages.getString("Scale.XS"), VNAMessages.getString("Scale.XS.Description"), SCALE_TYPE.SCALE_XS, "Ohm", VNAFormatFactory.getXsFormat(), -99999, 99999);
	}

	@Override
	public int getScaledSampleValue(VNACalibratedSample sample, int height) {
		return getScaledSampleValue(sample.getX(), height);
	}

	@Override
	public int getScaledSampleValue(double x, int height) {
		int rc = 0;
		height -= 1;
		if ((getCurrentMaxValue() > 0) && (getCurrentMinValue() >= 0)) {
			rc = (int) (height * ((x - getCurrentMinValue()) / getRange()));
		} else if ((getCurrentMaxValue() > 0) && (getCurrentMinValue() < 0)) {
			rc = (int) (height * ((x - getCurrentMinValue()) / getRange()));
		} else {
			rc = (int) (height * ((x - getCurrentMinValue()) / getRange()));
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

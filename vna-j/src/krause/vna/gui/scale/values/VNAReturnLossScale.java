/**
 * Copyright (C) 2014 Dietmar Krause, DL2SBA
 */
package krause.vna.gui.scale.values;

import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.scale.VNAScaleSymbols.SCALE_TYPE;
import krause.vna.resources.VNAMessages;

public class VNAReturnLossScale extends VNALossScale {
	public VNAReturnLossScale() {
		super(VNAMessages.getString("Scale.Returnloss"), VNAMessages.getString("Scale.Returnloss.Description"), SCALE_TYPE.SCALE_RETURNLOSS, "dB", VNAFormatFactory.getReflectionLossFormat(), -999, 999);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.gui.scale.VNAGenericScale#getScaledSampleValue(krause.vna.data.calibrated.VNACalibratedSample, int)
	 */
	public int getScaledSampleValue(VNACalibratedSample sample, int height) {
		double rl = sample.getReflectionLoss();
		return internalGetScaledSampleValue(rl, height - 1);
	}

	@Override
	public int getScaledSampleValue(double value, int height) {
		return internalGetScaledSampleValue(value, height - 1);
	}
}

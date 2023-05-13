/**
 * Copyright (C) 2014 Dietmar Krause, DL2SBA
 */
package krause.vna.gui.scale.values;

import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.scale.VNAScaleSymbols.SCALE_TYPE;
import krause.vna.resources.VNAMessages;

public class VNATransmissionLossScale extends VNALossScale {
	public VNATransmissionLossScale() {
		super(VNAMessages.getString("Scale.Transmissionloss"), VNAMessages.getString("Scale.Transmissionloss.Description"), SCALE_TYPE.SCALE_TRANSMISSIONLOSS, "dB", VNAFormatFactory.getReflectionLossFormat(), -999, 999);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.gui.scale.VNAGenericScale#getScaledSampleValue(krause.vna.data.calibrated.VNACalibratedSample, int)
	 */
	public int getScaledSampleValue(VNACalibratedSample sample, int height) {
		return getScaledSampleValue(sample.getTransmissionLoss(), height);
	}

	@Override
	public int getScaledSampleValue(double value, int height) {
		return internalGetScaledSampleValue(value, height - 1);
	}
}

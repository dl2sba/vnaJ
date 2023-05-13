/**
 * Copyright (C) 2014 Dietmar Krause, DL2SBA
 */
package krause.vna.gui.scale.values;

import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.gui.scale.VNAScaleSymbols.SCALE_TYPE;
import krause.vna.resources.VNAMessages;

public class VNATransmissionPhaseScale extends VNAPhaseScale {

	public VNATransmissionPhaseScale() {
		super(VNAMessages.getString("Scale.TransmissionPhase"), VNAMessages.getString("Scale.TransmissionPhase.description"), SCALE_TYPE.SCALE_TRANSMISSIONPHASE);
	}

	@Override
	public int getScaledSampleValue(final VNACalibratedSample sample, int height) {
		return getScaledSampleValue(sample.getTransmissionPhase(), height);
	}

	@Override
	public int getScaledSampleValue(double value, int height) {
		return internalGetScaleSampleValue(value, height - 1);
	}
}

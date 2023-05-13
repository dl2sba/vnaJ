/**
 * Copyright (C) 2007 Dietmar Krause, DL2SBA
 */
package krause.vna.gui.scale.values;

import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.gui.scale.VNAScaleSymbols.SCALE_TYPE;
import krause.vna.resources.VNAMessages;

public class VNAReturnPhaseScale extends VNAPhaseScale {

	public VNAReturnPhaseScale() {
		super(VNAMessages.getString("Scale.ReflectionPhase"), VNAMessages.getString("Scale.ReflectionPhase.Description"), SCALE_TYPE.SCALE_RETURNPHASE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.gui.scale.VNAGenericScale#getScaledSampleValue(krause.vna.data.calibrated.VNACalibratedSample, int)
	 */
	public int getScaledSampleValue(final VNACalibratedSample sample, int height) {
		double ph = sample.getReflectionPhase();
		return internalGetScaleSampleValue(ph, height - 1);
	}

	@Override
	public int getScaledSampleValue(double value, int height) {
		return internalGetScaleSampleValue(value, height - 1);
	}
}

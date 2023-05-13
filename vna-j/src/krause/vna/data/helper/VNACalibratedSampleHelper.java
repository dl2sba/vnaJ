/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 */
package krause.vna.data.helper;

import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.data.calibrated.VNACalibrationBlock;

public class VNACalibratedSampleHelper {
	static final VNACalibratedSampleHelper instance = new VNACalibratedSampleHelper();
	static final VNAConfig config = VNAConfig.getSingleton();

	private VNACalibratedSampleHelper() {

	}

	/**
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static VNACalibratedSample delta(VNACalibratedSample s1, VNACalibratedSample s2) {
		VNACalibratedSample rc = new VNACalibratedSample();
		rc.setFrequency(Math.abs(s1.getFrequency() - s2.getFrequency()));
		rc.setReflectionLoss(Math.abs(s1.getReflectionLoss() - s2.getReflectionLoss()));
		rc.setTransmissionLoss(Math.abs(s1.getTransmissionLoss() - s2.getTransmissionLoss()));
		rc.setReflectionPhase(Math.abs(s1.getReflectionPhase() - s2.getReflectionPhase()));
		rc.setTransmissionPhase(Math.abs(s1.getTransmissionPhase() - s2.getTransmissionPhase()));
		rc.setR(Math.abs(s1.getR() - s2.getR()));
		rc.setZ(Math.abs(s1.getZ() - s2.getZ()));
		rc.setX(Math.abs(s1.getX() - s2.getX()));
		return rc;
	}

	/**
	 * @param block
	 * @param mainFrame
	 * @return
	 */
	public static boolean blockMatchesCurrentConfig(VNACalibratedSampleBlock block, VNACalibrationBlock rcb) {
		boolean rc = false;
		TraceHelper.entry(instance, "blockMatchesCurrentConfig");
		if ((block != null) && (block.getCalibratedSamples().length == rcb.getNumberOfSteps())) {
			rc = true;
		}
		TraceHelper.exit(instance, "blockMatchesCurrentConfig");
		return rc;
	}
}

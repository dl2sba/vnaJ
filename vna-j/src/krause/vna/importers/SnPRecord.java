/**
 * Copyright (C) 2011 Dietmar Krause, DL2SBA
 *
 *	This file: SnPInputRecord.java
 *  Part of:   vna-j
 */

package krause.vna.importers;

import java.util.Arrays;

/**
 * @author Dietmar
 * 
 */
public class SnPRecord {

	private long frequency;
	private double[] loss = new double[4];
	private double[] phase = new double[4];

	public long getFrequency() {
		return frequency;
	}

	public void setFrequency(long frequency) {
		this.frequency = frequency;
	}

	public double[] getLoss() {
		return loss;
	}

	public void setLoss(int i, double loss) {
		this.loss[i] = loss;
	}

	public double[] getPhase() {
		return phase;
	}

	public void setPhase(int i, double phase) {
		this.phase[i] = phase;
	}

	@Override
	public String toString() {
		return "SnPInputRecord [frequency=" + frequency + ", loss=" + Arrays.toString(loss) + ", phase=" + Arrays.toString(phase) + "]";
	}

}

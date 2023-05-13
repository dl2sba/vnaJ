/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 *
 *	This file: VNAScanRange.java
 *  Part of:   vna-j
 */

package krause.vna.device;

import krause.vna.data.VNAFrequencyRange;

/**
 * @author Dietmar
 * 
 */
public class VNAScanRange extends VNAFrequencyRange {

	private int numScanPoints;

	/**
	 * 
	 * @param pStart
	 * @param pStop
	 * @param pSamples
	 */
	public VNAScanRange(long pStart, long pStop, int pSamples) {
		super(pStart, pStop);
		numScanPoints = pSamples;
	}

	/**
	 * 
	 * @param pRange
	 * @param pSamples
	 */
	public VNAScanRange(VNAFrequencyRange pRange, int pSamples) {
		super(pRange);
		numScanPoints = pSamples;
	}

	/**
	 * @return the numScanPoints
	 */
	public int getNumScanPoints() {
		return numScanPoints;
	}

	/**
	 * @param numScanPoints
	 *            the numScanPoints to set
	 */
	public void setNumScanPoints(int numScanPoints) {
		this.numScanPoints = numScanPoints;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("VNAScanRange [numScanPoints=%d, %s]", numScanPoints, super.toString());
	}
}

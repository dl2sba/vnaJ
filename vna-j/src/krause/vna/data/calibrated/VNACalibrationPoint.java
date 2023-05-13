/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 */
package krause.vna.data.calibrated;

import java.io.Serializable;

import org.apache.commons.math3.complex.Complex;

public class VNACalibrationPoint implements Serializable {

	private long frequency = 0;

	private Complex deltaE = null;
	private Complex e11 = null;
	private Complex e00 = null;
	private double loss = 0;
	private double phase = 0;
	private int rss1 = 0;
	private int rss2 = 0;
	private int rss3 = 0;

	private Complex edf = null;
	private Complex esf = null;
	private Complex erf = null;

	/**
	 * Copy all data from pSource to this instance except the frequency value
	 * 
	 * @param pSource
	 */
	public void copy(VNACalibrationPoint pSource) {
		setDeltaE(pSource.getDeltaE());
		setE00(pSource.getE00());
		setE11(pSource.getE11());
		setLoss(pSource.getLoss());
		setPhase(pSource.getPhase());
		setRss1(pSource.getRss1());
		setRss2(pSource.getRss2());
		setRss3(pSource.getRss3());
		
		setEdf(pSource.getEdf());
		setErf(pSource.getErf());
		setEsf(pSource.getEsf());
	}

	/**
	 * @return the deltaE
	 */
	public Complex getDeltaE() {
		return deltaE;
	}

	/**
	 * @return the e00
	 */
	public Complex getE00() {
		return e00;
	}

	/**
	 * @return the e11
	 */
	public Complex getE11() {
		return e11;
	}

	public Complex getEdf() {
		return edf;
	}

	public Complex getErf() {
		return erf;
	}

	public Complex getEsf() {
		return esf;
	}

	/**
	 * @return the frequency
	 */
	public long getFrequency() {
		return frequency;
	}

	/**
	 * @return the loss
	 */
	public double getLoss() {
		return loss;
	}

	/**
	 * @return the rss1
	 */
	public int getRss1() {
		return rss1;
	}

	/**
	 * @return the rss2
	 */
	public int getRss2() {
		return rss2;
	}

	/**
	 * @return the rss3
	 */
	public int getRss3() {
		return rss3;
	}

	/**
	 * @param deltaE
	 *            the deltaE to set
	 */
	public void setDeltaE(Complex deltaE) {
		this.deltaE = deltaE;
	}

	/**
	 * @param e00
	 *            the e00 to set
	 */
	public void setE00(Complex e00) {
		this.e00 = e00;
	}

	/**
	 * @param e11
	 *            the e11 to set
	 */
	public void setE11(Complex e11) {
		this.e11 = e11;
	}

	public void setEdf(Complex edf) {
		this.edf = edf;
	}

	public void setErf(Complex erf) {
		this.erf = erf;
	}

	public void setEsf(Complex esf) {
		this.esf = esf;
	}

	/**
	 * @param frequency
	 *            the frequency to set
	 */
	public void setFrequency(long frequency) {
		this.frequency = frequency;
	}

	/**
	 * @param loss
	 *            the loss to set
	 */
	public void setLoss(double loss) {
		this.loss = loss;
	}

	/**
	 * @param rss1
	 *            the rss1 to set
	 */
	public void setRss1(int rss1) {
		this.rss1 = rss1;
	}

	/**
	 * @param rss2
	 *            the rss2 to set
	 */
	public void setRss2(int rss2) {
		this.rss2 = rss2;
	}

	/**
	 * @param rss3
	 *            the rss3 to set
	 */
	public void setRss3(int rss3) {
		this.rss3 = rss3;
	}

	public double getPhase() {
		return phase;
	}

	public void setPhase(double phase) {
		this.phase = phase;
	}
}

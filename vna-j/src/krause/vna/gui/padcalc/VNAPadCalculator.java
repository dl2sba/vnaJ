/**
 * Copyright (C) 2011 Dietmar Krause, DL2SBA
 *
 *	This file: VNAPadCalculator.java
 *  Part of:   vna-j
 */

package krause.vna.gui.padcalc;

import java.util.ArrayList;
import java.util.List;

import krause.util.ras.logging.TraceHelper;

/**
 * @author Dietmar
 * 
 */
public class VNAPadCalculator {

	private VNAGenericPad pad;

	/**
	 * @param atten
	 */
	public void calculatePad(double atten) {
		TraceHelper.entry(this, "calculatePad");

		if (pad instanceof VNAPiPad) {
			calculatePiPad(atten);
		} else {
			calculateTPad(atten);
		}
		TraceHelper.exit(this, "calculatePad");
	}

	/**
	 * @param atten
	 * @param fullSeries
	 */
	private void calculateTPad(double atten) {
		TraceHelper.entry(this, "calculateTPad");
		calculatePiPad(atten);

		double r3 = (pad.getR3() * pad.getR4()) / (pad.getR3() + pad.getR4() + pad.getR5());
		double r4 = (pad.getR3() * pad.getR5()) / (pad.getR3() + pad.getR4() + pad.getR5());
		double r5 = (pad.getR4() * pad.getR5()) / (pad.getR3() + pad.getR4() + pad.getR5());
		
		pad.setR3(r3);
		pad.setR4(r4);
		pad.setR5(r5);
		
		TraceHelper.exit(this, "calculateTPad");
	}

	/**
	 * @param atten
	 * @param fullSeries
	 */
	private void calculatePiPad(double atten) {
		TraceHelper.entry(this, "calculatePiPad");

		double a = Math.log(10.0) / 20.0;
		double z = Math.exp(a * atten);
		double m = pad.getR1() / pad.getR2();

		double r3 = (pad.getR1() * (z * z - 1)) / ((z * z) - (2 * z * Math.sqrt(m)) + 1);
		double r4 = (pad.getR1() * (z * z - 1)) / (2 * z * Math.sqrt(m));
		double r5 = (pad.getR2() * (z * z - 1)) / ((z * z - (2 * z / Math.sqrt(m)) + 1));

		pad.setR3(r3);
		pad.setR4(r4);
		pad.setR5(r5);
		TraceHelper.exit(this, "calculatePiPad");
	}

	/**
	 * @param fullSeries
	 * @param resistanceX
	 * @param percentPrecision
	 * @return
	 */
	public List<Double> calculateSeriesCircuit(double[] fullSeries, double resistanceX, int maxParts, double percentPrecision) {
		List<Double> rc = new ArrayList<Double>();

		double minVal = resistanceX * (1.0 - percentPrecision);
		double maxVal = resistanceX * (1.0 + percentPrecision);

		int indexFound = -1;

		// first try to find in native sequency
		for (int i = 0; i < fullSeries.length; ++i) {
			double val = fullSeries[i];
			if ((minVal <= val) && (val <= maxVal)) {
				indexFound = i;
				break;
			}
		}

		// found ?
		if (indexFound != -1) {
			// yes
			rc.add(Double.valueOf(fullSeries[indexFound]));
		} else {
			int minIdx = -1;
			for (int i = 0; i < fullSeries.length; ++i) {
				double val = fullSeries[i];
				if (val >= minVal) {
					minIdx = i - 1;
					break;
				}
			}
			if (minIdx != -1) {
				double firstRes = fullSeries[minIdx];
				rc.add(Double.valueOf(firstRes));

				double newRes = resistanceX - firstRes;
				if (maxParts > 1) {
					List<Double> newSub = calculateSeriesCircuit(fullSeries, newRes, maxParts - 1, percentPrecision);
					rc.addAll(newSub);
				}
			}
		}
		return rc;
	}

	/**
	 * 
	 * @param orgSeries
	 * @param decades
	 * @return
	 */
	public double[] createFullSeries(double[] orgSeries, int decades) {
		int orgLen = orgSeries.length;
		int newLen = orgLen * decades;
		double[] rc = new double[newLen];

		int mult = 1;
		for (int i = 0; i < decades; ++i) {
			int offset = i * orgLen;
			for (int j = 0; j < orgLen; ++j) {
				int idx = j + offset;
				double d = orgSeries[j] * mult;
				rc[idx] = d;
			}
			mult *= 10;
		}
		return rc;
	}

	public VNAGenericPad getPad() {
		return pad;
	}

	public void setPad(VNAGenericPad pad) {
		this.pad = pad;
	}

	/**
	 * @param pp
	 */
	public void reverseCalcPad(double atten) {
		TraceHelper.entry(this, "reverseCalcPad");
		if (pad instanceof VNAPiPad) {
			reverseCalculatePiPad(atten);
		} else {
			reverseCalculateTPad(atten);
		}
		TraceHelper.exit(this, "reverseCalcPad");
	}

	/**
	 * @param atten
	 */
	private void reverseCalculateTPad(double atten) {
		TraceHelper.entry(this, "reverseCalculateTPad");
		TraceHelper.exit(this, "reverseCalculateTPad");

	}

	/**
	 * @param atten
	 */
	private void reverseCalculatePiPad(double atten) {
		TraceHelper.entry(this, "reverseCalculatePiPad");
		VNAPiPad pp = (VNAPiPad) getPad();

		double a = Math.log(10.0) / 20.0;
		double z = Math.exp(a * atten);
		double m = pp.getR1() / pp.getR2();

		double r1 = (pp.getR3() * ((z * z) - (2 * z * Math.sqrt(m)) + 1)) / (z * z - 1);
		double r2 = (pp.getR5() * (z * z - (2 * z / Math.sqrt(m)) + 1)) / (z * z - 1);

		pp.setR1(r1);
		pp.setR2(r2);
		TraceHelper.exit(this, "reverseCalculatePiPad");
	}
}

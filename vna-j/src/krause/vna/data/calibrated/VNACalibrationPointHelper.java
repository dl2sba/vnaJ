/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 */
package krause.vna.data.calibrated;

import java.io.Serializable;

import org.apache.commons.math3.complex.Complex;

public class VNACalibrationPointHelper implements Serializable {

	/**
	 * 
	 * @param y1
	 * @param y2
	 * @param k1
	 * @param k2
	 * @return
	 */
	public static double interpolate(double y1, double y2, long k1, long k2) {
		return y1 + (k1 * (y2 - y1) / k2);
	}

	/**
	 * 
	 * @param y1
	 * @param y2
	 * @param k1
	 * @param k2
	 * @return
	 */
	public static int interpolate(int y1, int y2, long k1, long k2) {
		return (int) (y1 + (k1 * (y2 - y1) / k2));
	}

	public static Complex interpolate(Complex c1, Complex c2, long k1, long k2) {
		if (c1 != null && c2 != null) {
			double real = interpolate(c1.getReal(), c2.getReal(), k1, k2);
			double imag = interpolate(c1.getImaginary(), c2.getImaginary(), k1, k2);

			return new Complex(real, imag);
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param p1
	 * @param p2
	 * @param newFreq
	 * @return
	 */
	public static VNACalibrationPoint interpolate(final VNACalibrationPoint p1, final VNACalibrationPoint p2, long f) {

		long f1 = p1.getFrequency();
		long f2 = p2.getFrequency();

		long K1 = f - f1;
		long K2 = f2 - f1;

		VNACalibrationPoint rc = new VNACalibrationPoint();
		rc.setFrequency(f);

		rc.setLoss(interpolate(p1.getLoss(), p2.getLoss(), K1, K2));
		rc.setPhase(interpolate(p1.getPhase(), p2.getPhase(), K1, K2));
		rc.setDeltaE(interpolate(p1.getDeltaE(), p2.getDeltaE(), K1, K2));
		rc.setE00(interpolate(p1.getE00(), p2.getE00(), K1, K2));
		rc.setE11(interpolate(p1.getE11(), p2.getE11(), K1, K2));

		rc.setEdf(interpolate(p1.getEdf(), p2.getEdf(), K1, K2));
		rc.setErf(interpolate(p1.getErf(), p2.getErf(), K1, K2));
		rc.setEsf(interpolate(p1.getEsf(), p2.getEsf(), K1, K2));

		rc.setRss1(interpolate(p1.getRss1(), p2.getRss1(), K1, K2));
		rc.setRss2(interpolate(p1.getRss2(), p2.getRss2(), K1, K2));
		rc.setRss3(interpolate(p1.getRss3(), p2.getRss3(), K1, K2));

		return rc;
	}
}

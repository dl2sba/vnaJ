package krause.vna.data.fft;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.complex.Complex;

import krause.common.math.FFT;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.calibrated.VNACalibratedSample;

public class VNAFFTHelper {
	public static Complex[] createFFTDataPoints(VNACalibratedSample[] input) {
		int inputLen = input.length;
		// fill up to 2^n
		//
		int nx = (int) (Math.log(inputLen) / Math.log(2.0));
		if ((1 << nx) != inputLen) {
			nx += 1;
		}

		int newLen = 1 << nx;

		Complex[] rc = new Complex[newLen];

		for (int i = 0; i < inputLen; ++i) {
			// VNACalibratedSample orgPt = input[i];
			// double radian = Math.toRadians(orgPt.getReflectionPhase());
			// double amplitude = Math.pow(10, orgPt.getReflectionLoss() /
			// 20.0);
			// double real = amplitude * Math.cos(radian);
			// double imag = amplitude * Math.sin(radian);
			// rc[i] = new Complex(real, imag);
			rc[i] = input[i].getRHO();
		}

		for (int i = inputLen; i < newLen; ++i) {
			rc[i] = Complex.ZERO;
		}
		return rc;
	}

	public static Complex[] doIFFT(VNACalibratedSample[] calibratedSamples) {
		Complex[] fftIn = VNAFFTHelper.createFFTDataPoints(calibratedSamples);
		Complex[] rc = FFT.ifft(fftIn);
		return rc;
	}

	public static Complex[] doFFT(VNACalibratedSample[] calibratedSamples) {
		Complex[] fftIn = VNAFFTHelper.createFFTDataPoints(calibratedSamples);
		Complex[] rc = FFT.fft(fftIn);
		return rc;
	}

	public static double[] getABS(VNACalibratedSample[] inp) {
		double[] rc;
		rc = new double[inp.length];

		for (int i = 0; i < inp.length; ++i) {
			double radian = (inp[i].getReflectionPhase() * Math.PI / 180.0);
			double amplitude = Math.pow(10, inp[i].getReflectionLoss() / 20.0);
			double real = amplitude * Math.cos(radian);
			double imag = amplitude * Math.sin(radian);

			rc[i] = new Complex(real, imag).abs();
		}

		return rc;
	}

	public static double[] getABS(Complex[] inp) {
		double[] rc;
		rc = new double[inp.length];

		for (int i = 0; i < inp.length; ++i) {
			rc[i] = inp[i].abs();
		}

		return rc;
	}

	/**
	 * Find the peaks in the given double array
	 * 
	 * @param input
	 *            where to find the peaks
	 * 
	 * @return the found peaks
	 */
	public static int[] findPeaks(double[] input) {
		List<Integer> peakList = new ArrayList<Integer>();

		final int inputLen = input.length;
		final double gradient[] = new double[inputLen - 1];

		double lastY = input[0];
		for (int i = 1; i < inputLen; ++i) {
			double currY = input[i];
			double currGradient = currY - lastY;
			if (Math.abs(currGradient) < 0.005) {
				currGradient = 0;
			}
			gradient[i - 1] = currGradient;
			lastY = currY;
		}

		int state = 0;
		final int max = gradient.length;
		for (int i = 0; i < max; ++i) {
			double currGradient = gradient[i];
			switch (state) {
			case 0:
				if (currGradient > 0) {
					state = 1;
				}
				break;

			case 1:
				if (currGradient < 0) {
					peakList.add(Integer.valueOf(i));
					state = 0;
				}
				break;
			}
		}

		int peakLen = peakList.size();
		int peaks[] = new int[peakLen];
		for (int i = 0; i < peakLen; ++i) {
			peaks[i] = peakList.get(i);
		}
		TraceHelper.exitWithRC(Class.class, "findPeaks", peakList);
		return peaks;
	}

	/**
	 * 
	 * @param fftAbs
	 * @return
	 */
	public static int[] findPeaks2(double[] fftAbs) {
		int rc[] = null;

		double min = Double.MIN_NORMAL;
		int minIdx = -1;

		for (int i = 0; i < fftAbs.length; ++i) {
			if (fftAbs[i] > min) {
				min = fftAbs[i];
				minIdx = i;
			}
		}

		if (minIdx != -1) {
			rc = new int[] {
					minIdx
			};
		}
		return rc;
	}

	public static Complex[] createFFTDataPoints2(VNACalibratedSample[] input) {
		int inputLen = input.length;
		// fill up to 2^n
		//
		int nx = (int) (Math.log(inputLen) / Math.log(2.0));
		if ((1 << nx) != inputLen) {
			nx += 1;
		}

		int newLen = 1 << nx;

		Complex[] rc = new Complex[newLen];

		for (int i = 0; i < inputLen; ++i) {
			VNACalibratedSample orgPt = input[i];
			double radian = (orgPt.getReflectionPhase() * Math.PI / 180.0);
			double amplitude = Math.pow(10, orgPt.getReflectionLoss() / 20.0);
			double real = amplitude * Math.cos(radian);
			double imag = amplitude * Math.sin(radian);
			rc[i] = new Complex(real, imag);
			// rc[i] = input[i].getRHO();
		}

		for (int i = inputLen; i < newLen; ++i) {
			rc[i] = Complex.ZERO;
		}
		return rc;
	}
}

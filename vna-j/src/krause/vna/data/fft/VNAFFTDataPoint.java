package krause.vna.data.fft;

import org.apache.commons.math3.complex.Complex;

public class VNAFFTDataPoint {
	private long frequency;
	private double radian;
	private Complex realComplex;
	private Complex iFFT;
	private double dtf;
	private double distance;
	private double time;

	public long getFrequency() {
		return frequency;
	}

	public void setFrequency(long frequency) {
		this.frequency = frequency;
	}

	public double getRadian() {
		return radian;
	}

	public void setRadian(double radian) {
		this.radian = radian;
	}

	public Complex getRealComplex() {
		return realComplex;
	}

	public void setRealComplex(Complex realComplex) {
		this.realComplex = realComplex;
	}

	public Complex getiFFT() {
		return iFFT;
	}

	public void setiFFT(Complex iFFT) {
		this.iFFT = iFFT;
	}

	public double getDtf() {
		return dtf;
	}

	public void setDtf(double dtf) {
		this.dtf = dtf;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}
}

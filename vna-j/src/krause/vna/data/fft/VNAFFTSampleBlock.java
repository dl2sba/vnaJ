package krause.vna.data.fft;

import org.apache.commons.math3.complex.Complex;

import krause.vna.data.calibrated.VNACalibratedSampleBlock;

public class VNAFFTSampleBlock {
	private VNACalibratedSampleBlock scanData;
	private Complex[] fftInput = null;
	private Complex[] fftOutput = null;

	public VNACalibratedSampleBlock getScanData() {
		return scanData;
	}

	public void setScanData(VNACalibratedSampleBlock scanData) {
		this.scanData = scanData;
	}

	public Complex[] getFftInput() {
		return fftInput;
	}

	public void setFftInput(Complex[] fftInput) {
		this.fftInput = fftInput;
	}

	public Complex[] getFftOutput() {
		return fftOutput;
	}

	public void setFftOutput(Complex[] fftOutput) {
		this.fftOutput = fftOutput;
	}

}

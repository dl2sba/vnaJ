package krause.vna.data.calibrated;

import krause.vna.data.calibrationkit.VNACalibrationKit;

public class VNACalibrationContextTiny extends VNACalibrationContext {
	private double cosineCorrection;
	private double gainCorrection;
	private double sineCorrection;
	private double tempCorrection;
	private VNACalibrationKit calibrationSet;
	

	public double getCosineCorrection() {
		return cosineCorrection;
	}

	public double getGainCorrection() {
		return gainCorrection;
	}

	public double getSineCorrection() {
		return sineCorrection;
	}

	public double getTempCorrection() {
		return tempCorrection;
	}

	public void setCosineCorrection(double cosineCorrection) {
		this.cosineCorrection = cosineCorrection;
	}

	public void setGainCorrection(double gainCorrection) {
		this.gainCorrection = gainCorrection;
	}

	public void setSineCorrection(double sineCorrection) {
		this.sineCorrection = sineCorrection;
	}

	public void setTempCorrection(double tempCorrection) {
		this.tempCorrection = tempCorrection;
	}

	public VNACalibrationKit getCalibrationKit() {
		return calibrationSet;
	}

	public void setCalibrationSet(VNACalibrationKit calibrationSet) {
		this.calibrationSet = calibrationSet;
	}
}

package krause.vna.marker.math;

import krause.vna.data.calibrated.VNACalibratedSample;

public class VNAMarkerMathInput {

	private long lowFrequency;
	private long highFrequency;
	private long centerFrequency;

	private double Z;
	private double Rs;
	private double Xs;

	/**
	 * 
	 */
	public VNAMarkerMathInput() {
	}

	/**
	 * @param sample
	 */
	public VNAMarkerMathInput(VNACalibratedSample sample) {
		Z = sample.getZ();
		Rs = sample.getR();
		Xs = sample.getX();
		centerFrequency = sample.getFrequency();
	}

	/**
	 * @param z
	 *            the z to set
	 */
	public void setZ(double z) {
		Z = z;
	}

	/**
	 * @return the z
	 */
	public double getZ() {
		return Z;
	}

	/**
	 * @return the rs
	 */
	public double getRs() {
		return Rs;
	}

	/**
	 * @param rs
	 *            the rs to set
	 */
	public void setRs(double rs) {
		Rs = rs;
	}

	/**
	 * @return the xs
	 */
	public double getXs() {
		return Xs;
	}

	/**
	 * @param xs
	 *            the xs to set
	 */
	public void setXs(double xs) {
		Xs = xs;
	}

	public long getLowFrequency() {
		return lowFrequency;
	}

	public void setLowFrequency(long lowFrequency) {
		this.lowFrequency = lowFrequency;
	}

	public long getHighFrequency() {
		return highFrequency;
	}

	public void setHighFrequency(long highFrequency) {
		this.highFrequency = highFrequency;
	}

	public long getCenterFrequency() {
		return centerFrequency;
	}

	public void setCenterFrequency(long centerFrequency) {
		this.centerFrequency = centerFrequency;
	}

}

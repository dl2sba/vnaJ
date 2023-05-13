package krause.vna.gui.cable;

import krause.vna.data.calibrated.VNACalibratedSample;

public class VNACableMeasurementPoint {
	private VNACalibratedSample start;
	private VNACalibratedSample stop;
	private long delta;
	private double length;
	private double velocityFactor;
	private boolean meterMode = true;
	@SuppressWarnings("unused")
	private boolean scale360 = false;

	// speed of light
	public static final double SOL = 299792458;
	public static final double METER2FEET = 0.30480;
	
	public double getVelocityFactor() {
		return velocityFactor;
	}


	public VNACableMeasurementPoint(boolean pMeterMode, boolean pScale360) {
		meterMode = pMeterMode;
		scale360 = pScale360;
	}

	/**
	 * @return the delta
	 */
	public long getDelta() {
		return delta;
	}

	/**
	 * @param delta
	 *            the delta to set
	 */
	public void setDelta(long delta) {
		this.delta = delta;
	}

	/**
	 * @return the length
	 */
	public double getLength() {
		return length;
	}

	/**
	 * @return the start
	 */
	public VNACalibratedSample getStart() {
		return start;
	}

	/**
	 * @param start
	 *            the start to set
	 */
	public void setStart(VNACalibratedSample start) {
		this.start = start;
	}

	/**
	 * @return the stop
	 */
	public VNACalibratedSample getStop() {
		return stop;
	}

	/**
	 * @param stop
	 *            the stop to set
	 */
	public void setStop(VNACalibratedSample stop) {
		this.stop = stop;
	}

	/**
	 * 
	 * @param velocityFactor
	 */
	public void calculateLength(double velocityFactor) {
		setDelta(getStop().getFrequency() - getStart().getFrequency());
		double l = (SOL * velocityFactor) / (2 * getDelta() + 0.0000001);
		if (!meterMode) {
			l /= METER2FEET;
		}
		length = l;
	}

	public void calculateVelocityFactor(double pCableLength) {
		setDelta(getStop().getFrequency() - getStart().getFrequency());

		if (!meterMode) {
			pCableLength *= METER2FEET;
		}
		double vf = (pCableLength * 2 * getDelta()) / SOL;

		velocityFactor = vf;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "VNACableMeasurementPoint [delta=" + delta + ", length=" + length + ", start=" + start.getFrequency() + ", stop=" + stop.getFrequency() + "]";
	}

}

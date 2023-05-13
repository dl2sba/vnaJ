package krause.vna.data;

import java.io.Serializable;

import org.apache.commons.math3.complex.Complex;

public class VNABaseSample implements Serializable {

	// version without P data -100000000020181228L
	// version with P data -100000000020200105L
	private static final long serialVersionUID = -100000000020200105L;
	private double angle = 0;
	private double loss = 0;
	private long frequency = 0;

	private int rss1 = 0;
	private int rss2 = 0;
	private int rss3 = 0;

	private boolean hasPData = false;
	private int p1 = 0;
	private int p2 = 0;
	private int p3 = 0;
	private int p4 = 0;
	private int p1Ref = 0;
	private int p2Ref = 0;
	private int p3Ref = 0;
	private int p4Ref = 0;

	public VNABaseSample() {
	}

	public VNABaseSample(double angle, double loss, long frequency) {
		super();
		this.angle = angle;
		this.loss = loss;
		this.frequency = frequency;
		hasPData = false;
	}

	public VNABaseSample(VNABaseSample raw) {
		this.frequency = raw.getFrequency();
		this.angle = raw.getAngle();
		this.loss = raw.getLoss();
		this.rss1 = raw.getRss1();
		this.rss2 = raw.getRss2();
		this.rss3 = raw.getRss3();
		this.hasPData = raw.hasPData;
		this.p1 = raw.p1;
		this.p2 = raw.p2;
		this.p3 = raw.p3;
		this.p4 = raw.p4;
		this.p1Ref = raw.p1Ref;
		this.p2Ref = raw.p2Ref;
		this.p3Ref = raw.p3Ref;
		this.p4Ref = raw.p4Ref;
	}

	/**
	 * Creates a complex number with real part == loss and imaginary part == angle
	 * 
	 * @return the rawData
	 */
	public Complex asComplex() {
		return new Complex(this.loss, this.angle);
	}

	/**
	 * @return the angle
	 */
	public double getAngle() {
		return this.angle;
	}

	/**
	 * @return the frequency
	 */
	public long getFrequency() {
		return this.frequency;
	}

	/**
	 * @return the loss
	 */
	public double getLoss() {
		return this.loss;
	}

	public int getP1() {
		return this.p1;
	}

	public int getP1Ref() {
		return this.p1Ref;
	}

	public int getP2() {
		return this.p2;
	}

	public int getP2Ref() {
		return this.p2Ref;
	}

	public int getP3() {
		return this.p3;
	}

	public int getP3Ref() {
		return this.p3Ref;
	}

	public int getP4() {
		return this.p4;
	}

	public int getP4Ref() {
		return this.p4Ref;
	}

	public int getRss1() {
		return this.rss1;
	}

	public int getRss2() {
		return this.rss2;
	}

	public int getRss3() {
		return this.rss3;
	}

	public boolean hasPData() {
		return this.hasPData;
	}

	/**
	 * @param angle
	 *            the angle to set
	 */
	public void setAngle(double angle) {
		this.angle = angle;
	}

	/**
	 * @param frequency
	 *            the frequency to set
	 */
	public void setFrequency(long frequency) {
		this.frequency = frequency;
	}

	public void setHasPData(boolean hasPData) {
		this.hasPData = hasPData;
	}

	/**
	 * @param loss
	 *            the loss to set
	 */
	public void setLoss(double loss) {
		this.loss = loss;
	}

	public void setP1(int p1) {
		this.p1 = p1;
	}

	public void setP1Ref(int p1Ref) {
		this.p1Ref = p1Ref;
	}

	public void setP2(int p2) {
		this.p2 = p2;
	}

	public void setP2Ref(int p2Ref) {
		this.p2Ref = p2Ref;
	}

	public void setP3(int p3) {
		this.p3 = p3;
	}

	public void setP3Ref(int p3Ref) {
		this.p3Ref = p3Ref;
	}

	public void setP4(int p4) {
		this.p4 = p4;
	}

	public void setP4Ref(int p4Ref) {
		this.p4Ref = p4Ref;
	}

	public void setRss1(int rss1) {
		this.rss1 = rss1;
	}

	public void setRss2(int rss2) {
		this.rss2 = rss2;
	}

	public void setRss3(int rss3) {
		this.rss3 = rss3;
	}

	/**
	 * Copy all data from pSource to this instance except the frequency value
	 * 
	 * @param pSource
	 */
	public void copy(VNABaseSample pSource) {
		setAngle(pSource.getAngle());
		setLoss(pSource.getLoss());
		setHasPData(pSource.hasPData);
		setP1(pSource.getP1());
		setP2(pSource.getP2());
		setP3(pSource.getP3());
		setP4(pSource.getP4());
		setP1Ref(pSource.getP1Ref());
		setP2Ref(pSource.getP2Ref());
		setP3Ref(pSource.getP3Ref());
		setP4Ref(pSource.getP4Ref());
		setRss1(pSource.getRss1());
		setRss2(pSource.getRss2());
		setRss3(pSource.getRss3());
	}

	@Override
	public String toString() {
		return "VNABaseSample [frequency=" + this.frequency + " angle=" + this.angle + ", loss=" + this.loss + ", rss1=" + this.rss1 + ", rss2=" + this.rss2 + ", rss3=" + this.rss3 + "]";
	}

}

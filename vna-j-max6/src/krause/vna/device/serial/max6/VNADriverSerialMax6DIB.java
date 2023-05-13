package krause.vna.device.serial.max6;

import org.apache.commons.math3.complex.Complex;

import krause.common.TypedProperties;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNAScanMode;
import krause.vna.device.VNAScanModeParameter;
import krause.vna.device.VNASerialDeviceInfoBlock;
import krause.vna.gui.scale.VNAScaleSymbols.SCALE_TYPE;

public class VNADriverSerialMax6DIB extends VNASerialDeviceInfoBlock {

	public static final float DDS_MHZ = 400;
	public static final int DEFAULT_TICKS = (int) ((1l << 32) / DDS_MHZ);

	public static final int MAX_LEVEL = 16383;
	public static final int MIN_LEVEL = 0;

	private double levelMin;
	private double levelMax;
	private double ReflectionScale;
	private double reflectionOffset;
	private double transmissionScale;
	private double transmissionOffset;
	private double rss1Scale;
	private double rss1Offset;
	private double rss2Scale;
	private double rss2Offset;
	private double rss3Scale;
	private double rss3Offset;

	public VNADriverSerialMax6DIB() {
		reset();
		//
		clearScanModeParameters();
		addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_REFLECTION, true, false, false, false, SCALE_TYPE.SCALE_RETURNLOSS, SCALE_TYPE.SCALE_RETURNPHASE));
		addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_RSS1, true, false, false, true, SCALE_TYPE.SCALE_RETURNLOSS, SCALE_TYPE.SCALE_TRANSMISSIONLOSS));

		//
		setShortName("MAX6");
		setLongName("MAX6 - SP3SWJ");
		setType("4");
	}

	/**
	 * @return the levelMax
	 */
	public double getLevelMax() {
		return levelMax;
	}

	/**
	 * @return the levelMin
	 */
	public double getLevelMin() {
		return levelMin;
	}

	/**
	 * @return the reflectionOffset
	 */
	public double getReflectionOffset() {
		return reflectionOffset;
	}

	/**
	 * @return the reflectionScale
	 */
	public double getReflectionScale() {
		return ReflectionScale;
	}

	/**
	 * @return the rss1Offset
	 */
	public double getRss1Offset() {
		return rss1Offset;
	}

	/**
	 * @return the rss1Scale
	 */
	public double getRss1Scale() {
		return rss1Scale;
	}

	/**
	 * @return the rss2Offset
	 */
	public double getRss2Offset() {
		return rss2Offset;
	}

	/**
	 * @return the rss2Scale
	 */
	public double getRss2Scale() {
		return rss2Scale;
	}

	/**
	 * @return the rss3Offset
	 */
	public double getRss3Offset() {
		return rss3Offset;
	}

	/**
	 * @return the rss3Scale
	 */
	public double getRss3Scale() {
		return rss3Scale;
	}

	public double getTransmissionOffset() {
		return transmissionOffset;
	}

	public double getTransmissionScale() {
		return transmissionScale;
	}

	@Override
	public void reset() {
		super.reset();

		setReferenceResistance(new Complex(DEFAULT_REFERENCE_RESISTANCE_REAL, DEFAULT_REFERENCE_RESISTANCE_IMAG));
		setDdsTicksPerMHz(DEFAULT_TICKS);

		setMinFrequency(100000);
		setMaxFrequency(180000000);
		setMinLoss(5);
		setMaxLoss(-80);
		setMinPhase(0);
		setMaxPhase(180);
		setNumberOfSamples4Calibration(2000);
		setLevelMax(20);
		setLevelMin(-80);

		setRss1Scale(VNADriverSerialMax6MathHelper.DEFAULT_RSS_SCALE);
		setRss1Offset(VNADriverSerialMax6MathHelper.DEFAULT_RSS_OFFSET);

		setRss2Scale(VNADriverSerialMax6MathHelper.DEFAULT_RSS_SCALE);
		setRss2Offset(VNADriverSerialMax6MathHelper.DEFAULT_RSS_OFFSET);

		setRss3Scale(VNADriverSerialMax6MathHelper.DEFAULT_RSS_SCALE);
		setRss3Offset(VNADriverSerialMax6MathHelper.DEFAULT_RSS_OFFSET);

		setReflectionOffset(VNADriverSerialMax6MathHelper.DEFAULT_REFLECTION_OFFSET);
		setReflectionScale(VNADriverSerialMax6MathHelper.DEFAULT_REFLECTION_SCALE);

		setTransmissionOffset(VNADriverSerialMax6MathHelper.DEFAULT_TRANSMISSION_OFFSET);
		setTransmissionScale(VNADriverSerialMax6MathHelper.DEFAULT_TRANSMISSION_SCALE);

	}

	@Override
	public void restore(TypedProperties config, String prefix) {
		TraceHelper.entry(this, "restore", prefix);
		reset();
		super.restore(config, prefix);

		setRss1Scale(config.getDouble(prefix + "rss1Scale", VNADriverSerialMax6MathHelper.DEFAULT_RSS_SCALE));
		setRss1Offset(config.getDouble(prefix + "rss1Offset", VNADriverSerialMax6MathHelper.DEFAULT_RSS_OFFSET));

		setRss2Scale(config.getDouble(prefix + "rss2Scale", VNADriverSerialMax6MathHelper.DEFAULT_RSS_SCALE));
		setRss2Offset(config.getDouble(prefix + "rss2Offset", VNADriverSerialMax6MathHelper.DEFAULT_RSS_OFFSET));

		setRss3Scale(config.getDouble(prefix + "rss3Scale", VNADriverSerialMax6MathHelper.DEFAULT_RSS_SCALE));
		setRss3Offset(config.getDouble(prefix + "rss3Offset", VNADriverSerialMax6MathHelper.DEFAULT_RSS_OFFSET));

		setReflectionScale(config.getDouble(prefix + "reflectionScale", VNADriverSerialMax6MathHelper.DEFAULT_REFLECTION_SCALE));
		setReflectionOffset(config.getDouble(prefix + "reflectionOffset", VNADriverSerialMax6MathHelper.DEFAULT_REFLECTION_OFFSET));

		setTransmissionScale(config.getDouble(prefix + "transmissionScale", VNADriverSerialMax6MathHelper.DEFAULT_TRANSMISSION_SCALE));
		setTransmissionOffset(config.getDouble(prefix + "transmissionOffset", VNADriverSerialMax6MathHelper.DEFAULT_TRANSMISSION_OFFSET));
		TraceHelper.exit(this, "restore");
	}

	/**
	 * @param levelMax
	 *            the levelMax to set
	 */
	public void setLevelMax(double levelMax) {
		this.levelMax = levelMax;
	}

	/**
	 * @param levelMin
	 *            the levelMin to set
	 */
	public void setLevelMin(double levelMin) {
		this.levelMin = levelMin;
	}

	/**
	 * @param reflectionOffset
	 *            the reflectionOffset to set
	 */
	public void setReflectionOffset(double reflectionOffset) {
		this.reflectionOffset = reflectionOffset;
	}

	/**
	 * @param reflectionScale
	 *            the reflectionScale to set
	 */
	public void setReflectionScale(double reflectionScale) {
		ReflectionScale = reflectionScale;
	}

	/**
	 * @param rss1Offset
	 *            the rss1Offset to set
	 */
	public void setRss1Offset(double rss1Offset) {
		this.rss1Offset = rss1Offset;
	}

	/**
	 * @param rss1Scale
	 *            the rss1Scale to set
	 */
	public void setRss1Scale(double rss1Scale) {
		this.rss1Scale = rss1Scale;
	}

	/**
	 * @param rss2Offset
	 *            the rss2Offset to set
	 */
	public void setRss2Offset(double rss2Offset) {
		this.rss2Offset = rss2Offset;
	}

	/**
	 * @param rss2Scale
	 *            the rss2Scale to set
	 */
	public void setRss2Scale(double rss2Scale) {
		this.rss2Scale = rss2Scale;
	}

	/**
	 * @param rss3Offset
	 *            the rss3Offset to set
	 */
	public void setRss3Offset(double rss3Offset) {
		this.rss3Offset = rss3Offset;
	}

	/**
	 * @param rss3Scale
	 *            the rss3Scale to set
	 */
	public void setRss3Scale(double rss3Scale) {
		this.rss3Scale = rss3Scale;
	}

	public void setTransmissionOffset(double transmissionOffset) {
		this.transmissionOffset = transmissionOffset;
	}

	public void setTransmissionScale(double transmissionScale) {
		this.transmissionScale = transmissionScale;
	}

	@Override
	public void store(TypedProperties config, String prefix) {
		TraceHelper.entry(this, "store");
		super.store(config, prefix);

		config.putDouble(prefix + "rss1Scale", getRss1Scale());
		config.putDouble(prefix + "rss1Offset", getRss1Offset());
		config.putDouble(prefix + "rss2Scale", getRss2Scale());
		config.putDouble(prefix + "rss2Offset", getRss2Offset());
		config.putDouble(prefix + "rss3Scale", getRss3Scale());
		config.putDouble(prefix + "rss3Offset", getRss3Offset());

		config.putDouble(prefix + "reflectionOffset", getReflectionOffset());
		config.putDouble(prefix + "reflectionScale", getReflectionScale());
		config.putDouble(prefix + "transmissionOffset", getTransmissionOffset());
		config.putDouble(prefix + "transmissionScale", getTransmissionScale());
		TraceHelper.exit(this, "store");
	}

	@Override
	public int calculateRealBaudrate(int driverBaudrate) {
		return driverBaudrate / 10;
	}
}

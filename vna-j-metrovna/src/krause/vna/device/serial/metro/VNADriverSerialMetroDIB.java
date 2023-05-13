package krause.vna.device.serial.metro;

import org.apache.commons.math3.complex.Complex;

import krause.common.TypedProperties;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNAScanMode;
import krause.vna.device.VNADriverFactorySymbols;
import krause.vna.device.VNAGenericDriver;
import krause.vna.device.VNAScanModeParameter;
import krause.vna.device.VNASerialDeviceInfoBlock;
import krause.vna.gui.scale.VNAScaleSymbols.SCALE_TYPE;

public class VNADriverSerialMetroDIB extends VNASerialDeviceInfoBlock {

	public static final float DDS_MHZ = 400;
	public static final int DEFAULT_TICKS = (int) ((1l << 32) / DDS_MHZ);

	public static final long MAX_FREQUENCY = ONE_MHZ * 4400;
	public static final double MAX_RETURNLOSS = -60.0;
	public static final double MAX_TRANSMISSIONLOSS = -76.0;
	public static final double MAX_PHASE = 180.0;
	public static final long MIN_FREQUENCY = 100 * ONE_KHZ;
	public static final double MIN_LOSS = 5.0;
	public static final double MIN_PHASE = 0;

	public static final boolean INVERT_REFLECTION_LOSS = true;
	public static final boolean INVERT_TRANSMISSION_LOSS = true;

	private double maxReflectionLoss;
	private double maxTransmissionLoss;

	public VNADriverSerialMetroDIB() {
		reset();
		//
		clearScanModeParameters();
		addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_REFLECTION, true, false, false, false, SCALE_TYPE.SCALE_RETURNLOSS, SCALE_TYPE.SCALE_RETURNPHASE));
		addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_TRANSMISSION, false, false, false, true, SCALE_TYPE.SCALE_TRANSMISSIONLOSS, SCALE_TYPE.SCALE_TRANSMISSIONPHASE));
		//
		setShortName("MetroVNA");
		setLongName("IZ7LDG - MetroVNA");
		//
		setType(VNADriverFactorySymbols.TYPE_METROVNA);
	}

	public double getMaxReflectionLoss() {
		return maxReflectionLoss;
	}

	public double getMaxTransmissionLoss() {
		return maxTransmissionLoss;
	}

	@Override
	public void reset() {
		super.reset();

		setMinFrequency(MIN_FREQUENCY);
		setMaxFrequency(180 * ONE_MHZ);
		setMinPhase(MIN_PHASE);
		setMaxPhase(MAX_PHASE);
		setNumberOfSamples4Calibration(VNAGenericDriver.MINIMUM_SCAN_POINTS);
		setDdsTicksPerMHz(DEFAULT_TICKS);
		setReferenceResistance(new Complex(DEFAULT_REFERENCE_RESISTANCE_REAL, DEFAULT_REFERENCE_RESISTANCE_IMAG));
		setMaxReflectionLoss(MAX_RETURNLOSS);
		setMaxTransmissionLoss(MAX_TRANSMISSIONLOSS);

		setMinLoss(MIN_LOSS);
		setMaxLoss(MAX_TRANSMISSIONLOSS);
	}

	@Override
	public void restore(TypedProperties config, String prefix) {
		TraceHelper.entry(this, "restore", prefix);
		//
		// first reset all values
		reset();

		// get the values from the super class
		super.restore(config, prefix);

		// no the class specific ones
		setMaxTransmissionLoss(config.getDouble(prefix + "maxTransmissionLoss", getMaxTransmissionLoss()));
		setMaxReflectionLoss(config.getDouble(prefix + "maxReflectionLoss", getMaxReflectionLoss()));
		TraceHelper.exit(this, "restore");
	}

	public void setMaxReflectionLoss(double maxReflectionLoss) {
		this.maxReflectionLoss = maxReflectionLoss;
	}

	public void setMaxTransmissionLoss(double maxTransmissionLoss) {
		this.maxTransmissionLoss = maxTransmissionLoss;
	}

	@Override
	public void store(TypedProperties config, String prefix) {
		TraceHelper.entry(this, "store");
		super.store(config, prefix);
		config.putDouble(prefix + "maxTransmissionLoss", getMaxTransmissionLoss());
		config.putDouble(prefix + "maxReflectionLoss", getMaxReflectionLoss());
		TraceHelper.exit(this, "store");
	}

	@Override
	public int calculateRealBaudrate(int driverBaudrate) {
		return driverBaudrate / 30;
	}
}

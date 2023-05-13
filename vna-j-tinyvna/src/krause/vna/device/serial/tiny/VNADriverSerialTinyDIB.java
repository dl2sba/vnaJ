package krause.vna.device.serial.tiny;

import krause.common.TypedProperties;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNAScanMode;
import krause.vna.device.VNADriverFactorySymbols;
import krause.vna.device.VNAScanModeParameter;
import krause.vna.device.VNASerialDeviceInfoBlock;
import krause.vna.gui.scale.VNAScaleSymbols.SCALE_TYPE;

public class VNADriverSerialTinyDIB extends VNASerialDeviceInfoBlock {
	public static final int AFTERCOMMANDDELAY = 0;
	public static final int BAURATE_BOOT = 230400;
	public static final int BAURATE_SCAN = 921600;
	public static final int CAL_STEPS = 1000;
	public static final long DDS_TICKS = 10000000l;
	public static final double GAIN_CORR = 1.0;
	public static final double IF_PHASE_CORR = 1.10;

	public static final long MAX_FREQ = 3000 * ONE_MHZ;
	public static final long MIN_FREQ = 1 * ONE_MHZ;

	public static final double MAX_CORR_GAIN = 2;
	public static final double MAX_CORR_IF_PHASE = 20.0;
	public static final double MAX_CORR_PHASE = 20.0;
	public static final double MAX_CORR_TEMP = 0.5;

	public static final double MIN_CORR_GAIN = 0.5;
	public static final double MIN_CORR_IF_PHASE = -20.0;
	public static final double MIN_CORR_PHASE = -20.0;
	public static final double MIN_CORR_TEMP = -0.5;

	public static final double MIN_PHASE = -180;
	public static final double MAX_PHASE = 180;

	public static final int PRESCALER_DEFAULT = 10;

	public static final double PHASE_CORR = 0.0;
	public static final double TEMP_CORR = 0.011;

	public static final double TEMP_REFERENCE = 40.0;

	public static final double MIN_LOSS = 0.0;
	public static final double MAX_LOSS = -120.0;

	public static final int MAX_BOOTBAUD = 921600;
	public static final int MIN_BOOTBAUD = 19200;

	public static final int AUTOCAL_NUM_SAMPLES = 800;
	public static final long AUTOCAL_START_FREQ = 100000000;
	public static final long AUTOCAL_STOP_FREQ = 200000000;
	public static final int AUTOCAL_NUM_OVERSAMPLES = 4;

	private int bootloaderBaudrate;
	private double gainCorrection;
	private double ifPhaseCorrection;
	private double phaseCorrection;
	private int prescaler;
	private String scanCommandReflection;
	private String scanCommandTransmission;
	private double tempCorrection;

	public VNADriverSerialTinyDIB() {
		TraceHelper.entry(this, "VNADriverSerialTinyDIB");
		//
		reset();
		//
		clearScanModeParameters();
		addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_REFLECTION, true, true, true, false, SCALE_TYPE.SCALE_RETURNLOSS, SCALE_TYPE.SCALE_RETURNPHASE));
		addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_TRANSMISSION, true, false, false, true, SCALE_TYPE.SCALE_TRANSMISSIONLOSS, SCALE_TYPE.SCALE_TRANSMISSIONPHASE));
		//
		setShortName("miniVNA Tiny");
		setLongName("mini radio solutions - miniVNA Tiny");
		setType(VNADriverFactorySymbols.TYPE_TINYVNA);

		TraceHelper.exit(this, "VNADriverSerialTinyDIB");
	}

	public int getBootloaderBaudrate() {
		return bootloaderBaudrate;
	}

	public double getGainCorrection() {
		return gainCorrection;
	}

	public double getIfPhaseCorrection() {
		return ifPhaseCorrection;
	}

	public double getPhaseCorrection() {
		return phaseCorrection;
	}

	public int getPrescaler() {
		return prescaler;
	}

	public String getScanCommandReflection() {
		return scanCommandReflection;
	}

	public String getScanCommandTransmission() {
		return scanCommandTransmission;
	}

	public double getTempCorrection() {
		return tempCorrection;
	}

	@Override
	public void reset() {
		super.reset();

		setMinFrequency(MIN_FREQ);
		setMaxFrequency(MAX_FREQ);
		setMinLoss(MIN_LOSS);
		setMaxLoss(MAX_LOSS);
		setMinPhase(MIN_PHASE);
		setMaxPhase(MAX_PHASE);
		setNumberOfSamples4Calibration(CAL_STEPS);
		setNumberOfOverscans4Calibration(1);

		// special settings for the tinyvna
		setPrescaler(PRESCALER_DEFAULT);
		setScanCommandTransmission("6");
		setScanCommandReflection("7");

		setDdsTicksPerMHz(DDS_TICKS);

		setAfterCommandDelay(AFTERCOMMANDDELAY);
		setBaudrate(BAURATE_SCAN);
		setBootloaderBaudrate(BAURATE_BOOT);

		setPhaseCorrection(PHASE_CORR);
		setGainCorrection(GAIN_CORR);
		setTempCorrection(TEMP_CORR);

		setIfPhaseCorrection(IF_PHASE_CORR);

		// the default filtermode is mode 1 for the tiny
		setFilterMode(FILTER_NONE);

		// enable peak suppression
		setPeakSuppression(true);
	}

	@Override
	public void restore(TypedProperties config, String prefix) {
		reset();
		super.restore(config, prefix);
		setPhaseCorrection(config.getDouble(prefix + "phaseCorrection", getPhaseCorrection()));
		setGainCorrection(config.getDouble(prefix + "gainCorrection", getGainCorrection()));
		setTempCorrection(config.getDouble(prefix + "tempCorrection", getTempCorrection()));
		setIfPhaseCorrection(config.getDouble(prefix + "ifPhaseCorrection", getIfPhaseCorrection()));
		setBootloaderBaudrate(config.getInteger(prefix + "bootloaderBaudrate", getBootloaderBaudrate()));
	}

	public void setBootloaderBaudrate(int bootloaderBaudrate) {
		this.bootloaderBaudrate = bootloaderBaudrate;
	}

	public void setGainCorrection(double gainCorrection) {
		this.gainCorrection = gainCorrection;
	}

	public void setIfPhaseCorrection(double ifPhaseCorrection) {
		this.ifPhaseCorrection = ifPhaseCorrection;
	}

	public void setPhaseCorrection(double phaseCorrection) {
		this.phaseCorrection = phaseCorrection;
	}

	public void setPrescaler(int prescaler) {
		this.prescaler = prescaler;
	}

	public void setScanCommandReflection(String scanCommandReflection) {
		this.scanCommandReflection = scanCommandReflection;
	}

	public void setScanCommandTransmission(String scanCommandTransmission) {
		this.scanCommandTransmission = scanCommandTransmission;
	}

	public void setTempCorrection(double tempCorrection) {
		this.tempCorrection = tempCorrection;
	}

	@Override
	public void store(TypedProperties config, String prefix) {
		super.store(config, prefix);
		config.putDouble(prefix + "phaseCorrection", getPhaseCorrection());
		config.putDouble(prefix + "gainCorrection", getGainCorrection());
		config.putDouble(prefix + "tempCorrection", getTempCorrection());
		config.putDouble(prefix + "ifPhaseCorrection", getIfPhaseCorrection());
	}

	@Override
	public long[] getSwitchPoints() {
		return new long[] {
				1045000000,
				1525000000
		};
	}

	@Override
	public int calculateRealBaudrate(int driverBaudrate) {
		return driverBaudrate / 300;
	}
}

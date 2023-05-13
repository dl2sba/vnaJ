package krause.vna.device.serial.pro2;

import krause.common.TypedProperties;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNAScanMode;
import krause.vna.device.VNADriverFactorySymbols;
import krause.vna.device.VNAScanModeParameter;
import krause.vna.device.VNASerialDeviceInfoBlock;
import krause.vna.gui.scale.VNAScaleSymbols.SCALE_TYPE;

public class VNADriverSerialPro2DIB extends VNASerialDeviceInfoBlock {
	public static final int AFTERCOMMANDDELAY = 50;
	public static final int BAURATE_BOOT = 115200;
	public static final int BAURATE_SCAN = 921600;
	public static final long DDS_TICKS = 8259595;
	public static final double IF_PHASE_CORR = 1.10;

	public static final long MAX_FREQ = 230 * ONE_MHZ;
	public static final long MIN_FREQ = 10 * ONE_KHZ;

	public static final double MIN_PHASE = -180;
	public static final double MAX_PHASE = 180;

	public static final double MIN_LOSS = 0.0;
	public static final double MAX_LOSS = -110.0;
	public static final int RESOLUTION_16BIT = 1;
	public static final int RESOLUTION_24BIT = 2;
	public static final int SAMPLE_RATE = 4;

	private int bootloaderBaudrate;
	private int resolution;
	private int sampleRate;

	public VNADriverSerialPro2DIB() {
		TraceHelper.entry(this, "VNADriverSerialPro2DIB");
		//
		reset();
		//
		clearScanModeParameters();
		addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_REFLECTION, true, true, true, false, SCALE_TYPE.SCALE_RETURNLOSS, SCALE_TYPE.SCALE_RETURNPHASE));
		addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_TRANSMISSION, true, false, false, true, SCALE_TYPE.SCALE_TRANSMISSIONLOSS, SCALE_TYPE.SCALE_TRANSMISSIONPHASE));
		//
		setShortName("miniVNA-pro2");
		setLongName("mini radio solutions - miniVNA pro2");
		setType(VNADriverFactorySymbols.TYPE_MININVNAPRO2);

		TraceHelper.exit(this, "VNADriverSerialPro2DIB");
	}

	public int getBootloaderBaudrate() {
		return bootloaderBaudrate;
	}

	public String getScanCommandReflection() {
		String rc = "1";
		switch (getResolution()) {
		case RESOLUTION_16BIT:
			rc = "1";
			break;

		case RESOLUTION_24BIT:
			rc = "101";
			break;

		default:
			ErrorLogHelper.text(this, "getScanCommandReflection", "Illegal resolution " + getResolution());
			break;
		}
		return rc;
	}

	public String getScanCommandTransmission() {
		String rc = "0";
		switch (getResolution()) {
		case RESOLUTION_16BIT:
			rc = "0";
			break;

		case RESOLUTION_24BIT:
			rc = "100";
			break;

		default:
			ErrorLogHelper.text(this, "getScanCommandTransmission", "Illegal resolution [" + getResolution() + "]");
			break;
		}
		return rc;
	}

	@Override
	public void reset() {
		// set values from superclass
		super.reset();

		// set my values
		setMinFrequency(MIN_FREQ);
		setMaxFrequency(MAX_FREQ);
		setMinLoss(MIN_LOSS);
		setMaxLoss(MAX_LOSS);
		setMinPhase(MIN_PHASE);
		setMaxPhase(MAX_PHASE);

		setDdsTicksPerMHz(DDS_TICKS);

		setAfterCommandDelay(AFTERCOMMANDDELAY);
		setBaudrate(BAURATE_SCAN);
		setBootloaderBaudrate(BAURATE_BOOT);

		setResolution(RESOLUTION_24BIT);
		setSampleRate(SAMPLE_RATE);

		setPeakSuppression(false);
		setFirmwareFileFilter("PCV36*.bin");
	}

	/**
	 * call super and restore phase/gain/temp/ifphase correction bootloader
	 * baudrate ddsticks min/max frequency min/max loss peak suppresion
	 */
	@Override
	public void restore(TypedProperties config, String prefix) {
		TraceHelper.entry(this, "restore", prefix);

		// set default values
		reset();

		// restore common values
		super.restore(config, prefix);

		// now get values for this device
		setBootloaderBaudrate(config.getInteger(prefix + "bootloaderBaudrate", getBootloaderBaudrate()));
		setSampleRate(config.getInteger(prefix + "sampleRate", getSampleRate()));
		setResolution(config.getInteger(prefix + "resolution", getResolution()));

		TraceHelper.exit(this, "restore");
	}

	public void setBootloaderBaudrate(int bootloaderBaudrate) {
		this.bootloaderBaudrate = bootloaderBaudrate;
	}

	@Override
	public void store(TypedProperties config, String prefix) {
		TraceHelper.entry(this, "store");

		super.store(config, prefix);

		config.putInteger(prefix + "bootloaderBaudrate", getBootloaderBaudrate());
		config.putInteger(prefix + "sampleRate", getSampleRate());
		config.putInteger(prefix + "resolution", getResolution());

		TraceHelper.exit(this, "store");
	}

	public int getResolution() {
		return resolution;
	}

	public void setResolution(int resolution) {
		this.resolution = resolution;
	}

	public int getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}

	@Override
	public int calculateRealBaudrate(int driverBaudrate) {
		return driverBaudrate / 300;
	}
}

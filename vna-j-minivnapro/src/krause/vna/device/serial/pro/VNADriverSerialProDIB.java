package krause.vna.device.serial.pro;

import krause.common.TypedProperties;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNAScanMode;
import krause.vna.device.VNADriverFactorySymbols;
import krause.vna.device.VNAGenericDriver;
import krause.vna.device.VNAScanModeParameter;
import krause.vna.device.VNASerialDeviceInfoBlock;
import krause.vna.gui.scale.VNAScaleSymbols.SCALE_TYPE;

public class VNADriverSerialProDIB extends VNASerialDeviceInfoBlock {

	public static final int FILTER_NONE = 0;
	public static final int FIRMWARE_ORG = 0;
	public static final int FIRMWARE_2_3 = 1;

	private int firmwareVersion = FIRMWARE_2_3;
	private boolean fixed6dBOnThru = true;
	private double attenOffsetI = 0;
	private double attenOffsetQ = 0;

	public static final float DDS_MHZ = 520;
	public static final int DEFAULT_TICKS = (int) ((1l << 32) / DDS_MHZ);

	private static final long MIN_FREQ = 100 * ONE_KHZ;
	private static final long MAX_FREQ = 200 * ONE_MHZ;
	private static final double MIN_LOSS = 10.0;
	private static final double MAX_LOSS = -110.0;

	public VNADriverSerialProDIB() {
		TraceHelper.entry(this, "VNADriverSerialProDIB");
		//
		reset();
		//
		clearScanModeParameters();
		addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_REFLECTION, true, true, true, false, SCALE_TYPE.SCALE_RETURNLOSS, SCALE_TYPE.SCALE_RETURNPHASE));
		addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_TRANSMISSION, true, false, false, true, SCALE_TYPE.SCALE_TRANSMISSIONLOSS, SCALE_TYPE.SCALE_TRANSMISSIONPHASE));
		//
		setShortName("miniVNA-pro");
		setLongName("mini radio solutions - miniVNA pro");
		setType(VNADriverFactorySymbols.TYPE_MININVNAPRO);
		TraceHelper.exit(this, "VNADriverSerialProDIB");
	}

	public int getFirmwareVersion() {
		return firmwareVersion;
	}

	public boolean isFixed6dBOnThru() {
		return fixed6dBOnThru;
	}

	@Override
	public void reset() {
		super.reset();

		setMinFrequency(MIN_FREQ);
		setMaxFrequency(MAX_FREQ);
		setMinLoss(MIN_LOSS);
		setMaxLoss(MAX_LOSS);
		setMinPhase(-180);
		setMaxPhase(180);
		setNumberOfSamples4Calibration(VNAGenericDriver.MINIMUM_SCAN_POINTS);
		setDdsTicksPerMHz(DEFAULT_TICKS);
		setAttenOffsetI(0);
		setAttenOffsetQ(0);
	}

	@Override
	public void restore(TypedProperties config, String prefix) {
		TraceHelper.entry(this, "restore", prefix);
		reset();
		super.restore(config, prefix);
		setFirmwareVersion(config.getInteger(prefix + "firmwareVersion", getFirmwareVersion()));
		setFixed6dBOnThru(config.getBoolean(prefix + "fixed6Db", isFixed6dBOnThru()));
		setAttenOffsetI(config.getDouble(prefix + "attenuatorOffsetI", getAttenOffsetI()));
		setAttenOffsetQ(config.getDouble(prefix + "attenuatorOffsetQ", getAttenOffsetQ()));
		TraceHelper.exit(this, "restore");
	}

	public void setFirmwareVersion(int firmwareVersion) {
		this.firmwareVersion = firmwareVersion;
	}

	public void setFixed6dBOnThru(boolean fixed6dBOnThru) {
		this.fixed6dBOnThru = fixed6dBOnThru;
	}

	@Override
	public void store(TypedProperties config, String prefix) {
		TraceHelper.entry(this, "store");
		super.store(config, prefix);
		config.putInteger(prefix + "firmwareVersion", getFirmwareVersion());
		config.putBoolean(prefix + "fixed6Db", isFixed6dBOnThru());
		config.putDouble(prefix + "attenuatorOffsetI", getAttenOffsetI());
		config.putDouble(prefix + "attenuatorOffsetQ", getAttenOffsetQ());
		TraceHelper.exit(this, "store");
	}

	public double getAttenOffsetI() {
		return attenOffsetI;
	}

	public void setAttenOffsetI(double attenOffsetI) {
		this.attenOffsetI = attenOffsetI;
	}

	public double getAttenOffsetQ() {
		return attenOffsetQ;
	}

	public void setAttenOffsetQ(double attenOffsetQ) {
		this.attenOffsetQ = attenOffsetQ;
	}

	@Override
	public int calculateRealBaudrate(int driverBaudrate) {
		return driverBaudrate / 30;
	}
}

package krause.vna.device.serial.proext;

import org.apache.commons.math3.complex.Complex;

import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNAScanMode;
import krause.vna.device.VNADriverFactorySymbols;
import krause.vna.device.VNAScanModeParameter;
import krause.vna.device.serial.pro.VNADriverSerialProDIB;
import krause.vna.gui.scale.VNAScaleSymbols.SCALE_TYPE;

public class VNADriverSerialProExtDIB extends VNADriverSerialProDIB {
	private final long MIN_FREQ = 10 * ONE_MHZ;
	private final long MAX_FREQ = 1500 * ONE_MHZ;

	private final double MIN_LOSS = 10.0;
	private final double MAX_LOSS = -70.0;

	private String scanCommandReflection;
	private String scanCommandTransmission;
	private int prescaler;

	public VNADriverSerialProExtDIB() {
		TraceHelper.entry(this, "VNADriverSerialProExtDIB");
		//
		reset();
		//
		clearScanModeParameters();
		addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_REFLECTION, true, true, true, false, SCALE_TYPE.SCALE_RETURNLOSS, SCALE_TYPE.SCALE_RETURNPHASE));
		addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_TRANSMISSION, true, false, false, true, SCALE_TYPE.SCALE_TRANSMISSIONLOSS, SCALE_TYPE.SCALE_TRANSMISSIONPHASE));
		//
		setShortName("miniVNA-pro-extender");
		setLongName("mini radio solutions - miniVNA pro extender");
		setType(VNADriverFactorySymbols.TYPE_MININVNAPRO_EXT);
		TraceHelper.exit(this, "VNADriverSerialProExtDIB");
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

	@Override
	public void reset() {
		super.reset();

		setMinFrequency(MIN_FREQ);
		setMaxFrequency(MAX_FREQ);
		setMinLoss(MIN_LOSS);
		setMaxLoss(MAX_LOSS);
		setMinPhase(-180);
		setMaxPhase(180);
		setNumberOfSamples4Calibration(10000);

		// special settings for the extender
		setPrescaler(10);
		setScanCommandTransmission("6");
		setScanCommandReflection("7");
		setAfterCommandDelay(100);

		setDdsTicksPerMHz(1000000);
		setReferenceResistance(new Complex(DEFAULT_REFERENCE_RESISTANCE_REAL, DEFAULT_REFERENCE_RESISTANCE_IMAG));
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
}

package krause.vna.device.serial.std;

import org.apache.commons.math3.complex.Complex;

import krause.common.TypedProperties;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNAScanMode;
import krause.vna.device.VNADriverFactorySymbols;
import krause.vna.device.VNAScanModeParameter;
import krause.vna.device.VNASerialDeviceInfoBlock;
import krause.vna.gui.scale.VNAScaleSymbols.SCALE_TYPE;

public class VNADriverSerialStdDIB extends VNASerialDeviceInfoBlock {

	public static final int DEFAULT_TICKS = 10737904;

	public VNADriverSerialStdDIB() {
		reset();
		//
		clearScanModeParameters();
		addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_REFLECTION, true, false, false, false, SCALE_TYPE.SCALE_RETURNLOSS, SCALE_TYPE.SCALE_RETURNPHASE));
		addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_TRANSMISSION, false, false, false, true, SCALE_TYPE.SCALE_TRANSMISSIONLOSS, SCALE_TYPE.SCALE_TRANSMISSIONPHASE));
		//
		setShortName("miniVNA");
		setLongName("mini radio solutions - miniVNA");
		//
		setType(VNADriverFactorySymbols.TYPE_MINIVNA);
	}

	@Override
	public void reset() {
		super.reset();

		setMinFrequency(100000);
		setMaxFrequency(180000000);
		setMinLoss(5);
		setMaxLoss(-60);
		setMinPhase(0);
		setMaxPhase(180);
		setNumberOfSamples4Calibration(3000);
		setDdsTicksPerMHz(DEFAULT_TICKS);
		setReferenceResistance(new Complex(DEFAULT_REFERENCE_RESISTANCE_REAL, DEFAULT_REFERENCE_RESISTANCE_IMAG));
	}

	@Override
	public void restore(TypedProperties config, String prefix) {
		final String methodName = "restore";
		TraceHelper.entry(this, methodName);

		reset();
		super.restore(config, prefix);
		setMaxLoss(config.getDouble(prefix + "maxLoss", getMaxLoss()));
		TraceHelper.exit(this, methodName);
	}

	@Override
	public void store(TypedProperties config, String prefix) {
		final String methodName = "store";
		TraceHelper.entry(this, methodName);
		super.store(config, prefix);
		config.putDouble(prefix + "maxLoss", getMaxLoss());
		TraceHelper.exit(this, methodName);
	}

	@Override
	public int calculateRealBaudrate(int driverBaudrate) {
		final String methodName = "calculateRealBaudrate";
		TraceHelper.entry(this, methodName, "in=%d", driverBaudrate);
		final int realBaudrate = driverBaudrate / 15;
		TraceHelper.exitWithRC(this, methodName, "out=%d", realBaudrate);
		return realBaudrate;
	}
}

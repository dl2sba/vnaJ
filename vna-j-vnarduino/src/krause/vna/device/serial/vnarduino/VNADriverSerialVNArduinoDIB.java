package krause.vna.device.serial.vnarduino;

import org.apache.commons.math3.complex.Complex;

import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNAScanMode;
import krause.vna.device.VNADriverFactorySymbols;
import krause.vna.device.VNAScanModeParameter;
import krause.vna.device.serial.std.VNADriverSerialStdDIB;
import krause.vna.gui.scale.VNAScaleSymbols.SCALE_TYPE;

public class VNADriverSerialVNArduinoDIB extends VNADriverSerialStdDIB {

	public static final int DEFAULT_TICKS = 23861284;

	public VNADriverSerialVNArduinoDIB() {
		reset();
		//
		clearScanModeParameters();
		addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_REFLECTION, true, true, false, false, SCALE_TYPE.SCALE_RETURNLOSS, SCALE_TYPE.SCALE_RETURNPHASE));
		addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_TRANSMISSION, false, false, false, true, SCALE_TYPE.SCALE_TRANSMISSIONLOSS, SCALE_TYPE.SCALE_TRANSMISSIONPHASE));
		//
		setShortName("VNArduino");
		setLongName("F4GOH VNArduino");
		//
		setType(VNADriverFactorySymbols.TYPE_VNARDUINO);
	}

	@Override
	public void reset() {
		super.reset();

		setMinFrequency(100 * ONE_KHZ);
		setMaxFrequency(65 * ONE_MHZ);
		setMinLoss(5);
		setMaxLoss(-70);
		setMinPhase(0);
		setMaxPhase(180);
		setNumberOfSamples4Calibration(3000);
		setDdsTicksPerMHz(DEFAULT_TICKS);

		setReferenceResistance(new Complex(DEFAULT_REFERENCE_RESISTANCE_REAL, DEFAULT_REFERENCE_RESISTANCE_IMAG));
	}

	@Override
	public int calculateRealBaudrate(int driverBaudrate) {
		final String methodName = "calculateRealBaudrate";
		TraceHelper.entry(this, methodName, "driverBaudrate=%d", driverBaudrate);
		final int realBaudrate = driverBaudrate / 30;
		TraceHelper.exitWithRC(this, methodName, "usedBaurate=%d", realBaudrate);
		return realBaudrate;
	}
}

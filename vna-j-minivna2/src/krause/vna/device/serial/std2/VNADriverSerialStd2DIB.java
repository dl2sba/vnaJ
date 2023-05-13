package krause.vna.device.serial.std2;

import org.apache.commons.math3.complex.Complex;

import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNAScanMode;
import krause.vna.device.VNADriverFactorySymbols;
import krause.vna.device.VNAScanModeParameter;
import krause.vna.device.VNASerialDeviceInfoBlock;
import krause.vna.gui.scale.VNAScaleSymbols.SCALE_TYPE;

public class VNADriverSerialStd2DIB extends VNASerialDeviceInfoBlock {

	public static final int DEFAULT_TICKS = 10737904;

	public VNADriverSerialStd2DIB() {
		reset();
		//
		clearScanModeParameters();
		addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_REFLECTION, true, true, false, false, SCALE_TYPE.SCALE_RETURNLOSS, SCALE_TYPE.SCALE_RETURNPHASE));
		addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_TRANSMISSION, false, false, false, true, SCALE_TYPE.SCALE_TRANSMISSIONLOSS, SCALE_TYPE.SCALE_TRANSMISSIONPHASE));
		//
		setShortName("miniVNA V2");
		setLongName("mini radio solutions - miniVNA V2");
		//
		setType(VNADriverFactorySymbols.TYPE_MINIVNA2);
	}

	@Override
	public void reset() {
		super.reset();

		setMinFrequency(100 * ONE_KHZ);
		setMaxFrequency(180 * ONE_MHZ);
		setMinLoss(5);
		setMaxLoss(-70);
		setMinPhase(-180);
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

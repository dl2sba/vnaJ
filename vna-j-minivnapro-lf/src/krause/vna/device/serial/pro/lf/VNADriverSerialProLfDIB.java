package krause.vna.device.serial.pro.lf;

import org.apache.commons.math3.complex.Complex;

import krause.vna.data.VNAScanMode;
import krause.vna.device.VNAScanModeParameter;
import krause.vna.device.serial.pro.VNADriverSerialProDIB;
import krause.vna.gui.scale.VNAScaleSymbols.SCALE_TYPE;

public class VNADriverSerialProLfDIB extends VNADriverSerialProDIB {

	@Override
	public void reset() {
		super.reset();

		setMinFrequency(15 * ONE_KHZ);
		setMaxFrequency(1 * ONE_MHZ);
		setMinLoss(10);
		setMaxLoss(-90);
		setMinPhase(-180);
		setMaxPhase(180);
		setNumberOfSamples4Calibration(2000);
		setDdsTicksPerMHz(DEFAULT_TICKS);
		setReferenceResistance(new Complex(DEFAULT_REFERENCE_RESISTANCE_REAL, DEFAULT_REFERENCE_RESISTANCE_IMAG));
	}

	public VNADriverSerialProLfDIB() {
		reset();
		//
		clearScanModeParameters();
		addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_REFLECTION, true, true, true, false, SCALE_TYPE.SCALE_RETURNLOSS, SCALE_TYPE.SCALE_RETURNPHASE));
		addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_TRANSMISSION, true, false, false, true, SCALE_TYPE.SCALE_TRANSMISSIONLOSS, SCALE_TYPE.SCALE_TRANSMISSIONPHASE));
		//
		setShortName("miniVNA-pro-LF");
		setLongName("mini radio solutions - miniVNA pro - LF version");
		setType("12");
	}
}

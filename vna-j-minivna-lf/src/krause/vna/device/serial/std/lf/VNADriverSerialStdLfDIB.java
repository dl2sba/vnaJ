package krause.vna.device.serial.std.lf;

import org.apache.commons.math3.complex.Complex;

import krause.vna.data.VNAScanMode;
import krause.vna.device.VNAScanModeParameter;
import krause.vna.device.serial.std.VNADriverSerialStdDIB;
import krause.vna.gui.scale.VNAScaleSymbols.SCALE_TYPE;

public final class VNADriverSerialStdLfDIB extends VNADriverSerialStdDIB {

	@Override
	public void reset() {
		super.reset();

		setMinFrequency(15000);
		setMaxFrequency(1000000);
		setMinLoss(5);
		setMaxLoss(-70);
		setMinPhase(0);
		setMaxPhase(180);
		setNumberOfSamples4Calibration(2000);
		setDdsTicksPerMHz(DEFAULT_TICKS);
		setReferenceResistance(new Complex(DEFAULT_REFERENCE_RESISTANCE_REAL, DEFAULT_REFERENCE_RESISTANCE_IMAG));
	}

	public VNADriverSerialStdLfDIB() {
		reset();
		//
		clearScanModeParameters();
		addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_REFLECTION, true, false, false, false, SCALE_TYPE.SCALE_RETURNLOSS, SCALE_TYPE.SCALE_RETURNPHASE));
		addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_TRANSMISSION, false, false, false, true, SCALE_TYPE.SCALE_TRANSMISSIONLOSS, SCALE_TYPE.SCALE_TRANSMISSIONPHASE));
		//
		setShortName("miniVNA-LF");
		setLongName("mini radio solutions - miniVNA LF-Version");
		setType("10");
	}
}

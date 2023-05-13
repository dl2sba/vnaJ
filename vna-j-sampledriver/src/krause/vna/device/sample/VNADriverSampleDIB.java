package krause.vna.device.sample;

import org.apache.commons.math3.complex.Complex;

import krause.common.TypedProperties;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNAScanMode;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.device.VNAScanModeParameter;
import krause.vna.gui.scale.VNAScaleSymbols.SCALE_TYPE;

public class VNADriverSampleDIB extends VNADeviceInfoBlock {

	public VNADriverSampleDIB() {
		reset();
		//
		clearScanModeParameters();
		addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_REFLECTION, true, false, false, false, SCALE_TYPE.SCALE_RETURNLOSS, SCALE_TYPE.SCALE_RETURNPHASE));
		addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_TRANSMISSION, false, true, false, false, SCALE_TYPE.SCALE_TRANSMISSIONLOSS, SCALE_TYPE.SCALE_TRANSMISSIONPHASE));
		addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_RSS1, false, false, true, false, SCALE_TYPE.SCALE_RETURNLOSS, SCALE_TYPE.SCALE_TRANSMISSIONLOSS));
		addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_TEST, true, true, true, true, SCALE_TYPE.SCALE_RETURNPHASE, SCALE_TYPE.SCALE_TRANSMISSIONPHASE));

		//
		setShortName("Sample");
		setLongName("vna/J sample driver");
		//
		setType("0");
	}

	@Override
	public void restore(TypedProperties config, String prefix) {
		TraceHelper.entry(this, "restore", prefix);
		TraceHelper.exit(this, "restore");
	}

	@Override
	public void store(TypedProperties config, String prefix) {
		TraceHelper.entry(this, "store");
		TraceHelper.exit(this, "store");
	}

	@Override
	public void reset() {
		super.reset();

		setMinFrequency(100);
		setMaxFrequency(9999999999l);
		setMinLoss(5);
		setMaxLoss(-100);
		setMinPhase(0);
		setMaxPhase(180);
		setNumberOfSamples4Calibration(2000);
		setDdsTicksPerMHz(8589934);
		setReferenceResistance(new Complex(DEFAULT_REFERENCE_RESISTANCE_REAL, DEFAULT_REFERENCE_RESISTANCE_IMAG));
	}

	@Override
	public int calculateRealBaudrate(int driverBaudrate) {
		return driverBaudrate;
	}
}

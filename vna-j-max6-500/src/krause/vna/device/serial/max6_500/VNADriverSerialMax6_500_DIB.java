package krause.vna.device.serial.max6_500;

import org.apache.commons.math3.complex.Complex;

import krause.vna.data.VNAScanMode;
import krause.vna.device.VNADriverFactorySymbols;
import krause.vna.device.VNAScanModeParameter;
import krause.vna.device.serial.max6.VNADriverSerialMax6DIB;
import krause.vna.device.serial.max6.VNADriverSerialMax6MathHelper;
import krause.vna.gui.scale.VNAScaleSymbols.SCALE_TYPE;

public class VNADriverSerialMax6_500_DIB extends VNADriverSerialMax6DIB {
	public final static float DDS_MHZ = 1250;
	public final static int DEFAULT_TICKS = (int) ((1l << 32) / DDS_MHZ);


	public VNADriverSerialMax6_500_DIB() {
		//
		reset();

		//
		clearScanModeParameters();
		addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_REFLECTION, true, false, false, false, SCALE_TYPE.SCALE_RETURNLOSS, SCALE_TYPE.SCALE_RETURNPHASE));
		addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_RSS1, true, false, false, true, SCALE_TYPE.SCALE_TRANSMISSIONLOSS, SCALE_TYPE.SCALE_TRANSMISSIONPHASE));

		//
		setShortName("MAX6-500MHz");
		setLongName("MAX6-500MHz - SP3SWJ");
		setType(VNADriverFactorySymbols.TYPE_MAX6_500);
	}

	@Override
	public void reset() {
		super.reset();
		
		setReferenceResistance(new Complex(DEFAULT_REFERENCE_RESISTANCE_REAL, DEFAULT_REFERENCE_RESISTANCE_IMAG));
		setDdsTicksPerMHz(DEFAULT_TICKS);

		setMinFrequency(100000);
		setMaxFrequency(500000000);
		setMinLoss(5);
		setMaxLoss(-80);
		setMinPhase(0);
		setMaxPhase(180);
		setNumberOfSamples4Calibration(2000);
		setLevelMax(20);
		setLevelMin(-80);

		setRss1Scale(VNADriverSerialMax6MathHelper.DEFAULT_RSS_SCALE);
		setRss1Offset(VNADriverSerialMax6MathHelper.DEFAULT_RSS_OFFSET);

		setRss2Scale(VNADriverSerialMax6MathHelper.DEFAULT_RSS_SCALE);
		setRss2Offset(VNADriverSerialMax6MathHelper.DEFAULT_RSS_OFFSET);

		setRss3Scale(VNADriverSerialMax6MathHelper.DEFAULT_RSS_SCALE);
		setRss3Offset(VNADriverSerialMax6MathHelper.DEFAULT_RSS_OFFSET);

		setReflectionOffset(VNADriverSerialMax6MathHelper.DEFAULT_REFLECTION_OFFSET);
		setReflectionScale(VNADriverSerialMax6MathHelper.DEFAULT_REFLECTION_SCALE);

		setTransmissionOffset(VNADriverSerialMax6MathHelper.DEFAULT_TRANSMISSION_OFFSET);
		setTransmissionScale(VNADriverSerialMax6MathHelper.DEFAULT_TRANSMISSION_SCALE);
	}
}

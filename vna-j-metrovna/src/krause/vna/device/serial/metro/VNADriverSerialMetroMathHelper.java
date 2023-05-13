package krause.vna.device.serial.metro;

import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNABaseSample;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrated.VNACalibrationContext;
import krause.vna.data.calibrated.VNACalibrationPoint;
import krause.vna.data.calibrationkit.VNACalibrationKit;
import krause.vna.device.IVNADriver;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.device.VNADriverMathBaseHelper;

public class VNADriverSerialMetroMathHelper extends VNADriverMathBaseHelper {

	// constants for conversion of ADC-values to numerical values
	private final static int DEFAULT_ADC_BITS = 1024;

	private final static double DEFAULT_PHASE_PER_BIT = 180.0 / (DEFAULT_ADC_BITS - 1);
	private final double RAD2DEG = 180.0 / Math.PI;

	public VNADriverSerialMetroMathHelper(IVNADriver driver) {
		super(driver);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seekrause.vna.device.IVNADriverMathHelper#applyFilter(krause.vna.data. VNASampleBlock)
	 */
	public void applyFilter(VNABaseSample[] samples) {
		TraceHelper.entry(this, "applyFilter");
		VNADeviceInfoBlock dib = getDriver().getDeviceInfoBlock();

		// do some filtering before the specific stuff
		super.applyPreFilter(samples, dib);

		// do some filtering before the specific stuff
		super.applyPostFilter(samples, dib);

		TraceHelper.exit(this, "applyFilter");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNADriverMathHelper#createCalibratedSample(krause. vna.data.VNAScanMode,
	 * krause.vna.data.VNABaseSample, krause.vna.data.calibrated.VNACalibrationPoint)
	 */
	public VNACalibratedSample createCalibratedSample(VNACalibrationContext context, VNABaseSample rhoMSample, VNACalibrationPoint calib) {
		if (context.getScanMode().isTransmissionMode()) {
			return createCalibratedSampleForTransmission(rhoMSample, calib);
		} else {
			return createCalibratedSampleForReflection(context, rhoMSample, calib);
		}
	}

	/**
	 * 
	 * @param dib
	 * @param raw
	 * @param calib
	 * @return
	 */
	private VNACalibratedSample createCalibratedSampleForReflection(VNACalibrationContext context, VNABaseSample raw, VNACalibrationPoint calib) {
		//
		double lossPerBit = -((VNADriverSerialMetroDIB) context.getDib()).getMaxReflectionLoss() / (DEFAULT_ADC_BITS - 1);

		// transform the binary adc values to real numbers
		// double rl = -((raw.getLoss() - calib.getLoss()) *
		// DEFAULT_RETURNLOSS_PER_BIT);
		double rl = -((raw.getLoss() - calib.getLoss()) * lossPerBit);
		double phase = raw.getAngle() * DEFAULT_PHASE_PER_BIT;
		double mag = Math.pow(10, rl / 20.0);
		double swr = Math.abs((1.0 + mag) / (1.0 - mag));

		// f = Cos((angle(i) * 0.1758) / 57.324)
		double f = Math.cos(phase / RAD2DEG);
		// g = Sin((angle(i) * 0.1758) / 57.324)
		double g = Math.sin(phase / RAD2DEG);
		// rr = f * mag
		double rr = f * mag;
		// ss = g * mag
		double ss = g * mag;
		// '******************************************* X calc
		// *************************************
		// x_imp = Abs(((2 * ss) / (((1 - rr) ^ 2) + (ss ^ 2))) * 50)
		double x_imp = Math.abs(((2 * ss) / (((1 - rr) * (1 - rr)) + (ss * ss))) * context.getDib().getReferenceResistance().getReal());
		// '******************************************* R calc
		// *************************************
		// r_imp = Abs(((1 - (rr ^ 2) - (ss ^ 2)) / (((1 - rr) ^ 2) + (ss ^ 2)))
		// * 50)
		double r_imp = Math.abs(((1 - (rr * rr) - (ss * ss)) / (((1 - rr) * (1 - rr)) + (ss * ss))) * context.getDib().getReferenceResistance().getReal());
		// '******************************************* Z calc
		// *************************************
		// z_imp = Sqr(((r_imp) ^ 2 + (x_imp) ^ 2))
		double z_imp = Math.sqrt(((r_imp * r_imp) + (x_imp * x_imp)));

		VNACalibratedSample rc = new VNACalibratedSample();
		rc.setFrequency(raw.getFrequency());
		rc.setMag(mag);
		rc.setReflectionLoss(rl);
		rc.setSWR(swr);
		rc.setReflectionPhase(phase);
		rc.setR(r_imp);
		rc.setX(x_imp);
		rc.setZ(z_imp);

		return rc;
	}

	/**
	 * 
	 * @param raw
	 * @param calib
	 * @return
	 */
	private VNACalibratedSample createCalibratedSampleForTransmission(VNABaseSample raw, VNACalibrationPoint calib) {

		double lossPerBit = -((VNADriverSerialMetroDIB) getDriver().getDeviceInfoBlock()).getMaxTransmissionLoss() / (DEFAULT_ADC_BITS - 1);

		VNACalibratedSample rc = new VNACalibratedSample();
		// transform the binary adc values to real numbers
		rc.setTransmissionLoss(-((raw.getLoss() - calib.getLoss()) * lossPerBit));
		rc.setTransmissionPhase(raw.getAngle() * DEFAULT_PHASE_PER_BIT);
		rc.setFrequency(raw.getFrequency());
		return rc;
	}

	@Override
	public VNACalibrationContext createCalibrationContextForCalibratedSamples(VNACalibrationBlock calBlock) {
		TraceHelper.entry(this, "createCalibrationContextForCalibratedSamples");
		// create calibration context
		final VNADriverSerialMetroDIB dib = (VNADriverSerialMetroDIB) calBlock.getMathHelper().getDriver().getDeviceInfoBlock();
		final VNACalibrationContext context = new VNACalibrationContext();
		//
		context.setDib(dib);
		context.setCalibrationBlock(calBlock);
		context.setScanMode(calBlock.getScanMode());

		TraceHelper.exit(this, "createCalibrationContextForCalibratedSamples");
		return context;
	}

	@Override
	public VNACalibrationContext createCalibrationContextForCalibrationPoints(final VNACalibrationBlock calBlock, final VNACalibrationKit kit) {
		TraceHelper.entry(this, "createCalibrationContextForCalibrationPoints");

		// create calibration context
		final VNADriverSerialMetroDIB dib = (VNADriverSerialMetroDIB) calBlock.getMathHelper().getDriver().getDeviceInfoBlock();
		final VNACalibrationContext context = new VNACalibrationContext();
		//
		context.setDib(dib);
		context.setCalibrationBlock(calBlock);
		context.setScanMode(calBlock.getScanMode());

		TraceHelper.exit(this, "createCalibrationContextForCalibrationPoints");
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNADriverMathHelper#createCalibrationPoint(krause. vna.data.VNAScanMode,
	 * krause.vna.data.VNABaseSample, krause.vna.data.VNABaseSample, krause.vna.data.VNABaseSample, krause.vna.data.VNABaseSample)
	 */
	public VNACalibrationPoint createCalibrationPoint(VNACalibrationContext context, VNABaseSample numOpen, VNABaseSample numShort, VNABaseSample numLoad, VNABaseSample numLoop) {
		if (context.getScanMode().isTransmissionMode()) {
			return createCalibrationPointForTransmission(numLoop);
		} else {
			return createCalibrationPointForReflection(numOpen);
		}
	}

	/**
	 * 
	 * @param numOpen
	 * @return
	 */
	private VNACalibrationPoint createCalibrationPointForReflection(VNABaseSample numOpen) {
		VNACalibrationPoint rc = null;
		rc = new VNACalibrationPoint();
		rc.setFrequency(numOpen.getFrequency());
		// only calibration with LOSS
		rc.setLoss(numOpen.getLoss());
		return rc;
	}

	/**
	 * 
	 * @param numLoop
	 * @return
	 */
	private VNACalibrationPoint createCalibrationPointForTransmission(VNABaseSample numLoop) {
		VNACalibrationPoint rc = null;
		rc = new VNACalibrationPoint();
		rc.setFrequency(numLoop.getFrequency());
		// only calibration with LOSS
		rc.setLoss(numLoop.getLoss());
		return rc;
	}
}

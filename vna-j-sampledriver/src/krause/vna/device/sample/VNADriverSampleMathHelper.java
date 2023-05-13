package krause.vna.device.sample;

import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNABaseSample;
import krause.vna.data.VNAScanMode;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrated.VNACalibrationContext;
import krause.vna.data.calibrated.VNACalibrationPoint;
import krause.vna.data.calibrationkit.VNACalibrationKit;
import krause.vna.device.IVNADriver;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.device.VNADriverMathBaseHelper;

public class VNADriverSampleMathHelper extends VNADriverMathBaseHelper {

	public VNADriverSampleMathHelper(IVNADriver driver) {
		super(driver);
	}

	// constants for conversion of ADC-values to numerical values
	private static final int DEFAULT_ADC_BITS = 1024;
	private static final double DEFAULT_PHASE_PER_BIT = 180.0 / (DEFAULT_ADC_BITS - 1);
	private static final double DEFAULT_RETURNLOSS_PER_BIT = 60.0 / (DEFAULT_ADC_BITS - 1);
	// private final double DEG2RAD = 1.74532925199433E-02; // PI() / 180
	private static final double RAD2DEG = 180.0 / Math.PI;

	/**
	 * 
	 * @param raw
	 * @param calib
	 * @return
	 */
	private VNACalibratedSample createDummyCalibratedSample(VNABaseSample raw, VNACalibrationPoint calib) {
		// transform the binary adc values to real numbers
		double rl = (raw.getLoss() - calib.getLoss()) * DEFAULT_RETURNLOSS_PER_BIT;
		double phase = raw.getAngle() * DEFAULT_PHASE_PER_BIT;
		double mag = Math.pow(10, -rl / 20.0);
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
		double xImp = Math.abs(((2 * ss) / (((1 - rr) * (1 - rr)) + (ss * ss))) * 50.0);
		// '******************************************* R calc
		// *************************************
		// r_imp = Abs(((1 - (rr ^ 2) - (ss ^ 2)) / (((1 - rr) ^ 2) + (ss ^ 2)))
		// * 50)
		double rImp = Math.abs(((1 - (rr * rr) - (ss * ss)) / (((1 - rr) * (1 - rr)) + (ss * ss))) * 50);
		// '******************************************* Z calc
		// *************************************
		// z_imp = Sqr(((r_imp) ^ 2 + (x_imp) ^ 2))
		double zImp = Math.sqrt(((rImp * rImp) + (xImp * xImp)));

		VNACalibratedSample rc = new VNACalibratedSample();
		rc.setFrequency(raw.getFrequency());
		rc.setMag(mag);
		rc.setReflectionLoss(rl);
		rc.setTransmissionLoss(-rl);
		rc.setSWR(swr);
		rc.setReflectionPhase(phase);
		rc.setR(rImp);
		rc.setX(xImp);
		rc.setZ(zImp);
		rc.setRelativeSignalStrength1(raw.getRss1() * DEFAULT_RETURNLOSS_PER_BIT);
		rc.setRelativeSignalStrength2(raw.getRss2() * DEFAULT_RETURNLOSS_PER_BIT);
		rc.setRelativeSignalStrength3(raw.getRss3() * DEFAULT_RETURNLOSS_PER_BIT);

		return rc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * krause.vna.device.IVNADriverMathHelper#createCalibratedSample(krause.
	 * vna.device.VNADeviceInfoBlock, krause.vna.data.VNAScanMode,
	 * krause.vna.data.VNABaseSample,
	 * krause.vna.data.calibrated.VNACalibrationPoint)
	 */
	public VNACalibratedSample createCalibratedSample(VNACalibrationContext context, VNABaseSample sample, VNACalibrationPoint calib) {
		VNACalibratedSample rc = null;
		rc = createDummyCalibratedSample(sample, calib);
		return rc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * krause.vna.device.IVNADriverMathHelper#createCalibrationPoint(krause.
	 * vna.device.VNADeviceInfoBlock, krause.vna.data.VNAScanMode,
	 * krause.vna.data.VNABaseSample, krause.vna.data.VNABaseSample,
	 * krause.vna.data.VNABaseSample, krause.vna.data.VNABaseSample)
	 */
	public VNACalibrationPoint createCalibrationPoint(VNACalibrationContext context, VNABaseSample numOpen, VNABaseSample numShort, VNABaseSample numLoad, VNABaseSample numLoop) {
		final VNACalibrationPoint rc = new VNACalibrationPoint();
		final VNAScanMode mode = context.getScanMode();

		if (mode.isTransmissionMode()) {
			rc.setFrequency(numShort.getFrequency());
			rc.setLoss(numShort.getLoss());
			rc.setRss1(numShort.getRss1());
			rc.setRss2(numShort.getRss2());
			rc.setRss3(numShort.getRss3());
		} else if (mode.isReflectionMode()) {
			rc.setFrequency(numOpen.getFrequency());
			rc.setLoss(numOpen.getLoss());
			rc.setRss1(numOpen.getRss1());
			rc.setRss2(numOpen.getRss2());
			rc.setRss3(numOpen.getRss3());
		} else if (mode.isRss1Mode()) {
			rc.setFrequency(numLoad.getFrequency());
			rc.setLoss(numLoad.getLoss());
			rc.setRss1(numLoad.getRss1());
			rc.setRss2(numLoad.getRss2());
			rc.setRss3(numLoad.getRss3());
		} else if (mode.isRss2Mode()) {
			rc.setFrequency(numLoop.getFrequency());
			rc.setLoss(numLoop.getLoss());
			rc.setRss1(numLoop.getRss1());
			rc.setRss2(numLoop.getRss2());
			rc.setRss3(numLoop.getRss3());
		}
		return rc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seekrause.vna.device.IVNADriverMathHelper#applyFilter(krause.vna.data.
	 * VNASampleBlock)
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

	@Override
	public VNACalibrationContext createCalibrationContextForCalibrationPoints(final VNACalibrationBlock calBlock, final VNACalibrationKit kit) {
		final String methodName = "createCalibrationContextForCalibrationPoints";
		TraceHelper.entry(this, methodName);

		
		// create calibration context
		final VNADriverSampleDIB dib = (VNADriverSampleDIB) calBlock.getMathHelper().getDriver().getDeviceInfoBlock();
		final VNACalibrationContext context = new VNACalibrationContext();
		//
		context.setDib(dib);
		context.setCalibrationBlock(calBlock);
		context.setScanMode(calBlock.getScanMode());

TraceHelper.exit(this, methodName);		return context;
	}

	@Override
	public VNACalibrationContext createCalibrationContextForCalibratedSamples(VNACalibrationBlock calBlock) {
		TraceHelper.entry(this, "createCalibrationContextForCalibratedSamples");
		// create calibration context
		final VNADriverSampleDIB dib = (VNADriverSampleDIB) calBlock.getMathHelper().getDriver().getDeviceInfoBlock();
		final VNACalibrationContext context = new VNACalibrationContext();
		//
		context.setDib(dib);
		context.setCalibrationBlock(calBlock);
		context.setScanMode(calBlock.getScanMode());

		TraceHelper.exit(this, "createCalibrationContextForCalibratedSamples");
		return context;
	}
}

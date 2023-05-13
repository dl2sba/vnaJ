package krause.vna.device.serial.max6;

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

public class VNADriverSerialMax6MathHelper extends VNADriverMathBaseHelper {

	// constants for conversion of ADC-values to numerical values
	public final static int DEFAULT_ADC_BITS = 1024;

	public final static double DEFAULT_PHASE_PER_BIT = 180.0 / (DEFAULT_ADC_BITS - 1);

	public final static double DEFAULT_TRANSMISSION_SCALE = 0.145;
	public static final double DEFAULT_TRANSMISSION_OFFSET = 0;

	public final static double DEFAULT_REFLECTION_OFFSET = 0;
	public static final double DEFAULT_REFLECTION_SCALE = 0.05865103;

	public final static double DEFAULT_RSS_OFFSET = 80;
	public final static double DEFAULT_RSS_SCALE = 0.145;

	public final double RAD2DEG = 180.0 / Math.PI;
	public final double R_50 = 50.0;

	public VNADriverSerialMax6MathHelper(IVNADriver driver) {
		super(driver);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNADriverMathHelper#createCalibratedSample(krause. vna.data.VNAScanMode,
	 * krause.vna.data.VNABaseSample, krause.vna.data.calibrated.VNACalibrationPoint)
	 */
	public VNACalibratedSample createCalibratedSample(VNACalibrationContext context, VNABaseSample rawSample, VNACalibrationPoint calibPoint) {
		final VNADriverSerialMax6DIB myDib = (VNADriverSerialMax6DIB) context.getDib();

		if (context.getScanMode().isRss1Mode()) {
			return createCalibratedSampleForRss1(myDib, rawSample, calibPoint);
		} else if (context.getScanMode().isRss3Mode()) {
			return createCalibratedSampleForRss3(myDib, rawSample, calibPoint);
		} else if (context.getScanMode().isReflectionMode()) {
			return createCalibratedSampleForReflectionMode0(myDib, rawSample, calibPoint);
		} else {
			return null;
		}
	}

	/**
	 * We use the loss from the calibration point. We use the reflectionScale and reflectionOffset from DIB
	 * 
	 * @param raw
	 * @param calib
	 * @return
	 */
	private VNACalibratedSample createCalibratedSampleForReflectionMode0(VNADriverSerialMax6DIB dib, VNABaseSample raw, VNACalibrationPoint calib) {
		// transform the binary adc values to real numbers
		double rl = -((raw.getLoss() - calib.getLoss()) * dib.getReflectionScale() - dib.getReflectionOffset());
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
		double x_imp = Math.abs(((2 * ss) / (((1 - rr) * (1 - rr)) + (ss * ss))) * dib.getReferenceResistance().getReal());
		// '******************************************* R calc
		// *************************************
		// r_imp = Abs(((1 - (rr ^ 2) - (ss ^ 2)) / (((1 - rr) ^ 2) + (ss ^ 2)))
		// * 50)
		double r_imp = Math.abs(((1 - (rr * rr) - (ss * ss)) / (((1 - rr) * (1 - rr)) + (ss * ss))) * dib.getReferenceResistance().getReal());
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
	private VNACalibratedSample createCalibratedSampleForRss1(VNADriverSerialMax6DIB dib, VNABaseSample raw, VNACalibrationPoint calib) {
		VNACalibratedSample rc;
		// first we do the reflection calculation
		rc = createCalibratedSampleForReflectionMode2(dib, raw, calib);

		// then the level stuff
		// rc.setTransmissionLoss((calib.getRss1() - raw.getRss1()) *
		// dib.getRss1Scale() - dib.getRss1Offset());
		rc.setTransmissionLoss(-((calib.getRss1() - raw.getRss1()) * dib.getTransmissionScale()));
		//
		rc.setRelativeSignalStrength1(raw.getRss1() * dib.getRss1Scale() - dib.getRss1Offset());
		return rc;
	}

	/**
	 * 
	 * @param dib
	 * @param raw
	 * @param calib
	 * @return
	 */
	private VNACalibratedSample createCalibratedSampleForRss3(VNADriverSerialMax6DIB dib, VNABaseSample raw, VNACalibrationPoint calib) {
		VNACalibratedSample rc;
		// first we process reflection and RSS1 stuff
		rc = createCalibratedSampleForRss1(dib, raw, calib);

		// then the rest of the RSS
		rc.setRelativeSignalStrength2((raw.getRss2() - calib.getRss2()) * dib.getRss2Scale() - dib.getRss2Offset());
		rc.setRelativeSignalStrength3((raw.getRss3() - calib.getRss3()) * dib.getRss3Scale() - dib.getRss3Offset());
		return rc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNADriverMathHelper#createCalibrationPoint(krause. vna.data.VNAScanMode,
	 * krause.vna.data.VNABaseSample, krause.vna.data.VNABaseSample, krause.vna.data.VNABaseSample)
	 */
	public VNACalibrationPoint createCalibrationPoint(VNACalibrationContext context, VNABaseSample pOpen, VNABaseSample pShort, VNABaseSample pLoad, VNABaseSample pLoop) {
		if (context.getScanMode().isRss1Mode()) {
			return createCalibrationPointForRss1(pOpen, pLoop);
		} else if (context.getScanMode().isRss3Mode()) {
			return createCalibrationPointForRss3(pOpen, pLoop);
		} else if (context.getScanMode().isReflectionMode()) {
			return createCalibrationPointForReflection(pOpen);
		} else {
			return null;
		}
	}

	/**
	 * We only use the loss part of the OPEN scan for calibration. Phase part cannot be calibrated
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
	 * @param numOpen
	 * @param numLoop
	 * @param numLoad
	 * @return
	 */
	private VNACalibrationPoint createCalibrationPointForRss1(VNABaseSample numOpen, VNABaseSample numLoop) {
		VNACalibrationPoint rc = null;
		rc = new VNACalibrationPoint();

		rc.setFrequency(numLoop.getFrequency());
		rc.setLoss(numOpen.getLoss());
		rc.setRss1(numLoop.getRss1());

		return rc;
	}

	/**
	 * 
	 * @param numOpen
	 * @param numLoop
	 * @param numLoad
	 * @return
	 */
	private VNACalibrationPoint createCalibrationPointForRss3(VNABaseSample numOpen, VNABaseSample numLoop) {
		VNACalibrationPoint rc = null;
		rc = new VNACalibrationPoint();

		rc.setFrequency(numLoop.getFrequency());
		rc.setLoss(numLoop.getLoss());
		rc.setRss1(numLoop.getRss1());
		rc.setRss2(numLoop.getRss2());
		rc.setRss3(numLoop.getRss3());

		return rc;
	}

	/**
	 * We use the loss from the calibration point. We use the reflectionScale and reflectionOffset from DIB
	 * 
	 * @param raw
	 * @param calib
	 * @return
	 */
	private VNACalibratedSample createCalibratedSampleForReflectionMode2(VNADriverSerialMax6DIB dib, VNABaseSample raw, VNACalibrationPoint calib) {
		// transform the binary adc values to real numbers
		double rl = -(-(calib.getLoss() - raw.getLoss()) * dib.getReflectionScale() - dib.getReflectionOffset());
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
		double x_imp = Math.abs(((2 * ss) / (((1 - rr) * (1 - rr)) + (ss * ss))) * 50.0);
		// '******************************************* R calc
		// *************************************
		// r_imp = Abs(((1 - (rr ^ 2) - (ss ^ 2)) / (((1 - rr) ^ 2) + (ss ^ 2)))
		// * 50)
		double r_imp = Math.abs(((1 - (rr * rr) - (ss * ss)) / (((1 - rr) * (1 - rr)) + (ss * ss))) * 50);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @seekrause.vna.device.IVNADriverMathHelper#applyFilter(krause.vna.data. VNASampleBlock)
	 */
	public void applyFilter(VNABaseSample[] samples) {
		final String methodName = "applyFilter";
		TraceHelper.entry(this, methodName);

		VNADeviceInfoBlock dib = getDriver().getDeviceInfoBlock();

		// do some filtering before the specific stuff
		super.applyPreFilter(samples, dib);

		// do some filtering before the specific stuff
		super.applyPostFilter(samples, dib);

		TraceHelper.exit(this, methodName);
	}

	@Override
	public VNACalibrationContext createCalibrationContextForCalibratedSamples(VNACalibrationBlock calBlock) {
		final String methodName = "createCalibrationContextForCalibratedSamples";
		TraceHelper.entry(this, methodName);
		// create calibration context
		final VNADriverSerialMax6DIB dib = (VNADriverSerialMax6DIB) calBlock.getMathHelper().getDriver().getDeviceInfoBlock();
		final VNACalibrationContext context = new VNACalibrationContext();
		//
		context.setDib(dib);
		context.setCalibrationBlock(calBlock);
		context.setScanMode(calBlock.getScanMode());

		TraceHelper.exit(this, methodName);
		return context;
	}

	@Override
	public VNACalibrationContext createCalibrationContextForCalibrationPoints(VNACalibrationBlock calBlock, final VNACalibrationKit kit) {
		final String methodName = "createCalibrationContextForCalibrationPoints";
		TraceHelper.entry(this, methodName);

		// create calibration context
		final VNADriverSerialMax6DIB dib = (VNADriverSerialMax6DIB) calBlock.getMathHelper().getDriver().getDeviceInfoBlock();
		final VNACalibrationContext context = new VNACalibrationContext();
		//
		context.setDib(dib);
		context.setCalibrationBlock(calBlock);
		context.setScanMode(calBlock.getScanMode());

		TraceHelper.exit(this, methodName);
		return context;
	}
}

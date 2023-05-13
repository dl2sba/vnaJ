package krause.vna.device.serial.tiny2;

import org.apache.commons.math3.complex.Complex;

import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNABaseSample;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrated.VNACalibrationContext;
import krause.vna.data.calibrated.VNACalibrationContextTiny;
import krause.vna.data.calibrated.VNACalibrationPoint;
import krause.vna.data.calibrationkit.VNACalibrationKit;
import krause.vna.device.IVNADriver;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.device.VNADriverMathBaseHelper;

public class VNADriverSerialTiny2MathHelper extends VNADriverMathBaseHelper {
	private static final double SMALL_PHASE = 0.1;

	public VNADriverSerialTiny2MathHelper(IVNADriver driver) {
		super(driver);
		TraceHelper.entry(this, "VNADriverSerialTiny2MathHelper");

		TraceHelper.exit(this, "VNADriverSerialTiny2MathHelper");
	}

	/**
	 * calculate the corrected sample based on the given conversion temperature, reference temperature and the parameters from the
	 * DIB
	 * 
	 * @param context
	 * @param sample
	 * @param conversionTemp
	 * @return
	 */
	private VNABaseSample calculateCorrectedBaseSample(final VNACalibrationContextTiny context, final VNABaseSample sample, final double conversionTemp) {
		VNABaseSample rc = new VNABaseSample(sample);

		// calculate difference between calibration temperature from the context
		// and the
		// given conversionTemp
		// for calibration data this is the calibration temperature - so no
		// correction
		// for measurement data this is the measurement temperature - so
		// correction
		// relative to calibration temperature
		final double deltaTemp = context.getCalibrationTemperature() - conversionTemp;

		// calculate the factor based on the factor from the configuration
		// dialog and
		// the delta temperature
		final double tempCorrectionFactor = 1.0 - (deltaTemp * context.getTempCorrection());

		// has this analyzer a reference channel?
		if (context.getDib().hasReferenceChannel()) {
			// yes
			// calculate IQ data for measurement data
			double real = (sample.getP1() - sample.getP2()) / 2.0;
			double imag = (sample.getP3() - sample.getP4()) / 2.0;

			// apply phase and gain correction to measurement data
			imag = (imag * context.getGainCorrection() - real * context.getSineCorrection()) / context.getCosineCorrection();

			// calculate IQ data for reference data
			double realRef = (sample.getP1Ref() - sample.getP2Ref()) / 2.0;
			double imagRef = (sample.getP3Ref() - sample.getP4Ref()) / 2.0;

			// apply temperature, phase and gain correction to reference data
			realRef *= tempCorrectionFactor;
			imagRef *= tempCorrectionFactor;
			imagRef = (imagRef * context.getGainCorrection() - realRef * context.getSineCorrection()) / context.getCosineCorrection();

			// now do a complex division
			final Complex dataChannel = new Complex(real, imag);
			final Complex referenceChannel = new Complex(realRef, imagRef);
			final Complex correctedChannel = dataChannel.divide(referenceChannel);

			// write corrected data
			rc.setLoss(correctedChannel.getReal());
			rc.setAngle(correctedChannel.getImaginary());

		} else {
			// no
			// calculate IQ data for measurement data
			double real = (sample.getP1() - sample.getP2()) / 2.0;
			double imag = (sample.getP3() - sample.getP4()) / 2.0;

			// apply temperature, phase and gain correction to measurement data
			real *= tempCorrectionFactor;
			imag *= tempCorrectionFactor;
			imag = (imag * context.getGainCorrection() - real * context.getSineCorrection()) / context.getCosineCorrection();

			// so use only corrected measurement data as IQ data
			rc.setLoss(real);
			rc.setAngle(imag);
		}
		return rc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNADriverMathHelper#applyFilter(krause.vna.data. VNASampleBlock)
	 */
	public void applyFilter(VNABaseSample[] samples) {

		TraceHelper.entry(this, "applyFilter");

		VNADeviceInfoBlock dib = getDriver().getDeviceInfoBlock();

		// do some filtering before the specific stuff
		super.applyPreFilter(samples, dib);

		// do some filtering after the specific stuff
		super.applyPostFilter(samples, dib);

		TraceHelper.exit(this, "applyFilter");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNADriverMathHelper#createCalibratedSample(krause. vna.data.VNAScanMode,
	 * krause.vna.data.VNABaseSample, krause.vna.data.calibrated.VNACalibrationPoint)
	 */
	public VNACalibratedSample createCalibratedSample(VNACalibrationContext pContext, VNABaseSample rawSample, VNACalibrationPoint calibPoint) {
		final VNACalibrationContextTiny context = (VNACalibrationContextTiny) pContext;
		final VNABaseSample correctedRawSample = calculateCorrectedBaseSample(context, rawSample, context.getConversionTemperature());

		if (context.getScanMode().isTransmissionMode()) {
			return createCalibratedSampleForTransmission(context, correctedRawSample, calibPoint);
		} else {
			return createCalibratedSampleForReflection(context, correctedRawSample, calibPoint);
		}
	}

	/**
	 * 
	 * @param context
	 * @param rawSample
	 * @param calib
	 * @param ifPhaseCorrection
	 * @param ifPhaseCorrection
	 * @return
	 */
	private VNACalibratedSample createCalibratedSampleForReflection(VNACalibrationContext context, VNABaseSample rawSample, VNACalibrationPoint calib) {
		final VNADeviceInfoBlock dib = context.getDib();
		final Complex rhoM = rawSample.asComplex();

		// calculate rhoA
		// formula: RhoA = (RhoM - e00) / (RhoM*e11 - DeltaE)
		final Complex rho = rhoM.subtract(calib.getE00()).divide(rhoM.multiply(calib.getE11()).subtract(calib.getDeltaE()));

		// calculate MAG
		double mag = rho.abs();
		if (mag > 1.0) {
			mag = 1.0;
		}

		// calculate SWR
		double swr = (1.0 + mag) / (1 - mag);

		// calculate RL
		double reflectionLoss = 20.0 * Math.log10(mag);
		reflectionLoss = Math.max(reflectionLoss, dib.getMaxLoss());

		// calculate PHASE RL
		double reflectionPhase = Math.toDegrees(rho.getArgument());

		if ((reflectionPhase >= 0.0) && (reflectionPhase < SMALL_PHASE)) {
			reflectionPhase = SMALL_PHASE;
		} else if ((reflectionPhase > -SMALL_PHASE) && (reflectionPhase < 0.0)) {
			reflectionPhase = -SMALL_PHASE;
		}

		// folding variant
		if (reflectionPhase > VNADriverSerialTiny2DIB.MAX_PHASE) {
			reflectionPhase += 2 * VNADriverSerialTiny2DIB.MIN_PHASE;
		} else if (reflectionPhase < VNADriverSerialTiny2DIB.MIN_PHASE) {
			reflectionPhase += 2 * VNADriverSerialTiny2DIB.MAX_PHASE;
		}

		// f = Cos((angle(i) * 0.1758) / 57.324)
		final double f = Math.cos(Math.toRadians(reflectionPhase));
		// g = Sin((angle(i) * 0.1758) / 57.324)
		final double g = Math.sin(Math.toRadians(reflectionPhase));
		// rr = f * mag
		final double rr = f * mag;
		// ss = g * mag
		final double ss = g * mag;

		// ******************************************* X calc
		// x_imp = Abs(((2 * ss) / (((1 - rr) ^ 2) + (ss ^ 2))) * 50)
		// final double x_imp = Math.abs(((2 * ss) / (((1 - rr) * (1 - rr)) +
		// (ss * ss))) * context.getDib().getReferenceResistance().getReal())
		final double x_imp = ((2 * ss) / (((1 - rr) * (1 - rr)) + (ss * ss))) * context.getDib().getReferenceResistance().getReal();

		// ******************************************* R calc
		// r_imp = Abs(((1 - (rr ^ 2) - (ss ^ 2)) / (((1 - rr) ^ 2) + (ss ^ 2)))
		// * 50)
		// final double r_imp = Math.abs(((1 - (rr * rr) - (ss * ss)) / (((1 -
		// rr) * (1 - rr)) + (ss * ss))) *
		// context.getDib().getReferenceResistance().getReal())
		double r_imp = ((1 - (rr * rr) - (ss * ss)) / (((1 - rr) * (1 - rr)) + (ss * ss))) * context.getDib().getReferenceResistance().getReal();
		if (r_imp < 0) {
			r_imp = 0;
		}

		// ******************************************* Z calc
		// z_imp = Sqr(((r_imp) ^ 2 + (x_imp) ^ 2))
		final double z_imp = Math.sqrt(((r_imp * r_imp) + (x_imp * x_imp)));

		//
		final VNACalibratedSample rc = new VNACalibratedSample();
		rc.setFrequency(rawSample.getFrequency());
		rc.setRHO(rho);
		rc.setMag(mag);
		rc.setReflectionLoss(reflectionLoss);
		rc.setReflectionPhase(reflectionPhase);
		rc.setSWR(swr);
		rc.setR(r_imp);
		rc.setX(x_imp);
		rc.setZ(z_imp);

		return rc;
	}

	/**
	 * Result E00 as mThru
	 * 
	 * Result E11 as mOpen
	 * 
	 * Result DeltaE as mThru - mOpen
	 * 
	 * 
	 * @param context
	 * @param rawSample
	 * @param calPoint
	 * @param ifPhaseCorrection
	 * @return
	 */

	private VNACalibratedSample createCalibratedSampleForTransmission(VNACalibrationContext context, VNABaseSample rawSample, VNACalibrationPoint calPoint) {
		final VNADeviceInfoBlock dib = context.getDib();
		final VNACalibratedSample rc = new VNACalibratedSample();
		//
		rc.setFrequency(rawSample.getFrequency());

		//
		final Complex mDUT = new Complex((rawSample.getAngle() - 512) * 0.003, (rawSample.getLoss() - 512) * 0.003);
		//
		// GDUT=(M(dut) - M(open) / M(Thru)-M(open)
		final Complex gDUT = mDUT.subtract(calPoint.getE11()).divide(calPoint.getDeltaE());

		// MAG = =IMABS(gDUT)
		rc.setMag(gDUT.abs());

		// calculate RL
		double tl = Math.max(20.0 * Math.log10(rc.getMag()), dib.getMaxLoss());

		// calculate PHASE IMARGUMENT(GDUT)*57,3
		double transmissionPhase = Math.toDegrees(-gDUT.getArgument());

		// correct the phase to the min/max range of the analyzer
		if (transmissionPhase > VNADriverSerialTiny2DIB.MAX_PHASE) {
			transmissionPhase -= VNADriverSerialTiny2DIB.MAX_PHASE;
		} else if (transmissionPhase < VNADriverSerialTiny2DIB.MIN_PHASE) {
			transmissionPhase += VNADriverSerialTiny2DIB.MIN_PHASE;
		}

		rc.setTransmissionPhase(transmissionPhase);

		double mag1 = Math.pow(10, tl / 20.0);
		double mag = Math.pow(10, -tl / 20.0);
		double dRef = 2 * context.getDib().getReferenceResistance().getReal();
		double rs = ((dRef * mag) / Math.sqrt(1.0 + Math.pow(Math.tan(Math.toRadians(transmissionPhase)), 2))) - dRef;
		double xs = -(rs + 100.0) * Math.tan(Math.toRadians(transmissionPhase));
		double z = Math.sqrt(rs * rs + xs * xs);

		rc.setTransmissionLoss(tl);
		rc.setTransmissionPhase(transmissionPhase);
		rc.setMag(mag1);
		rc.setR(rs);
		rc.setX(xs);
		rc.setZ(z);
		rc.setFrequency(rawSample.getFrequency());

		return rc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNADriverMathHelper# createCalibrationContextForCalibratedSamples(krause.vna.data.calibrated.
	 * VNACalibrationBlock)
	 */
	public VNACalibrationContext createCalibrationContextForCalibratedSamples(VNACalibrationBlock calBlock) {
		TraceHelper.entry(this, "createCalibrationContextForCalibratedSamples");

		// create calibration context
		final VNADriverSerialTiny2DIB dib = (VNADriverSerialTiny2DIB) calBlock.getMathHelper().getDriver().getDeviceInfoBlock();
		final VNACalibrationContextTiny context = new VNACalibrationContextTiny();
		//
		context.setDib(dib);
		context.setCalibrationBlock(calBlock);
		context.setScanMode(calBlock.getScanMode());

		// update the temperature in this cal block
		context.setCalibrationTemperature(calBlock.getTemperature());

		// create correction constants
		final double correctionRadian = dib.getPhaseCorrection() * Math.PI / 180.0;
		context.setSineCorrection(Math.sin(correctionRadian));
		context.setCosineCorrection(Math.cos(correctionRadian));
		context.setGainCorrection(dib.getGainCorrection());
		context.setTempCorrection(dib.getTempCorrection());

		TraceHelper.exit(this, "createCalibrationContextForCalibratedSamples");
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNADriverMathHelper# createCalibrationContextForCalibrationPoints
	 * (krause.vna.data.calibrated.VNACalibrationBlock)
	 */
	public VNACalibrationContext createCalibrationContextForCalibrationPoints(final VNACalibrationBlock calBlock, final VNACalibrationKit calKit) {
		final String methodName = "createCalibrationContextForCalibrationPoints";
		TraceHelper.entry(this, methodName);
		VNACalibrationContextTiny context = null;
		if (calBlock != null) {

			// create calibration context
			final VNADriverSerialTiny2DIB dib = (VNADriverSerialTiny2DIB) calBlock.getMathHelper().getDriver().getDeviceInfoBlock();
			context = new VNACalibrationContextTiny();
			//
			context.setDib(dib);
			context.setCalibrationBlock(calBlock);
			context.setScanMode(calBlock.getScanMode());
			context.setCalibrationSet(calKit);

			// update the temperature in this cal block
			calBlock.calculateCalibrationTemperature();
			context.setCalibrationTemperature(calBlock.getTemperature());

			// create correction constants
			double correctionRadian = dib.getPhaseCorrection() * Math.PI / 180.0;
			context.setSineCorrection(Math.sin(correctionRadian));
			context.setCosineCorrection(Math.cos(correctionRadian));
			context.setGainCorrection(dib.getGainCorrection());
			context.setTempCorrection(dib.getTempCorrection());

		} else {
			ErrorLogHelper.text(this, methodName, "calBlock is null");
		}
		TraceHelper.exit(this, methodName);
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNADriverMathHelper#createCalibrationPoint(krause.vna. data .calibrated.VNACalibrationContext,
	 * krause.vna.data.VNABaseSample, krause.vna.data.VNABaseSample, krause.vna.data.VNABaseSample, krause.vna.data.VNABaseSample)
	 */
	public VNACalibrationPoint createCalibrationPoint(final VNACalibrationContext pContext, final VNABaseSample sampleOpen, final VNABaseSample sampleShort, final VNABaseSample sampleLoad, final VNABaseSample sampleLoop) {
		final VNACalibrationContextTiny tinyContext = (VNACalibrationContextTiny) pContext;

		// what kind of scan?
		if (tinyContext.getScanMode().isTransmissionMode()) {
			// transmission
			// correct
			final VNABaseSample corrOpen = calculateCorrectedBaseSample(tinyContext, sampleOpen, tinyContext.getCalibrationTemperature());
			final VNABaseSample corrLoop = calculateCorrectedBaseSample(tinyContext, sampleLoop, tinyContext.getCalibrationTemperature());

			// apply transformation
			return createCalibrationPointForTransmission(corrOpen, corrLoop);
		} else {
			// reflection
			// correct
			final VNABaseSample corrOpen = calculateCorrectedBaseSample(tinyContext, sampleOpen, tinyContext.getCalibrationTemperature());
			final VNABaseSample corrShort = calculateCorrectedBaseSample(tinyContext, sampleShort, tinyContext.getCalibrationTemperature());
			final VNABaseSample corrLoad = calculateCorrectedBaseSample(tinyContext, sampleLoad, tinyContext.getCalibrationTemperature());

			// apply transformation
			return createCalibrationPointForReflection(tinyContext, corrOpen, corrShort, corrLoad);
		}
	}

	/**
	 * 
	 * @param pContext
	 * @param baseOpen
	 * @param baseShort
	 * @param baseLoad
	 * @return
	 */
	protected VNACalibrationPoint createCalibrationPointForReflection(final VNACalibrationContextTiny calContext, final VNABaseSample sampleOpen, final VNABaseSample sampleShort, final VNABaseSample sampleLoad) {
		VNACalibrationPoint rc = new VNACalibrationPoint();

		//
		// M1 = open
		// M2 = short
		// M3 = load
		final Complex complexOpen = sampleOpen.asComplex();
		final Complex complexShort = sampleShort.asComplex();
		final Complex complexLoad = sampleLoad.asComplex();

		final VNACalibrationKit calibrationKit = calContext.getCalibrationKit();
		final double f = sampleShort.getFrequency();
		final double waveLength = (SOL / f) * 1000; // 299792458

		// formula A1
		// ideal OPEN
		final double openOffset = calibrationKit.getOpenOffset();
		final double openPhase = -2 * PHASE_FULL * openOffset / waveLength;
		final double openRealCorrection = Math.cos(Math.toRadians(openPhase));
		final double openImagCorrection = Math.sin(Math.toRadians(openPhase));
		final Complex idealOPEN = new Complex(openRealCorrection, openImagCorrection);

		// formula A3
		// ideal LOAD
		final Complex idealLOAD = new Complex(R_ZERO, 0);

		// formula A2
		// ideal SHORT
		final double phase = PHASE_HALF - (2 * (PHASE_FULL * calibrationKit.getShortOffset() / waveLength));
		final double realCorrection = Math.cos(Math.toRadians(phase));
		final double imagCorrection = Math.sin(Math.toRadians(phase));
		final Complex idealSHORT = new Complex(realCorrection, imagCorrection);

		// calculate deltaE
		// formula 1a: (((A2*M2 - A1*M1)*(M1-M3)) + ((A3*M3 - A1*M1)*(M2-M1))) /
		// formula 1b: (((A2*M2 - A1*M1)*(A3-A1)) - ((A3*M3 - A1*M1)*(A2-A1)))
		Complex p1 = complexShort.multiply(idealSHORT).subtract(complexOpen.multiply(idealOPEN)).multiply(complexOpen.subtract(complexLoad));
		Complex p2 = complexLoad.multiply(idealLOAD).subtract(complexOpen.multiply(idealOPEN)).multiply(complexShort.subtract(complexOpen));
		Complex p3 = complexShort.multiply(idealSHORT).subtract(complexOpen.multiply(idealOPEN)).multiply(idealLOAD.subtract(idealOPEN));
		Complex p4 = complexLoad.multiply(idealLOAD).subtract(complexOpen.multiply(idealOPEN)).multiply(idealSHORT.subtract(idealOPEN));
		rc.setDeltaE(p1.add(p2).divide(p3.subtract(p4)));

		// calculate e11
		// formula 1: e11 = (M2 - M1 + (A2-A1)*DeltaE) / (A2*M2 - A1*M1)
		rc.setE11(complexShort.subtract(complexOpen).add(rc.getDeltaE().multiply(idealSHORT.subtract(idealOPEN))).divide(complexShort.multiply(idealSHORT).subtract(complexOpen.multiply(idealOPEN))));

		// calculate e00
		// formula 1: e00 = M1 - A1*M1*e11 + A1*DeltaE
		rc.setE00(complexOpen.subtract(complexOpen.multiply(idealOPEN).multiply(rc.getE11())).add(rc.getDeltaE().multiply(idealOPEN)));

		// transfer frequency of point of open sample to calibration point
		rc.setFrequency(sampleOpen.getFrequency());
		return rc;
	}

	/**
	 * Use sampleOpen as open Use sampleLoop as thru
	 * 
	 * Result E00 as mThru
	 * 
	 * Result E11 as mOpen
	 * 
	 * Result DeltaE as mThru - mOpen
	 * 
	 * @param sampleOpen
	 * @param sampleLoop
	 * @return
	 */
	private VNACalibrationPoint createCalibrationPointForTransmission(VNABaseSample sampleOpen, VNABaseSample sampleLoop) {
		VNACalibrationPoint rc = new VNACalibrationPoint();
		//
		rc.setFrequency(sampleLoop.getFrequency());
		//
		rc.setE00(new Complex((sampleLoop.getAngle() - 512) * 0.003, (sampleLoop.getLoss() - 512) * 0.003));

		//
		rc.setE11(new Complex((sampleOpen.getAngle() - 512) * 0.003, (sampleOpen.getLoss() - 512) * 0.003));

		// hru)-M(open)
		rc.setDeltaE(rc.getE00().subtract(rc.getE11()));
		return rc;
	}
}

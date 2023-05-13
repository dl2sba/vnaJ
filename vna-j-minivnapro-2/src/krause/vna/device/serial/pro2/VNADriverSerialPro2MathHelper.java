package krause.vna.device.serial.pro2;

import org.apache.commons.math3.complex.Complex;

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

public class VNADriverSerialPro2MathHelper extends VNADriverMathBaseHelper {

	static final double R_MINUS_ONE = -1;
	static final double R_PLUS_ONE = 1.0;
	static final double R_ZERO = 0;

	private static final double SMALL_PHASE = 0.1;

	/**
	 * 
	 * @param driver
	 */
	public VNADriverSerialPro2MathHelper(IVNADriver driver) {
		super(driver);
		TraceHelper.entry(this, "VNADriverSerialPro2MathHelper");
		TraceHelper.exit(this, "VNADriverSerialPro2MathHelper");
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

		// do some filtering before the specific stuff
		super.applyPostFilter(samples, dib);

		TraceHelper.exit(this, "applyFilter");
	}

	/**
	 * 
	 * @param context
	 * @param sample
	 * @param temp
	 * @return
	 */
	private VNABaseSample calculateCorrectedBaseSample(VNACalibrationContextPro2 context, VNABaseSample sample) {
		return sample;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNADriverMathHelper#createCalibratedSample(krause. vna.data.VNAScanMode,
	 * krause.vna.data.VNABaseSample, krause.vna.data.calibrated.VNACalibrationPoint)
	 */
	public VNACalibratedSample createCalibratedSample(VNACalibrationContext pContext, VNABaseSample rawSample, VNACalibrationPoint calibPoint) {
		final VNACalibrationContextPro2 context = (VNACalibrationContextPro2) pContext;
		final VNABaseSample correctedRawSample = calculateCorrectedBaseSample(context, rawSample);

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
	 * @return
	 */
	private VNACalibratedSample createCalibratedSampleForReflection(VNACalibrationContext context, VNABaseSample rawSample, VNACalibrationPoint calib) {
		final VNADeviceInfoBlock dib = context.getDib();
		final Complex rhoM = rawSample.asComplex();

		// calculate rhoA
		// RhoA = (RhoM - e00) / (RhoM*e11 - DeltaE);
		final Complex rho = rhoM.subtract(calib.getE00()).divide(rhoM.multiply(calib.getE11()).subtract(calib.getDeltaE()));

		// calculate MAG
		double mag = rho.abs();
		if (mag > 1.0) {
			mag = 1.0;
		}

		// calculate SWR
		double swr = (1.0 + mag) / (1 - mag);

		// calculate RL
		double returnLoss = 20.0 * Math.log10(mag);
		returnLoss = Math.max(returnLoss, dib.getMaxLoss());

		// calculate PHASE RL
		double returnPhase = Math.toDegrees(rho.getArgument());

		if ((returnPhase >= 0.0) && (returnPhase < SMALL_PHASE)) {
			returnPhase = SMALL_PHASE;
		} else if ((returnPhase > -SMALL_PHASE) && (returnPhase < 0.0)) {
			returnPhase = -SMALL_PHASE;
		}

		// folding variant
		if (returnPhase > VNADriverSerialPro2DIB.MAX_PHASE) {
			returnPhase += 2 * VNADriverSerialPro2DIB.MIN_PHASE;
		} else if (returnPhase < VNADriverSerialPro2DIB.MIN_PHASE) {
			returnPhase += 2 * VNADriverSerialPro2DIB.MAX_PHASE;
		}

		// f = Cos((angle(i) * 0.1758) / 57.324)
		final double f = Math.cos(Math.toRadians(returnPhase));
		// g = Sin((angle(i) * 0.1758) / 57.324)
		final double g = Math.sin(Math.toRadians(returnPhase));
		// rr = f * mag
		final double rr = f * mag;
		// ss = g * mag
		final double ss = g * mag;
		// '******************************************* X calc
		// *************************************
		// x_imp = Abs(((2 * ss) / (((1 - rr) ^ 2) + (ss ^ 2))) * 50)
		final double xImp = ((2 * ss) / (((1 - rr) * (1 - rr)) + (ss * ss))) * context.getDib().getReferenceResistance().getReal();
		// '******************************************* R calc
		// *************************************
		// r_imp = Abs(((1 - (rr ^ 2) - (ss ^ 2)) / (((1 - rr) ^ 2) + (ss ^ 2)))
		// * 50)
		double rImp = ((1 - (rr * rr) - (ss * ss)) / (((1 - rr) * (1 - rr)) + (ss * ss))) * context.getDib().getReferenceResistance().getReal();
		if (rImp < 0) {
			rImp = 0;
		}

		// '******************************************* Z calc
		// *************************************
		// z_imp = Sqr(((r_imp) ^ 2 + (x_imp) ^ 2))
		final double zImp = Math.sqrt(((rImp * rImp) + (xImp * xImp)));

		//
		final VNACalibratedSample rc = new VNACalibratedSample();
		rc.setFrequency(rawSample.getFrequency());
		rc.setRHO(rho);
		rc.setMag(mag);
		rc.setReflectionLoss(returnLoss);
		rc.setReflectionPhase(returnPhase);
		rc.setSWR(swr);
		rc.setR(rImp);
		rc.setX(xImp);
		rc.setZ(zImp);

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
		TraceHelper.entry(this, "createCalibratedSampleForTransmission");
		VNACalibratedSample rc = new VNACalibratedSample();
		//
		Complex mDUT = new Complex((rawSample.getAngle() - 512) * 0.003, (rawSample.getLoss() - 512) * 0.003);
		//
		// GDUT=(M(dut) - M(open) / M(Thru)-M(open)
		Complex gDUT = mDUT.subtract(calPoint.getE11()).divide(calPoint.getDeltaE());

		// MAG = =IMABS(gDUT)
		rc.setMag(gDUT.abs());

		// calculate RL
		double tl = Math.max(20.0 * Math.log10(rc.getMag()), context.getDib().getMaxLoss());

		// calculate PHASE IMARGUMENT(GDUT)*57,3
		double tp = Math.toDegrees(-gDUT.getArgument());

		double mag1 = Math.pow(10, tl / 20.0);
		double mag = Math.pow(10, -tl / 20.0);
		double dRef = 2 * context.getDib().getReferenceResistance().getReal();
		double rs = ((dRef * mag) / Math.sqrt(1.0 + Math.pow(Math.tan(Math.toRadians(tp)), 2))) - dRef;
		double xs = -(rs + 100.0) * Math.tan(Math.toRadians(tp));
		double z = Math.sqrt(rs * rs + xs * xs);

		rc.setTransmissionLoss(tl);
		rc.setTransmissionPhase(tp);
		rc.setMag(mag1);
		rc.setR(rs);
		rc.setX(xs);
		rc.setZ(z);
		rc.setFrequency(rawSample.getFrequency());

		TraceHelper.exitWithRC(this, "createCalibratedSampleForTransmission", rc);
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
		final VNADriverSerialPro2DIB dib = (VNADriverSerialPro2DIB) calBlock.getMathHelper().getDriver().getDeviceInfoBlock();
		final VNACalibrationContextPro2 context = new VNACalibrationContextPro2();
		//
		context.setDib(dib);
		context.setCalibrationBlock(calBlock);
		context.setScanMode(calBlock.getScanMode());

		// update the temperature in this cal block
		context.setCalibrationTemperature(calBlock.getTemperature());

		TraceHelper.exit(this, "createCalibrationContextForCalibratedSamples");
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNADriverMathHelper# createCalibrationContextForCalibrationPoints
	 * (krause.vna.data.calibrated.VNACalibrationBlock)
	 */
	public VNACalibrationContext createCalibrationContextForCalibrationPoints(final VNACalibrationBlock calBlock, final VNACalibrationKit kit) {
		TraceHelper.entry(this, "createCalibrationContextForCalibrationPoints");

		// create calibration context
		final VNADriverSerialPro2DIB dib = (VNADriverSerialPro2DIB) calBlock.getMathHelper().getDriver().getDeviceInfoBlock();
		final VNACalibrationContextPro2 context = new VNACalibrationContextPro2();
		//
		context.setDib(dib);
		context.setCalibrationBlock(calBlock);
		context.setScanMode(calBlock.getScanMode());

		// update the temperature in this cal block
		calBlock.calculateCalibrationTemperature();
		context.setCalibrationTemperature(calBlock.getTemperature());

		TraceHelper.exit(this, "createCalibrationContextForCalibrationPoints");
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNADriverMathHelper#createCalibrationPoint(krause. vna.data.VNAScanMode,
	 * krause.vna.data.VNABaseSample, krause.vna.data.VNABaseSample, krause.vna.data.VNABaseSample)
	 */
	public VNACalibrationPoint createCalibrationPoint(VNACalibrationContext pContext, VNABaseSample sampleOpen, VNABaseSample sampleShort, VNABaseSample sampleLoad, VNABaseSample sampleLoop) {
		VNACalibrationContextPro2 context = (VNACalibrationContextPro2) pContext;

		// what kind of scan?
		if (context.getScanMode().isTransmissionMode()) {
			// transmission
			// correct
			VNABaseSample corrOpen = calculateCorrectedBaseSample(context, sampleOpen);
			VNABaseSample corrLoop = calculateCorrectedBaseSample(context, sampleLoop);

			// apply transformation
			return createCalibrationPointForTransmission(corrOpen, corrLoop);
		} else {
			// reflection
			// correct
			VNABaseSample corrOpen = calculateCorrectedBaseSample(context, sampleOpen);
			VNABaseSample corrShort = calculateCorrectedBaseSample(context, sampleShort);
			VNABaseSample corrLoad = calculateCorrectedBaseSample(context, sampleLoad);

			// apply transformation
			return createCalibrationPointForReflection(corrOpen, corrShort, corrLoad);
		}
	}

	/**
	 * 
	 * @param baseOpen
	 * @param baseShort
	 * @param baseLoad
	 * @return
	 */
	private VNACalibrationPoint createCalibrationPointForReflection(VNABaseSample sampleOpen, VNABaseSample sampleShort, VNABaseSample sampleLoad) {
		VNACalibrationPoint rc = new VNACalibrationPoint();

		//
		Complex m1 = sampleOpen.asComplex();
		Complex m2 = sampleShort.asComplex();
		Complex m3 = sampleLoad.asComplex();

		// calculate deltaE
		final double A1 = R_PLUS_ONE;
		final double A2 = R_MINUS_ONE;
		final double A3 = R_ZERO;

		// (((A2*M2 - A1*M1)*(M1-M3)) + ((A3*M3 - A1*M1)*(M2-M1))) /
		// (((A2*M2 - A1*M1)*(A3-A1)) - ((A3*M3 - A1*M1)*(A2-A1)));
		Complex p1 = m2.multiply(A2).subtract(m1.multiply(A1)).multiply(m1.subtract(m3));
		Complex p2 = m3.multiply(A3).subtract(m1.multiply(A1)).multiply(m2.subtract(m1));
		Complex p3 = m2.multiply(A2).subtract(m1.multiply(A1)).multiply(A3 - A1);
		Complex p4 = m3.multiply(A3).subtract(m1.multiply(A1)).multiply(A2 - A1);
		rc.setDeltaE(p1.add(p2).divide(p3.subtract(p4)));

		// calculate e11
		// e11 = (M2 - M1 + (A2-A1)*DeltaE) / (A2*M2 - A1*M1);
		rc.setE11(m2.subtract(m1).add(rc.getDeltaE().multiply(A2 - A1)).divide(m2.multiply(A2).subtract(m1.multiply(A1))));

		// calculate e00
		// e00 = M1 - A1*M1*e11 + A1*DeltaE;
		rc.setE00(m1.subtract(m1.multiply(A1).multiply(rc.getE11())).add(rc.getDeltaE().multiply(A1)));

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

package krause.vna.device.serial.std2;

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

public class VNADriverSerialStd2MathHelper extends VNADriverMathBaseHelper {

	// constants for conversion of ADC-values to numerical values
	private static final int DEFAULT_ADC_TICKS = 1024;

	private static final double DEFAULT_MAX_LOSS = 60.0;
	private static final double DEFAULT_MAX_PHASE = 180.0;

	private static final double DEFAULT_PHASE_PER_BIT = DEFAULT_MAX_PHASE / (DEFAULT_ADC_TICKS - 1);
	private static final double DEFAULT_RETURNLOSS_PER_BIT = DEFAULT_MAX_LOSS / (DEFAULT_ADC_TICKS - 1);

	public VNADriverSerialStd2MathHelper(IVNADriver driver) {
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
			return createCalibratedSampleForTransmission(context, rhoMSample, calib);
		} else {
			return createCalibratedSampleForReflection(rhoMSample, calib);
		}
	}

	/**
	 * 
	 * @param dib
	 * @param pRaw
	 * @param pCalPoint
	 * @return
	 */
	private VNACalibratedSample createCalibratedSampleForReflection(final VNABaseSample pRaw, final VNACalibrationPoint pCalPoint) {
		double calMag = pCalPoint.getDeltaE().getReal();
		double calPhsOffset = pCalPoint.getE00().getReal();
		double calPhsRange = pCalPoint.getE11().getReal();

		double dutPhs = pRaw.getAngle(); // Read VNA phase ADC value 0 to 1023
		double dutMag = pRaw.getLoss(); // Read VNA magnitude ADC value 0 to 1023

		// Magnitude ADC value = 0 -> RL = 30 dB without calibration
		// Magnitude ADC value = 512 -> RL = 0 dB without calibration
		// Magnitude ADC value = 1023 -> RL = -30 dB without calibration
		// cal_mag is ideally 512
		double calibratedMag = calMag - dutMag; // Magnitude as non-positive value.
		// Offset so mid ADC value produces 0 dB, and adjust for calibration.

		double calcRL = calibratedMag * DEFAULT_MAX_LOSS / (DEFAULT_ADC_TICKS - 1); // Return loss in dB as non-positive value.
		double calcRho;
		double calcSwr;

		if (calcRL > -0.173741) {
			// If return loss in dB is close enough to 0.0 to
			// produce SWR of 99.99 or return loss is out of range,
			calcRL = 0.0; // set return loss in dB to 0.0, and
			calcRho = 1.0; // set magnitude of reflection coefficient to 1.0, and
			calcSwr = 99.99; // set SWR to maximum value.
		} else {
			// Return loss in dB is in range
			calcRho = Math.pow(10.0, calcRL / 20.0); // Calculate magnitude of reflection coefficient.
			// Return loss in dB is non-positive.
			calcSwr = (1.0 + calcRho) / (1.0 - calcRho); // Calculate SWR
		}

		// Phase ADC value = 0 -> Phi = 0 degrees without calibration
		// Phase ADC value = 1023 -> Phi = 180 degrees without calibration
		// cal_phs_offset is ideally 0
		double calibratedPhase = dutPhs - calPhsOffset; // Adjust angle as ADC value for calibration

		double calcPhi;
		double re;
		double im;

		if (calibratedPhase < 0) {
			// If angle is too low,
			calcPhi = 0.0; // set it to minimum value.
			re = calcRho; // Real part of reflection coefficient
			im = 0.0; // Imaginary part of reflection coefficient
		} else {
			// Angle isn't too low
			// cal_phs_range is ideally 1023
			calcPhi = calibratedPhase * DEFAULT_MAX_PHASE / calPhsRange; // Scale angle to degrees

			if (calcPhi > DEFAULT_MAX_PHASE) { // If angle is too high,
				calcPhi = DEFAULT_MAX_PHASE; // set it to maximum value.
				re = -calcRho; // Real part of reflection coefficient
				im = 0.0; // Imaginary part of reflection coefficient
			} else { // Angle is in range
				double phiRadians = Math.toRadians(calcPhi); // Scale angle to radians
				re = calcRho * Math.cos(phiRadians); // Real part of reflection coefficient
				im = calcRho * Math.sin(phiRadians); // Imaginary part of reflection coefficient
			}
		} // Angle isn't too low

		// denominator = ( (1 - re) * (1 - re) + (im * im) ) / 50.0
		double denominator = 1.0 - re;
		denominator = (denominator * denominator + im * im) / 50.0;

		double calcRs;
		double calcXs;

		if (denominator < 0.00001) { // If denominator is too small
			calcRs = 9999.0; // Real part of DUT impedance
			calcXs = 9999.0; // Imaginary part of DUT impedance
		} else { // Denominator is in range

			// Rs = fabs((1.0 - re * re - im * im) / denominator) * 50.0

			calcRs = (1.0 - re * re - im * im) / denominator; // Real part of DUT impedance.
																// denominator scaled above.
			// Xs = fabs(2.0 * im) / denominator * 50.0

			calcXs = 2.0 * im / denominator; // Imaginary part of DUT impedance.
												// denominator scaled above.
			if (calcRs > 9999.0) // If Rs is too high,
				calcRs = 9999.0; // set it to maximum value.
			else if (calcRs < 0.0) // If Rs is too low,
				calcRs = 0.0; // set it to minimum value.

			if (calcXs > 9999.0) // If Xs is too high,
				calcXs = 9999.0; // set it to maximum value.
			else if (calcXs < 0.0) // If Xs is too low,
				calcXs = 0.0; // set it to minimum value.

		} // Denominator is in range

		double calcZ = Math.sqrt(calcRs * calcRs + calcXs * calcXs); // Magnitude of DUT impedance

		if (calcZ > 9999.0) // If it's too high,
			calcZ = 9999.0; // set it to maximum value.

		final VNACalibratedSample rc = new VNACalibratedSample();
		rc.setFrequency(pRaw.getFrequency());
		rc.setX(calcXs);
		rc.setR(calcRs);
		rc.setZ(calcZ);
		rc.setRHO(new Complex(re, im));
		rc.setReflectionLoss(calcRL);
		rc.setReflectionPhase(calcPhi);
		rc.setMag(calibratedMag);
		rc.setSWR(calcSwr);

		return rc;
	}

	/**
	 * 
	 * @param context
	 * @param raw
	 * @param calib
	 * @return
	 */
	private VNACalibratedSample createCalibratedSampleForTransmission(VNACalibrationContext context, VNABaseSample raw, VNACalibrationPoint calib) {
		VNACalibratedSample rc = new VNACalibratedSample();

		// transform the binary adc values to real numbers
		double tl = -((raw.getLoss() - calib.getLoss()) * DEFAULT_RETURNLOSS_PER_BIT);
		double tp = (raw.getAngle() - calib.getPhase()) * DEFAULT_PHASE_PER_BIT;

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
		rc.setFrequency(raw.getFrequency());

		return rc;
	}

	@Override
	public VNACalibrationContext createCalibrationContextForCalibratedSamples(VNACalibrationBlock calBlock) {
		TraceHelper.entry(this, "createCalibrationContextForCalibratedSamples");

		// create calibration context
		final VNACalibrationContext context = new VNACalibrationContext();
		//
		context.setDib(calBlock.getMathHelper().getDriver().getDeviceInfoBlock());
		context.setCalibrationBlock(calBlock);
		context.setScanMode(calBlock.getScanMode());

		TraceHelper.exit(this, "createCalibrationContextForCalibratedSamples");
		return context;
	}

	@Override
	public VNACalibrationContext createCalibrationContextForCalibrationPoints(final VNACalibrationBlock calBlock, final VNACalibrationKit kit) {
		final String methodName = "createCalibrationContextForCalibrationPoints";
		TraceHelper.entry(this, methodName);

		// create calibration context
		final VNACalibrationContext context = new VNACalibrationContext();
		//
		context.setDib(calBlock.getMathHelper().getDriver().getDeviceInfoBlock());
		context.setCalibrationBlock(calBlock);
		context.setScanMode(calBlock.getScanMode());

		TraceHelper.exit(this, methodName);
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
			return createCalibrationPointForReflection(numOpen, numShort);
		}
	}

	/**
	 * 
	 * @param pOpen
	 * @param pShort
	 * @param pLoad
	 * @return
	 */
	private VNACalibrationPoint createCalibrationPointForReflection(VNABaseSample pOpen, VNABaseSample pShort) {
		// prepare return object
		VNACalibrationPoint rc = new VNACalibrationPoint();
		rc.setFrequency(pOpen.getFrequency());

		// Connect open to DUT.
		// Ideal return loss = 0 dB at 0 degrees.
		//
		// Save calibration phase offset as ADC value = VNA phase ADC value with DUT open.
		// Save initial value of calibration magnitude offset.
		double calPhsOffset = pOpen.getAngle(); // Read VNA phase ADC value 0 to 1023, ideally 0
		double calMag = pOpen.getLoss(); // Read VNA magnitude ADC value 0 to 1023, ideally 512

		// Connect short to DUT.
		// Ideal return loss = 0 dB at 180 degrees.
		double shortPhs = pShort.getAngle(); // Read VNA phase ADC value 0 to 1023, ideally 1023
		double shortMag = pShort.getLoss(); // Read VNA magnitude ADC value 0 to 1023, ideally 512

		// Save calibration magnitude offset as ADC value = Largest magnitude ADC value.
		if (shortMag > calMag)
			calMag = shortMag;

		// Save calibration phase range in ADC steps.
		double calPhsRange;
		if ((calPhsOffset + 300) > shortPhs) // If phase range in ADC steps would be too small,
			calPhsRange = DEFAULT_ADC_TICKS - 1; // set it to ideal value.
		else // Phase range is valid
			calPhsRange = shortPhs - calPhsOffset; // Calculate phase range in ADC steps

		// cal_mag is ideally 512.
		// cal_phs_offset is ideally 0.
		// cal_phs_range is ideally 1023.
		rc.setDeltaE(new Complex(calMag));
		rc.setE00(new Complex(calPhsOffset));
		rc.setE11(new Complex(calPhsRange));

		return rc;
	}

	/**
	 * 
	 * @param numLoop
	 * @return
	 */
	private VNACalibrationPoint createCalibrationPointForTransmission(VNABaseSample numLoop) {
		final VNACalibrationPoint rc = new VNACalibrationPoint();
		rc.setFrequency(numLoop.getFrequency());
		rc.setLoss(numLoop.getLoss());
		rc.setPhase(numLoop.getAngle());
		return rc;
	}
}

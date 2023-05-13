package krause.vna.device.serial.std;

import krause.util.ras.logging.ErrorLogHelper;
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

public class VNADriverSerialStdMathHelper extends VNADriverMathBaseHelper {

	// constants for conversion of ADC-values to numerical values
	private static final int DEFAULT_ADC_BITS = 1024;
	private static final double DEFAULT_PHASE_PER_BIT = 180.0 / (DEFAULT_ADC_BITS - 1);

	public VNADriverSerialStdMathHelper(IVNADriver driver) {
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
	private static VNACalibratedSample createCalibratedSampleForReflection(VNACalibrationContext context, VNABaseSample raw, VNACalibrationPoint calib) {
		//
		// transform the binary adc values to real numbers
		final double loss = -((raw.getLoss() - calib.getLoss()) * context.getReturnLossPerBit());
		final double phase = (raw.getAngle() - calib.getPhase()) * DEFAULT_PHASE_PER_BIT;
		final double mag = Math.pow(10, loss / 20.0);
		final double swr = Math.abs((1.0 + mag) / (1.0 - mag));

		// f = Cos((angle(i) * 0.1758) / 57.324)
		final double f = Math.cos(Math.toRadians(phase));
		// g = Sin((angle(i) * 0.1758) / 57.324)
		final double g = Math.sin(Math.toRadians(phase));
		// rr = f * mag
		final double rr = f * mag;
		// ss = g * mag
		final double ss = g * mag;
		// '******************************************* X calc
		// *************************************
		// x_imp = Abs(((2 * ss) / (((1 - rr) ^ 2) + (ss ^ 2))) * 50)
		final double x_imp = Math.abs(((2 * ss) / (((1 - rr) * (1 - rr)) + (ss * ss))) * context.getDib().getReferenceResistance().getReal());
		// '******************************************* R calc
		// *************************************
		// r_imp = Abs(((1 - (rr ^ 2) - (ss ^ 2)) / (((1 - rr) ^ 2) + (ss ^ 2)))
		// * 50)
		final double r_imp = Math.abs(((1 - (rr * rr) - (ss * ss)) / (((1 - rr) * (1 - rr)) + (ss * ss))) * context.getDib().getReferenceResistance().getReal());
		// '******************************************* Z calc
		// *************************************
		// z_imp = Sqr(((r_imp) ^ 2 + (x_imp) ^ 2))
		final double z_imp = Math.sqrt(((r_imp * r_imp) + (x_imp * x_imp)));

		final VNACalibratedSample rc = new VNACalibratedSample();
		rc.setFrequency(raw.getFrequency());
		rc.setMag(mag);
		rc.setReflectionLoss(loss);
		rc.setReflectionPhase(phase);
		rc.setSWR(swr);
		rc.setR(r_imp);
		rc.setX(x_imp);
		rc.setZ(z_imp);

		return rc;
	}

	/**
	 * 
	 * @param context
	 * @param raw
	 * @param calib
	 * @return
	 */
	private static VNACalibratedSample createCalibratedSampleForTransmission(final VNACalibrationContext context, final VNABaseSample raw, final VNACalibrationPoint calib) {
		final VNACalibratedSample rc = new VNACalibratedSample();

		// transform the binary adc values to real numbers
		final double loss = -((raw.getLoss() - calib.getLoss()) * context.getTransmissionLossPerBit());
		final double phase = Math.abs((raw.getAngle() - calib.getPhase()) * DEFAULT_PHASE_PER_BIT);
		final double mag1 = Math.pow(10, loss / 20.0);
		final double mag = Math.pow(10, -loss / 20.0);
		final double dRef = 2 * context.getDib().getReferenceResistance().getReal();
		final double rs = ((dRef * mag) / Math.sqrt(1.0 + Math.pow(Math.tan(Math.toRadians(phase)), 2))) - dRef;
		final double xs = -(rs + 100.0) * Math.tan(Math.toRadians(phase));
		final double z = Math.sqrt(rs * rs + xs * xs);

		rc.setTransmissionLoss(loss);
		rc.setTransmissionPhase(phase);
		rc.setMag(mag1);
		rc.setR(rs);
		rc.setX(xs);
		rc.setZ(z);
		rc.setFrequency(raw.getFrequency());

		return rc;
	}

	@Override
	public VNACalibrationContext createCalibrationContextForCalibratedSamples(VNACalibrationBlock calBlock) {
		final String methodName = "createCalibrationContextForCalibratedSamples";
		TraceHelper.entry(this, methodName);

		// create calibration context
		final VNADriverSerialStdDIB dib = (VNADriverSerialStdDIB) calBlock.getMathHelper().getDriver().getDeviceInfoBlock();
		final VNACalibrationContext context = new VNACalibrationContext();
		//
		context.setDib(dib);
		context.setCalibrationBlock(calBlock);
		context.setScanMode(calBlock.getScanMode());

		context.setAdcBits(10);
		final int maxVal = -(1 << context.getAdcBits()) - 1;
		if (context.getScanMode().isReflectionMode()) {
			context.setReturnLossPerBit(dib.getMaxLoss() / maxVal);
		} else if (context.getScanMode().isTransmissionMode()) {
			context.setTransmissionLossPerBit(dib.getMaxLoss() / maxVal);
		} else {
			ErrorLogHelper.text(this, methodName, "Not supported scan mode [%d]", context.getScanMode());
		}

		TraceHelper.exit(this, methodName);
		return context;
	}

	@Override
	public VNACalibrationContext createCalibrationContextForCalibrationPoints(final VNACalibrationBlock calBlock, final VNACalibrationKit kit) {
		final String methodName = "createCalibrationContextForCalibrationPoints";
		TraceHelper.entry(this, methodName);

		// create calibration context
		final VNADriverSerialStdDIB dib = (VNADriverSerialStdDIB) calBlock.getMathHelper().getDriver().getDeviceInfoBlock();
		final VNACalibrationContext context = new VNACalibrationContext();
		//
		context.setDib(dib);
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
			return createCalibrationPointForReflection(numOpen);
		}
	}

	/**
	 * 
	 * @param numOpen
	 * @return
	 */
	private static VNACalibrationPoint createCalibrationPointForReflection(VNABaseSample numOpen) {
		final VNACalibrationPoint rc = new VNACalibrationPoint();
		rc.setFrequency(numOpen.getFrequency());

		// only calibration with LOSS
		rc.setLoss(numOpen.getLoss());
		rc.setPhase(numOpen.getAngle());
		return rc;
	}

	/**
	 * 
	 * @param numLoop
	 * @return
	 */
	private static VNACalibrationPoint createCalibrationPointForTransmission(VNABaseSample numLoop) {
		final VNACalibrationPoint rc = new VNACalibrationPoint();
		rc.setFrequency(numLoop.getFrequency());

		// only calibration with LOSS
		rc.setLoss(numLoop.getLoss());
		rc.setPhase(numLoop.getAngle());
		return rc;
	}
}

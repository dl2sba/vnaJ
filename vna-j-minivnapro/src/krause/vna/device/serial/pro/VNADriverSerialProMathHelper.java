package krause.vna.device.serial.pro;

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

public class VNADriverSerialProMathHelper extends VNADriverMathBaseHelper {

	public VNADriverSerialProMathHelper(IVNADriver driver) {
		super(driver);
		TraceHelper.entry(this, "VNADriverSerialProMathHelper");
		TraceHelper.exit(this, "VNADriverSerialProMathHelper");
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
	public VNACalibratedSample createCalibratedSample(VNACalibrationContext context, VNABaseSample rawSample, VNACalibrationPoint calibPoint) {
		if (context.getScanMode().isTransmissionMode()) {
			return createCalibratedSampleForTransmission(context.getDib(), rawSample, calibPoint);
		} else {
			return createCalibratedSampleForReflection(context.getDib(), rawSample, calibPoint);
		}
	}

	/**
	 * 
	 * @param rawSample
	 * @param calib
	 * @return
	 */
	private VNACalibratedSample createCalibratedSampleForReflection(VNADeviceInfoBlock dib, VNABaseSample rawSample, VNACalibrationPoint calib) {
		VNACalibratedSample rc = new VNACalibratedSample();
		rc.setFrequency(rawSample.getFrequency());

		// convert raw to loss & phase
		Complex rhoM = rawSample.asComplex();

		// calculate rhoA
		// RhoA = (RhoM - e00) / (RhoM*e11 - DeltaE)
		Complex rho = rhoM.subtract(calib.getE00()).divide(rhoM.multiply(calib.getE11()).subtract(calib.getDeltaE()));
		//
		rc.setRHO(rho);

		// calculate PHASE RL
		rc.setReflectionPhase(Math.toDegrees(rho.getArgument()));

		// calculate =IMDIV(IMPRODUKT(50;(IMSUMME(1;T217)));IMSUB(1;T217))
		Complex zComplex50Ohms = dib.getReferenceResistance().multiply(C_1.add(rc.getRHO())).divide(C_1.subtract(rc.getRHO()));
		//
		rc.setZComplex50Ohms(zComplex50Ohms);
		// calculate ZMag
		rc.setZ(zComplex50Ohms.abs());
		// calculate X
		rc.setX(zComplex50Ohms.getImaginary());
		// calculate R
		rc.setR(zComplex50Ohms.getReal());

		// calculate MAG
		double mag = rc.getRHO().abs();
		if (mag > 1.0) {
			mag = 1.0;
		}
		rc.setMag(mag);

		// calculate SWR
		double swr = (1.0d + mag) / (1.0d - mag);
		rc.setSWR(swr);

		// calculate RL
		double loss = 20.0 * Math.log10(rc.getMag());
		loss = Math.max(loss, getDriver().getDeviceInfoBlock().getMaxLoss());
		rc.setReflectionLoss(loss);

		return rc;
	}

	/**
	 * Result E00 as mThru
	 * 
	 * Result E11 as mOpen
	 * 
	 * Result DeltaE as mThru - mOpen
	 * 
	 * @param rawSample
	 * @param calPoint
	 * @return
	 */
	private VNACalibratedSample createCalibratedSampleForTransmission(VNADeviceInfoBlock dib, VNABaseSample rawSample, VNACalibrationPoint calPoint) {
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
		double loss = Math.max(20.0 * Math.log10(rc.getMag()), dib.getMaxLoss());

		// calculate PHASE IMARGUMENT(GDUT)*57,3
		double phase = Math.toDegrees(-gDUT.getArgument());

		double mag1 = Math.pow(10, loss / 20.0);
		double mag = Math.pow(10, -loss / 20.0);
		double dRef = 2 * dib.getReferenceResistance().getReal();
		double rs = ((dRef * mag) / Math.sqrt(1.0 + Math.pow(Math.tan(Math.toRadians(phase)), 2))) - dRef;
		double xs = -(rs + 100.0) * Math.tan(Math.toRadians(phase));
		double z = Math.sqrt(rs * rs + xs * xs);

		rc.setTransmissionLoss(loss);
		rc.setTransmissionPhase(phase);
		rc.setMag(mag1);
		rc.setR(rs);
		rc.setX(xs);
		rc.setZ(z);
		rc.setFrequency(rawSample.getFrequency());

		TraceHelper.exitWithRC(this, "createCalibratedSampleForTransmission", rc);
		return rc;
	}

	@Override
	public VNACalibrationContext createCalibrationContextForCalibratedSamples(VNACalibrationBlock calBlock) {
		TraceHelper.entry(this, "createCalibrationContextForCalibratedSamples");
		// create calibration context
		final VNADriverSerialProDIB dib = (VNADriverSerialProDIB) calBlock.getMathHelper().getDriver().getDeviceInfoBlock();
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
		final String methodName = "createCalibrationContextForCalibrationPoints";
		TraceHelper.entry(this, methodName);

		// create calibration context
		final VNADriverSerialProDIB dib = (VNADriverSerialProDIB) calBlock.getMathHelper().getDriver().getDeviceInfoBlock();
		final VNACalibrationContext context = new VNACalibrationContext();
		//
		context.setCalibrationBlock(calBlock);
		context.setScanMode(calBlock.getScanMode());
		context.setDib(dib);

		TraceHelper.exit(this, methodName);
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNADriverMathHelper#createCalibrationPoint(krause. vna.data.VNAScanMode,
	 * krause.vna.data.VNABaseSample, krause.vna.data.VNABaseSample, krause.vna.data.VNABaseSample)
	 */
	public VNACalibrationPoint createCalibrationPoint(VNACalibrationContext context, VNABaseSample numOpen, VNABaseSample numShort, VNABaseSample numLoad, VNABaseSample numLoop) {
		if (context.getScanMode().isTransmissionMode()) {
			return createCalibrationPointForTransmission(context.getDib(), numOpen, numLoop);
		} else {
			return createCalibrationPointForReflection(context.getDib(), numOpen, numShort, numLoad);
		}
	}

	/**
	 * 
	 * @param numOpen
	 * @param numShort
	 * @param numLoad
	 * @return
	 */
	private VNACalibrationPoint createCalibrationPointForReflection(VNADeviceInfoBlock dib, VNABaseSample numOpen, VNABaseSample numShort, VNABaseSample numLoad) {
		VNACalibrationPoint rc = new VNACalibrationPoint();
		rc.setFrequency(numOpen.getFrequency());
		//
		Complex cOpen = numOpen.asComplex();
		Complex cShort = numShort.asComplex();
		Complex cLoad = numLoad.asComplex();

		// calculate deltaE
		// (((A2*M2 - A1*M1)*(M1-M3)) + ((A3*M3 - A1*M1)*(M2-M1))) /
		// (((A2*M2 - A1*M1)*(A3-A1)) - ((A3*M3 - A1*M1)*(A2-A1)))
		Complex p1 = cShort.multiply(R_MINUS_ONE).subtract(cOpen.multiply(R_PLUS_ONE)).multiply(cOpen.subtract(cLoad));
		Complex p2 = cLoad.multiply(R_ZERO).subtract(cOpen.multiply(R_PLUS_ONE)).multiply(cShort.subtract(cOpen));
		Complex p3 = cShort.multiply(R_MINUS_ONE).subtract(cOpen.multiply(R_PLUS_ONE)).multiply(R_ZERO - R_PLUS_ONE);
		Complex p4 = cLoad.multiply(R_ZERO).subtract(cOpen.multiply(R_PLUS_ONE)).multiply(R_MINUS_ONE - R_PLUS_ONE);
		rc.setDeltaE(p1.add(p2).divide(p3.subtract(p4)));

		//
		// calculate e11 --> source match error
		// e11 = (M2 - M1 + (A2-A1)*DeltaE) / (A2*M2 - A1*M1)
		rc.setE11(cShort.subtract(cOpen).add(rc.getDeltaE().multiply(R_MINUS_ONE - R_PLUS_ONE)).divide(cShort.multiply(R_MINUS_ONE).subtract(cOpen.multiply(R_PLUS_ONE))));
		//
		// calculate e00 --> directivity error
		// e00 = M1 - A1*M1*e11 + A1*DeltaE
		rc.setE00(cOpen.subtract(cOpen.multiply(R_PLUS_ONE).multiply(rc.getE11())).add(rc.getDeltaE().multiply(R_PLUS_ONE)));
		//
		return rc;
	}

	/**
	 * Use numOpen as open Use numShort as thru
	 * 
	 * Result E00 as mThru
	 * 
	 * Result E11 as mOpen
	 * 
	 * Result DeltaE as mThru - mOpen
	 * 
	 * @param numOpen
	 * @param numLoop
	 * @return
	 */
	private VNACalibrationPoint createCalibrationPointForTransmission(VNADeviceInfoBlock dib, VNABaseSample numOpen, VNABaseSample numLoop) {
		VNACalibrationPoint rc = new VNACalibrationPoint();
		//
		rc.setFrequency(numLoop.getFrequency());
		//
		rc.setE00(new Complex((numLoop.getAngle() - 512) * 0.003, (numLoop.getLoss() - 512) * 0.003));

		//
		rc.setE11(new Complex((numOpen.getAngle() - 512) * 0.003, (numOpen.getLoss() - 512) * 0.003));

		// M(thru)-M(open)
		rc.setDeltaE(rc.getE00().subtract(rc.getE11()));
		return rc;
	}
}

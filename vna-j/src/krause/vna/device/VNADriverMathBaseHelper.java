package krause.vna.device;

import org.apache.commons.math3.complex.Complex;

import krause.common.exception.ProcessingException;
import krause.util.file.TimestampControlledFile;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.LogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNABaseSample;
import krause.vna.data.VNAMinMaxPair;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrated.VNACalibrationContext;
import krause.vna.data.calibrated.VNACalibrationPoint;
import krause.vna.data.filter.VNABaseFilterHelper;
import krause.vna.gui.cable.VNACableMeasurementPoint;
import krause.vna.gui.format.VNAFormatFactory;

public abstract class VNADriverMathBaseHelper implements IVNADriverMathHelper {
	public static final double PHASE_FULL = 360.0;
	public static final double PHASE_HALF = 180.0;

	public static final double PI_HALF = Math.PI / 2.0;

	public static final double PHASE_SWITCH = 170.0;
	public static final double PHASE_MINDIFF = 0.02;
	public static final double ONE_GHZ = 1e9;

	public static final Complex C_1 = new Complex(1, 0);
	public static final double R_MINUS_ONE = -1;
	public static final double R_PLUS_ONE = 1.0;
	public static final double R_ZERO = 0;

	// Speed of light in m/s
	public static final double SOL = 299792458;

	private IVNADriver driver;
	private VNAConfig config = VNAConfig.getSingleton();

	private TimestampControlledFile gaussianFilterFile = null;
	private double[] gaussianFilter = null;

	/**
	 * 
	 * @param driver
	 */
	public VNADriverMathBaseHelper(IVNADriver driver) {
		super();
		TraceHelper.entry(this, "VNADriverMathBaseHelper");
		this.driver = driver;
		this.gaussianFilterFile = new TimestampControlledFile(config.getGaussianFilterFileName());
		TraceHelper.exit(this, "VNADriverMathBaseHelper");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNADriverMathHelper#createCalibratedSamples(krause .vna.data.calibrated.VNACalibrationContext, krause.vna.data.VNASampleBlock, krause.vna.data.calibrated.VNACalibrationBlock)
	 */
	public VNACalibratedSampleBlock createCalibratedSamples(VNACalibrationContext context, VNASampleBlock raw) {
		TraceHelper.entry(this, "createCalibratedSamples");

		// put conversion temperature into context
		context.setConversionTemperature(raw.getDeviceTemperature());

		final int listLength = raw.getSamples().length;
		final VNACalibrationBlock calBlock = context.getCalibrationBlock();

		TraceHelper.text(this, "createCalibratedSamples", "calibration data created at temp=" + context.getCalibrationTemperature());
		TraceHelper.text(this, "createCalibratedSamples", "raw data created at temp=" + context.getConversionTemperature());

		//
		// are both parameters for port extension calculation set?
		if (config.isPortExtensionEnabled()) {
			// yes
			// calculate phase constant - this is used as a marker in
			// postprocessing
			// newPhase =((360*$A$1)/(298/(A3/1000000)*$B$1)*2) $A$1=length
			// $B$1=vf A3=f
			context.setPortExtensionPhaseConstant((2 * PHASE_FULL * config.getPortExtensionCableLength()) / (VNACableMeasurementPoint.SOL * config.getPortExtensionVf()));

			TraceHelper.text(this, "createCalibratedSamples", "port extension will be enabled with constant " + context.getPortExtensionPhaseConstant());
		}

		// now postprocess the calibrated sample block
		final VNACalibratedSampleBlock rc = new VNACalibratedSampleBlock(listLength);
		for (int i = 0; i < listLength; ++i) {
			// create calibrated sample
			VNACalibratedSample s = createCalibratedSample(context, raw.getSamples()[i], calBlock.getCalibrationPoints()[i]);

			// postprocess this sample
			postProcessCalibratedSample(s, context);

			// put it into result
			rc.consumeCalibratedSample(s, i);
		}

		// now postprocess complete bunch of samples
		postProcessCalibratedSamples(rc, context);

		TraceHelper.exit(this, "createCalibratedSamples");
		return rc;
	}

	/**
	 * This post-processes all calibrated samples after individual post-processing for each sample
	 * 
	 * @param rc
	 * @param context
	 */
	private void postProcessCalibratedSamples(final VNACalibratedSampleBlock csb, final VNACalibrationContext context) {
		TraceHelper.entry(this, "postProcessCalibratedSamples");

		final VNACalibratedSample[] samples = csb.getCalibratedSamples();
		final int len = samples.length;

		// more then one sample?
		if (len > 1) {
			// yes
			VNAMinMaxPair mmGroupDelay = csb.getMmGRPDLY();
			// freq diff is constant over samples
			double diffFreq = 1.0 * samples[1].getFrequency() - samples[0].getFrequency();
			TraceHelper.text(this, "postProcessCalibratedSamples", "Hz/step=" + VNAFormatFactory.getFrequencyFormat().format(diffFreq));

			//
			VNACalibratedSample lastSample = samples[0];
			lastSample.setGroupDelay(0);

			// transmission mode?
			if (context.getScanMode().isTransmissionMode()) {
				// yes
				for (int i = 1; i < len; ++i) {
					final VNACalibratedSample currentSample = samples[i];

					final double lastPhase = lastSample.getTransmissionPhase();
					final double currentPhase = currentSample.getTransmissionPhase();
					double diffPhase = currentPhase - lastPhase;
					if (diffPhase > PHASE_SWITCH) {
						diffPhase -= PHASE_FULL;
					} else if (diffPhase < -PHASE_SWITCH) {
						diffPhase += PHASE_FULL;
					}

					final double groupDelay = (-1.0 / 360.0) * (diffPhase / diffFreq) * ONE_GHZ;

					currentSample.setGroupDelay(groupDelay);

					// update the minMaxValues
					mmGroupDelay.consume(groupDelay, i);

					// remember last sample
					lastSample = currentSample;
				}
			}
			// reflection mode?
			else if (context.getScanMode().isReflectionMode()) {
				// yes

				for (int i = 1; i < len; ++i) {
					final VNACalibratedSample currentSample = samples[i];

					final double lastPhase = lastSample.getReflectionPhase();
					final double currentPhase = currentSample.getReflectionPhase();
					double diffPhase = currentPhase - lastPhase;
					if (diffPhase > PHASE_SWITCH) {
						diffPhase -= PHASE_FULL;
					} else if (diffPhase < -PHASE_SWITCH) {
						diffPhase += PHASE_FULL;
					}
					final double groupDelay = (-1.0 / 360.0) * (diffPhase / diffFreq) * ONE_GHZ;

					currentSample.setGroupDelay(groupDelay);

					// update the minMaxValues
					mmGroupDelay.consume(groupDelay, i);

					// remember last sample
					lastSample = currentSample;
				}
			}
		}
		TraceHelper.exit(this, "postProcessCalibratedSamples");
	}

	/**
	 * This post-processes one calibrated sample after the calculations done by the specific driver
	 * 
	 * @param sample
	 * @param context
	 */
	private void postProcessCalibratedSample(VNACalibratedSample sample, VNACalibrationContext context) {
		// which mode?
		// transmission?
		if (context.getScanMode().isTransmissionMode()) {
			// yes
			// do nothing

		} else if (context.getScanMode().isReflectionMode()) {
			// calculate Theta phase angle
			// Application.Atan2(R, X) * RAD2DEG 'Theta
			sample.setTheta(Math.toDegrees(PI_HALF - Math.atan2(sample.getR(), sample.getX())));

			// check if the sample has a calculated RHO
			if (sample.getRHO() == null) {
				// no
				// then calculate from reflection phase and angle
				final double radian = Math.toRadians(sample.getReflectionPhase());
				final double amplitude = Math.pow(10, sample.getReflectionLoss() / 20.0);
				final double real = amplitude * Math.cos(radian);
				final double imag = amplitude * Math.sin(radian);
				sample.setRHO(new Complex(real, imag));
			}

			// is port extension enabled?
			if (config.isPortExtensionEnabled()) {
				// yes
				final double refRes = context.getDib().getReferenceResistance().getReal();

				// calculate new phase value
				final double phaseShift = (context.getPortExtensionPhaseConstant() * sample.getFrequency());
				double newPhase = sample.getReflectionPhase() + phaseShift;
				while (newPhase < -PHASE_HALF) {
					newPhase += PHASE_FULL;
				}

				while (newPhase > PHASE_HALF) {
					newPhase -= PHASE_FULL;
				}

				// column L =COS(BOGENMASS(C3+K3))*I3
				final double L3 = Math.cos(Math.toRadians(newPhase)) * sample.getMag();

				// column M =SIN(BOGENMASS(C3+K3))*I3
				final double M3 = Math.sin(Math.toRadians(newPhase)) * sample.getMag();

				// R = ((1 - (L3* L3) - (M3* M3)) / (((1 - L3) * (1 - L3)) +
				// (M3* M3)))*50
				final double newR = ((1 - (L3 * L3) - (M3 * M3)) / (((1 - L3) * (1 - L3)) + (M3 * M3))) * refRes;

				// X = ((2*M3)/(((1-L3)*(1-L3))+(M3*M3)))*50
				final double newX = ((2 * M3) / (((1 - L3) * (1 - L3)) + (M3 * M3))) * refRes;

				// z_imp = Sqr(((r ^ 2 + x ^ 2))
				final double newZ = Math.sqrt(((newR * newR) + (newX * newX)));

				// recalculate reflection coefficient
				// |p| = SQRT[((R-50)^2 + X^2)/((R+50)^2 + X^2)]
				double newReflCoeff = Math.sqrt((((newR - refRes) * (newR - refRes)) + newX * newX) / (((newR + refRes) * (newR + refRes)) + newX * newX));

				// recalculate swr
				// SWR = (1 + |p|)/(1 - |p|)
				double newSWR = (1 + newReflCoeff) / (1 - newReflCoeff);

				// TraceHelper.text(this, "postProcessCalibratedSample", "p=" +
				// sample.getReflectionPhase() + "/" + newPhase + " r=" +
				// sample.getR() + "/" + newR + " x=" + sample.getX() + "/" +
				// newX);
				// set newly calculated values
				sample.setR(newR);
				sample.setX(newX);
				sample.setZ(newZ);
				sample.setReflectionPhase(newPhase);
				sample.setSWR(newSWR);
			}
		}
		// TraceHelper.exit(this, "postProcessCalibratedSample");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNADriverMathHelper#createCalibrationPoints(krause .vna.data.VNAScanMode, krause.vna.data.calibrated.VNACalibrationBlock)
	 */
	public void createCalibrationPoints(VNACalibrationContext context, VNACalibrationBlock calBlock) {
		TraceHelper.entry(this, "createCalibrationPoints");

		if (context != null && calBlock != null) {
			TraceHelper.text(this, "createCalibrationPoints", "creating calibration points for temp=" + calBlock.getTemperature());
			final int listLength = calBlock.getNumberOfSteps();
			final VNACalibrationPoint[] rc = new VNACalibrationPoint[listLength];

			final boolean useOpen = (calBlock.getCalibrationData4Open() != null) && (calBlock.getCalibrationData4Open().getSamples() != null);
			final boolean useLoad = (calBlock.getCalibrationData4Load() != null) && (calBlock.getCalibrationData4Load().getSamples() != null);
			final boolean useLoop = (calBlock.getCalibrationData4Loop() != null) && (calBlock.getCalibrationData4Loop().getSamples() != null);
			final boolean useShort = (calBlock.getCalibrationData4Short() != null) && (calBlock.getCalibrationData4Short().getSamples() != null);

			for (int i = 0; i < listLength; ++i) {
				VNABaseSample sOpen = null;
				VNABaseSample sShort = null;
				VNABaseSample sLoad = null;
				VNABaseSample sLoop = null;
				if (useLoad) {
					sLoad = calBlock.getCalibrationData4Load().getSamples()[i];
				}
				if (useOpen) {
					sOpen = calBlock.getCalibrationData4Open().getSamples()[i];
				}
				if (useShort) {
					sShort = calBlock.getCalibrationData4Short().getSamples()[i];
				}
				if (useLoop) {
					sLoop = calBlock.getCalibrationData4Loop().getSamples()[i];
				}
				rc[i] = createCalibrationPoint(context, sOpen, sShort, sLoad, sLoop);
			}
			calBlock.setCalibrationPoints(rc);
		} else {
			ErrorLogHelper.text(this, "createCalibrationPoints", "Parameter(s) are null");
		}

		TraceHelper.exit(this, "createCalibrationPoints");
	}

	/**
	 * calculate a moving average filter on the given sample block. this will generate a frequency shift in the data.
	 * 
	 * @param rawSamples
	 * @param degree
	 */
	public void calculateMovingAverage(VNASampleBlock rawBlock, int degree) {
		int min = degree;
		VNABaseSample[] rawSamples = rawBlock.getSamples();
		int max = rawSamples.length - degree;

		for (int i = min; i < max; ++i) {
			double loc = rawSamples[i].getLoss();

			for (int j = i - degree; j < i; ++j) {
				loc += rawSamples[i].getLoss();
			}
			for (int j = i + 1; j <= i + degree; ++j) {
				loc += rawSamples[i].getLoss();
			}
			loc /= ((2 * degree) + 1);
			rawSamples[i].setLoss(loc);
		}
	}

	/**
	 * The floating point implementation looks like
	 * 
	 * output:=input*N+output*(1-N);
	 * 
	 * As can be seen each input value requires two multiplications plus an addition. Since most controllers do not have the time nor the power for floating point operations, the exponential average is mostly implemented for integer numbers.
	 * 
	 * be N of {1/32, 1/64, 1/128, 1/256, 1/512} being shifts to the right by 5,6,7,8,9
	 * 
	 * output:=input * 1/32 + output -( output * 1/32) or rather
	 * 
	 * output:=input shr 5 + output - output shr 5
	 * 
	 * 
	 * @param rawSamples
	 * @param n
	 */
	public void calculateExponentialAverage4Loss(VNASampleBlock rawBlock, int n) {
		int min = 1;
		VNABaseSample[] rawSamples = rawBlock.getSamples();
		int max = rawSamples.length;
		int div = 1 << n;
		double output = rawSamples[0].getLoss();

		for (int i = min; i < max; ++i) {
			VNABaseSample current = rawSamples[i];
			double input = current.getLoss();
			output = (input / div) + output - (output / div);
			current.setLoss(output);
		}
	}

	/**
	 * The floating point implementation looks like
	 * 
	 * output:=input*N+output*(1-N);
	 * 
	 * As can be seen each input value requires two multiplications plus an addition. Since most controllers do not have the time nor the power for floating point operations, the exponential average is mostly implemented for integer numbers.
	 * 
	 * be N of {1/32, 1/64, 1/128, 1/256, 1/512} being shifts to the right by 5,6,7,8,9
	 * 
	 * output:=input * 1/32 + output -( output * 1/32) or rather
	 * 
	 * output:=input shr 5 + output - output shr 5
	 * 
	 * 
	 * @param rawSamples
	 * @param n
	 */
	public void calculateExponentialAverage4Phase(VNASampleBlock rawBlock, int n) {
		int min = 1;
		VNABaseSample[] rawSamples = rawBlock.getSamples();
		int max = rawSamples.length;
		int div = 1 << n;
		double output = rawSamples[0].getAngle();

		for (int i = min; i < max; ++i) {
			VNABaseSample current = rawSamples[i];
			double input = current.getAngle();
			output = (input / div) + output - (output / div);
			current.setAngle(output);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNADriverMathHelper#createCalibrationBlockFromRaw( krause.vna.data.VNAScanMode, krause.vna.data.VNASampleBlock, krause.vna.data.VNASampleBlock, krause.vna.data.VNASampleBlock)
	 */
	public final VNACalibrationBlock createCalibrationBlockFromRaw(VNACalibrationContext context, VNASampleBlock listOpen, VNASampleBlock listShort, VNASampleBlock listLoad, VNASampleBlock listLoop) throws ProcessingException {
		TraceHelper.entry(this, "createCalibrationBlockFromRaw");
		VNASampleBlock blk;
		if (listLoad != null) {
			blk = listLoad;
		} else if (listOpen != null) {
			blk = listOpen;
		} else if (listShort != null) {
			blk = listShort;
		} else if (listLoop != null) {
			blk = listLoop;
		} else {
			throw new ProcessingException("No data set on raw data");
		}
		VNACalibrationBlock rc = new VNACalibrationBlock(blk);
		rc.setCalibrationData4Load(listLoad);
		rc.setCalibrationData4Open(listOpen);
		rc.setCalibrationData4Short(listShort);
		rc.setCalibrationData4Loop(listLoop);
		createCalibrationPoints(context, rc);
		TraceHelper.exit(this, "createCalibrationBlockFromRaw");

		return rc;
	}

	/**
	 * apply some math to the base sample data prior filtering in the math helper of the specific device
	 * 
	 * @param samples
	 * @param dib
	 */
	public void applyPreFilter(VNABaseSample[] samples, VNADeviceInfoBlock dib) {
		final String methodName = "applyPreFilter";
		TraceHelper.entry(this, methodName);
		TraceHelper.exit(this, methodName);
	}

	/**
	 * apply some math to the base sample data after filtering in the math helper of the specific device
	 * 
	 * http://dev.theomader.com/gaussian-kernel-calculator/
	 * 
	 * @param samples
	 * @param dib
	 */
	public void applyPostFilter(VNABaseSample[] samples, VNADeviceInfoBlock dib) {
		final String methodName = "applyPostFilter";
		TraceHelper.entry(this, methodName);
		//
		// apply the filter?
		if (config.isApplyGaussianFilter()) {
			// yes
			// filter parameters not read or updated in filesystem ?
			if ((gaussianFilter == null) || (gaussianFilterFile.needsReload())) {
				// no
				String fn = gaussianFilterFile.getFilename();
				gaussianFilter = VNABaseFilterHelper.loadFilterParameters(fn);
				LogHelper.text(this, methodName, "Filter file [" + fn + "] read/reloaded");
			}
			final int numSamples = samples.length;
			final int filterLen = gaussianFilter.length;
			final int filterMid = filterLen / 2;
			// for every point
			for (int i = filterMid; i < numSamples - filterMid; ++i) {
				double newLoss = 0;
				double newPhase = 0;
				for (int j = 0; j < filterLen; ++j) {
					newLoss += (samples[i - filterMid + j].getLoss() * gaussianFilter[j]);
					newPhase += (samples[i - filterMid + j].getAngle() * gaussianFilter[j]);
				}
				samples[i].setLoss(newLoss);
				samples[i].setAngle(newPhase);
			}
		}

		TraceHelper.exit(this, methodName);
	}

	public IVNADriver getDriver() {
		return driver;
	}

	public void setDriver(IVNADriver pDriver) {
		driver = pDriver;
	}
}

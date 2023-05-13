package krause.vna.device.serial.tiny.calibration;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import krause.util.ras.logging.TraceHelper;
import krause.vna.background.VNABackgroundJob;
import krause.vna.background.VnaBackgroundTask;
import krause.vna.data.IVNADataConsumer;
import krause.vna.data.VNABaseSample;
import krause.vna.data.VNADataPool;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.VNAScanMode;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrated.VNACalibrationContext;
import krause.vna.data.calibrationkit.VNACalibrationKit;
import krause.vna.data.helper.VNACalibrationBlockHelper;
import krause.vna.data.helper.VNASampleBlockHelper;
import krause.vna.device.IVNADriverMathHelper;
import krause.vna.device.VNAScanRange;
import krause.vna.device.serial.tiny.VNADriverSerialTinyDIB;
import krause.vna.device.serial.tiny.VNADriverSerialTinyMessages;
import krause.vna.resources.VNAMessages;

public class PhaseCalibrationHelper implements IVNADataConsumer {
	private final VNADataPool datapool = VNADataPool.getSingleton();
	private final VNADriverSerialTinyDIB dib;
	private final Component parent;

	private static final double STEP_GAIN = (VNADriverSerialTinyDIB.MAX_CORR_GAIN - VNADriverSerialTinyDIB.MIN_CORR_GAIN) / 50;
	private static final double STEP_PHASE = (VNADriverSerialTinyDIB.MAX_CORR_PHASE - VNADriverSerialTinyDIB.MIN_CORR_PHASE) / 200;

	public PhaseCalibrationHelper(Component pComp, VNADriverSerialTinyDIB pDib) {
		final String methodName = "PhaseCalibrationHelper";
		TraceHelper.entry(this, methodName);
		this.dib = pDib;
		this.parent = pComp;
		TraceHelper.exit(this, methodName);
	}

	public void calculateFrequencies(VNABaseSample[] pSamples) {
		final String methodName = "calculateFrequencies";
		TraceHelper.entry(this, methodName);

		// find first maximum for P1
		int lastVal = pSamples[0].getP1();
		int lastDirection = 0;
		int newDirection = 0;

		int peakIndex = -1;
		int dipIndex = -1;

		for (int i = 1; i < pSamples.length; ++i) {
			int currVal = pSamples[i].getP1();
			if (lastVal > currVal) {
				// we are decreasing
				newDirection = -1;
			} else if (lastVal < currVal) {
				newDirection = 1;
			} else {
				newDirection = 0;
			}

			lastVal = currVal;

			if (newDirection == 0) {
				continue;
			}

			if ((lastDirection == 1) && (newDirection == -1)) {
				// we found a peak
				peakIndex = i;
				TraceHelper.text(this, methodName, "peak=" + peakIndex + " f=" + pSamples[i].getFrequency());
			} else if ((lastDirection == -1) && (newDirection == 1)) {
				// we found a dip
				dipIndex = i;
				TraceHelper.text(this, methodName, "dip=" + dipIndex + " f=" + pSamples[i].getFrequency());
			}

			lastDirection = newDirection;
		}

		TraceHelper.exit(this, methodName);

	}

	public void calculateBestFit(VNABaseSample[] pSamples) {
		final String methodName = "calculateBestFit";
		TraceHelper.entry(this, methodName);

		final WeightedObservedPoints obsP1 = new WeightedObservedPoints();
		final WeightedObservedPoints obsP2 = new WeightedObservedPoints();
		final WeightedObservedPoints obsP3 = new WeightedObservedPoints();
		final WeightedObservedPoints obsP4 = new WeightedObservedPoints();

		for (int x = 0; x < pSamples.length; ++x) {
			VNABaseSample aSample = pSamples[x];
			// Collect data.
			obsP1.add(x, aSample.getP1());
			obsP2.add(x, aSample.getP2());
			obsP3.add(x, aSample.getP3());
			obsP4.add(x, aSample.getP4());
		}

		// Retrieve fitted parameters (coefficients of the polynomial function).
		final double[] coeffP1 = PolynomialCurveFitter.create(3).fit(obsP1.toList());
		final double[] coeffP2 = PolynomialCurveFitter.create(3).fit(obsP2.toList());
		final double[] coeffP3 = PolynomialCurveFitter.create(3).fit(obsP3.toList());
		final double[] coeffP4 = PolynomialCurveFitter.create(3).fit(obsP4.toList());

		for (int x = 0; x < pSamples.length; ++x) {

			long i = 1;
			double p1 = coeffP1[0];
			double p2 = coeffP2[0];
			double p3 = coeffP3[0];
			double p4 = coeffP4[0];

			for (int coeffIndex = 1; coeffIndex < coeffP1.length; ++coeffIndex) {
				i *= x;
				p1 += i * coeffP1[coeffIndex];
				p2 += i * coeffP2[coeffIndex];
				p3 += i * coeffP3[coeffIndex];
				p4 += i * coeffP4[coeffIndex];
			}

			// write back newly calculated value
			VNABaseSample aSample = pSamples[x];
			aSample.setP1((int) p1);
			aSample.setP2((int) p2);
			aSample.setP3((int) p3);
			aSample.setP4((int) p4);
		}

		TraceHelper.exit(this, methodName);

	}

	/**
	 * 
	 * @param rawData
	 */
	public void autoCalOnMagnitude(VNASampleBlock rawData) {
		TraceHelper.entry(this, "autoCalOnMagnitude");
		double minMagnitudeGain = Double.MAX_VALUE;
		double minMagnitudePhase = Double.MAX_VALUE;
		double minDiffMagnitude = Double.MAX_VALUE;

		double curPhase = VNADriverSerialTinyDIB.MIN_CORR_PHASE;
		while (curPhase < VNADriverSerialTinyDIB.MAX_CORR_PHASE) {
			double curGain = VNADriverSerialTinyDIB.MIN_CORR_GAIN;
			while (curGain < VNADriverSerialTinyDIB.MAX_CORR_GAIN) {
				TraceHelper.text(this, "autoCalOnMagnitude", "p=" + curPhase + " g=" + curGain);
				// put current values to DIB
				dib.setGainCorrection(curGain);
				dib.setPhaseCorrection(curPhase);

				// retransform data
				Tuple[] transformedData = transformData(rawData);

				// search minimum and maximum magnitude
				double minMagnitude = Double.MAX_VALUE;
				double maxMagnitude = Double.MIN_VALUE;
				for (Tuple data : transformedData) {
					double mag = data.getVal();

					if (mag < minMagnitude) {
						minMagnitude = mag;
					}

					if (mag > maxMagnitude) {
						maxMagnitude = mag;
					}
				}

				double diffMagnitude = maxMagnitude - minMagnitude;

				if (diffMagnitude < minDiffMagnitude) {
					minDiffMagnitude = diffMagnitude;
					minMagnitudeGain = curGain;
					minMagnitudePhase = curPhase;
				}

				curGain += STEP_GAIN;
			}
			curPhase += STEP_PHASE;
		}
		TraceHelper.text(this, "doAutoCal", "minMagnitude     =" + minDiffMagnitude);
		TraceHelper.text(this, "doAutoCal", "minMagnitudeGain =" + minMagnitudeGain);
		TraceHelper.text(this, "doAutoCal", "minMagnitudePhase=" + minMagnitudePhase);

		// write back to DIB
		dib.setGainCorrection(minMagnitudeGain);
		dib.setPhaseCorrection(minMagnitudePhase);
		TraceHelper.exit(this, "autoCalOnMagnitude");
	}

	/**
	 * 
	 * @param rawScanData
	 * @param raw
	 * @return
	 */
	private Tuple[] transformData(VNASampleBlock rawScanData) {
		Tuple[] rc;
		// build the calibrated stuff
		final IVNADriverMathHelper mathHelper = datapool.getDriver().getMathHelper();
		final VNACalibrationBlock mcb = datapool.getMainCalibrationBlock();
		final VNACalibrationKit kit = datapool.getCalibrationKit();
		final VNACalibrationContext ctxCalPoints = mathHelper.createCalibrationContextForCalibrationPoints(mcb, kit);

		// create a new calibration block based on
		mathHelper.createCalibrationPoints(ctxCalPoints, mcb);

		//
		// create now a resized cal block for this scan
		final VNACalibrationBlock resizedCalibrationBlock = VNACalibrationBlockHelper.createResizedCalibrationBlock(mcb, VNADriverSerialTinyDIB.AUTOCAL_START_FREQ, VNADriverSerialTinyDIB.AUTOCAL_STOP_FREQ, VNADriverSerialTinyDIB.AUTOCAL_NUM_SAMPLES);
		final VNACalibrationContext ctxCalSamples = mathHelper.createCalibrationContextForCalibratedSamples(resizedCalibrationBlock);

		// create samples
		final VNACalibratedSampleBlock calibratedData = mathHelper.createCalibratedSamples(ctxCalSamples, rawScanData);

		//
		// =======================================
		// calibration on magnitude
		//
		// create an equal transformed data
		rc = new Tuple[calibratedData.getCalibratedSamples().length];

		for (int i = 0; i < rc.length; ++i) {
			VNACalibratedSample data = calibratedData.getCalibratedSamples()[i];
			Tuple at = new Tuple();

			at.setFrq(data.getFrequency());
			at.setVal(data.getMag());

			rc[i] = at;
		}

		return rc;
	}

	public void doCalibrate() {
		Object[] options = {
				VNAMessages.getString("Button.Continue"),
				VNAMessages.getString("Button.Cancel")
		};
		int n = JOptionPane.showOptionDialog(this.parent, VNADriverSerialTinyMessages.getString("msgExit"), VNADriverSerialTinyMessages.getString("calTitle"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		// user response
		if (n == 0) {
			// OK
			// read from fields
			VNAScanRange range = new VNAScanRange(VNADriverSerialTinyDIB.AUTOCAL_START_FREQ, VNADriverSerialTinyDIB.AUTOCAL_STOP_FREQ, VNADriverSerialTinyDIB.AUTOCAL_NUM_SAMPLES);

			// create one instance
			VnaBackgroundTask backgroundTask = new VnaBackgroundTask(datapool.getDriver());
			backgroundTask.addDataConsumer(this);
			backgroundTask.setStatusLabel(null);

			for (int i = 0; i < VNADriverSerialTinyDIB.AUTOCAL_NUM_OVERSAMPLES; ++i) {
				VNABackgroundJob job = new VNABackgroundJob();
				job.setSpeedup(1);
				job.setNumberOfSamples(VNADriverSerialTinyDIB.AUTOCAL_NUM_SAMPLES);
				job.setFrequencyRange(range);
				job.setScanMode(VNAScanMode.MODE_REFLECTION);
				backgroundTask.addJob(job);
			}
			backgroundTask.execute();
		}
	}

	@Override
	public void consumeDataBlock(List<VNABackgroundJob> jobs) {
		TraceHelper.entry(this, "consumeDataBlock");

		Object[] options = {
				VNAMessages.getString("Button.Continue"),
				VNAMessages.getString("Button.Cancel")
		};

		int n = JOptionPane.showOptionDialog(this.parent, VNADriverSerialTinyMessages.getString("msgCalculate"), VNADriverSerialTinyMessages.getString("calTitle"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		// user response
		if (n == 0) {
			// OK

			VNASampleBlock rawScanData = null;

			// we receive more than one?
			if (jobs.size() > 1) {
				// yes
				final List<VNASampleBlock> blocks = new ArrayList<>();

				// calculate average of them
				for (VNABackgroundJob job : jobs) {
					blocks.add(job.getResult());
				}

				rawScanData = VNASampleBlockHelper.calculateAverageSampleBlock(blocks);
				// we receive exactly one?
			} else if (jobs.size() == 1) {
				// yes
				// then use this
				rawScanData = jobs.get(0).getResult();
			}

			autoCalOnMagnitude(rawScanData);

			// inform user
			JOptionPane.showMessageDialog(this.parent, VNADriverSerialTinyMessages.getString("msgFinish"), VNADriverSerialTinyMessages.getString("calTitle"), JOptionPane.INFORMATION_MESSAGE, null);
		}
		TraceHelper.exit(this, "consumeDataBlock");
	}

}

package krause.vna.gui.cable;

import java.util.ArrayList;
import java.util.List;

import krause.util.ras.logging.TraceHelper;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;

public class VNACableMeasurementHelper {

	private boolean metricMode;
	private boolean scale360;

	public VNACableMeasurementHelper(boolean scale, boolean meter) {
		TraceHelper.exit(this, "VNACableMeasurementHelper");
		scale360 = scale;
		metricMode = meter;
		TraceHelper.exit(this, "VNACableMeasurementHelper");
	}

	/**
	 * 
	 * @param pVelocity
	 * @return
	 */
	public VNACableMeasurementPoint calculateLength(List<VNACalibratedSample> points, double pVelocity) {
		TraceHelper.entry(this, "calculateLength");
		VNACableMeasurementPoint rc = null;

		if (points != null) {
			if (points.size() == 2) {
				VNACableMeasurementPoint p1 = new VNACableMeasurementPoint(metricMode, scale360);
				p1.setStart(points.get(0));
				p1.setStop(points.get(1));
				p1.calculateLength(pVelocity);
				rc = p1;
			}
		}
		TraceHelper.exitWithRC(this, "calculateLength", rc);
		return rc;
	}

	public VNACableMeasurementPoint calculateVelocityFactor(List<VNACalibratedSample> points, double pCableLength) {
		final String methodName = "calculateVelocityFactor";
		TraceHelper.entry(this, methodName);
		VNACableMeasurementPoint rc = null;

		if ((points != null) && (points.size() == 2)) {
			VNACableMeasurementPoint p1 = new VNACableMeasurementPoint(metricMode, scale360);
			p1.setStart(points.get(0));
			p1.setStop(points.get(1));
			p1.calculateVelocityFactor(pCableLength);
			rc = p1;

		}
		TraceHelper.exitWithRC(this, methodName, rc);
		return rc;

	}

	public List<VNACalibratedSample> findAllCrossingPoints(VNACalibratedSampleBlock samples) {
		if (scale360) {
			return findAllCrossingPointsWithSign(samples);
		} else {
			return findAllCrossingPointsWithoutSign(samples);
		}
	}

	private List<VNACalibratedSample> findAllCrossingPointsWithoutSign(VNACalibratedSampleBlock samples) {
		TraceHelper.entry(this, "findAllCrossingPointsWithoutSign");

		List<VNACalibratedSample> rc = new ArrayList<>();
		//
		VNACalibratedSample[] calibratedSample = samples.getCalibratedSamples();
		double lastPhase = calibratedSample[10].getReflectionPhase();
		//
		int state = 0;
		for (int i = 10, max = calibratedSample.length; (i < max) && (state != 99); ++i) {
			VNACalibratedSample currentSample = calibratedSample[i];
			double phase = currentSample.getReflectionPhase();
			switch (state) {

			// find first falling edge
			case 0:
				// falling?
				if ((phase >= 90.0) && (lastPhase < 90.0)) {
					// yes
					rc.add(currentSample);
					// now wait for next rising edge
					state = 1;
				} else if ((phase < 90.0) && (lastPhase >= 90.0)) {
					// yes
					rc.add(currentSample);
					// now wait for next rising edge
					state = 0;
				}

				break;

			// wait for rising edge
			case 1:
				// falling?
				if ((phase < 90.0) && (lastPhase >= 90.0)) {
					// yes
					// add sample
					rc.add(currentSample);

					// and wait for next falling edge
					state = 0;
				}
				break;

			default:
				break;
			}
			lastPhase = phase;
		}
		TraceHelper.exitWithRC(this, "findAllCrossingPointsWithoutSign", "#=" + rc.size());
		return rc;
	}

	public List<VNACalibratedSample> findAllCrossingPointsWithSign(VNACalibratedSampleBlock samples) {
		final String methodName = "findAllCrossingPointsWithSign";
		TraceHelper.entry(this, methodName);

		List<VNACalibratedSample> rc = new ArrayList<>();
		//
		VNACalibratedSample[] calibratedSample = samples.getCalibratedSamples();
		double lastPhase = calibratedSample[10].getReflectionPhase();
		//
		int state = 0;
		for (int i = 10, max = calibratedSample.length; (i < max) && (state != 99); ++i) {
			VNACalibratedSample currentSample = calibratedSample[i];
			double phase = currentSample.getReflectionPhase();
			// do we have a rising edge?
			if ((phase > 0) && (lastPhase < 0)) {
				// yes
				rc.add(currentSample);
				TraceHelper.text(this, methodName, "state=" + state + " a f=" + currentSample.getFrequency() + " °=" + phase + " last°=" + lastPhase);
				state = 1;
				// do we have a falling edge?
			} else if ((phase < 0) && (lastPhase >= 0)) {
				TraceHelper.text(this, methodName, "state=" + state + " b f=" + currentSample.getFrequency() + " °=" + phase + " last°=" + lastPhase);
				rc.add(currentSample);
				state = 0;
			}
			lastPhase = phase;
		}
		TraceHelper.exitWithRC(this, methodName, "#=" + rc.size());

		return rc;
	}

	public List<VNACalibratedSample> findTwoCrossingPoints(VNACalibratedSampleBlock samples) {
		if (scale360) {
			return findTwoCrossingPointsWithSign(samples);
		} else {
			return findTwoCrossingPointsWithoutSign(samples);
		}
	}

	public List<VNACalibratedSample> findTwoCrossingPointsWithoutSign(VNACalibratedSampleBlock samples) {
		TraceHelper.entry(this, "findTwoCrossingPointsWithoutSign");

		List<VNACalibratedSample> rc = new ArrayList<>();
		VNACalibratedSample firstPoint = null;
		VNACalibratedSample secondPoint = null;
		final int startIndex = 10;
		//
		VNACalibratedSample sample1 = samples.getCalibratedSamples()[startIndex];
		VNACalibratedSample sample2 = samples.getCalibratedSamples()[startIndex + 1];
		//
		boolean fromLow = (sample1.getReflectionPhase() < sample2.getReflectionPhase());

		//
		int state = 0;
		for (int i = startIndex, max = samples.getCalibratedSamples().length; (i < max) && (state != 99); ++i) {
			VNACalibratedSample currentSample = samples.getCalibratedSamples()[i];
			double phase = currentSample.getReflectionPhase();
			switch (state) {
			case 0:
				if (fromLow) {
					if (phase > 90.0) {
						firstPoint = currentSample;
						rc.add(firstPoint);
						state = 1;
						i += 10;
					}
				} else {
					if (phase < 90.0) {
						firstPoint = currentSample;
						rc.add(firstPoint);
						state = 1;
						i += 10;
					}

				}
				break;
			case 1:
				if (fromLow) {
					if (phase < 90.0) {
						state = 2;
						i += 10;
					}
				} else {
					if (phase > 90.0) {
						state = 2;
						i += 10;
					}
				}
				break;
			case 2:
				if (fromLow) {
					if (phase > 90.0) {
						secondPoint = currentSample;
						rc.add(secondPoint);
						state = 99;
					}
				} else {
					if (phase < 90.0) {
						secondPoint = currentSample;
						rc.add(secondPoint);
						state = 99;
					}
				}
				break;

			default:
				break;
			}
		}

		TraceHelper.exit(this, "findTwoCrossingPointsWithoutSign");
		return rc;
	}

	public List<VNACalibratedSample> findTwoCrossingPointsWithSign(VNACalibratedSampleBlock samples) {

		List<VNACalibratedSample> rc = new ArrayList<>();
		VNACalibratedSample firstPoint = null;
		VNACalibratedSample secondPoint = null;
		//
		VNACalibratedSample[] calibratedSample = samples.getCalibratedSamples();
		double lastPhase = calibratedSample[10].getReflectionPhase();
		//
		int state = 0;
		for (int i = 10, max = calibratedSample.length; (i < max) && (state != 99); ++i) {
			VNACalibratedSample currentSample = calibratedSample[i];
			double phase = currentSample.getReflectionPhase();
			switch (state) {
			case 0:
				if ((phase > 0) && (lastPhase < 0)) {
					firstPoint = currentSample;
					rc.add(firstPoint);
					state = 1;
				}
				break;
			case 1:
				if ((phase > 0) && (lastPhase < 0)) {
					secondPoint = currentSample;
					rc.add(secondPoint);
					state = 99;
				}
				break;

			default:
				break;
			}
			lastPhase = phase;
		}
		return rc;
	}
}

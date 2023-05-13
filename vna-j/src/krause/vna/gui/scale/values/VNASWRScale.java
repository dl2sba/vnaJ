/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 */
package krause.vna.gui.scale.values;

import java.awt.Color;
import java.awt.Graphics;

import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.scale.VNAGenericScale;
import krause.vna.gui.scale.VNAScaleSymbols.SCALE_TYPE;
import krause.vna.resources.VNAMessages;

public class VNASWRScale extends VNAGenericScale {

	public static final double ABSOLUTE_MIN = 1.0;
	public static final double ABSOLUTE_MAX = 50.0;
	public static final double DEFAULT_MIN = 1.0;
	public static final double DEFAULT_MAX = 5.0;

	private double swr2Relative(double swr) {
		return Math.log10(swr);
	}

	private double relative2Swr(double relative) {
		return Math.pow(10.0, relative);
	}

	public VNASWRScale() {
		super(VNAMessages.getString("Scale.SWR"), VNAMessages.getString("Scale.SWR.Description"), SCALE_TYPE.SCALE_SWR, null, VNAFormatFactory.getSwrFormat(), ABSOLUTE_MIN, ABSOLUTE_MAX);
	}

	@Override
	public int getScaledSampleValue(VNACalibratedSample sample, int height) {
		return getScaledSampleValue(sample.getSWR(), height);
	}

	@Override
	public int getScaledSampleValue(double swr, int height) {
		int rc = 0;
		double newSwr = swr;

		// clip to available range
		if (swr > getCurrentMaxValue()) {
			newSwr = getCurrentMaxValue();
		} else if (swr < getCurrentMinValue()) {
			newSwr = getCurrentMinValue();
		}

		// map to linear scale
		double scaleVal = swr2Relative(newSwr);
		double relativeMin = swr2Relative(getCurrentMinValue());
		double relativeMax = swr2Relative(getCurrentMaxValue());
		double range = relativeMax - relativeMin;
		scaleVal -= relativeMin;
		scaleVal /= range;

		rc = height - 1 - (int) (height * scaleVal);

		return rc;
	}

	public void paintScale(int width, int height, Graphics g) {
		final String methodName = "paintScale";
		TraceHelper.entry(this, methodName);
		TraceHelper.text(this, methodName, "ScaleMax=%d", getCurrentMaxValue());
		TraceHelper.text(this, methodName, "ScaleMin=%d", getCurrentMinValue());
		TraceHelper.text(this, methodName, "RangeScale=%d", getRange());
		TraceHelper.text(this, methodName, "NoOfTicks=%d", getNoOfTicks());

		height -= 1;

		g.setColor(Color.BLACK);
		g.setFont(g.getFont().deriveFont((float) 10));
		//
		int[] tc = new int[getNoOfTicks() + 1];
		setTickCoordinates(tc);

		// now sort out the rest of the ticks
		// search for matching ticks from the well known
		double stepDiagram = height * 1.0 / getNoOfTicks();

		double relMinValue = swr2Relative(getCurrentMinValue());
		double relMaxValue = swr2Relative(getCurrentMaxValue());
		double relRange = relMaxValue - relMinValue;
		double relStep = relRange / getNoOfTicks();

		TraceHelper.text(this, methodName, "ScaleMaxRel=%d", relMaxValue);
		TraceHelper.text(this, methodName, "ScaleMinRel=%d", relMinValue);
		TraceHelper.text(this, methodName, "RangeRel=%d", relRange);
		TraceHelper.text(this, methodName, "StepRel=%d", relStep);

		for (int i = 0; i <= getNoOfTicks(); ++i) {
			int y = height - (int) (i * stepDiagram);
			g.drawLine(0, y, width, y);
			tc[i] = y;

			double curVal = relMinValue + i * relStep;
			curVal = relative2Swr(curVal);

			// top most tick label?
			if (i == getNoOfTicks()) {
				// yes
				// last tick label is printed below tick
				y += 9;
			}
			g.drawString(getFormat().format(curVal) + ":1", 1, y);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.gui.scale.VNAGenericScale#initScaleFromConfigOrDib(krause.vna.device.VNADeviceInfoBlock,
	 * krause.vna.config.VNAConfig)
	 */
	public void initScaleFromConfigOrDib(VNADeviceInfoBlock block, VNAConfig config) {
		TraceHelper.entry(this, "initScaleFromConfigOrDib");
		setAbsolutMinValue(ABSOLUTE_MIN);
		setAbsolutMaxValue(ABSOLUTE_MAX);

		setDefaultMinValue(config.getDouble(this.getClass().getSimpleName() + ".defaultMinValue", DEFAULT_MIN));
		setDefaultMaxValue(config.getDouble(this.getClass().getSimpleName() + ".defaultMaxValue", DEFAULT_MAX));

		resetDefault();
		TraceHelper.exit(this, "initScaleFromConfigOrDib");
	}

	@Override
	public boolean supportsCustomScaling() {
		return true;
	}

}

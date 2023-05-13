/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 */
package krause.vna.data.reference;

import java.awt.Color;
import java.awt.Graphics;

import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.gui.scale.VNAGenericScale;

public class VNAReferenceDataHelper {

	/**
	 * @param g
	 * @param scaleLeft
	 * @param scaleRight
	 * @param height
	 * @param startFreq
	 * @param stopFreq
	 */
	public static void paint(Graphics g, VNAGenericScale scaleLeft, VNAGenericScale scaleRight, int width, int height, VNACalibratedSample[] resizedSamples) {
		TraceHelper.entry(VNAReferenceDataHelper.class, "paint");

		if (resizedSamples != null) {
			int endIndex = resizedSamples.length;
			if (endIndex > 1) {
				TraceHelper.text(VNAReferenceDataHelper.class, "paint", "painting ...");

				Color color = VNAConfig.getSingleton().getColorReference();

				int lastX = 0;
				int lastY1 = 0;

				for (int currX = 0; currX < endIndex; ++currX) {
					VNACalibratedSample currSample = resizedSamples[currX];

					if (currSample != null) {
						int currY1 = scaleLeft.getScaledSampleValue(currSample, height);
						// draw it
						// draw it
						g.setColor(color);
						g.drawLine(lastX, lastY1, currX, currY1);
						//
						lastX = currX;
						lastY1 = currY1;
					}
				}
				TraceHelper.text(VNAReferenceDataHelper.class, "paint", "... done");
			}
		}
		TraceHelper.exit(VNAReferenceDataHelper.class, "paint");
	}

}

package krause.vna.gui.scale.values;

import java.awt.Graphics;
import java.text.NumberFormat;

import krause.vna.gui.scale.VNAGenericScale;
import krause.vna.gui.scale.VNAScaleSymbols.SCALE_TYPE;

public abstract class VNALinearScale extends VNAGenericScale {

	public VNALinearScale(String scaleName, String scaleDescription, SCALE_TYPE scaleType, String pUnit, NumberFormat pFormat, double absMinVal, double absMaxVal) {
		super(scaleName, scaleDescription, scaleType, pUnit, pFormat, absMinVal, absMaxVal);
	}

	@Override
	public void paintScale(final int width, int height, final Graphics g) {
		g.setColor(getFontColor());
		g.setFont(getFont());
		//
		// range = 0 .. height-1
		height -= 1;
		//
		final int nOfTicks = getNoOfTicks();
		final int[] tickCoordinates = new int[nOfTicks + 1];
		setTickCoordinates(tickCoordinates);
		//
		double scale = (height * 1.0) / nOfTicks;
		double addi = getRange() / nOfTicks;
		//
		double val = getCurrentMaxValue();
		for (int i = 0; i <= nOfTicks; ++i) {
			final int y = (int) (scale * i);
			tickCoordinates[i] = y;
			g.drawLine(0, y, width, y);
			if (i == 0) {
				g.drawString(getFormat().format(val), 1, y + 10);
			} else {
				g.drawString(getFormat().format(val), 1, y - 2);
			}
			val -= addi;
		}
	}

}

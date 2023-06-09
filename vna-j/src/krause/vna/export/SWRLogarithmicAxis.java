package krause.vna.export;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.List;

import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberTick;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;

public class SWRLogarithmicAxis extends LogarithmicAxis {

	public SWRLogarithmicAxis(String string) {
		super(string);
	}

	/**
	 * Calculates the positions of the tick labels for the axis, storing the
	 * results in the tick label list (ready for drawing).
	 * 
	 * @param g2
	 *            the graphics device.
	 * @param dataArea
	 *            the area in which the plot should be drawn.
	 * @param edge
	 *            the location of the axis.
	 * 
	 * @return A list of ticks.
	 */
	protected List<NumberTick> refreshTicksVertical(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {

		List<NumberTick> ticks = new java.util.ArrayList<NumberTick>();

		// get lower bound value:
		double lowerBoundVal = getRange().getLowerBound();
		// if small log values and lower bound value too small
		// then set to a small value (don't allow <= 0):
		if (this.smallLogFlag && lowerBoundVal < SMALL_LOG_VALUE) {
			lowerBoundVal = SMALL_LOG_VALUE;
		}
		// get upper bound value
		double upperBoundVal = getRange().getUpperBound();

		// get log10 version of lower bound and round to integer:
		int iBegCount = (int) Math.rint(switchedLog10(lowerBoundVal));
		// get log10 version of upper bound and round to integer:
		// int iEndCount = (int) Math.rint(switchedLog10(upperBoundVal));
		int iEndCount = (int) Math.ceil(switchedLog10(upperBoundVal));

		if (iBegCount == iEndCount && iBegCount > 0 && Math.pow(10, iBegCount) > lowerBoundVal) {
			// only 1 power of 10 value, it's > 0 and its resulting
			// tick value will be larger than lower bound of data
			--iBegCount; // decrement to generate more ticks
		}

		double tickVal;
		String tickLabel;
		boolean zeroTickFlag = false;
		for (int i = iBegCount; i <= iEndCount; i++) {
			// for each tick with a label to be displayed
			int jEndCount = 10;
			if (i == iEndCount) {
				jEndCount = 1;
			}

			for (int j = 0; j < jEndCount; j++) {
				// for each tick to be displayed
				if (this.smallLogFlag) {
					// small log values in use
					tickVal = Math.pow(10, i) + (Math.pow(10, i) * j);
					if (j == 0) {
						// first tick of group; create label text
						if (this.log10TickLabelsFlag) {
							// if flag then
							tickLabel = "10^" + i; // create "log10"-type label
						} else { // not "log10"-type label
							if (this.expTickLabelsFlag) {
								// if flag then
								tickLabel = "1e" + i; // create "1e#"-type label
							} else { // not "1e#"-type label
								if (i >= 0) { // if positive exponent then
									// make integer
									NumberFormat format = getNumberFormatOverride();
									if (format != null) {
										tickLabel = format.format(tickVal);
									} else {
										tickLabel = Long.toString((long) Math.rint(tickVal));
									}
								} else {
									// negative exponent; create fractional
									// value
									// set exact number of fractional digits to
									// be shown:
									this.numberFormatterObj.setMaximumFractionDigits(-i);
									// create tick label:
									tickLabel = this.numberFormatterObj.format(tickVal);
								}
							}
						}
					} else { // not first tick to be displayed
						NumberFormat format = getNumberFormatOverride();
						if (format != null) {
							tickLabel = format.format(tickVal);
						} else {
							tickLabel = Long.toString((long) Math.rint(tickVal));
						}
						// tickLabel = ""; // no tick label
					}
				} else { // not small log values in use; allow for values <= 0
					if (zeroTickFlag) { // if did zero tick last iter then
						--j;
					} // decrement to do 1.0 tick now
					tickVal = (i >= 0) ? Math.pow(10, i) + (Math.pow(10, i) * j) : -(Math.pow(10, -i) - (Math.pow(10, -i - 1) * j));
					if (j == 0) { // first tick of group
						if (!zeroTickFlag) { // did not do zero tick last
							// iteration
							if (i > iBegCount && i < iEndCount && Math.abs(tickVal - 1.0) < 0.0001) {
								// not first or last tick on graph and value
								// is 1.0
								tickVal = 0.0; // change value to 0.0
								zeroTickFlag = true; // indicate zero tick
								tickLabel = "0"; // create label for tick
							} else {
								// first or last tick on graph or value is 1.0
								// create label for tick:
								if (this.log10TickLabelsFlag) {
									// create "log10"-type label
									tickLabel = (((i < 0) ? "-" : "") + "10^" + Math.abs(i));
								} else {
									if (this.expTickLabelsFlag) {
										// create "1e#"-type label
										tickLabel = (((i < 0) ? "-" : "") + "1e" + Math.abs(i));
									} else {
										NumberFormat format = getNumberFormatOverride();
										if (format != null) {
											tickLabel = format.format(tickVal);
										} else {
											tickLabel = Long.toString((long) Math.rint(tickVal));
										}
									}
								}
							}
						} else { // did zero tick last iteration
							tickLabel = ""; // no label
							zeroTickFlag = false; // clear flag
						}
					} else { // not first tick of group
						tickLabel = ""; // no label
						zeroTickFlag = false; // make sure flag cleared
					}
				}

				if (tickVal > upperBoundVal) {
					return ticks; // if past highest data value then exit method
				}

				if (tickVal >= lowerBoundVal - SMALL_LOG_VALUE) {
					// tick value not below lowest data value
					TextAnchor anchor = null;
					TextAnchor rotationAnchor = null;
					double angle = 0.0;
					if (isVerticalTickLabels()) {
						if (edge == RectangleEdge.LEFT) {
							anchor = TextAnchor.BOTTOM_CENTER;
							rotationAnchor = TextAnchor.BOTTOM_CENTER;
							angle = -Math.PI / 2.0;
						} else {
							anchor = TextAnchor.BOTTOM_CENTER;
							rotationAnchor = TextAnchor.BOTTOM_CENTER;
							angle = Math.PI / 2.0;
						}
					} else {
						if (edge == RectangleEdge.LEFT) {
							anchor = TextAnchor.CENTER_RIGHT;
							rotationAnchor = TextAnchor.CENTER_RIGHT;
						} else {
							anchor = TextAnchor.CENTER_LEFT;
							rotationAnchor = TextAnchor.CENTER_LEFT;
						}
					}
					// create tick object and add to list:
					ticks.add(new NumberTick(Double.valueOf(tickVal), tickLabel, anchor, rotationAnchor, angle));
				}
			}
		}
		return ticks;
	}

}

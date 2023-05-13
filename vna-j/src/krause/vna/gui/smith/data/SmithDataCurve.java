package krause.vna.gui.smith.data;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import krause.vna.data.calibrated.VNACalibratedSample;

public class SmithDataCurve {
	private int numLines;
	private Line2D[] lines = null;
	private VNACalibratedSample[] samples;

	public SmithDataCurve(int points) {
		super();
		numLines = points - 1;
		lines = new Line2D[numLines];
	}

	public void addPoints(Point2D[] newPoints) {
		int len = newPoints.length;
		if (len > 1) {
			Point2D lp = newPoints[0];
			for (int i = 1; i < len; ++i) {
				Point2D np = newPoints[i];
				lines[i - 1] = new Line2D.Double(lp, np);
				lp = np;
			}
		}
	}

	public Line2D[] getLines() {
		return lines;
	}

	public void setLines(Line2D[] lines) {
		this.lines = lines;
	}

	public void setSamples(VNACalibratedSample[] samples) {
		this.samples = samples;
	}

	public VNACalibratedSample[] getSamples() {
		return samples;
	}
}

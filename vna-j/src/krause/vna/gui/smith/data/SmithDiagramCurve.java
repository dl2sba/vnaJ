package krause.vna.gui.smith.data;

import java.awt.Polygon;

import krause.vna.data.calibrated.VNACalibratedSample;

public class SmithDiagramCurve extends Polygon {
	public SmithDiagramCurve() {
		super();
	}

	public SmithDiagramCurve(int[] xpoints, int[] ypoints, int npoints) {
		super(xpoints, ypoints, npoints);
	}

	private String label;
	private boolean realCurve;
	private VNACalibratedSample[] samples;

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param realCurve the realCurve to set
	 */
	public void setRealCurve(boolean realCurve) {
		this.realCurve = realCurve;
	}

	/**
	 * @return the realCurve
	 */
	public boolean isRealCurve() {
		return realCurve;
	}

	public VNACalibratedSample[] getSamples() {
		return samples;
	}

	public void setSamples(VNACalibratedSample[] samples) {
		this.samples = samples;
	}
}

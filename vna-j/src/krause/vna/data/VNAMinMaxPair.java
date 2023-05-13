package krause.vna.data;

import java.io.Serializable;

import org.jdom.Element;

import krause.vna.gui.scale.VNAScaleSymbols.SCALE_TYPE;

public class VNAMinMaxPair implements Serializable {

	private double minValue = Double.MAX_VALUE;
	private double maxValue = -Double.MAX_VALUE;
	private int minIndex = -1;
	private int maxIndex = -1;
	private SCALE_TYPE type;

	public VNAMinMaxPair(SCALE_TYPE ptype) {
		super();
		this.setType(ptype);
	}

	public VNAMinMaxPair() {
		super();
	}

	public VNAMinMaxPair(double pMin, double pMax) {
		super();
		minValue = pMin;
		maxValue = pMax;
	}

	/**
	 * 
	 * @param pair
	 */
	public void consume(VNAMinMaxPair pair) {
		consume(pair.getMinValue(), pair.getMinIndex());
		consume(pair.getMaxValue(), pair.getMaxIndex());
	}

	/**
	 * Compare the given value val against the min and max values and eventually
	 * update these to val. Store the given index idx that either to minIndex or
	 * maxIndex.
	 * 
	 * @param val
	 * @param idx
	 */
	public void consume(double val, int idx) {
		if (val < minValue) {
			minValue = val;
			minIndex = idx;
		}
		if (val > maxValue) {
			maxValue = val;
			maxIndex = idx;
		}
	}

	public double getMinValue() {
		return minValue;
	}

	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

	public int getMinIndex() {
		return minIndex;
	}

	public void setMinIndex(int minIndex) {
		this.minIndex = minIndex;
	}

	public int getMaxIndex() {
		return maxIndex;
	}

	public void setMaxIndex(int maxIndex) {
		this.maxIndex = maxIndex;
	}

	public void setType(SCALE_TYPE type) {
		this.type = type;
	}

	public SCALE_TYPE getType() {
		return type;
	}

	public Element asElement() {
		Element rc = new Element(getType().toString());
		rc.addContent(new Element("min").setText(Double.toString(getMinValue())));
		rc.addContent(new Element("max").setText(Double.toString(getMaxValue())));
		rc.addContent(new Element("minindex").setText(Integer.toString(getMinIndex())));
		rc.addContent(new Element("maxindex").setText(Integer.toString(getMaxIndex())));
		return rc;
	}

	public static VNAMinMaxPair fromElement(Element root, String name) {
		VNAMinMaxPair rc = new VNAMinMaxPair();
		Element e = root.getChild(name);
		if (e != null) {
			rc.setMinValue(Double.parseDouble(e.getChildText("min")));
			rc.setMaxValue(Double.parseDouble(e.getChildText("max")));
			rc.setMinIndex(Integer.parseInt(e.getChildText("minindex")));
			rc.setMaxIndex(Integer.parseInt(e.getChildText("maxindex")));
		}
		return rc;
	}
}

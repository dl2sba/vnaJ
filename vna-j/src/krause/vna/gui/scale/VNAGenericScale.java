/**
 * Copyright (C) 2020 Dietmar Krause, DL2SBA
 */
package krause.vna.gui.scale;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.text.NumberFormat;

import javax.swing.UIManager;

import krause.common.TypedProperties;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNAMinMaxPair;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.gui.scale.VNAScaleSymbols.SCALE_TYPE;

public abstract class VNAGenericScale {
	private double absolutMaxValue = Double.MAX_VALUE;
	private double absolutMinValue = Double.MIN_VALUE;
	private double currentMaxValue = Double.MAX_VALUE;

	private double currentMinValue = Double.MIN_VALUE;
	private double defaultMaxValue = Double.MAX_VALUE;
	private double defaultMinValue = Double.MIN_VALUE;

	private Double guideLineValue = null;

	private String description = null;

	private final Font font = new java.awt.Font("Dialog", java.awt.Font.PLAIN, 10);
	private final Color fontColor = UIManager.getColor("Panel.foreground");
	private NumberFormat format = null;

	private String name = null;

	private int noOfTicks = 10;

	private double range = 1;

	private int[] tickCoordinates = new int[0];

	private SCALE_TYPE type = SCALE_TYPE.SCALE_NONE;

	private String unit = null;

	public VNAGenericScale(String scaleName, String scaleDescription, SCALE_TYPE scaleType, String pUnit, NumberFormat pFormat, double absMinVal, double absMaxVal) {
		name = scaleName;
		setDescription(scaleDescription);
		type = scaleType;
		unit = pUnit;
		format = pFormat;
		absolutMaxValue = absMaxVal;
		absolutMinValue = absMinVal;
		resetDefault();
	}

	public double getAbsolutMaxValue() {
		return absolutMaxValue;
	}

	public double getAbsolutMinValue() {
		return absolutMinValue;
	}

	public double getCurrentMaxValue() {
		return currentMaxValue;
	}

	public double getCurrentMinValue() {
		return currentMinValue;
	}

	public VNAMinMaxPair getCurrentMinMaxValue() {
		return new VNAMinMaxPair(currentMinValue, currentMaxValue);
	}

	public double getDefaultMaxValue() {
		return defaultMaxValue;
	}

	public double getDefaultMinValue() {
		return defaultMinValue;
	}

	public String getDescription() {
		return description;
	}

	public Font getFont() {
		return font;
	}

	public NumberFormat getFormat() {
		return format;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the noOfTicks
	 */
	public int getNoOfTicks() {
		return noOfTicks;
	}

	/**
	 * @return the range
	 */
	public double getRange() {
		return range;
	}

	public abstract int getScaledSampleValue(double value, int height);

	public abstract int getScaledSampleValue(VNACalibratedSample sample, int height);

	/**
	 * Used to draw the dotted lines in the main diagram
	 * 
	 * @return
	 */
	public int[] getTickCoordinates() {
		return tickCoordinates;
	}

	/**
	 * @return Returns the type.
	 */
	public SCALE_TYPE getType() {
		return type;
	}

	public String getUnit() {
		return unit;
	}

	/**
	 * 
	 * @param block
	 */
	public abstract void initScaleFromConfigOrDib(VNADeviceInfoBlock block, VNAConfig config);

	public abstract void paintScale(int width, int height, Graphics g);

	/**
	 * 
	 */
	public void rescale() {
		if ((currentMaxValue > 0) && (currentMinValue >= 0)) {
			range = currentMaxValue - currentMinValue;
		} else if ((currentMaxValue > 0) && (currentMinValue < 0)) {
			range = currentMaxValue - currentMinValue;
		} else {
			range = -currentMinValue + currentMaxValue;
		}
	}

	public final void resetDefault() {
		currentMaxValue = defaultMaxValue;
		currentMinValue = defaultMinValue;
		rescale();
	}

	public void restoreFromProperties(TypedProperties props) {
		currentMaxValue = props.getDouble("" + type + ".currentMaxValue", defaultMaxValue);
		currentMinValue = props.getDouble("" + type + ".currentMinValue", defaultMinValue);
	}

	public void saveToProperties(TypedProperties props) {
		props.putDouble("" + type + ".currentMaxValue", currentMaxValue);
		props.putDouble("" + type + ".currentMinValue", currentMinValue);
	}

	public void setAbsolutMaxValue(double absolutMaxValue) {
		this.absolutMaxValue = absolutMaxValue;
	}

	public void setAbsolutMinValue(double absolutMinValue) {
		this.absolutMinValue = absolutMinValue;
	}

	public final void setCurrentMaxValue(double maxValue) {
		if (maxValue > absolutMaxValue) {
			this.currentMaxValue = absolutMaxValue;
		} else if (maxValue < absolutMinValue) {
			this.currentMaxValue = absolutMinValue;
		} else {
			this.currentMaxValue = maxValue;
		}
	}

	public final void setCurrentMinValue(double minValue) {
		if (minValue < absolutMinValue) {
			this.currentMinValue = absolutMinValue;
		} else if (minValue > absolutMaxValue) {
			this.currentMinValue = absolutMaxValue;
		} else {
			this.currentMinValue = minValue;
		}
	}

	public void setCurrentMinMaxValue(VNAMinMaxPair mm) {
		if (mm.getMaxValue() > getAbsolutMaxValue()) {
			currentMaxValue = getAbsolutMaxValue();
		} else {
			currentMaxValue = mm.getMaxValue();
		}
		if (mm.getMinValue() < getAbsolutMinValue()) {
			currentMinValue = getAbsolutMinValue();
		} else {
			currentMinValue = mm.getMinValue();
		}
	}

	public final void setDefaultMaxValue(double defaultMaxValue) {
		this.defaultMaxValue = defaultMaxValue;
	}

	public final void setDefaultMinValue(double defaultMinValue) {
		this.defaultMinValue = defaultMinValue;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setFormat(NumberFormat format) {
		this.format = format;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param noOfTicks
	 *            the noOfTicks to set
	 */
	public void setNoOfTicks(int noOfTicks) {
		this.noOfTicks = noOfTicks;
	}

	/**
	 * Used to draw the dotted lines in the main diagram
	 * 
	 * @param tickCoordinates
	 */
	public void setTickCoordinates(int[] tickCoordinates) {
		this.tickCoordinates = tickCoordinates;
	}

	public void setType(SCALE_TYPE type) {
		this.type = type;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public boolean supportsCustomScaling() {
		return false;
	}

	public String toString() {
		if (unit != null) {
			return name + " (" + unit + ")";
		} else {
			return name;
		}
	}

	public Color getFontColor() {
		return fontColor;
	}

	public Double getGuideLineValue() {
		return guideLineValue;
	}

	public void setGuideLineValue(Double glv) {
		this.guideLineValue = glv;
	}

	public String getFormattedValueAsString(double val) {
		return getFormat().format(val);
	}

	public String getFormattedValueAsStringWithUnit(double val) {
		if (getUnit() != null) {
			return getFormat().format(val) + getUnit();
		} else {
			return getFormat().format(val);
		}
	}

}

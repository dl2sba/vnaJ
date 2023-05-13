/**
 * Copyright (C) 2011 Dietmar Krause, DL2SBA
 *
 *	This file: VNARssPair.java
 *  Part of:   vna-j-max6
 */

package krause.vna.device.serial.max6;

/**
 * @author Dietmar
 * 
 */
public class VNARssPair {

	public VNARssPair(double offset, double scale) {
		super();
		this.offset = offset;
		this.scale = scale;
	}

	private double offset = 0.0;
	private double scale = 1.0;

	public double getOffset() {
		return offset;
	}

	public void setOffset(double offset) {
		this.offset = offset;
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}
}

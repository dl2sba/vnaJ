/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 *
 *	This file: VNACalibratedSample4.java
 *  Part of:   vna-j
 */

package krause.vna.data.calibrated;

/**
 * @author Dietmar
 * 
 */
public class VNACalibratedSample4 {
	private VNACalibratedSample min1;
	private VNACalibratedSample min2;
	private VNACalibratedSample max1;
	private VNACalibratedSample max2;

	public VNACalibratedSample4(boolean scale360) {
		max1 = new VNACalibratedSample();
		max2 = new VNACalibratedSample();
		min1 = new VNACalibratedSample();
		min2 = new VNACalibratedSample();

		if (scale360) {
			max1.setReflectionPhase(-Double.MAX_VALUE);
			max2.setReflectionPhase(-Double.MAX_VALUE);
			min1.setReflectionPhase(Double.MAX_VALUE);
			min2.setReflectionPhase(Double.MAX_VALUE);
		} else {
			max1.setReflectionPhase(0);
			max2.setReflectionPhase(0);
			min1.setReflectionPhase(Double.MAX_VALUE);
			min2.setReflectionPhase(Double.MAX_VALUE);

		}
	}

	public VNACalibratedSample getMin1() {
		return min1;
	}

	public void setMin1(VNACalibratedSample min1) {
		this.min1 = min1;
	}

	public VNACalibratedSample getMin2() {
		return min2;
	}

	public void setMin2(VNACalibratedSample min2) {
		this.min2 = min2;
	}

	public VNACalibratedSample getMax1() {
		return max1;
	}

	public void setMax1(VNACalibratedSample max1) {
		this.max1 = max1;
	}

	public VNACalibratedSample getMax2() {
		return max2;
	}

	public void setMax2(VNACalibratedSample max2) {
		this.max2 = max2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "VNACalibratedSample4 [max1=" + max1 + ", max2=" + max2 + ", min1=" + min1 + ", min2=" + min2 + "]";
	}
}

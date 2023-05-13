package krause.vna.data;

public class VNABaseSample4 {
	private VNABaseSample min1;
	private VNABaseSample min2;
	private VNABaseSample max1;
	private VNABaseSample max2;

	public VNABaseSample4() {
		max1 = new VNABaseSample(Integer.MIN_VALUE, 0, 0);
		max2 = new VNABaseSample(Integer.MIN_VALUE, 0, 0);
		min1 = new VNABaseSample(Integer.MAX_VALUE, 0, 0);
		min2 = new VNABaseSample(Integer.MAX_VALUE, 0, 0);
	}

	/**
	 * @return the min1
	 */
	public VNABaseSample getMin1() {
		return min1;
	}

	/**
	 * @param min1
	 *            the min1 to set
	 */
	public void setMin1(VNABaseSample min1) {
		this.min1 = min1;
	}

	/**
	 * @return the min2
	 */
	public VNABaseSample getMin2() {
		return min2;
	}

	/**
	 * @param min2
	 *            the min2 to set
	 */
	public void setMin2(VNABaseSample min2) {
		this.min2 = min2;
	}

	/**
	 * @return the max1
	 */
	public VNABaseSample getMax1() {
		return max1;
	}

	/**
	 * @param max1
	 *            the max1 to set
	 */
	public void setMax1(VNABaseSample max1) {
		this.max1 = max1;
	}

	/**
	 * @return the max2
	 */
	public VNABaseSample getMax2() {
		return max2;
	}

	/**
	 * @param max2
	 *            the max2 to set
	 */
	public void setMax2(VNABaseSample max2) {
		this.max2 = max2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "VNABaseSample4 [max1=" + max1 + ", max2=" + max2 + ", min1=" + min1 + ", min2=" + min2 + "]";
	}
}

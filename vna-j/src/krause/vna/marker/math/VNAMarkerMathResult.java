package krause.vna.marker.math;

public class VNAMarkerMathResult extends VNAMarkerMathInput {

	private long bandWidth;
	private double q;
	private double Rp;
	private double Xp;

	private double serialCapacity;
	private double serialInductance;
	private double parallelCapacity;
	private double parallelInductance;

	public VNAMarkerMathResult(VNAMarkerMathInput inp) {
		setCenterFrequency(inp.getCenterFrequency());
		setHighFrequency(inp.getHighFrequency());
		setLowFrequency(inp.getLowFrequency());
		setRs(inp.getRs());
		setXs(inp.getXs());
		setZ(inp.getZ());
	}

	public double getParallelCapacity() {
		return parallelCapacity;
	}

	public void setParallelCapacity(double parallelCapacity) {
		this.parallelCapacity = parallelCapacity;
	}

	public double getParallelInductance() {
		return parallelInductance;
	}

	public void setParallelInductance(double parallelInductance) {
		this.parallelInductance = parallelInductance;
	}

	/**
	 * @return the bandWidth
	 */
	public long getBandWidth() {
		return bandWidth;
	}

	/**
	 * @param bandWidth
	 *            the bandWidth to set
	 */
	public void setBandWidth(long bandWidth) {
		this.bandWidth = bandWidth;
	}

	/**
	 * @return the q
	 */
	public double getQ() {
		return q;
	}

	/**
	 * @param q
	 *            the q to set
	 */
	public void setQ(double q) {
		this.q = q;
	}

	/**
	 * @param serialCapacity
	 *            the serialCapacity to set
	 */
	public void setSerialCapacity(double serialCapacity) {
		this.serialCapacity = serialCapacity;
	}

	/**
	 * @return the serialCapacity
	 */
	public double getSerialCapacity() {
		return serialCapacity;
	}

	/**
	 * @param serialInductance
	 *            the serialInductance to set
	 */
	public void setSerialInductance(double serialInductance) {
		this.serialInductance = serialInductance;
	}

	/**
	 * @return the serialInductance
	 */
	public double getSerialInductance() {
		return serialInductance;
	}

	/**
	 * @param rp
	 *            the rp to set
	 */
	public void setRp(double rp) {
		Rp = rp;
	}

	/**
	 * @param xp
	 *            the xp to set
	 */
	public void setXp(double xp) {
		Xp = xp;
	}

	/**
	 * @return the rp
	 */
	public double getRp() {
		return Rp;
	}

	/**
	 * @return the xp
	 */
	public double getXp() {
		return Xp;
	}

}

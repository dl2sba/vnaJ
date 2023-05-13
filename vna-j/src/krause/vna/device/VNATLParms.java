package krause.vna.device;

import org.apache.commons.math3.complex.Complex;

public class VNATLParms {
	private Complex z0;
	private double correctedVf;
	private double loss;

	public Complex getZ0() {
		return z0;
	}

	public void setZ0(Complex z0) {
		this.z0 = z0;
	}

	public double getCorrectedVf() {
		return correctedVf;
	}

	public void setCorrectedVf(double correctedVf) {
		this.correctedVf = correctedVf;
	}

	public double getLoss() {
		return loss;
	}

	public void setLoss(double loss) {
		this.loss = loss;
	}
}

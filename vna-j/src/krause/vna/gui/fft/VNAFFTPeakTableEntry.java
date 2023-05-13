package krause.vna.gui.fft;

public class VNAFFTPeakTableEntry {
	public VNAFFTPeakTableEntry(int bin, double value, double length) {
		super();
		this.bin = bin;
		this.value = value;
		this.length = length;
	}

	private int bin;
	private double value;
	private double length;

	public int getBin() {
		return bin;
	}

	public void setBin(int bin) {
		this.bin = bin;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}
}

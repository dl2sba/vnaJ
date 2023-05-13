package krause.vna.background;

import krause.vna.data.VNAFrequencyRange;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.VNAScanMode;
import krause.vna.device.VNADeviceInfoBlock;

public class VNABackgroundJob {
	private int average = 1;
	private VNAFrequencyRange frequencyRange;
	private int numberOfSamples;
	private VNASampleBlock result = null;
	private VNAScanMode scanMode;
	private int speedup = 1; 
	private int overScan = 1;

	public int getAverage() {
		return average;
	}

	public VNAFrequencyRange getFrequencyRange() {
		return frequencyRange;
	}

	public int getNumberOfSamples() {
		return numberOfSamples;
	}

	public VNASampleBlock getResult() {
		return result;
	}

	public VNAScanMode getScanMode() {
		return scanMode;
	}

	public int getSpeedup() {
		return speedup;
	}

	public void setAverage(int average) {
		this.average = average;
	}

	public void setFrequencyRange(VNADeviceInfoBlock dib) {
		frequencyRange = new VNAFrequencyRange(dib.getMinFrequency(), dib.getMaxFrequency());
	}

	public void setFrequencyRange(VNAFrequencyRange frequencyRange) {
		this.frequencyRange = frequencyRange;
	}

	public void setNumberOfSamples(int numberOfSamples) {
		this.numberOfSamples = numberOfSamples;
	}

	public void setNumberOfSamples(VNADeviceInfoBlock dib) {
		numberOfSamples = dib.getNumberOfSamples4Calibration();
	}

	public void setResult(VNASampleBlock result) {
		this.result = result;
	}

	public void setScanMode(VNAScanMode scanMode) {
		this.scanMode = scanMode;
	}

	public void setSpeedup(int speedup) {
		this.speedup = speedup;
	}

	public int getOverScan() {
		return overScan;
	}

	public void setOverScan(int overScan) {
		this.overScan = overScan;
	}
}

package krause.vna.gui.multiscan;

import krause.vna.background.VNABackgroundJob;
import krause.vna.data.VNAFrequencyRange;

public class VNAMultiScanBackgroundJob extends VNABackgroundJob {
	private VNAMultiScanResult resultWindow;

	public VNAMultiScanBackgroundJob(VNAMultiScanResult pResultWindow) {
		resultWindow = pResultWindow;

		setNumberOfSamples(500);
		setSpeedup(1);
		setFrequencyRange(new VNAFrequencyRange(pResultWindow.getStartFrequency(), pResultWindow.getStopFrequency()));
		setScanMode(pResultWindow.getScanMode());
	}

	public VNAMultiScanResult getResultWindow() {
		return resultWindow;
	}

}

package krause.vna.data.observer;

import krause.vna.data.calibrated.VNACalibrationBlock;

public interface VNACalibrationBlockObserver {
	public void blockChanged(VNACalibrationBlock oldBlock, VNACalibrationBlock newBlock);
}

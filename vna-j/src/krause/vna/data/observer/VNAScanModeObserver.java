package krause.vna.data.observer;

import krause.vna.data.VNAScanMode;

public interface VNAScanModeObserver extends VNAObserver {
	public void changeMode(VNAScanMode oldMode, VNAScanMode newMode);

}

package krause.vna.data.observer;

import krause.vna.data.VNAApplicationState.INNERSTATE;

public interface VNAApplicationStateObserver {
	public void changeState(INNERSTATE oldState, INNERSTATE newState);
}

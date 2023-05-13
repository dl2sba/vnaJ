package krause.vna.data.observer;

import krause.vna.data.VNAFrequencyRange;

public interface VNAFrequencyRangeObserver extends VNAObserver {

	public void changeRange(VNAFrequencyRange oldRange, VNAFrequencyRange newRange);

}

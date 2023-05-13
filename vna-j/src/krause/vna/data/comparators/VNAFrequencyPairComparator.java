package krause.vna.data.comparators;

import java.util.Comparator;

import krause.vna.gui.util.VNAFrequencyPair;

public class VNAFrequencyPairComparator implements Comparator<VNAFrequencyPair> {
	public int compare(VNAFrequencyPair f1, VNAFrequencyPair f2) {
		if ((f1 == null) || (f2 == null)) {
			return 0;
		}
		if (f1.getStartFrequency() > f2.getStartFrequency()) {
			return 1;
		} else if (f1.getStartFrequency() < f2.getStartFrequency()) {
			return -1;
		} else {
			if (f1.getStopFrequency() > f2.getStopFrequency()) {
				return 1;
			} else if (f1.getStopFrequency() < f2.getStopFrequency()) {
				return -1;
			} else {
				return 0;
			}
		}
	}
}

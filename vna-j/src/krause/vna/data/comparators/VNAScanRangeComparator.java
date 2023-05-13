package krause.vna.data.comparators;

import java.util.Comparator;

import krause.vna.device.VNAScanRange;

public class VNAScanRangeComparator implements Comparator<VNAScanRange> {
	public int compare(VNAScanRange f1, VNAScanRange f2) {
		if ((f1 == null) || (f2 == null)) {
			return 0;
		}
		if (f1.getStart() > f2.getStart()) {
			return 1;
		} else if (f1.getStart() < f2.getStart()) {
			return -1;
		} else {
			if (f1.getStop() > f2.getStop()) {
				return 1;
			} else if (f1.getStop() < f2.getStop()) {
				return -1;
			} else {
				return 0;
			}
		}
	}
}

package krause.vna.data.comparators;

import java.util.Comparator;

import krause.vna.gui.util.tables.VNAProperty;

public class VNAPropertyComparator implements Comparator<VNAProperty> {
	public int compare(VNAProperty object1, VNAProperty object2) {
		if ((object1 != null) && (object2 != null)) {
			String tKey1 = object1.getKey().toUpperCase();
			String tKey2 = object2.getKey().toUpperCase();
			return tKey1.compareTo(tKey2);
		}
		return 0;
	}
}

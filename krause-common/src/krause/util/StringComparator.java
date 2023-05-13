package krause.util;

import java.util.Comparator;

public class StringComparator implements Comparator<String> {

	@Override
	public int compare(String o1, String o2) {
		if (o1 != null) {
			return o1.compareTo(o2);
		} else if (o1 == null && o2 == null) {
			return 0;
		} else {
			return 1;
		}
	}

}

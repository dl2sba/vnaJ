package krause.vna.device;

import java.util.Comparator;

public class VNADriverNameComparator implements Comparator<IVNADriver> {

	@Override
	public int compare(IVNADriver o1, IVNADriver o2) {
		if (o1 == o2) {
			return 0;
		} else if (o1 == null) {
			return -1;
		} else if (o2 != null) {
			VNADeviceInfoBlock dib1 = o1.getDeviceInfoBlock();
			VNADeviceInfoBlock dib2 = o2.getDeviceInfoBlock();
			return dib1.getShortName().compareToIgnoreCase(dib2.getShortName());
		} else {
			return 1;
		}
	}

}

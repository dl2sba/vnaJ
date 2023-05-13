package krause.vna.data.reference;

import java.util.Comparator;

/*
 * 
 */
public class VNAReferenceDataComparator implements Comparator<VNAReferenceDataBlock> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(VNAReferenceDataBlock object1, VNAReferenceDataBlock object2) {
		if ((object1 != null) && (object2 != null)) {
			if ((object1.getFile() != null) && (object2.getFile() != null)) {
				return object1.getFile().getName().compareTo(object2.getFile().getName());
			}
		}
		return 0;
	}
}

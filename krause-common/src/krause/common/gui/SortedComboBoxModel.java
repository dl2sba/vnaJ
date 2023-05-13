package krause.common.gui;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;

@SuppressWarnings("rawtypes")
public class SortedComboBoxModel extends DefaultComboBoxModel {
	/**
	 * 
	 */
	public SortedComboBoxModel() {

	}

	@SuppressWarnings("unchecked")
	public SortedComboBoxModel(final Object[] itemArray) {
		super();
		Arrays.sort(itemArray);
		for (int i = 0; i < itemArray.length; i++) {
			super.addElement(itemArray[i]);
		}
	}

	public SortedComboBoxModel(Vector<Object> itemVector) {
		super();
		Iterator<Object> it = itemVector.iterator();
		while (it.hasNext()) {
			this.addElement(it.next());
		}
	}

	@SuppressWarnings("unchecked")
	public void addElement(Object o) {
		int min = 0;
		int max = this.getSize() - 1;
		int insertAt = -1;
		int compare = -2;
		while (min <= max) {
			Comparable inList = (Comparable) this.getElementAt(min);
			compare = inList.compareTo(o);
			if (compare > 0) {
				insertAt = min;
				break;
			}
			min++;
		}
		if (insertAt == -1)
			super.addElement(o);
		else
			super.insertElementAt(o, insertAt);
		this.setSelectedItem(o);
	}

}

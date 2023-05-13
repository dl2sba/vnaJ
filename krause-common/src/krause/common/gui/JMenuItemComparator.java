package krause.common.gui;

import java.util.Comparator;

import javax.swing.JMenuItem;

public class JMenuItemComparator implements Comparator<JMenuItem> {

	public int compare(JMenuItem o1, JMenuItem o2) {
		if ((o1 != null) && (o2 != null)) {
			String s1 = o1.getText();
			String s2 = o2.getText();
			if (s1 != null && s2 != null) {
				return s1.toUpperCase().compareTo(s2.toUpperCase());
			}
		}
		return 0;
	}

}

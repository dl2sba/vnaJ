package krause.vna.gui.panels.data;

import java.util.ArrayList;
import java.util.List;

import javax.swing.MutableComboBoxModel;
import javax.swing.event.ListDataListener;

import krause.vna.device.VNAScanModeParameter;

/**
 * A simple sorted combobox modell to display VNAScanMode entries
 * @author Dietmar
 *
 */
public class VNAScanModeComboBoxModel implements MutableComboBoxModel {

	private List<VNAScanModeParameter> modes = new ArrayList<VNAScanModeParameter>();
	private int selectedIndex = -1;

	public Object getSelectedItem() {
		Object rc = null;
		if (selectedIndex != -1) {
			if (modes != null) {
				if (selectedIndex >= 0 && selectedIndex < modes.size()) {
					rc = modes.get(selectedIndex);
				}
			}
		}
		return rc;
	}

	public void setSelectedItem(Object arg0) {
		if (modes != null) {
			selectedIndex = modes.indexOf(arg0);
		}
	}

	public void addListDataListener(ListDataListener arg0) {
	}

	public Object getElementAt(int arg0) {
		Object rc = null;
		if (arg0 >= 0 && arg0 < modes.size()) {
			rc = modes.get(arg0);
		}
		return rc;
	}

	public int getSize() {
		int rc = 0;
		if (modes != null) {
			rc = modes.size();
		}
		return rc;
	}

	public void removeListDataListener(ListDataListener arg0) {
	}

	/**
	 * This method adds the given entry in the lexical correct position in the list
	 * 
	 */
	public void addElement(Object arg0) {
		VNAScanModeParameter smp = (VNAScanModeParameter) arg0;
		if (modes.size() == 0) {
			modes.add(smp);
		} else if (smp.toString().compareToIgnoreCase(modes.get(0).toString()) <= 0) {
			modes.add(0, smp);
		} else if (smp.toString().compareToIgnoreCase(modes.get(modes.size() - 1).toString()) >= 0) {
			modes.add(smp);
		} else {
			int i = 0;
			while (smp.toString().compareToIgnoreCase(modes.get(i).toString()) > 0) {
				++i;
			}
			modes.add(i,smp);
		}
	}

	public void insertElementAt(Object arg0, int arg1) {
		modes.add(arg1, (VNAScanModeParameter) arg0);
	}

	public void removeElement(Object arg0) {
		modes.remove(arg0);
	}

	public void removeElementAt(int arg0) {
		modes.remove(arg0);
	}

	public List<VNAScanModeParameter> getModes() {
		return modes;
	}

}

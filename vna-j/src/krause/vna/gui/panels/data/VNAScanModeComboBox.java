package krause.vna.gui.panels.data;

import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;

import krause.vna.data.VNAScanMode;
import krause.vna.device.VNAScanModeParameter;

public class VNAScanModeComboBox extends JComboBox<VNAScanModeParameter> {

	@SuppressWarnings("unchecked")
	public VNAScanModeComboBox() {
		super();
		setModel(new VNAScanModeComboBoxModel());
		setEditable(false);
	}

	public VNAScanMode getSelectedMode() {
		VNAScanModeParameter req = (VNAScanModeParameter) getModel().getSelectedItem();
		return req.getMode();
	}

	public void setSelectedMode(VNAScanMode scanMode) {
		VNAScanModeComboBoxModel mod = (VNAScanModeComboBoxModel) getModel();
		List<VNAScanModeParameter> modes = mod.getModes();
		for (VNAScanModeParameter mode : modes) {
			if (mode.getMode().equals(scanMode)) {
				setSelectedItem(mode);
				break;
			}
		}
	}

	public void setModes(Map<VNAScanMode, VNAScanModeParameter> scanModeParameters) {
		removeAllItems();
		for (VNAScanMode key : scanModeParameters.keySet()) {
			VNAScanModeParameter ent = scanModeParameters.get(key);
			addItem(ent);
		}
	}
}

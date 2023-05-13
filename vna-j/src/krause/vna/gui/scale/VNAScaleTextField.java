package krause.vna.gui.scale;

import javax.swing.JTextField;

public class VNAScaleTextField extends JTextField {
	private transient 	VNAGenericScale scale = null;
	private VNAScaleTextField minField = null;
	private VNAScaleTextField maxField = null;

	public VNAGenericScale getScale() {
		return scale;
	}

	public void setScale(VNAGenericScale scale) {
		this.scale = scale;
	}

	public VNAScaleTextField(String format, VNAGenericScale aScale) {
		super(format);
		setScale(aScale);
	}

	public VNAScaleTextField getMinField() {
		return minField;
	}

	public void setMinField(VNAScaleTextField minField) {
		this.minField = minField;
	}

	public VNAScaleTextField getMaxField() {
		return maxField;
	}

	public void setMaxField(VNAScaleTextField maxField) {
		this.maxField = maxField;
	}

}

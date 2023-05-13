package krause.vna.gui.panels;

import javax.swing.JCheckBox;

import krause.vna.gui.scale.VNAGenericScale;

public class VNAScaleCheckBox extends JCheckBox {
	private transient VNAGenericScale scale = null;
	
	public VNAScaleCheckBox(String name,VNAGenericScale pScale){
		super(name);
		setScale(pScale);
	}

	public VNAGenericScale getScale() {
		return scale;
	}

	public void setScale(VNAGenericScale scale) {
		this.scale = scale;
	}

}

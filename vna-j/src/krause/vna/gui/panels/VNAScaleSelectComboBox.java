package krause.vna.gui.panels;

import javax.swing.JComboBox;

import krause.vna.gui.scale.VNAScaleSymbols;
import krause.vna.gui.scale.VNAScaleSymbols.SCALE_TYPE;

public class VNAScaleSelectComboBox extends JComboBox {

	@SuppressWarnings("unchecked")
	public VNAScaleSelectComboBox() {
		setEditable(false);
		setMaximumRowCount(15);

		addItem(VNAScaleSymbols.MAP_SCALE_TYPES.get(SCALE_TYPE.SCALE_NONE));
		addItem(VNAScaleSymbols.MAP_SCALE_TYPES.get(SCALE_TYPE.SCALE_RETURNLOSS));
		addItem(VNAScaleSymbols.MAP_SCALE_TYPES.get(SCALE_TYPE.SCALE_RETURNPHASE));
		addItem(VNAScaleSymbols.MAP_SCALE_TYPES.get(SCALE_TYPE.SCALE_TRANSMISSIONLOSS));
		addItem(VNAScaleSymbols.MAP_SCALE_TYPES.get(SCALE_TYPE.SCALE_TRANSMISSIONPHASE));
		addItem(VNAScaleSymbols.MAP_SCALE_TYPES.get(SCALE_TYPE.SCALE_SWR));
		addItem(VNAScaleSymbols.MAP_SCALE_TYPES.get(SCALE_TYPE.SCALE_RSS));
		addItem(VNAScaleSymbols.MAP_SCALE_TYPES.get(SCALE_TYPE.SCALE_RS));
		addItem(VNAScaleSymbols.MAP_SCALE_TYPES.get(SCALE_TYPE.SCALE_THETA));
		addItem(VNAScaleSymbols.MAP_SCALE_TYPES.get(SCALE_TYPE.SCALE_GRPDLY));
		addItem(VNAScaleSymbols.MAP_SCALE_TYPES.get(SCALE_TYPE.SCALE_XS));
		addItem(VNAScaleSymbols.MAP_SCALE_TYPES.get(SCALE_TYPE.SCALE_Z_ABS));
	}
}

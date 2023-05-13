package krause.vna.gui.reference;

import krause.vna.data.reference.VNAReferenceDataBlock;

public interface IVNAReferenceDataSelectionListener {
	/**
	 * 
	 * @param blk
	 * @param doubleClick
	 */
	public void valueChanged(VNAReferenceDataBlock blk, boolean doubleClick);
}

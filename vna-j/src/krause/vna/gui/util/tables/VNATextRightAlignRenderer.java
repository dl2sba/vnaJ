package krause.vna.gui.util.tables;

import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * @author Dietmar Krause
 * 
 */
public class VNATextRightAlignRenderer extends DefaultTableCellRenderer {
	public VNATextRightAlignRenderer() {
		setHorizontalAlignment(JLabel.RIGHT);
	}
}
